package me.jhoboken.RandomBlockMinigame;

public class inGamePlayer {
	public boolean inGame; 
	public String block;  
	public String name; 
	public int rerolls;
	public inGamePlayer(String pName) {
		name = pName;
		inGame = false; 
		block = ""; 
		rerolls = 3;
	}
	public boolean getStatus() {
		return inGame;
	}
	public void setStatus(boolean ig) {
		inGame = ig;
	}
	public void setBlock(String b){
		block = b;
	}
	public String getBlock() {
		return block;
	}  
	public String getName() {
		return name;
	} 
	public void setRerolls(int r) {
		rerolls = r;
	}
	public int getRerolls() {
		return rerolls;
	} 
	public void startRound() {
		inGame = true;
	}
	public void foundBlock() {
		block = ""; 
		inGame = true;
	}
	
	

}
