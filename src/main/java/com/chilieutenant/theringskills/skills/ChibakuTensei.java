package com.chilieutenant.theringskills.skills;

import com.chilieutenant.theringskillsapi.GeneralMethods;
import com.chilieutenant.theringskillsapi.MainAbility;
import com.chilieutenant.theringskillsapi.TempBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChibakuTensei extends MainAbility {

    private double size = 0.1;
    private List<FallingBlock> fbs = new ArrayList<FallingBlock>();
    private Location targetLoc;
    private List<Material> types = new ArrayList<Material>();
    private List<TempBlock> tbs = new ArrayList<TempBlock>();
    private List<FallingBlock> goingToRemove = new ArrayList<FallingBlock>();
    private String mode;

    public ChibakuTensei(Player player) {
        super(player);
        if(hasAbility(player, ChibakuTensei.class)) {
            return;
        }
        if(aPlayer.isOnCooldown(this)) {
            return;
        }
        types.add(Material.STONE);
        targetLoc = player.getLocation().add(0, 35, 0);
        mode = "Topla";
        start();
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String s) {
        this.mode = s;
    }

    @Override
    public String getName() {
        return "ChibakuTensei";
    }

    @Override
    public long getCooldown() {
        return 5000;
    }

    int i = 0;
    int step = 0;
    @Override
    public void progress() {
        i++;
        if(mode.equalsIgnoreCase("Bitir")) {
            for(FallingBlock fb : fbs) {
                fb.remove();
            }
            for(TempBlock tbl : tbs) {
                tbl.revertBlock();
            }
            aPlayer.addCooldown(this);
            remove();
            return;
        }
        if(mode.equalsIgnoreCase("Topla")) {
            for(Entity e : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), 10)) {
                if(e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId() && !(e instanceof FallingBlock)) {
                    e.setVelocity(targetLoc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.4));
                }
            }
            for(Entity e : GeneralMethods.getEntitiesAroundPoint(targetLoc, 30)) {
                if(e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId() && !(e instanceof FallingBlock)) {
                    e.setVelocity(targetLoc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.4));
                }
            }
            for(Block b : GeneralMethods.getBlocksAroundPoint(targetLoc, size)) {
                if(b.getType() == Material.AIR) {
                    Random random = new Random();
                    TempBlock tbl = new TempBlock(b, types.get(random.nextInt(types.size())));
                    tbs.add(tbl);
                }
            }
            if(i % 15 == 0) {
                for(Block b : GeneralMethods.getBlocksAroundPoint(player.getLocation(), 10)) {
                    if(b.getType() != Material.AIR) {
                        if(b.getLocation().distance(player.getLocation()) > 4 && fbs.size() < 250) {
                            Block block = GeneralMethods.getTopBlock(b.getLocation(), 10);
                            if(block.getType() == Material.GRASS) {
                                block.breakNaturally();
                                block = block.getRelative(BlockFace.DOWN);
                            }
                            if(block.getType() == Material.TALL_GRASS) {
                                block.breakNaturally();
                                block = block.getRelative(BlockFace.DOWN, 2);
                            }
                            if(block != null && block.getType().isSolid() && !block.isPassable()) {
                                Material type = block.getType();
                                types.add(block.getType());
                                TempBlock tbl = new TempBlock(block, Material.AIR);
                                tbs.add(tbl);
                                FallingBlock fb = player.getWorld().spawnFallingBlock(block.getLocation().add(0, 0.1, 0), Bukkit.createBlockData(type));
                                fb.setGravity(false);
                                fb.setInvulnerable(true);
                                fb.setDropItem(false);
                                fb.setCustomName("chibaku");
                                fbs.add(fb);
                            }
                        }
                    }
                }
            }
            for(FallingBlock fb : fbs) {
                if(fb.getLocation().distance(targetLoc) < 1.5 + (size*1.5)) {
                    goingToRemove.add(fb);
                    types.add(fb.getBlockData().getMaterial());
                    fb.remove();
                    size+=0.005;
                }else {
                    fb.setVelocity(targetLoc.toVector().subtract(fb.getLocation().toVector()).normalize().multiply(0.4));
                }
            }
            for(int i = goingToRemove.size()-1; i >= 0; i--) {
                fbs.remove(goingToRemove.get(i));
                goingToRemove.remove(goingToRemove.get(i));
            }
            if(size > 20) {
                setMode("Mühür");
                for(FallingBlock fb : fbs) {
                    fb.remove();
                }
            }
        }
        if(mode.equalsIgnoreCase("Mühür")) {
            if(i % 20 == 0) {
                step++;
            }
            if(step == 1) {
                for(Block b : GeneralMethods.getBlocksAroundPoint(targetLoc, size)) {
                    TempBlock tbl = new TempBlock(b, Material.STONE);
                    tbs.add(tbl);
                }
            }
            if(step == 2) {
                for(Block b : GeneralMethods.getBlocksAroundPoint(targetLoc, size)) {
                    TempBlock tbl = new TempBlock(b, Material.STONE_BRICKS);
                    tbs.add(tbl);
                }
            }
            if(step == 3) {
                for(Block b : GeneralMethods.getBlocksAroundPoint(targetLoc, size)) {
                    TempBlock tbl = new TempBlock(b, Material.BLACKSTONE);
                    tbs.add(tbl);
                }
            }

        }

    }

}
