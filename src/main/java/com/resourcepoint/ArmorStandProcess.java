package com.resourcepoint;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;


import java.util.*;

public class ArmorStandProcess {
    private final JavaPlugin plugin;
    private static com.resourcepoint.ArmorStandProcess instance;
    private BukkitTask rotatingTask;
    private BukkitTask dropItemTask;
    public static BiMap<String, ItemStack> armorStandHelmet=HashBiMap.create();//自定义名称--盔甲架头盔材料对应表
    public static BiMap<String, ItemStack> armorStandItem= HashBiMap.create();//自定义名称--盔甲架掉落物品材料对应表
    public static HashMap<String,ArmorStand> armorStandList=new HashMap<>();//自定义盔甲架名称--主盔甲架实体对应表
    public static HashMap<UUID,ArmorStand> armorStandName=new HashMap<>();//主盔甲架UID--显示名称的盔甲架实体对应表
    public static HashMap<UUID,ArmorStand> subArmorStandList= new HashMap<>();//主盔甲架UID--副盔甲架实体对应表
    public static HashMap<UUID,ItemStack> dropItemList=new HashMap<>();//主盔甲架UID--掉落物品材料对应表
    public static HashMap<UUID,Integer> durList = new HashMap<>();//主盔甲架UID--物品掉落周期对应表
    public static HashMap<UUID,Integer> fullLine = new HashMap<>();//主盔甲架UID--物品掉落溢出阈值对应表
    public static HashMap<UUID,Integer> leftTimeMap = new HashMap<>();//主盔甲架UID--距离掉落物品剩余时间


    private ArmorStandProcess(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public static com.resourcepoint.ArmorStandProcess getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new com.resourcepoint.ArmorStandProcess(plugin);
        }
        return instance;
    }


    public void GenerateArmorStand(Player player, String name, String helmetName,int dur,String dropItem,int fullCount){
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0,2,0),EntityType.ARMOR_STAND);
        armorStand.setCustomName(name);
        armorStand.setCustomNameVisible(false);
        armorStand.setVisible(false);
        armorStand.setCanPickupItems(false);
        armorStand.setArms(false);
        armorStand.setBasePlate(false);
        armorStand.setMarker(true);;
        armorStand.setGravity(false);
        armorStand.setHelmet(armorStandHelmet.get(helmetName));
        UUID armorStandUid= armorStand.getUniqueId();

        String name_proc= name.replace("&","§");
        ArmorStand armorStandNameTag = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0,2.5,0),EntityType.ARMOR_STAND);
        armorStandNameTag.setCustomName(name_proc);
        armorStandNameTag.setCustomNameVisible(true);
        armorStandNameTag.setVisible(false);
        armorStandNameTag.setCanPickupItems(false);
        armorStandNameTag.setArms(false);
        armorStandNameTag.setBasePlate(false);
        armorStandNameTag.setMarker(true);;
        armorStandNameTag.setGravity(false);


        ArmorStand subArmorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0,2.1,0),EntityType.ARMOR_STAND);
        subArmorStand.setCustomName("§7距离下一次刷新还有 §e"+dur+" §7秒");
        subArmorStand.setCustomNameVisible(true);
        subArmorStand.setVisible(false);
        subArmorStand.setCanPickupItems(false);
        subArmorStand.setArms(false);
        subArmorStand.setBasePlate(false);
        subArmorStand.setMarker(true);;
        subArmorStand.setGravity(false);

        armorStandName.put(armorStandUid,armorStandNameTag);
        armorStandList.put(name,armorStand);
        subArmorStandList.put(armorStandUid,subArmorStand);
        dropItemList.put(armorStandUid,armorStandItem.get(dropItem));
        durList.put(armorStandUid,dur);
        leftTimeMap.put(armorStandUid,dur);
        fullLine.put(armorStandUid,fullCount);
    }

    public void InitHashMap(){
        ItemStack chest = new ItemStack(Material.CHEST);
        armorStandHelmet.put("chest",chest);
        ItemStack ender_chest = new ItemStack(Material.ENDER_CHEST);
        armorStandHelmet.put("ender_chest",ender_chest);
        ItemStack iron_block = new ItemStack(Material.IRON_BLOCK);
        armorStandHelmet.put("iron_block",iron_block);
        ItemStack gold_block = new ItemStack(Material.GOLD_BLOCK);
        armorStandHelmet.put("gold_block",gold_block);
        ItemStack diamond_block = new ItemStack(Material.DIAMOND_BLOCK);
        armorStandHelmet.put("diamond_block",diamond_block);
        ItemStack emerald_block = new ItemStack(Material.EMERALD_BLOCK);
        armorStandHelmet.put("emerald_block",emerald_block);
        ItemStack glass = new ItemStack(Material.GLASS);
        armorStandHelmet.put("glass",glass);



        ItemStack herb = new ItemStack(Material.POTION, 1,(short)8197);
        armorStandItem.put("herb",herb);
        ItemStack fish_rod = new ItemStack(Material.FISHING_ROD);
        armorStandItem.put("fishing_rod",fish_rod);
        ItemStack arrow = new ItemStack(Material.ARROW);
        armorStandItem.put("arrow",arrow);
        ItemStack emerald = new ItemStack(Material.EMERALD);
        armorStandItem.put("emerald",emerald);
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        armorStandItem.put("diamond",diamond);
        ItemStack gold = new ItemStack(Material.GOLD_INGOT);
        armorStandItem.put("gold",gold);
        ItemStack iron = new ItemStack(Material.IRON_INGOT);
        armorStandItem.put("iron",iron);
    }

    public void StartRotateHelmet(){
        rotatingTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (ArmorStand armorStand : armorStandList.values()) {
                    if (armorStand != null && !armorStand.isDead()) {
                        // 获取当前头盔的旋转角度
                        EulerAngle headPose = armorStand.getHeadPose();
                        double currentYaw = headPose.getY();
                        // 增加旋转角度
                        double newYaw = (currentYaw + Math.toRadians(2.88)) % (2 * Math.PI);
                        // 设置新的旋转角度
                        armorStand.setHeadPose(new EulerAngle(headPose.getX(), newYaw, headPose.getZ()));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L); // 每秒运行一次 (20 ticks)
    }

    public void DropItemsTask() {
        dropItemTask =new BukkitRunnable() {
            @Override
            public void run() {
                for (ArmorStand armorStand : armorStandList.values()) {
                    Location loc = armorStand.getLocation().add(0,-2,0);
                    UUID armorStandUid = armorStand.getUniqueId();
                    ArmorStand subArmorStand= subArmorStandList.get(armorStandUid);
                    int leftTime = leftTimeMap.get(armorStandUid);
                    if((leftTime -1)>=0) {
                        int leftTime_new = leftTime -1;
                        leftTimeMap.put(armorStandUid,leftTime_new);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if(!isItemsFull(loc,3,fullLine.get(armorStandUid))) {
                                    subArmorStand.setCustomName("§7距离下一次刷新还有 §e" + leftTime_new + " §7秒");
                                }
                                else {
                                    subArmorStand.setCustomName("§c资源点满啦=w=");
                                }
                            }
                        }.runTask(plugin);
                    }
                    else {
                        int leftTime_new = durList.get(armorStandUid);
                        leftTimeMap.put(armorStandUid,leftTime_new);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if(!isItemsFull(loc,3,fullLine.get(armorStandUid))) {
                                    subArmorStand.setCustomName("§7距离下一次刷新还有 §e"+leftTime_new+" §7秒");
                                    Item droppedItem= armorStand.getWorld().dropItem(loc, dropItemList.get(armorStandUid));
                                    droppedItem.setVelocity(new Vector(0,0.2,0));
                                }
                                else {
                                    subArmorStand.setCustomName("§c资源点满啦=w=");
                                }
                            }
                        }.runTask(plugin);
                    }
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 0L, 20L);
    }


    public void CancelTasks(){
        rotatingTask.cancel();
        dropItemTask.cancel();
    }

    public void DeleteAllArmorStand(){
        for(String name : armorStandList.keySet()){
            ArmorStand armorStand = armorStandList.get(name);
            UUID armorStandUid = armorStand.getUniqueId();
            ArmorStand subArmorStand = subArmorStandList.get(armorStandUid);
            ArmorStand armorStandNameTag = armorStandName.get(armorStandUid);
            if(!armorStand.isDead()) {
                armorStand.remove();
            }
            if(!subArmorStand.isDead()) {
               subArmorStand.remove();
            }
            if(!armorStandNameTag.isDead()) {
                armorStandNameTag.remove();
            }
        }
    }


    public boolean DeleteArmorStand(String name){
        ArmorStand armorStand = armorStandList.get(name);
        armorStandList.remove(name);
        UUID armorStandUid = armorStand.getUniqueId();
        ArmorStand subArmorStand = subArmorStandList.get(armorStandUid);
        ArmorStand armorStandNameTag = armorStandName.get(armorStandUid);
        subArmorStandList.remove(armorStandUid);
        dropItemList.remove(armorStandUid);
        armorStandName.remove(armorStandUid);
        leftTimeMap.remove(armorStandUid);
        durList.remove(armorStandUid);
        if(!armorStand.isDead()) {
            armorStand.remove();
        }
        if(!subArmorStand.isDead()) {
            subArmorStand.remove();
        }
        if(!armorStandNameTag.isDead()) {
            armorStandNameTag.remove();
        }
        return true;
    }

    public void ShowAllArmorStand(Player player){
        Set armorStandNames= armorStandList.keySet();
        String result_raw = String.join(",",armorStandNames);
        String result_final = "§b当前存在的资源点:§f"+result_raw;
        player.sendMessage(result_final);
    }


    public static ArmorStand GetEntityByUid(UUID uuid){
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand && entity.getUniqueId().equals(uuid)) {
                    return (ArmorStand) entity;
                }
            }
        }
        return null;
    }




    public boolean isItemsFull(Location loc, double r,int fL){
        int count = 0;
        World world = loc.getWorld();
        for (Entity entity : world.getNearbyEntities(loc,r,r,r)) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                count += item.getItemStack().getAmount();
            }
        }

        if(count>=fL){
            return true;
        }
        else {
            return false;
        }
    }





}
