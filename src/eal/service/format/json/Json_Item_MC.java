package eal.service.format.json;

import org.json.JSONArray;
import org.json.JSONObject;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_MC;

public class Json_Item_MC extends Json_Item {

	private Item_MC item;
	
	public Json_Item_MC(Item_MC item) {
		this.item = item;
	}
	
	
	@Override
	public JSONObject toJSON() {
		JSONObject res = super.toJSON();

		res.put("type", "MC");
		
		JSONArray ja = new JSONArray();
		for (int index=0; index<item.getNumberOfAnswers(); index++) {
			JSONObject o = new JSONObject();
			o.put("text", item.getAnswerText(index));
			o.put("points_pos", item.getAnswerPoints(index, true));
			o.put("points_neg", item.getAnswerPoints(index, false));
			ja.put(o);
		}
		res.put("answers", ja);
		
		res.put("minnumber", this.item.getMinNumber());
		res.put("maxnumber", this.item.getMaxNumber());
		
		return res;

	}
	
	@Override
	public Item getItem() {
		return this.item;
	}
	
	
	
}
