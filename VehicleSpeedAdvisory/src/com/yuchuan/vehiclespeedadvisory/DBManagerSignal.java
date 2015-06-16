package com.yuchuan.vehiclespeedadvisory;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;



public class DBManagerSignal
{	
		public static int P1MinGreen = 122;
//		public static int P2MinGreen = 42;
		
		private static int YellowRemain = 5;
		
	
		public static int RunJDBC() {
		String FormatTime=null;
		int GrRemainTime=-1;
		try
		{
			Connection con=getConnection();	
			System.out.println("successSQL");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			FormatTime=df.format(new Date());
				
//			PreparedStatement psta = con.prepareStatement("select s1,g1,c2,c4 from (select id,s1,g1,c2,c4,timestamp from test order by id DESC limit 0,40)aa where timestamp = ?");
//			psta.setString(1, FormatTime);
			PreparedStatement psta = con.prepareStatement("select s1,g1,c2,c4 from (select id,s1,g1,c2,c4,timestamp from test order by id DESC limit 0,1)aa");
			System.out.println("successState");
				
				
			ResultSet rs = psta.executeQuery();
			System.out.println("successEXE");
			if(rs.next())
			{
				GrRemainTime=GreenRemain(rs);
				if(rs.getInt("s1")<2)
					System.out.println("p1 Green remain:"+GrRemainTime);
				else 
//					System.out.println("p1 Red");
					GrRemainTime=-1;
			}
			
			rs.close();
			psta.close();
			con.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return GrRemainTime;
	} 

	public static int GreenRemain(ResultSet CurrentSet) throws SQLException{
		int IntResult = -1;
		if(CurrentSet.getInt("s1")==0){
			//p1 green now
			if(CurrentSet.getInt("g1")<P1MinGreen){
				if((CurrentSet.getInt("c2")+CurrentSet.getInt("c4"))!=0)
					IntResult = P1MinGreen-CurrentSet.getInt("g1")+6;
				else
					IntResult = P1MinGreen-CurrentSet.getInt("g1")+6; //while((CurrentSet.getInt("c2")+CurrentSet.getInt("c4"))==0)
			}
			else
				P1MinGreen=CurrentSet.getInt("g1");
		}
//		else{
//			if(CurrentSet.getInt("s2")==0){
//				//p2 green now
//				if(CurrentSet.getInt("g2")<P2MinGreen){
//					if((CurrentSet.getInt("c1")+CurrentSet.getInt("c3"))!=0)
//						IntResult = P2MinGreen-CurrentSet.getInt("g2")+6;
//					else
//						IntResult = P2MinGreen-CurrentSet.getInt("g2")+6; //while((CurrentSet.getInt("c1")+CurrentSet.getInt("c3"))==0)
//				}
//			}
			else
				//yellow 
				if(CurrentSet.getInt("s1")==1){
					
					IntResult=-2;
					}
				else
				//all red
				IntResult=0;
		
		
		return IntResult;
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

