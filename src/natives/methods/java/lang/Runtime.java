package natives.methods.java.lang;

import clazz.instances.InstancedClazz;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "java/lang/Runtime")
public class Runtime {
    @INativeMethod
    public static int availableProcessors(InstancedClazz instance) {
        return java.lang.Runtime.getRuntime().availableProcessors();
    }

    @INativeMethod
    public static long maxMemory(InstancedClazz instance) {
        return java.lang.Runtime.getRuntime().maxMemory();
    }
}
