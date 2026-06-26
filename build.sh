#!/bin/bash
mkdir -p bin
find src -name "*.java" > sources.txt
javac -d bin -cp "lib/*" @sources.txt
rm sources.txt
echo "Build complete!"
