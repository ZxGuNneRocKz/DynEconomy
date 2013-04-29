package lib.PatPeter.SQLibrary;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class SQLite extends Database
{
	public String location;
	public String name;
	private File sqlFile;
	
	public SQLite(Logger log, String prefix, String name, String location)
	{
		super(log, prefix, "[SQLite] ");
		this.name = name;
		this.location = location;
		File folder = new File(this.location);
		if((this.name.contains("/")) || (this.name.contains("\\")) || (this.name.endsWith(".db")))
			writeError("The database name can not contain: /, \\, or .db", true);
		if(!folder.exists()) folder.mkdir();
		
		this.sqlFile = new File(folder.getAbsolutePath() + File.separator + name + ".db");
	}
	
	@Override
	protected boolean initialize()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			
			return true;
		}
		catch(ClassNotFoundException e)
		{ writeError("You need the SQLite library " + e, true); }
		return false;
	}
	
	@Override
	public Connection open()
	{
		if(initialize())
		{
			try
			{
				this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.sqlFile.getAbsolutePath());
				return this.connection;
			}
			catch(SQLException e)
			{ writeError("SQLite exception on initialize " + e, true); }
		}
		return null;
	}
	
	@Override
	public void close()
	{
		if(this.connection != null)
		{
			try
			{ this.connection.close(); }
			catch(SQLException ex)
			{ writeError("Error on Connection close: " + ex, true); }
		}
	}
	
	@Override
	public Connection getConnection()
	{
		if(this.connection == null) return open();
		return this.connection;
	}
	
	@Override
	public boolean checkConnection()
	{
		if(this.connection != null) return true;
		return false;
	}
	
	@Override
	public ResultSet query(String query)
	{
		Statement statement = null;
		ResultSet result = null;
		try
		{
			this.connection = open();
			statement = this.connection.createStatement();
			
			statement.executeQuery(query);
			return result;
		}
		catch(SQLException ex)
		{
			if((ex.getMessage().toLowerCase().contains("locking")) || (ex.getMessage().toLowerCase().contains("locked"))) { return retry(query); }
			
			writeError("Error at SQL Query: " + ex.getMessage(), false);
		}
		
		return null;
	}
	
	@Override
	PreparedStatement prepare(String query)
	{
		try
		{
			this.connection = open();
			return this.connection.prepareStatement(query);
		}
		catch(SQLException e)
		{
			if(!e.toString().contains("not return ResultSet"))
				writeError("Error in SQL prepare() query: " + e.getMessage(), false);
		}
		return null;
	}
	
	@Override
	public boolean createTable(String query)
	{
		Statement statement = null;
		try
		{
			if((query.equals("")) || (query == null))
			{
				writeError("SQL Create Table query empty.", true);
				return false;
			}
			
			statement = this.connection.createStatement();
			statement.execute(query);
			return true;
		}
		catch(SQLException ex)
		{
			writeError(ex.getMessage(), true);
		}
		return false;
	}
	
	@Override
	public boolean checkTable(String table)
	{
		DatabaseMetaData dbm = null;
		try
		{
			dbm = open().getMetaData();
			ResultSet tables = dbm.getTables(null, null, table, null);
			if(tables.next()) { return true; }
			return false;
		}
		catch(SQLException e)
		{
			writeError("Failed to check if table \"" + table + "\" exists: " + e.getMessage(), true);
		}
		return false;
	}
	
	@Override
	public boolean wipeTable(String table)
	{
		Statement statement = null;
		String query = null;
		try
		{
			if(!checkTable(table))
			{
				writeError("Error at Wipe Table: table, " + table + ", does not exist", true);
				return false;
			}
			statement = this.connection.createStatement();
			query = "DELETE FROM " + table + ";";
			statement.executeQuery(query);
			return true;
		}
		catch(SQLException ex)
		{
			if((!ex.getMessage().toLowerCase().contains("locking")) && (!ex.getMessage().toLowerCase().contains("locked")) && (!ex
					.toString().contains("not return ResultSet")))
				writeError("Error at SQL Wipe Table Query: " + ex, false);
		}
		return false;
	}
	
	public ResultSet retry(String query)
	{
		Statement statement = null;
		try
		{
			statement = this.connection.createStatement();
			return statement.executeQuery(query);
		}
		catch(SQLException ex)
		{
			if((ex.getMessage().toLowerCase().contains("locking")) || (ex.getMessage().toLowerCase().contains("locked")))
				writeError("Please close your previous ResultSet to run the query: \n" + query, false);
			else writeError("Error in SQL query: " + ex.getMessage(), false);	
		}
		
		return null;
	}
}
