package blazebot.Parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Stack;

import javax.swing.JEditorPane;

import blazebot.CrashGUI;
import blazebot.Item;
import blazebot.ItemSearch;
import blazebot.BotMain;
import blazebot.Poll;
import blazebot.StackUtils;
import blazebot.User;

public class commandParser implements Runnable 
{
	static Stack<String[]> commands = new Stack<String[]>();
	public commandParser()
	{
		//BadURLS = StackUtils.loadBadURLs();
		new Thread(this).start();
	}
	public static long drawTime = 600000; // default
	public static Poll activePoll;
	public static long lastcmd=0;
	public static boolean blockLinks = true;
	public static User permitted=null;
	public static void save()throws IOException{
		StackUtils.saveSA("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/timed.cfg",User.linkTimedOut);
		Poll.savePolls("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/polls.cfg");
		StackUtils.saveSA("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/commands.cfg",commands);
		saveLinks();
		System.err.println("Saving commands");
	}
	static void saveLinks()
	{
		StackUtils.SaveString("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/blockLinks.cfg", String.valueOf(blockLinks)); //to block links
	}
	public static void load()throws IOException{
		User.linkTimedOut = StackUtils.loadSA("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/timed.cfg");
		Poll.loadPolls("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/polls.cfg");
		commands = StackUtils.loadSA("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/commands.cfg");
		blockLinks = Boolean.parseBoolean(StackUtils.LoadString("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/blockLinks.cfg"));
		System.err.println("Loading commands");
	}
	public static void parse(String inmsg) throws IOException
	{
		String sender = inmsg.substring(1, inmsg.indexOf("!"));
		User user = User.getUser(sender.toLowerCase());
		user.lastMsg = new Date().getTime();
		String message = inmsg.substring(inmsg.indexOf(":",1)+1).trim();
		System.out.println(sender+": "+message);
		if(!user.isMod&&activePoll==null)
			if(new Date().getTime()-user.lastCmd<10000||new Date().getTime()-lastcmd<5000)
				return;
		if(!message.startsWith("!")||user.name.equals(BotMain.name))
		{
			return;
		}
		user.lastCmd=new Date().getTime();
		lastcmd=new Date().getTime();
		String[] params = message.split(" ");
		
//		if(params[0].equalsIgnoreCase("!commands")&&user.isMod){
//			String str="Mod commands: !help !addcmd !removecmd !addpoll !removepoll !permit !ping !uptime !vote !endpoll",str2="User Commands:";
//			
//			for(int i=0;i<commands.size();i++){
//				String[] cmds=commands.get(i);
//				if(cmds[1].equals("mod")){
//					str+=" "+cmds[0];
//				}else{
//					str2+=" "+cmds[0];
//				}
//			}
//			str+=" Polls ";
//			for(int i=0;i<Poll.polls.size();i++){
//				str+=" "+Poll.polls.get(i).command;
//			}
//			BotMain.chatMsg(str);
//			BotMain.chatMsg(str2);
//		}
		/////////////BotMain Commands ////////////////// 
		if(params[0].equalsIgnoreCase("!draw")&&user.isMod){
			User drawUser;
			User[] chatUsers = new User[User.hmusers.size()];
			User.hmusers.values().toArray(chatUsers);
			if(params.length>1)
				try{drawTime = Integer.valueOf(params[1]) * 1000;}catch(Exception e){CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());}
			do{
				//drawUser = User.users.get((int)(Math.random()*((double)User.users.size())));
				drawUser = chatUsers[((int)(Math.random()*((double)chatUsers.length)))];
				/*if(User.users.size()<1)
				{
					break;
				}*/
				if(User.hmusers.size()<1)
				{
					break;
				}
			}while(!(drawUser.isReal&&new Date().getTime() - drawUser.lastMsg < drawTime&&!drawUser.name.equalsIgnoreCase("engimedbot")));
			BotMain.chatMsg("Random draw of chatters in the past " + drawTime/1000 + " seconds : " + drawUser.name);
		}
		if(params[0].equalsIgnoreCase("!permit")&&user.isMod)
		{
			permitted=User.getUser(params[1].replace("@",""));
			if(permitted.isReal)
			{
				permitted.linkPermitted = true;
				BotMain.chatMsg(permitted.name+" Permitted, post your link now");
			}
			else
			{
				BotMain.chatMsg(sender+", User not found");
			}
		}
		if (params[0].equalsIgnoreCase("!help")) 
	    {
			BotMain.chatMsg(sender+", To prevent spam, we moved !help to whispers. do \"/w engimedbot help\"");
	    }
		if (params[0].equalsIgnoreCase("!ping") && user.isMod) 
	    {
	    	BotMain.chatMsg("Pong");
	    }
		try{
			if(user.isMod)
			{
				if(params[0].equalsIgnoreCase("!whatis")||params[0].equalsIgnoreCase("!whatisinfo")){
					Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
					String info=BotMain.combine(item.info);
					BotMain.chatMsg(info.substring(0, info.length()>200?200:info.length())+(info.length()>200?" ... platinumgod.co.uk for more":""));
					return;
				}
				if(params[0].equalsIgnoreCase("!whatispools")){
					Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
					BotMain.chatMsg("Pools: "+BotMain.combine(item.pools));
					return;
				}
				if(params[0].equalsIgnoreCase("!whatispickup")){
					Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
					BotMain.chatMsg(item.pickup);
					return;
				}
				if(params[0].equalsIgnoreCase("!whatisid")){
					Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
					BotMain.chatMsg("ID: "+item.id);
					return;
				}
				if(params[0].equalsIgnoreCase("!whatisunlock")){
					Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
					BotMain.chatMsg(item.unlock);
					return;
				}
			}
		}catch(NullPointerException e){
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
			BotMain.chatMsg("Item not found");
			return;
		}
		/////////////////End//////////////////////
		for(int i=0;i<commands.size();i++)// loops through all stored commands
		{
			String[] curcmd = commands.get(i);
			if(params[0].equalsIgnoreCase(curcmd[0]))
			{
				if(!curcmd[1].equals("mod")||(curcmd[1].equals("mod")&&user.isMod))
					BotMain.chatMsg(curcmd[2]);
				return;
			}
		}
		if(user.isMod)
			for(int i=0;i<Poll.polls.size();i++)
			{
				Poll poll = Poll.polls.get(i);
				if(params[0].equals(poll.command))
				{
					activePoll=poll;
					poll.startPoll();
					return;
				}
			}
		if(params[0].equalsIgnoreCase("!follower") && user.isMod)
		{
			JEditorPane jep = new JEditorPane("https://api.twitch.tv/kraken/channels/recursiveblaze/follows?direction=DESC&limit=1&offset=0");
			String text = jep.getText();
			BotMain.chatMsg("The latest follower is "+ItemSearch.getContent(text, "\"display_name\":\"", "\",\""));
		}
		if(params[0].equalsIgnoreCase("!timeoutlink"))
		{
			long time = new Date().getTime();
			if(params[1].equalsIgnoreCase("help"))
			{
				BotMain.chatMsg("d = day, w = week, m = minute, nothing = hour, ex \"!timeoutlink example 1d\" will timeout for a day but \"!timeoutlink example 1\" will be for one hour");
			}
			try
			{
				if(params.length>=3)
				{
					if(params[2].contains("d"))
					{
						BotMain.chatMsg("Timing out "+params[1]+" for "+params[2].substring(0, params[2].length()-1)+" day(s)");
						time = time+Integer.parseInt(params[2].substring(0, params[2].length()-1))*86400000;
					}
					else if(params[2].contains("w"))
					{
						BotMain.chatMsg("Timing out "+params[1]+" for "+params[2].substring(0, params[2].length()-1)+" week(s)");
						time = time+Integer.parseInt(params[2].substring(0, params[2].length()-1))*86400000*7;
					}
					else if(params[2].contains("m"))
					{
						BotMain.chatMsg("Timing out "+params[1]+" for "+params[2].substring(0, params[2].length()-1)+" minute(s)");
						time = time+Integer.parseInt(params[2].substring(0, params[2].length()-1))*60000;
					}
					else
					{
						BotMain.chatMsg("Timing out "+params[1]+" for "+params[2].substring(0, params[2].length()-1)+" hour(s)");
						time = time+Integer.parseInt(params[2])*3600000;
					}
				}
			}
			catch(NumberFormatException e)
			{
				BotMain.chatMsg("not formated correctly, use !timeoutlink help for help");
			}
			String[] data = {User.getUser(params[1].replace("@","")).name, String.valueOf(time)};
			User.linkTimedOut.push(data);
		}
		if(params[0].equalsIgnoreCase("!uptime"))
		{
			String channelt=BotMain.channel.substring(1);
			if(params.length>1)
			{
				channelt = params[1];
			}
			URL MyURL = null;;
			try {
				MyURL = new URL("https://nightdev.com/hosted/uptime.php?channel="+channelt);
				BufferedReader in = new BufferedReader(new InputStreamReader(MyURL.openStream()));
				String inputLine = in.readLine();
				in.close();
				if(inputLine.equals("The channel is not live."))
				{
					BotMain.chatMsg("Sorry, "+channelt+" is not live");
				}
				else
				{
					BotMain.chatMsg(channelt+" has been live for "+inputLine);
				}
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				BotMain.chatMsg("Error");
			}
			
		}
		if(params[0].equalsIgnoreCase("!changevote")||params[0].equalsIgnoreCase("!vote")||params[0].equalsIgnoreCase("!pullout"))
				if(activePoll!=null&&user.isReal)
				{
					if(params[0].equalsIgnoreCase("!vote"))
					{
						if(params.length > 1)
						{
							if(!voted.contains(user))
							{
								if(Poll.vote(activePoll,user, BotMain.combine(Arrays.copyOfRange(params,1,params.length)))){
									total++;
									voted.push(user);
									counted+=1;
								}else{
									BotMain.chatMsg("That is an invalid option "+user.name);
								}
							}
							else
							{
								if(Poll.vote(activePoll,user, BotMain.combine(Arrays.copyOfRange(params,1,params.length)))){
									voted.push(user);
									counted+=1;
								}else{
									BotMain.chatMsg("That is an invalid option "+user.name);
								}
							}
						}
					}
					if(params[0].equalsIgnoreCase("!changevote"))
					{
						if(params.length > 1)
						{
							if(!voted.contains(user))
							{
								if(Poll.vote(activePoll,user, BotMain.combine(Arrays.copyOfRange(params,1,params.length)))){
									total++;
									voted.push(user);
									counted+=1;
								}else{
									BotMain.chatMsg("That is an invalid option "+user.name);
								}
							}
							else
							{
								if(Poll.vote(activePoll,user, BotMain.combine(Arrays.copyOfRange(params,1,params.length)))){
									voted.push(user);
									counted+=1;
								}else{
									BotMain.chatMsg("That is an invalid option "+user.name);
								}
							}
						}
					}
//					if(params[0].equalsIgnoreCase("!pullout"))
//					{
//						
//						//ShipPoll.removeVote(sender);
//					}
				}
				else
				{
					if(!user.isReal)
						BotMain.chatMsg(user.name + " Please wait until twitch registers you");
					else if(activePoll==null)
						BotMain.chatMsg(user.name + " There is no active poll");
				}
		if(params[0].equalsIgnoreCase("!endPoll"))
		{
			total = 0;
			activePoll.endPoll();
			voted=new Stack<User>();
			activePoll=null;
		}
	}
	static int delay=0;
	static int counted=0;
	static int total=0;
	static Stack<User> voted = new Stack<User>();
	public void run() 
	{
		while(true)
		{
			try 
			{
				if(delay>0&activePoll!=null)
				{
					delay-=1;
					
				}
				if(delay==0)
				{
					if(counted>0)
					{
						BotMain.chatMsg(counted + " Votes counted, "+total+" total");//oh yeh
					}
					counted=0;
					delay=50;
				}
				Thread.sleep(100);
			}
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
			}
		}
		
	}
}
