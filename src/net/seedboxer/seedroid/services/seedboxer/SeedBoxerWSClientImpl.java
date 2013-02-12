/*******************************************************************************
 * SeedBoxerWSClientImpl.java
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
package net.seedboxer.seedroid.services.seedboxer;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.seedboxer.seedroid.services.seedboxer.types.APIResponse;
import net.seedboxer.seedroid.services.seedboxer.types.APIResponse.ResponseStatus;
import net.seedboxer.seedroid.services.seedboxer.types.FileValue;
import net.seedboxer.seedroid.services.seedboxer.types.UserAPIKeyResponse;
import net.seedboxer.seedroid.services.seedboxer.types.UserStatusAPIResponse;
import net.seedboxer.seedroid.utils.RequestMethod;
import net.seedboxer.seedroid.utils.RestClient;
import net.seedboxer.seedroid.utils.RestClientFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class SeedBoxerWSClientImpl implements SeedBoxerWSClient {
	
	private static final Pattern apikey_pattern = Pattern.compile("<response><apiKey>\\(*\\)</apiKey></response>");

	private static final String RESPONSE_OK = "<status>OK</status>";
	private static final String WS_PREFIX = "webservices/";
	private static final String LIST_DOWNLOAD_WS = WS_PREFIX + "downloads/list";
	private static final String DOWNLOADS_PUT_WS = WS_PREFIX + "downloads/put";
	private static final String DOWNLOADS_DELETE_WS = WS_PREFIX + "downloads/delete";
	private static final String DOWNLOADS_QUEUE_WS = WS_PREFIX + "downloads/queue";
	private static final String STATUS_WS = WS_PREFIX + "status";
	private static final String REGISTER_DEVICE_WS = WS_PREFIX + "registerDevice";
	private static final String APIKEY_WS = WS_PREFIX + "apikey";

	private final String apikey;
	private String server;
	private final Gson gson = new Gson();
	private final RestClientFactory restClientFactory;

	public SeedBoxerWSClientImpl(RestClientFactory restClientFactory, String apikey, String server) {
		this.restClientFactory = restClientFactory;
		this.apikey = apikey;
		this.server = server;
		if (!server.endsWith("/")) {
			this.server = this.server + "/";
		}
	}

	public List<FileValue> getItemsAvaibleForDownload() throws Exception {
		String response = executeRESTWS(LIST_DOWNLOAD_WS, Collections.<String, Object> emptyMap());

		if (response == null) {
			return null;
		} else {
			return parseFilesValue(response);
		}
	}

	private List<FileValue> parseFilesValue(String response) {
		Type listType = new TypeToken<ArrayList<FileValue>>() { }.getType();		
		return gson.fromJson(response, listType);
	}

	public boolean putToDownload(List<FileValue> toDownload) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		List<String> fileNames = new ArrayList<String>();
		for (FileValue item : toDownload) {
			fileNames.add(item.getName());
		}
		params.put("fileName", fileNames);

		String response = executeRESTWS(DOWNLOADS_PUT_WS, params);
		APIResponse apiResponse = gson.fromJson(response, APIResponse.class);
		return apiResponse != null && apiResponse.getStatus() == ResponseStatus.SUCCESS ;
	}

	public UserStatusAPIResponse getStatusOfDownloads() throws Exception {
		String response = executeRESTWS(STATUS_WS, Collections.<String, Object> emptyMap());

		if (response == null) {
			return null;
		} else {
			return gson.fromJson(response, UserStatusAPIResponse.class);
		}
	}

	private String executeRESTWS(String type, Map<String, Object> params) throws Exception {
		return executeRESTWS(type, params, null, null);
	}
	
	private String executeRESTWS(String type, Map<String, Object> params, String username, String password) throws Exception {
		RestClient client = restClientFactory.getClient(server + type);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			addParam(client, entry);
		}
		if (username != null && password != null) {
			client.AddBasicAuthentication(username, password);
		} else if (apikey != null) {
			client.AddParam("apikey", apikey);
		}
		client.AddHeader("Content-Type", "application/json");
		client.AddHeader("Accept", "application/json");

		try {
			client.Execute(RequestMethod.GET);
			return client.getResponse();
		} catch (Exception e) {
			throw new Exception("Error contacting web service", e);
		}
	}

	private void addParam(RestClient client, Map.Entry<String, Object> entry) {
		Object value = entry.getValue();
		if (value instanceof Collection<?>) {
			for (Object item : (Collection<?>)value) {
				client.AddParam(entry.getKey(), item.toString());
			}
		} else {
			client.AddParam(entry.getKey(), value.toString());
		}
	}

	public boolean registerDevice(String deviceId, String registrationId)
			throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("deviceId", deviceId);
		params.put("registrationId", registrationId);
		String response = executeRESTWS(REGISTER_DEVICE_WS, params);

		APIResponse apiResponse = gson.fromJson(response, APIResponse.class);
		return apiResponse != null && apiResponse.getStatus() == ResponseStatus.SUCCESS ;
	}

	public boolean unregisterDevice(String deviceId, String registrationId)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public List<FileValue> getQueue() throws Exception {
		String response = executeRESTWS(DOWNLOADS_QUEUE_WS, Collections.<String, Object> emptyMap());

		if (response == null) {
			return null;
		} else {
			return parseFilesValue(response);
		}
	}

	public boolean removeFromQueue(long queueId) throws Exception {
		String response = executeRESTWS(DOWNLOADS_DELETE_WS, Collections.singletonMap("downloadId", (Object)queueId));

		APIResponse apiResponse = gson.fromJson(response, APIResponse.class);
		return apiResponse != null && apiResponse.getStatus() == ResponseStatus.SUCCESS ;
	}
	
	public String getApikey(String username, String password) throws Exception {
		String response = executeRESTWS(APIKEY_WS, Collections.<String, Object> emptyMap(), username, password);
		
		UserAPIKeyResponse apikey = gson.fromJson(response, UserAPIKeyResponse.class);
		
		if (apikey != null) {
			return apikey.getApiKey();
		} else {
			return null;
		}
	}

}