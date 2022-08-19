package com.chilieutenant.theringskills.skills;

import com.chilieutenant.theringskillsapi.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Chidori extends MainAbility {

    private long time;
    private Location righthand;

    public Chidori(Player player) {
        super(player);

        if(hasAbility(player, Chidori.class)) {
            return;
        }
        if(aPlayer.isOnCooldown(this)) {
            return;
        }
        time = System.currentTimeMillis();
        start();
    }

    public Location randomSphereLoc(Location l, int radius) {
        int i = Utils.getCircle(l, radius, 0, true, true, 0).size() - 1;
        Random rnd = new Random();
        return Utils.getCircle(l, radius, 0, true, true, 0).get(rnd.nextInt(i));
    }

    public void chidoriParticleOut(Location l, int radius) {
        Location loc = l.clone();
        Location randomloc = randomSphereLoc(loc, radius);
        Vector dir = randomloc.toVector().subtract(loc.toVector()).normalize().multiply(radius*0.06);
        new BukkitRunnable() {
            @Override
            public void run() {
                loc.add(dir);
                ParticleEffect.REDSTONE.display(loc, 2, Math.random()/8, Math.random()/8, Math.random()/8, 0.005, new Particle.DustOptions(Color.fromRGB(255, 225, 255), 0.3F));
                ParticleEffect.REDSTONE.display(loc, 2, Math.random()/8, Math.random()/8, Math.random()/8, 0.005, new Particle.DustOptions(Color.fromRGB(1, 225, 255), 0.6F));
                if(loc.distance(randomloc) < 0.5) {
                    this.cancel();
                }
            }

        }.runTaskTimer(main.getInstance(), 0, 1);
    }
    public void chidoriParticleIn(Location l, int radius) {
        Location loc = l.clone();
        Location randomloc = randomSphereLoc(loc, radius);
        Vector dir = loc.toVector().subtract(randomloc.toVector()).normalize().multiply(radius*0.06);
        new BukkitRunnable() {
            @Override
            public void run() {
                randomloc.add(dir);
                ParticleEffect.REDSTONE.display(randomloc, 2, Math.random()/8, Math.random()/8, Math.random()/8, 0.005, new Particle.DustOptions(Color.fromRGB(1, 225, 255), 0.6F));
                ParticleEffect.REDSTONE.display(randomloc, 2, Math.random()/8, Math.random()/8, Math.random()/8, 0.005, new Particle.DustOptions(Color.fromRGB(255, 225, 255), 0.3F));
                if(loc.distance(randomloc) < 0.5) {
                    this.cancel();
                }
            }

        }.runTaskTimer(main.getInstance(), 0, 1);
    }

    @Override
    public String getName() {
        return "Chidori";
    }

    @Override
    public long getCooldown() {
        return 10000;
    }

    public void interaction(Location loc) {
        ParticleEffect.EXPLOSION_HUGE.display(loc, 5, 1, 1, 1);
        ParticleEffect.CLOUD.display(loc, 100, 1, 1, 1);
        for(Entity e : GeneralMethods.getEntitiesAroundPoint(loc, 3.5)) {
            if(e instanceof LivingEntity) {
                DamageHandler.damageEntity(e, 8, this);
                Vector dir = loc.toVector().subtract(e.getLocation().toVector()).multiply(-3);
                dir.setY(1);
                e.setVelocity(dir);
            }
        }
    }

    int i = 0;
    @Override
    public void progress() {
        i++;
        Location offset1 = Rasengan.getRightSide(player.getLocation(), .55).add(0, 1.2, 0);
        Vector dir1 = player.getEyeLocation().getDirection();
        righthand = offset1.toVector().add(dir1.clone().multiply(.8D)).toLocation(player.getWorld());

        if(System.currentTimeMillis() < time + 1500) {
            if(!player.isSneaking()) {
                remove();
                return;
            }else {
                chidoriParticleIn(righthand, 2);
            }
        }else {
            if(System.currentTimeMillis() > time + 9000) {
                aPlayer.addCooldown(this);
                remove();
                return;
            }
            chidoriParticleOut(righthand, 3);
            ParticleEffect.REDSTONE.display(righthand, 5, 0.2, 0.2,0.2, 0.005, new Particle.DustOptions(Color.fromRGB(1, 225, 255), 1));
            for(Entity e : GeneralMethods.getEntitiesAroundPoint(righthand, 1)) {
                if(e.getUniqueId() != player.getUniqueId() && e instanceof LivingEntity) {
                    e.getLocation().getWorld().playSound(e.getLocation(), Sound.ENTITY_CREEPER_HURT, 1f, 1f);
                    aPlayer.addCooldown(this);
                    if(e instanceof Player){
                        Player player1 = (Player) e;
                        player1.sendMessage("aaa");
                        if(hasAbility(player1, Rasengan.class)){
                            player1.sendMessage("bbb");
                            interaction(player1.getLocation());
                            remove();
                            return;
                        }
                    }
                    DamageHandler.damageEntity(e, 10, this);
                    remove();
                    return;
                }
            }
        }

    }
}
