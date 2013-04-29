package couk.Adamki11s.Extras.Inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExtrasInventory extends InventoryMethods
{
	public ExtrasInventory(Player player)
	{ super(player); }
	
	@Override
	public void addToInventory(int itemID, int amount)
	{
		player.getInventory().addItem(new ItemStack(itemID, amount));
		this.updateInventory();
	}
	
	@Override
	public void addToInventory(ItemStack[] items)
	{
		player.getInventory().addItem(items);
		this.updateInventory();
	}
	
	@Override
	public void removeFromInventory(ItemStack[] items)
	{
		player.getInventory().removeItem(items);
		this.updateInventory();
	}
	
	@Override
	public void removeFromInventory(ItemStack item)
	{
		for(ItemStack is : player.getInventory().getContents())
			if((is != null) && (is.getType().equals(is.getType())) &&
					(is.getEnchantments().size() == is.getEnchantments().size()))
				player.getInventory().removeItem(new ItemStack[] { is });
		this.updateInventory();
	}
	
	@Override
	public int getEmptySlots()
	{
		ItemStack[] invent = player.getInventory().getContents();
		int freeSlots = 0;
		for(ItemStack i : invent) if(i == null) freeSlots++;
		return freeSlots;
	}
	
	@Override
	public int getAmountOf(int itemID)
	{
		ItemStack[] invent = player.getInventory().getContents();
		int amount = 0;
		for(ItemStack i : invent)
		{
			if((i != null) && (i.getTypeId() == itemID))
			{
				amount += i.getAmount();
				if(i.getDurability() >= 1) amount -= i.getAmount();
			}
		}
		return amount;
	}
}
