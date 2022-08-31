package me.camper.PlayerMugger;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.aasmus.pvptoggle.PvPToggle;

import me.camper.PlayerMugger.Mugger.Mugger;
import me.camper.PlayerMugger.Thief.Thief;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.server.level.WorldServer;

public class Commands implements CommandExecutor{
	
	public String cmd1 = "callmugger";
	
	private Plugin plugin = PlayerMugger.getPlugin(PlayerMugger.class); 
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		Player player = ((Player) sender);		
						
		if(sender instanceof Player)
		{
			//se comando for igual cmd1 e argumento n��o for nulo
			if(cmd.getName().equalsIgnoreCase(cmd1) && args.length !=0)			
			{	
				//verificar tempo cooldwawn do jogador.
				if(player.hasPermission("mugger.nocooldown") || PlayerMugger.TempoCall.get(player.getUniqueId()) == 0)
				{
					//verficar se nome do jogador esta online				
					if(Bukkit.getPlayer(args[0]) != null)
					{
						if(Bukkit.getPlayer(args[0]).getDisplayName().toString().equals(player.getDisplayName().toString()))		
							player.sendMessage(ChatColor.RED + "Não pode contratar Mugger em você mesmo");
						else
						{
							if(! PlayerMugger.Alvo_Contratou.containsKey(Bukkit.getPlayer(args[0]).getUniqueId()))
							{
								PvPToggle.instance.dataUtils.UpdatePlayerPvPState(Bukkit.getPlayer(args[0]));			                	
			                	if(PvPToggle.instance.dataUtils.GetPlayerPvPState(Bukkit.getPlayer(args[0])) == false)
			        			{
			                		if(PlayerMugger.econ.getBalance(player) >= 10000)
									{
										PlayerMugger.econ.withdrawPlayer(player, 10000);	
										PlayerMugger.TempoCall.put(player.getUniqueId(), 600);
										player.sendMessage(ChatColor.YELLOW + "Você contratou Mugger $10,000.00 para roubar: " + ChatColor.AQUA + Bukkit.getPlayer(args[0].toString()).getDisplayName());
																	
										Location locArgs = plugin.getConfig().getLocation(".Mugger");
																																																								
										UUID ID = disguise(locArgs, 1, 0, Bukkit.getPlayer(args[0].toString()).getLocation());	
										
										Bukkit.getEntity(ID).teleport(Bukkit.getPlayer(args[0].toString()));

										//tempo de vida do Mugger
										PlayerMugger.TempoSpawn.put(ID, 60);	
										
										//ID do mugger aponta para ID do player alvo
										PlayerMugger.Mugger_Alvo.put(ID, Bukkit.getPlayer(args[0]).getUniqueId());	
										
										//ID do player alvo aponta para ID do player que contratou mugger
										PlayerMugger.Alvo_Contratou.put(Bukkit.getPlayer(args[0]).getUniqueId(), player.getUniqueId());																															
									}
									else
										player.sendMessage(ChatColor.RED + "Você não possui dinheiro suficiente, custo de contratação: R$10.000,00");
			        			}
			                	else
			                		player.sendMessage(ChatColor.YELLOW + "Não pode contratar Mugger, pois o jogador esta com pvp desativado");
							}
							else
								player.sendMessage(ChatColor.RED + "Já chamaram um Mugger para este jogador");
						}																																	
					}							
					else
						player.sendMessage(ChatColor.RED + "Jogador não encontrado");																																		
				}
				else
					player.sendMessage(ChatColor.RED + "Aguarde " + ChatColor.YELLOW + PlayerMugger.TempoCall.get(player.getUniqueId()).toString() + " segundos, " + ChatColor.RED + "para contratar Mugger");	
			}
			else			
				player.sendMessage(ChatColor.RED + "Comando inválido, Tente: /callmuger 'Nome' ");												
		}	
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public static UUID disguise(Location locArgs, int mugger, int money, Location tpPlayer)
	{				
		if(mugger ==1)
		{
			Mugger ent = new Mugger(locArgs);
			WorldServer world = ((CraftWorld) locArgs.getWorld()).getHandle();
			world.addEntity(ent);
									
			((LivingEntity) Bukkit.getEntity(ent.getUniqueID())).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 15),true);
			((LivingEntity) Bukkit.getEntity(ent.getUniqueID())).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 2),true);
			((LivingEntity) Bukkit.getEntity(ent.getUniqueID())).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 3),true);
			
			//.addPassenger(ent.getBukkitEntity());
			
											
			try {
			MobDisguise mobDisguise = new MobDisguise(DisguiseType.VILLAGER);
			mobDisguise.setEntity(Bukkit.getEntity(ent.getUniqueID()));
			mobDisguise.startDisguise();		
			}catch(Exception e) {}
														
			return ent.getUniqueID();						
		}
		else // ladr��o (Thief)
		{			
			Thief ent = new Thief(locArgs);
			WorldServer world = ((CraftWorld) locArgs.getWorld()).getHandle();
			world.addEntity(ent);									
												
			if(money == 1)
				ent.setCustomName(new ChatComponentText(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Ladrão"));						
							
			return ent.getUniqueID();
		}									
	}
}
