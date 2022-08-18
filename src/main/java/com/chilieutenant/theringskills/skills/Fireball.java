package com.chilieutenant.theringskills.skills;

import com.chilieutenant.theringskillsapi.DamageHandler;
import com.chilieutenant.theringskillsapi.GeneralMethods;
import com.chilieutenant.theringskillsapi.MainAbility;
import com.chilieutenant.theringskillsapi.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.util.Vector;

public class Fireball extends MainAbility {

    private Location location;
    private Location origin;
    private Vector dir;

    public Fireball(Player player) {
        super(player);

        if(hasAbility(player, Fireball.class)){
            return;
        }
        if(aPlayer.isOnCooldown(this)){
            player.sendMessage(ChatColor.RED + "Bekleme süresinde");
            return;
        }

        location = player.getEyeLocation();
        origin = location.clone();
        dir = location.getDirection();

        aPlayer.addCooldown(this);
        start();
    }

    @Override
    public String getName() {
        return "Fireball";
    }

    @Override
    public long getCooldown() {
        return 10000;
    }

    @Override
    public void progress() {
        location.add(dir.normalize().multiply(0.4));
        ParticleEffect.FLAME.display(location, 40, 1, 1, 1, 0.005);
        for(Entity e : GeneralMethods.getEntitiesAroundPoint(location, 1.5)){
            if(e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId()){
                DamageHandler.damageEntity(e, 10, this);
                e.setFireTicks(60);
                remove();
                return;
            }
        }

        if(location.distance(origin) > 20 || !location.getBlock().isPassable()){
            remove();
        }
    }
}
