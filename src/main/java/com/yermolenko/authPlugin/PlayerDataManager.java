package com.yermolenko.authPlugin;

import org.bukkit.entity.Player;
import java.util.HashMap;

public class PlayerDataManager {
    private final AuthPlugin plugin;
    private final HashMap<String, Boolean> loggedInPlayers = new HashMap<>();

    public PlayerDataManager(AuthPlugin plugin) {
        this.plugin = plugin;
    }

    public void savePlayerData() {
        try {
            plugin.saveConfig(); // Save the config.yml file
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config.yml: " + e.getMessage());
        }
    }

    public String getPlayerKey(Player player) {
        return player.getName(); // Use username only
    }

    public void registerPlayer(Player player, String password) {
        String key = getPlayerKey(player);
        String playerPath = "players." + key;
        plugin.getConfig().set(playerPath + ".password", password);
        loggedInPlayers.put(key, false); // Set as not logged in after registration
        savePlayerData();
    }

    public boolean isRegistered(Player player) {
        return plugin.getConfig().contains("players." + getPlayerKey(player));
    }

    public boolean checkPassword(Player player, String password) {
        String key = getPlayerKey(player);
        String storedPassword = plugin.getConfig().getString("players." + key + ".password");
        return storedPassword != null && storedPassword.equals(password);
    }

    public void setLoggedIn(Player player, boolean status) {
        loggedInPlayers.put(getPlayerKey(player), status);
    }

    public boolean isLoggedIn(Player player) {
        return loggedInPlayers.getOrDefault(getPlayerKey(player), false);
    }
}