package blazebot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class User {
	public User(String name, boolean mod){
		this.name=name;
		isMod=mod;
		//users.push(this);
		hmusers.put(name,this);
	}
	private User(String name){
		this.name=name;
		isMod=false;
		isReal=false;
	}
	public static Stack<String[]> linkTimedOut;
	public final String name;
	public boolean isMod = false;
	public boolean linkPermitted = false;
	public boolean isReal = true;// this is default
	public long lastMsg = 0;
	public long lastCmd = 0;
	public static User addUser(String nme)
	{
		/*for(int i=0;i<users.size();i++)
		{
			User user = users.get(i);
			if(user.name.equals(nme))
			{
				System.out.println("Duplicate user "+nme+" Skipping..");
				return null;
				
			}
		}*/
		if(hmusers.containsKey(nme)){
			System.out.println("Duplicate user "+nme+" Skipping..");
			return null;
		}
		System.out.println("Added user "+nme+" to active list");
		return new User(nme,false);
	}
	public static User getUser(String name)
	{
		/*for(int i=0;i<users.size();i++){
			User user = users.get(i);
			if(user.name.equalsIgnoreCase(name))
			{
				return user;
			}
		}*/

		if(hmusers.containsKey(name.toLowerCase())){
			return hmusers.get(name.toLowerCase());
		}
		return new User(name.toLowerCase());
	}
	/**
	 * Used for removing users from the list (this is used to keep user count)
	 * @param nme : Name of user to remove
	 * @return 
	 */
	
	public static boolean removeUser(String nme){
		/*for(int i=0;i<users.size();i++){
			User user = users.get(i);
			if(user.name.equals(nme)){
				users.remove(user);
				return true;
			}
		}
		return false;*/
		if(hmusers.containsKey(nme)){
			hmusers.remove(nme);
			return true;
		}
		return false;
	}
	//public static Stack<User> users = new Stack<User>();
	public static HashMap<String,User> hmusers = new HashMap<>();
	public static ArrayList<String> nameExistTest = new ArrayList<String>();
}
