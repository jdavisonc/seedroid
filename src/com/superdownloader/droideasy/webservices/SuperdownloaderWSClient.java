package com.superdownloader.droideasy.webservices;

import java.util.List;

import com.superdownloader.droideasy.types.Item;

public interface SuperdownloaderWSClient {

	public List<Item> getItemsAvaibleForDownload() throws Exception;

	public List<Item> getStatusOfDownloads() throws Exception;

	public boolean putToDownload(List<Item> toDownload) throws Exception;

	public boolean registerDevice(String deviceId, String registrationId) throws Exception;

}
