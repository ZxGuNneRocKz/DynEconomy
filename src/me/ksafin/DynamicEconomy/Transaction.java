package me.ksafin.DynamicEconomy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import couk.Adamki11s.Extras.Colour.ExtrasColour;
import couk.Adamki11s.Extras.Inventory.ExtrasInventory;

public class Transaction implements Runnable
{
	private static ExtrasColour color = new ExtrasColour();
	private static Logger log = Logger.getLogger("Minecraft");
	public static FileConfiguration regionConfigFile;
	static NumberFormat f = NumberFormat.getNumberInstance(Locale.US);
	public static DecimalFormat decFormat = (DecimalFormat) f;
	public static DecimalFormat changeFormat = (DecimalFormat) f;
	
	public static boolean buy(Player player, String[] args, boolean sign)
	{
		String stringPlay = player.getName();
		
		if((args.length == 0) || (args.length > 2))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/buy [Item] (Amount)");
			Utility.writeToLog(stringPlay + " incorrectly called /buy");
			return false;
		}
		
		double tax = DynamicEconomy.purchasetax;
		String[] itemInfo = new String[7];
		try
		{ itemInfo = Item.getAllInfo(args[0]); }
		catch(Exception e)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + 
					"&2You entered the command arguments in the wrong order, or your item name was invalid ");
			Utility.writeToLog(stringPlay + " entered an invalid item, or entered command arguments in the wrong order");
			return false;
		}
		
		String itemName = itemInfo[0];
		double itemPrice = Double.parseDouble(itemInfo[1]);
		int itemStock = Integer.parseInt(itemInfo[5]);
		long itemID = Long.parseLong(itemInfo[6]);
		
		if((itemID >= 2500L) && (itemID < 2600L))
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&f" + itemName + "&2 is an enchantment. Use &f/buyenchantment&2 to buy it.");
			Utility.writeToLog(stringPlay + "tried to buy the enchantment " + itemName + 
					" via /buy instead of /buyenchantment.");
			return false;
		}
		
		if(itemName.equals(""))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.itemDoesntExist);
			Utility.writeToLog(stringPlay + " attempted to buy the non-existent item '" + itemName + "'");
			return false;
		}
		
		for(int x = 0; x < DynamicEconomy.bannedPurchaseItems.length; x++)
		{
			String bannedItem = Item.getTrueName(DynamicEconomy.bannedPurchaseItems[x]);
			if(bannedItem.equals(itemName))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.bannedItem);
				Utility.writeToLog(stringPlay + " attempted to buy the banned item: " + bannedItem);
				return false;
			}
		}
		
		int purchaseAmount = 0;
		
		if(args.length == 1) purchaseAmount = DynamicEconomy.defaultAmount;
		else if(args.length == 2)
		{
			if(args[1].equalsIgnoreCase("all")) purchaseAmount = itemStock;
			else
			{
				try
				{ purchaseAmount = Integer.parseInt(args[1]); }
				catch(Exception e)
				{
					color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.invalidCommandArgs);
					Utility.writeToLog(stringPlay +
							" entered an invalid purchase amount, or entered command arguments in the wrong order.");
					return false;
				}
			}
		}
		
		ExtrasInventory inv = new ExtrasInventory(player);
		int emptySlotAmount = inv.getEmptySlots();
		int reqSlot = 0;
		
		if(((itemID >= 256L) && (itemID <= 258L)) || ((itemID >= 267L) && (itemID <= 279L)) ||
				((itemID >= 298L) && (itemID <= 317L)) || ((itemID >= 283L) && (itemID <= 286L)) ||
				((itemID >= 290L) && (itemID <= 294L)))
			reqSlot = purchaseAmount;
		else reqSlot = (int) (purchaseAmount / 64.0D + 0.99D);
		
		if(reqSlot > emptySlotAmount)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2You need &f" + reqSlot + "&2 empty slots, but have &f" + emptySlotAmount);
			Utility.writeToLog(stringPlay + " attempted to buy " + purchaseAmount + " of '" + itemName + 
					"', but didn't have enough space.");
			return false;
		}
		
		if(purchaseAmount <= 0)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.negativeBuyAmount);
			Utility.writeToLog(stringPlay + " attempted to buy " + purchaseAmount + " of '" + itemName + 
					"', but this amount is invalid.");
			return false;
		}
		
		double balance = DynamicEconomy.economy.getBalance(player.getName());
		
		if(itemStock < purchaseAmount)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.notEnoughStock);
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Your request: &f" + decFormat
					.format(purchaseAmount) + "   &2Current Stock: &f" + itemStock);
			Utility.writeToLog(stringPlay + " attempted to buy " + decFormat.format(purchaseAmount) + " of '" + itemName + 
					"' but there was only " + itemStock + " remaining");
			return false;
		}
		
		EnderEngine engine = new EnderEngine(itemInfo);
		double totalCost = (sign) ? engine.getCost(1) : engine.getCost(purchaseAmount);
		double newPrice = engine.getPrice();
		int newStock = engine.getStock();
		engine.setBuyTime();
		
		double change = newPrice - itemPrice;
		
		int changeStock = 0;
		changeStock = newStock - itemStock;
		
		double percentTax = tax * 100.0D;
		tax *= totalCost;
		totalCost += tax;
		
		if(DynamicEconomy.depositTax)
		{
			try
			{
				if(DynamicEconomy.taxAccountIsBank) DynamicEconomy.economy.bankDeposit(DynamicEconomy.taxAccount, tax);
				else DynamicEconomy.economy.depositPlayer(DynamicEconomy.taxAccount, tax);
			}
			catch(Exception e)
			{
				log.info("Tax-Account " + DynamicEconomy.taxAccount + " not found.");
				Utility.writeToLog("Attempted to deposit tax of " + DynamicEconomy.currencySymbol + tax + " to account " + DynamicEconomy.taxAccount + " but account not found.");
			}
		}
		
		decFormat.applyPattern("#.##");
		
		if(balance < totalCost)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.notEnoughMoney);
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Your balance: &f" + DynamicEconomy.currencySymbol + 
					decFormat.format(balance) + "   &2Your order total: &f" + DynamicEconomy.currencySymbol +
					decFormat.format(totalCost));
			Utility.writeToLog(stringPlay + " attempted to buy " + decFormat.format(purchaseAmount) +
					" of '" + itemName + "' for " + decFormat.format(totalCost) + " but could not afford it.");
			return false;
		}
		
		DynamicEconomy.economy.withdrawPlayer(player.getName(), totalCost);
		
		color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.purchaseSuccess);
		Utility.writeToLog(stringPlay + " bought " + purchaseAmount + " of '" + itemName + "' for " + totalCost);
		
		totalCost = Double.valueOf(decFormat.format(totalCost)).doubleValue();
		itemPrice = Double.valueOf(decFormat.format(itemPrice)).doubleValue();
		
		color.sendColouredMessage(player,
				DynamicEconomy.prefix + "&fYou bought " + purchaseAmount + " &2of " + itemName + "&f + " +
				decFormat.format(percentTax) + "&2% tax = &f" + DynamicEconomy.currencySymbol + totalCost + " &2TOTAL");
		
		changeFormat.applyPattern("#.#####");
		newPrice = Double.valueOf(decFormat.format(newPrice)).doubleValue();
		change = Double.valueOf(changeFormat.format(change)).doubleValue();
		
		if(itemPrice != newPrice)
		{
			if(DynamicEconomy.globalNotify)
				for(Player p : Bukkit.getServer().getOnlinePlayers())
					if((!Utility.isQuiet(p)) && (!p.equals(player)))
						color.sendColouredMessage(p,
								DynamicEconomy.prefix + "&2New Price of &f" + itemName + "&2 is &f" + 
								DynamicEconomy.currencySymbol + newPrice + "&2 (+" + change + ")");
			
			if(DynamicEconomy.localNotify)
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&2New Price of &f" + itemName + "&2 is &f" + 
						DynamicEconomy.currencySymbol + newPrice + "&2 (+" + change + ")");
			
			Utility.writeToLog(DynamicEconomy.prefix + " New price of " + itemName + " changed dynamically to " + newPrice + "(+" + change + ")");
		}
		
		int mat = 0;
		short dmg = 0;
		
		if(itemID > 3000L)
		{
			mat = getMat(itemID);
			dmg = getDmg(itemID);
			ItemStack items = new ItemStack(mat, purchaseAmount, dmg);
			inv.addToInventory(new ItemStack[] { items });
		}
		else if(itemID == 999L) player.giveExp(purchaseAmount);
		else if(((itemID >= 256L) && (itemID <= 258L)) || ((itemID >= 267L) && (itemID <= 279L)) || 
				((itemID >= 298L) && (itemID <= 317L)) || ((itemID >= 283L) && (itemID <= 286L)) || 
				((itemID >= 290L) && (itemID <= 294L)))
			for(int x = 0; x < purchaseAmount; x++)
				inv.addToInventory((int) itemID, 1);
		else inv.addToInventory((int) itemID, purchaseAmount);
		
		engine.updateConfig();
		dataSigns.checkForUpdates(itemName, changeStock, change);
		DynamicShop.updateItem(itemName);
		return true;
	}
	
	private static int getMat(long itemID)
	{
		String idStr = String.valueOf(itemID);
		String[] split = idStr.split("00");
		int mat = Integer.parseInt(split[0]);
		return mat;
	}
	
	private static short getDmg(long itemID)
	{
		String idStr = String.valueOf(itemID);
		String[] split = idStr.split("00");
		short dmg = Short.parseShort(split[1]);
		return dmg;
	}
	
	public static boolean sellInventory(Player player)
	{
		decFormat.setGroupingUsed(false);
		
		Inventory inv = player.getInventory();
		
		ItemStack[] contents = inv.getContents();
		String[] itemInfo = new String[7];
		
		double totalSale = 0.0D;
		
		double maxDur = 0.0D;
		
		double tax = DynamicEconomy.salestax;
		
		String itemSearchID = "";
		
		int changeStock = 0;
		
		ArrayList<String> banned = new ArrayList<String>();
		
		for(int i = 0; i < DynamicEconomy.bannedSaleItems.length; i++) banned.add(DynamicEconomy.bannedSaleItems[i]);
		
		for(ItemStack item : contents)
		{
			if(item != null)
			{
				double dur = item.getDurability();
				int itemID = item.getTypeId();
				
				if((dur == 0.0D) || ((itemID >= 256) && (itemID <= 258)) || ((itemID >= 267) && (itemID <= 279)) || 
						((itemID >= 298) && (itemID <= 317)) || ((itemID >= 283) && (itemID <= 286)) || 
						((itemID >= 290) && (itemID <= 294)))
					itemSearchID = String.valueOf(item.getTypeId());
				else itemSearchID = item.getTypeId() + ":" + (int) dur;
				
				itemInfo = Item.getAllInfo(itemSearchID);
				
				String itemName = itemInfo[0];
				double itemPrice = Double.parseDouble(itemInfo[1]);
				int itemStock = Integer.parseInt(itemInfo[5]);
				int saleAmount = item.getAmount();
				
				if(banned.contains(item))
				{
					color.sendColouredMessage(player,
							DynamicEconomy.prefix + "&2You are not permitted to sell &f" + itemName);
					Utility.writeToLog(player.getName() + " tried to sell " + itemName + " but was denied access.");
				}
				else
				{
					double newPrice = 0.0D;
					int newStock = 0;
						
					EnderEngine engine = new EnderEngine(itemInfo);
						
					if(((itemID >= 256) && (itemID <= 258)) || ((itemID >= 267) && (itemID <= 279)) || 
							((itemID >= 298) && (itemID <= 317)) || ((itemID >= 283) && (itemID <= 286)) || 
							((itemID >= 290) && (itemID <= 294)))
					{
							engine.incrementStock(1);
										
							short playerDur = item.getDurability();
							String itemMCname = item.getType().toString();
							maxDur = Item.getMaxDur(itemMCname);
							playerDur = (short)(int)(maxDur - playerDur);
							double percentDur = playerDur / maxDur; totalSale +=
							engine.getPrice() * percentDur;	 
					}
					else
					{
							totalSale += engine.getSale(saleAmount);
							newPrice = engine.getPrice();
							newStock = engine.getStock();
							engine.setSellTime();
					}
						
					engine.updateConfig();
						
					int change = (int) (newPrice - itemPrice);
					changeStock = newStock - itemStock;
						
						dataSigns.checkForUpdates(itemName, changeStock, change);
					DynamicShop.updateItem(itemName);
						
					new ExtrasInventory(player).removeFromInventory(new ItemStack[] { item });
				}
			}
		}
		double percentTax = tax * 100.0D;
		tax *= totalSale;
		
		if(DynamicEconomy.depositTax)
		{
			try
			{
				if(DynamicEconomy.taxAccountIsBank) DynamicEconomy.economy.bankDeposit(DynamicEconomy.taxAccount, tax);
				else DynamicEconomy.economy.depositPlayer(DynamicEconomy.taxAccount, tax);
			}
			catch(Exception e)
			{
				log.info("Tax-Account " + DynamicEconomy.taxAccount + " not found.");
				Utility.writeToLog("Attempted to deposit tax of " + DynamicEconomy.currencySymbol + tax + " to account " + DynamicEconomy.taxAccount + " but account not found.");
			}
		}
		
		DynamicEconomy.economy.depositPlayer(player.getName(), totalSale);
		
		color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.saleSuccess);
		
		totalSale = Double.valueOf(decFormat.format(totalSale)).doubleValue();
		totalSale -= tax;
		
		color.sendColouredMessage(player, DynamicEconomy.prefix + "&fYou sold&f your inventory &2 - " + decFormat
				.format(percentTax) + "&2% tax = &f" + DynamicEconomy.currencySymbol + totalSale + " &2TOTAL");
		
		Utility.writeToLog(DynamicEconomy.prefix + player.getName() + " sold his entire inventory for " + totalSale);
		
		return true;
	}
	
	public static boolean sell(Player player, String[] args, boolean sign)
	{
		String stringPlay = player.getName();
		
		if((args.length == 0) || (args.length > 2))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/sell [Item] (Amount)");
			Utility.writeToLog(stringPlay + " incorrectly called /sell");
			return false;
		}
		
		if((args[0].equalsIgnoreCase("inventory")) && (args.length == 1))
		{
			sellInventory(player);
			return true;
		}
		
		String[] itemInfo = new String[7];
		try
		{
			if((args.length != 1) || (!args[0].equals("hand"))) itemInfo = Item.getAllInfo(args[0]);
			else
			{
				ItemStack handItem = player.getInventory().getItemInHand();
				
				if(handItem.getEnchantments().size() != 0)
				{
					color.sendColouredMessage(player,
							DynamicEconomy.prefix + "&2This item is enchanted. Use &f/sellenchantment &2instead");
					return false;
				}
				
				int dur = handItem.getDurability();
				int id = handItem.getTypeId();
				
				String name = "";
				
				if(dur == 0) name = String.valueOf(id);
				else name = id + ":" + dur;
				itemInfo = Item.getAllInfo(name);
			}
		}
		catch(Exception e)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + 
					"&2You entered the command arguments in the wrong order, or your item name was invalid ");
			Utility.writeToLog(stringPlay + " entered an invalid item, or entered command arguments in the wrong order");
			return false;
		}
		
		String itemName = itemInfo[0];
		double itemPrice = Double.parseDouble(itemInfo[1]);
		int itemStock = Integer.parseInt(itemInfo[5]);
		long itemID = Long.parseLong(itemInfo[6]);
		
		if((itemID >= 2500L) && (itemID < 2600L))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&f" + itemName + 
					"&2 is an enchantment. Use &f/sellenchantment&2 to sell it.");
			Utility.writeToLog(stringPlay + "tried to buy the enchantment " + itemName + 
					" via /sell instead of /sellenchantment.");
			return false;
		}
		
		double tax = DynamicEconomy.salestax;
		
		if(itemName.equals(""))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.itemDoesntExist);
			Utility.writeToLog(stringPlay + " attempted to sell the non-existent item '" + itemName + "'");
			return false;
		}
		
		for(int x = 0; x < DynamicEconomy.bannedSaleItems.length; x++)
		{
			String bannedItem = Item.getTrueName(DynamicEconomy.bannedSaleItems[x]);
			if(bannedItem.equals(itemName))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.bannedItem);
				Utility.writeToLog(stringPlay + " attempted to sell the banned item: " + bannedItem);
				return false;
			}
		}
		
		int saleAmount = 0;
		boolean isAll = false;
		
		int mat = 0;
		short dmg = 0;
		
		int userAmount = 0;
		
		if(args.length == 1)
		{
			if(args[0].equals("hand"))
			{
				saleAmount = player.getInventory().getItemInHand().getAmount();
				
				if(saleAmount == 0)
				{
					color.sendColouredMessage(player, DynamicEconomy.prefix + "&2You have no item in your hand.");
					Utility.writeToLog(stringPlay + " called /sell hand, but had no item in hand.");
					return false;
				}
			}
			else saleAmount = DynamicEconomy.defaultAmount;
		}
		else if(args.length == 2)
		{
			if(args[1].equalsIgnoreCase("all"))
			{
				if(itemID > 3000L)
				{
					mat = getMat(itemID);
					dmg = getDmg(itemID);
					
					ItemStack saleItem = new ItemStack(mat, saleAmount, dmg);
					saleAmount = getAmountOfDataValue(player, saleItem);
				}
				else if(itemID == 999L)
				{
					saleAmount = (int) player.getExp();
				}
				else
				{
					ItemStack saleItem = new ItemStack((int) itemID);
					saleAmount = getQuantity(player, saleItem);
				}
				
				isAll = true;
			}
			else
			{
				try
				{ saleAmount = Integer.parseInt(args[1]); }
				catch(Exception e)
				{
					color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.invalidCommandArgs);
					Utility.writeToLog(stringPlay +
							" entered an invalid purchase amount, or entered command arguments in the wrong order.");
					return false;
				}
			}
		}
		
		if(saleAmount <= 0)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.negativeSellAmount);
			Utility.writeToLog(stringPlay + " attempted to sell " + saleAmount + " of '" + itemName +
					"', but this amount is invalid.");
			return false;
		}
		
		EnderEngine engine = new EnderEngine(itemInfo);
		double totalSale = (sign) ? engine.getSale(1) : engine.getSale(saleAmount);
		double newPrice = engine.getPrice();
		int newStock = engine.getStock();
		engine.setSellTime();
		
		double change = newPrice - itemPrice;
		int changeStock = newStock - itemStock;
		
		if(args[0].equalsIgnoreCase("hand")) userAmount = player.getInventory().getItemInHand().getAmount();
		else if(itemID > 3000L)
		{
			mat = getMat(itemID);
			dmg = getDmg(itemID);
			
			ItemStack saleItem = new ItemStack(mat, saleAmount, dmg);
			userAmount = getAmountOfDataValue(player, saleItem);
		}
		else if(itemID == 999L)
		{
			float percExp1 = player.getExp();
			player.giveExp(1);
			float percExp2 = player.getExp();
			float percUnit = percExp2 - percExp1;
			player.setExp(percExp2 - percUnit);
			int level = player.getLevel();
			
			int originalLevel = player.getLevel();
			float originalExp = player.getExp();
			
			float curExp = player.getExp();
			
			int expcount = 0;
			
			while(level > 0)
			{
				if(curExp <= 0.0F)
				{
					level--;
					player.setExp(0.5F);
					percExp1 = player.getExp();
					player.giveExp(1);
					percExp2 = player.getExp();
					percUnit = percExp2 - percExp1;
					player.setExp(1.0F);
					curExp = player.getExp();
				}
				
				curExp -= percUnit;
				expcount++;
			}
			
			player.setLevel(originalLevel);
			player.setExp(originalExp);
			
			userAmount = expcount;
		}
		else
		{
			ItemStack i = new ItemStack((int) itemID);
			userAmount = getQuantity(player, i);
		}
		
		if(isAll)
		{
			if(saleAmount <= 0)
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2You have no &f" + itemName);
				Utility.writeToLog(stringPlay + " attempted to sell all of their " + itemName + ", but had none.");
				return false;
			}
		}
		
		if((saleAmount > userAmount) && (!isAll))
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2You do not have &f" + saleAmount + " " + itemName);
			Utility.writeToLog(stringPlay + " attempted to sell " + saleAmount + " of " + itemName + ", but didn't have that many.");
			return false;
		}
		
		double percentTax = tax * 100.0D;
		tax *= totalSale;
		totalSale -= tax;
		
		if(DynamicEconomy.depositTax)
		{
			try
			{
				if(DynamicEconomy.taxAccountIsBank) DynamicEconomy.economy.bankDeposit(DynamicEconomy.taxAccount, tax);
				else DynamicEconomy.economy.depositPlayer(DynamicEconomy.taxAccount, tax);
			}
			catch(Exception e)
			{
				log.info("Tax-Account " + DynamicEconomy.taxAccount + " not found.");
				Utility.writeToLog("Attempted to deposit tax of " + DynamicEconomy.currencySymbol + tax + " to account " + DynamicEconomy.taxAccount + " but account not found.");
			}
		}
		
		DynamicEconomy.economy.depositPlayer(player.getName(), totalSale);
		
		color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.saleSuccess);
		
		decFormat.applyPattern("#.##");
		
		if(((itemID >= 256L) && (itemID <= 258L)) || ((itemID >= 267L) && (itemID <= 279L)) ||
				((itemID >= 298L) && (itemID <= 317L)) || ((itemID >= 283L) && (itemID <= 286L)) ||
				((itemID >= 290L) && (itemID <= 294L)))
		{
			totalSale = 0.0D;
			
			HashMap<Integer, ? extends ItemStack> itemsList = player.getInventory().all((int) itemID);
			
			int numSold = 0;
			
			for(Iterator<? extends ItemStack> localIterator2 = itemsList.values().iterator(); localIterator2.hasNext();)
			{
				Object s = localIterator2.next();
				
				if(numSold == saleAmount) break;
				ItemStack cs = (ItemStack) s;
				
				 ItemStack itemStack = new ItemStack((int)itemID);
				 String itemMCname = itemStack.getType().toString(); double maxDur = Item.getMaxDur(itemMCname);
				 double playerDur =  cs.getDurability(); playerDur = maxDur - playerDur;
				 double percentDur = playerDur / maxDur; totalSale += itemPrice * percentDur;
				 double indivsale = itemPrice * percentDur;
				
				new ExtrasInventory(player).removeFromInventory(cs);
				
				itemPrice = Double.valueOf(decFormat.format(itemPrice)).doubleValue();
				engine.incrementStock(1);
				
				 percentDur *= 100.0D; percentDur = Double.valueOf(decFormat.format(percentDur)).doubleValue();
				 indivsale =  Double.valueOf(decFormat.format(indivsale)).doubleValue();
				 color.sendColouredMessage(player, DynamicEconomy.prefix + "&f1 &2" + itemName + "&f with " + percentDur +
						 "% &2durability = &2" + DynamicEconomy.currencySymbol + indivsale);
				 
				 Utility.writeToLog(
						 stringPlay + " sold a '" + itemName + "' at " + percentDur + "% durability for " + indivsale);
				 
				numSold++;
			}
			
			percentTax = tax * 100.0D;
			tax = DynamicEconomy.salestax * totalSale;
			totalSale -= tax;
			totalSale = Double.valueOf(decFormat.format(totalSale)).doubleValue();
			color.sendColouredMessage(
					player,
					DynamicEconomy.prefix + "&2TOTAL SALE (with " + decFormat.format(percentTax) + "% tax): &f" + DynamicEconomy.currencySymbol + totalSale);
		}
		else
		{
			ExtrasInventory inv = new ExtrasInventory(player);
			
			if(args[0].equalsIgnoreCase("hand"))
			{
				ItemStack i = player.getInventory().getItemInHand();
				inv.removeFromInventory(new ItemStack[] { i });
			}
			else if(itemID > 3000L)
			{
				mat = getMat(itemID);
				dmg = getDmg(itemID);
				
				ItemStack i = new ItemStack(mat, saleAmount, dmg);
				inv.removeFromInventory(new ItemStack[] { i });
			}
			else if(itemID == 999L)
			{
				float percExp1 = player.getExp();
				player.giveExp(1);
				float percExp2 = player.getExp();
				float percUnit = percExp2 - percExp1;
				player.setExp(percExp2 - percUnit);
				int level = player.getLevel();
				
				float curExp = player.getExp();
				
				for(int x = 0; x < saleAmount; x++)
				{
					if(curExp <= 0.0F)
					{
						level--;
						if(level == 0)
						{
							player.setLevel(0);
							player.setExp(0.0F);
							break;
						}
						player.setExp(0.5F);
						percExp1 = player.getExp();
						player.giveExp(1);
						percExp2 = player.getExp();
						percUnit = percExp2 - percExp1;
						player.setExp(1.0F);
						curExp = player.getExp();
					}
					
					curExp -= percUnit;
				}
				
				player.setLevel(level);
				player.setExp(curExp);
			}
			else
			{
				Material material = Material.getMaterial((int) itemID);
				ItemStack i = new ItemStack(material, saleAmount);
				inv.removeFromInventory(new ItemStack[] { i });
			}
			
			totalSale = Double.valueOf(decFormat.format(totalSale)).doubleValue();
			itemPrice = Double.valueOf(decFormat.format(itemPrice)).doubleValue();
			newPrice = Double.valueOf(decFormat.format(newPrice)).doubleValue();
			
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&fYou sold &2" + saleAmount + "&f of &2" + itemName + "&f - " + decFormat
							.format(percentTax) + "&2% tax = &f" + DynamicEconomy.currencySymbol + totalSale + " &2TOTAL");
			Utility.writeToLog(stringPlay + " succesfully sold " + saleAmount + " of '" + itemName + "' for " + totalSale);
		}
		
		changeFormat.applyPattern("#.#####");
		
		newPrice = Double.valueOf(decFormat.format(newPrice)).doubleValue();
		change = Double.valueOf(changeFormat.format(change)).doubleValue();
		
		if(itemPrice != newPrice)
		{
			if(DynamicEconomy.globalNotify)
			{
				float percExp1 = Bukkit.getServer().getOnlinePlayers().length;
				for(int mat1 = 0; mat1 < percExp1; mat1++)
				{
					Player p = player;
					if(!Utility.isQuiet(p))
						color.sendColouredMessage(p,DynamicEconomy.prefix + "&2New Price of &f" + itemName + "&2 is &f" +
								DynamicEconomy.currencySymbol + newPrice + "&2 (" + change + ")");
				}
			}
			else if(DynamicEconomy.localNotify)
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2New Price of &f" + itemName + "&2 is &f" +
						DynamicEconomy.currencySymbol + newPrice + "&2 (" + change + ")");
			
			Utility.writeToLog(DynamicEconomy.prefix +
					" New price of " + itemName + " changed dynamically to " + newPrice +"(" + change + ")");
		}
		
		engine.updateConfig();
		dataSigns.checkForUpdates(itemName, changeStock, change);
		DynamicShop.updateItem(itemName);
		
		return true;
	}
	
	private static int getAmountOfDataValue(Player p, ItemStack m)
	{
		ItemStack[] invent = p.getInventory().getContents();
		int amount = 0;
		for(ItemStack i : invent)
			if((i != null) && (i.getTypeId() == m.getTypeId()) && (i.getDurability() == m.getDurability()))
				amount += i.getAmount();
		return amount;
	}
	
	public static int getQuantity(Player p, ItemStack item)
	{
		ItemStack[] invent = p.getInventory().getContents();
		int amount = 0;
		for(ItemStack i : invent)
			if((i != null) && (i.getType().equals(item.getType())) && (i.getEnchantments().equals(item.getEnchantments())) &&
					(i.getDurability() == item.getDurability()))
				amount += i.getAmount();
		
		return amount;
	}
	
	public static void addStock(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if((args.length < 2) || (args.length > 2))
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/addStock [Item] [AdditionalStock]");
			Utility.writeToLog(stringPlay + " incorrectly called /addstock");
		}
		else
		{
			int addStock = 0;
			try
			{ addStock = Integer.parseInt(args[1]); }
			catch(Exception e)
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2You entered a non-integer amount.");
				Utility.writeToLog("Player" + stringPlay + " tried to add a non integer stock amount");
				e.printStackTrace();
			}
			
			String[] info = Item.getAllInfo(args[0]);
			
			EnderEngine engine = new EnderEngine(info);
			engine.incrementStock(addStock);
			engine.updateConfig();
			
			int newStock = engine.getStock();
			int oldStock = Integer.parseInt(info[5]);
			
			color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.stockAdded);
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Previous Stock: &f" + oldStock + "&2 | New Stock: &f" + newStock);
			Utility.writeToLog(stringPlay + " added " + addStock + " stock of " + info[0] + " for a new stock total of " + newStock);
			dataSigns.checkForUpdates(info[0], newStock - oldStock, 0.0D);
		}
	}
	
	public static void removeStock(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if((args.length < 2) || (args.length > 2))
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/addStock [Item] [AdditionalStock]");
			Utility.writeToLog(stringPlay + " incorrectly called /removestock");
		}
		else
		{
			int removeStock = 0;
			try
			{
				removeStock = Integer.parseInt(args[1]);
			}
			catch(Exception e)
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2You entered a non-integer amount.");
				Utility.writeToLog("Player" + stringPlay + " tried to remove a non integer stock amount");
				e.printStackTrace();
			}
			
			String[] info = Item.getAllInfo(args[0]);
			
			EnderEngine engine = new EnderEngine(info);
			engine.decrementStock(removeStock);
			engine.updateConfig();
			
			int newStock = engine.getStock();
			int oldStock = Integer.parseInt(info[5]);
			
			color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.stockRemoved);
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Previous Stock: &f" + oldStock + "&2 | New Stock: &f" + newStock);
			Utility.writeToLog(stringPlay + " removed " + removeStock + " stock of " + info[0] + " for a new stock total of " + newStock);
			dataSigns.checkForUpdates(info[0], newStock - oldStock, 0.0D);
		}
	}
	
	public static void curTaxes(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if(args.length > 0)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/curTaxes");
			Utility.writeToLog(stringPlay + " incorrectly called /curTaxes");
		}
		else
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&fSales Tax: &2" + DynamicEconomy.salestax * 100.0D + "%");
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&fPurchase Tax: &2" + DynamicEconomy.purchasetax * 100.0D + "%");
			Utility.writeToLog(stringPlay + " called /curtaxes");
		}
	}
	
	public static void setTaxes(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if(args.length != 2)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/settax [sale|purchase] [amount]");
			Utility.writeToLog(stringPlay + " incorrectly called /settax");
		}
		else
		{
			Double tax = Double.valueOf(0.0D);
			try
			{ tax = Double.valueOf(Double.parseDouble(args[1])); }
			catch(Exception e)
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + "&2 " + args[1] + "&f% is an invalid amount.");
				return;
			}
			
			if(args[0].equalsIgnoreCase("sale")) DynamicEconomy.config.set("salestax", tax);
			else if(args[0].equalsIgnoreCase("purchase")) DynamicEconomy.config.set("purchasetax", tax);
			else
			{
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&2This is an invalid tax name. Use either &fsale or &fpurchase");
				Utility.writeToLog(stringPlay + " tried to set tax '" + args[0] + "', which doesn't exist.");
				return;
			}
				
			try
			{ DynamicEconomy.config.save(DynamicEconomy.configFile); }
			catch(Exception e)
			{
				log.info("[DynamicEconomy] Error saving config in /settax");
				e.printStackTrace();
			}
			
			decFormat.applyPattern("###.##");
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Global " + args[0] + "tax set to &f" +
							decFormat.format(tax.doubleValue() * 100.0D) + "%");
			Utility.writeToLog(stringPlay + " set Global " + args[0] + "tax to " +
							decFormat.format(tax.doubleValue() * 100.0D) + "%");
			DynamicEconomy.relConfig();
		}
	}
	
	public static void buyEnchantment(Player player, String[] args)
	{
		if(args.length != 2)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/buyEnchantment [Enchantment] [Level]");
			Utility.writeToLog(player.getName() + " incorrectly called /buyEnchantment");
		}
		else
		{
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
				{ level = Integer.parseInt(args[1]); }
				catch(NumberFormatException e)
				{
					color.sendColouredMessage(player, DynamicEconomy.prefix + "&f" + args[1] +
							"&2 is not a valid enchantment level. Use 1-5 or I-V.");
					Utility.writeToLog(player.getName() + " called /buyenchantment with invalid level " + args[1]);
					return;
				}
			}
			
			if(!DynamicEconomy.itemConfig.contains(enchantment))
			{
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&f" + enchantment + "&2 is not a valid enchantment.");
				Utility.writeToLog(player.getName() + " called /buyenchantment with invalid enchantment " + enchantment);
				return;
			}
			
			String[] enchantmentInfo = Item.getAllInfo(enchantment);
			int id = Integer.parseInt(enchantmentInfo[6]);
			int stock = Integer.parseInt(enchantmentInfo[5]);
			double itemPrice = Double.parseDouble(enchantmentInfo[1]);
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
			
			double tax = DynamicEconomy.purchasetax;
			
			for(int x = 0; x < DynamicEconomy.bannedPurchaseItems.length; x++)
			{
				String bannedItem = Item.getTrueName(DynamicEconomy.bannedPurchaseItems[x]);
				if(bannedItem.equals(enchantment))
				{
					color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.bannedItem);
					Utility.writeToLog(player.getName() + " attempted to buy the banned item: " + bannedItem);
					return;
				}
			}
			
			ItemStack enchantTarg = player.getItemInHand();
			decFormat.applyPattern("#.##");
			
			boolean canEnchant = enchant.canEnchantItem(enchantTarg);
			
			if(!canEnchant)
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix +
						"&2The item you are holding cannot be given the enchantment &f" + enchantment);
				Utility.writeToLog(player.getName() + " called /buyenchantment " + enchantment +
						" for an item this enchantment cannot be applied to.");
				return;
			}
			
			if(stock == 0)
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.notEnoughStock);
				Utility.writeToLog(player.getName() + " called /buyenchantment " + enchantment +
						" but there were none in stock.");
				return;
			}
			
			EnderEngine engine = new EnderEngine(enchantmentInfo);
			double totalCost = engine.getCost(1) * level;
			double newPrice = engine.getPrice();
			int newStock = engine.getStock();
			
			double bal = DynamicEconomy.economy.getBalance(player.getName());
			
			if(totalCost > bal)
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.notEnoughMoney);
				Utility.writeToLog(player.getName() + " called /buyenchantment " + enchantment +
						" but could not afford it.");
				return;
			}
			
			double percentTax = tax * 100.0D;
			tax *= totalCost;
			totalCost += tax;
			
			DynamicEconomy.economy.withdrawPlayer(player.getName(), totalCost);
			
			if(DynamicEconomy.depositTax)
			{
				try
				{
					if(DynamicEconomy.taxAccountIsBank) DynamicEconomy.economy.bankDeposit(DynamicEconomy.taxAccount, tax);
					else DynamicEconomy.economy.depositPlayer(DynamicEconomy.taxAccount, tax);
				}
				catch(Exception e)
				{
					log.info("Tax-Account " + DynamicEconomy.taxAccount + " not found.");
					Utility.writeToLog("Attempted to deposit tax of " + DynamicEconomy.currencySymbol + tax +
							" to account " + DynamicEconomy.taxAccount + " but account not found.");
				}
			}
			
			color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.purchaseSuccess);
			Utility.writeToLog(player.getName() + " bought " + 1 + " of '" + enchantment + "' for " + totalCost);
			
			decFormat.setGroupingUsed(false);
			
			totalCost = Double.valueOf(decFormat.format(totalCost)).doubleValue();
			itemPrice = Double.valueOf(decFormat.format(itemPrice)).doubleValue();
			
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&fYou bought " + 1 + " &2of " + enchantment +
					"&f + " + decFormat.format(percentTax) + "&2% tax = &f" + DynamicEconomy.currencySymbol +
					totalCost +" &2TOTAL");
			
			changeFormat.applyPattern("#.#####");
			
			newPrice = Double.valueOf(decFormat.format(newPrice)).doubleValue();
			double change = Double.valueOf(changeFormat.format(newPrice - itemPrice)).doubleValue();
			
			if(itemPrice != newPrice)
			{
				if(DynamicEconomy.globalNotify)
					for(Player p : Bukkit.getServer().getOnlinePlayers())
						if(!Utility.isQuiet(p))
							color.sendColouredMessage(p, DynamicEconomy.prefix + "&2New Price of &f" + enchantment +
									"&2 is &f" + DynamicEconomy.currencySymbol + newPrice + "&2 (+" + change + ")");
				else if(DynamicEconomy.localNotify)
					color.sendColouredMessage(player, DynamicEconomy.prefix + "&2New Price of &f" + enchantment +
							"&2 is &f" + DynamicEconomy.currencySymbol + newPrice + "&2 (+" + change + ")");
				
				Utility.writeToLog(DynamicEconomy.prefix + " New price of " + enchantment + " changed dynamically to " + 
							newPrice + "(+" + change + ")");
			}
			
			enchantTarg.addEnchantment(enchant, level);
			player.getInventory().setItemInHand(enchantTarg);
			
			engine.updateConfig();
			dataSigns.checkForUpdates(enchantment, stock - newStock, change);
			DynamicShop.updateItem(enchantment);
		}
	}
	
	public static void sellEnchantment(Player player, String[] args)
	{
		if(args.length != 1)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/sellEnchantment [Enchantment]");
			Utility.writeToLog(player.getName() + " incorrectly called /sellEnchantment");
		}
		else
		{
			String enchantment = args[0].toUpperCase();
			int level = 0;
			
			if(!DynamicEconomy.itemConfig.contains(enchantment))
			{
				color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&f" + enchantment + "&2 is not a valid enchantment.");
				Utility.writeToLog(player.getName() + " called /sellenchantment with invalid enchantment " + enchantment);
				return;
			}
			
			String[] enchantmentInfo = Item.getAllInfo(enchantment);
			int id = Integer.parseInt(enchantmentInfo[6]);
			int stock = Integer.parseInt(enchantmentInfo[5]);
			double itemPrice = Double.parseDouble(enchantmentInfo[1]);
			int enchantmentID = id % 2500;
			
			Enchantment enchant = Enchantment.getById(enchantmentID);
			
			double tax = DynamicEconomy.salestax;
			
			for(int x = 0; x < DynamicEconomy.bannedSaleItems.length; x++)
			{
				String bannedItem = Item.getTrueName(DynamicEconomy.bannedSaleItems[x]);
				if(bannedItem.equals(enchantment))
				{
					color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.bannedItem);
					Utility.writeToLog(player.getName() + " attempted to sell the banned item: " + bannedItem);
					return;
				}
			}
			
			ItemStack enchantTarg = player.getItemInHand();
			
			if(!enchantTarg.getEnchantments().containsKey(enchant))
			{
				color.sendColouredMessage(player, DynamicEconomy.prefix +
						"&2The item you are holding does not have the enchantment &f" + enchantment);
				Utility.writeToLog(player.getName() + " called /sellenchantment " + enchantment + " for an item which doesnt have this enchantment");
				return;
			}
			
			level = ((Integer) enchantTarg.getEnchantments().get(enchant)).intValue();
			enchantTarg.removeEnchantment(enchant);
			
			EnderEngine engine = new EnderEngine(enchantmentInfo);
			double totalSale = engine.getSale(1) * level;
			double newPrice = engine.getPrice();
			int newStock = engine.getStock();
			
			double percentTax = tax * 100.0D;
			tax *= totalSale;
			totalSale -= tax;
			
			player.getInventory().setItemInHand(enchantTarg);
			
			DynamicEconomy.economy.depositPlayer(player.getName(), totalSale);
			
			if(DynamicEconomy.depositTax)
			{
				try
				{
					if(DynamicEconomy.taxAccountIsBank) DynamicEconomy.economy.bankDeposit(DynamicEconomy.taxAccount, tax);
					else DynamicEconomy.economy.depositPlayer(DynamicEconomy.taxAccount, tax);
				}
				catch(Exception e)
				{
					log.info("Tax-Account " + DynamicEconomy.taxAccount + " not found.");
					Utility.writeToLog("Attempted to deposit tax of " + DynamicEconomy.currencySymbol + tax +
							" to account " + DynamicEconomy.taxAccount + " but account not found.");
				}
			}
			
			color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.saleSuccess);
			
			Utility.writeToLog(player.getName() + " sold " + 1 + " of '" + enchantment + "' for " + totalSale);
			
			decFormat.setGroupingUsed(false);
			
			totalSale = Double.valueOf(decFormat.format(totalSale)).doubleValue();
			itemPrice = Double.valueOf(decFormat.format(itemPrice)).doubleValue();
			
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2TOTAL SALE (with " +
						decFormat.format(percentTax) + "% tax): &f" + DynamicEconomy.currencySymbol + totalSale);
			
			changeFormat.applyPattern("#.#####");
			
			newPrice = Double.valueOf(decFormat.format(newPrice)).doubleValue();
			double change = Double.valueOf(changeFormat.format(itemPrice - newPrice)).doubleValue();
			
			if(itemPrice != newPrice)
			{
				if(DynamicEconomy.globalNotify)
					for(Player p : Bukkit.getServer().getOnlinePlayers())
						if(!Utility.isQuiet(p))
							color.sendColouredMessage(p, DynamicEconomy.prefix + "&2New Price of &f" + enchantment + 
									"&2 is &f" + DynamicEconomy.currencySymbol + newPrice + "&2 (-" + change + ")");
				else if(DynamicEconomy.localNotify)
					color.sendColouredMessage(player, DynamicEconomy.prefix + "&2New Price of &f" + enchantment + 
							"&2 is &f" + DynamicEconomy.currencySymbol + newPrice + "&2 (+" + change + ")");
				
				Utility.writeToLog(DynamicEconomy.prefix + " New price of " + enchantment + " changed dynamically to " + newPrice + "(-" + change + ")");
			}
			
			engine.updateConfig();
			dataSigns.checkForUpdates(enchantment, stock - newStock, change);
			DynamicShop.updateItem(enchantment);
		}
	}
	
	public void run()
	{
		Set<String> itemsSet = DynamicEconomy.itemConfig.getKeys(false);
		
		Object[] itemsObj = itemsSet.toArray();
		String[] items = new String[itemsObj.length];
		
		for(int i = 0; i < items.length; i++) items[i] = itemsObj[i].toString();
		
		long period = DynamicEconomy.overTimePriceChangePeriod * 60L * 1000L;
		
		for(int x = 0; x < items.length; x++)
		{
			long buyTime = DynamicEconomy.itemConfig.getLong(items[x] + ".buytime");
			long sellTime = DynamicEconomy.itemConfig.getLong(items[x] + ".selltime");
			Calendar.getInstance();
			
			long buyDifference = Calendar.getInstance().getTimeInMillis() - buyTime;
			Calendar.getInstance();
			
			long sellDifference = Calendar.getInstance().getTimeInMillis() - sellTime;
			
			EnderEngine engine = new EnderEngine(Item.getAllInfo(items[x]));
			
			if((DynamicEconomy.enableOverTimePriceDecay) && (buyDifference >= period) && (buyTime != 0L))
			{
				engine.decay();
				
				double price = engine.getPrice();
				
				decFormat.applyPattern("#.##");
				
				if(DynamicEconomy.globalNotify)
				{
					for(Player p : Bukkit.getServer().getOnlinePlayers())
					{
						if(!Utility.isQuiet(p))
						{
							price = Double.valueOf(decFormat.format(price)).doubleValue();
							color.sendColouredMessage(p, DynamicEconomy.prefix + "&2New Price of &f" + items[x] + 
									"&2 is &f" + DynamicEconomy.currencySymbol + price + "&2 ( -" +
									DynamicEconomy.overTimePriceDecayPercent * 100.0D + "% )");
						}
					}
				}
				DynamicEconomy.itemConfig.set(items[x] + ".buytime", Long.valueOf(Calendar.getInstance().getTimeInMillis()));
			}
			
			if((DynamicEconomy.enableOverTimePriceInflation) && (sellDifference >= period) && (sellTime != 0L))
			{
				engine.inflate();
				
				double price = engine.getPrice();
				
				decFormat.applyPattern("#.##");
				
				if(DynamicEconomy.globalNotify)
				{
					for(Player p : Bukkit.getServer().getOnlinePlayers())
					{
						if(!Utility.isQuiet(p))
						{
							price = Double.valueOf(decFormat.format(price)).doubleValue();
							color.sendColouredMessage(p, DynamicEconomy.prefix + "&2New Price of &f" + items[x] + 
									"&2 is &f" + DynamicEconomy.currencySymbol + price + 
									"&2 ( +" + DynamicEconomy.overTimePriceInflationPercent * 100.0D + "% )");
						}
					}
				}
				DynamicEconomy.itemConfig.set(items[x] + ".selltime", 
						Long.valueOf(Calendar.getInstance().getTimeInMillis()));
			}
			
		}
		
		try
		{ DynamicEconomy.itemConfig.save(DynamicEconomy.itemsFile); }
		catch(Exception e)
		{ e.printStackTrace(); }
	}
}
