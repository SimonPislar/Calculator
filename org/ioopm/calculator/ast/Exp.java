package org.ioopm.calculator.ast;


public class Exp extends Unary {
    
    public Exp(SymbolicExpression e) {
        super(e);
    }

    @Override
    public String getName() {
        return "Exp";
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

}
