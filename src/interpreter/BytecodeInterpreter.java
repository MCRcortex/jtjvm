package interpreter;

import clazz.*;
import clazz.instances.*;
import clazz.instances.special.ClazzLoaderObj;
import clazz.instances.special.ClazzObj;
import clazz.instances.special.PrimTypeObj;
import clazzParser.Attribute;
import clazzParser.ConstantPool;
import util.ClazzedNamedDescription;
import util.DDescription;
import util.NamedDescription;

import java.nio.charset.StandardCharsets;

import static interpreter.Bytecodes.*;
import static util.Flags.ACC_STATIC;


//TODO: NEED TO FIX == comparison on clazz objects, they need to compare the ClazzMeta not the clazzobj
public class BytecodeInterpreter {
    private static class LocalVar {
        Slot[] slots;
        public LocalVar(int max_locals) {
            slots = new Slot[max_locals];
        }

        public Slot.IntSlot getInt(int index) {
            return (Slot.IntSlot) slots[index];
        }

        public void put(int index, Slot value) {
            if (value.size != 1)
                throw new IllegalStateException("Slot size 2");
            slots[index] = value;
        }

        public void put(int index, int value) {
            put(index, new Slot.IntSlot(value));
        }

        public void put(int index, long value) {
            put2(index, new Slot.LongSlot(value));
        }

        public void put(int index, float value) {
            put(index, new Slot.FloatSlot(value));
        }

        public Slot.ObjectRefSlot getObjRef(int index) {
            return (Slot.ObjectRefSlot) slots[index];
        }

        public void put2(int index, Slot value) {
            if (value.size != 2)
                throw new IllegalStateException("Slot size ! 2");
            slots[index] = value;
            slots[index+1] = value;
        }

        public Slot.LongSlot getLong(int index) {
            if (slots[index] != slots[index+1])
                throw new IllegalStateException("Slots not equal");
            return (Slot.LongSlot)slots[index];
        }

        public Slot.FloatSlot getFloat(int index) {
            return (Slot.FloatSlot) slots[index];
        }

        public Slot.DoubleSlot getDouble(int index) {
            if (slots[index] != slots[index+1])
                throw new IllegalStateException("Slots not equal");
            return (Slot.DoubleSlot)slots[index];
        }
    }


    public static Slot runBytecode(MethodMeta method, Slot... slot_args) {
        BytecodeExecutor executor = (BytecodeExecutor) method.executor;
        Stack stack = new Stack(executor.max_stack);
        LocalVar locals = new LocalVar(executor.max_locals);
        for (int i = 0; i < slot_args.length; i++) {
            if (slot_args[i].size == 1) {
                locals.put(i, slot_args[i]);
            } else {
                locals.put2(i, slot_args[i]);
                i++;
            }
        }

        int pc = 0;
        while (pc!=executor.bytecode.length) {
            switch (Byte.toUnsignedInt(executor.bytecode[pc++])) {
                case _aconst_null -> {
                    stack.push(new Slot.ObjectRefSlot(null));
                }

                case _iconst_m1 -> {
                    stack.push(-1);
                }

                case _iconst_0 -> {
                    stack.push(0);
                }

                case _iconst_1 -> {
                    stack.push(1);
                }

                case _iconst_2 -> {
                    stack.push(2);
                }

                case _iconst_3 -> {
                    stack.push(3);
                }

                case _iconst_4 -> {
                    stack.push(4);
                }

                case _iconst_5 -> {
                    stack.push(5);
                }

                case _lconst_0 -> {
                    stack.push((long) 0);
                }

                case _lconst_1 -> {
                    stack.push((long) 1);
                }

                case _dconst_0 -> {
                    stack.push((double) 0);
                }

                case _dconst_1 -> {
                    stack.push((double) 1);
                }

                case _fconst_0 -> {
                    stack.push((float) 0);
                }

                case _fconst_1 -> {
                    stack.push((float) 1);
                }

                case _aload_0 -> {
                    stack.push(locals.getObjRef(0));
                }

                case _aload_1 -> {
                    stack.push(locals.getObjRef(1));
                }

                case _aload_2 -> {
                    stack.push(locals.getObjRef(2));
                }

                case _aload_3 -> {
                    stack.push(locals.getObjRef(3));
                }

                case _iload_0 -> {
                    stack.push(locals.getInt(0));
                }

                case _iload_1 -> {
                    stack.push(locals.getInt(1));
                }

                case _iload_2 -> {
                    stack.push(locals.getInt(2));
                }

                case _iload_3 -> {
                    stack.push(locals.getInt(3));
                }

                case _aload -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    //System.out.println("aload: "+ (value));
                    stack.push(locals.getObjRef(value));
                }

                case _lload -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    //System.out.println("aload: "+ (value));
                    stack.push2(locals.getLong(value));
                }

                case _aaload -> {
                    int index = stack.popInt().value;
                    AArray array = (AArray) stack.popObjRef().value;
                    stack.push(array.oops[index]);
                }

                case _caload -> {
                    int index = stack.popInt().value;
                    PrimArray<Character> array = (PrimArray<Character>) stack.popObjRef().value;
                    stack.push(array.array[index]);
                }

                case _iload -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    stack.push(locals.getInt(value));
                    //System.out.println("iload: "+ (value));
                }

                case _astore_0 -> {
                    locals.put(0, stack.popObjRef());
                }

                case _astore_1 -> {
                    locals.put(1, stack.popObjRef());
                }

                case _astore_2 -> {
                    locals.put(2, stack.popObjRef());
                }

                case _astore_3 -> {
                    locals.put(3, stack.popObjRef());
                }

                case _astore -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    //System.out.println("astore: "+ (value));
                    locals.put(value, stack.popObjRef());
                }

                case _aastore -> {
                    OOP value = stack.popObjRef().value;
                    int index = stack.popInt().value;
                    AArray array = (AArray)stack.popObjRef().value;
                    array.oops[index] = value;
                    //System.out.println("aload: "+ (value));
                }

                case _castore -> {
                    //FIXME: THIS IS HACK do proper stack char lookup
                    char value = (char)(int)stack.popInt().value;
                    int index = stack.popInt().value;
                    PrimArray<Character> array = (PrimArray<Character>)stack.popObjRef().value;
                    array.array[index] = value;
                    //System.out.println("aload: "+ (value));
                }

                case _bastore -> {
                    //FIXME: THIS IS HACK do proper stack char lookup
                    byte value = (byte)(int)stack.popInt().value;
                    int index = stack.popInt().value;
                    PrimArray<Byte> array = (PrimArray<Byte>)stack.popObjRef().value;
                    array.array[index] = value;
                    //System.out.println("aload: "+ (value));
                }

                case _istore -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    //System.out.println("istore: "+ (value));
                    locals.put(value, stack.popInt().value);
                }

                case _lstore -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    //System.out.println("istore: "+ (value));
                    locals.put(value, stack.popLong().value);
                }

                case _fstore -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    //System.out.println("istore: "+ (value));
                    locals.put(value, stack.popFloat().value);
                }

                case _fload -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    //System.out.println("istore: "+ (value));
                    stack.push(locals.getFloat(value).value);
                }

                case _fload_0 -> {
                    stack.push(locals.getFloat(0).value);
                }

                case _fload_1 -> {
                    stack.push(locals.getFloat(1).value);
                }

                case _fload_2 -> {
                    stack.push(locals.getFloat(2).value);
                }

                case _fload_3 -> {
                    stack.push(locals.getFloat(3).value);
                }


                case _istore_0 -> {
                    locals.put(0, stack.popInt());
                }

                case _istore_1 -> {
                    locals.put(1, stack.popInt());
                }

                case _istore_2 -> {
                    locals.put(2, stack.popInt());
                }

                case _istore_3 -> {
                    locals.put(3, stack.popInt());
                }

                case _lstore_0 -> {
                    locals.put2(0, stack.popLong());
                }

                case _lstore_1 -> {
                    locals.put2(1, stack.popLong());
                }

                case _lstore_2 -> {
                    locals.put2(2, stack.popLong());
                }

                case _lstore_3 -> {
                    locals.put2(3, stack.popLong());
                }

                case _lload_0 -> {
                    stack.push2(locals.getLong(0));
                }

                case _lload_1 -> {
                    stack.push2(locals.getLong(1));
                }

                case _lload_2 -> {
                    stack.push2(locals.getLong(2));
                }

                case _lload_3 -> {
                    stack.push2(locals.getLong(3));
                }

                case _dload_0 -> {
                    stack.push2(locals.getDouble(0));
                }

                case _dload_1 -> {
                    stack.push2(locals.getDouble(1));
                }

                case _dload_2 -> {
                    stack.push2(locals.getDouble(2));
                }

                case _dload_3 -> {
                    stack.push2(locals.getDouble(3));
                }

                case _iastore -> {
                    int value = stack.popInt().value;
                    int index = stack.popInt().value;
                    PrimArray<Integer> array = (PrimArray<Integer>) stack.popObjRef().value;
                    //System.out.println("iastore: "+array+"["+index+"] = "+value);
                    array.array[index] = value;
                    //throw new IllegalStateException();
                }

                case _lastore -> {
                    long value = stack.popLong().value;
                    int index = stack.popInt().value;
                    PrimArray<Long> array = (PrimArray<Long>) stack.popObjRef().value;
                    //System.out.println("iastore: "+array+"["+index+"] = "+value);
                    array.array[index] = value;
                    //throw new IllegalStateException();
                }

                case _laload -> {
                    int index = stack.popInt().value;
                    PrimArray<Long> array = (PrimArray<Long>) stack.popObjRef().value;
                    stack.push(array.array[index]);
                }

                case _iaload -> {
                    int index = stack.popInt().value;
                    PrimArray<Integer> array = (PrimArray<Integer>) stack.popObjRef().value;
                    //System.out.println("iaload: "+array+"["+index+"]");
                    stack.push(array.array[index]==null?0:array.array[index]);
                }

                case _baload -> {
                    int index = stack.popInt().value;
                    PrimArray<Byte> array = (PrimArray<Byte>) stack.popObjRef().value;
                    //System.out.println("baload: "+array+"["+index+"]");
                    if (array.type != DDescription.BYTE) {
                        throw new IllegalStateException();
                    }
                    if (array.array[index] == null) {
                        array.array[index] = 0;
                    }
                    stack.push(array.array[index]);
                }



                case _sipush -> {
                    int value = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("sipush: "+ (value));
                    stack.push(value);
                }

                case _bipush -> {
                    int value = executor.bytecode[pc++];
                    //System.out.println("bipush: "+ (value));
                    stack.push(value);
                }

                case _getfield -> {
                    OOP obj = stack.popObjRef().value;
                    if (obj == null) {
                        throw new IllegalStateException();
                    }
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ClazzedNamedDescription cnd = method.clazz.pool.getCND(value);
                    ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(cnd.clazz());
                    clazz.ensureResolved();
                    //TODO: ensure the type of the stack is the same as expected type
                    FieldMeta field = clazz.locateField(cnd.description().name);
                    loadField(field, obj, stack);
                    //System.out.println("getfield: "+cnd);
                }

                case _getstatic -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ClazzedNamedDescription cnd = method.clazz.pool.getCND(value);
                    ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(cnd.clazz());
                    clazz.ensureResolved();
                    //TODO: ensure the type of the stack is the same as expected type
                    FieldMeta field = clazz.locateField(cnd.description().name);
                    loadField(field, null, stack);
                    //System.out.println("getstatic: "+cnd);
                }

                case _putfield -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ClazzedNamedDescription cnd = method.clazz.pool.getCND(value);
                    ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(cnd.clazz());
                    clazz.ensureResolved();
                    //TODO: ensure the type of the stack is the same as expected type
                    FieldMeta field = clazz.locateField(cnd.description().name);
                    //FIXME: THIS IS A HACK, do it properly
                    OOP val = null;
                    if (cnd.description().description.equals("J")) {
                        long v = stack.popLong().value;
                        val = stack.popObjRef().value;
                        stack.push(v);
                    } else if (cnd.description().description.equals("D")) {
                        double v = stack.popDouble().value;
                        val = stack.popObjRef().value;
                        stack.push(v);
                    } else {
                        stack.swap();//NOTE: THIS IS A HACK
                        val = stack.popObjRef().value;
                    }
                    storeField(field, val, stack);
                    //System.out.println("putfield: "+cnd);
                }

                case _putstatic -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ClazzedNamedDescription cnd = method.clazz.pool.getCND(value);
                    ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(cnd.clazz());
                    clazz.ensureResolved();
                    //TODO: ensure the type of the stack is the same as expected type
                    storeField(clazz.fields.get(cnd.description().name), null, stack);
                    //System.out.println("putstatic: "+cnd);
                }

                //NOTE: if the return type is void, doesnt push anything onto stack
                case _invokestatic -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ClazzedNamedDescription cnd = method.clazz.pool.getCND(value);
                    ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(cnd.clazz());
                    clazz.ensureResolved();
                    MethodMeta method_ = null;
                    //FIXME: this is big hack to get polymorphic methods to work
                    if (cnd.clazz().equals("java/lang/invoke/MethodHandle") && cnd.description().name.equals("linkToSpecial")) {
                        method_ = clazz.methods.get(new NamedDescription(cnd.description().name, "([Ljava/lang/Object;)Ljava/lang/Object;"));
                    }
                    else {
                        method_ = clazz.findMethodBounded(cnd.description());
                        if (method_ == null) {
                            throw new IllegalStateException("Method null: "+cnd);
                        }
                        if (method_.executor == null) {
                            StackFrames.dumpStack();
                            throw new IllegalStateException("Executor null: "+cnd+((method_.flags&ACC_STATIC)==0?"":" static"));
                        }
                    }
                    DDescription.MethodType d = DDescription.decodeMethod(cnd.description().description);
                    Slot[] args = stack.popN(d.paramSize);
                    //System.out.println("invokestatic: "+cnd);
                    Slot result = method_.executor.run(args);
                    if (d.retType.size() == 1)
                        stack.push(result);
                    else if (d.retType.size() == 2)
                        stack.push2(result);
                }


                //TODO: fix alot of this as all the MethodMeta is wrong as its not in reference to the objec
                //TODO: if class is null need to throw NullPointerException in the method
                case _invokevirtual -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ClazzedNamedDescription cnd = method.clazz.pool.getCND(value);




                    //TODO: IMPLMENT
                    if (cnd.clazz().charAt(0)=='[' && cnd.description().name.equals("clone"))//TODO: INSTEAD of this, special case all array objects
                        continue;
                    ClazzMeta clazz=method.clazz.loader.getOrLoadClazz(cnd.clazz());
                    clazz.ensureResolved();
                    //System.out.println("invokevirtual: "+cnd);
                    DDescription.MethodType d = DDescription.decodeMethod(cnd.description().description);
                    Slot[] args = stack.popN(d.paramSize + 1);
                    if (((Slot.ObjectRefSlot)args[0]).value == null) {
                        StackFrames.dumpStack();
                        throw new IllegalStateException("data null: "+cnd);
                    }
                    MethodMeta method_ = ClazzMeta.locateMethod(cnd.description(), clazz, ((Slot.ObjectRefSlot)args[0]).value);
                    if (method_ == null) {
                        throw new IllegalStateException("Cannot find method: "+cnd);
                    }
                    if (method_.executor == null) {
                        StackFrames.dumpStack();
                        throw new IllegalStateException("Executor null: "+cnd+((method_.flags&ACC_STATIC)==0?"":" static"));
                    }

                    Slot result = method_.executor.run(args);
                    if (d.retType.size() == 1)
                        stack.push(result);
                    else if (d.retType.size() == 2)
                        stack.push2(result);
                }

                case _invokespecial -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ClazzedNamedDescription cnd = method.clazz.pool.getCND(value);
                    ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(cnd.clazz());
                    clazz.ensureResolved();
                    //System.out.println("invokespecial: "+cnd);
                    //FIXME: hack to fix descriptor type of generated clazzes idk why TODO FIX
                    cnd.description().description = cnd.description().description.replace(")Ljava/lang/Void;", ")V");

                    DDescription.MethodType d = DDescription.decodeMethod(cnd.description().description);
                    Slot[] args = stack.popN(d.paramSize + 1);
                    MethodMeta method_ = ClazzMeta.locateMethod(cnd.description(), clazz, null);
                    if (method_ == null) {
                        throw new IllegalStateException("Method null: " + cnd+ " in method " + method.disc);
                    }
                    if (method_.executor == null) {
                        throw new IllegalStateException("Executor null: "+cnd+((method_.flags&ACC_STATIC)==0?"":" static"));
                    }
                    Slot result = method_.executor.run(args);
                    if (d.retType.size() == 1)
                        stack.push(result);
                    else if (d.retType.size() == 2)
                        stack.push2(result);
                }

                case _invokedynamic -> {
                    pc = invokedynamic(method, executor, pc, stack, locals);
                }

                case _invokeinterface -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    int stack_count = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    if (executor.bytecode[pc++]!=0)
                        throw new IllegalStateException();
                    ClazzedNamedDescription cnd = method.clazz.pool.getCND(value);
                    ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(cnd.clazz());
                    clazz.ensureResolved();

                    //FIXME: hack to fix descriptor type of generated clazzes idk why TODO FIX
                    cnd.description().description = cnd.description().description.replace(")Ljava/lang/Boolean;", ")Z");


                    DDescription.MethodType d = DDescription.decodeMethod(cnd.description().description);
                    Slot[] args = stack.popN(stack_count/*d.paramSize + 1*/);
                    if (args[0] == null || ((Slot.ObjectRefSlot)args[0]).value == null) {
                        throw new IllegalStateException();
                    }
                    MethodMeta method_ = ClazzMeta.locateMethod(cnd.description(), clazz, ((Slot.ObjectRefSlot)args[0]).value);
                    if (method_ == null) {
                        method_ = ClazzMeta.locateInterfaceMethod(cnd.description(), clazz, ((Slot.ObjectRefSlot)args[0]).value);
                        if (method_ == null) {
                            throw new IllegalStateException();
                        }
                    }
                    if (method_.executor == null) {
                        throw new IllegalStateException("Executor null: "+cnd+((method_.flags&ACC_STATIC)==0?"":" static"));
                    }
                    Slot result = method_.executor.run(args);
                    if (d.retType.size() == 1)
                        stack.push(result);
                    else if (d.retType.size() == 2)
                        stack.push2(result);
                    //MethodMeta method_ = method.clazz.loader.getOrLoadClazz(cnd.clazz()).getMethod(cnd.description());
                    //System.out.println("invokeinterface: "+cnd+ ", "+stack_count);
                    //throw new IllegalStateException();
                }

                //FIXME: look, just going to assume everything is ok, i.e. no cast checking
                case _checkcast -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ClazzObj clazz = getOrLoadFunkyClazz(method.clazz.loader, method.clazz.pool.getClzAsStr(value));
                    //
                    //System.out.println("checkcast: "+clazz);
                    Slot.ObjectRefSlot ref = stack.popObjRef();
                    if (ref.value == null) {//trival
                        stack.push(ref);
                        continue;
                    }
                    if (clazz.arrayDim != 0)
                        clazz.clazz.ensureResolved();//TODO: check if this is right
                    /*
                    //TODO: CHECK CLASS SUPERS AND INTERFACES
                    if (((InstancedClazz)ref.value).clazz != clazz) {
                        //throw new IllegalStateException("TODO IMPLMENT inner throw of casting exception");
                        System.out.println("NOTE: not exact smae class in cast check, this is just cause its not implmented");
                    }

                     */
                    stack.push(ref);
                }

                case _instanceof -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    OOP clzI = (stack.popObjRef().value);
                    if (clzI == null) {
                        stack.push(false);
                        continue;
                    }
                    String cname = method.clazz.pool.getClzAsStr(value);
                    if (cname.charAt(0) == '[') {
                        DDescription.Type type = DDescription.decodeType(cname);
                        ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(type.type.clazz);
                        if (!(clzI instanceof Array))
                            stack.push(false);
                        else {
                            if (clzI instanceof AArray) {
                                if ( ((AArray) clzI).clazz.arrayDim == type.arrayDim && ((AArray) clzI).clazz.getClazzName().equals(type.type.clazz))
                                    stack.push(true);
                                else
                                    stack.push(false);
                            } else
                                throw new IllegalStateException();
                        }

                    } else {
                        ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(cname);
                        if (clzI instanceof Array)
                            stack.push(false);
                        else
                            stack.push(((InstancedClazz)clzI).clazz.isSameOrSubClazz(clazz));//TODO: FIXME: this doesnt check any inheratince at all... FIX THIS SHIT

                    }
                }

                case _arraylength -> {
                    Array array = (Array)stack.popObjRef().value;
                    //TODO: if array is null need to throw NullPointerException in the method
                    if (array == null) {
                        System.out.println("arraylength throw nullptr");
                        stack.push(0);
                    } else {
                        stack.push(array.length());
                    }
                }

                case _ldc2_w -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ldc2(method, stack, locals, value);
                }

                case _ldc_w -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ldc(method, stack, locals, value);
                }

                case _ldc -> {
                    int index = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ldc(method, stack, locals, index);
                }

                case _goto -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("goto: "+jump);
                    pc += jump - 3;
                }

                case _lcmp -> {
                    long b = stack.popLong().value;
                    long a = stack.popLong().value;
                    //TODO: CHECK THIS ORDER IS RIGHT
                    stack.push(Long.compare(a,b));
                }

                case _fcmpg -> {
                    float b = stack.popFloat().value;
                    float a = stack.popFloat().value;
                    if (Float.isNaN(a) || Float.isNaN(b)) {
                        stack.push(1);
                    } else
                        stack.push(Float.compare(a,b));
                }

                case _fcmpl -> {
                    float b = stack.popFloat().value;
                    float a = stack.popFloat().value;
                    if (Float.isNaN(a) || Float.isNaN(b)) {
                        stack.push(-1);
                    } else
                        stack.push(Float.compare(a,b));
                }

                case _ifnull -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("ifnull: "+jump);
                    if (stack.popObjRef().value == null) {
                        pc += jump - 3;
                    }
                }

                case _ifnonnull -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("ifnonnull: "+jump);
                    if (stack.popObjRef().value != null) {
                        pc += jump - 3;
                    }
                }

                case _ifne -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    if (stack.popInt().value != 0) {
                        pc += jump - 3;
                    }
                }

                case _ifeq -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    if (stack.popInt().value == 0) {
                        pc += jump - 3;
                    }
                }

                case _ifge -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    if (stack.popInt().value >= 0) {
                        pc += jump - 3;
                    }
                }

                case _ifgt -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    if (stack.popInt().value > 0) {
                        pc += jump - 3;
                    }
                }

                case _iflt -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    if (stack.popInt().value < 0) {
                        pc += jump - 3;
                    }
                }

                case _ifle -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    if (stack.popInt().value <= 0) {
                        pc += jump - 3;
                    }
                }

                case _if_icmpge -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("if_icmpge: "+jump);
                    //FIXME: hack fix
                    stack.swap();
                    if (((int)stack.popInt().value) >= ((int)stack.popInt().value)) {
                        pc += jump - 3;
                    }
                }

                case _if_icmpgt -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("if_icmpgt: "+jump);
                    stack.swap();
                    if (((int)stack.popInt().value) > ((int)stack.popInt().value)) {
                        pc += jump - 3;
                    }
                }

                case _if_icmpeq -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("if_icmpeq: "+jump);
                    if (((int)stack.popInt().value) == ((int)stack.popInt().value)) {
                        pc += jump - 3;
                    }
                }

                case _if_icmpne -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("if_icmpne: "+jump);
                    if (((int)stack.popInt().value) != ((int)stack.popInt().value)) {
                        pc += jump - 3;
                    }
                }

                case _if_icmple -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("if_icmple: "+jump);
                    //FIXME: hack fix
                    stack.swap();
                    if (((int)stack.popInt().value) <= ((int)stack.popInt().value)) {
                        pc += jump - 3;
                    }
                }

                case _if_icmplt -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("if_icmplt: "+jump);
                    //FIXME: hack fix
                    stack.swap();
                    if (((int)stack.popInt().value) < ((int)stack.popInt().value)) {
                        pc += jump - 3;
                    }
                }

                case _if_acmpeq -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("if_acmpeq: "+jump);
                    OOP a = stack.popObjRef().value;
                    OOP b = stack.popObjRef().value;
                    /*if (a instanceof ClazzObj || b instanceof ClazzObj) {
                        if (((ClazzObj)a).clazz == ((ClazzObj)b).clazz)
                            pc += jump - 3;
                    } else */if (a == b)
                        pc += jump - 3;
                }

                case _if_acmpne -> {
                    int jump = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                    //System.out.println("if_acmpne: "+jump);
                    OOP a = stack.popObjRef().value;
                    OOP b = stack.popObjRef().value;
                    /* if (a instanceof ClazzObj || b instanceof ClazzObj) {
                        if (((ClazzObj)a).clazz == ((ClazzObj)b).clazz)
                            continue;
                        else
                            pc += jump - 3;
                    } else */if (a != b)
                        pc += jump - 3;
                }

                case _new -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(method.clazz.pool.getClzAsStr(value));
                    clazz.ensureResolved();
                    //System.out.println("new: "+clazz.name);
                    stack.push(clazz.instantiate());
                }

                case _anewarray -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    int count = stack.popInt().value;
                    ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(method.clazz.pool.getClzAsStr(value));
                    //NOTE: DO NOT ENSURE RESOLVED!!!!

                    //System.out.println("anewarray: "+clazz.name+" size: "+count);
                    stack.push(new AArray(count, clazz.getClazzObj(1)));
                }

                case _newarray -> {
                    int ptype = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    int count = stack.popInt().value;
                    //System.out.println("newarray: "+ptype+" size: "+count);
                    //FIXME: get the correct type from ptype
                    stack.push(new PrimArray<>(count, new DDescription.PrimType[]{DDescription.BOOLEAN, DDescription.CHAR, DDescription.FLOAT, DDescription.DOUBLE, DDescription.BYTE, DDescription.SHORT,DDescription.INTEGER,DDescription.LONG}[ptype-4]));
                }

                case _dup -> {
                    Slot<?> s = stack.pop();
                    stack.push(s);
                    stack.push(s);
                }

                case _dup2 -> {
                    Slot<?> a = stack.pop();
                    if (a.size == 2) {
                        stack.pop();
                        stack.push2(a);
                        stack.push2(a);
                        continue;
                    }
                    Slot<?> b = stack.pop();
                    stack.push(b);
                    stack.push(a);
                    stack.push(b);
                    stack.push(a);
                }

                case _dup_x1 -> {
                    Slot<?> s1 = stack.pop();
                    Slot<?> s2 = stack.pop();
                    stack.push(s1);
                    stack.push(s2);
                    stack.push(s1);
                }

                case _dup_x2 -> {
                    Slot<?> s1 = stack.pop();
                    Slot<?> s2 = stack.pop();
                    Slot<?> s3 = stack.pop();
                    stack.push(s1);
                    stack.push(s3);
                    stack.push(s2);
                    stack.push(s1);
                }

                case _dup2_x1 -> {
                    Slot<?> s1 = stack.pop();
                    Slot<?> s2 = stack.pop();
                    Slot<?> s3 = stack.pop();
                    stack.pushRaw(s1);
                    stack.pushRaw(s2);
                    stack.pushRaw(s3);
                    stack.pushRaw(s1);
                    stack.pushRaw(s2);
                }

                case _dup2_x2 -> {
                    Slot<?> s1 = stack.pop2();
                    Slot<?> s2 = stack.pop2();
                    stack.push2(s1);
                    stack.push2(s2);
                    stack.push2(s1);
                }

                case _iinc -> {
                    int index = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    byte by = executor.bytecode[pc++];
                    //System.out.println("iinc: "+index + " " +by);
                    locals.put(index, locals.getInt(index).value + by);
                }

                case _athrow -> {
                    throw new IllegalStateException();
                }

                case _pop -> {
                    stack.pop();
                }

                case _pop2 -> {
                   stack.pop();
                   stack.pop();
                }

                //TODO: need to special case
                case _ret -> {
                    int index = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    System.out.println("ret: "+index);
                    throw new IllegalStateException();
                }


                case _iand -> {
                    stack.push(stack.popInt().value & stack.popInt().value);
                }

                case _ior -> {
                    stack.push(stack.popInt().value | stack.popInt().value);
                }

                case _lor -> {
                    stack.push(stack.popLong().value | stack.popLong().value);
                }

                case _land -> {
                    stack.push(stack.popLong().value & stack.popLong().value);
                }

                case _fadd -> {
                    stack.push(stack.popFloat().value + stack.popFloat().value);
                }

                case _isub -> {
                    //FIXME: HACK FIX
                    stack.swap();
                    stack.push(stack.popInt().value - stack.popInt().value);
                }

                case _iadd -> {
                    stack.push(stack.popInt().value + stack.popInt().value);
                }

                case _idiv -> {
                    //FIXME: Fix this hackfix
                    stack.swap();
                    stack.push(stack.popInt().value / stack.popInt().value);
                }

                case _ddiv -> {
                    double b = stack.popDouble().value;
                    double a = stack.popDouble().value;
                    stack.push(a / b);
                }

                case _ineg -> {
                    stack.push(- stack.popInt().value);
                }

                case _ixor -> {
                    stack.push(stack.popInt().value ^ stack.popInt().value);
                }

                case _dadd -> {
                    stack.push(stack.popDouble().value + stack.popDouble().value);
                }

                case _ladd -> {
                    stack.push(stack.popLong().value + stack.popLong().value);
                }

                case _lsub -> {
                    long b = stack.popLong().value;
                    long a = stack.popLong().value;
                    stack.push(a - b);
                }

                case _imul -> {
                    stack.push(stack.popInt().value * stack.popInt().value);
                }

                case _dmul -> {
                    stack.push(stack.popDouble().value * stack.popDouble().value);
                }

                case _fmul -> {
                    stack.push(stack.popFloat().value * stack.popFloat().value);
                }

                case _irem -> {
                    //FIXME: HACK FIX
                    stack.swap();
                    stack.push(stack.popInt().value % stack.popInt().value);
                }

                case _ishr -> {
                    //FIXME: HACK FIX
                    stack.swap();
                    stack.push(stack.popInt().value >> stack.popInt().value);
                }

                case _iushr -> {
                    //FIXME: HACK FIX
                    stack.swap();
                    stack.push(stack.popInt().value >>> stack.popInt().value);
                }

                case _ishl -> {
                    //FIXME: HACK FIX
                    stack.swap();
                    stack.push(stack.popInt().value << stack.popInt().value);
                }

                case _lmul -> {
                    stack.push(stack.popLong().value * stack.popLong().value);
                }

                case _lshl -> {
                    int by = stack.popInt().value;
                    long v = stack.popLong().value;
                    stack.push(v << by);
                }

                case _lshr -> {
                    int by = stack.popInt().value;
                    long v = stack.popLong().value;
                    stack.push(v >> by);
                }

                case _lushr -> {
                    int by = stack.popInt().value;
                    long v = stack.popLong().value;
                    stack.push(v >>> by);
                }

                case _i2c -> {
                    int val = stack.popInt().value;
                    //System.out.println("i2c: "+val);
                    stack.push((char)val);
                }

                case _i2d -> {
                    int val = stack.popInt().value;
                    stack.push((double) val);
                }

                case _i2b -> {
                    int val = stack.popInt().value;
                    //System.out.println("i2c: "+val);
                    stack.push((byte) val);
                }

                case _i2f -> {
                    int val = stack.popInt().value;
                    stack.push((float) val);
                }

                case _f2i -> {
                    float val = stack.popFloat().value;
                    stack.push((int) val);
                }

                case _d2i -> {
                    double val = stack.popDouble().value;
                    stack.push((int) val);
                }

                case _fdiv -> {
                    //FIXME: This is a hackfix
                    stack.swap();
                    stack.push(stack.popFloat().value/stack.popFloat().value);
                }

                case _l2i -> {
                    long val = stack.popLong().value;
                    //System.out.println("l2i: "+val);
                    //FIXME: implement properly
                    stack.push((int)val);
                }

                case _l2f -> {
                    long val = stack.popLong().value;
                    stack.push((float) val);
                }

                case _i2s -> {
                    int val = stack.popInt().value;
                    stack.push((short) val);
                }

                case _f2d -> {
                    float val = stack.popFloat().value;
                    stack.push((double) val);
                }

                case _d2l -> {
                    double val = stack.popDouble().value;
                    stack.push((long) val);
                }

                case _i2l -> {
                    int val = stack.popInt().value;
                    //System.out.println("i2l: "+val);
                    //FIXME: implement properly
                    stack.push((long)val);
                }

                case _tableswitch -> {
                    int basepc = pc -1;
                    int index = stack.popInt().value;
                    pc = (int)Math.ceil((pc)/4.0)*4;
                    int def = readSignedInt(executor, pc);
                    int low = readSignedInt(executor, pc+4);
                    int high = readSignedInt(executor, pc+8);
                    pc += 12;
                    if (high<low)
                        throw new IllegalStateException();
                    if (index<low || high<index) {
                        pc = def + basepc;
                        continue;
                    }
                    int jump = readSignedInt(executor, pc + (index - low)*4);
                    pc = jump + basepc;
                    //throw new IllegalStateException();
                }
                case _lookupswitch -> {
                    int basepc = pc -1;
                    int key = stack.popInt().value;
                    pc = (int)Math.ceil((pc)/4.0)*4;
                    int defaultOffset = readSignedInt(executor, pc);
                    int npairs = readSignedInt(executor, pc+4);
                    //pc+=8;
                    int skip = defaultOffset;
                    while (--npairs >= 0) {
                        pc += 8;
                        //System.out.println(Integer.toBinaryString(readSignedInt(executor, pc)));
                        if (key == readSignedInt(executor, pc)) {
                            skip = readSignedInt(executor, pc+4);
                            break;
                        }
                    }
                    pc = basepc + skip;
                    /*
                      CASE(_lookupswitch): {
                                jint* lpc  = (jint*)VMalignWordUp(pc+1);
                                int32_t  key  = STACK_INT(-1);
                                int32_t  skip = Bytes::get_Java_u4((address) lpc);
                                int32_t  npairs = Bytes::get_Java_u4((address) &lpc[1]);
                                while (--npairs >= 0) {
                                    lpc += 2;
                                    if (key == (int32_t)Bytes::get_Java_u4((address)lpc)) {
                                        skip = Bytes::get_Java_u4((address)&lpc[1]);
                                        break;
                                    }
                                }
                                address branch_pc = pc;
                                UPDATE_PC_AND_TOS(skip, -1);
                                DO_BACKEDGE_CHECKS(skip, branch_pc);
                                CONTINUE;
                            }
                     */
                }

                case _multianewarray -> {
                    int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
                    int dimcount = Byte.toUnsignedInt(executor.bytecode[pc++]);
                    DDescription.Type type = DDescription.decodeType(method.clazz.pool.getClzAsStr(value));
                    ClazzMeta clazz = method.clazz.loader.getOrLoadClazz(type.type.clazz);
                    int[] dims = new int[dimcount];
                    for (int i = 0; i < dimcount; i++)
                        dims[dimcount-i-1] = stack.popInt().value;
                    stack.push(createArray(0, dims, clazz, type));
                }

                case _areturn -> {return stack.popObjRef();}//TODO: need to do return value
                case _return  -> {return null;}//TODO: need to do return value
                case _lreturn -> {return stack.popLong();}//TODO: need to do return value
                case _ireturn -> {return stack.popInt();}//TODO: need to do return value
                case _freturn -> {return stack.popFloat();}//TODO: need to do return value
                case _dreturn -> {return stack.popDouble();}//TODO: need to do return value

                case _monitorenter -> {stack.popObjRef().value.monitor.lock();}
                case _monitorexit -> {stack.popObjRef().value.monitor.unlock();}

                case _wide -> {
                    switch (Byte.toUnsignedInt(executor.bytecode[pc++])) {
                        case _iinc -> {
                            int index = (Byte.toUnsignedInt(executor.bytecode[pc++])<<8) | (Byte.toUnsignedInt(executor.bytecode[pc++]));
                            int by = (short)(Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]));
                            locals.put(index, locals.getInt(index).value + by);
                        }
                        default -> {throw new IllegalStateException(Integer.toHexString(Byte.toUnsignedInt(executor.bytecode[pc-1])));}
                    }
                }

                default -> System.err.println(Integer.toHexString(Byte.toUnsignedInt(executor.bytecode[pc-1])));
            }
        }
        return null;
    }



    private static void ldc(MethodMeta method, Stack stack, LocalVar locals, int index) {
        ConstantPool.PEntry entry = method.clazz.pool.pool[index];
        if (entry instanceof ConstantPool.C_StringRef) {
            String value = method.clazz.pool.getUTF8(((ConstantPool.C_StringRef) entry).ref);
            stack.push(new Slot.ObjectRefSlot(createStr(method.clazz.loader, value)));
        } else if (entry instanceof ConstantPool.C_ClazzInfo) {
            String c = method.clazz.pool.getClzAsStr(index);
            ClazzObj obj = getOrLoadFunkyClazz(method.clazz.loader, c);
            obj.clazz.ensureResolved();//TODO: CHECK IF THIS IS CORRECT
            stack.push(obj);
        } else if (entry instanceof ConstantPool.C_Int) {
            stack.push(((ConstantPool.C_Int) entry).value);
        } else if (entry instanceof ConstantPool.C_Float) {
            stack.push(((ConstantPool.C_Float) entry).value);
        } else {
            throw new IllegalStateException("Unknown type: " + entry);
        }
    }

    public static InstancedClazz createStr(ClazzLoaderObj loader, String string) {
        ClazzMeta str = loader.getOrLoadClazz("java/lang/String");
        InstancedClazz strI = str.instantiate();
        //System.out.println("String load: "+string);
        FieldMeta val = str.fields.get("value");
        byte[] b = string.getBytes(StandardCharsets.UTF_8);
        PrimArray<Byte> strArray = new PrimArray<Byte>(b.length, DDescription.BYTE);
        for (int i = 0; i < string.getBytes().length; i++) {
            strArray.array[i] = string.getBytes()[i];
        }
        val.accessor.set(strI, strArray);
        FieldMeta coder = str.fields.get("coder");
        coder.accessor.set(strI, new PrimTypeObj((byte)(string.equals("\uFFFD")?1:0)));
        return strI;
    }

    private static ClazzObj getOrLoadFunkyClazz(ClazzLoaderObj loader, String clazz) {
        if (clazz.charAt(0) == '[') {
            DDescription.Type clazzType = DDescription.decodeType(clazz);
            return loader.getOrLoadClazz(clazzType.type.clazz).getClazzObj(clazzType.arrayDim);
        } else {
            return loader.getOrLoadClazz(clazz).getClazzObj();
        }
    }


    private static void ldc2(MethodMeta method, Stack stack, LocalVar locals, int index) {
        ConstantPool.PEntry entry = method.clazz.pool.pool[index];
        if (entry instanceof ConstantPool.C_Long) {
            stack.push(((ConstantPool.C_Long) entry).value);
        } else if (entry instanceof ConstantPool.C_Double) {
            stack.push(((ConstantPool.C_Double) entry).value);
        } else {
            throw new IllegalStateException("Illegal type: " + entry);
        }
    }


    private static void storeField(FieldMeta field, OOP clazz, Stack stack) {
        DDescription.Type type = DDescription.decodeType(field.disc.description);
        if (type.arrayDim > 0) {
            Slot val = stack.pop();
            if (val == Slot.NULL) {
                field.accessor.set(clazz, null);
            } else {
                field.accessor.set(clazz, (Array)((Slot.ObjectRefSlot)val).value);
            }
        } else {
            if (type.type instanceof DDescription.ClazzType) {
                Slot val = stack.pop();
                if (val == Slot.NULL) {
                    field.accessor.set(clazz, null);
                } else {
                    field.accessor.set(clazz, (OOP) ((Slot.ObjectRefSlot)val).value);
                }
            } else {
                if (type.type == DDescription.INTEGER) {
                    field.accessor.set(clazz, new PrimTypeObj(stack.popInt().value));
                } else if (type.type == DDescription.BOOLEAN) {
                    Slot.IntSlot val = stack.popInt();
                    if (!(val.value == 0 || val.value == 1))
                        throw new IllegalStateException("Boolean not 1 or 0");
                    field.accessor.set(clazz, new PrimTypeObj(val.value == 1));
                } else if (type.type == DDescription.LONG) {
                    field.accessor.set(clazz, new PrimTypeObj((stack.popLong()).value));
                } else if (type.type == DDescription.BYTE) {
                    //TODO: DO THIS PROPERLY
                    Slot.IntSlot val = stack.popInt();
                    field.accessor.set(clazz, new PrimTypeObj((byte)(int)val.value));
                } else if (type.type == DDescription.DOUBLE) {
                    field.accessor.set(clazz, new PrimTypeObj((double)stack.popDouble().value));
                } else if (type.type == DDescription.FLOAT) {
                    field.accessor.set(clazz, new PrimTypeObj((float)stack.popFloat().value));
                } else if (type.type == DDescription.CHAR) {
                    field.accessor.set(clazz, new PrimTypeObj((char)(int)stack.popInt().value));
                } else if (type.type == DDescription.SHORT) {
                    field.accessor.set(clazz, new PrimTypeObj((short)(int)stack.popInt().value));
                } else {
                    throw new IllegalStateException("Not implemented");
                }
            }
        }
    }

    private static void loadField(FieldMeta field, OOP clazz, Stack stack) {
        DDescription.Type type = DDescription.decodeType(field.disc.description);
        if (type.arrayDim > 0) {
            stack.push((Array)field.accessor.get(clazz));
        } else {
            if (type.type instanceof DDescription.ClazzType) {
                stack.push(field.accessor.get(clazz));
            } else {
                if (type.type == DDescription.INTEGER) {
                    OOP val = field.accessor.get(clazz);
                    if (!(val instanceof PrimTypeObj && ((PrimTypeObj) val).object instanceof Integer))
                        throw new ClassCastException("Incorrect stack type");
                    stack.push((int)((PrimTypeObj) val).object);
                } else if (type.type == DDescription.BOOLEAN) {
                    OOP val = field.accessor.get(clazz);
                    if (!(val instanceof PrimTypeObj && ((PrimTypeObj) val).object instanceof Boolean))
                        throw new ClassCastException("Incorrect stack type");
                    stack.push((boolean)((PrimTypeObj) val).object);
                } else if (type.type == DDescription.BYTE) {
                    OOP val = field.accessor.get(clazz);
                    if (!(val instanceof PrimTypeObj && ((PrimTypeObj) val).object instanceof Byte))
                        throw new ClassCastException("Incorrect stack type");
                    stack.push((Byte)((PrimTypeObj) val).object);
                } else if (type.type == DDescription.CHAR) {
                    OOP val = field.accessor.get(clazz);
                    if (!(val instanceof PrimTypeObj && ((PrimTypeObj) val).object instanceof Character))
                        throw new ClassCastException("Incorrect stack type");
                    stack.push((Character)((PrimTypeObj) val).object);
                } else if (type.type == DDescription.LONG) {
                    OOP val = field.accessor.get(clazz);
                    if (!(val instanceof PrimTypeObj && ((PrimTypeObj) val).object instanceof Long))
                        throw new ClassCastException("Incorrect stack type");
                    stack.push((Long)((PrimTypeObj) val).object);
                } else if (type.type == DDescription.FLOAT) {
                    OOP val = field.accessor.get(clazz);
                    if (!(val instanceof PrimTypeObj && ((PrimTypeObj) val).object instanceof Float))
                        throw new ClassCastException("Incorrect stack type");
                    stack.push((float)((PrimTypeObj) val).object);
                } else if (type.type == DDescription.SHORT) {
                    OOP val = field.accessor.get(clazz);
                    if (!(val instanceof PrimTypeObj && ((PrimTypeObj) val).object instanceof Short))
                        throw new ClassCastException("Incorrect stack type");
                    stack.push((short)((PrimTypeObj) val).object);
                } else {
                    throw new IllegalStateException("Not implemented: "+ field.disc.description);
                }
            }
        }
    }


    private static int readSignedInt(BytecodeExecutor executor, int index) {
        return (Byte.toUnsignedInt(executor.bytecode[index])<<24)|(Byte.toUnsignedInt(executor.bytecode[index+1])<<16)|(Byte.toUnsignedInt(executor.bytecode[index+2])<<8)|Byte.toUnsignedInt(executor.bytecode[index+3]);
    }




    private static int invokedynamic(MethodMeta method, BytecodeExecutor executor, int pc, Stack stack, LocalVar locals) {

        int value = Byte.toUnsignedInt(executor.bytecode[pc++]) << 8 | Byte.toUnsignedInt(executor.bytecode[pc++]);
        if (!(executor.bytecode[pc++]==0 && executor.bytecode[pc++]==0))
            throw new IllegalStateException();
        ConstantPool.C_InvokeDynamic id = (ConstantPool.C_InvokeDynamic) method.clazz.pool.pool[value];
        Attribute.BootstrapMethod bm = method.clazz.attributes.getBootstrap().methods[id.bootstrap_method_attr_index];
        NamedDescription m = method.clazz.pool.getND(id.nametype_index);
        DDescription.MethodType mt = DDescription.decodeMethod(m.description);


        ClazzedNamedDescription bootCND = method.clazz.pool.getCND(((ConstantPool.C_MethodHandle) method.clazz.pool.pool[bm.bootstrap_method_ref]).ref_index);
        ClazzMeta bootClazz = method.clazz.loader.getOrLoadClazz(bootCND.clazz());
        bootClazz.ensureResolved();
        MethodMeta bootMethod = bootClazz.methods.get(bootCND.description());
        int bootKind = ((ConstantPool.C_MethodHandle) method.clazz.pool.pool[bm.bootstrap_method_ref]).kind;


        InstancedClazz methodTypeInstance = createMethodType(bootMethod);//TODO: replace this with a new method that delegates to findMethodHandleType instead
        ClazzMeta mhn = method.clazz.loader.getOrLoadClazz("java/lang/invoke/MethodHandleNatives");
        mhn.ensureResolved();
        MethodMeta linkMethodHandleConstant = mhn.methods.get(new NamedDescription("linkMethodHandleConstant", "(Ljava/lang/Class;ILjava/lang/Class;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;"));
        InstancedClazz boostrapMethodHandle = (InstancedClazz) linkMethodHandleConstant.executor.run(new Slot.ObjectRefSlot(method.clazz.getClazzObj()),
                new Slot.IntSlot(bootKind),
                new Slot.ObjectRefSlot(bootClazz.getClazzObj()),
                new Slot.ObjectRefSlot(createStr(method.clazz.loader, bootMethod.disc.name)),
                new Slot.ObjectRefSlot(methodTypeInstance)).value;

        MethodMeta findMethodHandleType = mhn.methods.get(new NamedDescription("findMethodHandleType", "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;"));

        //Need to generate a methodtype for mt i.e. mt
        //Any time a method type needs to be found use findMethodHandle this includes computing the args for the invoke dynamic instruction
        Slot[] fmhtCall = new Slot[2];
        //put ret type
        fmhtCall[0] = new Slot.ObjectRefSlot(method.clazz.loader.getOrLoadClazz(mt.retType.type.clazz).getClazzObj());
        //put arg types
        AArray fmht2 = new AArray(mt.args.length, method.clazz.loader.getLoadedClazz("java/lang/Class").getClazzObj(1));
        for (int i = 0; i < mt.args.length; i++) {
            fmht2.oops[i] = method.clazz.loader.getOrLoadClazz(mt.args[i].type.clazz).getClazzObj(mt.args[i].arrayDim);
        }
        fmhtCall[1] = new Slot.ObjectRefSlot(fmht2);
        InstancedClazz mtti = (InstancedClazz)((Slot.ObjectRefSlot)findMethodHandleType.executor.run(fmhtCall)).value;
        System.out.println(mtti);


        AArray staticArgs = new AArray(bm.bootstrap_arguments.length, method.clazz.loader.getLoadedClazz("java/lang/Object").getClazzObj(1));
        for (int i = 0; i < bm.bootstrap_arguments.length; i++ ) {
            ConstantPool.PEntry entry = method.clazz.pool.pool[bm.bootstrap_arguments[i]];

            if (entry instanceof ConstantPool.C_MethodType) {
                DDescription.MethodType disc = DDescription.decodeMethod(method.clazz.pool.getUTF8(((ConstantPool.C_MethodType) entry).descriptor_index));

                //Use findMethodHandleType
                fmhtCall[0] = new Slot.ObjectRefSlot(method.clazz.loader.getOrLoadClazz(disc.retType.type.clazz).getClazzObj(disc.retType.arrayDim));
                fmht2 = new AArray(disc.args.length, method.clazz.loader.getLoadedClazz("java/lang/Class").getClazzObj(1));
                for (int j = 0; j < disc.args.length; j++) {
                    fmht2.oops[j] = method.clazz.loader.getOrLoadClazz(disc.args[j].type.clazz).getClazzObj(disc.args[j].arrayDim);
                }
                fmhtCall[1] = new Slot.ObjectRefSlot(fmht2);
                InstancedClazz methodTypeArg = (InstancedClazz)((Slot.ObjectRefSlot)findMethodHandleType.executor.run(fmhtCall)).value;
                staticArgs.oops[i] = methodTypeArg;
                //System.out.println(disc);
            } else if (entry instanceof ConstantPool.C_MethodHandle) {
                ClazzedNamedDescription cnd = method.clazz.pool.getCND(((ConstantPool.C_MethodHandle) entry).ref_index);
                DDescription.MethodType methodDisc = DDescription.decodeMethod(cnd.description().description);
                int ekind = ((ConstantPool.C_MethodHandle) entry).kind;
                //Use linkMethodHandleConstant
                InstancedClazz methodHandleArg = (InstancedClazz) linkMethodHandleConstant.executor.run(
                        new Slot.ObjectRefSlot(method.clazz.getClazzObj()),
                        new Slot.IntSlot(ekind),
                        new Slot.ObjectRefSlot(method.clazz.loader.getOrLoadClazz(cnd.clazz()).getClazzObj()),
                        new Slot.ObjectRefSlot(createStr(method.clazz.loader, cnd.description().name)),
                        new Slot.ObjectRefSlot(createMethodType(method.clazz.loader.getLoadedClazz(cnd.clazz()).methods.get(cnd.description())))//FIXME: make a new thing that calls findMethodHandleType
                ).value;
                //System.out.println(entry);
                staticArgs.oops[i] = methodHandleArg;
            } else
                throw new IllegalStateException();

        }


        /*
        findMethodHandleType.executor.run(new Slot.ObjectRefSlot(method.clazz.getClazzObj()),
                new Slot.IntSlot(bootKind),
                new Slot.ObjectRefSlot(bootClazz.getClazzObj()),
                new Slot.ObjectRefSlot(createStr(method.clazz.loader, bootMethod.disc.name)),
                new Slot.ObjectRefSlot(methodTypeInstance));*/


        //method.clazz.pool.getND(((ConstantPool.C_MethodRef) method.clazz.pool.pool[((ConstantPool.C_MethodHandle) method.clazz.pool.pool[bm.bootstrap_arguments[1]]).ref_index]).nametype_index)


        // JDK 17
        //MethodMeta linkCallSite = mhn.methods.get(new NamedDescription("linkCallSite", "(Ljava/lang/Object;ILjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/invoke/MemberName;"));
        //JDK 18
        MethodMeta linkCallSite = mhn.methods.get(new NamedDescription("linkCallSite", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/invoke/MemberName;"));

        AArray appendixResult = new AArray(1, method.clazz.loader.getOrLoadClazz("java/lang/Object").getClazzObj(1));

        //JDK 17
        /*
        InstancedClazz callSite = (InstancedClazz) ((Slot.ObjectRefSlot) linkCallSite.executor.run(
                new Slot.ObjectRefSlot(StackFrames.getFrames().get(0).clazz.getClazzObj()),
                new Slot.IntSlot(-1),
                new Slot.ObjectRefSlot(boostrapMethodHandle),
                new Slot.ObjectRefSlot(createStr(method.clazz.loader, m.name)), new Slot.ObjectRefSlot(mtti),
                new Slot.ObjectRefSlot(staticArgs),
                new Slot.ObjectRefSlot(appendixResult)
        )).value;
        */
        //JDK 18

        InstancedClazz callSite = (InstancedClazz) ((Slot.ObjectRefSlot) linkCallSite.executor.run(
                new Slot.ObjectRefSlot(StackFrames.getFrames().get(0).clazz.getClazzObj()),
                new Slot.ObjectRefSlot(boostrapMethodHandle),
                new Slot.ObjectRefSlot(createStr(method.clazz.loader, m.name)), new Slot.ObjectRefSlot(mtti),
                new Slot.ObjectRefSlot(staticArgs),
                new Slot.ObjectRefSlot(appendixResult)
        )).value;


        //NOTE: TODO/TRY, invoke the lambdaform discribed method handle method with some args? probably its own

        //((ConstantCallSite) binding).dynamicInvoker().invokeWithArguments()
        InstancedClazz builder = (InstancedClazz)(appendixResult.oops[0]);
        MethodMeta mmm = ((PrimTypeObj<MethodMeta>)((InstancedClazz)((InstancedClazz)((InstancedClazz)(builder.fields[1])).fields[6]).fields[4]).fields[0]).object;

        DDescription.MethodType mmmType = DDescription.decodeMethod(mmm.disc.description);
        Slot[] args = new Slot[mmmType.paramSize];
        System.arraycopy(stack.popN(mmmType.paramSize-1), 0, args, 1, mmmType.paramSize-1);
        args[0] = new Slot.ObjectRefSlot(builder);
        InstancedClazz invokeDynamicResult = (InstancedClazz) ((Slot.ObjectRefSlot)mmm.executor.run(args)).value;

        System.out.println("invokedynamic: "+id);
        stack.push(new Slot.ObjectRefSlot(invokeDynamicResult));
        return pc;
    }


    public static InstancedClazz createMethodType(MethodMeta meta) {
        DDescription.MethodType mdt = DDescription.decodeMethod(meta.disc.description);
        if (mdt.retType.arrayDim != 0)
            throw new IllegalStateException();
        ClazzMeta ret = meta.clazz.loader.getOrLoadClazz(mdt.retType.type.clazz);
        ret.ensureResolved();

        AArray clazzArgs = new AArray(mdt.args.length, meta.clazz.loader.getOrLoadClazz("java/lang/Object").getClazzObj(1));
        for (int i = 0; i < mdt.args.length; i++) {
            if (mdt.args[i].arrayDim != 0)
                throw new IllegalStateException();

            ClazzMeta clz = meta.clazz.loader.getOrLoadClazz(mdt.args[i].type.clazz);
            clz.ensureResolved();
            clazzArgs.oops[i] = clz.getClazzObj(mdt.args[i].arrayDim);
        }


        ClazzMeta mt = meta.clazz.loader.getOrLoadClazz("java/lang/invoke/MethodType");
        mt.ensureResolved();

        ClazzMeta mtf = meta.clazz.loader.getOrLoadClazz("java/lang/invoke/MethodTypeForm");
        mtf.ensureResolved();


        InstancedClazz mti = mt.instantiate();
        mt.methods.get(new NamedDescription("<init>", "(Ljava/lang/Class;[Ljava/lang/Class;)V")).executor
                .run(new Slot.ObjectRefSlot(mti), new Slot.ObjectRefSlot(ret.getClazzObj(mdt.retType.arrayDim)), new Slot.ObjectRefSlot(clazzArgs));
        InstancedClazz mtfi = (InstancedClazz) ((Slot.ObjectRefSlot)mtf.methods.get(new NamedDescription("findForm", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodTypeForm;")).executor
                .run(new Slot.ObjectRefSlot(mti))).value;
        mt.fields.get("form").accessor.set(mti, mtfi);

        return mti;
    }

    private static Array createArray(int i, int[] dimensions, ClazzMeta base, DDescription.Type type) {
        if (i < dimensions.length-1) {
            AArray out = new AArray(dimensions[i], base.getClazzObj(dimensions.length-i));
            for (int j = 0; j < dimensions[i]; j++) {
                out.oops[j] = createArray(i+1, dimensions, base, type);
            }
            return out;
        } else {
            if (type.type instanceof DDescription.ClazzType) {
                return new AArray(dimensions[i], base.getClazzObj(dimensions.length-i));
            } else {
                return new PrimArray<Integer>(dimensions[i], type.type);
            }
        }
    }
}
