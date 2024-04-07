package org.ioopm.calculator.ast;

public class Sin extends Unary {

    public Sin(SymbolicExpression e) {
        super(e);
    }
    
    @Override
    public String getName() {
        return "Sin";
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

}