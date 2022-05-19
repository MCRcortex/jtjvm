package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Decoded description
public class DDescription {
    public static final PrimType VOID = new PrimType(0,    "void");//"java/lang/Void");
    public static final PrimType INTEGER = new PrimType(1, "int");//"java/lang/Integer");
    public static final PrimType BYTE = new PrimType(1,    "byte");//"java/lang/Byte");
    public static final PrimType CHAR = new PrimType(1,    "char");//"java/lang/Character");
    public static final PrimType SHORT = new PrimType(1,   "short");//"java/lang/Short");
    public static final PrimType BOOLEAN = new PrimType(1, "boolean");//"java/lang/Boolean");
    public static final PrimType FLOAT = new PrimType(1,   "float");//"java/lang/Float");
    public static final PrimType LONG = new PrimType(2,    "long");//"java/lang/Long");
    public static final PrimType DOUBLE = new PrimType(2,  "double");//"java/lang/Double");
    public static class PrimType {
        public final int STACK_SIZE;
        public final String clazz;

        public PrimType(int stack_size, String clazz) {
            STACK_SIZE = stack_size;
            this.clazz = clazz;
        }

        @Override
        public String toString() {
            return "PrimType{" +
                    "STACK_SIZE=" + STACK_SIZE +
                    '}';
        }
    }
    public static class ClazzType extends PrimType {
        public final String clazz;

        public ClazzType(String clazz) {
            super(1, clazz);
            this.clazz = clazz;
        }

        @Override
        public String toString() {
            return "ClazzType{" +
                    "clazz='" + clazz + '\'' +
                    '}';
        }
    }

    public static class Type {
        public final int arrayDim;
        public final PrimType type;
        public Type(int arrayDim, PrimType type) {
            this.arrayDim = arrayDim;
            this.type = type;
        }

        public int size() {
            return arrayDim == 0?type.STACK_SIZE:1;
        }

        @Override
        public String toString() {
            return "Type{" +
                    "arrayDim=" + arrayDim +
                    ", type=" + type +
                    '}';
        }
    }

    public static class MethodType {
        public final Type[] args;
        public final Type retType;
        public int paramSize;

        public MethodType(Type[] args, Type retType) {
            this.args = args;
            this.retType = retType;
            for (Type t : args)
                paramSize += t.size();
        }

        @Override
        public String toString() {
            return "MethodType{" +
                    "args=" + Arrays.toString(args) +
                    ", retType=" + retType +
                    ", paramSize=" + paramSize +
                    '}';
        }
    }



    public static Type decodeType(String desc) {
        int arraydim = 0;
        for(; desc.charAt(arraydim) == '['; arraydim++);
        desc = desc.substring(arraydim);
        if (desc.charAt(0) == 'L')
            return new Type(arraydim, new ClazzType(desc.substring(1, desc.length()-1)));
        else {
            //TODO: lookup val -> PrimType
            PrimType p = switch (desc.charAt(0)) {
                case 'V' -> VOID;
                case 'B' -> BYTE;
                case 'C' -> CHAR;
                case 'I' -> INTEGER;
                case 'S' -> SHORT;
                case 'Z' -> BOOLEAN;
                case 'J' -> LONG;
                case 'F' -> FLOAT;
                case 'D' -> DOUBLE;
                default -> throw new IllegalArgumentException();
            };
            return new Type(arraydim, p);
        }
    }

    public static MethodType decodeMethod(String desc) {
        if (desc.charAt(0) != '(')
            throw new IllegalArgumentException();
        desc = desc.substring(1);
        List<Type> args = new ArrayList<>();
        while (desc.charAt(0) != ')') {
            Type argType;
            {
                int arraydim = 0;
                for (; desc.charAt(arraydim) == '['; arraydim++) ;
                desc = desc.substring(arraydim);
                if (desc.charAt(0) == 'L') {
                    argType = new Type(arraydim, new ClazzType(desc.substring(1, desc.indexOf(';'))));
                     desc = desc.substring(desc.indexOf(';')+ 1);
                } else {
                    //TODO: lookup val -> PrimType
                    PrimType p = switch (desc.charAt(0)) {
                        case 'V' -> VOID;
                        case 'B' -> BYTE;
                        case 'C' -> CHAR;
                        case 'I' -> INTEGER;
                        case 'S' -> SHORT;
                        case 'Z' -> BOOLEAN;
                        case 'F' -> FLOAT;
                        case 'D' -> DOUBLE;
                        case 'J' -> LONG;
                        default -> throw new IllegalArgumentException();
                    };
                    argType = new Type(arraydim, p);
                    desc = desc.substring(1);
                }
            }
            args.add(argType);
        }
        desc = desc.substring(1);
        Type ret = decodeType(desc);
        return new MethodType(args.toArray(new Type[0]), ret);
    }

}
