package clazz.instances.special;

import clazz.BytecodeExecutor;
import clazz.ClazzMeta;
import clazz.MethodMeta;
import clazz.instances.InstancedClazz;
import clazzParser.ClazzFile;
import interpreter.BytecodeInterpreter;
import interpreter.Slot;
import interpreter.StackFrames;
import natives.BootClazzLoader;
import util.NamedDescription;

import java.util.HashMap;

public class ClazzLoaderObj extends InstancedClazz {
    public ClazzLoaderObj() {
        super(null);
    }

    public ClazzLoaderObj(ClazzMeta meta) {
        super(meta);
    }

    public HashMap<String, ClazzMeta> loadedClazzs = new HashMap<>();



    //TODO: need to add a callback into the ClassLoader this loader is representing that way when

    //TODO: need to add a bunch off callbacks back into the java code

    //findLoadedClass0
    public ClazzMeta getLoadedClazz(String clazz) {
        if (loadedClazzs.containsKey(clazz))
            return loadedClazzs.get(clazz);
        if (fields != null) {
            if (((ClazzLoaderObj) fields[0]) != null) {
                return ((ClazzLoaderObj) fields[0]).getLoadedClazz(clazz);
            } else  {
                return BootClazzLoader.ROOT.getLoadedClazz(clazz);
            }
        }
        return null;
    }

    public ClazzMeta getOrLoadClazz(String clazz) {
        clazz = clazz.replace('.','/');
        //Hack to get current stack frames
        if (StackFrames.getFrames().size()>0 && StackFrames.getFrames().get(0).clazz.getName().equals(clazz))
            return StackFrames.getFrames().get(0).clazz;
        ClazzMeta clz = getLoadedClazz(clazz);
        if (clz != null)
            return clz;
        //TODO: See if i need to define
        return loadClazz(clazz);
    }

    public ClazzMeta loadClazz(String clazz) {
        //TODO: need to make it invoke the clazzload function
        //TODO: CHECK IF  THIS IS RIGHT/FIGURE OUT HOW TO DO DELEGATION
        MethodMeta meta = ClazzMeta.locateMethod(new NamedDescription("loadClass", "(Ljava/lang/String;)Ljava/lang/Class;"), super.clazz, null);
        ClazzObj r = ((ClazzObj)((Slot.ObjectRefSlot)meta.executor.run(new Slot.ObjectRefSlot(this), new Slot.ObjectRefSlot(BytecodeInterpreter.createStr(this, clazz.replace("/","."))))).value);
        return r.clazz;
        //TODO: When a class method or variable is resolved (lazy evaulation) need to check if clinit needs to be called
    }

    //TODO: add more params like ClassLoader.defineClass1
    public ClazzMeta defineClazz0(byte[] clazz) {//This doesnt add to the clazz list
        return new ClazzMeta(this, new ClazzFile(clazz));
    }

    public ClazzMeta defineClazz(byte[] clazz)
    {
        ClazzFile clz = new ClazzFile(clazz);
        if (getLoadedClazz(clz.this_class) != null)
            throw new IllegalStateException("Class already defined");
        ClazzMeta clzz = new ClazzMeta(this, clz);
        loadedClazzs.put(clz.this_class, clzz);
        return clzz;
    }
}
