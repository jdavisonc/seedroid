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
package net.seedboxer.seedroid.services.seedboxer;

import java.util.List;

import net.seedboxer.seedroid.services.seedboxer.types.FileValue;
import net.seedboxer.seedroid.services.seedboxer.types.UserStatusAPIResponse;


public interface SeedBoxerWSClient {

	List<FileValue> getItemsAvaibleForDownload() throws Exception;

	UserStatusAPIResponse getStatusOfDownloads() throws Exception;

	boolean putToDownload(List<FileValue> toDownload) throws Exception;

	List<FileValue> getQueue() throws Exception;
	
	boolean updateQueue(List<FileValue> queue) throws Exception;

	boolean removeFromQueue(long id) throws Exception;

	boolean registerDevice(String registrationId) throws Exception;

	boolean unregisterDevice(String registrationId) throws Exception;
	
	String getApikey(String username, String password) throws Exception;

}
