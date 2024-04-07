package org.ioopm.calculator.ast;

public class Log extends Unary {
    
    public Log(SymbolicExpression e) {
        super(e);
    }

    @Override
    public String getName() {
        return "Log";
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}
