/*******************************************************************************
 * XMLfunctions.java
 * 
 * Copyright (c) 2012 SeedBoxer Team.
 * 
 * This file is part of Seedroid.
 * 
 * Seedroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Seedroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Seedroid.  If not, see <http ://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.seedboxer.seedroid.tools;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class XMLfunctions {

	public final static Document XMLfromString(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);
		} catch (ParserConfigurationException e) {
			Log.w("droidEasy","XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.w("droidEasy","Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			Log.w("droidEasy","I/O exeption: " + e.getMessage());
			return null;
		}
		return doc;
	}

	public static int numResults(Document doc) {
		Node results = doc.getDocumentElement();
		int res = -1;
		try {
			res = Integer.valueOf(results.getAttributes().getNamedItem("count")
					.getNodeValue());
		} catch (Exception e) {
			res = -1;
		}
		return res;
	}

	public final static String getElementValue(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid
						.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	public static String getAttributeValue(Element item, String name) {
		return item.getAttribute(name);
	}

	public static String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return XMLfunctions.getElementValue(n.item(0));
	}

}
