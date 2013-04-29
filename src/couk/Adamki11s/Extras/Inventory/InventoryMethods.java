package couk.Adamki11s.Extras.Inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMethods
{
	protected final Player player;
	
	public InventoryMethods(Player player)
	{ this.player = player; }
	
	public abstract void addToInventory(int itemID, int amount);
	
	public abstract void addToInventory(ItemStack[] items);
	
	public abstract void removeFromInventory(ItemStack[] items);
	
	public abstract void removeFromInventory(ItemStack item);
	
	public abstract int getEmptySlots();
	
	public abstract int getAmountOf(int itemID);
	
	@SuppressWarnings("deprecation")
	public void updateInventory()
	{ player.updateInventory(); }
}
