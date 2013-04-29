package me.ksafin.DynamicEconomy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import couk.Adamki11s.Extras.Colour.ExtrasColour;
import couk.Adamki11s.Extras.Inventory.ExtrasInventory;

public class Item
{
	private static ExtrasColour color = new ExtrasColour();
	static Logger log = Logger.getLogger("Minecraft");
	
	static NumberFormat f = NumberFormat.getNumberInstance(Locale.US);
	public static DecimalFormat decFormat = (DecimalFormat) f;
	
	public static String getTrueName(String arg)
	{
		arg = arg.toUpperCase();
		
		if(DynamicEconomy.aliasConfig.contains(arg))
		{
			String item = DynamicEconomy.aliasConfig.getString(arg);
			return item;
		}
		return "";
	}
	
	public static void addAlias(Player player, String[] args)
	{
		if(args.length != 2)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/addAlias [Alias] [Item]");
			Utility.writeToLog(player.getName() + " incorrectly called /addAlias");
			return;
		}
		String alias = args[0];
		String item = args[1];
		
		alias = alias.toUpperCase();
		item = item.toUpperCase();
		
		DynamicEconomy.aliasConfig.set(alias, item);
		try
		{ DynamicEconomy.aliasConfig.save(DynamicEconomy.aliasFile); }
		catch(Exception e)
		{ log.info("[DynamicEconomy] Error saving alias.yml"); }
		
		color.sendColouredMessage(player,
				DynamicEconomy.prefix + "&f" + alias + "&2 has been added as an alias for &f" + item);
		Utility.writeToLog(player.getName() + " added " + alias + " as an alias for " + item);
		
		DynamicEconomy.relConfig();
	}
	
	public static void removeAlias(Player player, String[] args)
	{
		if(args.length != 1)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/removeAlias [Alias]");
			Utility.writeToLog(player.getName() + " incorrectly called /removeAlias");
			return;
		}
		String alias = args[0];
		alias = alias.toUpperCase();
		
		if(!DynamicEconomy.aliasConfig.contains(alias))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&f" + alias + "&2 does not exist as an alias.");
			Utility.writeToLog(player.getName() + " tried to remove " + alias + " as an alias, but it doesn't exist.");
			return;
		}
		
		DynamicEconomy.aliasConfig.set(alias, null);
		try
		{
			DynamicEconomy.aliasConfig.save(DynamicEconomy.aliasFile);
		}
		catch(Exception e)
		{
			log.info("[DynamicEconomy] Error saving alias.yml");
		}
		
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&f" + alias + "&2 has been removed as an alias");
		Utility.writeToLog(player.getName() + " removed " + alias + " as an alias");
		
		DynamicEconomy.relConfig();
	}
	
	public static void priceEnchantment(Player player, String[] args)
	{
		if((args.length != 2) && (args.length != 3))
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/priceenchantment [Enchantment] [Level] [Buy|Sell]");
			Utility.writeToLog(player.getName() + " incorrectly called /canibuy");
			return;
		}
		String enchantment = args[0].toUpperCase();
		int level = 0;
		
		if(args[1].equalsIgnoreCase("I")) level = 1;
		else if(args[1].equalsIgnoreCase("II")) level = 2;
		else if(args[1].equalsIgnoreCase("III")) level = 3;
		else if(args[1].equalsIgnoreCase("IV")) level = 4;
		else if(args[1].equalsIgnoreCase("V")) level = 5;
		else
		{
			try
			{
				level = Integer.parseInt(args[1]);
			}
			catch(NumberFormatException e)
			{
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&2The level &f" + args[1] + "&2 is invalid. Use I-V,1-5");
				Utility.writeToLog(player.getName() + " incorrectly called /canibuy");
				return;
			}
		}
		
		if(!DynamicEconomy.itemConfig.contains(enchantment))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&f" + enchantment + "&2 is not a valid enchantment.");
			Utility.writeToLog(player.getName() + " called /priceenchantment with invalid enchantment " + enchantment);
			return;
		}
		
		String[] enchantmentInfo = getAllInfo(enchantment);
		
		int id = Integer.parseInt(enchantmentInfo[6]);
		int stock = Integer.parseInt(enchantmentInfo[5]);
		int enchantmentID = id % 2500;
		
		Enchantment enchant = Enchantment.getById(enchantmentID);
		int maxLevel = enchant.getMaxLevel();
		
		if(level > maxLevel)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&fThe maximum level for this enchantment is &f" + maxLevel);
			Utility.writeToLog(player.getName() + " called /buyenchantment with a level above the maximum for " + enchantment);
			return;
		}
		
		EnderEngine engine = new EnderEngine(enchantmentInfo);
		
		String desc = DynamicEconomy.itemConfig.getString(enchantment + ".description");
		
		decFormat.applyPattern("#.##");
		String price = decFormat.format(engine.getPrice());
		
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&2-----------------------------------");
		color.sendColouredMessage(
				player,
				DynamicEconomy.prefix + "&2Current Price of &f" + enchantment + "&2 is &f" + DynamicEconomy.currencySymbol + price);
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Description: &f" + desc);
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Stock: &f" + stock);
		
		String levelsString = "";
		for(int x = 1; x <= maxLevel; x++)
		{
			levelsString = levelsString + Utility.convertToRomanNumeral(x);
			levelsString = levelsString + " ";
		}
		
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Possible levels: &f" + levelsString);
		
		if(args.length == 3)
		{
			if(args[2].equalsIgnoreCase("sell"))
			{
				double tax = DynamicEconomy.salestax;
				
				double total = engine.getSale(1);
				total *= level;
				
				tax *= total;
				total -= tax;
				String totalStr = decFormat.format(total);
				
				color.sendColouredMessage(
						player,
						DynamicEconomy.prefix + "&2Selling &f1&2 of &f" + enchantment + "&2 + " + DynamicEconomy.currencySymbol + tax + " tax &fyields &2" + totalStr);
			}
			else if(args[2].equalsIgnoreCase("buy"))
			{
				double tax = DynamicEconomy.salestax;
				
				double total = engine.getCost(1);
				total *= level;
				
				tax *= total;
				total += tax;
				String totalStr = decFormat.format(total);
				
				color.sendColouredMessage(
						player,
						DynamicEconomy.prefix + "&2Buying &f1&2 of &f" + enchantment + "&2 + " + DynamicEconomy.currencySymbol + tax + " tax &fcosts &2" + totalStr);
			}
			else
			{
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&f" + args[2] + "&2 is not a valid type. Use &f'buy' or 'sell'");
				Utility.writeToLog(player.getName() + " called /priceenchantment with invalid type " + args[2]);
				return;
			}
		}
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&2-----------------------------------");
	}
	
	public static void canIBuy(Player player, String[] args)
	{
		if(args.length != 1)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/canibuy [Item]");
			Utility.writeToLog(player.getName() + " incorrectly called /canibuy");
			return;
		}
		String item = getTrueName(args[0]);
		
		if(item.equals(""))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Item &f" + args[0] + " &2doesn't exist.");
			Utility.writeToLog(player.getName() + " called /canibuy for item " + args[0] + ", which doesn't exist.");
		}
		else
		{
			List<String> bannedItems = Arrays.asList(DynamicEconomy.bannedPurchaseItems);
			
			if(bannedItems.contains(item))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2You &fCANNOT &2buy &f" + item);
				return;
			}
			
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2You &fCAN &2buy &f" + item);
		}
	}
	
	public static void canISell(Player player, String[] args)
	{
		if(args.length == 1)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/canisell [Item]");
			Utility.writeToLog(player.getName() + " incorrectly called /canisell");
			return;
		}
		String item = getTrueName(args[0]);
		
		if(item.equals(""))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Item &f" + args[0] + " &2doesn't exist.");
			Utility.writeToLog(player.getName() + " called /canisell for item " + args[0] + ", which doesn't exist.");
		}
		else
		{
			List<String> bannedItems = Arrays.asList(DynamicEconomy.bannedSaleItems);
			
			if(bannedItems.contains(item))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2You &fCANNOT &2sell &f" + item);
				return;
			}
			
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2You &fCAN &2sell &f" + item);
		}
	}
	
	public static boolean getPrice(Player player, String[] args)
	{
		int amt = 0;
		String stringPlay = player.getName();
		double total = 0.0D;
		if((args.length < 1) || (args.length > 3))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/price [Item]");
			Utility.writeToLog(stringPlay + " incorrectly called /price");
			return false;
		}
		if(args.length == 1)
		{
			String name = args[0];
			
			if(args[0].equalsIgnoreCase("hand"))
			{
				int id = player.getInventory().getItemInHand().getTypeId();
				int dur = player.getInventory().getItemInHand().getDurability();
				
				if(dur == 0) name = String.valueOf(id);
				else name = id + ":" + dur;
			}
			
			String[] itemInfo = getAllInfo(name);
			String item = itemInfo[0];
			int checkid = Integer.parseInt(itemInfo[6]);
			
			if((checkid >= 2500) && (checkid < 2600))
			{
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&f" + item + "&2 is an enchantment. Please use &f/priceenchantment");
				Utility.writeToLog(stringPlay + " called /price on an enchantment");
				return false;
			}
			
			if(item.equals(""))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2This item does not exist.");
				Utility.writeToLog(stringPlay + " called /price on non-existant item " + args[0]);
				return false;
			}
			
			decFormat.applyPattern("#.##");
			
			String reqDesc = item + ".description";
			
			Double price = Double.valueOf(Double.parseDouble(itemInfo[1]));
			int stock = Integer.parseInt(itemInfo[5]);
			String desc = DynamicEconomy.itemConfig.getString(reqDesc, "");
			String priceStr = decFormat.format(price);
			
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2-----------------------------------");
			color.sendColouredMessage(
					player,
					DynamicEconomy.prefix + "&2Current Price of &f" + item + "&2 is &f" + DynamicEconomy.currencySymbol + priceStr);
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Description: &f" + desc);
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Stock: &f" + stock);
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2-----------------------------------");
			Utility.writeToLog(stringPlay + " called /price for item " + item);
			return true;
		}
		if(args.length == 2)
		{
			amt = DynamicEconomy.defaultAmount;
		}
		else if(args.length == 3)
		{
			String reqID = getTrueName(args[0]) + ".id";
			long itemID = DynamicEconomy.itemConfig.getLong(reqID);
			String reqStock = getTrueName(args[0]) + ".stock";
			int stock = DynamicEconomy.itemConfig.getInt(reqStock);
			
			String type1 = args[1];
			
			if((args[2].equalsIgnoreCase("all")) && (type1.equals("sell")))
			{
				amt = new ExtrasInventory(player).getAmountOf((int) itemID);
				amt--;
			}
			else if((args[2].equalsIgnoreCase("all")) && (type1.equals("buy"))) amt = stock;
			else amt = Integer.parseInt(args[2]);
		}
		
		String item = getTrueName(args[0]);
		String type = args[1];
		String reqDesc = item + ".description";
		double tax = 0.0D;
		
		String[] itemInfo = getAllInfo(item);
		
		Double price = Double.valueOf(Double.parseDouble(itemInfo[1]));
		int stock = Integer.parseInt(itemInfo[5]);
		String desc = DynamicEconomy.itemConfig.getString(reqDesc, "");
		
		EnderEngine engine = new EnderEngine(itemInfo);
		
		if(engine.getStock() < amt)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2There is only &f" + engine.getStock() + "&2 of " + item + " in stock");
			Utility.writeToLog(stringPlay + " tried to buy " + amt + " of " + item + ", but there was only " + engine
					.getStock() + " in stock.");
			return false;
		}
		
		if((type.equalsIgnoreCase("sale")) || (type.equalsIgnoreCase("sell")))
		{
			total = engine.getSale(amt);
			tax = DynamicEconomy.salestax;
			
			tax *= total;
			total -= tax;
		}
		else if((type.equalsIgnoreCase("purchase")) || (type.equalsIgnoreCase("buy")))
		{
			total = engine.getCost(amt);
			tax = DynamicEconomy.purchasetax;
			
			tax *= total;
			total += tax;
		}
		else
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2The type &f" + type + "&2 does not exist. Use either buy or sell.");
			Utility.writeToLog(stringPlay + " called invalid type '" + type + "'");
			return false;
		}
		
		String priceStr = decFormat.format(price);
		String totalStr = decFormat.format(total);
		tax = Double.valueOf(decFormat.format(tax)).doubleValue();
		
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&2-----------------------------------");
		color.sendColouredMessage(
				player,
				DynamicEconomy.prefix + "&2Current Price of &f" + item + "&2 is &f" + DynamicEconomy.currencySymbol + priceStr);
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Description: &f" + desc);
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Stock: &f" + stock);
		if((type.equalsIgnoreCase("sale")) || (type.equalsIgnoreCase("sell"))) color
		.sendColouredMessage(
				player,
				DynamicEconomy.prefix + "&2Selling &f" + amt + "&2 of &f" + item + "&2 + " + DynamicEconomy.currencySymbol + tax + " tax &fyields &2" + totalStr);
		else if((type.equalsIgnoreCase("purchase")) || (type.equalsIgnoreCase("buy")))
		{
			color.sendColouredMessage(
					player,
					DynamicEconomy.prefix + "&2Buying &f" + amt + "&2 of &f" + item + "&2 + " + DynamicEconomy.currencySymbol + tax + " tax &fcosts &2" + totalStr);
		}
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&2-----------------------------------");
		if(args.length == 2) Utility.writeToLog(stringPlay + " called /price " + args[0] + " " + args[1]);
		else if(args.length == 3)
		{
			Utility.writeToLog(stringPlay + " called /price " + args[0] + " " + args[1] + " " + args[2]);
		}
		return true;
	}
	
	public static void saveItemFile()
	{
		try
		{
			DynamicEconomy.itemConfig.save(DynamicEconomy.itemsFile);
		}
		catch(Exception e)
		{
			log.info("[DynamicEconomy] IOException saving Items.yml.");
			Utility.writeToLog("[DynamicEconomy] IOException saving Items.yml.");
		}
	}
	
	public static void setFloor(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if(args.length != 2)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/setfloor [Item] [FloorPrice]");
			Utility.writeToLog(stringPlay + " incorrectly called /setfloor");
		}
		else
		{
			String[] itemInfo = getAllInfo(args[0]);
			
			EnderEngine engine = new EnderEngine(itemInfo);
			double oldPrice = engine.getPrice();
			double ceiling = engine.getCeiling();
			double oldFloor = engine.getFloor();
			
			decFormat.applyPattern("#.##");
			
			double floor = Double.valueOf(args[1]).doubleValue();
			
			boolean withinbounds = floor < ceiling;
			
			if(itemInfo[0].equals(""))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.itemDoesntExist);
				Utility.writeToLog(stringPlay + " attempted to set the floor of the non-existent item '" + itemInfo[0] + "'");
			}
			else if(!withinbounds)
			{
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&2The ceiling of " + itemInfo[0] + " is lower than desired floor.");
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&2You must increase ceiling above desired floor, or set lower floor.");
				color.sendColouredMessage(
						player,
						DynamicEconomy.prefix + "&2DESIRED FLOOR: &f" + DynamicEconomy.currencySymbol + floor + " CEILING: &f" + DynamicEconomy.currencySymbol + ceiling);
				Utility.writeToLog(stringPlay + " attempted to set floor of " + itemInfo[0] + " to " + floor + ", but the price is lower than the desired floor.");
			}
			else
			{
				engine.setFloor(floor);
				engine.updatePrice();
				engine.updateConfig();
				double newPrice = engine.getPrice();
				
				double changeCeiling = Double.valueOf(decFormat.format(floor - oldFloor)).doubleValue();
				double change = Double.valueOf(decFormat.format(newPrice - oldPrice)).doubleValue();
				
				color.sendColouredMessage(
						player,
						DynamicEconomy.prefix + "&2Price Floor of &f" + itemInfo[0] + " set to&f " + DynamicEconomy.currencySymbol + floor);
				Utility.writeToLog(stringPlay + " set the floor of " + itemInfo[0] + " to " + floor);
				dataSigns.checkForUpdatesNonRegular(itemInfo[0], 0.0D, changeCeiling, 0.0D);
				dataSigns.checkForUpdates(itemInfo[0], 0, change);
			}
		}
	}
	
	public static void setCeiling(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if(args.length != 2)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/setceiling [Item] [CeilingPrice]");
			Utility.writeToLog(stringPlay + " incorrectly called /setceiling");
		}
		else
		{
			String[] itemInfo = getAllInfo(args[0]);
			
			EnderEngine engine = new EnderEngine(itemInfo);
			double oldPrice = engine.getPrice();
			double floor = engine.getFloor();
			double oldCeiling = engine.getCeiling();
			
			decFormat.applyPattern("#.##");
			
			double ceiling = Double.valueOf(args[1]).doubleValue();
			
			boolean withinbounds = ceiling > floor;
			
			if(itemInfo[0].equals(""))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.itemDoesntExist);
				Utility.writeToLog(stringPlay + " attempted to set the floor of the non-existent item '" + itemInfo[0] + "'");
			}
			else if(!withinbounds)
			{
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&2The floor of " + itemInfo[0] + " is higher than desired ceiling.");
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&2You must decrease floor to below desired ceiling, or set higher ceiling.");
				color.sendColouredMessage(
						player,
						DynamicEconomy.prefix + "&2DESIRED CEILING: &f" + DynamicEconomy.currencySymbol + ceiling + " FLOOR: &f" + DynamicEconomy.currencySymbol + floor);
				Utility.writeToLog(stringPlay + " attempted to set ceiling of " + itemInfo[0] + " to " + ceiling + ", but the floor is higher than the desired ceiling.");
			}
			else
			{
				engine.setCeiling(ceiling);
				engine.updatePrice();
				engine.updateConfig();
				double newPrice = engine.getPrice();
				
				double changeCeiling = Double.valueOf(decFormat.format(ceiling - oldCeiling)).doubleValue();
				double change = Double.valueOf(decFormat.format(newPrice - oldPrice)).doubleValue();
				
				color.sendColouredMessage(
						player,
						DynamicEconomy.prefix + "&2Price Ceiling of &f" + itemInfo[0] + " set to &f" + DynamicEconomy.currencySymbol + ceiling);
				Utility.writeToLog(stringPlay + " set the floor of " + itemInfo[0] + " to " + ceiling);
				dataSigns.checkForUpdatesNonRegular(itemInfo[0], 0.0D, changeCeiling, 0.0D);
				dataSigns.checkForUpdates(itemInfo[0], 0, change);
			}
		}
	}
	
	public static void getFloor(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if(args.length != 1)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/getfloor [Item]");
			Utility.writeToLog(stringPlay + " incorrectly called /getfloor");
		}
		else
		{
			String item = getTrueName(args[0]);
			String request = item + ".floor";
			
			if(item.equals(""))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.itemDoesntExist);
				Utility.writeToLog(stringPlay + " attempted to get the floor of the non-existent item '" + item + "'");
			}
			else
			{
				Double floor = Double.valueOf(DynamicEconomy.itemConfig.getDouble(request, 0.0D));
				decFormat.applyPattern("#.##");
				floor = Double.valueOf(decFormat.format(floor));
				color.sendColouredMessage(
						player,
						DynamicEconomy.prefix + "&2Price Floor of &f" + item + " &2is &f" + DynamicEconomy.currencySymbol + floor);
				Utility.writeToLog(stringPlay + " called /getfloor for item '" + item + "'");
			}
		}
	}
	
	public static boolean isEnchantment(String item)
	{
		String trueName = getTrueName(item);
		
		int id = DynamicEconomy.itemConfig.getInt(trueName + ".id");
		
		if((id > 2500) && (id < 2600)) { return true; }
		return false;
	}
	
	public static void getCeiling(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if(args.length != 1)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/getceiling [Item]");
			Utility.writeToLog(stringPlay + " incorrectly called /getceiling");
		}
		else
		{
			String item = getTrueName(args[0]);
			String request = item + ".ceiling";
			
			if(item.equals(""))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.itemDoesntExist);
				Utility.writeToLog(stringPlay + " attempted to get the ceiling of the non-existent item '" + item + "'");
			}
			else
			{
				Double ceiling = Double.valueOf(DynamicEconomy.itemConfig.getDouble(request, 0.0D));
				decFormat.applyPattern("#.##");
				ceiling = Double.valueOf(decFormat.format(ceiling));
				color.sendColouredMessage(
						player,
						DynamicEconomy.prefix + "&2Price Ceiling of &f" + item + "&2 is &f" + DynamicEconomy.currencySymbol + ceiling);
				Utility.writeToLog(stringPlay + " called /getceiling for item '" + item + "'");
			}
		}
	}
	
	public static void getSpan(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if(args.length != 1)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/getspan [Item]");
			Utility.writeToLog(stringPlay + " incorrectly called /getspan");
		}
		else
		{
			String item = getTrueName(args[0]);
			String request = item + ".span";
			
			if(item.equals(""))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.itemDoesntExist);
				Utility.writeToLog(stringPlay + " attempted to get the velocity of the non-existent item '" + item + "'");
			}
			else
			{
				Double span = Double.valueOf(DynamicEconomy.itemConfig.getDouble(request, 0.0D));
				decFormat.applyPattern("#.##");
				span = Double.valueOf(decFormat.format(span));
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Span of &f" + item + " &2is &f" + span);
				Utility.writeToLog(stringPlay + " called /getspan for item '" + item + "'");
			}
		}
	}
	
	public static void setSpan(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if(args.length != 2)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/setspan [Item] [Span]");
			Utility.writeToLog(stringPlay + " incorrectly called /setspan");
		}
		else
		{
			String item = getTrueName(args[0]);
			Double span = Double.valueOf(Double.parseDouble(args[1]));
			Double oldSpan = Double.valueOf(DynamicEconomy.itemConfig.getDouble(item + ".span", 100.0D));
			Double change = Double.valueOf(span.doubleValue() - oldSpan.doubleValue());
			
			decFormat.applyPattern("#.##");
			span = Double.valueOf(decFormat.format(span));
			
			String request = item + ".span";
			
			if(item.equals(""))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.itemDoesntExist);
				Utility.writeToLog(stringPlay + " attempted to set the span of the non-existent item '" + item + "'");
			}
			else
			{
				DynamicEconomy.itemConfig.set(request, span);
				saveItemFile();
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Span of &f" + item + " &2set to &f" + span);
				Utility.writeToLog(stringPlay + " set the span of " + item + " to " + span);
				dataSigns.checkForUpdatesNonRegular(item, change.doubleValue(), 0.0D, 0.0D);
			}
		}
	}
	
	public static String[] getAllInfo(String item)
	{
		item = getTrueName(item);
		
		String requestPrice = item + ".price";
		String requestFloor = item + ".floor";
		String requestCeiling = item + ".ceiling";
		String requestSpan = item + ".span";
		String requestStock = item + ".stock";
		String requestID = item + ".id";
		
		double price = DynamicEconomy.itemConfig.getDouble(requestPrice, 0.0D);
		double floor = DynamicEconomy.itemConfig.getDouble(requestFloor, 0.0D);
		double ceiling = DynamicEconomy.itemConfig.getDouble(requestCeiling, 0.0D);
		double span = DynamicEconomy.itemConfig.getDouble(requestSpan, 0.0D);
		int stock = DynamicEconomy.itemConfig.getInt(requestStock, 0);
		long id = DynamicEconomy.itemConfig.getLong(requestID, 0L);
		
		decFormat.applyPattern("##.#####");
		decFormat.setGroupingUsed(false);
		
		String priceStr = decFormat.format(price);
		String floorStr = Double.toString(floor);
		String ceilingStr = Double.toString(ceiling);
		String spanStr = Double.toString(span);
		String stockStr = Integer.toString(stock);
		String idStr = Long.toString(id);
		
		String[] info = { item, priceStr, floorStr, ceilingStr, spanStr, stockStr, idStr };
		
		return info;
	}
	
	public static int getMaxDur(String itemName)
	{
		int maxDur = 0;
		
		if(itemName.equals("WOOD_PICKAXE")) maxDur = Material.WOOD_PICKAXE.getMaxDurability();
		else if(itemName.equals("WOOD_AXE")) maxDur = Material.WOOD_AXE.getMaxDurability();
		else if(itemName.equals("WOOD_SPADE")) maxDur = Material.WOOD_SPADE.getMaxDurability();
		else if(itemName.equals("WOOD_HOE")) maxDur = Material.WOOD_HOE.getMaxDurability();
		else if(itemName.equals("WOOD_SWORD"))
		{
			maxDur = Material.WOOD_SWORD.getMaxDurability();
		}
		
		if(itemName.equals("STONE_PICKAXE")) maxDur = Material.STONE_PICKAXE.getMaxDurability();
		else if(itemName.equals("STONE_AXE")) maxDur = Material.STONE_AXE.getMaxDurability();
		else if(itemName.equals("STONE_SPADE")) maxDur = Material.STONE_SPADE.getMaxDurability();
		else if(itemName.equals("STONE_HOE")) maxDur = Material.STONE_HOE.getMaxDurability();
		else if(itemName.equals("STONE_SWORD"))
		{
			maxDur = Material.STONE_SWORD.getMaxDurability();
		}
		
		if(itemName.equals("IRON_PICKAXE")) maxDur = Material.IRON_PICKAXE.getMaxDurability();
		else if(itemName.equals("IRON_AXE")) maxDur = Material.IRON_AXE.getMaxDurability();
		else if(itemName.equals("IRON_SPADE")) maxDur = Material.IRON_SPADE.getMaxDurability();
		else if(itemName.equals("IRON_HOE")) maxDur = Material.IRON_HOE.getMaxDurability();
		else if(itemName.equals("IRON_SWORD"))
		{
			maxDur = Material.IRON_SWORD.getMaxDurability();
		}
		
		if(itemName.equals("GOLD_PICKAXE")) maxDur = Material.GOLD_PICKAXE.getMaxDurability();
		else if(itemName.equals("GOLD_AXE")) maxDur = Material.GOLD_AXE.getMaxDurability();
		else if(itemName.equals("GOLD_SPADE")) maxDur = Material.GOLD_SPADE.getMaxDurability();
		else if(itemName.equals("GOLD_HOE")) maxDur = Material.GOLD_HOE.getMaxDurability();
		else if(itemName.equals("GOLD_SWORD"))
		{
			maxDur = Material.GOLD_SWORD.getMaxDurability();
		}
		
		if(itemName.equals("DIAMOND_PICKAXE")) maxDur = Material.DIAMOND_PICKAXE.getMaxDurability();
		else if(itemName.equals("DIAMOND_AXE")) maxDur = Material.DIAMOND_AXE.getMaxDurability();
		else if(itemName.equals("DIAMOND_SPADE")) maxDur = Material.DIAMOND_SPADE.getMaxDurability();
		else if(itemName.equals("DIAMOND_HOE")) maxDur = Material.DIAMOND_HOE.getMaxDurability();
		else if(itemName.equals("DIAMOND_SWORD"))
		{
			maxDur = Material.DIAMOND_SWORD.getMaxDurability();
		}
		
		if(itemName.equals("IRON_HELMET")) maxDur = Material.IRON_HELMET.getMaxDurability();
		else if(itemName.equals("IRON_CHESTPLATE")) maxDur = Material.IRON_CHESTPLATE.getMaxDurability();
		else if(itemName.equals("IRON_LEGGINGS")) maxDur = Material.IRON_LEGGINGS.getMaxDurability();
		else if(itemName.equals("IRON_BOOTS"))
		{
			maxDur = Material.IRON_BOOTS.getMaxDurability();
		}
		
		if(itemName.equals("GOLD_HELMET")) maxDur = Material.GOLD_HELMET.getMaxDurability();
		else if(itemName.equals("GOLD_CHESTPLATE")) maxDur = Material.GOLD_CHESTPLATE.getMaxDurability();
		else if(itemName.equals("GOLD_LEGGINGS")) maxDur = Material.GOLD_LEGGINGS.getMaxDurability();
		else if(itemName.equals("GOLD_BOOTS"))
		{
			maxDur = Material.GOLD_BOOTS.getMaxDurability();
		}
		
		if(itemName.equals("DIAMOND_HELMET")) maxDur = Material.DIAMOND_HELMET.getMaxDurability();
		else if(itemName.equals("DIAMOND_CHESTPLATE")) maxDur = Material.DIAMOND_CHESTPLATE.getMaxDurability();
		else if(itemName.equals("DIAMOND_LEGGINGS")) maxDur = Material.DIAMOND_LEGGINGS.getMaxDurability();
		else if(itemName.equals("DIAMOND_BOOTS"))
		{
			maxDur = Material.DIAMOND_BOOTS.getMaxDurability();
		}
		
		if(itemName.equals("LEATHER_HELMET")) maxDur = Material.LEATHER_HELMET.getMaxDurability();
		else if(itemName.equals("LEATHER_CHESTPLATE")) maxDur = Material.LEATHER_CHESTPLATE.getMaxDurability();
		else if(itemName.equals("LEATHER_LEGGINGS")) maxDur = Material.LEATHER_LEGGINGS.getMaxDurability();
		else if(itemName.equals("LEATHER_BOOTS"))
		{
			maxDur = Material.LEATHER_BOOTS.getMaxDurability();
		}
		
		return maxDur;
	}
	
	public static void banItem(Player player, String[] args)
	{
		if(args.length != 2)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/banitem [Item] [sale|purchase]");
			Utility.writeToLog(player.getName() + " incorrectly called /banitem");
		}
		else
		{
			String item = getTrueName(args[0]);
			if(item.equals(""))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2This item does not exist.");
				Utility.writeToLog(player.getName() + " called /banitem on the non-existant item " + args[0]);
			}
			else if((args[1].equalsIgnoreCase("sale")) || (args[1].equalsIgnoreCase("purchase")))
			{
				try
				{
					DynamicEconomy.config.load(DynamicEconomy.configFile);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				FileConfiguration conf = DynamicEconomy.config;
				
				String bannedString = "";
				
				if(args[1].equalsIgnoreCase("sale")) bannedString = conf.getString("banned-sale-items", "");
				else if(args[1].equalsIgnoreCase("purchase"))
				{
					bannedString = conf.getString("banned-purchase-items", "");
				}
				
				String banToken = "";
				
				if(bannedString.length() == 0) banToken = item;
				else
				{
					banToken = "," + item;
				}
				
				if(args[1].equalsIgnoreCase("sale")) conf.set("banned-sale-items", bannedString + banToken);
				else if(args[1].equalsIgnoreCase("purchase"))
				{
					conf.set("banned-purchase-items", bannedString + banToken);
				}
				try
				{
					DynamicEconomy.config.save(DynamicEconomy.configFile);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				DynamicEconomy.relConfig();
				
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&f" + args[0] + "&2 banned from &f" + args[1] + "&2 succesfully.");
				Utility.writeToLog(player.getName() + " banned " + args[0] + " from " + args[1]);
			}
			else
			{
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&f" + args[1] + "&2 is not a valid type. Only use &fsale &2or &fpurchase");
				Utility.writeToLog(player.getName() + " called /banitem with invalid type '" + args[1] + "'");
			}
		}
	}
	
	public static void unbanItem(Player player, String[] args)
	{
		if(args.length != 2)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/unbanitem [Item] [sale|purchase]");
			Utility.writeToLog(player.getName() + " incorrectly called /unbanitem");
		}
		else
		{
			String item = getTrueName(args[0]);
			if(item.equals(""))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2This item does not exist.");
				Utility.writeToLog(player.getName() + " called /unbanitem on the non-existant item " + args[0]);
			}
			else if((args[1].equalsIgnoreCase("sale")) || (args[1].equalsIgnoreCase("purchase")))
			{
				try
				{
					DynamicEconomy.config.load(DynamicEconomy.configFile);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				FileConfiguration conf = DynamicEconomy.config;
				
				String bannedString = "";
				
				if(args[1].equalsIgnoreCase("sale")) bannedString = conf.getString("banned-sale-items", "");
				else if(args[1].equalsIgnoreCase("purchase"))
				{
					bannedString = conf.getString("banned-purchase-items", "");
				}
				
				String[] bannedArray = bannedString.split(",");
				boolean contains = Arrays.asList(bannedArray).contains(item);
				
				if(contains)
				{
					ArrayList<String> bannedArrayList = new ArrayList<String>(Arrays.asList(bannedArray));
					bannedArrayList.remove(item);
					
					String newBannedString = "";
					
					for(int x = 0; x < bannedArrayList.size(); x++)
					{
						if(x == 0) newBannedString = newBannedString + (String) bannedArrayList.get(x);
						else
						{
							newBannedString = newBannedString + "," + (String) bannedArrayList.get(x);
						}
					}
					
					if(args[1].equalsIgnoreCase("sale")) conf.set("banned-sale-items", newBannedString);
					else if(args[1].equalsIgnoreCase("purchase"))
					{
						conf.set("banned-purchase-items", newBannedString);
					}
					
					try
					{
						DynamicEconomy.config.save(DynamicEconomy.configFile);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					DynamicEconomy.relConfig();
					
					color.sendColouredMessage(player,
							DynamicEconomy.prefix + "&f" + args[0] + "&2 unbanned from &f" + args[1] + "&2 succesfully.");
					Utility.writeToLog(player.getName() + " unbanned " + args[0] + " from " + args[1]);
				}
				else
				{
					color.sendColouredMessage(player, DynamicEconomy.prefix + "&2This item is not banned.");
					Utility.writeToLog(player.getName() + " called /unbanitem on " + args[0] + " which is not banned.");
				}
				
			}
			else
			{
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&f" + args[1] + "&2 is not a valid type. Only use &fsale &2or &fpurchase");
				Utility.writeToLog(player.getName() + " called /unbanitem with invalid type '" + args[1] + "'");
			}
		}
	}
	
	public static void getDurCommand(Player player, String[] args)
	{
		String stringPlay = player.getName();
		
		if(args.length == 0)
		{
			ItemStack playerItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
			int itemID = playerItem.getTypeId();
			
			if(((itemID >= 256) && (itemID <= 258)) || ((itemID >= 267) && (itemID <= 279)) || ((itemID >= 298) && (itemID <= 317)) || ((itemID >= 283) && (itemID <= 286)) || ((itemID >= 290) && (itemID <= 294)))
			{
				int playerDur = playerItem.getDurability();
				
				String itemName = playerItem.getType().toString();
				
				int maxDur = getMaxDur(itemName);
				
				int usesLeft = maxDur - playerDur;
				
				double percentDur = playerDur / maxDur;
				percentDur *= 100.0D;
				percentDur = 100.0D - percentDur;
				decFormat.applyPattern("#.##");
				percentDur = Double.valueOf(decFormat.format(percentDur)).doubleValue();
				
				color.sendColouredMessage(
						player,
						"&2The durability of your item is at &f" + percentDur + "%.&2 You have &f" + usesLeft + "&2 uses left out of a possible &f" + maxDur);
				Utility.writeToLog(stringPlay + " called /getdurability for item with ID of '" + itemID + "'");
			}
			else
			{
				color.sendColouredMessage(player, Messages.itemHasNoDurability);
				Utility.writeToLog(stringPlay + " called /getdurability for item with ID of '" + itemID + "', but failed because this item has no durability.");
			}
		}
		else if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("armor"))
			{
				ItemStack[] armor = player.getInventory().getArmorContents();
				int amountHelmet = armor[0].getAmount();
				int amountChestplate = armor[1].getAmount();
				int amountLeggings = armor[2].getAmount();
				int amountBoots = armor[3].getAmount();
				
				if((amountHelmet == 0) && (amountChestplate == 0) && (amountLeggings == 0) && (amountBoots == 0))
				{
					color.sendColouredMessage(player, Messages.noArmorEquipped);
					Utility.writeToLog(stringPlay + " attempted to call /getdurability armor, but has no armor equipped");
				}
				else
				{
					for(int x = 0; x < armor.length; x++)
					{
						int playerDur = armor[x].getDurability();
						String itemName = armor[x].getType().toString();
						int maxDur = getMaxDur(itemName);
						int usesLeft = maxDur - playerDur;
						int amount = armor[x].getAmount();
						double percentDur = playerDur / maxDur;
						percentDur *= 100.0D;
						percentDur = 100.0D - percentDur;
						if(amount == 0)
						{
							if(x == 3)
							{
								color.sendColouredMessage(player, Messages.noHelmetEquipped);
								Utility.writeToLog(stringPlay + " called /getdurability armor without having a helmet equipped");
							}
							else if(x == 2)
							{
								color.sendColouredMessage(player, Messages.noChestplateEquipped);
								Utility.writeToLog(stringPlay + " called /getdurability armor without having a chestplate equipped");
							}
							else if(x == 1)
							{
								color.sendColouredMessage(player, Messages.noLeggingsEquipped);
								Utility.writeToLog(stringPlay + " called /getdurability armor without having leggings equipped");
							}
							else
							{
								color.sendColouredMessage(player, Messages.noBootsEquipped);
								Utility.writeToLog(stringPlay + " called /getdurability armor without having boots equipped");
							}
						}
						else
						{
							percentDur = Double.valueOf(decFormat.format(percentDur)).doubleValue();
							color.sendColouredMessage(
									player,
									"&2The durability of &f" + itemName + "&2 is at &f" + percentDur + "%.&2 You have &f" + usesLeft + "&2 uses left out of a possible &f" + maxDur);
							Utility.writeToLog(stringPlay + " called /getdurability and found the durability of their '" + itemName + "' to be " + percentDur + "%");
						}
					}
				}
			}
			else if(args[0].equalsIgnoreCase("helmet"))
			{
				ItemStack helmet = player.getInventory().getHelmet();
				int amount = helmet.getAmount();
				if(amount == 1)
				{
					int playerDur = helmet.getDurability();
					String itemName = helmet.getType().toString();
					int maxDur = getMaxDur(itemName);
					int usesLeft = maxDur - playerDur;
					double percentDur = playerDur / maxDur;
					percentDur *= 100.0D;
					percentDur = 100.0D - percentDur;
					percentDur = Double.valueOf(decFormat.format(percentDur)).doubleValue();
					color.sendColouredMessage(
							player,
							"&2The durability of &f" + itemName + "&2 is at &f" + percentDur + "%.&2 You have &f" + usesLeft + "&2 uses left out of a possible &f" + maxDur);
					Utility.writeToLog(stringPlay + " called /getdurability and found the durability of their '" + itemName + "' to be " + percentDur + "%");
				}
				else
				{
					color.sendColouredMessage(player, Messages.noHelmetEquipped);
					Utility.writeToLog(stringPlay + " attempted to call /getdurability helmet, but did not have a helmet equipped");
				}
			}
			else if(args[0].equalsIgnoreCase("chestplate"))
			{
				ItemStack chestplate = player.getInventory().getChestplate();
				int amount = chestplate.getAmount();
				if(amount == 1)
				{
					int playerDur = chestplate.getDurability();
					String itemName = chestplate.getType().toString();
					int maxDur = getMaxDur(itemName);
					int usesLeft = maxDur - playerDur;
					double percentDur = playerDur / maxDur;
					percentDur *= 100.0D;
					percentDur = 100.0D - percentDur;
					percentDur = Double.valueOf(decFormat.format(percentDur)).doubleValue();
					color.sendColouredMessage(
							player,
							"&2The durability of &f" + itemName + "&2 is at &f" + percentDur + "%.&2 You have &f" + usesLeft + "&2 uses left out of a possible &f" + maxDur);
					Utility.writeToLog(stringPlay + " called /getdurability and found the durability of their '" + itemName + "' to be " + percentDur + "%");
				}
				else
				{
					color.sendColouredMessage(player, Messages.noChestplateEquipped);
					Utility.writeToLog(stringPlay + " attempted to call /getdurability chestplate, but did not have a chestplate equipped");
				}
			}
			else if(args[0].equalsIgnoreCase("leggings"))
			{
				ItemStack leggings = player.getInventory().getLeggings();
				int amount = leggings.getAmount();
				if(amount == 1)
				{
					int playerDur = leggings.getDurability();
					String itemName = leggings.getType().toString();
					int maxDur = getMaxDur(itemName);
					int usesLeft = maxDur - playerDur;
					double percentDur = playerDur / maxDur;
					percentDur *= 100.0D;
					percentDur = 100.0D - percentDur;
					percentDur = Double.valueOf(decFormat.format(percentDur)).doubleValue();
					color.sendColouredMessage(
							player,
							"&2The durability of &f" + itemName + "&2 is at &f" + percentDur + "%.&2 You have &f" + usesLeft + "&2 uses left out of a possible &f" + maxDur);
					Utility.writeToLog(stringPlay + " called /getdurability and found the durability of their '" + itemName + "' to be " + percentDur + "%");
				}
				else
				{
					color.sendColouredMessage(player, Messages.noLeggingsEquipped);
					Utility.writeToLog(stringPlay + " attempted to call /getdurability leggings, but did not have leggings equipped");
				}
			}
			else if(args[0].equalsIgnoreCase("boots"))
			{
				ItemStack boots = player.getInventory().getBoots();
				int amount = boots.getAmount();
				if(amount == 1)
				{
					int playerDur = boots.getDurability();
					String itemName = boots.getType().toString();
					int maxDur = getMaxDur(itemName);
					int usesLeft = maxDur - playerDur;
					double percentDur = playerDur / maxDur;
					percentDur *= 100.0D;
					percentDur = 100.0D - percentDur;
					percentDur = Double.valueOf(decFormat.format(percentDur)).doubleValue();
					color.sendColouredMessage(
							player,
							"&2The durability of &f" + itemName + "&2 is at &f" + percentDur + "%.&2 You have &f" + usesLeft + "&2 uses left out of a possible &f" + maxDur);
					Utility.writeToLog(stringPlay + " called /getdurability and found the durability of their '" + itemName + "' to be " + percentDur + "%");
				}
				else
				{
					color.sendColouredMessage(player, Messages.noBootsEquipped);
					Utility.writeToLog(stringPlay + " attempted to call /getdurability boots, but did not have boots equipped");
				}
			}
			else
			{
				color.sendColouredMessage(
						player,
						DynamicEconomy.prefix + "&2Wrong Command Usage. &f/getdurability (helmet/chestplate/leggings/boots/armor)");
				Utility.writeToLog(stringPlay + " incorrectly called /getdurability");
			}
		}
	}
}
