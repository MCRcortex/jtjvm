package clazz;

import clazz.field.InstancedFieldAccessor;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.special.ClazzLoaderObj;
import clazz.instances.special.ClazzObj;
import clazzParser.ClazzFile;
import clazzParser.ConstantPool;
import interpreter.Slot;
import interpreter.StackFrames;
import natives.BootClazzLoader;
import natives.methods.NativeMethodRegister;
import util.DDescription;
import util.NamedDescription;

import java.util.*;
import java.util.stream.Collectors;

import static util.Flags.*;


//TODO: need to add special case OOPs like class, classloader, UnsafeConstants
/**
 * Clazz meta is a clazz file that only loads other classes when needed, lazy evaluation
 */
public class ClazzMeta {
    //TODO: CREATE and store a ClazzObj thats specific to this object thus all instances of ClazzObj that refer to this meta are the exact same
    // would also need to handle array shit in there, but it would remove the hacks surrounding the fact that ClazzObjs might not be the same even tho there meta might be
    // DOOO THIS: Also need to have a special case for exact primative types created with like initPrimative or something so that when get name is called it returns int, byte etc not the boxed types



    public boolean resolved;
    public final ClazzLoaderObj loader;
    private String name;
    public int flags;
    public LazyClazzRef super_;
    public LazyClazzRef[] interfaces;
    public ConstantPool pool;
    public ClazzFile.Attributes attributes;

    public int instancedFieldIndexCount = 0;
    public FieldMeta[] fieldMetas;//Index -> field meta lookup

    //TODO: need to also take into account the "flags" when seaching for matching method
    public Map<NamedDescription, MethodMeta> methods = new LinkedHashMap<>();
    public Map<String, FieldMeta> fields = new LinkedHashMap<>();

    public ClazzMeta(ClazzLoaderObj loader, ClazzFile file) {
        this.loader = loader;
        pool = file.constant_pool;
        name = file.this_class;
        flags = file.access_flags;
        if (file.super_class != null) {
            super_ = new LazyClazzRef(file.super_class, loader);
        }
        interfaces = new LazyClazzRef[file.interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = new LazyClazzRef(file.interfaces[i], loader);
        }

        for (ClazzFile.Method m : file.methods) {
            methods.put(new NamedDescription(m.name, m.desc), new MethodMeta(this, m));
        }

        for (ClazzFile.Field f : file.fields) {
            FieldMeta fm = new FieldMeta(this, f);
            fields.put(f.name, fm);
        }

        attributes = file.attributes;
    }

    //TODO: move this elsewhere
    public void ensureResolved() {
        if (resolved)
            return;
        resolved = true;



        {
            if (super_ != null)
                super_.get().ensureResolved();

            for (LazyClazzRef inter : interfaces)
                inter.get().ensureResolved();
        }


        {

            if (super_ != null)
                instancedFieldIndexCount = super_.get().instancedFieldIndexCount;
            ArrayList<FieldMeta> instancedMetas = new ArrayList<>(fields.size());
            for (FieldMeta fm : fields.values()) {
                if (((fm.flags & ACC_STATIC) == 0)) {
                    ((InstancedFieldAccessor) fm.accessor).indexId = instancedFieldIndexCount;
                    instancedFieldIndexCount++;
                    instancedMetas.add(fm);
                }
            }

            if (super_ != null) {
                fieldMetas = new FieldMeta[instancedFieldIndexCount];
                System.arraycopy(super_.get().fieldMetas,0,fieldMetas,0, super_.get().instancedFieldIndexCount);
                System.arraycopy(instancedMetas.toArray(new FieldMeta[0]),0,fieldMetas,super_.get().instancedFieldIndexCount, instancedMetas.size());
            } else {
                fieldMetas = instancedMetas.toArray(new FieldMeta[0]);
            }
        }

        //FIXME: implment native registery properly and not just some static object
        //FIXME: THIS HORRIBLE HACKFEST MESS
        if (NativeMethodRegister.NMR.NATIVES.get(name) != null) {
            Map<String, INativeMethodExecutor> nativeMethods = NativeMethodRegister.NMR.NATIVES.get(name);
            for (Map.Entry<String, INativeMethodExecutor> nm : nativeMethods.entrySet()) {
                List<MethodMeta> methlist = methods.values().stream().filter(a -> a.disc.name.equals(nm.getKey()) && (a.flags&ACC_NATIVE)!=0).collect(Collectors.toList());
                if (methlist.size() == 0) {
                    System.err.println("NATIVE METHOD FAILED INJECTION !!!!!");
                    System.err.println(name+nm.getKey());
                    continue;
                }
                if (methlist.size() != 1)
                    throw new IllegalStateException();
                INativeMethodExecutor nativeExecutor = nm.getValue();
                MethodMeta methodMeta = methlist.get(0);
                methodMeta.executor = (args) -> {
                    StackFrames.push(methodMeta);
                    Slot result = nativeExecutor.run(methodMeta, args);
                    StackFrames.pop();
                    return result;
                };
            }
        }

        MethodMeta clinit = methods.get(new NamedDescription("<clinit>", "()V"));
        if (clinit != null) {
            System.out.println("START CLINIT: " + name);
            clinit.executor.run();
            System.out.println("DONE CLINIT: " + name);
        }
        System.out.println("CLAZZ RESOLVED: " + name);
    }


    public InstancedClazz instantiate() {
        if (this.isSameOrSubClazz(loader.getOrLoadClazz("java/lang/ClassLoader")))
            return new ClazzLoaderObj(this);
        return new InstancedClazz(this);
    }

    /*
    public MethodMeta getMethod(NamedDescription desc) {
        MethodMeta meta = methods.get(desc);
        if (meta != null)
            meta.ensureResolved();
        //TODO: need to make sure to call ensureResolve on the class, either here and/or in MethodMeta ensureResolved
        return meta;
    }

    public FieldMeta getField(NamedDescription desc) {
        FieldMeta meta = fields.get(desc);
        if (meta != null)
            meta.ensureResolved();
        return meta;
    }
     */



    //TODO: need to handel/check method return types

    //TODO: need to handle default functions in interfaces
    public static MethodMeta locateMethod(NamedDescription desc, ClazzMeta base, OOP oop) {
        //NOTE: Special casing all methods that have the @PolymorphicSignature annotation cause they are pain to deal with
        if (base.name.equals("java/lang/invoke/MethodHandle") && desc.name.equals("invoke")) {
            return base.methods.get(new NamedDescription("invoke", "([Ljava/lang/Object;)Ljava/lang/Object;"));
        }

        if (base.name.equals("java/lang/invoke/MethodHandle") && desc.name.equals("invokeBasic")) {
            return base.methods.get(new NamedDescription("invokeBasic", "([Ljava/lang/Object;)Ljava/lang/Object;"));
        }

        if (base.name.equals("java/lang/invoke/MethodHandle") && desc.name.equals("invokeExact")) {
            return base.methods.get(new NamedDescription("invokeExact", "([Ljava/lang/Object;)Ljava/lang/Object;"));
        }


        //SPECIAL CASE ClazzObj
        if (oop instanceof InstancedClazz || oop == null) {
            ClazzMeta clazz;
            if (oop == null)
                clazz = base;
            else
                clazz = ((InstancedClazz) oop).clazz;

            if (!clazz.isSameOrSubClazz(base))
                throw new IllegalStateException();

            /*
            if (base == obj.clazz) {
                return obj.clazz.methods.get(desc);
            }*/
            MethodMeta m = clazz.methods.get(desc);
            if (m != null)
                return m;
            LazyClazzRef ref = clazz.super_;
            while (ref != null) {
                ClazzMeta clz = ref.get();
                ref = clz.super_;
                m = clz.methods.get(desc);
                if (m == null)
                    continue;
                //TODO: test for clazz shit if it it doesnt have any modifiers
                if ((m.flags & ACC_STATIC)!=0)//|| (m.flags & ACC_PRIVATE)!=0
                    continue;
                return m;
            }
            return null;
        } else {
            //TODO: do actual special evaluation and dont just assume this
            return ClazzMeta.locateMethod(desc, base, null);
        }
    }

    public static MethodMeta locateInterfaceMethod(NamedDescription desc, ClazzMeta base, OOP oop) {
        //SPECIAL CASE ClazzObj
        if (oop instanceof InstancedClazz) {
            Queue<ClazzMeta> search = new LinkedList<>(List.of(((InstancedClazz) oop).clazz));
            while (search.size() != 0) {
                ClazzMeta meta = search.poll();
                MethodMeta m = meta.methods.get(desc);
                if (m != null)
                    return m;
                for (LazyClazzRef lcr : meta.interfaces) {
                    search.add(lcr.get());
                }
                if (meta.super_ != null) {
                    search.add(meta.super_.get());
                }
            }
            return null;
        } else {
            //TODO: do actual special evaluation and dont just assume this
            return base.methods.get(desc);
        }
    }

    public MethodMeta findMethodBounded(NamedDescription nd) {
        if (methods.containsKey(nd))
            return methods.get(nd);
        DDescription.MethodType mt = DDescription.decodeMethod(nd.description);
        ClazzMeta ret = loader.getOrLoadClazz(mt.retType.type.clazz);
        ClazzMeta[] args = new ClazzMeta[mt.args.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = loader.getOrLoadClazz(mt.args[i].type.clazz);
        }
        //TODO: account for arrays
        List<MethodMeta> metas = methods.values().stream().filter(a->a.disc.name.equals(nd.name)).filter(a-> {
            DDescription.MethodType mt2 = DDescription.decodeMethod(a.disc.description);
            if (mt2.args.length != mt.args.length)
                return false;

            ClazzMeta ret2 = loader.getOrLoadClazz(mt.retType.type.clazz);
            if (!ret2.isSameOrSubClazz(ret))
                return false;

            for (int i = 0; i < args.length; i++) {
                if (mt.args[i].arrayDim != mt2.args[i].arrayDim)
                    return false;
                ClazzMeta arg = loader.getOrLoadClazz(mt2.args[i].type.clazz);
                if (!arg.isSameOrSubClazz(args[i]))
                    return false;
            }
            return true;
        }).collect(Collectors.toList());
        if (metas.size() > 1)
            throw new IllegalStateException();
        return metas.isEmpty()?null:metas.get(0);
    }

    public boolean isSameOrSubClazz(ClazzMeta of) {
        if (this == of) {
            return true;
        }
        if (this.isPrimative() || of.isPrimative())
            return false;

        if (super_ != null) {
            if (super_.get().isSameOrSubClazz(of))
                return true;
        }
        for(LazyClazzRef i : interfaces) {
            if (i.get().isSameOrSubClazz(of))
                return true;
        }
        return false;
    }



    public FieldMeta locateField(String name) {
        ClazzMeta m = this;
        while (true) {
            FieldMeta f = m.fields.get(name);
            //TODO: Deal with private and static fields somehow?
            if (f != null)
                return f;
            if (m.super_ == null)
                return null;
            m = m.super_.get();
        }
    }



    private ClazzObj instance;
    private HashMap<Integer, ClazzObj> instances = new HashMap<>();
    public ClazzObj getClazzObj() {
        return getClazzObj(0);
    }
    public ClazzObj getClazzObj(int dimension) {
        if (instance == null && dimension == 0) {
            instance = new ClazzObj(this, 0);
        }
        if (dimension != 0)
            return instances.computeIfAbsent(dimension, a -> new ClazzObj(this, a));
        return instance;
    }



    //TODO: take into account array dim
    public String getName() {
        if (isPrimative()) {
            return switch (name) {
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
        } else
            return name;
    }

    public boolean isPrimative() {
        return BootClazzLoader.ROOT.PRIMATIVES.contains(this);
    }
}
