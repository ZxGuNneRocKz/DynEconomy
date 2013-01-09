package me.ksafin.DynamicEconomy.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.ksafin.DynamicEconomy.DynamicEconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {
	private static Logger logger = Logger.getLogger("PrisonSuite");
	
	public static void send(CommandSender player, String message) {
		if(message == null) message = "";
		player.sendMessage(Util.parseColors(message));
	}
	
	public static void send(CommandSender player, String title, String message) {
		if(message == null) message = "";
		send(player, ChatColor.GOLD + "[" + title + "] " + ChatColor.WHITE + message);
	}
	
	public static void sendSuccess(CommandSender player, String message) {
		if(message == null) message = "";
		send(player, DynamicEconomy.getSettings().GENERAL_TITLE + " " + ChatColor.WHITE + message);
	}

    /**
     * Broadcasts a message to all players on the server
     * @param message Message to be sent
     */
    public static void broadcast(String message) {
        if(message == null) message = "";
		message = DynamicEconomy.getSettings().GENERAL_TITLE + " " + ChatColor.WHITE + message;
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
        	p.sendMessage(Util.parseColors(message));
        }
    }
	
    /**
     * Sends a message into the server log if debug is enabled
     * @param message Message to be sent
     */
    public static void debug(String message) {
        if (DynamicEconomy.getSettings().DEBUG) log(message);
    }
	
	/**
	 * Sends a message into the server log
	 * @param message Message to be sent
	 */
	public static void log(String message) {
		logger.info("[DE] " + message);
	}
	
	/**
	 * Sends a message into the server log
	 * @param level Severity level
	 * @param message Message to be sent
	 */
	public static void log(Level level, String message) {
		logger.log(level, "[DE] " + message);
	}
}
