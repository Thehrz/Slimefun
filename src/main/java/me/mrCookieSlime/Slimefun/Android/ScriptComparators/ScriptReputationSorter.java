package me.mrCookieSlime.Slimefun.Android.ScriptComparators;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Android.ProgrammableAndroid;

import java.util.Comparator;

public class ScriptReputationSorter implements Comparator<Config> {
    ProgrammableAndroid android;

    public ScriptReputationSorter(ProgrammableAndroid programmableAndroid) {
        this.android = programmableAndroid;
    }

    @Override
    public int compare(Config c1, Config c2) {
        return (int) (this.android.getScriptRating(c2) - this.android.getScriptRating(c1));
    }
}

