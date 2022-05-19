package clazz;

import clazz.field.IFieldAccessor;
import clazz.field.InstancedFieldAccessor;
import clazz.field.StaticFieldAccessor;
import clazzParser.ClazzFile;
import util.NamedDescription;

import static util.Flags.*;

public class FieldMeta {
    public ClazzMeta clazz;
    public int flags;
    public NamedDescription disc;

    public final IFieldAccessor accessor;


    //TODO: NEED TO ADD NATIVE FIELD ACCESSES just for ease of use
    public FieldMeta(ClazzMeta clazz, ClazzFile.Field field) {
        this.clazz = clazz;
        flags = field.flags;
        disc = new NamedDescription(field.name, field.desc);
        accessor = ((flags&ACC_STATIC)==0)?new InstancedFieldAccessor(this):new StaticFieldAccessor(this);
    }

    public FieldMeta(ClazzMeta clazz, int flags, NamedDescription disc) {
        this.clazz = clazz;
        this.flags = flags;
        this.disc = disc;
        accessor = ((flags&ACC_STATIC)==0)?new InstancedFieldAccessor(this):new StaticFieldAccessor(this);
    }

    public void ensureResolved() {
        clazz.ensureResolved();
    }
}
