package natives.methods.java.lang;

import clazz.ClazzMeta;
import clazz.instances.InstancedClazz;
import clazz.instances.PrimArray;
import clazz.instances.special.ClazzLoaderObj;
import clazz.instances.special.ClazzObj;
import natives.BootClazzLoader;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "java/lang/ClassLoader")
public class C1assL0ader {
    @INativeMethod(Static = true)
    public static void registerNatives() {

    }

    //static native Class<?> defineClass0(ClassLoader loader,
    //                                        Class<?> lookup,
    //                                        String name,
    //                                        byte[] b, int off, int len,
    //                                        ProtectionDomain pd,
    //                                        boolean initialize,
    //                                        int flags,
    //                                        Object classData);
    @INativeMethod(Static = true)
    public static ClazzObj defineClass0(ClazzLoaderObj instance,
                                        ClazzObj lookup,
                                        String name,
                                        PrimArray<Byte> b, int off, int len,
                                        InstancedClazz pd,
                                        boolean initialize,
                                        int flags,
                                        InstancedClazz classData) {
        byte[] bytes = new byte[b.array.length];
        for(int i = 0; i < bytes.length; i++) bytes[i] = b.array[i];
        //ULTRA HACK TEST
        ClazzMeta newClazz = lookup.clazz.loader.defineClazz0(bytes);
        if (classData != null)
            newClazz.getClazzObj().getSuperMeta().fields.get("classData").accessor.set(newClazz.getClazzObj(), classData);

        //TODO: NEED TO LOOK INTO FLAGS? and other shit
        if (initialize)
            newClazz.ensureResolved();
        return newClazz.getClazzObj();
    }

    //static native Class<?> defineClass1(ClassLoader loader,
    //                                    String name,
    //                                    byte[] b,
    //                                    int off,
    //                                    int len,
    //                                    ProtectionDomain pd,
    //                                    String source);
    @INativeMethod(Static = true)
    public static ClazzObj defineClass1(ClazzLoaderObj instance,
                                        String name,
                                        PrimArray<Byte> b, int off, int len,
                                        InstancedClazz pd,
                                        String source) {
        byte[] bytes = new byte[b.array.length];
        for(int i = 0; i < bytes.length; i++) bytes[i] = b.array[i]!=null?b.array[i]:0;

        //TODO: need to load it via the instance and then call ClassLoader.addClass

        ClazzMeta newClazz = instance.defineClazz(bytes);
        newClazz.ensureResolved();
        //TODO: SET IT VIA THE source??????
        name = name.replace(".", "/");
        if (name.lastIndexOf("/") != -1) {
            String packageName = name.substring(0, name.lastIndexOf("/"));
            InstancedClazz module = M0dule.MODULE_TREE.get(packageName);
            if (module == null)
                module = ((InstancedClazz)instance.getLoadedClazz("java/lang/ClassLoader").fields.get("unnamedModule").accessor.get(instance));//throw new IllegalStateException();
            newClazz.getClazzObj().getSuperMeta().fields.get("module").accessor.set(newClazz.getClazzObj(), module);
        } else
            newClazz.getClazzObj().getSuperMeta().fields.get("module").accessor.set(newClazz.getClazzObj(), instance.getLoadedClazz("java/lang/ClassLoader").fields.get("unnamedModule").accessor.get(instance));


        //newClazz.getClazzObj().getSuperMeta().fields.get("module").accessor.set(newClazz.getClazzObj(), );
        //newClazz.ensureResolved();
        return newClazz.getClazzObj();
    }


    @INativeMethod(Static = true)
    public static ClazzObj findBootstrapClass(String clazz) {
        ClazzMeta m = BootClazzLoader.ROOT.getOrLoadClazz(clazz);
        return m==null?null:m.getClazzObj(0);
    }


    @INativeMethod
    public static ClazzObj findLoadedClass0(InstancedClazz loader, String clazz) {
        //TODO: IMPLMENT
        //ClazzMeta clazzMeta = loader.clazz.loader.getLoadedClazz(clazz);
        return null;//clazzMeta!=null?clazzMeta.getClazzObj():null;
    }

}
