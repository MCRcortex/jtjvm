package interpreter;

import clazz.instances.InstancedClazz;
import clazz.instances.OOP;

public class Slot<T> {
    public static final Slot<Void> NULL = new Slot<>(1, null);


    public int size;
    public T value;

    public Slot(int size, T value) {
        this.size = size;
        this.value = value;
    }


    public static class IntSlot extends Slot<Integer> {
        public IntSlot(int value) {
            super(1, value);
        }
    }

    public static class LongSlot extends Slot<Long> {
        public LongSlot(long value) {
            super(2, value);
        }
    }

    /*
    public static class BooleanSlot extends Slot<Boolean> {
        public BooleanSlot(boolean value) {
            super(1, value);
        }
    }

    public static class ByteSlot extends Slot<Byte> {
        public ByteSlot(byte value) {
            super(1, value);
        }
    }

    public static class CharSlot extends Slot<Character> {
        public CharSlot(char value) {
            super(1, value);
        }
    }
     */

    public static class ObjectRefSlot extends Slot<OOP> {
        public ObjectRefSlot(OOP object) {
            super(1, object);
        }
    }

    public static class FloatSlot extends Slot<Float> {
        public FloatSlot(Float value) {
            super(1, value);
        }
    }

    public static class DoubleSlot extends Slot<Double> {
        public DoubleSlot(Double value) {
            super(2, value);
        }
    }

}
