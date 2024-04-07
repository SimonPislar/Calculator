package org.ioopm.calculator.ast;

public class Variable extends Atom implements Comparable<Variable> {
    protected String identifier;

    public Variable(String id) {
        this.identifier = id;
    }

    public int compareTo(Variable other) {
        return this.identifier.compareTo(other.identifier);
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Variable) {
            return this.equals((Variable) other);
        } else {
            return false;
        }
    }
    
    public boolean equals(Variable other) {
        return this.identifier.compareTo(other.identifier) == 0;
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
