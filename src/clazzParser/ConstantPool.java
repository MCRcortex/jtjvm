package clazzParser;

import util.ClazzedNamedDescription;
import util.NamedDescription;

import java.io.DataInputStream;
import java.io.IOException;


public class ConstantPool {
    public static final int CONSTANT_Utf8 = 1;
    public static final int CONSTANT_Integer = 3;
    public static final int CONSTANT_Float = 4;
    public static final int CONSTANT_Long = 5;
    public static final int CONSTANT_Double = 6;
    public static final int CONSTANT_Class = 7;
    public static final int CONSTANT_String = 8;
    public static final int CONSTANT_Fieldref = 9;
    public static final int CONSTANT_Methodref = 10;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_NameAndType = 12;
    public static final int CONSTANT_MethodHandle = 15;
    public static final int CONSTANT_MethodType = 16;
    public static final int CONSTANT_Dynamic = 17;
    public static final int CONSTANT_InvokeDynamic = 18;
    public static final int CONSTANT_Module = 19;
    public static final int CONSTANT_Package = 20;
    public PEntry[] pool;
    public ConstantPool(DataInputStream data) throws IOException {
        int count = data.readUnsignedShort();
        pool = new PEntry[count];
        for (int i = 1; i < count; i++) {
            int tag = data.readUnsignedByte();
            switch (tag) {
                case CONSTANT_Class:
                    pool[i] = new C_ClazzInfo(data);
                    break;

                case CONSTANT_Double:
                    pool[i] = new C_Double(data);
                    i++;
                    break;

                case CONSTANT_Fieldref:
                    pool[i] = new C_FieldRef(data);
                    break;

                case CONSTANT_Float:
                    pool[i] = new C_Float(data);
                    break;

                case CONSTANT_Integer:
                    pool[i] = new C_Int(data);
                    break;

                case CONSTANT_InterfaceMethodref:
                    pool[i] = new C_InterfaceMethodRef(data);
                    break;

                case CONSTANT_InvokeDynamic:
                    pool[i] = new C_InvokeDynamic(data);
                    break;

                case CONSTANT_Dynamic:
                    System.out.println("AAA");
                    //pool[i] = new com.sun.tools.classfile.ConstantPool.CONSTANT_Dynamic_info(this, cr);
                    break;

                case CONSTANT_Long:
                    pool[i] = new C_Long(data);
                    i++;
                    break;

                case CONSTANT_MethodHandle:
                    pool[i] = new C_MethodHandle(data);
                    break;

                case CONSTANT_MethodType:
                    pool[i] = new C_MethodType(data);
                    break;

                case CONSTANT_Methodref:
                    pool[i] = new C_MethodRef(data);
                    break;

                case CONSTANT_Module:
                    pool[i] = new C_Module(data);
                    break;

                case CONSTANT_NameAndType:
                    pool[i] = new C_NameType(data);
                    break;

                case CONSTANT_Package:
                    pool[i] = new C_Package(data);
                    break;

                case CONSTANT_String:
                    pool[i] = new C_StringRef(data);
                    break;

                case CONSTANT_Utf8:
                    pool[i] = new C_UTF8(data);
                    break;

                default:
                    throw new IllegalStateException();
            }
        }
    }

    public String getUTF8(int index) {
        if (!(pool[index] instanceof C_UTF8))
            throw new IllegalArgumentException();
        return ((C_UTF8) pool[index]).str;
    }

    public String getClzAsStr(int index) {
        if (!(pool[index] instanceof C_ClazzInfo))
            throw new IllegalArgumentException();
        return getUTF8(((C_ClazzInfo) pool[index]).name_index);
    }

    public NamedDescription getND(int index) {
        if (!(pool[index] instanceof C_NameType))
            throw new IllegalArgumentException();
        return new NamedDescription(getUTF8(((C_NameType) pool[index]).name_index),
                getUTF8(((C_NameType) pool[index]).type_index));
    }

    public ClazzedNamedDescription getCND(int index) {
        if (!(pool[index] instanceof C_RefType))
            throw new IllegalArgumentException();
        return new ClazzedNamedDescription(getClzAsStr(((C_RefType) pool[index]).class_index),
                getND(((C_RefType) pool[index]).nametype_index));
    }

    public static class PEntry {

    }

    public class C_ClazzInfo extends PEntry {
        public final int name_index;
        public C_ClazzInfo(DataInputStream data) throws IOException {
            name_index = data.readUnsignedShort();
        }
    }

    public class C_UTF8 extends PEntry {
        public final String str;
        public C_UTF8(DataInputStream data) throws IOException {
            str = data.readUTF();
        }
    }

    public class C_RefType extends PEntry {
        public final int class_index;
        public final int nametype_index;
        public C_RefType(DataInputStream data) throws IOException {
            class_index = data.readUnsignedShort();
            nametype_index = data.readUnsignedShort();
        }
    }

    public class C_MethodRef extends C_RefType {
        public C_MethodRef(DataInputStream data) throws IOException {
            super(data);
        }
    }

    public class C_FieldRef extends C_RefType {
        public C_FieldRef(DataInputStream data) throws IOException {
            super(data);
        }
    }

    public class C_InterfaceMethodRef extends C_RefType {
        public C_InterfaceMethodRef(DataInputStream data) throws IOException {
            super(data);
        }
    }

    public class C_NameType extends PEntry {
        public final int name_index;
        public final int type_index;
        public C_NameType(DataInputStream data) throws IOException {
            name_index = data.readUnsignedShort();
            type_index = data.readUnsignedShort();
        }


        public NamedDescription get() {
            return null;
        }
    }

    public class C_StringRef extends PEntry {
        public final int ref;
        public C_StringRef(DataInputStream data) throws IOException {
            ref = data.readUnsignedShort();
        }
    }


    public class C_InvokeDynamic extends PEntry {
        public final int bootstrap_method_attr_index;
        public final int nametype_index;
        public C_InvokeDynamic(DataInputStream data) throws IOException {
            bootstrap_method_attr_index = data.readUnsignedShort();
            nametype_index = data.readUnsignedShort();
        }
    }

    public class C_MethodHandle extends PEntry {
        public final int kind;
        public final int ref_index;
        public C_MethodHandle(DataInputStream data) throws IOException {
            kind = data.readUnsignedByte();
            ref_index = data.readUnsignedShort();
        }
    }

    public class C_MethodType extends PEntry {
        public final int descriptor_index;
        public C_MethodType(DataInputStream data) throws IOException {
            descriptor_index = data.readUnsignedShort();
        }
    }

    public static class C_Int extends PEntry {
        public final int value;
        public C_Int(DataInputStream data) throws IOException {
            value = data.readInt();
        }
    }

    public static class C_Long extends PEntry {
        public final long value;
        public C_Long(DataInputStream data) throws IOException {
            value = data.readLong();
        }
    }

    public static class C_Double extends PEntry {
        public final double value;
        public C_Double(DataInputStream data) throws IOException {
            value = data.readDouble();
        }
    }

    public static class C_Float extends PEntry {
        public final float value;
        public C_Float(DataInputStream data) throws IOException {
            value = data.readFloat();
        }
    }

    public  class C_Module extends PEntry {
        public final int name_index;
        public C_Module(DataInputStream data) throws IOException {
            name_index = data.readUnsignedShort();
        }
    }

    public class C_Package extends PEntry {
        public final int name_index;
        public C_Package(DataInputStream data) throws IOException {
            name_index = data.readUnsignedShort();
        }
    }
}
