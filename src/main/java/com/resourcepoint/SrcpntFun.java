package com.resourcepoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SrcpntFun implements CommandExecutor {
    private final JavaPlugin plugin;
    public static ArmorStandProcess armorStandProcess;


    public SrcpntFun(JavaPlugin plugin) {
        this.plugin = plugin;
        armorStandProcess = ArmorStandProcess.getInstance(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§b§l资源点>>§c该指令只能由实体权限玩家执行");
            return true;
        }
        Player p = (Player) sender;
        if (!(p.isOp())) {
            p.sendMessage("§b§l资源点>>§c你没有执行此命令的权限");
            return true;
        }
        if (args.length < 1) {
            p.sendMessage("§b§l资源点>>§c传入参数过少,输入/srcpnt help查看帮助");
            return true;
        } else {
            if (args[0].equals("list")) {
                armorStandProcess.ShowAllArmorStand(p);
            }
            else if (args[0].equals("create")) {
                if (args.length < 5||args.length>6) {
                    p.sendMessage("§b§l资源点>>§c参数错误,输入/srcpnt help查看帮助");
                    return true;
                } else {
                    if (armorStandProcess.armorStandList.containsKey(args[1])) {
                        p.sendMessage("§b§l资源点>>§c“" + args[1] + "”已存在");
                        return true;
                    }
                    if (!(armorStandProcess.armorStandHelmet.containsKey(args[2]))) {
                        p.sendMessage("§b§l资源点>>§c错误的头盔类型");
                        return true;
                    }
                    if (!(armorStandProcess.armorStandItem.containsKey(args[4]))) {
                        p.sendMessage("§b§l资源点>>§c错误的掉落物类型");
                        return true;
                    }
                    int dur = 10;
                    try {
                        Integer.parseInt(args[3]);
                        if (dur <= 0) {
                            p.sendMessage("§b§l资源点>>§c请输入正确的刷新周期，必须为正整数");
                            return true;
                        }


                    } catch (NumberFormatException e) {
                        p.sendMessage("§b§l资源点>>§c请输入正确的刷新周期，必须为正整数");
                        return true;
                    }
                    int fullCount = 64;
                    if(args.length==5) {

                    }
                    else if(args.length==6) {
                        try {
                            fullCount = Integer.parseInt(args[5]);
                            if (fullCount <= 0) {
                                p.sendMessage("§b§l资源点>>§c请输入正确的溢出阈值，必须为正整数");
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            p.sendMessage("§b§l资源点>>§c请输入正确的刷新周期，必须为正整数");
                            return true;
                        }
                    }
                    armorStandProcess.GenerateArmorStand(p, args[1], args[2], dur, args[4],fullCount);
                    p.sendMessage("§b§l资源点>>§a资源点创建成功!");
                }
            } else if (args[0].equals("delete")) {
                if(args.length!=2){
                    p.sendMessage("§b§l资源点>>§c请正确输入要删除的资源点名称~");
                    return true;
                }
                if(armorStandProcess.armorStandList.containsKey(args[1])){
                    armorStandProcess.DeleteArmorStand(args[1]);
                    p.sendMessage("§b§l资源点>>§a资源点“"+args[1]+"”已删除！");
                    return true;
                }
                else {
                    p.sendMessage("§b§l资源点>>§c不存在名为“"+args[1] +"”的资源点");
                    return true;
                }
            }else if (args[0].equals("help")) {
                if(args.length!=2){
                    p.sendMessage("§b§l资源点指令帮助 >>\n");
                    p.sendMessage("-§a/srcpnt list    §e列出当前所有资源点\n");
                    p.sendMessage("-§a/srcpnt delete [资源点名称]    §e删除指定资源点\n");
                    p.sendMessage("-§a/srcpnt create [名称] [旋转物品] [刷新周期] [掉落物品]    §e创建资源点\n");
                    return true;
                }

            } else {
                p.sendMessage("§b§l资源点>>§c二级指令错误");
                return true;
            }
        }
    return true;
    }
}