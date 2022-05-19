package natives.methods.jdk.internal.loader;

import clazz.instances.InstancedClazz;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier( Clazz = "jdk/internal/loader/NativeLibraries")
public class Nativ3Libraries {

    @INativeMethod(Static = true)
    public static String findBuiltinLib(String lib) {
        return null;
    }

    @INativeMethod(Static = true)
    public static boolean load(InstancedClazz impl, String name, boolean isBuiltin, boolean isJNI, boolean throwExceptionIfFail) {
        return true;
    }
}
