package me.ksafin.DynamicEconomy;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.MySQL;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicEconomy extends JavaPlugin
{
	public static FileConfiguration config;
	String name;
	String version;
	private final DynamicEconomyPlayerListener playerListener = new DynamicEconomyPlayerListener();
	private DynamicEconomyCommandExecutor commandExec;
	public static Economy economy = null;
	public static String prefix;
	public static int defaultAmount;
	public static boolean localNotify;
	public static boolean globalNotify;
	public static boolean logwriting;
	public static double salestax;
	public static double purchasetax;
	public static boolean depositTax;
	public static String taxAccount;
	public static boolean location_restrict;
	public static int minimum_y;
	public static int maximum_y;
	public static boolean altCommands;
	public static boolean useLoans;
	public static double interestRate;
	public static int paybackTime;
	public static int maxLoans;
	public static double maxLoanAmount;
	public static double minLoanAmount;
	public static boolean useLoanAccount;
	public static String loanAccountName;
	public static long loanCheckInterval;
	public static boolean useStaticInterest;
	public static double dynamicCompressionRate;
	public static String[] bannedSaleItems;
	public static String[] bannedPurchaseItems;
	public static boolean enableOverTimePriceDecay;
	public static double overTimePriceDecayPercent;
	public static boolean enableOverTimePriceInflation;
	public static double overTimePriceInflationPercent;
	public static long overTimePriceChangePeriod;
	public static long overTimePriceChangePeriodCheck;
	public static boolean taxAccountIsBank;
	public static boolean loanAccountIsBank;
	public static String[] dyneconWorld;
	public static String currencySymbol;
	public static String signTaglineColor;
	public static String signInfoColor;
	public static String signInvalidColor;
	public static boolean usemysql;
	private static String sqlhost;
	private static String sqluser;
	private static String sqlpw;
	private static String sqlport;
	private static String sqldb;
	public static boolean isBuySellCommandsEnabled;
	static DynamicEconomy plugin;
	public static File configFile;
	public static File loansFile;
	static FileConfiguration loansFileConfig;
	public static File itemsFile;
	static FileConfiguration itemConfig;
	public static File messagesFile;
	public static FileConfiguration messagesConfig;
	public static File signsFile;
	public static FileConfiguration signsConfig;
	public static File usersFile;
	public static FileConfiguration usersConfig;
	public static File aliasFile;
	public static FileConfiguration aliasConfig;
	public static File shopsFile;
	public static FileConfiguration shopsConfig;
	static Logger log = Logger.getLogger("Minecraft");
	public static MySQL mysql;
	public static Connection connection;
	
	@Override
	public void onEnable()
	{
		PluginManager pm = this.getServer().getPluginManager();
		PluginDescriptionFile pdf = pm.getPlugin("DynamicEconomy").getDescription();
		
		plugin = this;
		
		boolean economyIsSet = this.setupEconomy().booleanValue();
		if(economyIsSet) log.info("[DynamicEconomy] Vault Economy hooked");
		else log.info("[DynamicEconomy] Vault Economy not hooked");
		
		itemsFile = new File(this.getDataFolder(), "Items.yml");
		itemConfig = YamlConfiguration.loadConfiguration(itemsFile);
		
		loansFile = new File(this.getDataFolder(), "Loans.yml");
		loansFileConfig = YamlConfiguration.loadConfiguration(loansFile);	
		
		configFile = new File(this.getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		
		messagesFile = new File(this.getDataFolder(), "messages.yml");
		messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
		
		File signsDir = new File(this.getDataFolder(), "signs");
		signsFile = new File(signsDir, "signs.yml");
		signsConfig = YamlConfiguration.loadConfiguration(signsFile);
		
		usersFile = new File(this.getDataFolder(), "users.yml");
		usersConfig = YamlConfiguration.loadConfiguration(usersFile);
		
		aliasFile = new File(this.getDataFolder(), "alias.yml");
		aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
		
		shopsFile = new File(this.getDataFolder(), "shops.yml");
		shopsConfig = YamlConfiguration.loadConfiguration(shopsFile);
		
		File logFile = new File(this.getDataFolder(), "log.txt");
		new Utility(logFile, this);
		
		InputStream itemsIn = this.getClass().getResourceAsStream("Items.yml");
		InputStream configIn = this.getClass().getResourceAsStream("config.yml");
		InputStream aliasIn = this.getClass().getResourceAsStream("alias.yml");
		InputStream messagesIn = this.getClass().getResourceAsStream("messages.yml");
		
		if(itemsFile.exists()) log.info("[DynamicEconomy] Items database loaded.");
		else
		{
			try
			{ Utility.copyFile(itemsIn, itemsFile); }
			catch(Exception e)
			{
				log.info("[DynamicEconomy] IOException when creating Items.yml in Main");
				log.info(itemsFile.toString());
				e.printStackTrace();
			}
			log.info("[DynamicEconomy] Items.yml created.");
		}
		
		if(configFile.exists()) log.info("[DynamicEconomy] Core Config loaded.");
		else
		{
			try
			{ Utility.copyFile(configIn, configFile); }
			catch(Exception e)
			{
				log.info("[DynamicEconomy] IOException when creating config.yml in Main");
				e.printStackTrace();
			}
			log.info("[DynamicEconomy] config.yml created.");
		}
		
		if(signsFile.exists()) log.info("[DynamicEconomy] Signs File Loaded.");
		else
		{
			try
			{
				signsDir.mkdir();
				signsConfig.save(signsFile);
			}
			catch(Exception e)
			{
				log.info("[DynamicEconomy] IOException when creating Signs File in Main");
				e.printStackTrace();
			}
			log.info("[DynamicEconomy] signs.yml created.");
		}
		
		if(shopsFile.exists()) log.info("[DynamicEconomy] Shops File Loaded.");
		else
		{
			try
			{ shopsConfig.save(shopsFile); }
			catch(Exception e)
			{
				log.info("[DynamicEconomy] IOException when creating Shops File in Main");
				e.printStackTrace();
			}
			log.info("[DynamicEconomy] shops.yml created.");
		}
		
		if(loansFile.exists()) log.info("[DynamicEconomy] Loans database loaded.");
		else
		{
			try
			{
				loansFileConfig.save(loansFile);
				loansFileConfig.createSection("loans");
				loansFileConfig.createSection("debts");
				loan.initFiles(loansFile, loansFileConfig);
			}
			catch(Exception e)
			{
				log.info("[DynamicEconomy] IOException when creating Loans.yml in Main");
				log.info(loansFile.toString());
				e.printStackTrace();
			}
			log.info("[DynamicEconomy] loans.yml created.");
		}
		
		if(messagesFile.exists()) log.info("[DynamicEconomy] Messages loaded.");
		else
		{
			try
			{ Utility.copyFile(messagesIn, messagesFile); }
			catch(Exception e)
			{
				log.info("[DynamicEconomy] IOException when creating messages.yml in Main");
				e.printStackTrace();
			}
			log.info("[DynamicEconomy] messages.yml created.");
		}
		
		if(usersFile.exists()) log.info("[DynamicEconomy] User Settings loaded.");
		else
		{
			try
			{
				usersConfig.save(usersFile);
				usersConfig.load(usersFile);
			}
			catch(Exception e)
			{
				log.info("[DynamicEconomy] IOException when creating users.yml in Main");
				e.printStackTrace();
			}
			log.info("[DynamicEconomy] users.yml created.");
		}
		
		if(aliasFile.exists()) log.info("[DynamicEconomy] Alias list loaded.");
		else
		{
			try
			{ Utility.copyFile(aliasIn, aliasFile); }
			catch(Exception e)
			{
				log.info("[DynamicEconomy] IOException when creating alias.yml in Main");
				e.printStackTrace();
			}
			log.info("[DynamicEconomy] alias.yml created.");
		}
		
		relConfig();
		
		if(usemysql)
		{
			mysql = new MySQL(log, "[DynamicEconomy]", sqlhost, sqlport, sqldb, sqluser, sqlpw);
			if(mysql.checkConnection())
			{
				connection = mysql.open();
				
				if(mysql.createTable("CREATE TABLE DynamicEconomyItems(item VARCHAR(50),PRIMARY KEY(item),id INT,price DECIMAL,floor DECIMAL,ceiling DECIMALdescription TEXT,stock INT,span DECIMALbuytime BIGINT,selltime BIGINT)"))
					log.info("[DynamicEconomy] MySQL Items Table created.");
				
				if(mysql.checkTable("DynamicEconomySigns")) log.info("[DynamicEconomy] MySQL Signs Table loaded.");
				else if(mysql
						.createTable("CREATE TABLE DynamicEconomySigns(coords VARCHAR(50),PRIMARY KEY(coords),world VARCHAR(50),item VARCHAR(50),type VARCHAR(50))")) log
						.info("[DynamicEconomy] MySQL Signs Table created.");
				else log.info("[DynamicEconomy] Failed to create MySQL Signs table.");
				
				if(mysql.checkTable("DynamicEconomyLoans")) log.info("[DynamicEconomy] MySQL Loans Table loaded.");
				else mysql.createTable("createTable(DynamicEconomyLoans)");
				
				if(mysql.checkTable("DynamicEconomyUsers")) log.info("[DynamicEconomy] MySQL Users Table loaded.");
				else mysql.createTable("createTable(DynamicEconomyUsers)");
				
				if(mysql.checkTable("DynamicEconomyAlias")) log.info("[DynamicEconomy] MySQL Alias Table loaded.");
				else mysql.createTable("createTable(DynamicEconomyAlias)");
			}
			else
			{
				log.info("[DynamicEconomy] Could not connect to MySQL database. ");
				Utility.writeToLog("Could not connect to MySQL database");
			}
		}
		
		if(useLoans)
		{
			this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new loan(), 300L, loanCheckInterval);
			log.info("[DynamicEconomy] Loan Thread Spawned with delay of " + loanCheckInterval + " ticks");
		}
		
		if((enableOverTimePriceDecay) || (enableOverTimePriceInflation))
		{
			long delay = overTimePriceChangePeriodCheck * 60L * 20L;
			this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Transaction(), 300L, delay);
			log.info("[DynamicEconomy] Price Change Thread Spawned with delay of " + delay + " ticks");
		}
		
		this.commandExec = new DynamicEconomyCommandExecutor(this, pdf, config, configFile);
		
		this.getCommand("setfloor").setExecutor(this.commandExec);
		this.getCommand("setceiling").setExecutor(this.commandExec);
		this.getCommand("getfloor").setExecutor(this.commandExec);
		this.getCommand("getceiling").setExecutor(this.commandExec);
		this.getCommand("getspan").setExecutor(this.commandExec);
		this.getCommand("setspan").setExecutor(this.commandExec);
		this.getCommand("dynamiceconomy").setExecutor(this.commandExec);
		this.getCommand("addstock").setExecutor(this.commandExec);
		this.getCommand("dynamiceconomyreloadconfig").setExecutor(this.commandExec);
		this.getCommand("removestock").setExecutor(this.commandExec);
		this.getCommand("getdurability").setExecutor(this.commandExec);
		this.getCommand("dynecon").setExecutor(this.commandExec);
		this.getCommand("curtaxes").setExecutor(this.commandExec);
		this.getCommand("settax").setExecutor(this.commandExec);
		this.getCommand("loan").setExecutor(this.commandExec);
		this.getCommand("curinterest").setExecutor(this.commandExec);
		this.getCommand("curloans").setExecutor(this.commandExec);
		this.getCommand("curworld").setExecutor(this.commandExec);
		this.getCommand("banitem").setExecutor(this.commandExec);
		this.getCommand("unbanitem").setExecutor(this.commandExec);
		this.getCommand("marketquiet").setExecutor(this.commandExec);
		this.getCommand("addalias").setExecutor(this.commandExec);
		this.getCommand("removealias").setExecutor(this.commandExec);
		
		this.getCommand("canibuy").setExecutor(this.commandExec);
		this.getCommand("canisell").setExecutor(this.commandExec);
		
		this.getCommand("buyenchantment").setExecutor(this.commandExec);
		this.getCommand("sellenchantment").setExecutor(this.commandExec);
		this.getCommand("priceenchantment").setExecutor(this.commandExec);
		
		this.getCommand("renewpricing").setExecutor(this.commandExec);
		
		if(altCommands)
		{
			this.getCommand("debuy").setExecutor(this.commandExec);
			this.getCommand("desell").setExecutor(this.commandExec);
			this.getCommand("deprice").setExecutor(this.commandExec);
		}
		else
		{
			this.getCommand("buy").setExecutor(this.commandExec);
			this.getCommand("sell").setExecutor(this.commandExec);
			this.getCommand("price").setExecutor(this.commandExec);
		}
		
		pm.registerEvents(this.playerListener, this);
	}
	
	private Boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(
				Economy.class);
		if(economyProvider != null) economy = economyProvider.getProvider();
		
		if(economy != null) return Boolean.valueOf(true);
		return Boolean.valueOf(false);
	}
	
	public static void relConfig()
	{
		try
		{
			config.load(configFile);
			messagesConfig.load(messagesFile);
			itemConfig.load(itemsFile);
			signsConfig.load(signsFile);
			usersConfig.load(usersFile);
			aliasConfig.load(aliasFile);
			shopsConfig.load(shopsFile);
		}
		catch(Exception e)
		{
			log.info("[DynamicEconomy] Error loading config.yml in reloadConfigValues() ");
			log.info(e.toString());
			e.printStackTrace();
		}
		
		prefix = config.getString("prefix", "");
		defaultAmount = config.getInt("default-amount", 1);
		localNotify = config.getBoolean("local-price-notify", true);
		globalNotify = config.getBoolean("global-price-notify", true);
		logwriting = config.getBoolean("log-writing", true);
		salestax = config.getDouble("salestax", 0.0D);
		purchasetax = config.getDouble("purchasetax", 0.0D);
		depositTax = config.getBoolean("deposit-tax-to-account", false);
		taxAccount = config.getString("account-name", "");
		location_restrict = config.getBoolean("location-restrict", false);
		minimum_y = config.getInt("minimum-y", 0);
		maximum_y = config.getInt("maximum-y", 0);
		altCommands = config.getBoolean("alt-commands", false);
		useLoans = config.getBoolean("use-loans", true);
		useStaticInterest = config.getBoolean("use-static-interest", false);
		dynamicCompressionRate = config.getDouble("dynamic-compression-rate", 0.0D);
		
		if(useStaticInterest) interestRate = config.getDouble("interest-rate", 0.05D);
		else interestRate = config.getDouble("dynamic-interest-rate", 0.0D);
		
		if(interestRate == 0.0D) plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{ loan.dynamicInterest(true); }
			
		}, 100L);
		
		paybackTime = config.getInt("payback-time", 20);
		maxLoans = config.getInt("max-num-loans", 1);
		maxLoanAmount = config.getDouble("max-loan-amount", 500.0D);
		minLoanAmount = config.getInt("min-loan-amount", 10);
		useLoanAccount = config.getBoolean("use-loan-account", false);
		loanAccountName = config.getString("loan-account-name", "");
		loanCheckInterval = config.getLong("loan-check-interval", 300L);
		bannedSaleItems = config.getString("banned-sale-items", "").split(",");
		bannedPurchaseItems = config.getString("banned-purchase-items", "").split(",");
		
		enableOverTimePriceDecay = config.getBoolean("enable-over-time-price-decay", true);
		overTimePriceDecayPercent = config.getDouble("over-time-price-decay-percent", 0.1D);
		
		enableOverTimePriceInflation = config.getBoolean("enable-over-time-price-inflation", true);
		overTimePriceInflationPercent = config.getDouble("over-time-price-inflation-percent", 0.1D);
		
		overTimePriceChangePeriod = config.getLong("over-time-price-change-period", 1440L);
		overTimePriceChangePeriodCheck = config.getLong("over-time-price-change-period-check", 15L);
		
		taxAccountIsBank = config.getBoolean("tax-account-is-bank", false);
		loanAccountIsBank = config.getBoolean("loan-account-is-bank", false);
		
		dyneconWorld = config.getString("dynecon-world", "world").split(",");
		currencySymbol = config.getString("currency-symbol", "$");
		signTaglineColor = config.getString("sign-tagline-color", "&2");
		signInfoColor = config.getString("sign-info-color", "&f");
		signInvalidColor = config.getString("sign-invalid-color", "&c");
		usemysql = config.getBoolean("use-mysql", false);
		sqlhost = config.getString("hostname", "localhost");
		sqldb = config.getString("database", "dynamiceconomy");
		sqluser = config.getString("user", "root");
		sqlpw = config.getString("password", "root");
		sqlport = config.getString("port", "3306");
		
		isBuySellCommandsEnabled = config.getBoolean("enable-buysell-commands", true);
		
		Messages.getMessages();
		dataSigns.updateTaxSigns();
		
		dataSigns.updateColors();
		DynamicShop.updateColors();
	}
	
	@Override
	public void onDisable()
	{
		try
		{
			Utility.out.close();
			Utility.bf.close();
			loansFileConfig.save(loansFile);
			itemConfig.save(itemsFile);
			signsConfig.save(signsFile);
			usersConfig.save(usersFile);
			config.save(configFile);
			aliasConfig.save(aliasFile);
			shopsConfig.save(shopsFile);
			this.getServer().getScheduler().cancelTasks(this);
		}
		catch(Exception e)
		{ log.info("[DynamicEconomy] Exception saving configuration files."); }
	}
}
