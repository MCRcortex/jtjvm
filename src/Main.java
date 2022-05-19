import clazz.BytecodeExecutor;
import clazz.ClazzMeta;
import clazz.MethodMeta;
import clazz.instances.InstancedClazz;
import clazz.instances.special.ClazzObj;
import clazz.instances.special.PrimTypeObj;
import clazzParser.ClazzFile;
import interpreter.BytecodeInterpreter;
import interpreter.Slot;
import interpreter.StackFrames;
import natives.BootClazzLoader;
import natives.methods.NativeMethodRegister;
import natives.methods.java.lang.Th3ad;
import util.NamedDescription;

import java.io.File;
import java.lang.invoke.LambdaMetafactory;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//TODO: LOOK INTO sun.instrument
public class Main {
    public static void main(String[] args) throws Exception {
        main2();
    }
    public static void main2() throws Exception {
        //TODO: look into sun.launcher.LauncherHelper to launch

        /*
        ResourceLocator root = ResourceLocator.createBootstrapResources();
        ClazzFile obj = new ClazzFile(Files.readAllBytes(root.findClazz("java/lang/String")));

        Path p = Paths.get(URI.create("jrt:/")).resolve("/modules");
        Files.walk(p).forEach(a->{
            if(a.toString().endsWith(".class"))
            {
            try {
                new ClazzFile(Files.readAllBytes(a));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }});
         */
        //System.out.println(root.getOrLoadClazz("java/lang/String").getMethods());
        //System.out.println(root.getOrLoadClazz("jdk/internal/loader/ClassLoaders").super_.get());


        /*
        Path p = Paths.get(URI.create("jrt:/")).resolve("/modules");
        Files.walk(p).forEach(a->{
            if(a.toString().startsWith("/modules/java.base/java") && a.toString().endsWith(".class"))
            {
                try {
                    root.getOrLoadClazz(a.toString().substring(a.toString().indexOf("/",9)+1).split("\\.")[0]).ensureResolved();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }});

         */


        //ClazzMeta meta = root.getOrLoadClazz("jdk/internal/loader/ClassLoaders");
        //meta.ensureResolved();
        initMainThread();

        ClazzMeta meta = BootClazzLoader.ROOT.getOrLoadClazz("java/lang/System");
        meta.ensureResolved();

        //MethodMeta method = meta.methods.get(new NamedDescription("initPhase1", "()V"));
        //BytecodeInterpreter.run(method);
        //method = meta.methods.get(new NamedDescription("initPhase3", "()V"));
        //method = meta.methods.get(new NamedDescription("initPhase2", "(ZZ)I"));
        MethodMeta method = meta.methods.get(new NamedDescription("initPhase1", "()V"));
        method.executor.run();
        BootClazzLoader.ROOT.getOrLoadClazz("jdk/internal/loader/ClassLoaders").ensureResolved();
        BootClazzLoader.ROOT.getOrLoadClazz("java/util/stream/Collectors").ensureResolved();//TODO: FIGURE OUT WHY PUTTING THIS HERE FIX'S IT???
        method = meta.methods.get(new NamedDescription("initPhase2", "(ZZ)I"));
        //method = meta.methods.get(new NamedDescription("initPhase3", "()V"));
        method.executor.run(new Slot.IntSlot(1), new Slot.IntSlot(1));
        method = meta.methods.get(new NamedDescription("initPhase3", "()V"));
        method.executor.run();


        meta = BootClazzLoader.ROOT.getOrLoadClazz("sun.launcher.LauncherHelper");
        meta.ensureResolved();
        method = meta.methods.get(new NamedDescription("checkAndLoadMain", "(ZILjava/lang/String;)Ljava/lang/Class;"));
        ClazzObj launchClazz = (ClazzObj) ((Slot.ObjectRefSlot)method.executor.run(new Slot.IntSlot(1), new Slot.IntSlot(1), new Slot.ObjectRefSlot(BytecodeInterpreter.createStr(BootClazzLoader.ROOT, "Main")))).value;
        MethodMeta mainMeta = launchClazz.clazz.methods.get(new NamedDescription("main", "([Ljava/lang/String;)V"));
        mainMeta.executor.run(new Slot.ObjectRefSlot(null));
        System.out.println("HELLO");
        /*
        try {

        } catch (Exception e) {
            for(MethodMeta frame : StackFrames.getFrames()) {
                System.err.println(frame.clazz.name + " " + frame.disc);
            }
        }
         */

        //meta = root.getOrLoadClazz("java/lang/invoke/MethodHandles");
        //method = meta.methods.get(new NamedDescription("loop", "([[Ljava/lang/invoke/MethodHandle;)Ljava/lang/invoke/MethodHandle;"));
        //BytecodeInterpreter.run(method, new Slot.ObjectRefSlot(null), new Slot.ObjectRefSlot(null), new Slot.ObjectRefSlot(null));
        //meta = root.gnetOrLoadClazz("jdk/internal/loader/ClassLoaders");
        //BytecodeInterpreter.run(meta.methods.get(new NamedDescription("platformClassLoader", "()Ljava/lang/ClassLoader;")));

        //CLZLDR a = new CLZLDR();
        //a.loadClass("test/BBB").getMethods()[1].invoke(null);
        //a.loadClass("test/BBB").getMethods()[1].invoke(null);
        //new B();
        //ClazzFile t = new ClazzFile(Files.readAllBytes(new File("D:\\expr\\out\\production\\JVMImpl\\clazzParser\\ClazzFile$Field.class").toPath()));


        //LambdaMetafactory.metafactory()


        //ClazzMeta meta = root.getOrLoadClazz("java/lang/System");
        //MethodMeta method = meta.getMethod(new NamedDescription("initPhase2", "(ZZ)I"));
        //BytecodeInterpreter.run(method, new Slot.BooleanSlot(true), new Slot.BooleanSlot(true));

    }

    private static void initMainThread() {
        ClazzMeta threadGroup = BootClazzLoader.ROOT.getOrLoadClazz("java/lang/ThreadGroup");
        threadGroup.ensureResolved();

        ClazzMeta thread = BootClazzLoader.ROOT.getOrLoadClazz("java/lang/Thread");
        thread.ensureResolved();

        InstancedClazz cThreadGroup = threadGroup.instantiate();
        threadGroup.methods.get(new NamedDescription("<init>", "()V")).executor.run(new Slot.ObjectRefSlot(cThreadGroup));

        InstancedClazz cThread = thread.instantiate();
        thread.fields.get("group").accessor.set(cThread, cThreadGroup);
        thread.fields.get("name").accessor.set(cThread, BytecodeInterpreter.createStr(BootClazzLoader.ROOT, "mainThread"));
        thread.fields.get("priority").accessor.set(cThread, new PrimTypeObj<>(5));

        Th3ad.threadClazzes.set(cThread);
    }
}
