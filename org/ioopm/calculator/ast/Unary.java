package org.ioopm.calculator.ast;

public abstract class Unary extends SymbolicExpression {
    protected SymbolicExpression expr;
    
    public Unary(SymbolicExpression e) {
        expr = e;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Unary) {
            return this.equals((Unary) other);
        } else {
            return false;
        }
    }
    
    public boolean equals(Unary other) {
        return this.expr.equals(other.expr);
    }

    public String toString() {
        return getName() + "(" + expr.toString() + ")";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
