package eal.service.format;

import java.io.InputStream;

import eal.service.format.eal.Item;

public interface Importer {

	public Item[] parse (String filename, InputStream in) throws Exception;
	
}
