package com.mateus.mflogger.listener;

import com.mateus.mflogger.MFLoggerPlugin;
import com.mateus.mflogger.logger.LogType;
import com.mateus.mflogger.logger.MFLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;

public class MainListener implements Listener {

    private final MFLoggerPlugin plugin;
    private final MFLogger logger;

    public MainListener(MFLoggerPlugin plugin, MFLogger logger) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.logger = logger;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (plugin.getConfig().getBoolean("logs-ativados.comandos")) {
            if (checkWhitelist(player)) return;
            logger.log(LogType.COMMAND, player, "Jogador executou o comando: \"" + event.getMessage() + "\"", System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() == InventoryType.PLAYER) return;
        if (plugin.getConfig().getBoolean("logs-ativados.comandos")) {
            Player player = (Player) event.getPlayer();
            if (checkWhitelist(player)) return;
            logger.log(LogType.INVENTORY, player, "Jogador abriu o inventário: \"" + inventory.getName() + "\"", System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getConfig().getBoolean("logs-ativados.quebrar-blocos")) {
            Player player = event.getPlayer();
            Location location = event.getBlock().getLocation();
            if (checkWhitelist(player)) return;
            logger.log(LogType.DESTROY, player, "Jogador quebrou um bloco do tipo " + event.getBlock().getType().name(), location, System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getConfig().getBoolean("logs-ativados.colocar-blocos")) {
            Player player = event.getPlayer();
            Location location = event.getBlock().getLocation();
            if (checkWhitelist(player)) return;
            logger.log(LogType.PLACE, player, "Jogador colocou um bloco do tipo " + event.getBlock().getType().name(), location, System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (plugin.getConfig().getBoolean("logs-ativados.dropar-item")) {
            Player player = event.getPlayer();
            if (checkWhitelist(player)) return;
            Item drop = event.getItemDrop();
            int count = drop.getItemStack().getAmount();
            logger.log(LogType.DROP, player, "Jogador jogou item no chão, do tipo ("  + count + "x " + drop.getItemStack().getType().name() + ")", System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.getConfig().getBoolean("logs-ativados.chat")) {
            Player player = event.getPlayer();
            if (checkWhitelist(player)) return;
            logger.log(LogType.CHAT, player, "Jogador digitou no chat: \"" + event.getMessage() + "\"", System.currentTimeMillis());
        }
    }

    private boolean checkWhitelist(Player player) {
        return plugin.getConfig().getBoolean("whitelist-ativada")
                && plugin.getConfig().getStringList("whitelist").contains(player.getName());
    }


}
