package com.nisovin.magicspells.util.cmd;

import com.nisovin.magicspells.exception.MagicException;

public abstract class Arg<T> {

	private String name;
	public String getName() { return this.name; }
	
	private T defaultValue = null;
	public T getDefaultValue() { return this.defaultValue; }
	
	private boolean hasDefaultValue = false;
	public boolean hasDefaultValue() { return this.hasDefaultValue; }
	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
		this.hasDefaultValue = true;
	}
	
	public Arg(String name) {
		this.name = name;
	}
	
	public Arg(String name, T defaultValue) {
		this(name);
		this.setDefaultValue(defaultValue);
	}
	
	public T readValue(String input) throws MagicException {
		
		return readValueInner(input);
	}
	
	protected abstract T readValueInner(String input) throws MagicException;
	
}
