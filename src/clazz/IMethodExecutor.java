package clazz;

import interpreter.Slot;

public interface IMethodExecutor {
    Slot run(Slot... args);
}
