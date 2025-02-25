package com.yermolenko.authPlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

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
            player.sendMessage(ChatColor.YELLOW + "Welcome! Please register using /reg [password]");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Welcome back! Please login using /log [password]");
        }
        dataManager.setLoggedIn(player, false); // Ensure player starts as not logged in
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager dataManager = plugin.getDataManager();

        if (!dataManager.isLoggedIn(player)) {
            event.setCancelled(true); // Disable movement until logged in
            if (!dataManager.isRegistered(player)) {
                player.sendMessage(ChatColor.RED + "Please register first using /reg [password]");
            } else {
                player.sendMessage(ChatColor.RED + "Please login using /log [password]");
            }
        }
    }
}
