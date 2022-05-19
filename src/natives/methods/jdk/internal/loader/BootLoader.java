package natives.methods.jdk.internal.loader;

import clazz.instances.InstancedClazz;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "jdk/internal/loader/BootLoader")
public class BootLoader {
    @INativeMethod(Static = true)
    public static void setBootLoaderUnnamedModule0(InstancedClazz module) {
        //TODO: IMPLMENT
    }
}
