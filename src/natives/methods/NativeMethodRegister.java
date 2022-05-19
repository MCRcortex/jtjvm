package natives.methods;

import clazz.INativeMethodExecutor;
import clazz.instances.AArray;
import clazz.instances.InstancedClazz;
import interpreter.Slot;
import natives.methods.java.io.*;
import natives.methods.java.lang.*;
import natives.methods.java.lang.Runtime;
import natives.methods.java.lang.System;
import natives.methods.java.lang.invoke.LambdaPr0xyClassArchive;
import natives.methods.java.lang.invoke.Meth0dHandle;
import natives.methods.java.lang.invoke.Meth0dHandleNatives;
import natives.methods.java.lang.reflect.Array;
import natives.methods.java.lang.reflect.NativeConstructorAccessorImpl;
import natives.methods.java.lang.reflect.NativeMethodAccessorImpl;
import natives.methods.jdk.internal.NativeImageBuffer;
import natives.methods.jdk.internal.loader.BootLoader;
import natives.methods.jdk.internal.misc.CDS;
import natives.methods.jdk.internal.misc.ScopedMemoryAccess;
import natives.methods.jdk.internal.misc.Unsafe_;
import natives.methods.jdk.internal.misc.VM;
import natives.methods.jdk.internal.loader.Nativ3Libraries;
import natives.methods.jdk.internal.reflect.Reflecti0n;
import natives.methods.jdk.internal.util.SystemProps__Raw;
import natives.methods.sun.nio.fs.W1nd0wsNativeDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

//TODO: Need to make like a field in these classes where the ClazzMeta can be set
public class NativeMethodRegister {
    //FIXME: Put this not in a public static, make like a JVM state thing and put it in there
    public static NativeMethodRegister NMR = new NativeMethodRegister();

    public Map<String, Map<String, INativeMethodExecutor>> NATIVES = new HashMap<>();

    public NativeMethodRegister() {
        registerKnown();
    }

    public void register(String clazz, String methodName, INativeMethodExecutor handle) {
        NATIVES.computeIfAbsent(clazz, a->new HashMap<>()).put(methodName, handle);
    }

    public void register(Class<?> from) {
        Map<String, INativeMethodExecutor> methodLUT = new HashMap<>();
        String clazzName = from.getAnnotation(IClazzSpecifier.class).Clazz();
        if (NATIVES.containsKey(clazzName))
            throw new IllegalStateException("Duplicate native clazz definisions "+clazzName);
        NATIVES.put(clazzName, methodLUT);
        for (Method m : from.getDeclaredMethods()) {
            if (m.getAnnotation(INativeMethod.class) != null) {
                if (methodLUT.containsKey(m.getName()))
                    throw new IllegalStateException("Duplicate native method names for the same clazz: "+m.getName());
                boolean isStatic = m.getAnnotation(INativeMethod.class).Static();
                INativeMethodExecutor executor = generateProxyMethod(m, isStatic);
                methodLUT.put(m.getName(), executor);
            }
            if (m.getAnnotation(IRawNativeMethod.class) != null) {
                if (methodLUT.containsKey(m.getName()))
                    throw new IllegalStateException("Duplicate native method names for the same clazz: "+m.getName());

                methodLUT.put(m.getName(), (a,b)-> {
                    try {
                        return (Slot) m.invoke(null,a,b);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        //e.printStackTrace();
                        throw new IllegalStateException(e.getMessage());
                    }
                });
            }
        }
    }

    private INativeMethodExecutor generateProxyMethod(Method target, boolean isStatic) {
        return (m, args) -> {
            Object ret = null;
            Class[] parameterTypes = target.getParameterTypes();
            Object[] params = new Object[parameterTypes.length];
            int slotId = 0;
            for (int i = 0; i < params.length; i++) {
                params[i] = Adaptor.fromInner(parameterTypes[i], args[slotId]);
                slotId += args[slotId].size;
            }
            if (args.length != slotId) {
                throw new IllegalStateException();
            }

            try {
                ret = target.invoke(null, params);
            } catch (Throwable e) {
                e.printStackTrace();
                java.lang.System.exit(-1);
                throw new IllegalStateException();
            }
            return Adaptor.toInner(m, target.getReturnType(), ret);
        };
    }

    public void registerKnown() {
        register(System.class);
        register(Klass.class);
        register(CDS.class);
        register(Object_.class);
        register(Fl0at.class);
        register(D0uble.class);
        register(Runtime.class);
        register(Unsafe_.class);
        register(Reflecti0n.class);
        register(SystemProps__Raw.class);
        register(VM.class);
        register(Fi1eInputStream.class);
        register(Fi1eDescriptor.class);
        register(Fi1eOutputStream.class);
        register(ScopedMemoryAccess.class);
        register(S1gnal.class);
        register("sun/io/Win32ErrorMode","setErrorMode", (m, args)->{return args[0];});
        register(Th3ad.class);
        register(WinNTFi1eSystem.class);
        register(BootLoader.class);
        register(C1assL0ader.class);
        register("java/lang/StringUTF16","isBigEndian", (m, args)->{return new Slot.IntSlot(0);});
        register("java/security/AccessController","getStackAccessControlContext", (m, args)->{return new Slot.ObjectRefSlot(null);});
        register("java/lang/ref/Reference","refersTo0", (m, args)->{
            //TODO: IMPLMENT PROPERLY
            return new Slot.IntSlot(1);
        });
        register(M0dule.class);
        register(NativeMethodAccessorImpl.class);
        register(NativeConstructorAccessorImpl.class);
        register(Meth0dHandleNatives.class);
        register(Array.class);
        register(Meth0dHandle.class);
        register(LambdaPr0xyClassArchive.class);
        register("java/lang/String","intern", (m, args)->{return new Slot.ObjectRefSlot((InstancedClazz)((Slot.ObjectRefSlot)args[0]).value);});//TODO: IMPLMENT
        register("java/lang/ref/Reference","clear0", (m, args)->{return new Slot.ObjectRefSlot(null);});//TODO: IMPLMENT
        register("jdk/internal/reflect/DirectConstructorHandleAccessor$NativeAccessor","newInstance0", (m, args)->{// jdk 18
            return new Slot.ObjectRefSlot(NativeConstructorAccessorImpl.newInstance0((InstancedClazz) ((Slot.ObjectRefSlot)args[0]).value, (AArray) ((Slot.ObjectRefSlot)args[1]).value));
        });
        register("jdk/internal/reflect/DirectMethodHandleAccessor$NativeAccessor","invoke0", (m, args)->{// jdk 18
            return new Slot.ObjectRefSlot(NativeMethodAccessorImpl.invoke0((InstancedClazz) ((Slot.ObjectRefSlot)args[0]).value, ((Slot.ObjectRefSlot)args[1]).value, (AArray) ((Slot.ObjectRefSlot)args[2]).value));
        });
        register(Nativ3Libraries.class);



        register(NativeImageBuffer.class);
        register(W1nd0wsNativeDispatcher.class);
    }
}