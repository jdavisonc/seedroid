package com.superdownloader.droideasy.webservices;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.superdownloader.droideasy.tools.XMLfunctions;
import com.superdownloader.droideasy.types.Item;

public class SuperdownloaderWSClientImpl implements SuperdownloaderWSClient {

	private static final String DEFAULT_SERVER_URL = "http://ks313077.kimsufi.com:8080/proEasy-1.0/webservices/";
	private static final String LIST_WS = "list";
	private static final String STATUS_WS = "status";
	private static final String PAUSE_WS = "pause";
	private static final String RESUME_WS = "resume";
	private static final String PUT_DOWNLOAD_WS = "putdownload";

	private final String username;
	private final String password;
	private final String server;

	public SuperdownloaderWSClientImpl(String username, String password, String server) {
		this.username = username;
		this.password = password;
		if (server != null) {
			this.server = server;
		} else {
			this.server = DEFAULT_SERVER_URL;
		}
	}

	@Override
	public List<Item> getItemsAvaibleForDownload() throws Exception {
		String response = executeRESTWS(LIST_WS);

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



	@Override
	public boolean putToDownload(List<Item> toDownload) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pauseAll() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean resumeAll() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Item> getStatusOfDownloads() throws Exception {
		String response = executeRESTWS(STATUS_WS);

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

	private String executeRESTWS(String type) throws Exception {
		RestClient client = new RestClient(this.server + type);

		client.AddParam("username", username);
		//client.AddHeader("Authorization", "Basic aGFybGV5OnAycHJ1bHo=");

		try {
			client.Execute(RequestMethod.GET);
			return client.getResponse();
		} catch (Exception e) {
			throw new Exception("Error contacting web service", e);
		}
	}

}
