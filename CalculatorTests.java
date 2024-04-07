import org.ioopm.calculator.ast.*;
import org.ioopm.calculator.parser.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

public class CalculatorTests {

    //AST Unit Tests

    @Test
    public void getValueTest() {
        Addition a1 = new Addition(new Constant(1.0), new Constant(2.0)); 
        Exception a1Exception = assertThrows(RuntimeException.class, a1::getValue);
        assertEquals("getValue called on non constant expression", a1Exception.getMessage());

        Cos co1 = new Cos(new Constant(1.0)); 
        Exception co1Exception = assertThrows(RuntimeException.class, co1::getValue);
        assertEquals("getValue called on non constant expression", co1Exception.getMessage());

        Variable v1 = new Variable("x");
        Exception v1Exception = assertThrows(RuntimeException.class, v1::getValue);
        assertEquals("getValue called on non constant expression", v1Exception.getMessage());

        Command va1 = Command.Vars.instance();
        Exception va1Exception = assertThrows(RuntimeException.class, va1::getValue);
        assertEquals("getValue called on non constant expression", va1Exception.getMessage());

        Constant c1 = new Constant(1.0);
        assertEquals(c1.getValue(), 1.0);
    }

    @Test
    public void isConstantTest() {
        Subtraction s1 = new Subtraction(new Constant(1.0), new Constant(2.0)); 
        assertFalse(s1.isConstant());

        Sin si1 = new Sin(new Constant(1.0)); 
        assertFalse(si1.isConstant());

        Variable v1 = new Variable("x");
        assertFalse(v1.isConstant());

        Constant c1 = new Constant(1.0);
        assertTrue(c1.isConstant());
    }

    @Test
    public void getNameTest() {
        Multiplication m1 = new Multiplication(new Constant(1.0), new Constant(2.0)); 
        assertEquals(m1.getName(), "*");

        Exp exp1 = new Exp(new Constant(1.0)); 
        assertEquals(exp1.getName(), "Exp");

        Variable v1 = new Variable("x");
        Exception v1Exception = assertThrows(RuntimeException.class, v1::getName);
        assertEquals("getName() called on expression with no operator", v1Exception.getMessage());

        Constant c1 = new Constant(1.0);
        Exception c1Exception = assertThrows(RuntimeException.class, c1::getName);
        assertEquals("getName() called on expression with no operator", c1Exception.getMessage());

        Command q1 = Command.Quit.instance();
        Exception q1Exception = assertThrows(RuntimeException.class, q1::getName);
        assertEquals("getName() called on expression with no operator", q1Exception.getMessage());
    }

    @Test
    public void isCommandTest() {
        Subtraction s1 = new Subtraction(new Constant(1.0), new Constant(2.0)); 
        assertFalse(s1.isCommand());

        Sin si1 = new Sin(new Constant(1.0)); 
        assertFalse(si1.isCommand());

        Variable v1 = new Variable("x");
        assertFalse(v1.isCommand());

        Constant c1 = new Constant(1.0);
        assertFalse(c1.isCommand());

        Command q1 = Command.Quit.instance();
        assertTrue(q1.isCommand());

        Command va1 = Command.Vars.instance();
        assertTrue(va1.isCommand());

        Command cl1 = Command.Clear.instance();
        assertTrue(cl1.isCommand());
    }

    @Test
    public void getPriorityTest() {
        Addition a1 = new Addition(new Constant(1.0), new Constant(2.0)); 
        assertEquals(1, a1.getPriority());

        Multiplication m1 = new Multiplication(new Constant(1.0), new Constant(2.0)); 
        assertEquals(2, m1.getPriority());

        Cos co1 = new Cos(new Constant(1.0)); 
        assertEquals(3, co1.getPriority());

        Constant c1 = new Constant(1.0);
        assertEquals(3, c1.getPriority());

        Command cl1 = Command.Clear.instance();
        assertEquals(0, cl1.getPriority());
    }

    @Test
    public void NamedConstantCheckerTest() {
        StackEnvironment stackEnv = new StackEnvironment();
        NamedConstant pi = new NamedConstant("pi", 3.14);
        Assignment piReassign = new Assignment(new Constant(1.0), pi);

        NamedConstantChecker piChecker = new NamedConstantChecker();

        assertFalse(piChecker.check(piReassign, stackEnv));
    }

    @Test
    public void ReassignmentCheckerTest() {
        StackEnvironment StackEnv = new StackEnvironment();
        Assignment x1 = new Assignment(new Constant(1.0), new Variable("x"));
        Assignment x2 = new Assignment(new Constant(2.0), new Variable("x"));
        Multiplication reassignment = new Multiplication(x1,x2);

        ReassignmentChecker reChecker = new ReassignmentChecker();

        assertFalse(reChecker.check(reassignment, StackEnv));
    }


    @Test
    public void ConditionalsEval() {

        StackEnvironment Env1 = new StackEnvironment();
        EvaluationVisitor evaluator1 = new EvaluationVisitor();
        Assignment x = new Assignment(new Constant(1.0), new Variable("x"));
        Assignment y = new Assignment(new Constant(2.0), new Variable("y"));

        evaluator1.evaluate(y, Env1);
        evaluator1.evaluate(x, Env1);

        Conditionals c = new Conditionals(new Variable("x"), "<", new Variable("y"), new Scope(new Constant(10.0)), new Scope(new Constant(0.0)));

        assertEquals(new Constant(10.0), evaluator1.evaluate(c, Env1));
        assertNotEquals(new Constant(0.0), evaluator1.evaluate(c, Env1));

        Conditionals invOp = new Conditionals(new Variable("x"), "pizza", new Variable("y"), new Scope(new Constant(10.0)), new Scope(new Constant(0.0)));

        Exception invalidOperator = assertThrows(RuntimeException.class, () -> evaluator1.evaluate(invOp, Env1));
        assertEquals("Invalid operator: ", invalidOperator.getMessage());

        StackEnvironment Env2 = new StackEnvironment();
        EvaluationVisitor evaluator2 = new EvaluationVisitor();

        Exception unassignedVar = assertThrows(RuntimeException.class, () -> evaluator2.evaluate(c, Env2));
        assertEquals("Variables must be assigned: ", unassignedVar.getMessage());

    }

    @Test
    public void ConditionalsEquals() {

        Conditionals c = new Conditionals(new Variable("x"), "<", new Variable("y"), new Scope(new Constant(10.0)), new Scope(new Constant(0.0)));

        assertEquals(c, new Conditionals(new Variable("x"), "<", new Variable("y"), new Scope(new Constant(10.0)), new Scope(new Constant(0.0))));

        assertNotEquals(c, new Conditionals(new Variable("z"), "<", new Variable("y"), new Scope(new Constant(10.0)), new Scope(new Constant(0.0))));
        assertNotEquals(c, new Conditionals(new Variable("x"), ">", new Variable("y"), new Scope(new Constant(10.0)), new Scope(new Constant(0.0))));
        assertNotEquals(c, new Conditionals(new Variable("x"), "<", new Variable("z"), new Scope(new Constant(10.0)), new Scope(new Constant(0.0))));
        assertNotEquals(c, new Conditionals(new Variable("x"), "<", new Variable("y"), new Scope(new Constant(15.0)), new Scope(new Constant(0.0))));
        assertNotEquals(c, new Conditionals(new Variable("x"), "<", new Variable("y"), new Scope(new Constant(10.0)), new Scope(new Constant(5.0))));
    }


    @Test
    public void functionEvalTest() {
        StackEnvironment env1 = new StackEnvironment();
        EvaluationVisitor evaluator = new EvaluationVisitor();

        ArrayList<Atom> params = new ArrayList<>();
        params.add(0,new Variable("x"));
        params.add(0,new Variable("y"));

        ArrayList<SymbolicExpression> body = new ArrayList<>();
        body.add(new Conditionals(params.get(0), "<", params.get(1), new Scope(params.get(1)), new Scope(params.get(0))));
        

        Sequence seq = new Sequence(params, new Variable("foo"), body);
        FunctionDeclaration foo = new FunctionDeclaration(seq);

        evaluator.evaluate(foo, env1);

        ArrayList<Atom> args = new ArrayList<>();
        args.add(0,new Constant(1.0));
        args.add(0,new Constant(0));

        FunctionCall fooCall = new FunctionCall(new Variable("foo"), args);

        assertEquals(new Constant(1.0), evaluator.evaluate(fooCall, env1));
        assertNotEquals(new Constant(0.0), evaluator.evaluate(fooCall, env1));

        FunctionCall barCall = new FunctionCall(new Variable("bar"), args);

        Exception nofuncexception = assertThrows(RuntimeException.class, () -> evaluator.evaluate(barCall, env1));
        assertEquals("No function with that name: ", nofuncexception.getMessage());
    }

    // Ast Integration Tests

    @Test
    public void equalsTest() {

        Constant c1 = new Constant(1.0);
        Constant c2 = new Constant(2.0);
        Constant c3 = new Constant(3.0);
        Constant c5 = new Constant(5.0);
        Constant c15 = new Constant(15.0);
        Multiplication m1 = new Multiplication(c1, c15); // 1 * 15
        Multiplication m2 = new Multiplication(c3, c5); // 3 * 5
        Exp e0 = new Exp(new Sin(new Log(new Division(new Constant(8.0), new Addition(c5,c3))))); // addition 5+3
        Variable v1 = new Variable("x");
        Assignment asv1 = new Assignment(new Constant(8.0), v1);
        NamedConstant nc1 = new NamedConstant("1", 1.0);
        NamedConstant nc1f = new NamedConstant("1", 0.0);
        NamedConstant nc1f2 = new NamedConstant("0", 1.0);

        Scope sc1 = new Scope(new Variable("x"));

        Scope sc2 = new Scope(new Assignment(new Scope(new Assignment(new Constant(1.0), new Variable("x"))), new Variable("x")));

        assertTrue(c1.equals(new Constant(1.0)));

        assertFalse(c1.equals(new Constant(0.0)));

        assertTrue(m1.equals(m1));

        assertTrue(m1.equals(new Multiplication(c1, c15)));

        assertFalse(m1.equals(new Multiplication(c3, c5)));

        assertFalse(m1.equals(m2));

        assertTrue(e0.equals(new Exp(new Sin(new Log(new Division(new Constant(8.0), new Addition(c5,c3)))))));

        // addition changed
        assertFalse(e0.equals(new Exp(new Sin(new Log(new Division(new Constant(8.0), new Addition(c2,c3))))))); 

        assertTrue(v1.equals(new Variable("x")));

        assertFalse(v1.equals(new Variable("y")));

        assertTrue(asv1.equals(new Assignment(new Constant(8.0), v1)));

        assertFalse(asv1.equals(new Assignment(new Constant(6.0), v1)));

        assertTrue(nc1.equals(new NamedConstant("1", 1.0)));

        assertFalse(nc1.equals(nc1f));

        assertFalse(nc1.equals(nc1f2));

        assertTrue(sc1.equals(new Scope(new Variable("x"))));

        assertTrue(sc2.equals(new Scope(new Assignment(new Scope(new Assignment(new Constant(1.0), new Variable("x"))), new Variable("x")))));

        assertFalse(sc2.equals(new Scope(new Assignment(new Scope(new Assignment(new Constant(1.0), new Variable("x"))), new Variable("y")))));
    }

    @Test
    public void evalTest() {

        Constant c0 = new Constant(0.0);
        Constant c1 = new Constant(1.0);
        Constant c2 = new Constant(2.0);
        Constant c3 = new Constant(3.0);
        Constant c5 = new Constant(5.0);
        Constant c15 = new Constant(15.0);

        Subtraction s2 = new Subtraction(c5, c3);

        Multiplication m1 = new Multiplication(c1, c15); // 1 * 15
        Multiplication m2 = new Multiplication(c3, c5); // 3 * 5

        Negation n1 = new Negation(c1);
        Negation n2 = new Negation(n1);

        Cos co1 = new Cos(c0); // Cos(0)

        Exp e0 = new Exp(new Sin(new Log(new Division(new Constant(8.0), new Addition(c5,c3))))); // addition 5+3

        Variable v1 = new Variable("x");

        Assignment asc1 = new Assignment(new Constant(1.0), new Variable ("x"));
        Assignment ase0 = new Assignment(e0, new Variable("y"));
        Assignment asv1 = new Assignment(new Constant(8.0), v1);
        Assignment asv11 = new Assignment(new Constant(3.0), v1);

        Addition a1 = new Addition(c5, asv1);
        Multiplication m3 = new Multiplication(a1, c2);

        NamedConstant nc1 = new NamedConstant("1", 1.0);
    
        Addition a2 = new Addition(c1 , nc1);

        Variable v2 = new Variable("f");
        Addition a3 = new Addition(c5, v2);

        Sin s1 = new Sin(v1);

        StackEnvironment env1 = new StackEnvironment();
        EvaluationVisitor evaluator1 = new EvaluationVisitor();
        assertEquals(new Constant(1.0), evaluator1.evaluate(c1, env1));
        assertNotEquals(new Constant(-1.0), evaluator1.evaluate(c1, env1));

        StackEnvironment env2 = new StackEnvironment();
        EvaluationVisitor evaluator2 = new EvaluationVisitor();
        assertEquals(new Constant(15.0), evaluator2.evaluate(m1, env2));
        assertNotEquals(new Constant(1.0), evaluator2.evaluate(m1, env2));

        StackEnvironment env3 = new StackEnvironment();
        EvaluationVisitor evaluator3 = new EvaluationVisitor();
        assertEquals(new Constant(15.0), evaluator3.evaluate(m2, env3));

        StackEnvironment env4 = new StackEnvironment();
        EvaluationVisitor evaluator4 = new EvaluationVisitor();
        assertEquals(new Constant(-1.0), evaluator4.evaluate(n1, env4));
        assertNotEquals(new Constant(1.0), evaluator4.evaluate(n1, env4));

        StackEnvironment env5 = new StackEnvironment();
        EvaluationVisitor evaluator5 = new EvaluationVisitor();
        assertEquals(new Constant(1.0), evaluator5.evaluate(n2, env5));
        assertNotEquals(new Constant(-1.0), evaluator5.evaluate(n2, env5));

        StackEnvironment env6 = new StackEnvironment();
        EvaluationVisitor evaluator6 = new EvaluationVisitor();
        assertEquals(new Constant(2.0), evaluator6.evaluate(s2, env6));

        StackEnvironment env7 = new StackEnvironment();
        EvaluationVisitor evaluator7 = new EvaluationVisitor();
        assertEquals(new Constant(1.0), evaluator7.evaluate(co1, env7));

        StackEnvironment env9 = new StackEnvironment();
        EvaluationVisitor evaluator8 = new EvaluationVisitor();
        assertEquals(new Constant(1.0), evaluator8.evaluate(e0, env9));

        StackEnvironment env10 = new StackEnvironment();
        EvaluationVisitor evaluator9 = new EvaluationVisitor();
        assertEquals(new Constant(1.0), evaluator9.evaluate(asc1, env10));

        StackEnvironment env11 = new StackEnvironment();
        EvaluationVisitor evaluator10 = new EvaluationVisitor();
        assertEquals(new Constant(1.0), evaluator10.evaluate(ase0, env11));

        StackEnvironment env12 = new StackEnvironment();
        EvaluationVisitor evaluator12 = new EvaluationVisitor();
        assertEquals(new Constant(26.0), evaluator12.evaluate(m3, env12));

        StackEnvironment env13 = new StackEnvironment();
        EvaluationVisitor evaluator13 = new EvaluationVisitor();
        assertEquals(new Constant(2.0), evaluator13.evaluate(a2, env13));

        StackEnvironment env14 = new StackEnvironment();
        EvaluationVisitor evaluator14 = new EvaluationVisitor();
        assertEquals(new Variable("x"), evaluator14.evaluate(v1, env14));
        assertNotEquals(new Variable("y"), evaluator14.evaluate(v1, env14));

        StackEnvironment env15 = new StackEnvironment();
        EvaluationVisitor evaluator15 = new EvaluationVisitor();
        assertEquals(new Addition(new Constant(5.0), new Variable ("f")), evaluator15.evaluate(a3, env15));
        assertNotEquals(new Addition(new Constant(1.0), new Variable ("f")), evaluator15.evaluate(a3, env15));
        assertNotEquals(new Addition(new Constant(5.0), new Variable ("g")), evaluator15.evaluate(a3, env15));

        StackEnvironment env16 = new StackEnvironment();
        EvaluationVisitor evaluator16 = new EvaluationVisitor();
        assertEquals(new Sin(new Variable("x")), evaluator16.evaluate(s1, env16));
        assertNotEquals(new Sin(new Variable("y")), evaluator16.evaluate(s1, env16));

        StackEnvironment env17 = new StackEnvironment();
        EvaluationVisitor evaluator17 = new EvaluationVisitor();
        assertEquals(new Constant(3.0), evaluator17.evaluate(asv11, env17));

        StackEnvironment env18 = new StackEnvironment();
        EvaluationVisitor evaluator18 = new EvaluationVisitor();
        assertEquals(new Constant(4.0), evaluator18.evaluate(new Assignment(new Constant(4.0), new Variable("y")), env18));

        StackEnvironment env19 = new StackEnvironment();
        EvaluationVisitor evaluator19 = new EvaluationVisitor();
        assertEquals(evaluator19.evaluate(new Addition(new Scope(new Assignment(new Constant(1.0), new Variable("x"))), new Scope(new Assignment(new Constant(1.0), new Variable("x")))), env19), new Constant(2.0));

        StackEnvironment env20 = new StackEnvironment();
        EvaluationVisitor evaluator20 = new EvaluationVisitor();
        assertEquals(evaluator20.evaluate(new Scope(new Assignment(new Scope(new Assignment(new Constant(1.0), new Variable("x"))), new Variable("x"))), env20), new Constant(1.0));

        StackEnvironment env21 = new StackEnvironment();
        EvaluationVisitor evaluator21 = new EvaluationVisitor();
        assertEquals(evaluator21.evaluate(new Scope(new Addition(new Assignment(new Constant(2.0), new Variable("x")), new Scope(new Assignment(new Constant(1.0), new Variable("x"))))), env21), new Constant(3.0));

        StackEnvironment env22 = new StackEnvironment();
        EvaluationVisitor evaluator22 = new EvaluationVisitor();
        assertEquals(evaluator22.evaluate(new Addition(new Assignment(new Constant(1.0), new Variable("x")), new Scope(new Addition(new Assignment(new Addition(new Constant(2.0), new Variable("x")), new Variable("x")), new Scope(new Assignment(new Addition(new Constant(3.0), new Variable("x")), new Variable("x")))))), env22), new Constant(10.0));

        StackEnvironment env23 = new StackEnvironment();
        EvaluationVisitor evaluator23 = new EvaluationVisitor();
        ArrayList<SymbolicExpression> sequence = new ArrayList<>();
        ArrayList<Atom> parameters = new ArrayList<>();
        parameters.add(new Constant(4));
        parameters.add(new Constant(3));
        ArrayList<Atom> arguments = new ArrayList<>();
        arguments.add(new Variable("x"));
        arguments.add(new Variable("y"));
        sequence.add(new Conditionals(new Variable("x"), ">", new Variable("y"), new Scope(new Variable("x")), new Scope(new Variable("y"))));
        Sequence sequence1 = new Sequence(arguments, new Variable("max"), sequence);
        FunctionDeclaration max = new FunctionDeclaration(sequence1);
        evaluator23.evaluate(max, env23);
        FunctionCall functionCall = new FunctionCall(new Variable("max"), parameters);
        SymbolicExpression evaluatedResult = evaluator23.evaluate(functionCall, env23);
        assertEquals(evaluatedResult, new Constant(4));
    }

    @Test
    public void testToString() {
        Constant c1 = new Constant(5);
        Constant c2 = new Constant(2);
        Variable v = new Variable("x");
        Addition a = new Addition(c1, v);
        Multiplication m = new Multiplication(a, c2);
        Constant c3 = new Constant(3);
        Division d = new Division(c3, c2);
        Subtraction s = new Subtraction(c1, d);
        Sin sin = new Sin(m);
        Multiplication multiplication = new Multiplication(sin, v);
        Assignment as = new Assignment(v, s);
        Log log = new Log(sin);
        Division e = new Division(m, log);
        Exp ex = new Exp(e);
        Constant c4 = new Constant(42);
        Addition a2 = new Addition(c4, ex);
        Subtraction s1 = new Subtraction(m, log);

        String expected1 = "(5.0 + x) * 2.0";
        assertEquals(m.toString(), expected1);

        String expected2 = "5.0 - 3.0 / 2.0";
        assertEquals(s.toString(), expected2);

        String expected3 = "Sin((5.0 + x) * 2.0) * x";
        assertEquals(multiplication.toString(), expected3);

        String expected4 = "x";
        assertEquals(as.toString(), expected4);

        String expected5 = "42.0 + Exp((5.0 + x) * 2.0 / Log(Sin((5.0 + x) * 2.0)))";
        assertEquals(a2.toString(), expected5);

        String expected6 = "(5.0 + x) * 2.0 - Log(Sin((5.0 + x) * 2.0))";
        assertEquals(s1.toString(), expected6);
    }

    @Test
    public void testParser() throws IOException {
        Environment environment = new Environment();
        final CalculatorParser pars = new CalculatorParser();

        Constant c1 = new Constant(5);
        Constant c2 = new Constant(2);
        Variable v = new Variable("x");
        Addition a = new Addition(c1, v);
        SymbolicExpression m = new Multiplication(a, c2);
        Constant c3 = new Constant(3);
        Division d = new Division(c3, c2);
        SymbolicExpression s = new Subtraction(c1, d);
        Sin sin = new Sin(m);
        SymbolicExpression multiplication = new Multiplication(sin, v);
        Assignment as = new Assignment(v, s);
        Log log = new Log(sin);
        Division e = new Division(m, log);
        Exp ex = new Exp(e);
        Constant c4 = new Constant(42);
        Addition a2 = new Addition(c4, ex);
        Subtraction s1 = new Subtraction(m, log);

        String string1 = m.toString();
        SymbolicExpression sym1 = pars.parse(string1);
        assertEquals(sym1, m);

        String string2 = s.toString();
        SymbolicExpression sym2 = pars.parse(string2);
        assertEquals(sym2, s);

        String string3 = multiplication.toString();
        SymbolicExpression sym3 = pars.parse(string3);
        assertEquals(sym3, multiplication);

        String string4 = as.toString();
        Variable x = new Variable(string4);
        SymbolicExpression sym4 = pars.parse(string4);
        assertEquals(sym4, x); 

        String string5 = a2.toString();
        SymbolicExpression sym5 = pars.parse(string5);
        assertEquals(sym5, a2);

        String string6 = s1.toString();
        SymbolicExpression sym6 = pars.parse(string6);
        assertEquals(sym6, s1);
    }
}