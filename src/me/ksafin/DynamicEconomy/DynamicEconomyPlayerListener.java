package me.ksafin.DynamicEconomy;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import couk.Adamki11s.Extras.Colour.ExtrasColour;

public class DynamicEconomyPlayerListener implements Listener
{
	public static Permission permission = null;
	private final ExtrasColour color = new ExtrasColour();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		String playerName = e.getPlayer().getName();
		Utility.writeToLog(playerName + " logged in.");
		
		if(!(DynamicEconomy.usersConfig.contains(playerName + ".QUIET")))
			DynamicEconomy.usersConfig.set(playerName + ".QUIET", false);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		Action action = e.getAction();
		Block block = e.getClickedBlock();
		
		if((DynamicShop.isShop(block)) && (block != null))
		{
			if(action == Action.LEFT_CLICK_BLOCK)
			{
				if(player.hasPermission("dynamiceconomy.buy"))
				{
					String[] args = DynamicShop.getArgs(block);
					if(Item.isEnchantment(args[0]))
					{
						args = DynamicShop.getEnchantArgs(block);
						Transaction.buyEnchantment(player, args);
					}
					else Transaction.buy(player, args, true);
				}
			}
			else if((action == Action.RIGHT_CLICK_BLOCK) && (player.hasPermission("dynamiceconomy.sell")))
			{
				String[] args = DynamicShop.getArgs(block);
				if(Item.isEnchantment(args[0]))
				{
					args = DynamicShop.getEnchantArgs(block);
					String[] newArgs = { args[0] };
					Transaction.sellEnchantment(player, newArgs);
				}
				else Transaction.sell(e.getPlayer(), args, true);
			}
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e)
	{
		Player player = e.getPlayer();
		String line = e.getLine(0);
		if((line.equalsIgnoreCase("dynamicsign")) || (line.equalsIgnoreCase("ds")))
		{
			if(player.hasPermission("dynamiceconomy.createsign")) new dataSigns(e);
			else
			{
				this.color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&2You do not have permission to create a &fDynamicSign");
				Utility.writeToLog(player.getName() + " tried to create a DynamicSign but didn't have permission.");
			}
		}
		else if((line.equalsIgnoreCase("dynamicshop")) || (line.equalsIgnoreCase("dsh")))
			if(player.hasPermission("dynamiceconomy.createshop")) new DynamicShop(e);
			else
			{
				this.color.sendColouredMessage(player,
						DynamicEconomy.prefix + "&2You do not have permission to create a &fDynamicShop");
				Utility.writeToLog(player.getName() + " tried to create a DynamicShop but didn't have permission.");
			}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Block block = e.getBlock();
		int id = block.getTypeId();
		if(id == 68 || id == 63 || id == 323)
		{
			dataSigns.removeDataSign(block);
			DynamicShop.removeShopSign(block);
		}
	}
}
