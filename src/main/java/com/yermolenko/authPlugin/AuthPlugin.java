package com.yermolenko.authPlugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class AuthPlugin extends JavaPlugin {
    private PlayerDataManager dataManager;
    private FileConfiguration messagesConfig;
    private FileConfiguration passwordsConfig; // New field

    @Override
    public void onEnable() {
        getLogger().info("AuthPlugin has been enabled!");
        loadMessagesConfig();
        loadPasswordsConfig(); // Load passwords.yml
        dataManager = new PlayerDataManager(this);
        getServer().getPluginManager().registerEvents(new AuthListener(this), this);
        AuthCommandExecutor commandExecutor = new AuthCommandExecutor(this);
        getCommand("reg").setExecutor(commandExecutor);
        getCommand("log").setExecutor(commandExecutor);
        getCommand("changepass").setExecutor(commandExecutor);
        getCommand("changeplayerpass").setExecutor(commandExecutor);
    }

    @Override
    public void onDisable() {
        dataManager.savePlayerData();
        getLogger().info("AuthPlugin has been disabled!");
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }

    private void loadMessagesConfig() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    // New method to load passwords.yml
    private void loadPasswordsConfig() {
        File passwordsFile = new File(getDataFolder(), "passwords.yml");
        if (!passwordsFile.exists()) {
            saveResource("passwords.yml", false);
        }
        passwordsConfig = YamlConfiguration.loadConfiguration(passwordsFile);
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    // New getter for passwords.yml
    public FileConfiguration getPasswordsConfig() {
        return passwordsConfig;
    }
}
