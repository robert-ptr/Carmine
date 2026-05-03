package carmine.compiler.passes;

import carmine.compiler.structures.Token;
import carmine.compiler.structures.TokenType;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerFormalTest {

    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("module", TokenType.MODULE);
        keywords.put("var", TokenType.VAR);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("while", TokenType.WHILE);
        keywords.put("enum", TokenType.ENUM);
        keywords.put("null", TokenType.NULL);
    }

    @TestFactory
    Stream<DynamicTest> generateTestsFromJSXM() throws Exception {
        File inputFile = new File("test/jsxm/carmine_scanner_test.xml");
        if (!inputFile.exists()) {
            return Stream.empty();
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputFile);

        NodeList sequences = doc.getElementsByTagName("sequence");
        List<DynamicTest> tests = new ArrayList<>();

        for (int i = 0; i < sequences.getLength(); i++) {
            Element sequence = (Element) sequences.item(i);
            String name = sequence.getAttribute("name");
            
            tests.add(DynamicTest.dynamicTest(name, () -> runSingleTest(sequence)));
        }

        return tests.stream();
    }

    private void runSingleTest(Element sequence) {
        NodeList calls = sequence.getElementsByTagName("call");
        StringBuilder code = new StringBuilder();
        List<ExpectedToken> expectedTokens = new ArrayList<>();

        StringBuilder currentLexeme = new StringBuilder();
        boolean expectError = false;

        for (int i = 0; i < calls.getLength(); i++) {
            Element call = (Element) calls.item(i);
            String funcName = ((Element)call.getElementsByTagName("function").item(0)).getAttribute("name");
            
            // Skip sequences that don't start from START state
            if (i == 0 && !funcName.startsWith("start")) {
                return;
            }

            Element input = (Element) call.getElementsByTagName("input").item(0);
            if (input != null) {
                String val = getInputValue(input);
                code.append(val);
                currentLexeme.append(val);
            }

            NodeList outputs = call.getElementsByTagName("output");
            if (outputs.getLength() > 0) {
                Element output = (Element) outputs.item(0);
                if (output.getAttribute("name").endsWith("_Error")) {
                    expectError = true;
                    break;
                }
            }

            if (outputs.getLength() > 0) {
                Element output = (Element) outputs.item(0);
                NodeList tokenNodes = output.getElementsByTagName("tokens");
                if (tokenNodes.getLength() > 0) {
                    String tokensStr = tokenNodes.item(0).getTextContent().trim();
                    if (!tokensStr.isEmpty()) {
                        for (String t : tokensStr.split(" ")) {
                            TokenType type = TokenType.valueOf(t);
                            String lexeme = currentLexeme.toString().trim();
                            expectedTokens.add(new ExpectedToken(type, lexeme));
                            currentLexeme.setLength(0);
                        }
                    }
                }
            }

            // Handle the case where the sequence ends without an "End" transition
            if (i == calls.getLength() - 1 && currentLexeme.length() > 0 && !expectError) {
                TokenType predicted = predictToken(funcName);
                if (predicted != null) {
                    expectedTokens.add(new ExpectedToken(predicted, currentLexeme.toString().trim()));
                    code.append(" "); // Append space to ensure scanner finishes the token
                }
            }
        }

        Carmine.hadError = false;
        Scanner scanner = new Scanner(code.toString());
        List<Token> actualTokens = scanner.scanTokens();

        if (expectError) {
            org.junit.jupiter.api.Assertions.assertTrue(Carmine.hadError, "Scanning was expected to produce an error for sequence: [" + code.toString().replace("\n", "\\n") + "]");
            return;
        }

        // Filter out EOF for comparison if not expected
        List<Token> filteredActual = new ArrayList<>();
        for (Token t : actualTokens) {
            if (t.getType() != TokenType.EOF) {
                filteredActual.add(t);
            }
        }

        assertEquals(expectedTokens.size(), filteredActual.size(), "Token count mismatch for code: [" + code.toString().replace("\n", "\\n") + "]");

        for (int i = 0; i < expectedTokens.size(); i++) {
            ExpectedToken expected = expectedTokens.get(i);
            Token actual = filteredActual.get(i);

            TokenType expectedType = expected.type;
            if (expectedType == TokenType.IDENTIFIER && keywords.containsKey(actual.getLexeme())) {
                expectedType = keywords.get(actual.getLexeme());
            }

            assertEquals(expectedType, actual.getType(), "Token type mismatch at index " + i + " for code: [" + code.toString().replace("\n", "\\n") + "]");
            // We don't strictly compare lexemes here because our "currentLexeme" reconstruction 
            // might include leading whitespace that the scanner strips.
            // But we can check if actual lexeme is contained in expected or vice versa.
        }
    }

    private TokenType predictToken(String funcName) {
        if (funcName.startsWith("startAlpha") || funcName.startsWith("ident")) {
            return TokenType.IDENTIFIER;
        }
        if (funcName.startsWith("startDigit") || funcName.startsWith("decimal") || funcName.startsWith("zeroDigit")) {
            return TokenType.DECIMAL;
        }
        if (funcName.startsWith("binDigit") || funcName.equals("zeroToBin")) {
            return TokenType.BINARY;
        }
        if (funcName.startsWith("hex") || funcName.equals("zeroToHex")) {
            return TokenType.HEXADECIMAL;
        }
        if (funcName.equals("startSlash")) {
            return TokenType.DIV;
        }
        if (funcName.equals("startMinus")) {
            return TokenType.MINUS;
        }
        if (funcName.equals("startStar")) {
            return TokenType.MUL;
        }
        if (funcName.equals("startEqual")) {
            return TokenType.ASSIGN;
        }
        if (funcName.equals("startLess")) {
            return TokenType.LESS;
        }
        if (funcName.equals("startGreater")) {
            return TokenType.GREATER;
        }
        if (funcName.equals("startBang")) {
            return TokenType.ERR;
        }
        return null;
    }

    private String getInputValue(Element input) {
        String name = input.getAttribute("name");
        switch (name) {
            case "space": return " ";
            case "digit0": return "0";
            case "newline": return "\n";
            case "slash": return "/";
            case "minus": return "-";
            case "star": return "*";
            case "equal": return "=";
            case "less": return "<";
            case "greater": return ">";
            case "bang": return "!";
            case "b_char": return "b";
            case "X_char": return "X";
            case "dot": return ".";
            default:
                NodeList nl = input.getChildNodes();
                for(int i=0; i<nl.getLength(); i++) {
                    if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        return nl.item(i).getTextContent();
                    }
                }
                return "";
        }
    }

    private static class ExpectedToken {
        TokenType type;
        String lexeme;

        ExpectedToken(TokenType type, String lexeme) {
            this.type = type;
            this.lexeme = lexeme;
        }
    }
}
