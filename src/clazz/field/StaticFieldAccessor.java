package clazz.field;

import clazz.FieldMeta;
import clazz.instances.InstancedClazz;
import clazz.instances.OOP;
import clazz.instances.special.PrimTypeObj;
import util.DDescription;

public class StaticFieldAccessor implements IFieldAccessor {
    private OOP object;


    private final FieldMeta field;
    public StaticFieldAccessor(FieldMeta field) {
        this.field = field;
    }

    @Override
    public OOP get(OOP clazz) {
        DDescription.Type t = DDescription.decodeType(field.disc.description);
        if (t.arrayDim != 0 || t.type instanceof DDescription.ClazzType) {
            return object;
        } else {
            if (object == null) {
                DDescription.Type type = DDescription.decodeType(field.disc.description);
                if (type.arrayDim != 0) {
                    return null;
                } else {
                    if (type.type == DDescription.INTEGER)
                        return new PrimTypeObj(0);
                    else if (type.type == DDescription.LONG)
                        return  new PrimTypeObj((long) 0);
                    else if (type.type == DDescription.BOOLEAN)
                        return new PrimTypeObj(false);
                    else if (type.type == DDescription.BYTE)
                        return new PrimTypeObj((byte)0);
                    else if (type.type == DDescription.CHAR)
                        return new PrimTypeObj((char)0);
                    else if (type.type == DDescription.FLOAT)
                        return new PrimTypeObj((float)0);
                    else if (type.type instanceof DDescription.ClazzType)
                        return null;
                    else
                        throw new IllegalStateException();
                }
            }
            return object;
        }
    }

    @Override
    public void set(OOP clazz, OOP obj) {
        object = obj;
    }
}
