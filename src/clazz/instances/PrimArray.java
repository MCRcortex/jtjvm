package clazz.instances;

import clazz.instances.special.PrimTypeObj;
import util.DDescription;

import java.lang.constant.Constable;

public class PrimArray<T extends Constable> extends Array {
    public DDescription.PrimType type;
    public T[] array;

    public PrimArray(int length, DDescription.PrimType type) {
        if (type == DDescription.BYTE) {
            array = (T[])new Byte[length];
            for (int i = 0; i < array.length; i++)
                array[i] = (T)(Object)(byte)0;
        } else if (type == DDescription.CHAR) {
            array = (T[])new Character[length];
            for (int i = 0; i < array.length; i++)
                array[i] = (T)(Object)(char)0;
        } else if (type == DDescription.INTEGER) {
            array = (T[])new Integer[length];
            for (int i = 0; i < array.length; i++)
                array[i] = (T)(Object)0;
        } else if (type == DDescription.BOOLEAN) {
            array = (T[])new Boolean[length];
            for (int i = 0; i < array.length; i++)
                array[i] = (T)(Object)false;
        } else if (type == DDescription.SHORT) {
            array = (T[])new Short[length];
            for (int i = 0; i < array.length; i++)
                array[i] = (T)(Object)(short)0;
        } else if (type == DDescription.LONG) {
            array = (T[])new Long[length];
            for (int i = 0; i < array.length; i++)
                array[i] = (T)(Object)(long)0;
        } else if (type == DDescription.DOUBLE) {
            array = (T[])new Double[length];
            for (int i = 0; i < array.length; i++)
                array[i] = (T)(Object)0.0;
        } else if (type == DDescription.FLOAT) {
            array = (T[])new Float[length];
            for (int i = 0; i < array.length; i++)
                array[i] = (T)(Object)0.0f;
        } else {
            throw new IllegalStateException();
        }
        this.type = type;
    }

    @Override
    public int length() {
        return array.length;
    }
}
