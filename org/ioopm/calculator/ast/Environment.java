package org.ioopm.calculator.ast;

import java.util.HashMap;
import java.util.TreeSet;

public class Environment extends HashMap<Variable, SymbolicExpression> {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        TreeSet<Variable> vars = new TreeSet<>(this.keySet());
        for (Variable v : vars) {
            sb.append(v.toString());
            sb.append(" = ");
            sb.append(this.get(v));
            sb.append("\n");
        }
        return sb.toString();
        }
}
