package natives.methods.java.lang;

import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "java/lang/Double")
public class D0uble {
    @INativeMethod(Static = true)
    public static long doubleToRawLongBits(double d) {
        return Double.doubleToRawLongBits(d);
    }

    @INativeMethod(Static = true)
    public static double longBitsToDouble(long l) {
        return Double.longBitsToDouble(l);
    }
}
