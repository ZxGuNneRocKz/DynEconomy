package me.ksafin.DynamicEconomy;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import couk.Adamki11s.Extras.Colour.ExtrasColour;

public class loan implements Runnable
{
	private static double amount;
	private Player player;
	private static final ExtrasColour color = new ExtrasColour();
	private static File loansFile;
	private static FileConfiguration loansFileConfig;
	
	static NumberFormat f = NumberFormat.getNumberInstance(Locale.US);
	public static DecimalFormat decFormat = (DecimalFormat) f;
	
	private String prefix = "loans.";
	
	private static Logger log = Logger.getLogger("Minecraft");
	
	public loan(double amt, Player play, int numLoans)
	{
		try
		{ loansFileConfig.load(loansFile); }
		catch(Exception e)
		{ e.printStackTrace(); }
		
		amount = amt;
		this.player = play;
		
		long time = Calendar.getInstance().getTimeInMillis();
		time += DynamicEconomy.paybackTime * 60 * 1000;
		
		numLoans++;
		String node = this.prefix + this.player.getName() + ".loan" + numLoans + ".";
		
		double interest = DynamicEconomy.interestRate * amount;
		
		loansFileConfig.set(node + "amount", Double.valueOf(amount));
		loansFileConfig.set(node + "time", Long.valueOf(time));
		loansFileConfig.set(node + "interest", Double.valueOf(interest));
		loansFileConfig.set(node + "debt", Double.valueOf(amount + interest));
		try
		{ loansFileConfig.save(loansFile); }
		catch(Exception e)
		{ Utility.writeToLog("Error creating loan of " + amount + " for " + play.getName()); }
		
		color.sendColouredMessage(this.player, "&2New Loan of &f" + DynamicEconomy.currencySymbol + amount + "&2 created!");
		color.sendColouredMessage(this.player, "&2You will be charged &f" + DynamicEconomy.currencySymbol + 
				(interest + amount) + "&2 in &f" + DynamicEconomy.paybackTime + "&2 minutes.");
		Utility.writeToLog(this.player.getName() + " has taken a loan for " + DynamicEconomy.currencySymbol + 
				(amount + interest));
		
		DynamicEconomy.economy.depositPlayer(this.player.getName(), amount);
		
		if((DynamicEconomy.useLoanAccount) && (!DynamicEconomy.loanAccountName.equals("")))
		{
			try
			{
				if(DynamicEconomy.loanAccountIsBank) DynamicEconomy.economy.bankWithdraw(DynamicEconomy.loanAccountName, 
						amount + interest);
				else DynamicEconomy.economy.withdrawPlayer(DynamicEconomy.loanAccountName, amount);
			}
			catch(Exception e)
			{
				log.info("Loan-Account " + DynamicEconomy.loanAccountName + " not found.");
				Utility.writeToLog("Attempted to withdraw loan of " + DynamicEconomy.currencySymbol + (amount + interest) + 
						" from account " + DynamicEconomy.loanAccountName + " but account not found.");
			}
		}
	}
	
	public loan() {}
	
	public static void initFiles(File loansF, FileConfiguration loansFileC)
	{
		loansFile = loansF;
		loansFileConfig = YamlConfiguration.loadConfiguration(loansFile);
	}
	
	public static boolean lend(Player player, String[] args)
	{
		String stringPlay = player.getName();
		if(args.length != 1)
		{
			color.sendColouredMessage(player, DynamicEconomy.prefix + "&2Wrong Command Usage. &f/loan [Amount]");
			Utility.writeToLog(stringPlay + " incorrectly called /loan");
			return false;
		}
		try
		{ loansFileConfig.load(loansFile); }
		catch(Exception e)
		{ e.printStackTrace(); }
		if(hasOutstandingDebt(player.getName()))
		{
			color.sendColouredMessage(
					player,
					DynamicEconomy.prefix + "&2You have an outstanding debt and therefore &fcannot " + 
						"&2take out any loans until it is payed.");
			return false;
		}
		
		try
		{ amount = Integer.parseInt(args[0]); 	}
		catch(Exception e)
		{
			color.sendColouredMessage(player,
					DynamicEconomy.prefix + "&2Wrong Command Usage. &f/loan [Amount], Amount must be a valid quantity.");
			Utility.writeToLog(stringPlay + " incorrectly called /loan");
			return false;
		}
		double amount = 0.0D;
		String node = "loans." + stringPlay;
		
		if(!loansFileConfig.contains(node)) loansFileConfig.createSection(node);
		
		Set<String> loansSet = loansFileConfig.getConfigurationSection(node).getKeys(false);
		
		int numLoans = loansSet.size();
		
		if(numLoans >= DynamicEconomy.maxLoans)
		{
			color.sendColouredMessage(player, Messages.maxLoans);
			return false;
		}
		
		if(amount > DynamicEconomy.maxLoanAmount)
		{
			color.sendColouredMessage(player, 
					"&2The requested loan is too large; Max loan amount is &f" + 
					DynamicEconomy.currencySymbol + DynamicEconomy.maxLoanAmount);
			return false;
		}
		if(amount < DynamicEconomy.minLoanAmount)
		{
			color.sendColouredMessage(player,
					"&2The requested loan is too small; Min loan amount is &f" + 
					DynamicEconomy.currencySymbol + DynamicEconomy.minLoanAmount);
			return false;
		}
		
		if(DynamicEconomy.useLoanAccount)
		{
			if(!DynamicEconomy.loanAccountName.equals(""))
			{
				double bal;
				if(DynamicEconomy.useLoanAccount)
					bal = DynamicEconomy.economy.bankBalance(DynamicEconomy.loanAccountName).balance;
				else bal = DynamicEconomy.economy.getBalance(DynamicEconomy.loanAccountName);
				if(amount > bal)
				{
					color.sendColouredMessage(player,
							"&2The Bank does not have enough money to loan you &f" + DynamicEconomy.currencySymbol + amount);
					Utility.writeToLog(stringPlay + " requested a loan, but the bank does not have enough funds.");
				}
			}
			else
			{
				color.sendColouredMessage(player, Messages.loanAccountNotFound);
				Utility.writeToLog(stringPlay + 
						" requested a loan, and use-loan-account in config.yml is true, but no account is set.");
				return false;
			}
		}
		
		new loan(amount, player, numLoans);
		try
		{ loansFileConfig.save(loansFile); }
		catch(Exception e)
		{ e.printStackTrace(); }
		return true;
	}
	
	public static void getInterest(Player player, String[] args)
	{
		decFormat.applyPattern("#.####");
		double interest = Double.valueOf(decFormat.format(DynamicEconomy.interestRate)).doubleValue() * 100.0D;
		
		color.sendColouredMessage(player, "&2The current Interest Rate is &f" + interest + " %");
		Utility.writeToLog(player.getName() + " called /curinterest");
	}
	
	public static void getLoans(Player player, String[] args)
	{
		int page = 0;
		try
		{ if(args.length == 1) page = Integer.parseInt(args[0]); }
		catch(Exception e)
		{
			color.sendColouredMessage(player, "&2You did not pass a valid loan #. /curloans [Loan #]");
			Utility.writeToLog(player.getName() + " called /curloans, but didn't give valid loan #.");
		}
		
		String node = "loans." + player.getName();
		
		Set<String> loansSet = loansFileConfig.getConfigurationSection(node).getKeys(false);
		
		if(loansSet.size() == 0)
		{
			color.sendColouredMessage(player, "&2You do not have any loans!");
			Utility.writeToLog(player.getName() + " called /curloans, but didn't have any loans.");
			return;
		}
		
		if(args.length == 0)
		{
			color.sendColouredMessage(player, "&2---------&fCurrent Loans&2---------");
			for(int x = 0; x < loansSet.size(); x++)
			{
				node = "loans." + player.getName() + ".loan" + (x + 1);
				
				double amount = loansFileConfig.getDouble(node + ".amount");
				double interest = loansFileConfig.getDouble(node + ".interest");
				long time = loansFileConfig.getLong(node + ".time");
				double debt = 0.0D;
				boolean debtStatus = false;
				
				if(loansFileConfig.contains(node + ".debtStatus"))
				{
					debt = loansFileConfig.getDouble(node + ".debt");
					debtStatus = true;
				}
				
				long curTime = Calendar.getInstance().getTimeInMillis();
				
				double remainingTime = time - curTime;
				remainingTime /= 1000.0D;
				int seconds = (int) (remainingTime % 60.0D);
				int minutes = (int) ((remainingTime - seconds) / 60.0D);
				
				color.sendColouredMessage(player, "&2Loan " + (x + 1));
				color.sendColouredMessage(player, "&2Original Loan Amount: &f" + amount);
				color.sendColouredMessage(player, "&2Interest Rate: &f" + DynamicEconomy.interestRate * 100.0D + " %");
				if(debtStatus) color.sendColouredMessage(player, "&2Total due: &f" + DynamicEconomy.currencySymbol + debt);
				else color.sendColouredMessage(player, 
						"&2Total due: &f" + DynamicEconomy.currencySymbol + (interest + amount));
				
				color.sendColouredMessage(player,
						"&2Due in approximately &f" + minutes + "&2 minutes and &f" + seconds + "&2 seconds");
				color.sendColouredMessage(player,
						"&2---------&fLoan &f" + (x + 1) + "&2 of &f" + loansSet.size() + "&2---------");
			}
		}
		else if(page > loansSet.size()) color.sendColouredMessage(player, "&2You do not have this many loans");
		else
		{
			node = "loans." + player.getName() + ".loan" + page;
			
			double amount = loansFileConfig.getDouble(node + ".amount");
			double interest = loansFileConfig.getDouble(node + ".interest");
			long time = loansFileConfig.getLong(node + ".time");
			
			long curTime = Calendar.getInstance().getTimeInMillis();
			
			double remainingTime = time - curTime;
			remainingTime /= 1000.0D;
			int seconds = (int) (remainingTime % 60.0D);
			int minutes = (int) ((remainingTime - seconds) / 60.0D);
			
			color.sendColouredMessage(player, "&2---------&fCurrent Loans&2---------");
			color.sendColouredMessage(player, "&2Loan " + page);
			color.sendColouredMessage(player, "&2Original Loan Amount: &f" + DynamicEconomy.currencySymbol + amount);
			color.sendColouredMessage(player, "&2Interest Rate: &f" + DynamicEconomy.interestRate * 100.0D + " %");
			color.sendColouredMessage(player, "&2Total due: &f" + DynamicEconomy.currencySymbol + (interest + amount));
			color.sendColouredMessage(player,
					"&2Due in approximately &f" + minutes + "&2 minutes and &f" + seconds + "&2 seconds");
			color.sendColouredMessage(player, "&2---------&fLoan &f" + page + "&2 of &f" + loansSet.size() + "---------");
		}
	}
	
	public static double dynamicInterest(boolean init)
	{
		try
		{ DynamicEconomy.config.load(DynamicEconomy.configFile); }
		catch(Exception e)
		{ Utility.writeToLog("Error loading config file."); }
		
		String[] diamondInfo = Item.getAllInfo("DIAMOND");
		String[] goldInfo = Item.getAllInfo("GOLDINGOT");
		String[] ironInfo = Item.getAllInfo("IRONINGOT");
		String[] coalInfo = Item.getAllInfo("COAL");
		
		double diamondPrice = Double.parseDouble(diamondInfo[1]);
		double goldPrice = Double.parseDouble(goldInfo[1]);
		double ironPrice = Double.parseDouble(ironInfo[1]);
		double coalPrice = Double.parseDouble(coalInfo[1]);
		
		double sumPrice = diamondPrice + goldPrice + ironPrice + coalPrice;
		double interest;
		if(init)
		{
			double compRate = sumPrice / 0.05D;
			DynamicEconomy.config.set("dynamic-compression-rate", Double.valueOf(compRate));
			
			interest = sumPrice / compRate;
		}
		else interest = sumPrice / DynamicEconomy.config.getDouble("dynamic-compression-rate", 0.0D);
		
		decFormat.applyPattern("#.####");
		interest = Double.valueOf(decFormat.format(interest)).doubleValue();
		
		DynamicEconomy.interestRate = interest;
		DynamicEconomy.config.set("dynamic-interest-rate", Double.valueOf(interest));
		try
		{ DynamicEconomy.config.save(DynamicEconomy.configFile); }
		catch(Exception e)
		{ e.printStackTrace(); }
		
		return interest;
	}
	
	private static boolean hasOutstandingDebt(String playerName)
	{
		updateDebtStatus(playerName);
		try
		{ loansFileConfig.load(loansFile); }
		catch(Exception e)
		{ Utility.writeToLog("Error loading loans file."); }
		
		String request = "debts." + playerName;
		boolean debt = loansFileConfig.getBoolean(request, false);
		
		return debt;
	}
	
	private static void updateDebtStatus(String playerName)
	{
		try
		{ loansFileConfig.load(loansFile); }
		catch(Exception e)
		{ Utility.writeToLog("Error loading loans file."); }
		
		if(!loansFileConfig.contains("loans." + playerName)) { return; }
		
		Set<String> userLoansSet = loansFileConfig.getConfigurationSection("loans." + playerName).getKeys(false);
		
		Object[] userLoansObj = userLoansSet.toArray();
		String[] userLoans = new String[userLoansObj.length];
		
		for(int x = 0; x < userLoansObj.length; x++)
		{ userLoans[x] = userLoansObj[x].toString(); }
		
		boolean debt = false;
		String node = "loans." + playerName;
		boolean debtStat = false;
		
		for(int x = 0; x < userLoans.length; x++)
		{
			node = "loans." + playerName + "." + userLoans[x] + ".debtStatus";
			debtStat = loansFileConfig.getBoolean(node, false);
			if(debtStat)
			{
				debt = true;
				break;
			}
		}
		
		loansFileConfig.set("debts." + playerName, Boolean.valueOf(debt));
		try
		{ loansFileConfig.save(loansFile); }
		catch(Exception e)
		{ Utility.writeToLog("Error saving loans file."); }
	}
	
	public void run()
	{
		if(DynamicEconomy.useLoans)
		{
			try
			{ loansFileConfig.load(loansFile); }
			catch(Exception e)
			{ Utility.writeToLog("Error loading loans file."); }
			
			DynamicEconomy.useStaticInterest = DynamicEconomy.config.getBoolean("use-static-interest", false);
			
			if(!DynamicEconomy.useStaticInterest) dynamicInterest(false);
			
			ConfigurationSection configSection = loansFileConfig.getConfigurationSection("loans");
			Set<String> loansSet;
			if(configSection == null) loansSet = new TreeSet<String>();
			else loansSet = configSection.getKeys(false);
			
			if(loansSet != null)
			{
				Object[] loansObj = loansSet.toArray();
				String[] loans = new String[loansObj.length];
				
				for(int i = 0; i < loans.length; i++) loans[i] = loansObj[i].toString();
				
				decFormat.applyPattern("#.###");
				
				for(int i = 0; i < loans.length; i++)
				{
					String node = "loans." + loans[i];
					Set<String> userLoansSet = loansFileConfig.getConfigurationSection(node).getKeys(false);
					
					Object[] userLoansObj = userLoansSet.toArray();
					String[] userLoans = new String[userLoansObj.length];
					
					for(int x = 0; x < userLoansObj.length; x++) userLoans[x] = userLoansObj[x].toString();
					
					for(int x = 0; x < userLoansSet.size(); x++)
					{
						node = "loans." + loans[i] + "." + userLoans[x];
						
						long time = loansFileConfig.getLong(node + ".time");
						double interest = loansFileConfig.getLong(node + ".interest");
						double amount = loansFileConfig.getLong(node + ".amount");
						double debt = loansFileConfig.getDouble(node + ".debt");
						
						long curTime = Calendar.getInstance().getTimeInMillis();
						
						if(curTime > time)
						{
							Player player = Bukkit.getServer().getPlayer(loans[i]);
							double bal = DynamicEconomy.economy.getBalance(loans[i]);
							
							if((bal < debt) && (bal != 0.0D))
							{
								debt -= bal;
								loansFileConfig.set(node + ".debt", Double.valueOf(debt));
								DynamicEconomy.economy.withdrawPlayer(loans[i], bal);
								
								if(player != null)
								{
									color.sendColouredMessage(player,
											DynamicEconomy.prefix + "&2You have been charged &f" +
											DynamicEconomy.currencySymbol + decFormat.format(bal) + "&2 for your loan.");
									color.sendColouredMessage(player,
											DynamicEconomy.prefix + "&2You have  &f" + DynamicEconomy.currencySymbol + 
											decFormat.format(debt) + "&2 remaining to pay.");
									color.sendColouredMessage(player, DynamicEconomy.prefix + 
											"&2Until this is paid, you &fcannot &2take out any more loans.");
								}
								Utility.writeToLog(loans[i] + " has been charged " + (amount + interest) + 
										" for their loan.");
								
								loansFileConfig.set(node + ".debtStatus", Boolean.valueOf(true));
								loansFileConfig.set("debts." + loans[i], Boolean.valueOf(true));
							}
							else if(bal > debt)
							{
								DynamicEconomy.economy.withdrawPlayer(loans[i], debt);
								
								if(player != null)
								{
									color.sendColouredMessage(player,
											DynamicEconomy.prefix + "&2You have been charged &f" + 
											DynamicEconomy.currencySymbol + decFormat.format(debt) + "&2 for your loan.");
								}
								Utility.writeToLog(loans[i] + " has been charged " + debt + " for their loan.");
								loansFileConfig.set(node, null);
							}
							try
							{ loansFileConfig.save(loansFile); }
							catch(Exception e)
							{ e.printStackTrace(); }
							
							if(!DynamicEconomy.loanAccountName.equals(""))
							{
								try
								{
										if(DynamicEconomy.loanAccountIsBank) DynamicEconomy.economy.bankDeposit(
												DynamicEconomy.loanAccountName, amount + interest);
										else DynamicEconomy.economy.depositPlayer(DynamicEconomy.loanAccountName,
												amount + interest);
								}
								catch(Exception e)
								{
									log.info("Loan-Account " + DynamicEconomy.loanAccountName + " not found.");
									Utility.writeToLog("Attempted to deposit loan of " + 
											DynamicEconomy.currencySymbol + (amount + interest) + " to account " + 
											DynamicEconomy.loanAccountName + " but account not found.");
								}
							}
						}
					}
				}
			}
		}
	}
}
