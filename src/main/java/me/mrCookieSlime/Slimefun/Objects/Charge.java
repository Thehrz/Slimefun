package me.mrCookieSlime.Slimefun.Objects;

public class Charge {
    final double charge;
    final double capacity;

    public Charge(double charge, double capacity) {
        this.charge = charge;
        this.capacity = capacity;
    }

    public double getStoredEnergy() {
        return this.charge;
    }

    public double getCapacity() {
        return this.capacity;
    }
}


