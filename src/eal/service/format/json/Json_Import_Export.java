package eal.service.format.json;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import eal.service.format.Exporter;
import eal.service.format.eal.Item;

public class Json_Import_Export implements Exporter {




	@Override
	public void create(Item[] items, OutputStream out) throws Exception {
		JSONObject res = new JSONObject();
		res.put("items", new JSONArray(Stream.of(items).map(Json_Item::create).filter(Objects::nonNull).collect(Collectors.toList())));
		PrintWriter p = new PrintWriter (out);
		p.write(res.toString());
		p.flush();
	}
}
