package natives.methods.jdk.internal.misc;

import clazz.instances.special.ClazzObj;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "jdk/internal/misc/CDS")
public class CDS {

    @INativeMethod(Static = true)
    public static boolean isDumpingClassList0() {
        return false;
    }

    @INativeMethod(Static = true)
    public static boolean isDumpingArchive0() {
        return false;
    }

    @INativeMethod(Static = true)
    public static boolean isSharingEnabled0() {
        return true;//Just following what jvm has inbuilt
    }

    @INativeMethod(Static = true)
    public static long getRandomSeedForDumping() {
        return 0;//Just following what jvm has inbuilt
    }

    @INativeMethod(Static = true)
    public static void initializeFromArchive(ClazzObj obj) {
        //FIXME: actually understand what this does lol
    }
}
