package natives.methods.jdk.internal.reflect;

import clazz.MethodMeta;
import clazz.instances.special.ClazzObj;
import interpreter.StackFrames;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

import java.util.List;

@IClazzSpecifier(Clazz = "jdk/internal/reflect/Reflection")
public class Reflecti0n {

    @INativeMethod(Static = true)
    public static ClazzObj getCallerClass() {
        //TODO: IMPLMENT THIS
        int i;
        List<MethodMeta> m = StackFrames.getFrames();
        //TODO: NEED TO SEE EXACTLY WHAT CLASSES TO SKIP !!!
        for (i = 0; m.get(i).clazz.getName().startsWith("jdk/internal/reflect")||m.get(i).clazz.getName().startsWith("java/lang/")||m.get(i).clazz.getName().startsWith("java/util/EnumMap"); i++);
        return m.get(i).clazz.getClazzObj();

    }

    @INativeMethod(Static = true)
    public static int getClassAccessFlags(ClazzObj clazz) {
        if (clazz.arrayDim != 0)
            throw new IllegalStateException();
        //TODO: need to figure out how to do this properly instead of forcing it to be public
        return clazz.clazz.flags | 1;
    }

    @INativeMethod(Static = true)
    public static boolean areNestMates(ClazzObj A, ClazzObj B) {
        //TODO: implment
        return true;
    }
}
