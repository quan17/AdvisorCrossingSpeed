package com.yuchuan.vehiclespeedadvisory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBManagerAdvSpeed {

	
	
	public static int getAdvSpeed() {
		String NowTime=null;
		int AdvSpeed=0;
		try
		{
			Connection con=getConnection();	
			System.out.println("successSQL2");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			NowTime=df.format(new Date());
				
//			PreparedStatement psta = con.prepareStatement("select a2 from (select id,a2,timestamp from advisory_speed order by id DESC limit 0,40)bb where timestamp = ?");
//			psta.setString(1, NowTime);
			PreparedStatement psta = con.prepareStatement("select a2 from (select id,a2,timestamp from advisory_speed order by id DESC limit 0,1)bb");		
				
			ResultSet rs = psta.executeQuery();
			
			if(rs.next())
			{
				AdvSpeed=rs.getInt("a2");
				System.out.println("AdvSpeed:"+AdvSpeed);
			}
			
			rs.close();
			psta.close();
			con.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return AdvSpeed;
	} 
	
	
	
	
	public static Connection getConnection()
	{
		Connection con=null;
		try  
	    {
	      Class.forName("com.mysql.jdbc.Driver");     
	      con = (Connection) DriverManager.getConnection(
	    		  "jdbc:mysql://128.235.162.244:3306/apha_live","apha","aphauser");
			          
		  System.out.println("Success connect Mysql server!");
	    }catch( SQLException ee) 
	    {
	    	ee.printStackTrace();
	    }
		catch (Exception eeea)
	    {
	      System.out.println("Error loading Mysql Driver!"); 
	      eeea.printStackTrace(); 
	    }
	    return con;
	}
}
