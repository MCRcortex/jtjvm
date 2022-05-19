package natives.methods.jdk.internal.misc;

import clazz.ClazzMeta;
import clazz.FieldMeta;
import clazz.field.InstancedFieldAccessor;
import clazz.instances.AArray;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.PrimArray;
import clazz.instances.special.ClazzObj;
import clazz.instances.special.PrimTypeObj;
import natives.BootClazzLoader;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;
import natives.methods.java.lang.invoke.Meth0dHandleNatives;

import java.util.Map;

import static util.Flags.ACC_STATIC;

@IClazzSpecifier(Clazz = "jdk/internal/misc/Unsafe")
public class Unsafe_ {
    @INativeMethod(Static = true)
    public static void registerNatives() {
        //Injecting constants here
        ClazzMeta unsafeConstants = BootClazzLoader.ROOT.getOrLoadClazz("jdk/internal/misc/UnsafeConstants");
        unsafeConstants.ensureResolved();
        unsafeConstants.fields.get("ADDRESS_SIZE0").accessor.set(null, new PrimTypeObj<>(8));
        unsafeConstants.fields.get("PAGE_SIZE").accessor.set(null, new PrimTypeObj<>(4028));
        //unsafeConstants.fields.get("UNALIGNED_ACCESS").accessor.set(null, new PrimTypeObj<>(true));
    }

    @INativeMethod
    public static int arrayBaseOffset0(InstancedClazz instance, ClazzObj clazz) {
        //FIXME: return actual value
        return 0;
    }

    @INativeMethod
    public static int arrayIndexScale0(InstancedClazz instance, ClazzObj clazz) {
        //FIXME: return actual value
        return 1;
    }

    @INativeMethod
    public static long objectFieldOffset1(InstancedClazz instance, ClazzObj clazz, String field) {
        //FIXME: return actual value
        if (clazz.arrayDim != 0)
            throw new IllegalStateException();
        FieldMeta fieldM = clazz.clazz.locateField(field);
        if ((fieldM.flags&ACC_STATIC) != 0)
            throw new IllegalStateException();
        return ((InstancedFieldAccessor)fieldM.accessor).indexId;
    }

    @INativeMethod
    public static void storeFence(InstancedClazz instance) {
        fullFence(instance);
    }

    @INativeMethod
    public static void fullFence(InstancedClazz instance) {
        //TODO: Implement a fence store?
        System.out.println("Fence");
    }

    //FIXME: DOESNT HAVE TO BE AN InstancedClazz obj CAN BE ANY OBJECT
    @INativeMethod
    public static boolean compareAndSetInt(InstancedClazz instance, InstancedClazz obj, long offset, int expected, int x) {
        if (obj == null) {
            throw new IllegalStateException("compareAndSet of statics not implmented");
        }
        //Lousy attempt at making it atomic
        synchronized (obj.fields) {
            int val = ((PrimTypeObj<Integer>) obj.fields[(int) offset]).object;
            if (val == expected) {
                obj.fields[(int) offset] = new PrimTypeObj<>(x);
                return true;
            } else {
                return false;
            }
        }
    }

    //FIXME: DOESNT HAVE TO BE AN InstancedClazz obj CAN BE ANY OBJECT
    @INativeMethod
    public static boolean compareAndSetLong(InstancedClazz instance, InstancedClazz obj, long offset, long expected, long x) {
        if (obj == null) {
            throw new IllegalStateException("compareAndSet of statics not implmented");
        }
        //Lousy attempt at making it atomic
        synchronized (obj.fields) {
            if (obj.fields[(int) offset] == null) throw new IllegalStateException();
            long val = (((PrimTypeObj<Long>) obj.fields[(int) offset]).object);
            if (val == expected) {
                obj.fields[(int) offset] = new PrimTypeObj<>(x);
                return true;
            } else {
                return false;
            }
        }
    }

    @INativeMethod
    public static boolean compareAndSetReference(InstancedClazz instance, OOP obj, long offset, OOP expected, OOP x) {
        if (obj == null) {
            throw new IllegalStateException("NOT IMPLMENTED");
        }
        if (obj instanceof AArray) {
            synchronized (((AArray) obj).oops) {
                OOP val = ((AArray) obj).oops[(int)offset];
                if (val == expected) {
                    ((AArray) obj).oops[(int) offset] = x;
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (obj instanceof InstancedClazz) {
            synchronized (((InstancedClazz) obj).fields) {
                OOP val = ((InstancedClazz) obj).fields[(int)offset];
                if (val == expected) {
                    ((InstancedClazz) obj).fields[(int) offset] = x;
                    return true;
                } else {
                    return false;
                }
            }
        }
        throw new IllegalStateException();
    }

    @INativeMethod
    public static OOP getReferenceVolatile(InstancedClazz instance, OOP obj, long offset) {
        if (obj == null) {
            throw new IllegalStateException("NOT IMPLMENTED");
        }
        if (obj instanceof AArray) {
            return ((AArray) obj).oops[(int)offset];
        }
        throw new IllegalStateException();
    }

    @INativeMethod
    public static OOP getReference(InstancedClazz instance, OOP obj, long offset) {
        if (obj == null) {
            throw new IllegalStateException("NOT IMPLMENTED");
        }
        if (obj instanceof AArray) {
            //TODO: IMPLEMENT PROPER SYNCING
            synchronized (((AArray) obj).oops) {
                return ((AArray) obj).oops[(int) offset];
            }
        }

        if (obj instanceof InstancedClazz) {
            //FieldMeta fm = Meth0dHandleNatives.FIELD_LUT.get(offset);
            //if (fm == null)
            //    throw new IllegalStateException();
            //return fm.accessor.get(obj);
            return ((InstancedClazz)obj).fields[(int) offset];//TODO: CHECK THIS IS CORRECT
        }

        throw new IllegalStateException();
    }

    @INativeMethod
    public static int getIntVolatile(InstancedClazz instance, OOP obj, long offset) {
        if (obj == null) {
            throw new IllegalStateException("NOT IMPLMENTED");
        }
        if (obj instanceof InstancedClazz) {
            return ((PrimTypeObj<Integer>)((InstancedClazz) obj).fields[(int)offset]).object;
        }
        throw new IllegalStateException();
    }


    @INativeMethod
    public static int getInt(InstancedClazz instance, OOP obj, long offset) {
        if (obj == null) {
            throw new IllegalStateException("NOT IMPLMENTED");
        }
        if (obj instanceof InstancedClazz) {
            return ((PrimTypeObj<Integer>)((InstancedClazz) obj).fields[(int)offset]).object;
        }
        if (obj instanceof PrimArray) {
            return (Byte.toUnsignedInt(((PrimArray<Byte>)obj).array[(int) offset+3])<<24)|
                    (Byte.toUnsignedInt(((PrimArray<Byte>)obj).array[(int) offset+2])<<16)|
                    (Byte.toUnsignedInt(((PrimArray<Byte>)obj).array[(int) offset+1])<<8)|
                    (Byte.toUnsignedInt(((PrimArray<Byte>)obj).array[(int) offset]));
        }
        throw new IllegalStateException();
    }

    @INativeMethod
    public static void putReferenceVolatile(InstancedClazz instance, OOP obj, long offset, OOP value) {
        if (obj == null) {
            throw new IllegalStateException("NOT IMPLMENTED");
        }
        if (obj instanceof AArray) {
            ((AArray)obj).oops[(int)offset] = value;
            return;
        }
        if (obj instanceof InstancedClazz) {
            ((InstancedClazz)obj).fields[(int)offset] = value;
            return;
        }
        throw new IllegalStateException();
    }

    @INativeMethod
    public static void ensureClassInitialized0(InstancedClazz instance, ClazzObj clazz) {
        clazz.clazz.ensureResolved();
    }

    @INativeMethod
    public static boolean shouldBeInitialized0(InstancedClazz instance, ClazzObj clazz) {
        return false;
    }


    @INativeMethod
    public static void putReference(InstancedClazz instance, OOP object, long offset, OOP item) {
        //TODO: take into account object clazz?
        FieldMeta fm = Meth0dHandleNatives.FIELD_LUT.get(offset);
        if (fm == null)
            throw new IllegalStateException();
        fm.accessor.set(object, item);
    }


    @INativeMethod
    public static OOP allocateInstance(InstancedClazz instance, ClazzObj object) {
        if (object.arrayDim != 0)
            throw new IllegalStateException();// TODO: implment arrays
        return object.clazz.instantiate();
    }


    //TODO: Dont actually do unsafe memory just alloc a bytearray, THIS WILL NOT WORK WITH LIBRARYRS THO
    @INativeMethod
    public static long allocateMemory0(InstancedClazz instance, long size) {
        return 1234;
    }

    @INativeMethod
    public static void copyMemory0(InstancedClazz instance, OOP objA, long offA, OOP objB, long offB, long size) {

    }

    @INativeMethod
    public static void putChar(InstancedClazz instance, OOP object, long offset, char c) {

    }


}
