package com.superdownloader.droideasy.types;

public class Item {

	private String name;

	private long size;

	private long transferred;
	
	private boolean selected;

	public Item() { }

	public Item(String name, long size, long transferred) {
		this.name = name;
		this.size = size;
		this.transferred = transferred;
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
	
	@Override
	public String toString() {
		return name;
	}

}
