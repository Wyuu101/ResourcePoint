package com.resourcepoint;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;


public class ConfigFile {
    private static com.resourcepoint.ConfigFile instance;
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private Logger logger;

    private ConfigFile(JavaPlugin plugin) {
        this.plugin = plugin;
        logger=plugin.getLogger();
        reloadConfig();
    }

    public static com.resourcepoint.ConfigFile getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new com.resourcepoint.ConfigFile(plugin);
        }
        return instance;
    }

    public void reloadConfig() {
        File pluginFolder = plugin.getDataFolder().getParentFile();
        File testFolder = new File(pluginFolder, "ResourcePoint");
        if (!testFolder.exists()) {
            testFolder.mkdirs();
        }
        File configFile = new File(testFolder, "config.yml");
        if (!configFile.exists()) {
            try {
                InputStream inputStream = plugin.getResource("config.yml");
                Files.copy(inputStream, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ResourcePoint] 配置文件释放失败！");
            }
            Bukkit.getServer().getConsoleSender().sendMessage("[ResourcePoint] 默认配置文件已释放");
        }

        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(configFile);
        config = customConfig;
    }


    public FileConfiguration getConfig() {
        return config;
    }
}