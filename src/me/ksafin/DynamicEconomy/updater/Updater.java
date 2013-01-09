package me.ksafin.DynamicEconomy.updater;

import java.util.Map;

import me.ksafin.DynamicEconomy.DynamicEconomy;
import me.ksafin.DynamicEconomy.settings.Settings;
import me.ksafin.DynamicEconomy.util.Message;

public class Updater {
	private static Map<String, String> data;
	
	public static boolean isUpdated() {
		Settings settings = DynamicEconomy.getSettings();
		DynamicEconomy plugin = DynamicEconomy.getInstance();
		
		if(!settings.UPDATE) return true;
		
		data = FetchSource.fetchSource();
		if(data == null) return true;
				if(data.get(plugin.getName()) == null) { return true; }
		double newVersion = Double.parseDouble(data.get(plugin.getName()));
		if(newVersion < DynamicEconomy.getVersion()) return true;
		
		Message.log(" +------------------------------------------+");
		Message.log(" |     DynamicEconomy is not up to date!    |");
		Message.log(" |           http://bit.ly/DynEcon          |");
		Message.log(" |                                          |");
		Message.log(" | New version: " + newVersion + "   Current version: " + DynamicEconomy.getVersion() + "  |");
		Message.log(" |                                          |");
		Message.log(" +------------------------------------------+");
		
		return false;
		
	}
}
