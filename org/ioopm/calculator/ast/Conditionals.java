package org.ioopm.calculator.ast;

public class Conditionals extends SymbolicExpression{
    protected Atom left;
    protected String op;
    protected Atom right;
    protected Scope ifScope;
    protected Scope elseScope;

    public Conditionals(Atom left, String op, Atom right, Scope ifScope, Scope elseScope) {
        this.left = left;
        this.op = op;
        this.right = right;
        this.ifScope = ifScope;
        this.elseScope = elseScope;
    }


    @Override
    public String toString() {
        return "if " + left.toString() + " " + op + " " + ifScope.toString() + " else " + elseScope.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Conditionals) {
            return this.equals((Conditionals) other);
        } else {
            return false;
        }
    }

    public boolean equals(Conditionals other) { 
        return (left.equals(other.left) && right.equals(other.right) && op.equals(other.op) 
                && ifScope.equals(other.ifScope) && elseScope.equals(other.elseScope));
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}
