package clazz;

import clazzParser.Attribute;
import interpreter.BytecodeInterpreter;
import interpreter.Slot;
import interpreter.StackFrames;

public class BytecodeExecutor implements IMethodExecutor {
    public int max_stack;
    public int max_locals;
    public byte[] bytecode;
    public Attribute.ExceptionElement[] exception_table;

    private final MethodMeta method;

    public BytecodeExecutor(MethodMeta method, Attribute.Code code) {
        max_stack = code.max_stack;
        max_locals = code.max_locals;
        bytecode = code.code;
        exception_table = code.exception_table;
        this.method = method;
    }

    @Override
    public Slot run(Slot... args) {
        StackFrames.push(method);
        Slot result = BytecodeInterpreter.runBytecode(method, args);
        StackFrames.pop();
        return result;
    }
}
