package natives.methods.java.io;

import clazz.instances.InstancedClazz;
import clazz.instances.special.PrimTypeObj;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

import java.io.IOException;

@IClazzSpecifier(Clazz = "java/io/FileDescriptor")
public class Fi1eDescriptor {
    @INativeMethod(Static = true)
    public static void initIDs() {

    }

    @INativeMethod(Static = true)
    public static long getHandle(int fd) {
        return 0;
    }

    @INativeMethod(Static = true)
    public static boolean getAppend(int fd) {
        return fd != 0;
    }

    @INativeMethod
    public static void close0(InstancedClazz descriptor) {
        int fd = ((PrimTypeObj<Integer>)descriptor.clazz.fields.get("fd").accessor.get(descriptor)).object;
        WinNTFi1eSystem.FDEntry entry = WinNTFi1eSystem.FD_S.remove(fd);
        if (entry == null)
            throw new IllegalStateException();
        try {
            entry.inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
