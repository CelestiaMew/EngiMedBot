package blazebot;

import java.util.Stack;

public class User {
	public User(String name, boolean mod){
		this.name=name;
		isMod=mod;
		users.push(this);
	}
	private User(String name){
		this.name=name;
		isMod=false;
		isReal=false;
	}
	public final String name;
	public boolean isMod = false;
	public boolean linkPermitted = false;
	public boolean isReal = true;// this is default
	public long lastMsg = 0;
	public static User addUser(String nme)
	{
		for(int i=0;i<users.size();i++)
		{
			User user = users.get(i);
			if(user.name.equals(nme))
			{
				System.out.println("Duplicate user "+nme+" Skipping..");
				return null;
				
			}
		}
		System.out.println("Added user "+nme+" to active list");
		return new User(nme,false);
	}
	public static User getUser(String name)
	{
		for(int i=0;i<users.size();i++){
			User user = users.get(i);
			if(user.name.equalsIgnoreCase(name))
			{
				return user;
			}
		}
		
		return new User(name);
	}
	/**
	 * Used for removing users from the list (this is used to keep user count)
	 * @param nme : Name of user to remove
	 * @return 
	 */
	
	public static boolean removeUser(String nme){
		for(int i=0;i<users.size();i++){
			User user = users.get(i);
			if(user.name.equals(nme)){
				users.remove(user);
				return true;
			}
		}
		return false;
	}
	public static Stack<User> users = new Stack<User>();
}
