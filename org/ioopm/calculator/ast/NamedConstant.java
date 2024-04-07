package org.ioopm.calculator.ast;

public class NamedConstant extends Atom {
    String identifier;
    double value;
    
    public NamedConstant(String id ,double val) {
        value = val;
        identifier = id;
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof NamedConstant) {
            return this.equals((NamedConstant) other);
            } 
        else {
            return false;
            }
    }

    public boolean equals(NamedConstant other) { 
        return this.value == other.value && this.identifier.compareTo(other.identifier) == 0;
    }

    @Override
    public boolean isConstant() { 
        return true;
    }
    
    @Override
    public double getValue() {
        return value;
    }

    public String toString() {
        return identifier;
    }
}