package couk.Adamki11s.Extras.Colour;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ExtrasColour extends ColourMethods
{
	@Override
	public void sendColouredMessage(Player p, String message)
	{ p.sendMessage(replaceMessage(message)); }
	
	@Override
	public void broadcastColouredMessage(Server s, String message)
	{ s.broadcastMessage(replaceMessage(message)); }
	
	private String replaceMessage(String msg)
	{
		msg = msg.replaceAll("&([a-z0-9])", "¤$1");
		msg = msg.replaceAll("&black", ChatColor.BLACK.toString());
		msg = msg.replaceAll("&blue", ChatColor.BLUE.toString());
		msg = msg.replaceAll("&darkblue", ChatColor.DARK_BLUE.toString());
		msg = msg.replaceAll("&darkaqua", ChatColor.DARK_AQUA.toString());
		msg = msg.replaceAll("&darkred", ChatColor.DARK_RED.toString());
		msg = msg.replaceAll("&darkpurple", ChatColor.DARK_PURPLE.toString());
		msg = msg.replaceAll("&gray", ChatColor.GRAY.toString());
		msg = msg.replaceAll("&darkgray", ChatColor.DARK_GRAY.toString());
		msg = msg.replaceAll("&grey", ChatColor.GRAY.toString());
		msg = msg.replaceAll("&darkgrey", ChatColor.DARK_GRAY.toString());
		msg = msg.replaceAll("&lightpurple", ChatColor.LIGHT_PURPLE.toString());
		msg = msg.replaceAll("&white", ChatColor.WHITE.toString());
		msg = msg.replaceAll("&red", ChatColor.RED.toString());
		msg = msg.replaceAll("&yellow", ChatColor.YELLOW.toString());
		msg = msg.replaceAll("&green", ChatColor.GREEN.toString());
		msg = msg.replaceAll("&aqua", ChatColor.AQUA.toString());
		msg = msg.replaceAll("&pink", ChatColor.LIGHT_PURPLE.toString());
		msg = msg.replaceAll("&purple", ChatColor.DARK_PURPLE.toString());
		msg = msg.replaceAll("&gold", ChatColor.GOLD.toString());
		return msg;
	}
}
