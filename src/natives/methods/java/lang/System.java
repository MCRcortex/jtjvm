package natives.methods.java.lang;

import clazz.ClazzMeta;
import clazz.instances.AArray;
import clazz.instances.InstancedClazz;
import clazz.instances.PrimArray;
import clazz.instances.special.ClazzObj;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "java/lang/System")
public class System {
    @INativeMethod(Static = true)
    public static void registerNatives() {

    }

    @INativeMethod(Static = true)
    public static long nanoTime() {
        return java.lang.System.nanoTime();
    }

    @INativeMethod(Static = true)
    public static void arraycopy(Object source, int offseta, Object dest, int offsetb, int count) {
        if (source instanceof PrimArray<?>) {
            java.lang.System.arraycopy(((PrimArray)source).array, offseta, ((PrimArray)dest).array, offsetb, count);
        } else if (source instanceof AArray) {
            java.lang.System.arraycopy(((AArray)source).oops, offseta, ((AArray)dest).oops, offsetb, count);
        } else
            throw new IllegalStateException();
    }

    @INativeMethod(Static = true)
    public static void setIn0(InstancedClazz stream) {
        ClazzMeta sys = stream.clazz.loader.getLoadedClazz("java/lang/System");
        sys.fields.get("in").accessor.set(null, stream);
    }

    @INativeMethod(Static = true)
    public static void setOut0(InstancedClazz stream) {
        ClazzMeta sys = stream.clazz.loader.getLoadedClazz("java/lang/System");
        sys.fields.get("out").accessor.set(null, stream);
    }

    @INativeMethod(Static = true)
    public static void setErr0(InstancedClazz stream) {
        ClazzMeta sys = stream.clazz.loader.getLoadedClazz("java/lang/System");
        sys.fields.get("err").accessor.set(null, stream);
    }

    @INativeMethod(Static = true)
    public static int identityHashCode(InstancedClazz object) {
        if (object == null)
            return 0;
        //TODO: IMPLMENT
        if (object instanceof ClazzObj)
            return object.clazz.hashCode() + (((ClazzObj) object).arrayDim*34957863);
        return object.hashCode();
    }
    @INativeMethod(Static = true)
    public static String mapLibraryName(String object) {
        return object+".dll";
    }
}
