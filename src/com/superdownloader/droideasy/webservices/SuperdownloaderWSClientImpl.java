package com.superdownloader.droideasy.webservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.superdownloader.droideasy.tools.XMLfunctions;
import com.superdownloader.droideasy.types.Item;

public class SuperdownloaderWSClientImpl implements SuperdownloaderWSClient {

	private static final String LIST_WS = "list";
	private static final String STATUS_WS = "status";
	private static final String REGISTER_DEVICE_WS = "registerDevice";
	private static final String PUT_DOWNLOAD_WS = "putdownload";

	private final String username;
	private final String password;
	private final String server;

	public SuperdownloaderWSClientImpl(String username, String password, String server) {
		this.username = username;
		this.password = password;
		this.server = server;
	}

	public List<Item> getItemsAvaibleForDownload() throws Exception {
		String response = executeRESTWS(LIST_WS, Collections.<String, String> emptyMap());

		if (response == null) {
			return null;
		} else {
			Document doc = XMLfunctions.XMLfromString(response);
			Node nodes = doc.getElementsByTagName("files").item(0);
			NodeList list = nodes.getChildNodes();
			List<Item> items = new ArrayList<Item>();
			for (int i = 0; i < list.getLength(); i++) {
				Element e = (Element) list.item(i);
				Item item = new Item();
				item.setName(XMLfunctions.getElementValue(e.getChildNodes().item(1))); // name
				items.add(item);
			}
			return items;
		}
	}

	public boolean putToDownload(List<Item> toDownload) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		for (Item item : toDownload) {
			params.put("download[]", item.getName());
		}
		String response = executeRESTWS(PUT_DOWNLOAD_WS, params);

		if ("<response>OK</response>".equals(response)) {
			return true;
		}
		return false;
	}

	public List<Item> getStatusOfDownloads() throws Exception {
		String response = executeRESTWS(STATUS_WS, Collections.<String, String> emptyMap());

		if (response == null) {
			return null;
		} else {
			Document doc = XMLfunctions.XMLfromString(response);
			Node nodes = doc.getElementsByTagName("uploads").item(0);
			NodeList list = nodes.getChildNodes();
			List<Item> items = new ArrayList<Item>();
			for (int i = 0; i < list.getLength(); i++) {
				Element e = (Element) list.item(i);

				String name = XMLfunctions.getElementValue(e.getChildNodes().item(0));
				String size = XMLfunctions.getElementValue(e.getChildNodes().item(1));
				String transferred = XMLfunctions.getElementValue(e.getChildNodes().item(2));

				Item item = new Item(name, Long.parseLong(size), Long.parseLong(transferred));
				items.add(item);
			}
			return items;
		}
	}

	private String executeRESTWS(String type, Map<String, String> params) throws Exception {
		RestClient client = new RestClient(server + type);

		client.AddParam("username", username);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			client.AddParam(entry.getKey(), entry.getValue());
		}
		//client.AddHeader("Authorization", "Basic aGFybGV5OnAycHJ1bHo=");

		try {
			client.Execute(RequestMethod.GET);
			return client.getResponse();
		} catch (Exception e) {
			throw new Exception("Error contacting web service", e);
		}
	}

	public boolean registerDevice(String deviceId, String registrationId)
			throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("deviceId", deviceId);
		params.put("registrationId", registrationId);
		String response = executeRESTWS(REGISTER_DEVICE_WS, params);

		if ("<response>OK</response>".equals(response)) {
			return true;
		}
		return false;
	}

	public boolean unregisterDevice(String deviceId, String registrationId)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
