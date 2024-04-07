package org.ioopm.calculator;

import org.ioopm.calculator.ast.*;
import org.ioopm.calculator.parser.*;

import java.util.Scanner;

public class Calculator {

    final static CalculatorParser pars = new CalculatorParser();
    final static StackEnvironment stackEnv = new StackEnvironment();

    static Scanner sc = new Scanner(System.in);
    static int SuccEvalExpr = 0;
    static int EnteredExpr = 0;
    static int FullyEvalExpr = 0;
    static boolean quit = false;

    public static void command(Command expr) {
        if (expr instanceof Command.Clear) {
            System.out.println("\nClearing variables...");
            stackEnv.clear();
        } else if (expr instanceof Command.Vars) {
            if (stackEnv.isEmpty()) {
                System.out.println("\nNo variables to print...\n");
            } else {
                System.out.println("\nPrinting Variables...");
                System.out.println(stackEnv);
            }
        } else if (expr instanceof Command.Quit) {
            System.out.println("\nQuitting...");
            System.out.println("Entered Expressions: " + EnteredExpr);
            System.out.println("Succesfully Evaluated Expressions: " + SuccEvalExpr);
            System.out.println("Fully Evaluated Expression " + FullyEvalExpr);
            sc.close();
            quit = true;
        } else if (expr instanceof Command.End) {
            System.out.println("Cannot use command End outside of function declaration mode");
        }
    }
    
    public static void main(String[] args) {

        while(true) {
            System.out.print("Enter an Expression: ");
            SymbolicExpression expr;
            NamedConstantChecker namedConstantChecker = new NamedConstantChecker();
            ReassignmentChecker reassignmentChecker = new ReassignmentChecker();
            ArgumentChecker argumentChecker = new ArgumentChecker();

            while(true) {
                try {
                    String input = sc.nextLine();
                    expr = pars.parse(input);
                    EnteredExpr++;
                    break;
                }
                catch (Exception InvalidInput) {
                    System.out.println(InvalidInput.getMessage());
                    System.out.print("Try again: ");
                }
            }
            if (expr.isCommand()) {
                command((Command) expr);
                if (quit) {
                    return;
                }
            } else {
                if (expr.isFunctionDeclaration()) {
                    FunctionDeclaration function = (FunctionDeclaration) expr;
                    SymbolicExpression parsedLine;
                    String input;
                    while (true) {
                        while (true) {
                            try {
                                input = sc.nextLine();
                                parsedLine = pars.parse(input);
                                break;
                            } catch (Exception InvalidInput) {
                                System.out.println(InvalidInput.getMessage());
                                System.out.print("Try again: ");
                            }
                        }
                        if (parsedLine.isCommand()) {
                            Command command = (Command) parsedLine;
                            if (command instanceof Command.End) {
                                break;
                            }
                        }
                        function.getSequence().getBody().add(parsedLine);
                    }
                }
                SymbolicExpression result;
                if (namedConstantChecker.check(expr, stackEnv) && reassignmentChecker.check(expr, stackEnv) 
                    && argumentChecker.check(expr, stackEnv)) {
                    try {
                        EvaluationVisitor evaluator = new EvaluationVisitor();
                        result = evaluator.evaluate(expr, stackEnv);
                        SuccEvalExpr++;
                        if (result.isConstant()) {
                            FullyEvalExpr++;
                        }
                        System.out.println(result);
                    }
                    catch(RuntimeException unassignedVariable) {
                        System.out.println(unassignedVariable.getMessage());
                    }
                }
            }
        }
    }
}