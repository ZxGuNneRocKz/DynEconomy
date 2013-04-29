package me.ksafin.DynamicEconomy;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

public class EnderEngine
{
	private String item;
	private double price;
	private double floor;
	private double ceiling;
	private double span;
	private int stock;
	private long id;
	private long buyTime;
	private long sellTime;
	private static NumberFormat f = NumberFormat.getNumberInstance(Locale.US);
	private static DecimalFormat decFormat = (DecimalFormat) f;
	
	private static Logger log = Logger.getLogger("Minecraft");
	
	public EnderEngine(String[] itemProperties)
	{
		this.item = itemProperties[0];
		this.price = Double.parseDouble(itemProperties[1]);
		this.floor = Double.parseDouble(itemProperties[2]);
		this.ceiling = Double.parseDouble(itemProperties[3]);
		this.span = Double.parseDouble(itemProperties[4]);
		this.stock = Integer.parseInt(itemProperties[5]);
		this.id = Long.parseLong(itemProperties[6]);
		decFormat.applyPattern("#.##");
	}
	
	public double getCost(int amount)
	{
		double totalcost = 0.0D;
		
		for(int x = 0; x < amount; x++)
		{
			this.price = calcPrice(this.stock, this.ceiling, this.floor, this.span);
			totalcost += this.price;
			this.stock -= 1;
		}
		
		return totalcost;
	}
	
	public void diagnose(double total)
	{
		log.info("[EnderEngine] Item Diagnostics");
		log.info("[EnderEngine] Item: " + this.item);
		log.info("[EnderEngine] Price: " + this.price);
		log.info("[EnderEngine] Floor: " + this.floor);
		log.info("[EnderEngine] Ceiling: " + this.ceiling);
		log.info("[EnderEngine] Span: " + this.span);
		log.info("[EnderEngine] Stock:" + this.stock);
		log.info("[EnderEngine] ID: " + this.id);
		log.info("[EnderEngine] ------------------");
		if(total != 0.0D) log.info("[EnderEngine] Cost: " + total);
	}
	
	public void reCalculatePrices()
	{
		Set<String> items = DynamicEconomy.itemConfig.getKeys(false);
		Iterator<String> i = items.iterator();
		
		while(i.hasNext())
		{
			String curItem = (String) i.next();
			String[] curItemInfo = Item.getAllInfo(curItem);
			this.item = curItemInfo[0];
			if(!this.item.equals(""))
			{
				this.floor = Double.parseDouble(curItemInfo[2]);
				this.ceiling = Double.parseDouble(curItemInfo[3]);
				this.span = Double.parseDouble(curItemInfo[4]);
				this.stock = Integer.parseInt(curItemInfo[5]);
				this.price = calcPrice(this.stock, this.ceiling, this.floor, this.span);
				updateConfig();
			}
		}
	}
	
	public double getSale(int amount)
	{
		double totalsale = 0.0D;
		
		for(int x = 0; x < amount; x++)
		{
			this.stock += 1;
			this.price = calcPrice(this.stock, this.ceiling, this.floor, this.span);
			totalsale += this.price;
		}
		
		return totalsale;
	}
	
	public double getPrice()
	{ return this.price; }
	
	public int getStock()
	{ return this.stock; }
	
	public double getFloor()
	{ return this.floor; }
	
	public double getCeiling()
	{ return this.ceiling; }
	
	public void setFloor(double newFloor)
	{ this.floor = newFloor; }
	
	public void setCeiling(double newCeiling)
	{ this.ceiling = newCeiling; }
	
	public void decrementStock(int amt)
	{
		this.stock -= amt;
		this.price = calcPrice(this.stock, this.ceiling, this.floor, this.span);
	}
	
	public void incrementStock(int amt)
	{
		this.stock += amt;
		this.price = calcPrice(this.stock, this.ceiling, this.floor, this.span);
	}
	
	public void updateConfig()
	{
		String node = this.item + ".";
		DynamicEconomy.itemConfig.set(node + "price", Double.valueOf(this.price));
		DynamicEconomy.itemConfig.set(node + "stock", Integer.valueOf(this.stock));
		DynamicEconomy.itemConfig.set(node + "ceiling", Double.valueOf(this.ceiling));
		DynamicEconomy.itemConfig.set(node + "floor", Double.valueOf(this.floor));
		
		saveConfig();
	}
	
	public void updatePrice()
	{ this.price = calcPrice(this.stock, this.ceiling, this.floor, this.span); }
	
	public void setBuyTime()
	{
		this.buyTime = Calendar.getInstance().getTimeInMillis();
		DynamicEconomy.itemConfig.set(this.item + ".buytime", Long.valueOf(this.buyTime));
	}
	
	public void setSellTime()
	{
		this.sellTime = Calendar.getInstance().getTimeInMillis();
		DynamicEconomy.itemConfig.set(this.item + ".selltime", Long.valueOf(this.sellTime));
	}
	
	private void saveConfig()
	{
		try
		{ DynamicEconomy.itemConfig.save(DynamicEconomy.itemsFile); }
		catch(IOException e)
		{
			Utility.writeToLog("[EnderEngine] Error saving new Item info for " + this.item);
			e.printStackTrace();
		}
	}
	
	public boolean isEnchantment()
	{
		if((this.id >= 2500L) && (this.id < 2600L)) { return true; }
		return false;
	}
	
	public void inflate()
	{
		boolean inflated = false;
		
		double origPrice = this.price;
		
		while((!inflated) && (this.stock != 0))
		{
			this.stock -= 1;
			updatePrice();
			
			if(Math.abs(origPrice - this.price) / origPrice >= DynamicEconomy.overTimePriceInflationPercent)
			{
				updateConfig();
				break;
			}
		}
	}
	
	public void decay()
	{
		boolean inflated = false;
		
		double origPrice = this.price;
		
		while((!inflated) && (this.stock != 0))
		{
			this.stock += 1;
			updatePrice();
			
			if((origPrice - this.price) / origPrice >= DynamicEconomy.overTimePriceDecayPercent)
			{
				updateConfig();
				break;
			}
		}
	}
	
	private double calcPrice(double s, double c, double f, double sp)
	{
		double numerator = (c - f) * Math.sqrt(s / sp);
		double denominator = -1.0D * Math.sqrt(s / sp) - 1.0D;
		double frac = numerator / denominator;
		double price = frac + c;
		price = Double.valueOf(decFormat.format(price)).doubleValue();
		return price;
	}
}
