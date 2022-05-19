package natives.methods.java.io;

import clazz.instances.InstancedClazz;
import clazz.instances.PrimArray;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "java/io/FileOutputStream")
public class Fi1eOutputStream {
    @INativeMethod(Static = true)
    public static void initIDs() {

    }

    @INativeMethod
    public static void writeBytes(InstancedClazz stream, PrimArray<Byte> data, int off, int len, boolean append) {

    }
}
