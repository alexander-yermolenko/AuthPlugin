package com.yermolenko.authPlugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PlayerDataManager {
    private final AuthPlugin plugin;
    private File playerDataFile;
    private FileConfiguration playerData;
    private HashMap<String, Boolean> loggedInPlayers = new HashMap<>();

    public PlayerDataManager(AuthPlugin plugin) {
        this.plugin = plugin;
        setupPlayerData();
    }

    private void setupPlayerData() {
        playerDataFile = new File(plugin.getDataFolder(), "players.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.getParentFile().mkdirs();
                playerDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create players.yml: " + e.getMessage());
            }
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public void savePlayerData() {
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save players.yml: " + e.getMessage());
        }
    }

    public String getPlayerKey(Player player) {
        return player.getName();
    }

    public void registerPlayer(Player player, String password) {
        String key = getPlayerKey(player);
        String playerPath = "players." + key;
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        playerData.set(playerPath + ".password", hashedPassword);
        loggedInPlayers.put(key, false);
        savePlayerData();
    }

    public boolean isRegistered(Player player) {
        return playerData.contains("players." + getPlayerKey(player));
    }

    // New method to check registration by name
    public boolean isRegisteredByName(String playerName) {
        return playerData.contains("players." + playerName);
    }

    public boolean checkPassword(Player player, String password) {
        String key = getPlayerKey(player);
        String storedHash = playerData.getString("players." + key + ".password");
        return storedHash != null && BCrypt.checkpw(password, storedHash);
    }

    public void setLoggedIn(Player player, boolean status) {
        loggedInPlayers.put(getPlayerKey(player), status);
    }

    public boolean isLoggedIn(Player player) {
        return loggedInPlayers.getOrDefault(getPlayerKey(player), false);
    }

    public void savePlayerLocation(Player player) {
        String key = getPlayerKey(player);
        String playerPath = "players." + key + ".location";
        Location loc = player.getLocation();
        playerData.set(playerPath + ".world", loc.getWorld().getName());
        playerData.set(playerPath + ".x", loc.getX());
        playerData.set(playerPath + ".y", loc.getY());
        playerData.set(playerPath + ".z", loc.getZ());
        playerData.set(playerPath + ".yaw", loc.getYaw());
        playerData.set(playerPath + ".pitch", loc.getPitch());
        savePlayerData();
    }

    public Location getPlayerLocation(Player player) {
        String key = getPlayerKey(player);
        String playerPath = "players." + key + ".location";
        if (!playerData.contains(playerPath)) {
            return null;
        }

        String worldName = playerData.getString(playerPath + ".world");
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            return null;
        }

        double x = playerData.getDouble(playerPath + ".x");
        double y = playerData.getDouble(playerPath + ".y");
        double z = playerData.getDouble(playerPath + ".z");
        float yaw = (float) playerData.getDouble(playerPath + ".yaw");
        float pitch = (float) playerData.getDouble(playerPath + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public void changePassword(String playerName, String newPassword) {
        String playerPath = "players." + playerName;
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        playerData.set(playerPath + ".password", hashedPassword);
        savePlayerData();
    }
}
