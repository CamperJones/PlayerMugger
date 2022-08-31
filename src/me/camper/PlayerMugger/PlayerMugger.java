package me.camper.PlayerMugger;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.camper.PlayerMugger.Events.EventsClass;
import net.milkbowl.vault.economy.Economy;

public class PlayerMugger extends JavaPlugin implements Listener 
{
	   public static Economy econ;
	   public static HashMap<UUID, Integer> TempoSpawn = new HashMap<UUID, Integer>(); 
	   public static HashMap<UUID, Integer> TempoCall = new HashMap<UUID, Integer>();
	   public static HashMap<UUID, UUID> Alvo_Contratou = new HashMap<UUID, UUID>();
	   public static HashMap<UUID, UUID> Mugger_Alvo = new HashMap<UUID, UUID>(); 
	   
	   public void onEnable() 
	   {	   
		   if(!setupEconomy())		
			   Bukkit.shutdown();
		   
		   Commands commands = new Commands(); 
		   
		   getCommand(commands.cmd1).setExecutor(commands);
		   	   
		   getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "PlayerBounty foi habilitado com sucesso!");
		   getServer().getPluginManager().registerEvents((Listener)new EventsClass(), this);
		   loadConfig();
		   		   		  
		   runnablerunner();		   
	   }
	   
	   private boolean setupEconomy() 
	   {
	        if (getServer().getPluginManager().getPlugin("Vault") == null) 
	        {
	            return false;
	        }
	        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	        if (rsp == null) {
	            return false;
	        }
	        econ = rsp.getProvider();
	        return econ != null;
	    }
	   
	  public void onDisable() 
	  {
	     getServer().getConsoleSender().sendMessage(ChatColor.RED + "PlayerBounty foi desabilitado!");
	  }
	  
	  public void loadConfig() 
	  {
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	  }	
	    
	  public void runnablerunner()
	  {
		  new BukkitRunnable()
		  {
			public void run()
			  {			  
				  for(UUID uuid : TempoCall.keySet())
				  {			
					  if(TempoCall.get(uuid) > 0)						  
						  TempoCall.put(uuid, TempoCall.get(uuid) -1);						  					  
				  }
				  for(UUID uuid : TempoSpawn.keySet())
				  {		
					  if(TempoSpawn.get(uuid) > 0)
					  {
						  TempoSpawn.put(uuid, TempoSpawn.get(uuid) -1);						  						  
					  }
					  else
					  {						  
						  if(Bukkit.getEntity(uuid) != null)
							  Bukkit.getEntity(uuid).remove();	
						 
						  if(Mugger_Alvo.get(uuid) != null)
						  {
							  if(Alvo_Contratou.get(Mugger_Alvo.get(uuid)) != null)
								  Alvo_Contratou.remove(Mugger_Alvo.get(uuid));
							  
							  Mugger_Alvo.remove(uuid);	
						  }
						  						  
						  TempoSpawn.remove(uuid);							  						  						  					  							 
					  }
				  }				  
			  }
		  }.runTaskTimer((Plugin) this, 0, 20);		  
	  }
}
