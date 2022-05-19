package clazz.field;

import clazz.instances.InstancedClazz;
import clazz.instances.OOP;

//TODO: add type checking just to be sure
public interface IFieldAccessor {
    OOP get(OOP clazz);
    void set(OOP clazz, OOP obj);
}
