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
import util.NamedDescription;

import java.util.List;
import java.util.stream.Collectors;

//NOTE: this is just here cause it looks nicer xdd
@IClazzSpecifier(Clazz = "jdk/internal/reflect/NativeConstructorAccessorImpl")
public class NativeConstructorAccessorImpl {
    @INativeMethod(Static = true)
    public static OOP newInstance0(InstancedClazz constructor, AArray args) {
        ClazzObj c = (ClazzObj) constructor.clazz.fields.get("clazz").accessor.get(constructor);
        c.clazz.ensureResolved();
        if (c.arrayDim != 0)
            throw new IllegalStateException();
        //TODO: IMPLMENT

        //Hack
        List<MethodMeta> inits = c.clazz.methods.values().stream().filter(a->a.disc.name.equals("<init>")).collect(Collectors.toList());
        if (inits.size() != 1)
            throw new IllegalStateException();

        InstancedClazz instance = c.clazz.instantiate();
        inits.get(0).executor.run(new Slot.ObjectRefSlot(instance));
        return instance;
    }
}
