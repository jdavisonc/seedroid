/*******************************************************************************
 * Item.java
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
package com.seedboxer.seedroid.types;

public class Item {

	private String name;

	private long size;

	private long transferred;

	private boolean selected;

	private long queueId;

	private boolean downloaded;

	public Item() { }

	public Item(String name, long size, long transferred) {
		this.name = name;
		this.size = size;
		this.transferred = transferred;
	}

	public Item(String name, long queueId, boolean downloaded) {
		this.name = name;
		this.queueId = queueId;
		this.downloaded = downloaded;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getTransferred() {
		return transferred;
	}

	public void setTransferred(long transferred) {
		this.transferred = transferred;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public long getQueueId() {
		return queueId;
	}

	public void setQueueId(long queueId) {
		this.queueId = queueId;
	}

	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	@Override
	public String toString() {
		return name;
	}

}
