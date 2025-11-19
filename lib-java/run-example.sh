#!/bin/bash

# Script to run Example.java
# This script should be run from the lib-java directory
# Note: Example classes are in src/examples/ and are not included in the library JAR

echo "Configurando JAVA_HOME para JDK 24..."
export JAVA_HOME=/c/Users/Arabe/jdk-24
export PATH=$JAVA_HOME/bin:$PATH

echo "Java version:"
java -version
echo ""

echo "Compilando o projeto (incluindo exemplos)..."
mvn clean compile

# Compile examples separately
echo "Compilando classes de exemplo..."
javac -cp "target/classes:$(mvn dependency:build-classpath -q -DincludeScope=compile | tail -1)" \
    -d target/classes \
    src/examples/java/dev/sassine/tokenoptimizer/*.java

if [ $? -ne 0 ]; then
    echo "Erro na compilação!"
    exit 1
fi

echo ""
echo "Executando Example.java..."
echo ""
java -cp "target/classes:$(mvn dependency:build-classpath -q -DincludeScope=compile | tail -1)" \
    dev.sassine.tokenoptimizer.Example

