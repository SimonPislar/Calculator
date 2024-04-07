package org.ioopm.calculator.ast;

public class Cos extends Unary {
   
    public Cos(SymbolicExpression e) { 
        super(e);
    }

    @Override
    public String getName() {
        return "Cos";
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}
