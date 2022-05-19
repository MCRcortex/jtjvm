package natives.methods.java.lang.reflect;

import clazz.ClazzMeta;
import clazz.MethodMeta;
import clazz.instances.AArray;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.special.ClazzObj;
import interpreter.Slot;
import natives.methods.Adaptor;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

import java.util.List;
import java.util.stream.Collectors;

//NOTE: this is just here cause it looks nicer xdd
@IClazzSpecifier(Clazz = "jdk/internal/reflect/NativeMethodAccessorImpl")
public class NativeMethodAccessorImpl {
    @INativeMethod(Static = true)
    public static OOP invoke0(InstancedClazz method, OOP instance, AArray args) {
        if (!method.clazz.getName().equals("java/lang/reflect/Method"))
            throw new IllegalStateException();
        //Thrown together for tests
        //TODO: CHECK THIS IS RIGHT
        ClazzMeta clazz = ((ClazzObj)method.fields[5]).clazz;
        String name = Adaptor.toString((InstancedClazz) method.fields[7]);
        List<MethodMeta> methods = clazz.methods.values().stream().filter(a -> a.disc.name.equals(name)).collect(Collectors.toList());
        if (methods.size() != 1)
            throw new IllegalStateException();
        //FIXME: do it right
        return (OOP) methods.get(0).executor.run().value;
    }
}
