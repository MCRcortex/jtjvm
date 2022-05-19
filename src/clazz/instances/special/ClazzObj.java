package clazz.instances.special;

import clazz.ClazzMeta;
import clazz.instances.InstancedClazz;
import natives.methods.java.lang.Klass;
import natives.methods.java.lang.M0dule;

//TODO: NOTE:FIX THIS THING HOW DOES IT MANAGE TO GET clazz, NOTE: NEED TO IMPLMENT INTO JVM
public class ClazzObj extends InstancedClazz {
    public final ClazzMeta clazz;
    public final int arrayDim;

    public ClazzObj(ClazzMeta clazz, boolean prim) {
        this(clazz);
    }
    public ClazzObj(ClazzMeta clazz) {
        this(clazz, 0);
    }
    //TODO: need to set the module component thing AaAa too something???
    public ClazzObj(ClazzMeta clazz, int arrayDim) {
        super(clazz == null?null:clazz.loader.getOrLoadClazz("java/lang/Class"));
        this.clazz = clazz;
        this.arrayDim = arrayDim;
        if (arrayDim != 0) {
            super.clazz.fields.get("componentType").accessor.set(this, super.clazz.getClazzObj(arrayDim - 1));
            super.clazz.fields.get("module").accessor.set(this, super.clazz.fields.get("module").accessor.get(clazz.getClazzObj()));
            super.clazz.fields.get("componentType").accessor.set(this, clazz.getClazzObj());
        } else {
            if (clazz.getName().lastIndexOf("/") != -1) {
                String packageName = clazz.getName().substring(0, clazz.getName().lastIndexOf("/"));
                InstancedClazz module = M0dule.MODULE_TREE.get(packageName);
                if (module == null) {
                    if (clazz.loader.getLoadedClazz("java/lang/ClassLoader") != null && clazz.loader.fields != null)
                        module = (InstancedClazz) clazz.loader.getLoadedClazz("java/lang/ClassLoader").fields.get("unnamedModule").accessor.get(clazz.loader);//throw new IllegalStateException();
                    else
                        module = M0dule.UNNAMED;
                }
                super.clazz.fields.get("module").accessor.set(this, module);
            } else {
                //SET TO UNNAMED MODULE
                if (clazz.loader.getLoadedClazz("java/lang/ClassLoader") != null && clazz.loader.fields != null)
                    super.clazz.fields.get("module").accessor.set(this, clazz.loader.getLoadedClazz("java/lang/ClassLoader").fields.get("unnamedModule").accessor.get(clazz.loader));
                else
                    super.clazz.fields.get("module").accessor.set(this, M0dule.UNNAMED);
            }
        }
    }

    public ClazzMeta getSuperMeta() {
        return super.clazz;
    }


    public String getClazzName() {
        if (super.clazz.isPrimative()) {
            //Special
            return super.clazz.getName();
        } else {
            return clazz.getName();
        }
    }
}
