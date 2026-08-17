package carmine.compiler.passes;

import carmine.compiler.passes.Carmine;
import carmine.compiler.structures.Token;
import carmine.compiler.structures.TokenType;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class ParserExpressionFormalTest {

    @TestFactory
    Stream<DynamicTest> generateTestsFromJSXM() throws Exception {
        File inputFile = new File("test/jsxm/carmine_parser_expr_test.xml");
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
        ArrayList<Token> tokens = new ArrayList<>();
        boolean expectIncomplete = true;
        boolean expectError = false;

        for (int i = 0; i < calls.getLength(); i++) {
            Element call = (Element) calls.item(i);
            String funcName = ((Element)call.getElementsByTagName("function").item(0)).getAttribute("name");
            
            Element input = (Element) call.getElementsByTagName("input").item(0);
            if (input != null) {
                Token t = getInputValue(input);
                if (t != null) {
                    tokens.add(t);
                }
            }

            NodeList outputs = call.getElementsByTagName("output");
            if (outputs.getLength() > 0) {
                Element output = (Element) outputs.item(0);
                if (output.getAttribute("name").endsWith("_Error")) {
                    expectError = true;
                    break;
                }
            }
            
            if (i == calls.getLength() - 1) {
                if (funcName.equals("emitStatement") || funcName.equals("emitStatementU")) {
                    expectIncomplete = false;
                }
            }
        }

        tokens.add(new Token(TokenType.EOF, "", null, 1));

        Carmine.hadError = false; // Reset global error state
        Parser parser = new Parser(tokens);
        parser.parse();

        if (expectError) {
            org.junit.jupiter.api.Assertions.assertTrue(Carmine.hadError, "Parsing was expected to fail for invalid syntactic sequence.");
        } else if (expectIncomplete) {
            org.junit.jupiter.api.Assertions.assertTrue(Carmine.hadError, "Parsing was expected to fail for incomplete token sequences.");
        } else {
            org.junit.jupiter.api.Assertions.assertFalse(Carmine.hadError, "Parsing produced an unexpected error for valid token sequence.");
        }
    }

    private Token getInputValue(Element input) {
        String name = input.getAttribute("name");
        switch (name) {
            case "ident": return new Token(TokenType.IDENTIFIER, "a", null, 1);
            case "lit": return new Token(TokenType.DECIMAL, "1", null, 1);
            case "unary_op": return new Token(TokenType.MINUS, "-", null, 1);
            case "bin_op": return new Token(TokenType.PLUS, "+", null, 1);
            case "assign": return new Token(TokenType.ASSIGN, "=", null, 1);
            case "semicolon": return new Token(TokenType.SEMICOLON, ";", null, 1);
            default: return null;
        }
    }
}
