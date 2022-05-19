package natives;

import clazz.ClazzMeta;
import clazz.instances.special.ClazzLoaderObj;
import clazzParser.ClazzFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class BootClazzLoader extends ClazzLoaderObj {
    //TODO: change so this is not needed anymore
    public static final BootClazzLoader ROOT;

    static {
        try {
            ROOT = new BootClazzLoader();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    ResourceLocator locator = ResourceLocator.createBootstrapResources();

    public BootClazzLoader() throws IOException {
    }

    public Set<ClazzMeta> PRIMATIVES = new HashSet<>();

    public ClazzMeta loadClazz(String clazz) {
        System.out.println("LOADING CLASS "+clazz);

        //HACK to load primative classes
        if (switch (clazz) {
            case "int", "void", "float", "char", "double", "boolean", "short", "byte", "long" -> true;
            default -> false;
            }) {
            String base = (switch (clazz){
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

            byte[] data = new byte[0];
            try {
                data = Files.readAllBytes(locator.findClazz(base));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ClazzMeta cm = new ClazzMeta(this, new ClazzFile(data));
            loadedClazzs.put(clazz, cm);
            PRIMATIVES.add(cm);
            return cm;
        }

        if (clazz.equals("java/lang/ServiceConfigurationError") || clazz.equals("java/lang/VirtualMachineError"))
            System.out.println("EEEEEEE");
        try {
            if (locator.findClazz(clazz) == null) {
                System.err.println("Cannot load root class: "+clazz);
                return null;//getLoadedClazz("java/lang/Object");
            }
            byte[] data = Files.readAllBytes(locator.findClazz(clazz));
            return defineClazz(data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
