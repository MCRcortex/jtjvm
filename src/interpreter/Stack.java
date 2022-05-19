package interpreter;

import clazz.instances.OOP;
import clazz.instances.special.PrimTypeObj;

public class Stack {
    Slot[] stack;
    int stack_ptr;

    public Stack(int max_stack) {
        stack = new Slot[max_stack];
    }

    public void pushRaw(Slot obj) {
        stack[stack_ptr++] = obj;
    }
    public void push(Slot obj) {
        if (obj instanceof Slot.ObjectRefSlot && obj.value instanceof PrimTypeObj)
            throw new IllegalStateException();
        if (obj.size != 1)
            throw new IllegalStateException("Non 1 size slot");
        stack[stack_ptr++] = obj;
    }

    public void push2(Slot obj) {
        if (obj.size != 2)
            throw new IllegalStateException("Non 2 size slot");
        stack[stack_ptr++] = obj;
        stack[stack_ptr++] = obj;
    }

    public Slot pop() {
        return stack[--stack_ptr];
    }

    public Slot pop2() {//TODO: CHECK IF a and b MUST BE THE SAME
        Slot a = pop();
        Slot b = pop();
        if (a != b)
            throw new IllegalStateException();
        return a;
    }

    public Slot[] popN(int count) {
        Slot[] r = new Slot[count];
        stack_ptr -= count;
        System.arraycopy(stack, stack_ptr, r, 0, count);
        return r;
    }

    public Slot.IntSlot popInt() {
        return (Slot.IntSlot)pop();
    }

    public Slot.LongSlot popLong() {
        Slot a = pop();
        Slot b = pop();
        if (a != b)
            throw new IllegalStateException();
        return (Slot.LongSlot)a;
    }

    public Slot.ObjectRefSlot popObjRef() {
        return (Slot.ObjectRefSlot)pop();
    }

    public void push(int i) {
        push(new Slot.IntSlot(i));
    }

    public void push(float i) {
        push(new Slot.FloatSlot(i));
    }

    public void push(boolean i) {
        push(new Slot.IntSlot(i?1:0));
    }

    public void push(byte i) {
        push(new Slot.IntSlot(i));
    }

    public void push(char i) {
        push(new Slot.IntSlot(i));
    }

    public void push(long i) {
        push2(new Slot.LongSlot(i));
    }


    public void push(OOP object) {
        push(new Slot.ObjectRefSlot(object));
    }

    public void push(double i) {
        push2(new Slot.DoubleSlot(i));
    }


    public void swap() {
        Slot a = pop();
        Slot b = pop();
        push(a);
        push(b);
    }

    /*
    public Slot.ByteSlot popByte() {
        return (Slot.ByteSlot) pop();
    }

    public Slot.CharSlot popChar() {
        return (Slot.CharSlot) pop();
    }
     */

    public Slot.FloatSlot popFloat() {
        return (Slot.FloatSlot) pop();
    }

    public Slot.DoubleSlot popDouble() {
        return (Slot.DoubleSlot) pop2();
    }
}
