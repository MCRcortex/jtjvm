package natives.methods.java.lang.invoke;

import clazz.instances.InstancedClazz;
import clazz.instances.special.ClazzObj;
import natives.methods.IClazzSpecifier;
import natives.methods.INativeMethod;

//Including this here because it can be used to speedup performance in the future
@IClazzSpecifier(Clazz = "java/lang/invoke/LambdaProxyClassArchive")
public class LambdaPr0xyClassArchive {
    @INativeMethod(Static = true)
    public static ClazzObj findFromArchive(ClazzObj caller,
                                           String interfaceMethodName,
                                           InstancedClazz factoryType,
                                           InstancedClazz interfaceMethodType,
                                           InstancedClazz implementationMember,
                                           InstancedClazz dynamicMethodType) {
        return null;
    }
}
