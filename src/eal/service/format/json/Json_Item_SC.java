package eal.service.format.json;

import org.json.JSONArray;
import org.json.JSONObject;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_SC;

public class Json_Item_SC extends Json_Item {

	private Item_SC item;
	
	public Json_Item_SC(Item_SC item) {
		this.item = item;
	}
	
	
	@Override
	public JSONObject toJSON() {

		JSONObject res = super.toJSON();
		res.put("type", "item_SC");

		JSONArray ja = new JSONArray();
		for (int index=0; index<item.getNumberOfAnswers(); index++) {
			JSONObject o = new JSONObject();
			o.put("text", item.getAnswerText(index));
			o.put("points", item.getAnswerPoints(index));
			ja.put (o);
		}
		
		res.put("answers", ja);
		return res;
		
	}
	
	@Override
	public Item getItem() {
		return this.item;
	}
	
	
	
}
