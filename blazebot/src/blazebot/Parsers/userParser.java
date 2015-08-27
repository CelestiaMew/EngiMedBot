package blazebot.Parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JEditorPane;

import org.json.JSONArray;
import org.json.JSONObject;

import blazebot.CrashGUI;
import blazebot.BotMain;
import blazebot.User;

public class userParser 
{
	public static synchronized void parse()
	{
		/*try
		{
			JEditorPane jep = new JEditorPane("http://tmi.twitch.tv/group/user/"+BotMain.channel.substring(1)+"/chatters");
			JSONObject json = new JSONObject(jep.getText());
			JSONObject chatters = json.getJSONObject("chatters");
			JSONArray mods = chatters.getJSONArray("moderators");
			JSONArray viewers = chatters.getJSONArray("viewers");
			Stack<User> users = new Stack<User>();
			for(int i = 0; i<viewers.length(); i++)
			{
				users.push(new User(viewers.getString(i),false));
			}
			for(int i = 0; i<mods.length(); i++)
			{
				users.push(new User(mods.getString(i),true));
			}
			User.users = users;
//			for(int i = 0; i<User.users.size(); i++)
//			{
//				viewersStored.add(User.users.get(i).name);
//			}
//			for(int i = 0; i<viewers.length(); i++)
//			{
//				viewersS.push(viewers.getString(i));
//			}
//			viewersS.removeAll(viewersStored);
//			for(int i = 0; i<viewersS.size(); i++)
//			{
//				User.users.push(new User(viewersS.get(i),false));
//			}
//			for(int i = 0; i<User.users.size(); i++)
//			{
//				viewersStored.add(User.users.get(i).name);
//			}
//			for(int i = 0; i<viewers.length(); i++)
//			{
//				
//			}
			
		}
		catch(IOException e)
		{
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}*/
		////////////////////////////////////////////////////////////////////////////////////
		try
		{
			JEditorPane jep = new JEditorPane("http://tmi.twitch.tv/group/user/"+BotMain.channel.substring(1)+"/chatters");
			JSONObject json = new JSONObject(jep.getText());
			JSONObject chatters = json.getJSONObject("chatters");
			JSONArray mods = chatters.getJSONArray("moderators");
			JSONArray staff = chatters.getJSONArray("staff");
			JSONArray admins = chatters.getJSONArray("admins");
			JSONArray gMods = chatters.getJSONArray("global_mods");
			JSONArray viewers = chatters.getJSONArray("viewers");
			//Stack<User> users = new Stack<User>();
			Stack<String> currentExistTest = new Stack<String>();
			for(int i = 0; i<viewers.length(); i++)
			{
				currentExistTest.add(viewers.getString(i).toLowerCase());
				if(!(User.hmusers.containsKey(viewers.getString(i).toLowerCase())))
					User.hmusers.put(viewers.getString(i).toLowerCase(),new User(viewers.getString(i),false));
				//users.push(new User(viewers.getString(i),false));
			}
			BotMain.Mconsole.clear();
			BotMain.Mconsole.print("Current Mods");
			for(int i = 0; i<mods.length(); i++)
			{
				currentExistTest.add(mods.getString(i).toLowerCase());
				BotMain.Mconsole.print(mods.getString(i).toLowerCase());
				if(!(User.hmusers.containsKey(mods.getString(i).toLowerCase())))
				{
					User.hmusers.put(mods.getString(i).toLowerCase(),new User(mods.getString(i),true));
				}
				//users.push(new User(mods.getString(i),true));
			}
			for(int i = 0; i<staff.length(); i++)
			{
				currentExistTest.add(staff.getString(i).toLowerCase());
				BotMain.Mconsole.print(staff.getString(i).toLowerCase());
				if(!(User.hmusers.containsKey(staff.getString(i).toLowerCase())))
				{
					User.hmusers.put(staff.getString(i).toLowerCase(),new User(staff.getString(i),true));
				}
				//users.push(new User(staff.getString(i),true));
			}
			for(int i = 0; i<admins.length(); i++)
			{
				currentExistTest.add(admins.getString(i).toLowerCase());
				BotMain.Mconsole.print(admins.getString(i).toLowerCase());
				if(!(User.hmusers.containsKey(admins.getString(i).toLowerCase())))
				{
					User.hmusers.put(admins.getString(i).toLowerCase(),new User(admins.getString(i),true));
				}
				//users.push(new User(admins.getString(i),true));
			}
			for(int i = 0; i<gMods.length(); i++)
			{
				currentExistTest.add(gMods.getString(i).toLowerCase());
				BotMain.Mconsole.print(gMods.getString(i).toLowerCase());
				if(!(User.hmusers.containsKey(gMods.getString(i).toLowerCase())))
				{
					User.hmusers.put(gMods.getString(i).toLowerCase(),new User(gMods.getString(i),true));
				}
				//users.push(new User(gMods.getString(i),true));
			}
			boolean nameFound = false;
			for(int k=0;k<User.nameExistTest.size();k++){//removes users that don't exist in chat next check
				for(int i=0;i<currentExistTest.size();i++){
					if(!nameFound&&User.nameExistTest.get(k).equals(currentExistTest.get(i))){
						nameFound = true;
						User.nameExistTest.remove(k);
						k--;
					}
				}
				nameFound = false;
			}
			for(int k=0;k<User.nameExistTest.size();k++){
				if(User.hmusers.containsKey(User.nameExistTest.get(k))){
					User.hmusers.remove(User.nameExistTest.get(k));
					User.nameExistTest.remove(k);
					k--;
				}
				else
					System.out.println("internal error removing name '" + User.nameExistTest.get(k) + "' from user map");//DEBUG
			}
			if(!(User.nameExistTest.size()==0))
				System.out.println("internal error 'nameExistTest' was not completely cleared. data: " + User.nameExistTest);//DEBUG
			User.nameExistTest.clear();//DEBUG RESOLUTION, note if previous line is true there is a problem with removing users from map
			for(int k=0;k<currentExistTest.size();k++)
				User.nameExistTest.add(currentExistTest.get(k));
		}
		catch(IOException e)
		{
			//CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
	}
	public static class ParerTimer implements Runnable
	{
		@Override
		public void run() 
		{
			while(true)
			{
				try
				{
					parse();
					//BotMain.console.print("Getting Users");
					try {
						Thread.sleep(60*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				catch(Exception e)
				{
					CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
				}
			}
		}
	}
}
