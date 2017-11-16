package com.labvolution.nmealistener.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Signal1<T> {
    private Set<Slot1<T>> slots; // interface that cannot contain duplicate elements

    public synchronized Object connect(Slot1<T> handler) {
        if (slots == null) {
            slots = new HashSet<>();
        }
        slots.add(handler);
        return handler;
    }

    public synchronized void disconnect(Object handle) {
        if (slots == null) {
            return;
        }
        slots.remove(handle);
    }

    public void fire(T value) {
        ArrayList<Slot1<T>> slots; // similar to arrays but can dynamically grow and shrink
        synchronized (this) {
            if (this.slots == null) {
                return;
            }
            slots = new ArrayList<>(this.slots);
        }
        for (Slot1<T> handler : slots) {
            handler.apply(value);
        }
    }
}
