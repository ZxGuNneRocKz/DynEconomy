package me.ksafin.DynamicEconomy;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import couk.Adamki11s.Extras.Colour.ExtrasColour;

public class DynamicEconomyCommandExecutor implements CommandExecutor
{
	private ExtrasColour color = new ExtrasColour();
	private FileConfiguration config;
	public File confFile;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		Player player = null;
		
		if((sender instanceof Player)) player = (Player) sender;
		
		if((cmd.getName().equalsIgnoreCase("price")) || (cmd.getName().equalsIgnoreCase("deprice")))
		{
			if(player.hasPermission("dynamiceconomy.price"))
			{
				if(isInWorld(player)) Item.getPrice(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /price, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /price but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("setfloor"))
		{
			if(player.hasPermission("dynamiceconomy.setfloor"))
			{
				if(isInWorld(player)) Item.setFloor(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /setfloor, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /setfloor but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("setceiling"))
		{
			if(player.hasPermission("dynamiceconomy.setceiling"))
			{
				if(isInWorld(player)) Item.setCeiling(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /setceiling, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /setceiling but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("getfloor"))
		{
			if(player.hasPermission("dynamiceconomy.getfloor"))
			{
				if(isInWorld(player)) Item.getFloor(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /getfloor, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /getfloor but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("getceiling"))
		{
			if(player.hasPermission("dynamiceconomy.getceiling"))
			{
				if(isInWorld(player)) Item.getCeiling(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /getceiling, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /getceiling but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("getspan"))
		{
			if(player.hasPermission("dynamiceconomy.getspan"))
			{
				if(isInWorld(player)) Item.getSpan(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /getspan, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /getspan but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("setspan"))
		{
			if(player.hasPermission("dynamiceconomy.setspan"))
			{
				if(isInWorld(player)) Item.setSpan(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /setspan, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /setspan but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("dynamiceconomy"))
		{
			if(player.hasPermission("dynamiceconomy.dynamiceconomy"))
			{
				if(isInWorld(player))
				{
					commandList(player, args);
					Utility.writeToLog(player.getName() + " called /dynamiceconomy for help");
				}
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /dynamiceconomy, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /dynamiceconomy but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("dynecon"))
		{
			if(player.hasPermission("dynamiceconomy.dynamiceconomy"))
			{
				if(isInWorld(player))
				{
					commandList(player, args);
					Utility.writeToLog(player.getName() + " called /dynamiceconomy for help");
				}
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /dynamiceconomy, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /dynecon but did not have permission.");
			}
			return true;
		}
		
		if((cmd.getName().equalsIgnoreCase("buy")) || (cmd.getName().equalsIgnoreCase("debuy")))
		{
			if(!DynamicEconomy.isBuySellCommandsEnabled)
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.buyDisabled);
				Utility.writeToLog(player.getName() + " tried to use /buy, but it's disabled.");
				return true;
			}
			if(player.hasPermission("dynamiceconomy.buy"))
			{
				if(isInWorld(player))
				{
					Location loc = player.getLocation();
					int y = loc.getBlockY();
					if(DynamicEconomy.location_restrict)
					{
						if(((y <= DynamicEconomy.maximum_y ? 1 : 0) & (y >= DynamicEconomy.minimum_y ? 1 : 0)) != 0)
							Transaction.buy(player, args, false);
						else if(y <= DynamicEconomy.minimum_y)
						{
							this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.belowMinY);
							Utility.writeToLog(player.getName() + " called /buy but was too deep underground.");
						}
						else if(y >= DynamicEconomy.maximum_y)
						{
							this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.aboveMaxY);
							Utility.writeToLog(player.getName() + " called /sell but was too high up.");
						}
					}
					else Transaction.buy(player, args, false);
				}
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /buy, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /buy but did not have permission.");
			}
			return true;
		}
		
		if((cmd.getName().equalsIgnoreCase("sell")) || (cmd.getName().equalsIgnoreCase("desell")))
		{
			if(!DynamicEconomy.isBuySellCommandsEnabled)
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.sellDisabled);
				Utility.writeToLog(player.getName() + " tried to use /sell, but it's disabled.");
				return true;
			}
			if(player.hasPermission("dynamiceconomy.sell"))
			{
				if(isInWorld(player))
				{
					Location loc = player.getLocation();
					int y = loc.getBlockY();
					if(DynamicEconomy.location_restrict)
					{
						if(((y <= DynamicEconomy.maximum_y ? 1 : 0) & (y >= DynamicEconomy.minimum_y ? 1 : 0)) != 0)
							Transaction.sell(player, args, false);
						else if(y <= DynamicEconomy.minimum_y)
						{
							this.color.sendColouredMessage(player,
									DynamicEconomy.prefix + "&2You are too deep underground to access the economy!.");
							Utility.writeToLog(player.getName() + " called /sell but was too deep underground.");
						}
						else if(y >= DynamicEconomy.maximum_y)
						{
							this.color.sendColouredMessage(player,
									DynamicEconomy.prefix + "&2You are too high up to access the economy!.");
							Utility.writeToLog(player.getName() + " called /sell but was too high up.");
						}
					}
					else Transaction.sell(player, args, false);
				}
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /sell, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /sell but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("addstock"))
		{
			if(player.hasPermission("dynamiceconomy.addstock"))
			{
				if(isInWorld(player)) Transaction.addStock(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /addstock, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /addstock but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("dynamiceconomyreloadconfig"))
		{
			boolean hasPerm = true;
			String name = "Console";
			if(player != null)
			{
				hasPerm = player.hasPermission("dynamiceconomy.dynamiceconomyreloadconfig");
				if(!isInWorld(player))
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /dynamiceconomyreloadconfig, but was in the wrong world.");
				}
				name = player.getName();
			}
			
			if(hasPerm)
			{
				DynamicEconomy.relConfig();
				player.sendMessage(ChatColor.GREEN + "Configuration for DynamicEconomy reloaded");
				Utility.writeToLog(name + " reloaded the DynamicEconomy config.yml");
			}
			else
			{
				player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /dynamiceconomyreloadconfig but did not have permission.");
			}	
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("removestock"))
		{
			if(player.hasPermission("dynamiceconomy.removestock"))
			{
				if(isInWorld(player)) Transaction.removeStock(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /removestock, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /removestock but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("getdurability"))
		{
			if(player.hasPermission("dynamiceconomy.getdurability"))
			{
				if(isInWorld(player)) Item.getDurCommand(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /getdurability, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /getdurability but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("curtaxes"))
		{
			if(player.hasPermission("dynamiceconomy.curtaxes"))
			{
				if(isInWorld(player)) Transaction.curTaxes(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /curtaxes, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /curtaxes but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("settax"))
		{
			if(player.hasPermission("dynamiceconomy.settax"))
			{
				if(isInWorld(player)) Transaction.setTaxes(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /settax, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /settax but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("loan"))
		{
			if(player.hasPermission("dynamiceconomy.loan"))
			{
				if(isInWorld(player))
				{
					if(DynamicEconomy.useLoans) loan.lend(player, args);
					else
					{
						this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.loansDisabled);
						Utility.writeToLog(player.getName() + " called /loan but loans are disabled.");
					}
				}
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /loan, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /loan but didn't have permission");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("curinterest"))
		{
			if(player.hasPermission("dynamiceconomy.curinterest"))
			{
				if(isInWorld(player))
				{
					if(DynamicEconomy.useLoans) loan.getInterest(player, args);
					else
					{
						this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.loansDisabled);
						Utility.writeToLog(player.getName() + " called /curinterest but loans are disabled.");
					}
				}
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /curinterest, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /curinterest but didn't have permission");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("curloans"))
		{
			if(player.hasPermission("dynamiceconomy.loan"))
			{
				if(isInWorld(player))
				{
					if(DynamicEconomy.useLoans) loan.getLoans(player, args);
					else
					{
						this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.loansDisabled);
						Utility.writeToLog(player.getName() + " called /curloans but loans are disabled.");
					}
				}
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /curinterest, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /curloans but didn't have permission");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("curworld"))
		{
			if(player.hasPermission("dynamiceconomy.curworld")) curWorld(player);
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /curworld but didn't have permission");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("banItem"))
		{
			if(player.hasPermission("dynamiceconomy.banItem"))
			{
				if(isInWorld(player)) Item.banItem(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /banItem, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /banItem but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("unbanItem"))
		{
			if(player.hasPermission("dynamiceconomy.banItem"))
			{
				if(isInWorld(player)) Item.unbanItem(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /unbanItem, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /unbanItem but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("marketquiet"))
		{
			if(player.hasPermission("dynamiceconomy.marketquiet"))
			{
				if(isInWorld(player)) Utility.makeQuiet(player);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /marketquiet, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /marketquiet but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("addalias"))
		{
			if(player.hasPermission("dynamiceconomy.alias"))
			{
				if(isInWorld(player)) Item.addAlias(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /addalias, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /addalias but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("removealias"))
		{
			if(player.hasPermission("dynamiceconomy.alias"))
			{
				if(isInWorld(player)) Item.removeAlias(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /removealias, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /removealias but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("canibuy"))
		{
			if(player.hasPermission("dynamiceconomy.canibuy"))
			{
				if(isInWorld(player)) Item.canIBuy(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /canibuy, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /canibuy but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("canisell"))
		{
			if(player.hasPermission("dynamiceconomy.canisell"))
			{
				if(isInWorld(player)) Item.canISell(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /canisell, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /canisell but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("buyenchantment"))
		{
			if(!DynamicEconomy.isBuySellCommandsEnabled)
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.buyDisabled);
				Utility.writeToLog(player.getName() + " tried to use /buyenchantment, but it's disabled.");
				return true;
			}
			if(player.hasPermission("dynamiceconomy.buyenchantment"))
			{
				if(isInWorld(player)) Transaction.buyEnchantment(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /buyenchantment, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /buyenchantment but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("sellenchantment"))
		{
			if(!DynamicEconomy.isBuySellCommandsEnabled)
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.buyDisabled);
				Utility.writeToLog(player.getName() + " tried to use /buyenchantment, but it's disabled.");
				return true;
			}
			if(player.hasPermission("dynamiceconomy.sellenchantment"))
			{
				if(isInWorld(player)) Transaction.sellEnchantment(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /sellenchantment, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /sellenchantment but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("priceenchantment"))
		{
			if(player.hasPermission("dynamiceconomy.priceenchantment"))
			{
				if(isInWorld(player)) Item.priceEnchantment(player, args);
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /priceenchantment, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /priceenchantment but did not have permission.");
			}
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("renewpricing"))
		{
			if(player.hasPermission("dynamiceconomy.renew"))
			{
				if(isInWorld(player))
				{
					new EnderEngine(Item.getAllInfo("STONE")).reCalculatePrices();
					player.sendMessage(DynamicEconomy.prefix + ChatColor.GREEN + "Prices for items updated!");
				}
				else
				{
					player.sendMessage(DynamicEconomy.prefix + ChatColor.RED + Messages.wrongWorld);
					Utility.writeToLog(player.getName() + " tried to call /renewpricing, but was in the wrong world.");
				}
			}
			else
			{
				this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.noPermission);
				Utility.writeToLog(player.getName() + " called /renewpricing but did not have permission.");
			}
			return true;
		}	
		return false;
	}
	
	public DynamicEconomyCommandExecutor(DynamicEconomy plugin, PluginDescriptionFile desc, FileConfiguration conf, File cf)
	{
		this.config = conf;
		this.confFile = cf;
	}
	
	public void commandList(Player player, String[] args)
	{
		ArrayList<Command> cmdList = (ArrayList<Command>) PluginCommandYamlParser.parse(DynamicEconomy.plugin);
		int length = cmdList.size();
		int numPages = (int) (length / 5.0D + 1.0D);
		int page;
		if(args.length == 0) page = 1;
		else page = Integer.parseInt(args[0]);
		
		int startCommand = page * 5 - 5;
		int endCommand = page * 5;
		
		if(endCommand > length) endCommand = length;
		
		player.sendMessage(ChatColor.GREEN + 
				"---------" + ChatColor.WHITE + "DynamicEconomy Commands " + ChatColor.GREEN + "---------");
		for(int x = startCommand; x < endCommand; x++)
		{
			Command cmd = (Command) cmdList.get(x);
			String command = cmd.getUsage();
			String desc = cmd.getDescription();
			String message = "&2" + command + " : &f" + desc;
			this.color.sendColouredMessage(player, message);
		}
		this.color.sendColouredMessage(player, "&2----------------&fPage &f" + page + "/" + numPages + "&2----------------");
	}
	
	public void getStockBoolean(Player player, String[] args)
	{
		if(args.length > 0)
			this.color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/isstock");
		else
		{
			Boolean isStock = Boolean.valueOf(this.config.getBoolean("Use-Stock", true));
			if(isStock.booleanValue()) this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.stockOn);
			else this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.stockOff);
		}
	}
	
	public void getBoundaryBoolean(Player player, String[] args)
	{
		if(args.length > 0)
			this.color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/isboundary");
		else
		{
			Boolean isStock = Boolean.valueOf(this.config.getBoolean("Use-boundaries", true));
			if(isStock.booleanValue()) this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.boundariesOn);
			else this.color.sendColouredMessage(player, DynamicEconomy.prefix + Messages.boundariesOff);
		}
	}
	
	public boolean isInWorld(Player player)
	{
		World world = player.getWorld();
		String worldName = world.getName();
		
		for(String worldIndex : DynamicEconomy.dyneconWorld) if(worldName.equalsIgnoreCase(worldIndex)) return true;
		return false;
	}
	
	public void curWorld(Player player)
	{
		World world = player.getWorld();
		String worldName = world.getName();
		this.color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Your current world is: &f" + worldName);
	}
}
