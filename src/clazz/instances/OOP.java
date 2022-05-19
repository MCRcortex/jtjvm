package clazz.instances;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//TODO: redo them slightly so that this has abstracted default methods for getting and setting fields and let each OOP delegate how
public class OOP {
    //TODO: Redo this better
    public final Lock monitor = new ReentrantLock();
}
