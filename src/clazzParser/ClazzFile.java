package clazzParser;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;

public class ClazzFile {
    public class Attributes {
        public final Attribute[] attributes;
        public final HashMap<String, Attribute> ats = new HashMap<>();
        public Attributes(DataInputStream data) throws Exception {
            int attrs_count = data.readUnsignedShort();
            attributes = new Attribute[attrs_count];
            for (int i = 0; i < attrs_count; i++) {
                int typeIndex = data.readUnsignedShort();
                if (!(constant_pool.pool[typeIndex] instanceof ConstantPool.C_UTF8))
                    throw new IllegalStateException();
                String type = ((ConstantPool.C_UTF8) constant_pool.pool[typeIndex]).str;

                byte[] attribData = new byte[data.readInt()];
                data.read(attribData);

                DataInputStream adata = new DataInputStream(new ByteArrayInputStream(attribData));
                //System.out.println(type);
                attributes[i] = switch (type) {
                    case "Code" -> new Attribute.Code(ClazzFile.this, adata);
                    case "BootstrapMethods" -> new Attribute.BootstrapMethods(ClazzFile.this, adata);
                    default -> null;
                };
                ats.put(type, attributes[i]);
            }
        }

        public Attribute.Code getCode() {
            return (Attribute.Code)ats.get("Code");
        }

        public Attribute.BootstrapMethods getBootstrap() {
            return (Attribute.BootstrapMethods)ats.get("BootstrapMethods");
        }
    }

    public class Field {
        public final int flags;
        public final String name;
        public final String desc;
        public final Attributes attributes;
        public Field(DataInputStream data) throws Exception {
            flags = data.readUnsignedShort();
            int name = data.readUnsignedShort();
            int desc = data.readUnsignedShort();
            this.name = ((ConstantPool.C_UTF8)constant_pool.pool[name]).str;
            this.desc = ((ConstantPool.C_UTF8)constant_pool.pool[desc]).str;
            attributes = new Attributes(data);
        }
    }

    public class Method {
        public final int flags;
        public final String name;
        public final String desc;
        public final Attributes attributes;
        public Method(DataInputStream data) throws Exception {
            flags = data.readUnsignedShort();
            int name = data.readUnsignedShort();
            int desc = data.readUnsignedShort();
            this.name = ((ConstantPool.C_UTF8)constant_pool.pool[name]).str;
            this.desc = ((ConstantPool.C_UTF8)constant_pool.pool[desc]).str;
            attributes = new Attributes(data);
            //if ((flags&0x0100) != 0) {
            //    System.out.println(this_class+" "+ this.name+" "+((flags&0x0008) != 0?"static":""));
            //}
        }
    }

    public final int magic;
    public final int minor_version;
    public final int major_version;
    public final ConstantPool constant_pool;
    public final int access_flags;
    public final String this_class;
    public final String super_class;
    public final String[] interfaces;
    public final Field[] fields;
    public final Method[] methods;
    public final Attributes attributes;

    public ClazzFile(byte[] from) {
        try {
            DataInputStream data = new DataInputStream(new ByteArrayInputStream(from));
            magic = data.readInt();
            minor_version = data.readUnsignedShort();
            major_version = data.readUnsignedShort();
            constant_pool = new ConstantPool(data);
            access_flags = data.readUnsignedShort();
            this_class = ((ConstantPool.C_UTF8) constant_pool.pool[((ConstantPool.C_ClazzInfo) constant_pool.pool[data.readUnsignedShort()]).name_index]).str;
            int super_c = data.readUnsignedShort();
            super_class = constant_pool.pool[super_c] == null ? null : ((ConstantPool.C_UTF8) constant_pool.pool[((ConstantPool.C_ClazzInfo) constant_pool.pool[super_c]).name_index]).str;


            int interfaces_count = data.readUnsignedShort();
            interfaces = new String[interfaces_count];
            for (int i = 0; i < interfaces_count; i++)
                interfaces[i] = ((ConstantPool.C_UTF8) constant_pool.pool[((ConstantPool.C_ClazzInfo) constant_pool.pool[data.readUnsignedShort()]).name_index]).str;

            int fields_count = data.readUnsignedShort();
            fields = new Field[fields_count];
            for (int i = 0; i < fields_count; i++)
                fields[i] = new Field(data);

            int methods_count = data.readUnsignedShort();
            methods = new Method[methods_count];
            for (int i = 0; i < methods_count; i++)
                methods[i] = new Method(data);

            attributes = new Attributes(data);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
