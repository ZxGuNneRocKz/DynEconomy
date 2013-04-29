package me.ksafin.DynamicEconomy;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.SignChangeEvent;

public class dataSigns
{
	static NumberFormat f = NumberFormat.getNumberInstance(Locale.US);
	public static DecimalFormat format = (DecimalFormat) f;
	
	public dataSigns(SignChangeEvent event)
	{
		String[] lines = event.getLines();
		if(lines[0].equalsIgnoreCase("dynamicsign"))
		{
			String item = lines[1];
			String info = lines[2];
			
			FileConfiguration conf = DynamicEconomy.signsConfig;
			try
			{ conf.load(DynamicEconomy.signsFile); }
			catch(Exception e)
			{ e.printStackTrace(); }
			
			Block block = event.getBlock();
			int x = block.getX();
			int y = block.getY();
			int z = block.getZ();
			
			String signID = x + " " + y + " " + z;
			
			ConfigurationSection curSign = conf.createSection(signID);
			curSign.set("WORLD", block.getWorld().getName());
			
			if((item.equalsIgnoreCase("purchasetax")) || (item.equalsIgnoreCase("salestax")))
			{
				double tax;
				String taxName;
				if(item.equalsIgnoreCase("purchasetax"))
				{
					taxName = "Purchase Tax";
					tax = DynamicEconomy.purchasetax;
				}
				else
				{
					taxName = "Sales Tax";
					tax = DynamicEconomy.salestax;
				}
				
				tax *= 100.0D;
				
				event.setLine(0, "");
				event.setLine(1, Utility.getColor(DynamicEconomy.signTaglineColor) + taxName);
				event.setLine(2, Utility.getColor(DynamicEconomy.signInfoColor) + tax + "%");
				
				curSign.set("TYPE", item);
				try
				{ conf.save(DynamicEconomy.signsFile); }
				catch(Exception e)
				{ e.printStackTrace(); }
			}
			else
			{
				item = Item.getTrueName(item);
				
				if(item.equals("")) return;
				String data;
				if(info.equalsIgnoreCase("price"))
					data = "$" + format.format(DynamicEconomy.itemConfig.getDouble(new StringBuilder(
							String.valueOf(item)).append(".price").toString(), 0.0D));
				else if(info.equalsIgnoreCase("stock"))
					data = String.valueOf(DynamicEconomy.itemConfig.getDouble(item + ".stock", 0.0D));
				else if(info.equalsIgnoreCase("span"))
					data = String.valueOf(DynamicEconomy.itemConfig.getDouble(item + ".span", 0.0D));
				else if(info.equalsIgnoreCase("ceiling"))
					data = "$" + String.valueOf(DynamicEconomy.itemConfig.getDouble(new StringBuilder(
							String.valueOf(item)).append(".ceiling").toString(), 0.0D));
				else if(info.equalsIgnoreCase("floor"))
					data = "$" + String.valueOf(DynamicEconomy.itemConfig.getDouble(new StringBuilder(
							String.valueOf(item)).append(".floor").toString(), 0.0D));
				else data = "0";
				
				if(!item.equals(""))
				{
					event.setLine(0, Utility.getColor(DynamicEconomy.signTaglineColor) + item);
					event.setLine(1, Utility.getColor(DynamicEconomy.signTaglineColor) + info.toUpperCase());
					event.setLine(2, Utility.getColor(DynamicEconomy.signInfoColor) + data);
					event.setLine(3, "");
					
					curSign.set("ITEM", item);
					curSign.set("TYPE", info);
					try
					{ conf.save(DynamicEconomy.signsFile); }
					catch(Exception e)
					{ e.printStackTrace(); }
				}
				else
				{
					event.setLine(0, "");
					event.setLine(1, Utility.getColor(DynamicEconomy.signInvalidColor) + "INVALID");
					event.setLine(2, Utility.getColor(DynamicEconomy.signInvalidColor) + "ARGUMENTS");
					event.setLine(3, "");
				}
			}
		}
	}
	
	public static void updateTaxSigns()
	{
		FileConfiguration conf = DynamicEconomy.signsConfig;
		
		Set<String> set = conf.getKeys(false);
		Object[] signsObj = set.toArray();
		String[] signs = new String[signsObj.length];
		
		for(int x = 0; x < signsObj.length; x++) signs[x] = signsObj[x].toString();
		
		for(String signID : signs)
		{
			String request = signID + ".TYPE";
			String type = conf.getString(request);
			
			if(type != null)
			{
				if(type.equalsIgnoreCase("purchasetax"))
				{
					Sign sign = getSign(signID);
					if(sign != null)
					{
						String tax = format.format(DynamicEconomy.purchasetax * 100.0D);
						sign.setLine(2, Utility.getColor(DynamicEconomy.signInfoColor) + tax + "%");
						sign.update();
					}
				}
				else if(type.equalsIgnoreCase("salestax"))
				{
					Sign sign = getSign(signID);
					if(sign != null)
					{
						String tax = format.format(DynamicEconomy.salestax * 100.0D);
						sign.setLine(2, Utility.getColor(DynamicEconomy.signInfoColor) + tax + "%");
						sign.update();
					}
				}
			}
		}
	}
	
	public static Sign getSign(String coords)
	{
		FileConfiguration conf = DynamicEconomy.signsConfig;
		String[] splitID = coords.split(" ");
		int x = Integer.parseInt(splitID[0]);
		int y = Integer.parseInt(splitID[1]);
		int z = Integer.parseInt(splitID[2]);
		
		String node = coords + ".WORLD";
		String worldName = conf.getString(node, "world");
		
		Location loc = new Location(Bukkit.getServer().getWorld(worldName), x, y, z);
		Block block = loc.getBlock();
		
		if((block.getState() instanceof Sign))
		{
			Sign sign = (Sign) block.getState();
			return sign;
		}
		conf.set(coords, null);
		try
		{ conf.save(DynamicEconomy.signsFile); }
		catch(IOException e)
		{ e.printStackTrace(); }
		Utility.writeToLog("DynamicSign no longer found at " + coords + ", entry removed from file");
		return null;
	}
	
	public static void removeDataSign(Block block)
	{
		FileConfiguration conf = DynamicEconomy.signsConfig;
		
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		
		String signID = x + " " + y + " " + z;
		
		if(conf.contains(signID))
		{
			conf.set(signID, null);
			try
			{ conf.save(DynamicEconomy.signsFile); }
			catch(Exception e)
			{ e.printStackTrace(); }
		}
	}
	
	public static void updateColors()
	{
		Set<String> signs = DynamicEconomy.signsConfig.getKeys(false);
		Iterator<String> i = signs.iterator();
		
		while(i.hasNext())
		{
			Sign curSign = getSign((String) i.next());
			
			if(curSign != null)
			{
				String line1 = curSign.getLine(0).substring(2);
				String line2 = curSign.getLine(1).substring(2);
				String line3 = curSign.getLine(2).substring(2);
				
				curSign.setLine(0, Utility.getColor(DynamicEconomy.signTaglineColor) + line1);
				curSign.setLine(1, Utility.getColor(DynamicEconomy.signTaglineColor) + line2);
				curSign.setLine(2, Utility.getColor(DynamicEconomy.signInfoColor) + line3);
				
				if(curSign.getLine(3).length() != 0)
				{
					String line4 = curSign.getLine(3).substring(2);
					curSign.setLine(3, Utility.getColor(DynamicEconomy.signInfoColor) + line4);
				}
				
				curSign.update();
			}
		}
	}
	
	public static void checkForUpdates(String item, int changeStock, double changePrice)
	{
		FileConfiguration conf = DynamicEconomy.signsConfig;
		
		Set<String> set = conf.getKeys(false);
		Object[] signsObj = set.toArray();
		String[] signs = new String[signsObj.length];
		
		for(int x = 0; x < signsObj.length; x++) signs[x] = signsObj[x].toString();
		
		for(int x = 0; x < signs.length; x++)
		{
			String request = signs[x] + ".ITEM";
			String itemName = conf.getString(request);
			
			if(itemName == null)
			{
				conf.set(signs[x], null);
				try
				{ conf.save(DynamicEconomy.signsFile); }
				catch(IOException e)
				{ e.printStackTrace(); }
				Utility.writeToLog("DynamicSign no longer found at " + signs[x] + ", entry removed from file");
			}
			else if(itemName.equalsIgnoreCase(item)) updateItem(item, signs[x], changeStock, changePrice);
		}
	}
	
	public static void checkForUpdatesNonRegular(String item, double changeSpan, double changeCeiling, double changeFloor)
	{
		FileConfiguration conf = DynamicEconomy.signsConfig;
		
		Set<String> set = conf.getKeys(false);
		Object[] signsObj = set.toArray();
		String[] signs = new String[signsObj.length];
		
		for(int x = 0; x < signsObj.length; x++) signs[x] = signsObj[x].toString();
		
		for(int x = 0; x < signs.length; x++)
		{
			String request = signs[x] + ".ITEM";
			String itemName = conf.getString(request);
			
			if(itemName == null)
			{
				conf.set(signs[x], null);
				try
				{ conf.save(DynamicEconomy.signsFile); }
				catch(IOException e)
				{ e.printStackTrace(); }
				Utility.writeToLog("DynamicSign no longer found at " + signs[x] + ", entry removed from file");
			}
			else if(itemName.equalsIgnoreCase(item)) updateItem(item, signs[x], changeSpan, changeCeiling, changeFloor);
		}
	}
	
	public static void updateItem(String item, String signID, int changeStock, double changePrice)
	{
		FileConfiguration conf = DynamicEconomy.signsConfig;
		
		Sign sign = getSign(signID);
		
		if(sign == null) { return; }
		
		String data = "";
		
		String type = conf.getString(signID + ".TYPE");
		String change = "";
		
		if((type.equalsIgnoreCase("price")) && (changePrice != 0.0D))
		{
			data = "$" + format.format(DynamicEconomy.itemConfig.getDouble(new StringBuilder(String.valueOf(item)).append(
					".price").toString(), 0.0D));
			if(changePrice > 0.0D) change = "+" + format.format(changePrice);
			else change = format.format(changePrice);
		}
		else if((type.equalsIgnoreCase("stock")) && (changeStock != 0))
		{
			data = String.valueOf(DynamicEconomy.itemConfig.getDouble(item + ".stock", 0.0D));
			if(changeStock > 0) change = "+" + changeStock;
			else change = changeStock + "";	
		}
		
		if(!change.equals(""))
		{
			sign.setLine(2, Utility.getColor(DynamicEconomy.signInfoColor) + data);
			sign.setLine(3, Utility.getColor(DynamicEconomy.signInfoColor) + "(" + change + ")");
		}
		
		sign.update();
	}
	
	public static void updateItem(String item, String signID, double changeSpan, double changeCeiling, double changeFloor)
	{
		FileConfiguration conf = DynamicEconomy.signsConfig;
		
		String[] splitID = signID.split(" ");
		int x = Integer.parseInt(splitID[0]);
		int y = Integer.parseInt(splitID[1]);
		int z = Integer.parseInt(splitID[2]);
		
		String node = signID + ".WORLD";
		String worldName = conf.getString(node, "world");
		
		Location loc = new Location(Bukkit.getServer().getWorld(worldName), x, y, z);
		Block block = loc.getBlock();
		Sign sign = (Sign) block.getState();
		
		String data = "";
		
		String type = conf.getString(signID + ".TYPE");
		
		String change = "";
		
		if((type.equalsIgnoreCase("ceiling")) && (changeCeiling != 0.0D))
		{
			data = format.format(DynamicEconomy.itemConfig.getDouble(item + ".ceiling", 0.0D));
			if(changeCeiling > 0.0D) change = "+" + format.format(changeCeiling);
			else change = format.format(changeCeiling);
		}
		else if((type.equalsIgnoreCase("floor")) && (changeFloor != 0.0D))
		{
			data = String.valueOf(DynamicEconomy.itemConfig.getDouble(item + ".floor", 0.0D));
			if(changeFloor > 0.0D) change = "+" + format.format(changeFloor);
			else change = format.format(changeFloor);
		}
		else if((type.equalsIgnoreCase("span")) && (changeSpan != 0.0D))
		{
			data = String.valueOf(DynamicEconomy.itemConfig.getDouble(item + ".velocity", 0.0D));
			if(changeSpan > 0.0D) change = "+" + format.format(changeSpan);
			else change = format.format(changeSpan);
		}
		
		if(!change.equals(""))
		{
			sign.setLine(2, Utility.getColor(DynamicEconomy.signInfoColor) + data);
			sign.setLine(3, Utility.getColor(DynamicEconomy.signInfoColor) + "(" + change + ")");
		}
		
		sign.update();
	}
}
