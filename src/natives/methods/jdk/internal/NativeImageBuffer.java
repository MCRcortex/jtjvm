package natives.methods.jdk.internal;


import clazz.ClazzMeta;
import clazz.MethodMeta;
import clazz.instances.InstancedClazz;
import clazz.instances.PrimArray;
import interpreter.Slot;
import natives.BootClazzLoader;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;
import util.DDescription;
import util.NamedDescription;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@IClazzSpecifier(Clazz = "jdk/internal/jimage/NativeImageBuffer")
public class NativeImageBuffer {
    @INativeMethod(Static = true)
    public static InstancedClazz getNativeMap(String module) throws IOException {
        //HACK!! TODO: implment native libraries
        ClazzMeta bbc = BootClazzLoader.ROOT.getOrLoadClazz("java/nio/ByteBuffer");
        bbc.ensureResolved();
        MethodMeta method = bbc.methods.get(new NamedDescription("wrap", "([B)Ljava/nio/ByteBuffer;"));
        byte[] fb = Files.readAllBytes(new File(module).toPath());
        PrimArray<Byte> moduleBytes = new PrimArray<>(fb.length, DDescription.BYTE);
        for(int i = 0; i < fb.length; i++) moduleBytes.array[i] = fb[i];
        return (InstancedClazz) ((Slot.ObjectRefSlot)method.executor.run(new Slot.ObjectRefSlot(moduleBytes))).value;
    }
}
