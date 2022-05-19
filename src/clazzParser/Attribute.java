package clazzParser;

import javax.xml.crypto.Data;
import java.io.DataInputStream;

public class Attribute {
    public static class ExceptionElement {
        public int start_pc;
        public int end_pc;
        public int handler_pc;
        public int catch_type;
        public ExceptionElement(DataInputStream data) throws Exception{
            start_pc = data.readUnsignedShort();
            end_pc = data.readUnsignedShort();
            handler_pc = data.readUnsignedShort();
            catch_type = data.readUnsignedShort();
        }
    }

    public static class Code extends Attribute {
        public int max_stack;
        public int max_locals;
        public byte[] code;
        public ExceptionElement[] exception_table;
        public ClazzFile.Attributes attributes;
        public Code(ClazzFile file, DataInputStream data) throws Exception {
            max_stack = data.readUnsignedShort();
            max_locals = data.readUnsignedShort();
            code = new byte[data.readInt()];
            data.read(code);
            exception_table = new ExceptionElement[data.readUnsignedShort()];
            for (int i = 0; i < exception_table.length; i++) {
                exception_table[i] = new ExceptionElement(data);
            }
            //Attributes
        }
    }

    public static class BootstrapMethod {
        public int bootstrap_method_ref;
        public int[] bootstrap_arguments;
        public BootstrapMethod(DataInputStream stream) throws Exception {
            bootstrap_method_ref = stream.readUnsignedShort();
            bootstrap_arguments = new int[stream.readUnsignedShort()];
            for (int i = 0; i < bootstrap_arguments.length; i++) {
                bootstrap_arguments[i] = stream.readUnsignedShort();
            }
        }
    }

    public static class BootstrapMethods extends Attribute {
        public BootstrapMethod[] methods;
        public BootstrapMethods(ClazzFile file, DataInputStream data) throws Exception {
            methods = new BootstrapMethod[data.readUnsignedShort()];
            for (int i = 0; i < methods.length; i++) {
                methods[i] = new BootstrapMethod(data);
            }
        }
    }
}
