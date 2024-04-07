package org.ioopm.calculator.ast;

import java.util.HashMap;

public class ReassignmentChecker implements Visitor {
    private HashMap<Variable, Integer> variables;
    private StackEnvironment stackEnv;

    public boolean check(SymbolicExpression topLevel, StackEnvironment stackEnv) {
        this.stackEnv = stackEnv;
        this.variables = new HashMap<>();
        try {
            topLevel.accept(this);
            return true;
        } catch(IllegalAssignmentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public SymbolicExpression visit(Addition n) {
        n.rhs.accept(this);
        n.lhs.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Assignment n) {
        Variable var = (Variable) n.rhs;
        if (variables.containsKey(var)) {
            throw new IllegalAssignmentException("Error: Variable " + var.toString() + " assigned multiple times in the same expression!\n");
        } else {
            variables.put(var, 1);
        }
        n.lhs.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Constant n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Cos n) {
        n.expr.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Division n) {
        n.rhs.accept(this);
        n.lhs.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Exp n) {
        n.expr.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Log n) {
        n.expr.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Multiplication n) {
        n.rhs.accept(this);
        n.lhs.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Negation n) {
        n.expr.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Sin n) {
        n.expr.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Subtraction n) {
        n.rhs.accept(this);
        n.lhs.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Variable n) {
        if (stackEnv.get(n) != null) {
            stackEnv.get(n).accept(this);
        }
        return n;
    }

    @Override
    public SymbolicExpression visit(Command n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(NamedConstant n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Scope n) {
        HashMap<Variable,Integer> varClone = new HashMap<>();
        variables.putAll(varClone);
        variables.clear();
        SymbolicExpression visited = n.expr.accept(this);
        variables = varClone;
        return visited;
    }

    @Override
    public SymbolicExpression visit(Conditionals n) {
        n.ifScope.accept(this);
        n.elseScope.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(FunctionCall n) {
        return n;
    }

    public SymbolicExpression visit(Sequence n) {
        for(SymbolicExpression expr : n.body) {
            expr.accept(this);
        }
        return n;
    }

    @Override
    public SymbolicExpression visit(FunctionDeclaration n) {
        n.seq.accept(this);
        return n;
    }
}
