package interpreter;

import clazz.MethodMeta;

import java.util.ArrayList;
import java.util.List;

public class StackFrames {
    private static final class FrameStack {
        final ArrayList<MethodMeta> frames = new ArrayList<>();
        public void push(MethodMeta meta) {
            frames.add(0,meta);
            //frames.add(meta);
        }

        public void pop() {
            frames.remove(0);
            //frames.remove(frames.size()-1);
        }
    }

    public static final ThreadLocal<FrameStack> frames = ThreadLocal.withInitial(FrameStack::new);
    public static void push(MethodMeta method) {
        frames.get().push(method);
    }

    public static void pop() {
        frames.get().pop();
    }

    public static List<MethodMeta> getFrames() {
        return new ArrayList<>(frames.get().frames);
    }

    public static void dumpStack() {
        for(MethodMeta frame : getFrames()) {
            System.err.println(frame.clazz.getName() + " " + frame.disc);
        }
    }
}
