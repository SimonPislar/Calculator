package org.ioopm.calculator.ast;

import java.util.ArrayList;

public class Sequence extends SymbolicExpression {
    protected ArrayList<Atom> params;
    protected Variable name;
    protected ArrayList<SymbolicExpression> body; 

    public Sequence(ArrayList<Atom> params, Variable name, ArrayList<SymbolicExpression> body) {
        this.params = params;
        this.name = name;
        this.body = body;
    }

    public ArrayList<SymbolicExpression> getBody() {
        return this.body;
    }

    @Override
    public SymbolicExpression accept(Visitor v) {
        return v.visit(this);   
    }

}