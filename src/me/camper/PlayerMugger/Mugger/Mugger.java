package me.camper.PlayerMugger.Mugger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntitySkeleton;

public class Mugger extends EntitySkeleton
{
	public Mugger(Location loc)
	{
		super(EntityTypes.aB, ((CraftWorld) loc.getWorld()).getHandle());
		
		this.setPosition(loc.getX(), loc.getY(), loc.getZ());
		
		this.setCustomName(new ChatComponentText(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + " Ladrão "));
		this.setCustomNameVisible(true);
		this.setInvisible(true);
		this.setHealth(100);			
	}	
}
