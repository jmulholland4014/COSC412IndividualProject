package me.jhoboken.RandomBlockMinigame;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin { 
	public ArrayList<inGamePlayer> minigameList = new ArrayList<inGamePlayer>();
	public ArrayList<String> inGameList = new ArrayList<String>();  
	public boolean untilLoss = false; 
	public Random random = new Random(); 
	public boolean allFound = false;
	@Override 
	public void onEnable() {
		//on Startups or reloads
		
	} 
	@Override
	public void onDisable() {
		//on reload or disables, shutdowns
	} 
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean permitted = false; 
		inGamePlayer igp = new inGamePlayer(sender.getName());
		if(!(sender instanceof Player)) {
			permitted = true; 
		}
		if(sender instanceof Player) {
			if(sender.hasPermission("Operator")) {
				permitted = true;
			}
		}
		if(label.equalsIgnoreCase("addToRBM")) {   
			if(permitted) {
				if(args.length == 0) {
					addPlayerToRBM(igp);
				} 
				else if (args.length == 1) {
					inGamePlayer temp = new inGamePlayer(args[0]); 
					addPlayerToRBM(temp);
				} 
				else {
					sender.sendMessage("Too many arguments to run the addToRBM command.");
				}
			}
			else {
				sender.sendMessage("You do not have permission to use this command.");
			}
			return true;
		} 
		if(label.equalsIgnoreCase("addAllToRBM")) {
			if(permitted) {
				addAllToRBM();
			} 
			else {
				sender.sendMessage("You do not have permission to use this command.");
			}
		}
		if(label.equalsIgnoreCase("removeAllFromRBM")) {
			if(permitted) {
				removeAllFromRBM();
			} 
			else {
				sender.sendMessage("You do not have permission to use this command.");
			}
		}
		if(label.equalsIgnoreCase("getRBMList")) {
			sender.sendMessage(getRBMList());
			return true;
		} 
		if(label.equalsIgnoreCase("removeFromRBM")) {
			if(permitted) {
				if(args.length == 0) {
					removePlayerFromRBM(igp);
				} 
				else if (args.length == 1) {
					inGamePlayer temp = new inGamePlayer(args[0]);
					removePlayerFromRBM(temp);
				} 
				else {
					sender.sendMessage("Too many arguments to run the addToRBM command.");
				}
			}
			else {
				sender.sendMessage("You do not have permission to use this command.");
			}
			return true;
		}
		if(label.equalsIgnoreCase("startRBM")) {  
			buildRerolls();
			if(minigameList.size() == 1) {
				untilLoss = true;
			}
			if(args.length == 0) {
				startRBM(300);
			} 
			else {
				startRBM(Integer.parseInt(args[0]));
			}
		} 
		if(label.equalsIgnoreCase("reroll")) {
			if(minigameList.size() < 1) {
				sender.sendMessage("Not enough players, type '/addtorbm or /addalltorbm' to add players to the game, and type '/startrbm' to begin the game.");
			}
			else {
				reroll(igp);
			}
		}
		return false;
	} 
	//
	//
	//
	//
	//METHODS
	//
	//
	//
	//
	//Adding players to list
	public void addPlayerToRBM(inGamePlayer igp) {   
		Player player = (Player) Bukkit.getPlayer(igp.getName());
		if(minigameList.contains(igp)) {
			player.sendMessage("You were already in the game!");
		} 
		else {
			minigameList.add(igp); 
			player.sendMessage("You are now in the minigame!");
		}
	} 
	public void removePlayerFromRBM(inGamePlayer igp) {  
		Player player = (Player) Bukkit.getPlayer(igp.getName());
		if(minigameList.contains(igp)) {
			minigameList.remove(igp); 
			player.sendMessage("You have been removed from the game.");
		} 
		else { 
			player.sendMessage("You were not in the game.");
		}
	}
	public void removeAllFromRBM() {
		for(Player all:getServer().getOnlinePlayers()){  
			inGamePlayer temp = new inGamePlayer(all.getName());
			if(minigameList.contains(temp)){
				minigameList.remove(temp);  
				all.sendMessage("You have been removed from the game.");
			} 
			else { 
				all.sendMessage("You were not in the game.");
			}
		}
	}
	public void addAllToRBM() { 
		for(Player all:getServer().getOnlinePlayers()){  
			inGamePlayer temp = new inGamePlayer(all.getName());
			if(minigameList.contains(temp)){
				
			} 
			else {
				minigameList.add(temp);  
				all.sendMessage("You have been added to the game!");
			}
		}
	} 
	public String getRBMList() {
		String result = "List of Players: \n";
		for (inGamePlayer p: minigameList) {
			result = result + p.getName() + " \n";
		} 
		return result;
	}
	//Game Methods
	public void startRBM(int timer) {    
		assignBlocks(); 
		setList(); 
		for(int i = 0; i < minigameList.size(); i++) {
			minigameList.get(i).startRound();
		}
		Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.GOLD + String.valueOf(timer) + ChatColor.GREEN + " seconds remaining!");
        Bukkit.broadcastMessage("");
		new BukkitRunnable() {
			int time = timer; // time in seconds
            public void run() {  
            	lookAtBlock();  
            	if(allFound) {
            		time = 0;
            	}
                if (time > 0) {
                    if (time < 11) {
                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage(ChatColor.GOLD + "" + time + ChatColor.GREEN + " seconds remaining!");
                        Bukkit.broadcastMessage("");
 
                    }
                    time--;
                } 
                else { 
                    killUnfinishedPlayers();
                    continueRBM(timer);
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 20, 20);
	} 
	public void assignBlocks() { 
		for(int i = 0; i < minigameList.size(); i++) {
			String temp = getRandomBlock().name();
			minigameList.get(i).setBlock(temp); 
			Bukkit.getPlayer(minigameList.get(i).getName()).sendMessage("Your block is "  + ChatColor.GOLD + temp);
		}
		
	}
	public void setList() {
		for (int i = 0; i < minigameList.size(); i++) {
			inGameList.add(minigameList.get(i).getName());
		}
	}
	public Material getRandomBlock() {
			Material material = null;
		    while (material == null) {
		      material = Material.values()[random.nextInt((Material.values()).length)];
		      material = validateMaterial(material);
		    } 
		    return material;
	}  
	public Material validateMaterial(Material m) { 
		if(m.name().contains("END_PORTAL")) {
			
		}
		else if (!(m.isSolid()) || !(m.isBlock()) || m.name().equalsIgnoreCase("BARRIER") || m.name().equalsIgnoreCase("DIAMOND_BLOCK") || 
					m.name().equalsIgnoreCase("EMERALD_BLOCK") || m.name().equalsIgnoreCase("CREEPER_HEAD") || m.name().equalsIgnoreCase("SKELETON_SKULL")
					|| m.name().equalsIgnoreCase("ZOMBIE_HEAD") || m.name().equalsIgnoreCase("STRUCTURE_BLOCK") || m.name().equalsIgnoreCase("NETHERITE_BLOCK") 
					|| m.name().equalsIgnoreCase("DRAGON_EGG") || m.name().equalsIgnoreCase("PETRIFIED_OAK_SLAB") 
					|| m.name().equalsIgnoreCase("END_STONE") || m.name().equalsIgnoreCase("CHORUS_PLANT") || m.name().contains("END") 
					|| m.name().contains("PURPUR") || m.name().contains("DRAGON")) {
		        m = null; 
		} 
		return m;
	}
	public void continueRBM(int timer){ 
		allFound = false;
		if(untilLoss) {
			for(inGamePlayer p: minigameList) {
				Bukkit.getPlayer(p.getName()).sendMessage(ChatColor.RED + "" + "You won, but this will go on until you lose.");
			}
			startRBM(timer);
		} 
		else if(minigameList.size() == 1) {
			for(inGamePlayer p: minigameList) {
				Bukkit.getPlayer(p.getName()).sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "YOU WIN!");
			}
		}  
		else if(minigameList.size() == 0) {
			Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "YOU ALL LOST!");
            Bukkit.broadcastMessage("");
		} 
		else if(minigameList.size() > 1) {
			startRBM(timer);
		}
	} 
	public void lookAtBlock() {
		for(int i = 0; i<minigameList.size(); i++) { 
			if(Bukkit.getPlayer(minigameList.get(i).getName()).getLocation().getBlock().getRelative(BlockFace.DOWN).getBlockData().getMaterial().name() == minigameList.get(i).getBlock()){
				Bukkit.getPlayer(minigameList.get(i).getName()).sendMessage("You found your block");
				inGameList.remove(minigameList.get(i).getName());  
				if(inGameList.size() == 0) {
					allFound = true;
				} 
				minigameList.get(i).foundBlock();
			}
			else if(Bukkit.getPlayer(minigameList.get(i).getName()).getLocation().add(0, 1, 0).getBlock().getRelative(BlockFace.DOWN).getBlockData().getMaterial().name() == minigameList.get(i).getBlock()){
				Bukkit.getPlayer(minigameList.get(i).getName()).sendMessage("You found your block");
				inGameList.remove(minigameList.get(i).getName());  
				if(inGameList.size() == 0) {
					allFound = true;
				}
				minigameList.get(i).foundBlock();
			}
			else if(Bukkit.getPlayer(minigameList.get(i).getName()).getLocation().add(0, -1, 0).getBlock().getRelative(BlockFace.DOWN).getBlockData().getMaterial().name() == minigameList.get(i).getBlock()){
				Bukkit.getPlayer(minigameList.get(i).getName()).sendMessage("You found your block");
				inGameList.remove(minigameList.get(i).getName());  
				if(inGameList.size() == 0) {
					allFound = true;
				}
				minigameList.get(i).foundBlock();
			}
		}
		
	} 
	public void killUnfinishedPlayers() {
		for(String p: inGameList) { 
			Bukkit.getPlayer(p).setHealth(0);
			Bukkit.getPlayer(p).sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You've failed to get your block"); 
			inGameList.remove(p);  
			for(int i = 0; i < minigameList.size(); i++) {
				if(minigameList.get(i).getName().equals(p)) {
					minigameList.remove(i);
				}
			}
			if(minigameList.size() == 0) {
				untilLoss=false;
			} 
		} 
		
	} 
	public void buildRerolls() {
		for (int i = 0; i < minigameList.size(); i++) {
			minigameList.get(i).setRerolls(3);
		} 
	} 
	public void reroll(inGamePlayer p) {
		int rerolls = -1;
		for(int i = 0; i < minigameList.size(); i++) {
			if(minigameList.get(i).getName().equals(p.getName())) {
				rerolls = minigameList.get(i).getRerolls();
			}
		}
		String randomBlock = getRandomBlock().name(); 
		Bukkit.getPlayer("jhoboken").sendMessage(String.valueOf(rerolls));
		if(rerolls > 0) { 
			rerolls = rerolls - 1;
			p.setBlock(randomBlock);
			for(int i = 0; i < minigameList.size(); i++) {
				if(minigameList.get(i).getName().equals(p.getName())) {
					minigameList.get(i).setRerolls(rerolls); 
					minigameList.get(i).setBlock(p.getBlock());
				}
			}
			Bukkit.getPlayer(p.getName()).sendMessage("Your new block is " + ChatColor.GOLD + randomBlock);
		} 
		else {
			Bukkit.getPlayer(p.getName()).sendMessage("No more rerolls");
		}
	}
	
}
