package com.chilieutenant.theringskills;

import com.chilieutenant.theringskills.skills.MainListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
