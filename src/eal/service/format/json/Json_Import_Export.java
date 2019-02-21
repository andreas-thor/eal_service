package eal.service.format.json;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import eal.service.format.eal.Item;

public class Json_Import_Export {

	
	
	public static JSONObject create (List<Item> items) throws Exception {
		
		JSONArray json_items = new JSONArray();
		for (Item i: items) {
			json_items.put(Json_Item.create (i));
		}
		
		JSONObject res = new JSONObject();
		res.put("items", json_items);
		return res;
		
	}
}
