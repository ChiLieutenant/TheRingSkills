package com.chilieutenant.theringskills.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class MainListener implements Listener {

    @EventHandler
    public void onShift(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        if(player.isSneaking()) return;

        if(player.hasPermission("fireball.skills")) new Fireball(player);
    }

}
