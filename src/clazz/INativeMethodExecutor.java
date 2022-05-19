package clazz;

import interpreter.Slot;

import java.lang.reflect.InvocationTargetException;

public interface INativeMethodExecutor {
    Slot run(MethodMeta method, Slot... args);
}
