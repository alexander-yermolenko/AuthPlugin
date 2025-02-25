package com.yermolenko.authPlugin;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuthCommandExecutor implements CommandExecutor {
    private final AuthPlugin plugin;

    public AuthCommandExecutor(AuthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.only-players")));
            return true;
        }

        Player player = (Player) sender;
        PlayerDataManager dataManager = plugin.getDataManager();

        if (command.getName().equalsIgnoreCase("reg")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.reg-usage")));
                return true;
            }

            if (dataManager.isRegistered(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.reg-already-registered")));
                return true;
            }

            dataManager.registerPlayer(player, args[0]);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.reg-success")));
            return true;
        }

        if (command.getName().equalsIgnoreCase("log")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.log-usage")));
                return true;
            }

            if (!dataManager.isRegistered(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.log-not-registered")));
                return true;
            }

            if (dataManager.isLoggedIn(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.log-already-logged-in")));
                return true;
            }

            if (dataManager.checkPassword(player, args[0])) {
                dataManager.setLoggedIn(player, true);
                World mainWorld = plugin.getServer().getWorld("world");
                if (mainWorld != null) {
                    player.teleport(mainWorld.getSpawnLocation());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.log-success")));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.log-main-world-not-found")));
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.log-wrong-password")));
            }
            return true;
        }

        return false;
    }
}
