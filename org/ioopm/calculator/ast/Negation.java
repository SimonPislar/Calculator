package org.ioopm.calculator.ast;

public class Negation extends Unary {
 
    public Negation(SymbolicExpression e) {
        super(e);
    }

    @Override
    public String getName() {
        return "negate";
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}
