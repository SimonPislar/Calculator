package org.ioopm.calculator.ast;

import java.util.ArrayList;

public class FunctionCall extends SymbolicExpression {
    protected Variable identifier;
    protected ArrayList<Atom> args;

    public FunctionCall(Variable identifier, ArrayList<Atom> args) {
        this.identifier = identifier;
        this.args = args;
    }

    @Override
    public String getName() {
        return this.identifier.toString();
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);
    }
}
