package clazz.instances;


import clazz.ClazzMeta;
import clazz.instances.special.ClazzObj;

//TODO: NEED TO ADD BASIC TYPECHECKING TOO too make sure shit doesnt go work too quickly
//TODO: FIGURE OUT HOW TO DO MULTIDIMARRAYS WELL
public class AArray extends Array {
    public final OOP[] oops;
    public final ClazzObj clazz;

    public AArray(int size, ClazzObj clazz) {
        this.oops = new OOP[size];
        this.clazz = clazz;
    }

    @Override
    public int length() {
        return oops.length;
    }
}
