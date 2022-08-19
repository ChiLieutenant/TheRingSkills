package com.chilieutenant.theringskills.skills;

import com.chilieutenant.theringskillsapi.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Rasengan extends MainAbility {

    private long time;
    private long vurtime;
    private Vector dir;
    private double size2;
    private int rotation2;
    private long regenTime = 6000;
    private double speed = 5;
    private double radius = 2.5D;
    private RegenTempBlock temp;
    Random rand = new Random();

    public Rasengan(Player player) {
        super(player);

        if(hasAbility(player, Rasengan.class)) {
            return;
        }
        if(aPlayer.isOnCooldown(this)) {
            return;
        }

        time = System.currentTimeMillis();
        start();
    }
    private void slamGround(Material material, Vector direction, Location loc) {
        for (Block block : GeneralMethods.getBlocksAroundPoint(loc, this.radius)) {
            for (int i = 0; i < 1; i++) {
                double x = this.rand.nextDouble() * 3.0D;
                double z = this.rand.nextDouble() * 3.0D;
                double y = this.rand.nextDouble() * 3.0D;

                x = this.rand.nextBoolean() ? x : -x;
                z = this.rand.nextBoolean() ? z : -z;
                y = this.rand.nextBoolean() ? y : -y;

                if (material != Material.BEDROCK) {
                    new TempFallingBlock(block.getLocation(), block.getBlockData(), direction.clone().add(new Vector(x, y, z)).normalize().multiply(0.9D), this);
                } else {
                    return;
                }
            }

            this.temp = new RegenTempBlock(block, Material.AIR, block.getBlockData(), this.regenTime);
            ParticleEffect.BLOCK_CRACK.display(block.getLocation(), 3, 0.10000000149011612D, 0.10000000149011612D, 0.10000000149011612D, 0.0D, material.createBlockData());
        }


        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc, this.radius)) {
            if (entity instanceof org.bukkit.entity.LivingEntity && entity.getUniqueId() != this.player.getUniqueId()) {
                DamageHandler.damageEntity(entity, 4, this);
            }
        }
    }
    public void circleParticle(Location loc) {
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i);
            double y = Math.cos(i)*0.2;
            for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
                double x = Math.cos(a) * radius * 0.2;
                double z = Math.sin(a) * radius * 0.2;
                loc.add(x, y, z);
                ParticleEffect.REDSTONE.display(loc, 1, 0, 0, 0, 0.005, new Particle.DustOptions(Color.AQUA, 1));
                loc.subtract(x, y, z);
            }
        }
    }

    public void vur(Entity e, Rasengan rs) {
        vurtime = System.currentTimeMillis();
        new BukkitRunnable(){
            int i = 0;
            public void run(){
                i++;
                if(i%5 == 0) {
                    circleParticle(e.getLocation().add(0, 0.8, 0));
                }
                if(System.currentTimeMillis() < vurtime + 2000) {
                    e.setVelocity(dir);
                    DamageHandler.damageEntity(e, 3, rs);
                }else {
                    this.cancel();
                }
                if(!e.getLocation().clone().add(0, 0.8, 0).add(dir.normalize().multiply(.8)).getBlock().isPassable()) {
                    slamGround(e.getLocation().clone().add(0, 0.8, 0).add(dir.normalize().multiply(.8)).getBlock().getType(), dir, e.getLocation().clone().add(0, 0.8, 0).add(dir.normalize().multiply(.8)));
                    this.cancel();
                }


            }

        }.runTaskTimer(main.getInstance(), 0, 1);

    }

    public void efekt(Location loc) {
        rotation2+=3;
        if (size2 <= 0.0D) {
            size2 = 1;
        } else {
            size2 -= 0.05D;
            for (int i = -180; i < 180; i += 90) {
                double angle = i * Math.PI / 180.0D;
                double x = size2 * Math.cos(angle + rotation2);
                double z = size2 * Math.sin(angle + rotation2);
                Location loca = loc.clone();
                loca.add(x, -0.2D, z);
                ParticleEffect.REDSTONE.display(loca, 1, 0, 0, 0, 0.0D, new Particle.DustOptions(Color.AQUA, 1));
            }


            for (int j = -180; j < 180; j += 90) {
                double angle = j * Math.PI / 180.0D;
                double x = size2 * Math.cos(angle + rotation2);
                double z = size2 * Math.sin(angle + rotation2);
                Location loca = loc.clone();
                loca.add(x, -0.2D, z);
                ParticleEffect.REDSTONE.display(loca, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.WHITE, 1));
            }
        }
    }


    @Override
    public String getName() {
        return "Rasengan";
    }

    @Override
    public long getCooldown() {
        return 10000;
    }

    public static Location getRightSide(final Location location, final double distance) {
        final float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    int i = 0;
    @Override
    public void progress() {
        i++;
        Location offset1 = getRightSide(player.getLocation(), .55).add(0, 1.2, 0);
        Vector dir1 = player.getEyeLocation().getDirection();
        Location righthand = offset1.toVector().add(dir1.clone().multiply(.8D)).toLocation(player.getWorld());

        if(System.currentTimeMillis() < time + 2000) {
            if(!player.isSneaking()) {
                remove();
                return;
            }else {
                efekt(righthand);
            }
        }else {

            if(System.currentTimeMillis() > time + 9000) {
                aPlayer.addCooldown(this);
                remove();
                return;
            }
            if(i%5 == 0) {
                circleParticle(righthand);
            }
            for(Entity e : GeneralMethods.getEntitiesAroundPoint(righthand, 1)) {
                if(e.getUniqueId() != player.getUniqueId() && e instanceof LivingEntity) {
                    aPlayer.addCooldown(this);
                    if(e instanceof Player){
                        Player player1 = (Player) e;
                        if(hasAbility(player1, Chidori.class)){
                            remove();
                            return;
                        }
                    }
                    dir = player.getLocation().getDirection();
                    vur(e, this);
                    remove();
                    return;
                }

            }
        }

    }
}
