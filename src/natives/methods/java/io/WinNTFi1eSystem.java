package natives.methods.java.io;

import clazz.instances.InstancedClazz;
import clazz.instances.PrimArray;
import natives.methods.Adaptor;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.HashMap;
import java.util.Map;

@IClazzSpecifier(Clazz = "java/io/WinNTFileSystem")
public class WinNTFi1eSystem {
    public static final class FDEntry {
        FileInputStream inputStream;

    }
    public static int FD_C = 1;
    public static Map<Integer, FDEntry> FD_S = new HashMap<>();

    @INativeMethod(Static = true)
    public static void initIDs() {

    }

    @INativeMethod
    public static String canonicalize0(InstancedClazz instance, String path) {
        try {
            System.out.println("Finding canonical path for: " + path);
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Canonical path fialed " +path);
        }
    }

    public static final int BA_EXISTS    = 0x01;
    public static final int BA_REGULAR   = 0x02;
    public static final int BA_DIRECTORY = 0x04;
    public static final int BA_HIDDEN    = 0x08;

    //DefaultFileSystem.getFileSystem().getBooleanAttributes(new File("D:\\expr\\out\\production\\JVMImpl\\Main.class"))
    @INativeMethod
    public static int getBooleanAttributes(InstancedClazz system, InstancedClazz file) {
        String path = Adaptor.toString((InstancedClazz) file.fields[0]);//File.path
        int attrs = new File(path).isFile()?BA_REGULAR:0;
        attrs |= new File(path).exists()?BA_EXISTS:0;
        attrs |= new File(path).isDirectory()?BA_DIRECTORY:0;
        attrs |= new File(path).isHidden()?BA_HIDDEN:0;
        return attrs;
    }

    @INativeMethod
    public static long getLength(InstancedClazz system, InstancedClazz file) {
        String path = Adaptor.toString((InstancedClazz) file.fields[0]);//File.path
        return new File(path).length();
    }

}
