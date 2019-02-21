package eal.service.format.eal;

public abstract class Item {

	public Integer ealid = null;
	public String title = null;
	public String description = null;
	public String question = null;

	public abstract int getPoints();
	
	
	
}
