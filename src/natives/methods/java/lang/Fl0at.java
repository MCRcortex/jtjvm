package natives.methods.java.lang;

import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "java/lang/Float")
public class Fl0at {
    @INativeMethod(Static = true)
    public static int floatToRawIntBits(float f) {
        return Float.floatToRawIntBits(f);
    }
}
