package me.camper.PlayerMugger.Events;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
//import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
//import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
//import org.bukkit.potion.PotionEffect;
//import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.camper.PlayerMugger.Commands;
import me.camper.PlayerMugger.PlayerMugger;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

public class EventsClass implements Listener 
{
	private Plugin plugin = PlayerMugger.getPlugin(PlayerMugger.class); 
	
	public static HashMap<UUID, Location> Respawn = new HashMap<UUID, Location>(); 
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		UUID uidPlayer = event.getPlayer().getUniqueId();
		
		if(Respawn.containsKey(uidPlayer))
		{
			Location location = Respawn.get(uidPlayer);
			event.setRespawnLocation(location);
			Respawn.remove(uidPlayer);
		}
		else
		{
			if(!event.getPlayer().getWorld().getName().equalsIgnoreCase("kitpvp"))
			{
				Location location = plugin.getConfig().getLocation(".Hospital");	
				event.setRespawnLocation(location);
			}
			else
				event.setRespawnLocation(new Location(event.getPlayer().getWorld(),-37,104,91));
		}
	}
	
	@EventHandler
	public void onJoinPlayer(PlayerJoinEvent event)
	{	
		if(!PlayerMugger.TempoCall.containsKey(event.getPlayer().getUniqueId()))
			PlayerMugger.TempoCall.put(event.getPlayer().getUniqueId(), 600);
	}
	
	@EventHandler
	public void followPlayer(EntityTargetEvent event)
	{
		for(UUID uuid : PlayerMugger.TempoSpawn.keySet())
		{
			if(event.getEntity() == Bukkit.getEntity(uuid))			
				event.setTarget(Bukkit.getPlayer(PlayerMugger.Mugger_Alvo.get(uuid)));	
		}
	}
	
	@EventHandler
	public void onEntityBurn(EntityCombustEvent event)
	{
		if(PlayerMugger.TempoSpawn.containsKey(event.getEntity().getUniqueId()))		
			event.setCancelled(true);
	}
		
	@EventHandler
	@SuppressWarnings("deprecation")
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		//se o jogador q morreu esta na lista de contratado Mugger
		if(PlayerMugger.Alvo_Contratou.containsKey(event.getEntity().getUniqueId()))
		{		
			event.setKeepInventory(true);
		    event.setKeepLevel(true);
		    event.getDrops().clear();
		    event.setDroppedExp(0);
		    
		    Player playerDie = (Player) event.getEntity();
			
			Respawn.put(playerDie.getUniqueId(), playerDie.getLocation());
		    
			//verificar se o jogador alvo q morreu possui mais de 5000$
			if(PlayerMugger.econ.getBalance(event.getEntity().getName()) >= 300000)
			{
				//avisar quem contratou sobre sucesso do Mugger
				Bukkit.getPlayer(PlayerMugger.Alvo_Contratou.get(event.getEntity().getUniqueId())).sendMessage(ChatColor.AQUA + "Mugger roubou o alvo, você pode mata-lo e ficar com o dinheiro roubado");
				
				//Tirar money do jogador alvo.
				PlayerMugger.econ.withdrawPlayer(event.getEntity().getName(), 30000);
				
				//avisar jogador alvo que ele foi roubado.
				event.getEntity().sendMessage(ChatColor.RED + "Você foi roubado, perdeu $30.000,00");
																			
				for(UUID uuid : PlayerMugger.Mugger_Alvo.keySet())
				{
					if(PlayerMugger.Mugger_Alvo.get(uuid) == event.getEntity().getUniqueId())
					{						
						Location locArgs = new Location(Bukkit.getWorld("world"),-381,84,-599);												
						UUID ID = Commands.disguise(locArgs, 0, 1, Bukkit.getEntity(uuid).getLocation());	
						
						new BukkitRunnable() 
						{				                
				                public void run () 
				                {						                	
				                    	//((LivingEntity) Bukkit.getEntity(ID)).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 2),true);
				            			
				                    	MobDisguise mobDisguise = new MobDisguise(DisguiseType.VILLAGER);
				            			mobDisguise.setEntity(Bukkit.getEntity(ID));
				            			mobDisguise.startDisguise();	
				            			
				            			Bukkit.getEntity(ID).teleport(event.getEntity().getLocation());
				            			
				            			Bukkit.getEntity(uuid).remove();
				                }
				         }.runTaskLater(plugin, 1);
				         				     					
				         //adicionar mesmo tempo spawn no thief do mugger
						PlayerMugger.TempoSpawn.put(ID, PlayerMugger.TempoSpawn.get(uuid));																										
						
						//remover player alvo e quem contratou
						PlayerMugger.Alvo_Contratou.remove(PlayerMugger.Mugger_Alvo.get(uuid));	
						
						//remover tempo spawn mugger
						PlayerMugger.TempoSpawn.remove(uuid);	
						
						//remover mugger e alvo
						PlayerMugger.Mugger_Alvo.remove(uuid);	
					}
				}
			}
			else
			{
				//avisar o alvo quem contratou mugger sobre jogador n��o ter dinheiro.
				Bukkit.getPlayer(PlayerMugger.Alvo_Contratou.get(event.getEntity().getUniqueId())).sendMessage(ChatColor.RED + "Mugger não conseguiu roubar, pois o alvo não tinha dinheiro");
				
				//spawn thief (Mugger fuj��o) 
				for(UUID uuid : PlayerMugger.Mugger_Alvo.keySet())
				{
					if(PlayerMugger.Mugger_Alvo.get(uuid) == event.getEntity().getUniqueId())
					{														
						Location locArgs = new Location(Bukkit.getWorld("world"),-381,84,-599);													
						UUID ID = Commands.disguise(locArgs, 0, 0, Bukkit.getEntity(uuid).getLocation());
						
						new BukkitRunnable() 
						{				                
				                public void run () 
				                {						                	
				                    	//((LivingEntity) Bukkit.getEntity(ID)).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 600, 4),true);
				            			
				                    	MobDisguise mobDisguise = new MobDisguise(DisguiseType.VILLAGER);
				            			mobDisguise.setEntity(Bukkit.getEntity(ID));
				            			mobDisguise.startDisguise();		
				            			
				            			Bukkit.getEntity(ID).teleport(event.getEntity().getLocation());
				            			
				            			Bukkit.getEntity(uuid).remove();
				                }
				         }.runTaskLater(plugin, 1);
																		
						PlayerMugger.TempoSpawn.put(ID, PlayerMugger.TempoSpawn.get(uuid));																										
												
						PlayerMugger.Alvo_Contratou.remove(PlayerMugger.Mugger_Alvo.get(uuid));						
						PlayerMugger.TempoSpawn.remove(uuid);	
						PlayerMugger.Mugger_Alvo.remove(uuid);						
					}
				}
			}				
			
			PlayerMugger.Alvo_Contratou.remove(event.getEntity().getUniqueId());						
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{		
		if(event.getEntity().getKiller() != null)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"levels addexp " + event.getEntity().getKiller().getDisplayName() + " 4 true true");
		
		if(PlayerMugger.TempoSpawn.containsKey(event.getEntity().getUniqueId()))
		{
			event.getDrops().clear();
			event.setDroppedExp(0);			
									
			if(PlayerMugger.Mugger_Alvo.get(event.getEntity().getUniqueId()) != null)
			{
				  if(PlayerMugger.Alvo_Contratou.get(PlayerMugger.Mugger_Alvo.get(event.getEntity().getUniqueId())) != null)
				  {
					  Bukkit.getPlayer(PlayerMugger.Alvo_Contratou.get(PlayerMugger.Mugger_Alvo.get(event.getEntity().getUniqueId()))).sendMessage(ChatColor.RED + "Mugger foi morto, não conseguiu roubar");
					  PlayerMugger.Alvo_Contratou.remove(PlayerMugger.Mugger_Alvo.get(event.getEntity().getUniqueId()));
				  }
				  
				  PlayerMugger.Mugger_Alvo.remove(event.getEntity().getUniqueId());				  
			}
			
			PlayerMugger.TempoSpawn.remove(event.getEntity().getUniqueId());
							
			if(event.getEntity().getKiller() != null && event.getEntity().getCustomName().contains(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Ladrão"))
			{	
				Player player = event.getEntity().getKiller();
				PlayerMugger.econ.depositPlayer(player, 30000);
				event.getEntity().getKiller().sendMessage(ChatColor.AQUA + "Você recebeu $30.000,00");
			}							
		}	
	}		
}
