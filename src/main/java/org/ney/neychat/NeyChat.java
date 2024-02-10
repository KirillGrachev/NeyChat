package org.ney.neychat;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.logging.Level;

public final class NeyChat extends JavaPlugin implements Listener {

    private double localchatradius;
    @Override
    public void onEnable() {
        saveDefaultConfig();

        loadconfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public void loadconfig () {

        localchatradius = getDouble("localchatradius");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnPlayerChatEvent(AsyncPlayerChatEvent event) {
        String message = event.getMessage();

        Player player = event.getPlayer();

        if (message.startsWith("!")) {
            GlobalChat(event, message, player);
        } else {
            LocalChat(event, message, player);
        }
    }

    private void GlobalChat (AsyncPlayerChatEvent event, String message, Player player) {
        message = message.substring(1).trim();

        if (message.isEmpty()) {
            event.setCancelled(true);

            getString("global-chat-empty");
            return;
        }

        String globalchat = getString("global-chat-format").replace("{message}", message);
        globalchat = PlaceholderAPI.setPlaceholders(player, globalchat);

        final String finalmessage = globalchat;

        Bukkit.getOnlinePlayers().forEach(target -> target.sendMessage(finalmessage));

        event.setCancelled(true);
}

    private void LocalChat (AsyncPlayerChatEvent event, String message, Player player) {

        Bukkit.getOnlinePlayers().stream()
                .filter(target -> player.getWorld() == target.getWorld() && player.getLocation().distance(target.getLocation()) <= localchatradius)
                .forEach(target -> {
                    String localchat = getString("local-chat-format").replace("{message}", message);
                    localchat = PlaceholderAPI.setPlaceholders(player, localchat);

                    target.sendMessage(localchat);

                    event.setCancelled(true);
                });

    }

    public double getDouble (String message) {
        return getConfig().getDouble(message);
    }

    public String getString (String message) {
        return  getConfig().getString(message);
    }
}
