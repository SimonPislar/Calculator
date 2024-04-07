package org.ioopm.calculator.ast;

public class Assignment extends Binary {

    public Assignment(SymbolicExpression left, SymbolicExpression right) {
        super(left, right);
    }

    @Override
    public String getName() {
        return "=";
      }

    @Override
    public String toString() {
        return lhs.toString();
    }
     
    @Override
    public int getPriority() {
        return lhs.getPriority();
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}
