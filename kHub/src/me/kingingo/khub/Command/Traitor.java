package me.kingingo.khub.Command;

import java.sql.ResultSet;

import me.kingingo.khub.kHub;

public class Traitor {

	public static boolean Exist(String p){
		boolean b = false;
		try{
			
			ResultSet rs =kHub.mysql.Query("SELECT paesse FROM users_TTT WHERE player='" + p.toLowerCase() + "'");
			
			while(rs.next()){
				b=Boolean.valueOf(true);
			}
 			
			rs.close();
		}catch (Exception err){
			System.err.println(err);
		}
		
		if(!b){
			//CreateAccount(p);
		}
				
		return b;
	}
	
	public static Integer getTraitor(String p){
		int d = 0;
		
		try{
			
			ResultSet rs =kHub.mysql.Query("SELECT paesse FROM users_TTT WHERE player='" + p.toLowerCase() + "'");
			
			while(rs.next()){
				d = rs.getInt(1);
			}
 			
			rs.close();
		}catch (Exception err){	
			System.err.println(err);
		}
		
		return d;
	}
	
	public static void delTraitor(String p,Integer coins){
		int c = getTraitor(p);
		int co=c-coins;
		kHub.mysql.Update("UPDATE `users_TTT` SET paesse='"+co+"' WHERE player='"+p.toLowerCase()+"'");
	}
	
	public static void addTraitor(String p,Integer coins){
		int c = getTraitor(p);
		int co=c+coins;
		kHub.mysql.Update("UPDATE `users_TTT` SET paesse='"+co+"' WHERE player='"+p.toLowerCase()+"'");
	}
	
}
