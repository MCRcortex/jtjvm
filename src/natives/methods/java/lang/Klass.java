package natives.methods.java.lang;

import clazz.ClazzMeta;
import clazz.LazyClazzRef;
import clazz.MethodMeta;
import clazz.instances.AArray;
import clazz.instances.Array;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.special.ClazzObj;
import clazz.instances.special.PrimTypeObj;
import interpreter.BytecodeInterpreter;
import interpreter.Slot;
import natives.BootClazzLoader;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;
import natives.methods.jdk.internal.reflect.Reflecti0n;
import util.DDescription;
import util.NamedDescription;

import java.util.*;
import java.util.stream.Collectors;

import static util.Flags.*;

@IClazzSpecifier(Clazz = "java/lang/Class")
public class Klass {
    @INativeMethod(Static = true)
    public static void registerNatives() {
        BootClazzLoader.ROOT.getOrLoadClazz("int");
        BootClazzLoader.ROOT.getOrLoadClazz("short");
        BootClazzLoader.ROOT.getOrLoadClazz("boolean");
        BootClazzLoader.ROOT.getOrLoadClazz("byte");
        BootClazzLoader.ROOT.getOrLoadClazz("char");
        BootClazzLoader.ROOT.getOrLoadClazz("long");
        BootClazzLoader.ROOT.getOrLoadClazz("double");
        BootClazzLoader.ROOT.getOrLoadClazz("float");
        BootClazzLoader.ROOT.getOrLoadClazz("void");
    }

    @INativeMethod(Static = true)
    public static boolean desiredAssertionStatus0(ClazzObj clz) {
        return false;
    }

    @INativeMethod(Static = true)
    public static ClazzObj getPrimitiveClass(String clz) {
        /*
        //FIXME: IMPLEMENT
        ClazzMeta clazz = BootClazzLoader.ROOT.getOrLoadClazz(switch (clz){
            case "int" ->     "java/lang/Integer";
            case "float" ->   "java/lang/Float";
            case "char" ->    "java/lang/Character";
            case "boolean" -> "java/lang/Boolean";
            case "double" ->  "java/lang/Double";
            case "long" ->    "java/lang/Long";
            case "byte" ->    "java/lang/Byte";
            case "short" ->   "java/lang/Short";
            case "void" ->   "java/lang/Void";
            default -> {throw new IllegalStateException();}
        });
        if (clazz == null)
            throw new IllegalStateException();
        ClazzObj o = new ClazzObj(clazz);//This creates a seperate object type
        primMap.put(clz, o);
         */
        return BootClazzLoader.ROOT.getOrLoadClazz(clz).getClazzObj();
    }

    @INativeMethod
    public static boolean isPrimitive(OOP obj_) {
        if (obj_ instanceof Array)//IDFK how this is possible
            return false;
        ClazzObj obj = (ClazzObj)(InstancedClazz) obj_;
        if (obj.arrayDim != 0)
            return false;
        return obj.clazz.isPrimative();
    }

    @INativeMethod
    public static String initClassName(ClazzObj obj) {
        //FIXME: IMPLEMENT
        //TODO: FOR primatives and shit needs to be `byte` also array types need to be like weird
        if (obj.arrayDim != 0)
            throw new IllegalStateException();
        /*
        String name = null;
        if (isPrimitive(obj)) {
            name = switch (obj.clazz.name) {
                case "java/lang/Integer" -> "int";
                case "java/lang/Float" -> "float";
                case "java/lang/Character" -> "char";
                case "java/lang/Boolean" -> "boolean";
                case "java/lang/Double" -> "double";
                case "java/lang/Long" -> "long";
                case "java/lang/Byte" -> "byte";
                case "java/lang/Short" -> "short";
                case "java/lang/Void" -> "void";
                default -> throw new IllegalStateException();
            };
        } else {
            name = obj.clazz.name;
        }*/
        String name = obj.getClazzName();
        name = name.replace("/", ".");
        obj.getSuperMeta().fields.get("name").accessor.set(obj, BytecodeInterpreter.createStr(obj.clazz.loader, name));
        return name;
    }

    @INativeMethod
    public static boolean isInterface(ClazzObj obj) {
        return (obj.clazz.flags&ACC_INTERFACE)!=0;
    }


    @INativeMethod
    public static int getModifiers(ClazzObj obj) {
        return obj.clazz.flags;
    }

    @INativeMethod
    public static boolean isArray(ClazzObj obj) {
        return (obj.arrayDim)!=0;
    }

    @INativeMethod
    public static boolean isAssignableFrom(ClazzObj obj, InstancedClazz other) {
        //TODO: Implement
        return true;
    }

    @INativeMethod(Static = true)
    public static ClazzObj forName0(String clazz, boolean initialize, InstancedClazz loader, ClazzObj caller) {
        if (loader == null) {//TODO: implment
            ClazzMeta m = BootClazzLoader.ROOT.getOrLoadClazz(clazz);
            if (initialize)
                m.ensureResolved();
            return m.getClazzObj();
        }
        //TODO: Needs to delegate to the clazzloaders loadClass if it is not loaded
        //TODO: NEED TO ALSO DO AN UPSEARCH OF THE loader clazzloader for already loaded clazz
        MethodMeta loaderMeth = ClazzMeta.locateMethod(new NamedDescription("loadClass", "(Ljava/lang/String;)Ljava/lang/Class;"),
                loader.clazz, null);
        ClazzObj found = (ClazzObj)((Slot.ObjectRefSlot)loaderMeth.executor.run(
                new Slot.ObjectRefSlot(loader),
                new Slot.ObjectRefSlot(BytecodeInterpreter.createStr(loader.clazz.loader, clazz)))).value;
        if (initialize)
            found.clazz.ensureResolved();
        return found;
    }

    @INativeMethod
    public static ClazzObj getSuperclass(ClazzObj obj) {
        if (obj.arrayDim != 0)
            throw new IllegalStateException();
        if (obj.clazz.super_ == null)
            return null;
        return obj.clazz.super_.get().getClazzObj();
    }


    @INativeMethod
    public static AArray getDeclaredMethods0(ClazzObj instance, boolean publicOnly) {
        if (instance.arrayDim != 0) throw new IllegalStateException();
        //TODO: IMPLMENT PROPERLY, this atm is just for testing
        ArrayList<InstancedClazz> methObjs = new ArrayList<>();
        for(MethodMeta m : instance.clazz.methods.values()) {
            if (publicOnly && (m.flags&ACC_PUBLIC) == 0) continue;
            InstancedClazz methObj = createMethodForMethodMeta(m);
            methObjs.add(methObj);
        }
        AArray a = new AArray(methObjs.size(), instance.clazz.loader.getLoadedClazz("java/lang/reflect/Method").getClazzObj(1));
        for (int i = 0; i<methObjs.size();i++) {
            a.oops[i] = methObjs.get(i);
        }
        return a;
    }

    public static InstancedClazz createMethodForMethodMeta(MethodMeta meta) {
        ClazzMeta mmeta = meta.clazz.loader.getOrLoadClazz("java/lang/reflect/Method");
        InstancedClazz mi = mmeta.instantiate();
        MethodMeta init = mmeta.methods.get(new NamedDescription("<init>", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;Ljava/lang/Class;[Ljava/lang/Class;IILjava/lang/String;[B[B[B)V"));
        DDescription.MethodType mt = DDescription.decodeMethod(meta.disc.description);
        AArray paramClazzes = new AArray(mt.args.length, meta.clazz.loader.getLoadedClazz("java/lang/Class").getClazzObj(1));
        for (int i = 0; i < paramClazzes.length(); i++) {
            paramClazzes.oops[i] = meta.clazz.loader.getOrLoadClazz(mt.args[i].type.clazz).getClazzObj(mt.args[i].arrayDim);
        }
        init.executor.run(
                new Slot.ObjectRefSlot(mi),
                new Slot.ObjectRefSlot(meta.clazz.getClazzObj()),
                new Slot.ObjectRefSlot(BytecodeInterpreter.createStr(meta.clazz.loader, meta.disc.name)),
                new Slot.ObjectRefSlot(paramClazzes),
                new Slot.ObjectRefSlot(meta.clazz.loader.getOrLoadClazz(mt.retType.type.clazz).getClazzObj(mt.retType.arrayDim)),
                new Slot.ObjectRefSlot(new AArray(0, meta.clazz.loader.getLoadedClazz("java/lang/Class").getClazzObj(1))),//TODO: IMPLEMENT
                new Slot.IntSlot(meta.flags),
                new Slot.IntSlot(-12345),//TODO: IMPLEMENT
                new Slot.ObjectRefSlot(BytecodeInterpreter.createStr(meta.clazz.loader, meta.disc.description)),
                new Slot.ObjectRefSlot(null),//TODO: IMPLEMENT
                new Slot.ObjectRefSlot(null),//TODO: IMPLEMENT
                new Slot.ObjectRefSlot(null)//TODO: IMPLEMENT
        );
        return mi;
    }

    @INativeMethod
    public static AArray getInterfaces0(ClazzObj instance) {
        LinkedHashSet<ClazzMeta> interfaces = new LinkedHashSet<>();
        Queue<ClazzMeta> queue = Arrays.stream(instance.clazz.interfaces).map(LazyClazzRef::get).collect(Collectors.toCollection(LinkedList::new));
        while (queue.size() != 0) {
            ClazzMeta m = queue.poll();
            interfaces.add(m);
            for (LazyClazzRef lcr : m.interfaces)
                queue.add(lcr.get());
        }
        ClazzMeta[] Interfaces = interfaces.toArray(queue.toArray(new ClazzMeta[0]));
        AArray out = new AArray(interfaces.size(), instance.clazz.loader.getLoadedClazz("java/lang/Class").getClazzObj(1));
        for (int i = 0; i < interfaces.size(); i++) {
            out.oops[i] = Interfaces[i].getClazzObj();
        }
        return out;
    }

    @INativeMethod
    public static InstancedClazz getConstantPool(ClazzObj obj) {
        //TODO: IMPLMENT
        return null;
    }

    @INativeMethod
    public static AArray getEnclosingMethod0(ClazzObj obj) {
        //TODO: IMPLMENT
        return null;
    }

    @INativeMethod
    public static ClazzObj getDeclaringClass0(ClazzObj obj) {
        //TODO: IMPLMENT
        return null;
    }

    @INativeMethod
    public static AArray getDeclaredConstructors0(ClazzObj obj, boolean publicOnly) {
        List<MethodMeta> constructors = obj.clazz.methods.values().stream()
                .filter(a->a.disc.name.equals("<init>") && ((!publicOnly) || ((a.flags&ACC_PUBLIC)!=0))).collect(Collectors.toList());
        ClazzMeta constructorClazz =obj.clazz.loader.getOrLoadClazz("java/lang/reflect/Constructor");
        constructorClazz.ensureResolved();
        AArray constructorsArray = new AArray(constructors.size(), constructorClazz.getClazzObj(1));
        for (int i = 0; i < constructors.size(); i++) {
            InstancedClazz ci = constructorClazz.instantiate();
            MethodMeta cmm = constructors.get(i);
            DDescription.MethodType cmmmt = DDescription.decodeMethod(cmm.disc.description);
            constructorsArray.oops[i] = ci;
            constructorClazz.fields.get("clazz").accessor.set(ci, obj);
            constructorClazz.fields.get("slot").accessor.set(ci, new PrimTypeObj<>(-1));
            constructorClazz.fields.get("modifiers").accessor.set(ci, new PrimTypeObj<>(cmm.flags));
            AArray parameterTypes = new AArray(cmmmt.args.length, obj.getSuperMeta().getClazzObj(1));
            //TODO: actually put in this
            for (int j = 0; j < cmmmt.args.length; j++) {
                parameterTypes.oops[j] = obj.clazz.loader.getOrLoadClazz(cmmmt.args[i].type.clazz).getClazzObj(cmmmt.args[i].arrayDim);
            }
            constructorClazz.fields.get("parameterTypes").accessor.set(ci, parameterTypes);

            //TODO: Need to add checkedExceptions, annotations, parameterAnnotations, signature

        }
        return constructorsArray;
    }




    @INativeMethod
    public static boolean isHidden(ClazzObj obj) {
        //TODO: Will need to implment
        return false;
    }

    @INativeMethod
    public static boolean isInstance(ClazzObj instance, OOP obj) {
        if (obj instanceof Array)
            throw new IllegalStateException();//TODO: IMPLMENT
        return ((InstancedClazz)obj).clazz.isSameOrSubClazz(instance.clazz);
    }

    @INativeMethod
    public static InstancedClazz getProtectionDomain0(ClazzObj instance) {
        return null;
    }
}