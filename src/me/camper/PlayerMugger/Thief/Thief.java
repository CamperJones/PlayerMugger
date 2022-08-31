package me.camper.PlayerMugger.Thief;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.npc.EntityVillager;

public class Thief extends EntityVillager
{
	public Thief(Location loc)
	{
		super(EntityTypes.aV, ((CraftWorld) loc.getWorld()).getHandle());
		
		this.setPosition(loc.getX(), loc.getY(), loc.getZ());
		
		this.setCustomName(new ChatComponentText(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + " Ladrão "));
		this.setCustomNameVisible(true);
		this.setHealth(100);		
		this.setInvisible(true);
	
		this.bP.a(0, new PathfinderGoalAvoidTarget<EntityPlayer>(this, EntityPlayer.class, 15, 1.0D, 1.0D));
		this.bP.a(1, new PathfinderGoalPanic(this, 2.0D));
		this.bP.a(2, new PathfinderGoalRandomStrollLand(this, 0.6D));
		this.bP.a(3, new PathfinderGoalRandomLookaround(this));							
	}
}
