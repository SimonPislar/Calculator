package org.ioopm.calculator.parser;

import org.ioopm.calculator.ast.*;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.IOException;

import java.util.*;

import static java.lang.String.valueOf;

/**
 * Represents the parsing of strings into valid expressions defined in the AST.
 */
public class CalculatorParser {
    private StreamTokenizer st;
    public ArrayList<FunctionCall> functions = new ArrayList<>();

    private static final char MULTIPLY = '*';
    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char DIVISION = '/';
    private static final char LESS_THAN = '<';
    private static final char GREATER_THAN = '>';
    private static final char EQUALS = '=';
    private static final String NEG = "Neg";
    private static final char NEGATION = '-';
    private static final String SIN = "Sin";
    private static final String COS = "Cos";
    private static final String LOG = "Log";
    private static final String EXP = "Exp";
    private static final char ASSIGNMENT = '=';
    private static final char SCOPE = '{';
    private static final char OPEN_PARENTHESES = '(';
    private static final char CLOSING_PARENTHESES = ')';
    private static final String IF = "if";
    private static final String LESS_THAN_OR_EQUALS = "<=";
    private static final String GREATER_THAN_OR_EQUALS = ">=";
    private static final String COMPARISON = "==";
    private static final String FUNCTION = "function";
    private static final char COMMA = ',';

    private static class FunctionSignature {
        public String functionName;
        public ArrayList<Atom> arguments;
    }

    // unallowerdVars is used to check if variabel name that we
    // want to assign new meaning to is a valid name eg 3 = Quit
    // or 10 + x = L is not allowed
    private final ArrayList <String> unallowedVars = new ArrayList<>(Arrays.asList("Quit",
            "Vars", "Clear", "quit", "vars", "clear", "if", "If", "End", "end"));

    /**
     * Used to parse the inputted string by the Calculator program
     * @param inputString the string used to parse
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     */
    public SymbolicExpression parse(String inputString) throws IOException {
        this.st = new StreamTokenizer(new StringReader(inputString)); // reads from inputString via stringreader.
        this.st.ordinaryChar('-');
        this.st.ordinaryChar('/');
        this.st.eolIsSignificant(true);
        return statement(); // the final result
    }

    /**
     * Checks wether the token read is a command or an assignment
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     * @throws SyntaxErrorException if the token parsed cannot be turned into a valid expression
     */
    private SymbolicExpression statement() throws IOException {
        SymbolicExpression result;
        this.st.nextToken(); //kollar pÃ¥ nÃ¤sta token som ligger pÃ¥ strÃ¶mmen
        if (this.st.ttype == StreamTokenizer.TT_EOF) {
            throw new SyntaxErrorException("Error: Expected an expression");
        }

        if (this.st.ttype == StreamTokenizer.TT_WORD) { // vilken typ det senaste tecken vi lÃ¤ste in hade.
            if (this.st.sval.equals("Quit") || this.st.sval.equals("Vars") || this.st.sval.equals("Clear") || this.st.sval.equals("End")) { // sval = string Variable
                result = command();
            } else {
                result = assignment(); // gÃ¥r vidare med uttrycket.
            }
        } else {
            result = assignment(); // om inte == word, gÃ¥ till assignment Ã¤ndÃ¥ (kan vara tt_number)
        }

        if (this.st.nextToken() != StreamTokenizer.TT_EOF) { // token should be an end of stream token if we are done
            if (this.st.ttype == StreamTokenizer.TT_WORD) {
                throw new SyntaxErrorException("Error: Unexpected '" + this.st.sval + "'");
            } else {
                throw new SyntaxErrorException("Error: Unexpected '" + (char) this.st.ttype + "'");
            }
        }
        return result;
    }

    /**
     * Checks what kind of command that should be returned
     * @return an instance of Quit, Clear or Vars depending on the token parsed
     */
    private SymbolicExpression command() {
        return switch (this.st.sval) {
            case "Quit" -> Command.Quit.instance();
            case "Clear" -> Command.Clear.instance();
            case "End" -> Command.End.instance();
            default -> Command.Vars.instance();
        };
    }


    /**
     * Checks wether the token read is an assignment between 2 expression and 
     * descend into the right hand side of '='
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     * @throws SyntaxErrorException if the token parsed cannot be turned into a valid expression,
     *         the variable on rhs of '=' is a number or invalid variable
     */
    private SymbolicExpression assignment() throws IOException {
        SymbolicExpression result = expression();
        this.st.nextToken();
        while (this.st.ttype == ASSIGNMENT) {
            this.st.nextToken();
            if (this.st.ttype == StreamTokenizer.TT_NUMBER) {
                throw new SyntaxErrorException("Error: Numbers cannot be used as a variable name");
            } else if (this.st.ttype != StreamTokenizer.TT_WORD) {
                throw new SyntaxErrorException("Error: Not a valid assignment of a variable"); //this handles faulty inputs after the equal sign eg. 1 = (x etc
            } else {
                if (this.st.sval.equals("ans")) {
                    throw new SyntaxErrorException("Error: ans cannot be redefined");
                }
                SymbolicExpression key = identifier();
                result = new Assignment(result, key);
            }
            this.st.nextToken();
        }
        this.st.pushBack();
        return result;
    }

    /**
     * Check if valid identifier for variable and return that if so
     * @return a SymbolicExpression that is either a named constant or a new variable
     * @throws IOException by nextToken() if it reads invalid input
     * @throws IllegalExpressionException if you try to redefine a string that isn't allowed
     */

    private SymbolicExpression identifier() throws IOException {
        SymbolicExpression result;
        if (Constants.namedConstants.containsKey(this.st.sval)) {
            for (FunctionCall function : this.functions) {
                if (function.toString().equals(this.st.sval)) {
                    throw new SyntaxErrorException(this.st.sval + " is already defined as a function");
                }
            }
            if (this.unallowedVars.contains(this.st.sval)) {
                throw new IllegalExpressionException("Error: cannot redefine " + this.st.sval);
            } else {
                result = new NamedConstant(st.sval, Constants.namedConstants.get(st.sval));
            }
        } else {
            result = regularIdentifier();
        }
        return result;
    }

    private SymbolicExpression regularIdentifier() throws IOException {
        Variable result;
        for (FunctionCall function : this.functions) {
            if (function.getName().equals(this.st.sval)) {
                ArrayList<Atom> arguments = parseFunctionArguments();
                return new FunctionCall(new Variable(function.getName()), arguments);
            }
        }
        if (this.unallowedVars.contains(this.st.sval)) {
            throw new IllegalExpressionException("Error: cannot redefine " + this.st.sval);
        } else {
            result = new Variable(this.st.sval);
        }
        return result;
    }


    /**
     * Checks wether the token read is an addition or subtraction
     * and then continue on with the right hand side of operator
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     */
    private SymbolicExpression expression() throws IOException {
        SymbolicExpression result = term();
        this.st.nextToken();
        while (this.st.ttype == ADDITION || this.st.ttype == SUBTRACTION) {
            int operation = st.ttype;
            this.st.nextToken();
            if (operation == ADDITION) {
                result = new Addition(result, term());
            } else {
                result = new Subtraction(result, term());
            }
            this.st.nextToken();
        }
        this.st.pushBack();
        return result;
    }

    /**
     * Checks wether the token read is an Multiplication or
     * Division and then continue on with the right hand side of
     * operator
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     */
    private SymbolicExpression term() throws IOException {
        SymbolicExpression result = primary();
        this.st.nextToken();
        while (this.st.ttype == MULTIPLY || this.st.ttype == DIVISION) {
            int operation = st.ttype;
            this.st.nextToken();

            if (operation == MULTIPLY) {
                result = new Multiplication(result, primary());
            } else {
                result = new Division(result, primary());
            }
            this.st.nextToken();
        }
        this.st.pushBack();
        return result;
    }

    /**
     * Checks wether the token read is a parantheses and then
     * continue on with the expression inside of it or if the
     * operation is an unary operation and then continue on with
     * the right hand side of that operator else if it's a
     * number/identifier
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     * @throws SyntaxErrorException if the token parsed cannot be turned into a valid expression,
     *         missing right parantheses
     */
    private SymbolicExpression primary() throws IOException {
        SymbolicExpression result;
        if (this.st.ttype == '(') {
            this.st.nextToken();
            result = assignment();
            /// This captures unbalanced parentheses!
            if (this.st.nextToken() != ')') {
                throw new SyntaxErrorException("expected ')'");
            }
        } else if (this.st.ttype == SCOPE) {
            this.st.nextToken();
            result = new Scope(assignment());
            if (this.st.nextToken() != '}') {
                throw new SyntaxErrorException("expected '}");
            }
        } else if (this.st.ttype == NEGATION) {
            result = unary();
        } else if (this.st.ttype == StreamTokenizer.TT_WORD) {
            result = switch (st.sval) {
                case SIN, COS, EXP, NEG, LOG -> unary();
                case IF -> conditionals();
                case FUNCTION -> function();
                default -> identifier();
            };
        } else {
            this.st.pushBack();
            result = number();
        }
        return result;
    }

    private ArrayList<Atom> parseFunctionArguments() throws IOException {
        ArrayList<Atom> arguments = new ArrayList<>();

        this.st.nextToken();

        if (this.st.ttype != OPEN_PARENTHESES) {
            throw new SyntaxErrorException("Expected '('");
        }

        this.st.nextToken();

        while (this.st.ttype != CLOSING_PARENTHESES) {

            if (this.st.ttype == StreamTokenizer.TT_WORD) {
                Atom argument = (Atom) identifier();
                arguments.add(argument);
            }
            else if (this.st.ttype == StreamTokenizer.TT_NUMBER) {
                arguments.add(new Constant(this.st.nval));
            }
            else {
                throw new SyntaxErrorException("Argument has to be an instance of Atom");
            }

            this.st.nextToken();

            if (this.st.ttype == COMMA) {
                this.st.nextToken();
            }
            else if (this.st.ttype == CLOSING_PARENTHESES) {
            }
            else {
                throw new SyntaxErrorException("Expected " + COMMA + " or " + CLOSING_PARENTHESES);
            }
        }
        return arguments;
    }

    private FunctionSignature parseFunctionSignature() throws IOException {
        FunctionSignature functionSignature = new FunctionSignature();
        this.st.nextToken();

        if (this.st.ttype != StreamTokenizer.TT_WORD) {
            throw new SyntaxErrorException("Function name must be a word");
        } else {
            if (this.unallowedVars.contains(this.st.sval)) {
                throw new IllegalExpressionException("Error: cannot redefine " + this.st.sval);
            }
            functionSignature.functionName = this.st.sval;
        }
        functionSignature.arguments = parseFunctionArguments();

        return functionSignature;
    }

    private SymbolicExpression function() throws IOException {
        FunctionSignature functionSignature = parseFunctionSignature();

        Variable functionName = new Variable(functionSignature.functionName);
        ArrayList<SymbolicExpression> functionBody = new ArrayList<>();

        this.functions.add(new FunctionCall(functionName, functionSignature.arguments));

        return new FunctionDeclaration(new Sequence(functionSignature.arguments, functionName, functionBody));
    }

    private Atom parseAtom() throws IOException {
        this.st.nextToken();
        if (this.st.ttype == StreamTokenizer.TT_WORD) {
            return new Variable(this.st.sval);
        } else if (this.st.ttype == StreamTokenizer.TT_NUMBER) {
            return new Constant(this.st.nval);
        } else {
            throw new SyntaxErrorException("If-statement parameters has to be instance of Atom");
        }
    }

    private SymbolicExpression conditionals() throws IOException {
        Atom leftAtom = parseAtom();
        String operator = operation(); //Operation like '<', '>' and similar
        Atom rightAtom = parseAtom();
        this.st.nextToken();
        SymbolicExpression leftExpression = primary();
        if (!(leftExpression instanceof Scope)) {
            throw new SyntaxErrorException("Expected scope");
        }
        this.st.nextToken();
        if (!(this.st.ttype == StreamTokenizer.TT_WORD && this.st.sval.equals("else"))) {
            throw new SyntaxErrorException("Expected else");
        }
        this.st.nextToken();
        SymbolicExpression rightExpression = primary();
        if (!(rightExpression instanceof Scope)) {
            throw new SyntaxErrorException("Expected scope");
        }
        return new Conditionals(leftAtom, operator, rightAtom, (Scope) leftExpression, (Scope) rightExpression);
    }

    private Boolean foundEqual() throws IOException{
        if (this.st.ttype == EQUALS) { 
            this.st.nextToken();   
            if(this.st.ttype == StreamTokenizer.TT_WORD || this.st.ttype == StreamTokenizer.TT_NUMBER) {
                this.st.pushBack();
                return true;
            }
        }
        return false;
    }

    private String operation() throws IOException {
        int t = this.st.nextToken();
        switch (t) {
            case LESS_THAN -> {
                this.st.nextToken();
                if (foundEqual()) return LESS_THAN_OR_EQUALS;

                else if (this.st.ttype == StreamTokenizer.TT_WORD || this.st.ttype == StreamTokenizer.TT_NUMBER) {
                    this.st.pushBack();
                    return valueOf(LESS_THAN);
                }
            }
            case GREATER_THAN -> {
                this.st.nextToken();
                if (foundEqual()) return GREATER_THAN_OR_EQUALS;

                else if (this.st.ttype == StreamTokenizer.TT_WORD || this.st.ttype == StreamTokenizer.TT_NUMBER) {
                    this.st.pushBack();
                    return valueOf(GREATER_THAN);
                }
            }
            case EQUALS -> {
                this.st.nextToken();
                if (foundEqual()) return COMPARISON;

                else {
                    this.st.pushBack();
                }
            }
        }
        throw new SyntaxErrorException("Expected an operator but got '" + st.sval + "'");
    }

    /**
     * Checks what type of Unary operation the token read is and
     * then continues with the expression that the operator holds
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     */
    private SymbolicExpression unary() throws IOException {
        SymbolicExpression result;
        int operationNeg = st.ttype;
        String operation = st.sval;
        this.st.nextToken();
        if (operationNeg == NEGATION || operation.equals(NEG)) {
            result = new Negation(primary());
        } else if (operation.equals(SIN)) {
            result = new Sin(primary());
        } else if (operation.equals(COS)) {
            result = new Cos(primary());
        } else if (operation.equals(LOG)) {
            result = new Log(primary());
        } else {
            result = new Exp(primary());
        }
        return result;
    }

    /**
     * Checks if the token read is a number - should always be a number in this method
     * @return a SymbolicExpression to be evaluated
     * @throws IOException by nextToken() if it reads invalid input
     * @throws SyntaxErrorException if the token parsed cannot be turned into a valid expression,
     *         expected a number which is not present
     */
    private SymbolicExpression number() throws IOException {
        this.st.nextToken();
        if (this.st.ttype == StreamTokenizer.TT_NUMBER) {
            return new Constant(this.st.nval);
        } else {
            throw new SyntaxErrorException("Error: Expected number");
        }
    }
}