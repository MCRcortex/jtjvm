package clazz;

import clazz.instances.special.ClazzLoaderObj;

public class LazyClazzRef {
    private String clazzName;
    private ClazzMeta resolved;
    //TODO: CHECK IF THIS IS OK TO DO or if every time  it gets resolved it needs to use some other loader
    private ClazzLoaderObj loader;
    public LazyClazzRef(String clazzName, ClazzLoaderObj loader) {
        this.clazzName = clazzName;
        this.loader = loader;
    }

    //getOrResolve
    public ClazzMeta get() {
        if (resolved != null)
            return resolved;
        resolved = loader.getOrLoadClazz(clazzName);
        return resolved;
    }
}
