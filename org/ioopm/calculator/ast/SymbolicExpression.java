package org.ioopm.calculator.ast;

public abstract class SymbolicExpression {


    public boolean isConstant() {
        return false;
    }

    public boolean isCommand() {
        return false;
    }

    public boolean isFunctionDeclaration() {
        return false;
    }

    public String getName() {
        throw new RuntimeException("getName() called on expression with no operator");
      }

    public int getPriority() {
        return 0;
    }

    public double getValue() {
        throw new RuntimeException("getValue called on non constant expression");
    }

    public abstract SymbolicExpression accept(Visitor v);
}