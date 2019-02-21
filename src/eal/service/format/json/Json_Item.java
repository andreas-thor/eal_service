package eal.service.format.json;

import org.json.JSONObject;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_FT;
import eal.service.format.eal.Item_MC;
import eal.service.format.eal.Item_SC;

public abstract class Json_Item {

	
	public abstract Item getItem();

	
	public JSONObject toJSON () {
		JSONObject result = new JSONObject();
		if (this.getItem().ealid != null) {
			result.put("ealid", this.getItem().ealid);
		}
		result.put("description", this.getItem().description);
		result.put("question", this.getItem().question);
		result.put("points", this.getItem().getPoints());
		return result;
	}


	public static JSONObject create(Item item) throws Exception {
		
		if (item instanceof Item_SC) {
			return new Json_Item_SC((Item_SC) item).toJSON();
		}
		if (item instanceof Item_MC) {
			return new Json_Item_MC((Item_MC) item).toJSON();
		}
		if (item instanceof Item_FT) {
			return new Json_Item_FT((Item_FT) item).toJSON();
		}
		
		throw new Exception("Unknown item type");
	}
}
