package org.ioopm.calculator.ast;

import java.util.ArrayDeque;
import java.util.Iterator;

public class StackEnvironment extends Environment {
    private final ArrayDeque<Environment> deque;

    public StackEnvironment() {
        deque = new ArrayDeque<>();
    }

    public void popEnvironment() {
        deque.pollFirst();
    }

    public void pushEnvironment() {
        deque.addFirst(new Environment());
    }

    @Override
    public SymbolicExpression get(Object k) {
        if (!deque.isEmpty()) {
            Iterator<Environment> iter = deque.iterator();
            Environment currentEnv;
            while (iter.hasNext()) {
                currentEnv = iter.next();
                if (currentEnv.containsKey(k)) {
                    return currentEnv.get(k);
                }
            }
        }
        return super.get(k);
    }

    @Override 
    public SymbolicExpression put(Variable k, SymbolicExpression v) {
        if (deque.isEmpty()) {
            return super.put(k, v);
         }
        else return deque.peekFirst().put(k, v); 
    }
}
