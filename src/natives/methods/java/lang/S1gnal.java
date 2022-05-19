package natives.methods.java.lang;

import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "jdk/internal/misc/Signal")
public class S1gnal {
    @INativeMethod(Static = true)
    public static int findSignal0(String signal) {
        //TODO: IMPLEMENT
        return 0;
    }

    @INativeMethod(Static = true)
    public static long handle0(int signal, long handler) {
        //TODO: IMPLEMENT
        return 0;
    }
}
