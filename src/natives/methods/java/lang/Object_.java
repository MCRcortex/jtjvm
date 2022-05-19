package natives.methods.java.lang;

import clazz.instances.AArray;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.PrimArray;
import clazz.instances.special.ClazzObj;
import natives.BootClazzLoader;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "java/lang/Object")
public class Object_ {
    @INativeMethod
    public static int hashCode(OOP clazz) {
        //TODO: IMPLMENT
        return 0;
    }

    @INativeMethod
    public static ClazzObj getClass(OOP clazz) {
        //TODO: FIX THIS
        if (clazz instanceof InstancedClazz)
            return ((InstancedClazz) clazz).clazz.getClazzObj();
        if (clazz instanceof AArray)
            return ((AArray) clazz).clazz;
        if (clazz instanceof PrimArray<?>) {
            PrimArray array = (PrimArray) clazz;
            return BootClazzLoader.ROOT.getOrLoadClazz(array.type.clazz).getClazzObj(1);//FIXME: multidim arrays
        }
        return null;//TODO: HANDLE ARRAY TYPES
    }

    @INativeMethod
    public static void notifyAll(InstancedClazz clazz) {
        //TODO: implement this
    }

    @INativeMethod
    public static InstancedClazz clone(InstancedClazz clazz) {
        InstancedClazz clone = new InstancedClazz(clazz.clazz);
        java.lang.System.arraycopy(clazz.fields, 0, clone.fields, 0, clazz.fields.length);
        return clone;
    }
}
