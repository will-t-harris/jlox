package jlox;

import java.util.List;

class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;

    LoxFunction(Stmt.Function declaration) {
        this.declaration = declaration;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // each function call gets its own environment (local scope)
        Environment environment = new Environment(interpreter.globals);

        for (int i = 0; i < declaration.params.size(); i++) {
            // bind the function parameters to their values in this local scope
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        interpreter.executeBlock(declaration.body, environment);

        return null;
    }
}