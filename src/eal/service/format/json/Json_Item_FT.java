package eal.service.format.json;

import org.json.JSONObject;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_FT;

public class Json_Item_FT extends Json_Item {

	private Item_FT item;
	
	public Json_Item_FT(Item_FT item) {
		this.item = item;
	}
	
	
	@Override
	public JSONObject toJSON() {
		JSONObject res = super.toJSON();
		res.put("type", "item_FT");
		return res;
	}
	
	@Override
	public Item getItem() {
		return this.item;
	}
	
	
	
}
