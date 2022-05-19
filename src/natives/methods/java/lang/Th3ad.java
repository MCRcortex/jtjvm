package natives.methods.java.lang;

import clazz.instances.InstancedClazz;
import natives.BootClazzLoader;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "java/lang/Thread")
public class Th3ad {
    public static final ThreadLocal<InstancedClazz> threadClazzes = new ThreadLocal<>();

    @INativeMethod(Static = true)
    public static void registerNatives() {

    }

    @INativeMethod(Static = true)
    public static InstancedClazz currentThread() {
        return threadClazzes.get();
    }

    @INativeMethod
    public static void setPriority0(InstancedClazz instance, int priority) {

    }

    @INativeMethod
    public static boolean isAlive(InstancedClazz instance) {
        //TODO: IMPLMENT PROERLY
        return currentThread() == instance;
    }

    @INativeMethod
    public static void start0(InstancedClazz instance) {
        //TODO: IMPLMENT THREADS
        return;
    }

}
