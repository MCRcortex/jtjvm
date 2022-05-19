package natives.methods.java.lang.invoke;

import clazz.ClazzMeta;
import clazz.FieldMeta;
import clazz.MethodMeta;
import clazz.instances.AArray;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.PrimArray;
import clazz.instances.special.ClazzObj;
import clazz.instances.special.PrimTypeObj;
import interpreter.BytecodeInterpreter;
import natives.methods.Adaptor;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;
import util.DDescription;
import util.NamedDescription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static util.Flags.ACC_PUBLIC;
import static util.Flags.ACC_STATIC;


@IClazzSpecifier(Clazz = "java/lang/invoke/MethodHandleNatives")
public class Meth0dHandleNatives {
    @INativeMethod(Static = true)
    public static void registerNatives() {

    }

    //Flag stuff is also in MethodHandleNatives line 116
    //lookupMode stuff is in MethodHandles$Lookup line 1445

    //TODO: ALSO ADD FIELD LOOKUP ASWELL!!!

    //static native MemberName resolve(MemberName self, Class<?> caller, int lookupMode, boolean speculativeResolve) throws LinkageError, ClassNotFoundException;
    @INativeMethod(Static = true)
    public static InstancedClazz resolve(InstancedClazz self, ClazzObj caller, int lookupMode, boolean speculativeResolve) {
        if (!self.clazz.getName().equals("java/lang/invoke/MemberName"))
            throw new IllegalStateException();

        ClazzMeta RMN = self.clazz.loader.getOrLoadClazz("java/lang/invoke/ResolvedMethodName");
        if (!RMN.fields.containsKey("injectedDataAField")) {
            if (RMN.resolved)
                throw new IllegalStateException("ResolvedMethodName class has been resolved before field injection");
            RMN.fields.put("injectedDataAField", new FieldMeta(RMN, 0, new NamedDescription("injectdDataAField", "")));
            RMN.ensureResolved();
        }


        ClazzObj clazz = (ClazzObj) self.clazz.fields.get("clazz").accessor.get(self);
        clazz.clazz.ensureResolved();
        String name = Adaptor.toString((InstancedClazz) self.clazz.fields.get("name").accessor.get(self));
        InstancedClazz type = (InstancedClazz) self.clazz.fields.get("type").accessor.get(self);
        int flags = ((PrimTypeObj<Integer>)self.clazz.fields.get("flags").accessor.get(self)).object;
        if (type.clazz.getName().equals("java/lang/invoke/MethodType")) {
            //TODO: IMPLEMENT NOTE: if it failes, return null
            //Find the method in clazz with name = name and disc = type
            List<MethodMeta> mths = clazz.clazz.methods.values().stream().filter(a -> a.disc.name.equals(name)).collect(Collectors.toList());
            if (mths.size() == 0)//TODO: REALLY DO NEED TO ADD TYPE RESOLVING
                //throw new IllegalStateException();//TODO: just return null
                return null;
            MethodMeta method = null;
            if (mths.size() == 1) {
                method = mths.get(0);
            } else {
                ClazzObj retType = (ClazzObj) type.fields[0];
                AArray paramTypes = (AArray) type.fields[1];
                //System.out.println(mtype);
                outer:
                for (MethodMeta mm : mths) {
                    DDescription.MethodType mt = DDescription.decodeMethod(mm.disc.description);
                    if (retType.getClazzName().equals(mt.retType.type.clazz) && retType.arrayDim == mt.retType.arrayDim && paramTypes.length() == mt.args.length) {
                        for (int i = 0; i < paramTypes.length(); i++) {
                            ClazzObj co = ((ClazzObj) paramTypes.oops[i]);
                            if (co.arrayDim != mt.args[i].arrayDim ||
                                    ((!(mt.args[i].type instanceof DDescription.ClazzType))||//FIXME: when fixed primative class fix this

                                            (!self.clazz.loader.getOrLoadClazz(mt.args[i].type.clazz).isSameOrSubClazz(co.clazz))
                                            //&&//FIXME: THIS IS HACK IDK WHY NEEDED

                                            //!co.clazz.name.equals(mt.args[i].type.clazz)
                                    ))//FIXME: this might break with primative types
                                continue outer;
                        }
                        if (method != null)
                            throw new IllegalStateException();
                        method = mm;
                    }
                }
            }
            if (method == null) {
                //throw new IllegalStateException();//TODO: just return null
                return null;
            }

            //NOTE: HACK TEST
            flags |= method.flags;
            //TODO: this if method
            flags |= 0x00010000;//MN_IS_METHOD
            //TODO: this if constructor
            //flags |= 0x00020000;//MN_IS_CONSTRUCTOR

            self.clazz.fields.get("flags").accessor.set(self, new PrimTypeObj<>(flags));
            InstancedClazz resolvedData = RMN.instantiate();
            resolvedData.fields[0] = new PrimTypeObj<>(method);
            self.clazz.fields.get("method").accessor.set(self, resolvedData);
            return self;
        } else {
            FieldMeta field = clazz.clazz.fields.get(name);
            if (field == null)
                throw new IllegalStateException();//TODO: just return null
                //return null;
            DDescription.Type t = DDescription.decodeType(field.disc.description);
            if (clazz.clazz.loader.getOrLoadClazz(t.type.clazz).getClazzObj(t.arrayDim) != type)
                throw new IllegalStateException();//TODO CHECK IF THIS IS RIGHT, might just need to check if subclass thing


            flags |= field.flags;

            self.clazz.fields.get("flags").accessor.set(self, new PrimTypeObj<>(flags));
            InstancedClazz resolvedData = RMN.instantiate();
            resolvedData.fields[0] = new PrimTypeObj<>(field);
            self.clazz.fields.get("method").accessor.set(self, resolvedData);
            return self;
        }
    }


    @INativeMethod(Static = true)
    public static OOP staticFieldBase(InstancedClazz instance) {
        //Extract field meta
        FieldMeta fm = ((PrimTypeObj<FieldMeta>)((InstancedClazz)instance.fields[4]).fields[0]).object;
        return fm.clazz.getClazzObj();
    }


    //FIXME: This is hack and will hog memory, this creates a lut for id -> fieldMeta so that e.g. unsafe.putReference can do its job
    public static final Map<Long, FieldMeta> FIELD_LUT = new HashMap<>();
    public static long CFID = 1;

    @INativeMethod(Static = true)
    public static long staticFieldOffset(InstancedClazz instance) {
        //Extract field meta
        FieldMeta fm = ((PrimTypeObj<FieldMeta>)((InstancedClazz)instance.fields[4]).fields[0]).object;
        if (FIELD_LUT.containsValue(fm)) {
            return FIELD_LUT.entrySet().stream().filter(a->a.getValue()==fm).findFirst().get().getKey();
        }
        long id = CFID++;
        FIELD_LUT.put(id, fm);
        return id;//TODO: Implment???
    }

    //FIXME: this is hacky implmentation
    @INativeMethod(Static = true)
    public static long objectFieldOffset(InstancedClazz instance) {
        //Extract field meta
        FieldMeta fm = ((PrimTypeObj<FieldMeta>)((InstancedClazz)instance.fields[4]).fields[0]).object;
        if (FIELD_LUT.containsValue(fm)) {
            return FIELD_LUT.entrySet().stream().filter(a->a.getValue()==fm).findFirst().get().getKey();
        }
        long id = CFID++;
        FIELD_LUT.put(id, fm);
        return id;//TODO: Implment???
    }

    //TODO: // fill in vmtarget, vmindex while we have m in hand:

    @INativeMethod(Static = true)
    public static void init(InstancedClazz self, OOP ref) {
        InstancedClazz method = (InstancedClazz) ref;
        //TODO: need to fill out the MemberName from the Method ref
        self.clazz.fields.get("clazz").accessor.set(self, method.clazz.fields.get("clazz").accessor.get(ref));

        String name = method.clazz.fields.containsKey("name")?Adaptor.toString((InstancedClazz) method.clazz.fields.get("name").accessor.get(ref)):null;
        if (method.clazz.getName().equals("java/lang/reflect/Constructor"))
            name = "<init>";
        self.clazz.fields.get("name").accessor.set(self, BytecodeInterpreter.createStr(self.clazz.loader, name));


        int flags = ((PrimTypeObj<Integer>)method.clazz.fields.get("modifiers").accessor.get(ref)).object;

        //TODO: THIS
        //TODO: this if method
        flags |= 0x00010000;//MN_IS_METHOD
        //TODO: this if constructor
        if (name.equals("<init>"))
            flags |= 0x00020000;//MN_IS_CONSTRUCTOR
        //TODO: SET THE REFKIND, hardcoded for testing

        flags |= ((name.equals("<init>")||name.equals("<clinit>"))?7:6)<<24;



        self.clazz.fields.get("flags").accessor.set(self, new PrimTypeObj<>(flags));


        ClazzMeta clazz = ((ClazzObj)method.fields[5]).clazz;
        String finalName = name;
        List<MethodMeta> methods = clazz.methods.values().stream().filter(a -> a.disc.name.equals(finalName)).collect(Collectors.toList());
        if (methods.size() != 1)
            throw new IllegalStateException();
        self.clazz.fields.get("type").accessor.set(self, BytecodeInterpreter.createMethodType(methods.get(0)));

        InstancedClazz resolvedData = clazz.loader.getLoadedClazz("java/lang/invoke/ResolvedMethodName").instantiate();
        resolvedData.fields[0] = new PrimTypeObj<>(methods.get(0));
        self.clazz.fields.get("method").accessor.set(self, resolvedData);
        //TODO: FILL OUT THE REST OF THIS
    }
}
