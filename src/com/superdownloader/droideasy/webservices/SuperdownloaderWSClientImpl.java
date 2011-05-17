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

	private static final String DEFAULT_SERVER_URL = "http://ks27242.kimsufi.com/ws/";
	private static final String LIST_WS = "list.php";
	private static final String PAUSE_WS = "pause.php";
	private static final String RESUME_WS = "resume.php";
	private static final String PUT_DOWNLOAD_WS = "putdownload.php";

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
		RestClient client = new RestClient(this.server + LIST_WS);
		client.AddHeader("Authorization", "Basic aGFybGV5OnAycHJ1bHo=");
		client.AddParam("user", username);

		try {
			client.Execute(RequestMethod.GET);
		} catch (Exception e) {
			throw new Exception("Error contacting web service", e);
		}
		String response = client.getResponse();
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
				item.setName(XMLfunctions.getAttributeValue(e, "value"));
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

}
