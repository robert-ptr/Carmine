#!/bin/bash
export JSXM_PATH=tools/jsxm/JSXM
export CLASSPATH=$JSXM_PATH/lib/JSXM.jar:$JSXM_PATH/lib/jdom.jar:$JSXM_PATH/lib/jaxb-impl.jar

mkdir -p test/jsxm/temp/types

echo "Generating SXM Java specification..."
java -cp $CLASSPATH ui.SXMBaseGenerator -path test/jsxm/temp -jarpath $JSXM_PATH/lib/JSXM.jar -overwrite test/jsxm/carmine_scanner.xml

echo "Moving generated Java to types/ directory..."
mv test/jsxm/temp/*.java test/jsxm/temp/types/ 2>/dev/null

echo "Compiling generated Java..."
javac -cp $CLASSPATH:test/jsxm/temp test/jsxm/temp/types/*.java -d test/jsxm/temp

echo "Generating test cases..."
java -cp $CLASSPATH:test/jsxm/temp ui.SXMTestGen -k 1 -sets test/jsxm/carmine_scanner_sets.xml -o test/jsxm/carmine_scanner_test.xml types.CarmineScannerSXM

echo "Cleaning up temp files..."
rm -rf test/jsxm/temp
