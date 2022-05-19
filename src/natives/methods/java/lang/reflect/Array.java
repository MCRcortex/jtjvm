package natives.methods.java.lang.reflect;

import clazz.instances.AArray;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.special.ClazzObj;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "java/lang/reflect/Array")
public class Array {
    @INativeMethod(Static = true)
    public static OOP newArray(ClazzObj clazz, int length) {
        return new AArray(length, clazz);
    }
}
