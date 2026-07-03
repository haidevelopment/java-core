@echo off
chcp 65001 > nul
if not exist bin mkdir bin
dir /s /B src\*.java > sources.txt
javac -encoding UTF-8 -d bin -cp "lib\*" @sources.txt
del sources.txt
echo Build complete!
