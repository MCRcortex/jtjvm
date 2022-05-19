package natives.methods.java.lang.invoke;

import clazz.MethodMeta;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.special.PrimTypeObj;
import interpreter.Slot;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;
import natives.methods.IRawNativeMethod;

import java.lang.invoke.MethodHandle;

@IClazzSpecifier(Clazz = "java/lang/invoke/MethodHandle")
public class Meth0dHandle {
    //NOTE: PRETTY SURE ITS MENT TO USE THE lambdaform from methodhandle

    @IRawNativeMethod
    public static Slot invoke(MethodMeta m, Slot[] args) {
        if (((InstancedClazz)((Slot.ObjectRefSlot)args[0]).value).clazz.getName().equals("java/lang/invoke/DirectMethodHandle")) {
            //The following assumes everything is correct, it follows the following path
            //DirectMethodHandle.member.method.injectedDataAField where DirectMethodHandle is the first slot arg
            MethodMeta method = ((PrimTypeObj<MethodMeta>) ((InstancedClazz) ((InstancedClazz) (((InstancedClazz) ((Slot.ObjectRefSlot) args[0]).value).fields[6])).fields[4]).fields[0]).object;
            Slot[] callArgs = new Slot[args.length - 1];//Create and copy the args except for the methodhandle which is in slot 0
            System.arraycopy(args, 1, callArgs, 0, callArgs.length);
            Slot result = method.executor.run(callArgs);
            return result;
        } else {
            throw new IllegalStateException();
        }
    }


    @IRawNativeMethod
    public static Slot invokeBasic(MethodMeta m, Slot[] args) {
        MethodMeta method = ((PrimTypeObj<MethodMeta>)((InstancedClazz)((InstancedClazz)((InstancedClazz)((InstancedClazz)((Slot.ObjectRefSlot)args[0]).value).fields[1]).fields[6]).fields[4]).fields[0]).object;
        Slot result = method.executor.run(args);
        return result;
    }

    @IRawNativeMethod
    public static Slot invokeExact(MethodMeta m, Slot[] args) {
        MethodMeta method = ((PrimTypeObj<MethodMeta>)((InstancedClazz)((InstancedClazz)((InstancedClazz)((InstancedClazz)((Slot.ObjectRefSlot)args[0]).value).fields[1]).fields[6]).fields[4]).fields[0]).object;
        //TODO: WHAT WHY DO WE DUPLICATE THE FIRST ARG!!! todo see why the fuck this is, i think its cause it needs the instance???
        Slot[] callArgs = new Slot[args.length + 1];//Create and copy the args except for the methodhandle which is in slot 0
        System.arraycopy(args, 0, callArgs, 1, args.length);
        callArgs[0] = args[0];
        Slot result = method.executor.run(callArgs);
        return result;
    }


    @IRawNativeMethod
    public static Slot linkToSpecial(MethodMeta m, Slot[] args) {
        //Last element in args is always MemberName
        MethodMeta method = ((PrimTypeObj<MethodMeta>)((InstancedClazz)((InstancedClazz)((Slot.ObjectRefSlot)args[args.length-1]).value).fields[4]).fields[0]).object;
        Slot[] callArgs = new Slot[args.length - 1];//Create and copy the args except for the methodhandle which is in slot 0
        System.arraycopy(args, 0, callArgs, 0, args.length-1);
        Slot result = method.executor.run(callArgs);
        return result;
    }
}
