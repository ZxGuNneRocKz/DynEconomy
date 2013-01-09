package me.ksafin.DynamicEconomy.settings;

import me.ksafin.DynamicEconomy.DynamicEconomy;

public class Settings {
	public final String GENERAL_TITLE;
	public final boolean UPDATE;
	public final boolean DEBUG;
	
	public Settings(DynamicEconomy plugin) {
		GENERAL_TITLE = "&b[DE]&f";
		UPDATE = true;
		DEBUG = true;
	}
}
