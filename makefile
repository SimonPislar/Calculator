all:
	mkdir -p classes
	javac -d classes -sourcepath . org/ioopm/calculator/Calculator.java

test: systest
	javac -cp junit-platform-console-standalone-1.8.1.jar:. CalculatorTests.java
	java -jar junit-platform-console-standalone-1.8.1.jar -cp . -c CalculatorTests

systest: all 
	java -cp classes org.ioopm.calculator.Calculator < input.txt > output.txt
	diff output.txt expected_output.txt
	java -cp classes org.ioopm.calculator.Calculator < inputCommands.txt > outputCommands.txt
	diff outputCommands.txt expectedCommands.txt
	java -cp classes org.ioopm.calculator.Calculator < inputScopes.txt > outputScopes.txt
	diff outputScopes.txt expectedScopes.txt
	java -cp classes org.ioopm.calculator.Calculator < inputFunctions.txt > outputFunctions.txt
	diff outputFunctions.txt expectedFunctions.txt
	rm -rf classes

run:
	java -cp classes org.ioopm.calculator.Calculator

clean:
	rm -rf classes
	rm -f *.class