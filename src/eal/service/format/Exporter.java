package eal.service.format;

import java.io.OutputStream;

import eal.service.format.eal.Item;

public interface Exporter {

	public void create (Item[] items, OutputStream out) throws Exception;
	
}
