package org.ioopm.calculator.ast;

public class NamedConstantChecker implements Visitor {
    private StackEnvironment stackEnv;

    public boolean check(SymbolicExpression topLevel, StackEnvironment stackenv) {
        this.stackEnv = stackenv;
        try {
            topLevel.accept(this);
            return true;
        } catch (IllegalAssignmentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public SymbolicExpression visit(Addition n) {
        n.lhs.accept(this);
        n.rhs.accept(this);
        return n;
    }

    @Override
    public SymbolicExpression visit(Assignment n) {
        if (n.rhs instanceof NamedConstant) {
            throw new IllegalAssignmentException("Error: NamedConstant " + n.rhs + " may not be reassigned!\n");
        } else {
            return n;
        }
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
        n.lhs.accept(this);
        n.rhs.accept(this);
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
        n.lhs.accept(this);
        n.rhs.accept(this);
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
        n.lhs.accept(this);
        n.rhs.accept(this);
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
        n.expr.accept(this);
        return n;
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

    @Override
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
