package org.ioopm.calculator.ast;

import java.util.ArrayList;

public class EvaluationVisitor implements Visitor {
    private StackEnvironment stackEnv;

    public SymbolicExpression evaluate(SymbolicExpression topLevel, StackEnvironment stackEnv) {
        this.stackEnv = stackEnv;
        return topLevel.accept(this);
    }

    @Override
    public SymbolicExpression visit(Addition n) {
        SymbolicExpression left = n.lhs.accept(this);
        SymbolicExpression right = n.rhs.accept(this);

        if (left.isConstant() && right.isConstant()) {
            return new Constant(left.getValue() + right.getValue());
        } 
        else if (left.isConstant() && !right.isConstant()) {
            return new Addition(new Constant(left.getValue()), right);
        }
        else if (!left.isConstant() && right.isConstant()) {
            return new Addition(left, new Constant(right.getValue()));
        }
        else return new Addition(left, right);
    }

    @Override
    public SymbolicExpression visit(Division n) {
        SymbolicExpression left = n.lhs.accept(this);
        SymbolicExpression right = n.rhs.accept(this);

        if (left.isConstant() && right.isConstant()) {
            return new Constant(left.getValue() / right.getValue());
        } 
        else if (left.isConstant() && !right.isConstant()) {
            return new Division(new Constant(left.getValue()), right);
        }
        else if (!left.isConstant() && right.isConstant()) {
            return new Division(left, new Constant(right.getValue()));
        }
        else return new Division(left, right);
    }

    @Override
    public SymbolicExpression visit(Multiplication n) {
        SymbolicExpression left = n.lhs.accept(this);
        SymbolicExpression right = n.rhs.accept(this);

        if (left.isConstant() && right.isConstant()) {
            return new Constant(left.getValue() * right.getValue());
        } 
        else if (left.isConstant() && !right.isConstant()) {
            return new Multiplication(new Constant(left.getValue()), right);
        }
        else if (!left.isConstant() && right.isConstant()) {
            return new Multiplication(left, new Constant(right.getValue()));
        }
        else return new Multiplication(left, right);
    }

    @Override
    public SymbolicExpression visit(Subtraction n) {
        SymbolicExpression left = n.lhs.accept(this);
        SymbolicExpression right = n.rhs.accept(this);

        if (left.isConstant() && right.isConstant()) {
            return new Constant(left.getValue() - right.getValue());
        } 
        else if (left.isConstant() && !right.isConstant()) {
            return new Subtraction(new Constant(left.getValue()), right);
        }
        else if (!left.isConstant() && right.isConstant()) {
            return new Subtraction(left, new Constant(right.getValue()));
        }
        else return new Subtraction(left, right);
    }

    @Override
    public SymbolicExpression visit(Assignment n) {
        if (n.rhs instanceof NamedConstant) {
            throw new IllegalAssignmentException("Cannot assign value to a named constant"); 
        }
        if (n.rhs instanceof Variable) {
            SymbolicExpression tmp = n.lhs.accept(this);
                stackEnv.put((Variable) n.rhs, n.lhs.accept(this));
                   return tmp; 
            }
        else throw new IllegalAssignmentException("Assignment to something other than variable");
          }

    @Override
    public SymbolicExpression visit(Cos n) {
        SymbolicExpression arg = n.expr.accept(this);
        if (arg.isConstant()) {
            return new Constant(Math.cos(arg.getValue()));
        } else {
            return new Cos(arg);
        }
    }

    @Override
    public SymbolicExpression visit(Exp n) {
        SymbolicExpression arg = n.expr.accept(this);
        if (arg.isConstant()) {
            return new Constant(Math.exp(arg.getValue()));
        } 
        else {
            return new Exp(arg);
        }
    }

    @Override
    public SymbolicExpression visit(Log n) {
        SymbolicExpression arg = n.expr.accept(this);
        if (arg.isConstant()) {
            return new Constant(Math.log(arg.getValue()));
        } else {
            return new Log(arg);
        }
    }

    @Override
    public SymbolicExpression visit(Negation n) {
        SymbolicExpression arg = n.expr.accept(this);
        if (arg.isConstant()) {
            return new Constant(-1 * (arg.getValue()));
        } 
        else {
            return new Negation(arg);
        }
    }

    @Override
    public SymbolicExpression visit(Sin n) {
        SymbolicExpression arg = n.expr.accept(this);
        if (arg.isConstant()) {
            return new Constant(Math.sin(arg.getValue()));
        } 
        else {
             return new Sin(arg);
        }
    }

    @Override
    public SymbolicExpression visit(Constant n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Variable n) {
        if (stackEnv.get(n) != null) {
            return stackEnv.get(n).accept(this);
        }
        else return n;
    }

    @Override
    public SymbolicExpression visit(Command n) {
        throw new RuntimeException("Commands may not be evaluated");
    }

    @Override
    public SymbolicExpression visit(NamedConstant n) {
        return n;
    }

    @Override
    public SymbolicExpression visit(Scope n) {
        stackEnv.pushEnvironment();
        SymbolicExpression visited = n.expr.accept(this);
        stackEnv.popEnvironment();
        return visited;
    }

    @Override
    public SymbolicExpression visit(Conditionals n) {
        SymbolicExpression leftEval = n.left.accept(this);
        SymbolicExpression rightEval = n.right.accept(this);

        try {
            leftEval.getValue();
            rightEval.getValue();
        }
        catch(RuntimeException unassignedVariable) {
            throw new RuntimeException("Variables must be assigned: ");
        }

        switch (n.op) {
            case "<":
                if (leftEval.getValue() < rightEval.getValue()) {
                    return n.ifScope.accept(this);
                } else return n.elseScope.accept(this);
            case ">":
                if (leftEval.getValue() > rightEval.getValue()) {
                    return n.ifScope.accept(this);
                } else return n.elseScope.accept(this);
            case ">=":
                if (leftEval.getValue() >= rightEval.getValue()) {
                    return n.ifScope.accept(this);
                } else return n.elseScope.accept(this);
            case "<=":
                if (leftEval.getValue() <= rightEval.getValue()) {
                    return n.ifScope.accept(this);
                } else return n.elseScope.accept(this);
            case "==":
                if (leftEval.getValue() == rightEval.getValue()) {
                    return n.ifScope.accept(this);
                } else return n.elseScope.accept(this);
            default:
                throw new RuntimeException("Invalid operator: ");
        }
    }

    @Override
    public SymbolicExpression visit(FunctionCall n) {
        Sequence SeqCopy;
        Sequence seq = (Sequence) stackEnv.get(n.identifier);
        if (seq == null) {
            throw new RuntimeException("No function with that name: ");
        }
        else {
            int i = 0;
            ArrayList<SymbolicExpression> bodyCopy = new ArrayList<>(seq.body);
            SeqCopy = new Sequence(seq.params, seq.name, bodyCopy);
           for (Atom arg: n.args) {
                if (arg instanceof Variable) {
                    if (stackEnv.get(arg) == null) {
                        throw new RuntimeException("\nCannot use unassigned Variable as function argument\n");
                    }
                }
                Atom param = seq.params.get(i);
                i++;
                bodyCopy.add(0, new Assignment(arg, param));
           }
           bodyCopy.add(0, new Assignment(new Constant(Math.PI), new Variable("pi")));
           bodyCopy.add(0, new Assignment(new Constant(Math.E), new Variable("e")));
           bodyCopy.add(0, new Assignment(new Constant(42.0), new Variable("Answer")));
           bodyCopy.add(0, new Assignment(new Constant(Math.pow(6.022140857 * 10, 23)), new Variable("L")));
        }
        return SeqCopy.accept(this);
    }

    @Override
    public SymbolicExpression visit(FunctionDeclaration n) {
        Variable name = n.seq.name;
        stackEnv.put(name, n.seq);
        return name;
    }

    @Override
    public SymbolicExpression visit(Sequence n) {
        SymbolicExpression current = null;
        stackEnv.pushEnvironment();
        for(SymbolicExpression expr : n.body) {
            current = expr.accept(this);
        }
        stackEnv.popEnvironment();
        return current;
    }
}