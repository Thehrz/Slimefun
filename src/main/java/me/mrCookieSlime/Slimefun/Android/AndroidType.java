package me.mrCookieSlime.Slimefun.Android;

public enum AndroidType {
    NONE,
    MINER,
    FARMER,
    ADVANCED_FARMER,
    WOODCUTTER,
    FIGHTER,
    FISHERMAN,
    NON_FIGHTER;

    public boolean isType(final AndroidType type) {
        return type.equals(AndroidType.NONE) || type.equals(this) || (type.equals(AndroidType.NON_FIGHTER) && !this.equals(AndroidType.FIGHTER));
    }
}
