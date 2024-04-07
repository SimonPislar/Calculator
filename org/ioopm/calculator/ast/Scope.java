package org.ioopm.calculator.ast;

public class Scope extends SymbolicExpression {
    protected SymbolicExpression expr;

    public Scope(SymbolicExpression expr) {
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "{" + expr.toString() + "}";
    }
    
    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

    public int getPriority() {
        return expr.getPriority();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Scope) {
            return this.equals((Scope) other);
        } else {
            return false;
        }
    }

    public boolean equals(Scope other) { 
        return expr.equals(other.expr);
    }
}
