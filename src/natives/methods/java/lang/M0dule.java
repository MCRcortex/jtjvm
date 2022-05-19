package natives.methods.java.lang;

import clazz.ClazzMeta;
import clazz.instances.AArray;
import clazz.instances.InstancedClazz;
import clazz.instances.special.ClazzObj;
import natives.BootClazzLoader;
import natives.methods.Adaptor;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

import java.util.TreeMap;

@IClazzSpecifier(Clazz = "java/lang/Module")
public class M0dule {
    //TODO: NEED TO MAKE A layer 0 module thing for the vm and set all clazzes to be part of it

    //Todo: make a lut of modules then have ClazzObj auto assign
    public static InstancedClazz UNNAMED;
    public static TreeMap<String, InstancedClazz> MODULE_TREE = new TreeMap<>();
    @INativeMethod(Static = true)
    public static void defineModule0(InstancedClazz module, boolean isOpen, String version, String location, AArray pns) {
        String[] packageNames = new String[pns.length()];
        for (int i = 0; i < packageNames.length; i++) {
            packageNames[i] = Adaptor.toString((InstancedClazz) pns.oops[i]);
            MODULE_TREE.put(packageNames[i].replace(".", "/"), module);
        }
        if (location.endsWith("java.base")) {
            UNNAMED = module;
            for (ClazzMeta cm : BootClazzLoader.ROOT.loadedClazzs.values()) {
                cm.getClazzObj().getSuperMeta().fields.get("module").accessor.set(cm.getClazzObj(), module);
            }

            //ClazzMeta cm = BootClazzLoader.ROOT.getOrLoadClazz("java/lang/Object");
            //cm.getClazzObj().getSuperMeta().fields.get("module").accessor.set(cm.getClazzObj(), module);
        }
    }

    @INativeMethod(Static = true)
    public static void addReads0(InstancedClazz from, InstancedClazz too) {

    }

    @INativeMethod(Static = true)
    public static void addExportsToAll0(InstancedClazz from, String pn) {

    }

    @INativeMethod(Static = true)
    public static void addExports0(InstancedClazz from, String pn, InstancedClazz too) {

    }
}
