import java.sql.*;

public class Database {
	
	private PreparedStatement stmt = null;
	private ResultSet rs = null;
	private String db_addr;
	private String db_root;
	private String db_pw; // ¸â¹ö º¯¼ö
	private Connection conn; 
	//sanghyun trace
	public Database(String _db_addr,String _db_root,String _db_pw){
		this.db_addr= _db_addr;
		this.db_root= _db_root;
		this.db_pw= _db_pw;
		
		try 
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} 
		catch (Exception ex) 
		{
			System.out.println("error_occur");
		}
		try{
			conn = DriverManager.getConnection("jdbc:mysql://"+db_addr+":3306" ,db_root ,db_pw);
		
		}
		catch(SQLException ex){
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		 
	}
	
	public Database() {
		// TODO Auto-generated constructor stub
	}

	public Connection get_connection(){
		return this.conn;
	}
	public void set_connection(Connection _c){
		this.conn = _c;
	}
	
	public String get_db_addr(){
		return this.db_addr;
	}
	public String get_db_root(){
		return this.db_root;
	}
	public String get_db_pw(){
		return this.db_pw;
	}
	public void set_db_addr(String _addr){
		this.db_addr = _addr;
	}
	public void set_db_root(String _root){
		this.db_root = _root;
	}
	public void set_db_pw(String _pw){
		this.db_pw = _pw;
	}
	public void set_statement(PreparedStatement _stmt){
		this.stmt = _stmt;
	}
	
	public PreparedStatement get_statement(){
		return this.stmt;
	}
	public ResultSet get_resultset(){
		return this.rs;
	}
	public void set_resultset(ResultSet _rst){
		this.rs = _rst;
	}
	
	public void finalize(){
		try{
			this.rs.close();
			
		}catch(Exception rse){
			System.out.println("Resultset close error.");
		}
		try{
			this.stmt.close();
		}catch(Exception pse){
			System.out.println("PreparedStatement close error.");
		}
		try{
			this.conn.close();
		}catch(Exception cone){
			System.out.println("Connection close Error.");
		}
		
	}

	
	
}
