package natives.methods;

import clazz.MethodMeta;
import clazz.field.InstancedFieldAccessor;
import clazz.instances.AArray;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.PrimArray;
import clazz.instances.special.ClazzObj;
import interpreter.BytecodeInterpreter;
import interpreter.Slot;
import util.NamedDescription;

import java.util.Arrays;
import java.util.List;

/**
 * Converts inner jvm classes and stuff to outer jvm classes and stuff
 */
public class Adaptor {
    public static Slot toInner(MethodMeta method, Class type, Object obj) {
        if (type.isArray()) {
            //FIXME: PURE PAIN
            //TODO: THIS
            if (type.getComponentType().equals(String.class)) {
                String[] strings = (String[]) obj;
                AArray out = new AArray(strings.length, method.clazz.loader.getOrLoadClazz("java/lang/String").getClazzObj(1));
                for (int i = 0; i < strings.length; i++) {
                    out.oops[i] = (OOP)toInner(method, type.componentType(), strings[i]).value;
                }
                return new Slot.ObjectRefSlot(out);
            }

            throw new IllegalStateException();
        } else {
            if (type == Void.TYPE)
                return null;
            if (type == Boolean.TYPE)
                return new Slot.IntSlot(((boolean) obj) ? 1 : 0);
            if (type == Long.TYPE)
                return new Slot.LongSlot((long) obj);
            if (type == Integer.TYPE)
                return new Slot.IntSlot((int) obj);
            if (type == Double.TYPE)
                return new Slot.DoubleSlot((double) obj);
            if (type.equals(String.class)) {
                if (obj == null)
                    return new Slot.ObjectRefSlot(null);
                return new Slot.ObjectRefSlot(BytecodeInterpreter.createStr(method.clazz.loader, (String) obj));
            }
            /*
            if (type.equals(InstancedClazz.class)) {//TODO: CHECK FOR OOP INSTEAD
                return new Slot.ObjectRefSlot((InstancedClazz)obj);
            }*/
            if (OOP.class.isAssignableFrom(type)) {
                return new Slot.ObjectRefSlot((OOP)obj);
            }
            throw new IllegalStateException();
        }
    }

    public static Object fromInner(Class type, Slot obj) {
        if (type.equals(InstancedClazz.class)) {
            return (InstancedClazz) ((Slot.ObjectRefSlot) obj).value;
        }
        if (type.equals(String.class)) {
            InstancedClazz strClz = (InstancedClazz) ((Slot.ObjectRefSlot) obj).value;
            return toString(strClz);
        }

        if (OOP.class.isAssignableFrom(type)) {
            return ((Slot.ObjectRefSlot) obj).value;
        }

        //TODO: check type is subclass of OOP
        if (obj instanceof Slot.ObjectRefSlot) {
            return obj.value;
        }

        if (type == Integer.TYPE)
            return ((Slot.IntSlot)obj).value;
        if (type == Float.TYPE)
            return ((Slot.FloatSlot)obj).value;
        if (type == Double.TYPE)
            return ((Slot.DoubleSlot)obj).value;
        if (type == Long.TYPE)
            return ((Slot.LongSlot)obj).value;
        if (type == Boolean.TYPE)
            return ((Slot.IntSlot) obj).value == 1;
        if (type == Character.TYPE)
            return (Character)(char)(int)((Slot.IntSlot) obj).value;

        throw new IllegalStateException();
    }


    public static String toString(InstancedClazz str) {
        if (!str.clazz.getName().equals("java/lang/String"))
            throw new IllegalArgumentException();
        Byte[] bytes = ((PrimArray<Byte>)str.fields[((InstancedFieldAccessor)str.clazz.fields.get("value").accessor).indexId]).array;
        byte[] bytes1 = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes1[i] = bytes[i];
        }
        return new String(bytes1);
    }
}
