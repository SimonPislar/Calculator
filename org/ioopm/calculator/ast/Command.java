package org.ioopm.calculator.ast;

public abstract class Command extends SymbolicExpression {
    
    @Override
    public boolean isCommand() {
        return true;
    }

    public static class Quit extends Command {
        private static final Quit theInstance = new Quit();
        private Quit() {}

        public static Quit instance() {
            return theInstance;
        }

        @Override
        public SymbolicExpression accept(Visitor v) {
            return v.visit(this);
        }
    }

    public static class Vars extends Command {
        private static final Vars theInstance = new Vars();
        private Vars() {}

        public static Vars instance() {
            return theInstance;
        }

        @Override
        public SymbolicExpression accept(Visitor v) {
            return v.visit(this);
        }
    }

    public static class Clear extends Command {
        private static final Clear theInstance = new Clear();
        private Clear() {}

        public static Clear instance() {
            return theInstance;
        }

        @Override
        public SymbolicExpression accept(Visitor v) {
            return v.visit(this);
        }
    }

    public static class End extends Command {
        private static final End theInstance = new End();
        private End() {}

        public static End instance() {
            return theInstance;
        }

        @Override
        public SymbolicExpression accept(Visitor v) {
            return v.visit(this);
        }
    }


}
