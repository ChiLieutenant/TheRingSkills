package com.chilieutenant.theringskills.skills;

import com.chilieutenant.theringskillsapi.MainAbility;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class MainListener implements Listener {

    @EventHandler
    public void onShift(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        if(player.isSneaking()) return;

        if(player.getName().equalsIgnoreCase("ChiLieutenant")) new Rasengan(player);
        if(player.getName().equalsIgnoreCase("RaiTaki") || player.getName().equalsIgnoreCase("dukeflacko")) new Chidori(player);
    }

    @EventHandler
    public void onLeft(PlayerInteractEvent event){
        if(event.getAction().equals(Action.LEFT_CLICK_AIR)){
            if(event.getPlayer().getName().equalsIgnoreCase("ChiLieutenant") || event.getPlayer().getName().equalsIgnoreCase("RaiTaki")) new Shuriken(event.getPlayer());
        }
    }

    @EventHandler
    public void onClick1(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }

        if(MainAbility.hasAbility(player, ChibakuTensei.class)) {
            ChibakuTensei ct = MainAbility.getAbility(player, ChibakuTensei.class);
            if(player.isSneaking()) {
                ct.setMode("Bitir");
                return;
            }
            if(ct.getMode().equalsIgnoreCase("Topla")) {
                ct.setMode("Mühür");
            }
        }
    }

    @EventHandler
    public void onLand(EntityChangeBlockEvent event) {
        if(event.getEntityType() == EntityType.FALLING_BLOCK) {
            if(event.getEntity().getCustomName() != null) {
                if(event.getEntity().getCustomName().equalsIgnoreCase("chibaku")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
