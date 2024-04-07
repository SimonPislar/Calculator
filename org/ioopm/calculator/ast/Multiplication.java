package org.ioopm.calculator.ast;

public class Multiplication extends Binary{

    public Multiplication(SymbolicExpression left, SymbolicExpression right) {
        super(left, right);
    }

    @Override
    public String getName() {
        return "*";
      }
    
    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }

}
