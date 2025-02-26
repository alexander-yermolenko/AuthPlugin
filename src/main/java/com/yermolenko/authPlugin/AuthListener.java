package com.yermolenko.authPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AuthListener implements Listener {
    private final AuthPlugin plugin;

    public AuthListener(AuthPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager dataManager = plugin.getDataManager();

        if (!dataManager.isRegistered(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("join-new-player")));
        } else {
            Location lastLocation = dataManager.getPlayerLocation(player);
            if (lastLocation != null) {
                player.teleport(lastLocation);
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("join-returning-player")));
        }
        dataManager.setLoggedIn(player, false);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager dataManager = plugin.getDataManager();

        if (!dataManager.isLoggedIn(player)) {
            event.setCancelled(true);
            if (!dataManager.isRegistered(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("move-not-registered")));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("move-not-logged-in")));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager dataManager = plugin.getDataManager();
        if (dataManager.isLoggedIn(player)) {
            dataManager.savePlayerLocation(player);
            dataManager.setLoggedIn(player, false);
        }
    }
}
