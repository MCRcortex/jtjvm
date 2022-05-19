package clazz.instances.special;

import clazz.instances.OOP;

//This is just done cause its easier
public class PrimTypeObj<T> extends OOP {
    public final T object;
    public PrimTypeObj(T value) {
        this.object = value;
    }
}
