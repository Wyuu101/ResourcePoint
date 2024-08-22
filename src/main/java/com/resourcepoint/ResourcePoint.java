package com.resourcepoint;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class ResourcePoint extends JavaPlugin {
    public final Logger logger= getLogger();
    public static ArmorStandProcess armorStandProcess;
    private static ConfigFile mangerConInstance;
    private FileConfiguration config;
    private DatabaseManager databaseManager;
    private boolean enableSQLite;


    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("srcpnt").setExecutor(new SrcpntFun(this));
        getCommand("srcpnt").setTabCompleter(new CommandCompleter());
        armorStandProcess = ArmorStandProcess.getInstance(this);
        armorStandProcess.InitHashMap();

        logger.info("===========ResourcePoint===========");
        logger.info("Author: X_32mx");
        logger.info("QQ: 2644489337");
        mangerConInstance= ConfigFile.getInstance(this );
        config= mangerConInstance.getConfig();
        databaseManager = new DatabaseManager(getDataFolder() + "/database.db");
        enableSQLite = config.getBoolean("Enable_SQLite",false);

        if(enableSQLite){
            logger.info("SQLite: enabled");
            try {
                databaseManager.openConnection();
                DatabaseOpe.InitializeDatabase(databaseManager);
                databaseManager.openConnection();
                DatabaseOpe.ReloadAllArmorStands(databaseManager);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            logger.info("资源点已重启");
        }
        else {
            try {
                logger.info("SQLite: disabled");
                databaseManager.openConnection();
                DatabaseOpe.InitializeDatabase(databaseManager);
                databaseManager.openConnection();
                DatabaseOpe.ClearTable(databaseManager);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            logger.info("资源点数据库已清除");
        }
        armorStandProcess.StartRotateHelmet();
        armorStandProcess.DropItemsTask();
        logger.info("==================================");
    }

    @Override
    public void onDisable() {
        armorStandProcess.CancelTasks();
        if(enableSQLite){
            try {
                databaseManager.openConnection();
                DatabaseOpe.ClearTable(databaseManager);
                databaseManager.openConnection();
                DatabaseOpe.SaveArmorStandData(databaseManager);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            logger.info("资源点数据已保存");
        }
        else {
            armorStandProcess.DeleteAllArmorStand();
            logger.info("资源点已清除");
        }
    }
}
