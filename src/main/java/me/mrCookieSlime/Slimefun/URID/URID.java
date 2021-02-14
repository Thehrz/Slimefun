package me.mrCookieSlime.Slimefun.URID;

import java.util.HashMap;
import java.util.Map;

public class URID {
    public static Map<URID, Object> objects = new HashMap<>();
    public static Map<Integer, URID> ids = new HashMap<>();

    private static int next = 0;
    private final int id;
    private boolean dirty;

    public URID(Object object, boolean dirty) {
        this.id = next;
        next++;
        objects.put(this, object);
        ids.put(toInteger(), this);
    }

    public static URID nextURID(Object object, boolean dirty) {
        URID urid = new URID(object, dirty);
        return urid;
    }

    public static URID fromInteger(int id) {
        return ids.get(id);
    }

    public static Object decode(URID urid) {
        return objects.get(urid);
    }

    public int toInteger() {
        return this.id;
    }

    public void markDirty() {
        if (this.dirty) {
            ids.remove(toInteger());
            objects.remove(this);
        }
    }
}


