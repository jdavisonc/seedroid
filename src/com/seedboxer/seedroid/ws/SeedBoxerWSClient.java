/*******************************************************************************
 * SeedBoxerWSClient.java
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
package com.seedboxer.seedroid.ws;

import java.util.List;

import com.seedboxer.seedroid.types.Item;

public interface SeedBoxerWSClient {

	public List<Item> getItemsAvaibleForDownload() throws Exception;

	public List<Item> getStatusOfDownloads() throws Exception;

	public boolean putToDownload(List<Item> toDownload) throws Exception;

	public boolean registerDevice(String deviceId, String registrationId) throws Exception;

	public boolean unregisterDevice(String deviceId, String registrationId) throws Exception;

}
