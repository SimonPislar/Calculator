package org.ioopm.calculator.ast;

public class FunctionDeclaration extends SymbolicExpression {
    protected Sequence seq;

    public FunctionDeclaration(Sequence seq) {
        this.seq = seq;
    }

    @Override
    public boolean isFunctionDeclaration() {
        return true;
    }

    public Sequence getSequence() {
        return this.seq;
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}
