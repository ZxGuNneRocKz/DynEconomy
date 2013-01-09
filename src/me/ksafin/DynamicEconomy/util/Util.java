package me.ksafin.DynamicEconomy.util;

import org.bukkit.ChatColor;

public class Util {
	/**
	 * Replaces the color codes with colors
	 * @param msg String to be parsed
	 * @return Parsed string
	 */
	public static String parseColors(String msg) {
		if(msg == null) return "";
		msg = msg.replaceAll("&0", ChatColor.BLACK.toString());
		msg = msg.replaceAll("&1", ChatColor.DARK_BLUE.toString());
		msg = msg.replaceAll("&2", ChatColor.DARK_GREEN.toString());
		msg = msg.replaceAll("&3", ChatColor.DARK_AQUA.toString());
		msg = msg.replaceAll("&4", ChatColor.DARK_RED.toString());
		msg = msg.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
		msg = msg.replaceAll("&6", ChatColor.GOLD.toString());
		msg = msg.replaceAll("&7", ChatColor.GRAY.toString());
		msg = msg.replaceAll("&8", ChatColor.DARK_GRAY.toString());
		msg = msg.replaceAll("&9", ChatColor.BLUE.toString());

		msg = msg.replaceAll("&a", ChatColor.GREEN.toString());
		msg = msg.replaceAll("&b", ChatColor.AQUA.toString());
		msg = msg.replaceAll("&c", ChatColor.RED.toString());
		msg = msg.replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
		msg = msg.replaceAll("&e", ChatColor.YELLOW.toString());
		msg = msg.replaceAll("&f", ChatColor.WHITE.toString());

		msg = msg.replaceAll("&A", ChatColor.GREEN.toString());
		msg = msg.replaceAll("&B", ChatColor.AQUA.toString());
		msg = msg.replaceAll("&C", ChatColor.RED.toString());
		msg = msg.replaceAll("&D", ChatColor.LIGHT_PURPLE.toString());
		msg = msg.replaceAll("&E", ChatColor.YELLOW.toString());
		msg = msg.replaceAll("&F", ChatColor.WHITE.toString());

		msg = msg.replaceAll("&k", ChatColor.MAGIC.toString());
		msg = msg.replaceAll("&l", ChatColor.BOLD.toString());
		msg = msg.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
		msg = msg.replaceAll("&n", ChatColor.UNDERLINE.toString());
		msg = msg.replaceAll("&o", ChatColor.ITALIC.toString());
		msg = msg.replaceAll("&r", ChatColor.RESET.toString());

		msg = msg.replaceAll("&K", ChatColor.MAGIC.toString());
		msg = msg.replaceAll("&L", ChatColor.BOLD.toString());
		msg = msg.replaceAll("&M", ChatColor.STRIKETHROUGH.toString());
		msg = msg.replaceAll("&N", ChatColor.UNDERLINE.toString());
		msg = msg.replaceAll("&O", ChatColor.ITALIC.toString());
		msg = msg.replaceAll("&R", ChatColor.RESET.toString());

		return msg;
	}
}