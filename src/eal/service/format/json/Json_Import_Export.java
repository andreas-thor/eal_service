package eal.service.format.json;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import eal.service.format.eal.Item;

public class Json_Import_Export {

	public JSONObject create(Stream<Item> items) throws Exception {

		JSONObject res = new JSONObject();
		res.put("items", new JSONArray(items.map(Json_Item::create).filter(Objects::nonNull).collect(Collectors.toList())));
		return res;

	}
}
