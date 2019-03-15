package eal.service.format.ilias;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_MC;
import eal.service.format.eal.Item_SC;

public class Ilias_Item_SC extends Ilias_Item_MC {

	private Item_SC item;
	public final static String type = "SINGLE CHOICE QUESTION";

	
	public Ilias_Item_SC() {
		this(new Item_SC());
	}
	
	public Ilias_Item_SC(Item_SC item) {
		super (null);
		this.item = item;
	}

	@Override
	public Item getItem() {
		return this.item;
	}

	@Override
	public Item_MC getItem_MC() {
		return this.item;
	}
	
	@Override
	public String getType() {
		return Ilias_Item_SC.type;
	}

	@Override
	public String getCardinality () {
		return "Single";
	}

}
