#!/bin/bash
export JSXM_PATH=tools/jsxm/JSXM
export CLASSPATH=$JSXM_PATH/lib/JSXM.jar:$JSXM_PATH/lib/jdom.jar:$JSXM_PATH/lib/jaxb-impl.jar

mkdir -p test/jsxm/temp_parser/types

echo "Generating SXM Java specification for Parser..."
java -cp $CLASSPATH ui.SXMBaseGenerator -path test/jsxm/temp_parser -jarpath $JSXM_PATH/lib/JSXM.jar -overwrite test/jsxm/carmine_parser_expr.xml

echo "Moving generated Java to types/ directory..."
mv test/jsxm/temp_parser/*.java test/jsxm/temp_parser/types/ 2>/dev/null

echo "Compiling generated Java..."
javac -cp $CLASSPATH:test/jsxm/temp_parser test/jsxm/temp_parser/types/*.java -d test/jsxm/temp_parser

echo "Generating test cases..."
java -cp $CLASSPATH:test/jsxm/temp_parser ui.SXMTestGen -k 1 -sets test/jsxm/carmine_parser_expr_sets.xml -o test/jsxm/carmine_parser_expr_test.xml types.CarmineParserExprSXM

echo "Cleaning up temp files..."
rm -rf test/jsxm/temp_parser
