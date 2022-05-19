package clazz.field;

import clazz.FieldMeta;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.special.ClazzObj;
import clazz.instances.special.PrimTypeObj;
import util.DDescription;

public class InstancedFieldAccessor implements IFieldAccessor {
    private final FieldMeta field;
    public int indexId = -1;

    public InstancedFieldAccessor(FieldMeta field) {
        this.field = field;
    }

    @Override
    public OOP get(OOP clazz) {
        //System.out.println("get field "+field.clazz.name+" "+field.disc+" for "+clazz);
        if (clazz instanceof InstancedClazz) {
            OOP val = ((InstancedClazz) clazz).fields[indexId];
            if (val != null)
                return val;
            return null;
            //throw new IllegalStateException("Should not reach");
        } else {
            throw new IllegalStateException("Not implemented");
        }
    }

    @Override
    public void set(OOP clazz, OOP obj) {
        //System.out.println("set field "+field.clazz.name+" "+field.disc+" for "+clazz+" as "+obj);
        if (clazz instanceof InstancedClazz) {
            ((InstancedClazz) clazz).fields[indexId] = obj;
        } else {
            throw new IllegalStateException("Not implemented");
        }
    }
}
