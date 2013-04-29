package me.ksafin.DynamicEconomy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import couk.Adamki11s.Extras.Colour.ExtrasColour;

public class Utility
{
	public File log;
	public static Logger logger = Logger.getLogger("Minecraft");
	public static FileWriter out;
	public static BufferedWriter bf;
	private DynamicEconomy plugin;
	public static ExtrasColour color = new ExtrasColour();
	
	public Utility(File logFile, DynamicEconomy plug)
	{
		this.log = logFile;
		this.plugin = plug;
		checkLog();
		try
		{
			out = new FileWriter(this.log, true);
			bf = new BufferedWriter(out);
		}
		catch(Exception e)
		{
			logger.info("[DynamicEconomy] Error creating FileWriter for log.txt");
			e.printStackTrace();
		}
	}
	
	public static int[] decodeCoordinates(String stringCoords)
	{
		String[] split = stringCoords.split(" ");
		int[] intCoords = new int[3];
		for(int x = 0; x < 3; x++) intCoords[x] = Integer.parseInt(split[x]);
		return intCoords;
	}
	
	public static String encodeCoordinates(int[] coordsArray)
	{
		String coords = "";
		for(int x = 0; x < 3; x++)
		{
			if(x < 2) coords = coords + coordsArray[x] + " ";
			else coords = coords + coordsArray[x];
		}
		return coords;
	}
	
	public void checkLog()
	{
		File file = new File(this.plugin.getDataFolder().getPath());
		if(!file.exists()) file.mkdir();
		
		File logF = new File(file.getPath() + File.separator + "log.txt");
		try
		{
			if(!logF.exists())
			{
				FileOutputStream fos = new FileOutputStream(logF);
				fos.flush();
				fos.close();
			}
		}
		catch(IOException ioe)
		{ logger.info("[DynamicEconomy] Exception creating log.txt"); }
	}
	
	public static void writeToLog(String message)
	{
		Date dateS = new Date();
		
		Timestamp ts = new Timestamp(dateS.getTime());
		
		if(DynamicEconomy.logwriting) try
		{
			bf.write("\n + [" + ts + "] " + message);
			bf.flush();
		}
		catch(Exception e)
		{
			logger.info("[DynamicEconomy] Exception writing to log.txt");
			e.printStackTrace();
		}
	}
	
	public static void wrongArgsMessage(Player player)
	{ color.sendColouredMessage(player, "&2You have put entered &finvalid &2 arguments for this command. &fTry Again."); }
	
	public static void copyFile(InputStream in, File targ) throws IOException, FileNotFoundException
	{
		OutputStream out = new FileOutputStream(targ);
		byte[] buf = new byte[1024];
		int len;
		while((len = in.read(buf)) > 0) out.write(buf, 0, len);
		in.close();
		out.close();
	}
	
	public static String convertToRomanNumeral(int x)
	{
		if(x == 1) return "I";
		if(x == 2) return "II";
		if(x == 3) return "III";
		if(x == 4) return "IV";
		if(x == 5) { return "V"; }
		return "";
	}
	
	public static boolean isQuiet(Player player)
	{ return Boolean.valueOf(DynamicEconomy.usersConfig.getBoolean(player.getName() + ".QUIET", true)); }
	
	public static void makeQuiet(Player player)
	{
		DynamicEconomy.usersConfig.set(player.getName() + ".QUIET", 
				Boolean.valueOf(!DynamicEconomy.usersConfig.getBoolean(player.getName() + ".QUIET")));
		
		color.sendColouredMessage(player, "&2Your Quiet mode has been set to: &f" + isQuiet(player));
		try
		{ DynamicEconomy.usersConfig.save(DynamicEconomy.usersFile); }
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public static String listToString(List<String> list)
	{
		String stringList = "";
		for(int x = 0; x < list.size(); x++) stringList = stringList + (String) list.get(x) + ",";
		if(stringList.length() != 0) stringList = stringList.substring(0, stringList.length() - 1);
		
		return stringList;
	}
	
	public static String getColor(String color)
	{
		if(color.equals("&0")) return ChatColor.BLACK.toString();
		if(color.equals("&1")) return ChatColor.DARK_BLUE.toString();
		if(color.equals("&2")) return ChatColor.DARK_GREEN.toString();
		if(color.equals("&3")) return ChatColor.DARK_AQUA.toString();
		if(color.equals("&4")) return ChatColor.DARK_RED.toString();
		if(color.equals("&5")) return ChatColor.DARK_PURPLE.toString();
		if(color.equals("&6")) return ChatColor.GOLD.toString();
		if(color.equals("&7")) return ChatColor.GRAY.toString();
		if(color.equals("&8")) return ChatColor.DARK_GRAY.toString();
		if(color.equals("&9")) return ChatColor.BLUE.toString();
		if(color.equals("&a")) return ChatColor.GREEN.toString();
		if(color.equals("&b")) return ChatColor.AQUA.toString();
		if(color.equals("&c")) return ChatColor.RED.toString();
		if(color.equals("&d")) return ChatColor.LIGHT_PURPLE.toString();
		if(color.equals("&e")) return ChatColor.YELLOW.toString();
		if(color.equals("&f")) return ChatColor.WHITE.toString();
		if(color.equals("&A")) return ChatColor.GREEN.toString();
		if(color.equals("&B")) return ChatColor.AQUA.toString();
		if(color.equals("&C")) return ChatColor.RED.toString();
		if(color.equals("&D")) return ChatColor.LIGHT_PURPLE.toString();
		if(color.equals("&E")) return ChatColor.YELLOW.toString();
		if(color.equals("&F")) return ChatColor.WHITE.toString();
		if(color.equals("&black")) return ChatColor.BLACK.toString();
		if(color.equals("&blue")) return ChatColor.BLUE.toString();
		if(color.equals("&darkblue")) return ChatColor.DARK_BLUE.toString();
		if(color.equals("&darkaqua")) return ChatColor.DARK_AQUA.toString();
		if(color.equals("&darkred")) return ChatColor.DARK_RED.toString();
		if(color.equals("&darkpurple")) return ChatColor.DARK_PURPLE.toString();
		if(color.equals("&gray")) return ChatColor.GRAY.toString();
		if(color.equals("&darkgray")) return ChatColor.DARK_GRAY.toString();
		if(color.equals("&grey")) return ChatColor.GRAY.toString();
		if(color.equals("&darkgrey")) return ChatColor.DARK_GRAY.toString();
		if(color.equals("&lightpurple")) return ChatColor.LIGHT_PURPLE.toString();
		if(color.equals("&white")) return ChatColor.WHITE.toString();
		if(color.equals("&red")) return ChatColor.RED.toString();
		if(color.equals("&yellow")) return ChatColor.YELLOW.toString();
		if(color.equals("&green")) return ChatColor.GREEN.toString();
		if(color.equals("&aqua")) return ChatColor.AQUA.toString();
		if(color.equals("&pink")) return ChatColor.LIGHT_PURPLE.toString();
		if(color.equals("&purple")) return ChatColor.DARK_PURPLE.toString();
		if(color.equals("&gold")) { return ChatColor.GOLD.toString(); }
		return "";
	}
}
