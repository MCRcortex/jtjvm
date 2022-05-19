package clazz;

import clazzParser.Attribute;
import clazzParser.ClazzFile;
import util.NamedDescription;

public class MethodMeta {
    //TODO: need to special case native methods
    public ClazzMeta clazz;
    public int flags;
    public NamedDescription disc;
    public ClazzFile.Attributes attributes;

    public IMethodExecutor executor;


    public MethodMeta(ClazzMeta clazz, ClazzFile.Method method) {
        this.clazz = clazz;
        flags = method.flags;
        disc = new NamedDescription(method.name, method.desc);
        attributes = method.attributes;
        Attribute.Code code = method.attributes.getCode();
        if (code != null) {
            executor = new BytecodeExecutor(this, code);
        }
    }


    public void ensureResolved() {
        clazz.ensureResolved();
    }
}
