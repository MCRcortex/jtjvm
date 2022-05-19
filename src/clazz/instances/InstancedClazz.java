package clazz.instances;

import clazz.ClazzMeta;
import clazz.FieldMeta;
import clazz.instances.special.PrimTypeObj;
import util.DDescription;

import java.util.HashMap;
import java.util.Map;

public class InstancedClazz extends OOP {
    public final ClazzMeta clazz;
    //ULTRA HACK
    //FIXME: MAKE BETTER
    //TODO: FIX THIS WONT WORK IF THERE ARE MULTIPLE PRIVATE FIELDS WITH THE SAME NAME AND TYP
    // this can occure due to inheratince
    public final OOP[] fields;// Represents all the fields in the class instance atm

    public InstancedClazz(ClazzMeta clazz) {
        this.clazz = clazz;
        if (clazz == null) {
            System.err.println("INSTANCE CLAZZ OF NULL META");
            fields = null;
        } else {
            clazz.ensureResolved();
            fields = new OOP[clazz.instancedFieldIndexCount];

            //Set the default values of OOP for all fields
            for (int i = 0; i < clazz.fieldMetas.length; i++) {
                FieldMeta fm = clazz.fieldMetas[i];
                if (fm.disc.description.equals(""))//Special case used for native field data injection
                    continue;
                DDescription.Type type = DDescription.decodeType(fm.disc.description);
                if (type.arrayDim != 0) {
                    fields[i] = null;
                } else {
                    if (type.type == DDescription.INTEGER)
                        fields[i] = new PrimTypeObj(0);
                    else if (type.type == DDescription.LONG)
                        fields[i] =  new PrimTypeObj((long) 0);
                    else if (type.type == DDescription.BOOLEAN)
                        fields[i] = new PrimTypeObj(false);
                    else if (type.type == DDescription.BYTE)
                        fields[i] = new PrimTypeObj((byte)0);
                    else if (type.type == DDescription.CHAR)
                        fields[i] = new PrimTypeObj((char)0);
                    else if (type.type == DDescription.FLOAT)
                        fields[i] = new PrimTypeObj((float)0);
                    else if (type.type == DDescription.SHORT)
                        fields[i] = new PrimTypeObj((short)0);
                    else if (type.type == DDescription.DOUBLE)
                        fields[i] = new PrimTypeObj((double)0);
                    else if (type.type instanceof DDescription.ClazzType)
                        fields[i] = null;
                    else
                        throw new IllegalStateException();
                }
            }
        }
    }
}
