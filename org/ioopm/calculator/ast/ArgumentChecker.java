package org.ioopm.calculator.ast;

public class ArgumentChecker implements Visitor {
    private StackEnvironment stackEnv;

    public boolean check(SymbolicExpression topLevel, StackEnvironment stackenv) {
        this.stackEnv = stackenv;
        try {
            topLevel.accept(this);
            return true;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public SymbolicExpression visit(Addition n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Assignment n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Constant n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Cos n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Division n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Exp n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Log n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Multiplication n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Negation n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Sin n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Subtraction n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Variable n) {
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
        int argNum = n.args.size();
        Sequence seq = (Sequence) stackEnv.get(n.identifier);
        int paramNum = seq.params.size();
        if(argNum != paramNum) {
            throw new RuntimeException("function " + n.identifier.toString() + 
            " called with two few arguments. Expected " + paramNum + ", got "+ argNum);
        }
        else return n;
    }

    @Override
    public SymbolicExpression visit(Sequence n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(FunctionDeclaration n) {
        return n;
    }
}

