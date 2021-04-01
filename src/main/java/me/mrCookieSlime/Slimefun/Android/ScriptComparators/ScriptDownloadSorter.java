package me.mrCookieSlime.Slimefun.Android.ScriptComparators;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Android.ProgrammableAndroid;

import java.util.Comparator;

public class ScriptDownloadSorter implements Comparator<Config> {
    ProgrammableAndroid android;

    public ScriptDownloadSorter(final ProgrammableAndroid programmableAndroid) {
        this.android = programmableAndroid;
    }

    @Override
    public int compare(final Config c1, final Config c2) {
        return c2.getInt("downloads") - c1.getInt("downloads");
    }
}
