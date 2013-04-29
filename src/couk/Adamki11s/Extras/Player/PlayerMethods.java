package couk.Adamki11s.Extras.Player;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class PlayerMethods
{
	public abstract boolean isOnGround(Player paramPlayer);
	
	public abstract boolean isUnderWater(Player paramPlayer);
	
	public abstract void mountEntity(Player paramPlayer, Entity paramEntity);
	
	public abstract void mountPlayer(Player paramPlayer1, Player paramPlayer2);
	
	public abstract void setBlockOnPlayerHead(Player paramPlayer, Material paramMaterial);
	
	public abstract void setBlockOnPlayerHead(Player paramPlayer, int paramInt);
	
	public abstract int getSecondsLived(Player paramPlayer);
	
	public abstract int getMinutesLived(Player paramPlayer);
	
	public abstract int getHoursLived(Player paramPlayer);
	
	public abstract UUID getUniqueUUID(Player paramPlayer);
	
	public abstract void forceChat(Player paramPlayer, String paramString);
	
	public abstract int getDimension(Player paramPlayer);
	
	public abstract void removeBlockOnPlayerHead(Player paramPlayer);
	
	public abstract Block getLookedAtBlock(Player paramPlayer);
	
	public abstract Location getLocationLooked(Player paramPlayer);
}
