package natives.methods.jdk.internal.util;

import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

@IClazzSpecifier(Clazz = "jdk/internal/util/SystemProps$Raw")
public class SystemProps__Raw {

    @INativeMethod(Static = true)
    public static String[] platformProperties() {
        String[] props = new String[51];
        props[0] = "AU";
        props[1] = "en";
        props[2] = "";
        props[3] = "";
        props[4] = "Cp1252";
        props[5] = "\\";
        props[6] = "AU";
        props[7] = "en";
        props[8] = "";
        props[9] = "";
        props[18] = "";//TEMP DIRECTORY
        props[19] = "\r\n";
        props[20] = "amd64";
        props[21] = "Windows 10";
        props[22] = "10.0";
        props[23] = ";";
        props[28] = "64";
        props[29] = "little";
        props[30] = "amd64";
        props[31] = "UnicodeLittle";
        props[32] = "Cp1252";
        props[33] = "";
        props[36] = System.getProperty("user.dir");//Current directory
        props[37] = "";//User directory
        props[38] = "jnj";//User
        return props;
    }

    @INativeMethod(Static = true)
    public static String[] vmProperties() {
        String[] props = new String[40];
        props[0] = "java.vm.specification.name";
        props[1] = "Java Virtual Machine Specification";
        props[2] = "java.vm.version";
        props[3] = "17.0.1+12-39";
        props[4] = "java.vm.name";
        props[5] = "OpenJDK 64-Bit Server VM";
        props[6] = "jdk.debug";
        props[7] = "release";
        props[8] = "sun.boot.library.path";
        props[9] = System.getProperties().getProperty("sun.boot.library.path");//Boot path
        props[10] = "java.library.path";
        props[11] = System.getProperties().getProperty("java.library.path");//Library path
        props[12] = "java.home";
        props[13] = System.getProperties().getProperty("java.home");//Java home path
        props[14] = "java.class.path";
        props[15] = System.getProperties().getProperty("java.class.path").split(";")[0];//Classpath
        props[16] = "jdk.boot.class.path.append";
        props[17] = "";//Added paths?
        props[18] = "java.vm.info";
        props[19] = "mixed mode, sharing";
        props[20] = "java.vm.specification.vendor";
        props[21] = "Oracle Corporation";
        props[22] = "java.vm.specification.version";
        props[23] = "17";
        props[24] = "java.vm.vendor";
        props[25] = "Oracle Corporation";
        props[26] = "jdk.module.addmods.0";
        props[27] = "java.instrument";
        props[28] = "file.encoding";
        props[29] = "UTF-8";
        props[30] = "sun.java.command";
        props[31] = "Main";//Launch command i think
        props[32] = "sun.java.launcher";
        props[33] = "SUN_STANDARD";//Launcher -- sun.launcher.LauncherHelper
        props[34] = "java.vm.compressedOopsMode";
        props[35] = "Zero based";
        props[36] = "sun.management.compiler";
        props[37] = "HotSpot 64-Bit Tiered Compilers";// this is just here cause, but in reality there is no compiler....
        return props;
    }
}
