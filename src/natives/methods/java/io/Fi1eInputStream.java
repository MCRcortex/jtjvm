package natives.methods.java.io;

import clazz.instances.InstancedClazz;
import clazz.instances.PrimArray;
import clazz.instances.special.PrimTypeObj;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

import java.io.*;

@IClazzSpecifier(Clazz = "java/io/FileInputStream")
public class Fi1eInputStream {
    @INativeMethod(Static = true)
    public static void initIDs() {
    }



    @INativeMethod
    public static void open0(InstancedClazz filestream, String path) {
        InstancedClazz fd = (InstancedClazz) filestream.clazz.fields.get("fd").accessor.get(filestream);
        int id = WinNTFi1eSystem.FD_C++;
        fd.clazz.fields.get("fd").accessor.set(fd, new PrimTypeObj<>(id));
        WinNTFi1eSystem.FDEntry entry = new WinNTFi1eSystem.FDEntry();
        try {
            entry.inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
        System.err.println("Opening file input stream: " + path);
        WinNTFi1eSystem.FD_S.put(id, entry);
    }

    @INativeMethod
    public static int readBytes(InstancedClazz filestream, PrimArray<Byte> buffer, int off, int len) {
        InstancedClazz fdi = (InstancedClazz) filestream.clazz.fields.get("fd").accessor.get(filestream);
        int fd = ((PrimTypeObj<Integer>)fdi.clazz.fields.get("fd").accessor.get(fdi)).object;
        WinNTFi1eSystem.FDEntry entry = WinNTFi1eSystem.FD_S.get(fd);
        byte[] holder = new byte[buffer.length()];
        int out = 0;
        try {
            out = entry.inputStream.read(holder, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = off; i < out+off; i++)
            buffer.array[i] = holder[i];
        return out;
    }
}
