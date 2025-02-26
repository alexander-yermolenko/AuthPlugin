package com.yermolenko.authPlugin;

import org.bukkit.ChatColor;
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
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("only-players")));
            return true;
        }

        Player player = (Player) sender;
        PlayerDataManager dataManager = plugin.getDataManager();
        int minLength = plugin.getPasswordsConfig().getInt("min-length", 6);
        int maxLength = plugin.getPasswordsConfig().getInt("max-length", 25);

        if (command.getName().equalsIgnoreCase("reg")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("reg-usage")));
                return true;
            }

            String password = args[0];
            if (password.length() < minLength || password.length() > maxLength) {
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("password-length-invalid", "&cPassword must be between %min% and %max% characters!"))
                        .replace("%min%", String.valueOf(minLength))
                        .replace("%max%", String.valueOf(maxLength));
                player.sendMessage(message);
                return true;
            }

            if (dataManager.isRegistered(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("reg-already-registered")));
                return true;
            }

            dataManager.registerPlayer(player, password);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("reg-success")));
            return true;
        }

        if (command.getName().equalsIgnoreCase("log")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("log-usage")));
                return true;
            }

            if (!dataManager.isRegistered(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("log-not-registered")));
                return true;
            }

            if (dataManager.isLoggedIn(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("log-already-logged-in")));
                return true;
            }

            if (dataManager.checkPassword(player, args[0])) {
                dataManager.setLoggedIn(player, true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("log-success")));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("log-wrong-password")));
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("changepass")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("changepass-usage")));
                return true;
            }

            if (!dataManager.isLoggedIn(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("not-logged-in")));
                return true;
            }

            if (!args[0].equals(args[1])) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("passwords-dont-match")));
                return true;
            }

            String newPassword = args[0];
            if (newPassword.length() < minLength || newPassword.length() > maxLength) {
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("password-length-invalid", "&cPassword must be between %min% and %max% characters!"))
                        .replace("%min%", String.valueOf(minLength))
                        .replace("%max%", String.valueOf(maxLength));
                player.sendMessage(message);
                return true;
            }

            dataManager.changePassword(player.getName(), newPassword);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("changepass-success")));
            return true;
        }

        if (command.getName().equalsIgnoreCase("changeplayerpass")) {
            if (!player.hasPermission("authplugin.admin")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("no-permission")));
                return true;
            }

            if (args.length != 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("changeplayerpass-usage")));
                return true;
            }

            String targetPlayer = args[0];
            String newPassword = args[1];
            if (newPassword.length() < minLength || newPassword.length() > maxLength) {
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("password-length-invalid", "&cPassword must be between %min% and %max% characters!"))
                        .replace("%min%", String.valueOf(minLength))
                        .replace("%max%", String.valueOf(maxLength));
                player.sendMessage(message);
                return true;
            }

            if (!dataManager.isRegisteredByName(targetPlayer)) {
                String message = ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("player-not-registered")).replace("%player%", targetPlayer);
                player.sendMessage(message);
                return true;
            }

            dataManager.changePassword(targetPlayer, newPassword);
            String message = ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getString("changeplayerpass-success")).replace("%player%", targetPlayer);
            player.sendMessage(message);
            return true;
        }

        return false;
    }
}
