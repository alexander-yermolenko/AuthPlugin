package com.yermolenko.authPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public class AuthPlugin extends JavaPlugin {
    private PlayerDataManager dataManager;

    @Override
    public void onEnable() {
        getLogger().info("AuthPlugin has been enabled!");
        saveDefaultConfig(); // Save default config.yml if it doesn’t exist
        dataManager = new PlayerDataManager(this);
        getServer().getPluginManager().registerEvents(new AuthListener(this), this);
        AuthCommandExecutor commandExecutor = new AuthCommandExecutor(this);
        getCommand("reg").setExecutor(commandExecutor);
        getCommand("log").setExecutor(commandExecutor);
    }

    @Override
    public void onDisable() {
        dataManager.savePlayerData();
        getLogger().info("AuthPlugin has been disabled!");
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }
}