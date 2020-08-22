package com.mateus.mflogger.command;

import com.mateus.mflogger.MFLoggerPlugin;
import com.mateus.mflogger.logger.MFLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LogCommand implements CommandExecutor {

    private final MFLogger logger;

    public LogCommand(MFLoggerPlugin plugin, MFLogger logger) {
        plugin.getCommand("getLogs").setExecutor(this);
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("mflogger.admin")) {
                player.sendMessage(ChatColor.RED + "Você não tem permissão de executar este comando.");
            } else {
                List<String> logs;
                if (args.length > 0 && isInteger(args[0])) {
                    int range = Integer.parseInt(args[0]);
                    logs = logger.getLogsByLocation(player.getLocation(), range);
                } else {
                    logs = logger.getLogsByLocation(player.getLocation(), 10);
                }
                if (logs.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "Não existe nada logado nessa região...");
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String log : logs) {
                        stringBuilder.append(log).append("\n");
                    }
                    player.sendMessage(stringBuilder.substring(0, stringBuilder.length() - 1));
                }
            }
        }
        return false;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
