package org.ioopm.calculator.ast;

public abstract class Binary extends SymbolicExpression {
    protected SymbolicExpression lhs;
    protected SymbolicExpression rhs;

    public Binary(SymbolicExpression left, SymbolicExpression right) { 
        lhs = left;
        rhs = right;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Binary) {
            return this.equals((Binary) other);
        } else {
            return false;
        }
    }
    
    public boolean equals(Binary other) { 
        if(this.getName().compareTo(other.getName()) == 0) {
            return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
        }
        else return false;
    }

    public String toString() { 
        if (rhs.getPriority() < this.getPriority() && this.getPriority() <= lhs.getPriority()) {
            return lhs.toString() + " " + getName() + " " + "(" + rhs.toString() + ")";
        }
        else if (lhs.getPriority() < this.getPriority() && this.getPriority() <= rhs.getPriority()) {
            return "(" + lhs.toString() +")" + " " + getName() + " " + rhs.toString();
        }
        else if (lhs.getPriority() == rhs.getPriority() && this.getPriority() > lhs.getPriority()) {
            return "(" + lhs.toString() + ")" + " " + getName() + " " + "(" + rhs.toString() + ")";
        }
        else  {
            return lhs.toString() + " " + getName() + " " + rhs.toString();
        }
    }
}   
