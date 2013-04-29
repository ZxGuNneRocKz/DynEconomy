package me.ksafin.DynamicEconomy;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.SignChangeEvent;

import couk.Adamki11s.Extras.Colour.ExtrasColour;

public class DynamicShop
{
	private static ExtrasColour color = new ExtrasColour();
	int level;
	int amount;
	
	public DynamicShop(SignChangeEvent event)
	{
		String item = event.getLine(1);
		
		FileConfiguration conf = DynamicEconomy.shopsConfig;
		
		if(Item.isEnchantment(item))
		{
			try
			{ this.level = Integer.parseInt(event.getLine(2)); }
			catch(NumberFormatException e)
			{
				setInvalid(event);
				return;
			}
			String[] itemInfo = Item.getAllInfo(item);
			int id = Integer.parseInt(itemInfo[6]);
			id %= 2500;
			
			Enchantment enchant = Enchantment.getById(id);
			
			if((this.level <= 0) || (this.level > enchant.getMaxLevel()))
			{
				setInvalidEnchantLevel(event);
				return;
			}
			
			if(itemInfo[0].equalsIgnoreCase(""))
			{
				color.sendColouredMessage(event.getPlayer(),
						DynamicEconomy.prefix + "&2The item &f" + item + "&2 doesn't exist.");
				Utility.writeToLog(event.getPlayer() + " tried to create a DynamicShop for the non-existant item " + item);
				setInvalid(event);
				return;
			}
			
			EnderEngine engine = new EnderEngine(itemInfo);
			
			double price = engine.getPrice();
			
			event.setLine(0, Utility.getColor(DynamicEconomy.signTaglineColor) + "[DynamicShop]");
			event.setLine(1, Utility.getColor(DynamicEconomy.signTaglineColor) + "Enchantment");
			event.setLine(2, Utility.getColor(DynamicEconomy.signInfoColor) + itemInfo[0] + " " +
					Utility.convertToRomanNumeral(this.level));
			event.setLine(3, Utility.getColor(DynamicEconomy.signTaglineColor) + DynamicEconomy.currencySymbol +
					Utility.getColor(DynamicEconomy.signInfoColor) + price * this.level);
			
			Block signBlock = event.getBlock();
			
			int x = signBlock.getX();
			int y = signBlock.getY();
			int z = signBlock.getZ();
			
			String coords = x + " " + y + " " + z;
			
			String node = coords + ".";
			
			conf.set(node + "item", itemInfo[0]);
			conf.set(node + "level", Integer.valueOf(this.level));
			conf.set(node + "world", event.getBlock().getWorld().getName());
		}
		else
		{
			try
			{ this.amount = Integer.parseInt(event.getLine(2)); }
			catch(NumberFormatException e)
			{
				setInvalid(event);
				return;
			}
			if(this.amount < 1) setInvalid(event);
			
			String[] itemInfo = Item.getAllInfo(item);
			
			if(itemInfo[0].equalsIgnoreCase(""))
			{
				color.sendColouredMessage(event.getPlayer(),
						DynamicEconomy.prefix + "&2The item &f" + item + "&2 doesn't exist.");
				Utility.writeToLog(event.getPlayer() + " tried to create a DynamicShop for the non-existant item " + item);
				setInvalid(event);
				return;
			}
			
			EnderEngine engine = new EnderEngine(itemInfo);
			
			double price = engine.getPrice();
			
			event.setLine(0, Utility.getColor(DynamicEconomy.signTaglineColor) + "[DynamicShop]");
			event.setLine(1, Utility.getColor(DynamicEconomy.signTaglineColor) + itemInfo[0]);
			event.setLine(2, Utility.getColor(DynamicEconomy.signTaglineColor) + DynamicEconomy.currencySymbol +
					Utility.getColor(DynamicEconomy.signInfoColor) + price);
			event.setLine(3, Utility.getColor(DynamicEconomy.signTaglineColor) + "AMOUNT: " +
					Utility.getColor(DynamicEconomy.signInfoColor) + this.amount);
			
			Block signBlock = event.getBlock();
			
			int x = signBlock.getX();
			int y = signBlock.getY();
			int z = signBlock.getZ();
			
			String coords = x + " " + y + " " + z;
			
			String node = coords + ".";
			
			conf.set(node + "item", itemInfo[0]);
			conf.set(node + "amount", Integer.valueOf(this.amount));
			conf.set(node + "world", event.getBlock().getWorld().getName());
		}
		
		try
		{ conf.save(DynamicEconomy.shopsFile); }
		catch(Exception e)
		{ e.printStackTrace(); }
	}
	
	public static void updateColors()
	{		
		for(String coords : DynamicEconomy.shopsConfig.getKeys(false))
		{
			Sign curSign = getSign(coords);
			
			if(curSign != null)
			{
				String item = DynamicEconomy.shopsConfig.getString(coords + ".item");
				String[] info = Item.getAllInfo(item);
				EnderEngine engine = new EnderEngine(info);
				
				if(engine.isEnchantment())
				{
					int level = DynamicEconomy.shopsConfig.getInt(coords + ".level");
					
					curSign.setLine(0, Utility.getColor(DynamicEconomy.signTaglineColor) + "[DynamicShop]");
					curSign.setLine(1, Utility.getColor(DynamicEconomy.signTaglineColor) + "Enchantment");
					curSign.setLine(2, Utility.getColor(DynamicEconomy.signInfoColor) + info[0] + " " +
							Utility.convertToRomanNumeral(level));
					curSign.setLine(3,
							Utility.getColor(DynamicEconomy.signTaglineColor) + DynamicEconomy.currencySymbol +
							Utility.getColor(DynamicEconomy.signInfoColor) + engine.getPrice() * level);
				}
				else
				{
					int amount = DynamicEconomy.shopsConfig.getInt(coords + ".amount");
					
					curSign.setLine(0, Utility.getColor(DynamicEconomy.signTaglineColor) + "[DynamicShop]");
					curSign.setLine(1, Utility.getColor(DynamicEconomy.signTaglineColor) + info[0]);
					curSign.setLine(2,
							Utility.getColor(DynamicEconomy.signTaglineColor) + DynamicEconomy.currencySymbol +
							Utility.getColor(DynamicEconomy.signInfoColor) + engine.getPrice());
					curSign.setLine(3, Utility.getColor(DynamicEconomy.signTaglineColor) + "AMOUNT: " +
							Utility.getColor(DynamicEconomy.signInfoColor) + amount);
				}
				
				curSign.update();
			}
		}
	}
	
	public static void removeShopSign(Block block)
	{
		FileConfiguration conf = DynamicEconomy.shopsConfig;
		
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		
		String signID = x + " " + y + " " + z;
		
		if(conf.contains(signID))
		{
			conf.set(signID, null);
			try
			{ conf.save(DynamicEconomy.shopsFile); }
			catch(Exception e)
			{ e.printStackTrace(); }
		}
	}
	
	public static boolean isShop(Block block)
	{
		if(block == null) return false;
		
		FileConfiguration conf = DynamicEconomy.shopsConfig;
		
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		
		String signID = x + " " + y + " " + z;
		
		if(conf.contains(signID)) return true;
		return false;
	}
	
	public static String[] getArgs(Block block)
	{
		FileConfiguration conf = DynamicEconomy.shopsConfig;
		
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		
		String signID = x + " " + y + " " + z;
		
		String item = conf.getString(signID + ".item");
		String amount = String.valueOf(conf.getInt(signID + ".amount"));
		
		String[] args = { item, amount };
		return args;
	}
	
	public static String[] getEnchantArgs(Block block)
	{
		FileConfiguration conf = DynamicEconomy.shopsConfig;
		
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		
		String signID = x + " " + y + " " + z;
		
		String item = conf.getString(signID + ".item");
		String level = String.valueOf(conf.getInt(signID + ".level"));
		
		String[] args = { item, level };
		return args;
	}
	
	public static void updateItem(String item)
	{
		int level = 1;
		
		for(String coords : DynamicEconomy.shopsConfig.getKeys(false))
		{
			ConfigurationSection curShop = DynamicEconomy.shopsConfig.getConfigurationSection(coords);
			
			if(curShop.getString("item").equalsIgnoreCase(item))
			{
				if(curShop.contains("level")) level = curShop.getInt("level");
				
				Sign sign = getSign(coords);
				
				double price = DynamicEconomy.itemConfig.getDouble(item + ".price") * level;
				
				if(curShop.contains("level")) sign.setLine(3,
						Utility.getColor(DynamicEconomy.signTaglineColor) + DynamicEconomy.currencySymbol +
						Utility.getColor(DynamicEconomy.signInfoColor) + price);
				else
				{
					sign.setLine(2,
							Utility.getColor(DynamicEconomy.signTaglineColor) + DynamicEconomy.currencySymbol +
							Utility.getColor(DynamicEconomy.signInfoColor) + price);
				}
				sign.update();
			}
		}
	}
	
	public static Sign getSign(String coords)
	{
		FileConfiguration conf = DynamicEconomy.shopsConfig;
		String[] splitID = coords.split(" ");
		int x = 0;
		int y = 0;
		int z = 0;
		try
		{
			x = Integer.parseInt(splitID[0]);
			y = Integer.parseInt(splitID[1]);
			z = Integer.parseInt(splitID[2]);
		}
		catch(NumberFormatException err)
		{ err.printStackTrace(); }
		
		String node = coords + ".world";
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
		{ conf.save(DynamicEconomy.shopsFile); }
		catch(IOException e)
		{ e.printStackTrace(); }
		Utility.writeToLog("DynamicShop no longer found at " + coords + ", entry removed from file");
		return null;
	}
	
	public void setInvalid(SignChangeEvent event)
	{
		event.setLine(0, "");
		event.setLine(1, Utility.getColor(DynamicEconomy.signInvalidColor) + "INVALID");
		event.setLine(2, Utility.getColor(DynamicEconomy.signInvalidColor) + "ARGUMENTS");
		event.setLine(3, "");
	}
	
	public void setInvalidEnchantLevel(SignChangeEvent event)
	{
		event.setLine(0, Utility.getColor(DynamicEconomy.signInvalidColor) + "INVALID");
		event.setLine(1, Utility.getColor(DynamicEconomy.signInvalidColor) + "ENCHANTMENT");
		event.setLine(2, Utility.getColor(DynamicEconomy.signInvalidColor) + "LEVEL");
		event.setLine(3, "");
	}
}
