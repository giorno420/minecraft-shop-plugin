package com.giornosmp.shop;

import org.bukkit.plugin.*;

public abstract class ThisPlugin
{
    public static Plugin p;

    public static void constructor(final Plugin p) {
        ThisPlugin.p = p;
    }

    public static Plugin get() {
        return ThisPlugin.p;
    }
}
