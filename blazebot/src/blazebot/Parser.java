package blazebot;

import java.awt.image.ImagingOpException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;//control shift o
import java.util.Stack;

import javax.swing.JEditorPane;

import org.json.JSONArray;
import org.json.JSONObject;

import blazebot.Poll.Option;

public class Parser implements Runnable
{
	static Stack<String[]> commands = new Stack<String[]>();
	Stack<String> BadURLS = new Stack<String>();
	public Parser()
	{
		//BadURLS = StackUtils.loadBadURLs();
		new Thread(this).start();
	}
	public static Poll activePoll;
	public long lastcmd=0;
	static boolean blockLinks = true;
	public static void whisperParser(String inmsg)
	{
		String sender = inmsg.substring(1, inmsg.indexOf("!"));
		User user = User.getUser(sender);
		String message = inmsg.substring(inmsg.indexOf(":",1)+1).trim();
		System.out.println(sender+": "+message);
		String[] params = message.split(" ");
		if(params[0].equalsIgnoreCase("help"))
		{
			if(params.length==2)
			{
				if(isMod(user))
				{
					switch(params[1].toLowerCase())
					{
						
						case "!uptime":Main.whisper("user access; Shows time stream has been active, use: !uptime [nameofstream]", user.name);return;
						case "!addcmd":Main.whisper("mod access; Adds new command, (: means mod only) use: !addcmd<:> !<command> <command text>", user.name);return;
						case "!removecmd":Main.whisper("mod access; Shows time stream has been active, use: !removecmd !<command>", user.name);return;
						case "!addpoll":Main.whisper("mod access; Adds a poll command, use: !addpoll !<pollcommand> <poll message>;<option 1>;[option2]...", user.name);return;
						case "!removepoll":Main.whisper("mod access; Removes a poll, use: !removepoll !<pollcommand>", user.name);return;
						case "!permit":Main.whisper("mod access; Permits a user for posting a link, use: !permit user (note: with BTTV you can click their name and there is a button for it)", user.name);return;
						case "!ping":Main.whisper("mod access; Returns pong (note: used for testing, is not in commands stack)", user.name);return;
						case "!vote":Main.whisper("user access; Votes on current poll, use: !vote <option>", user.name);return;
						case "!endpoll":Main.whisper("mod access; Ends and counts current poll, use: !endpoll", user.name);return;
						case "!help":Main.whisper("mod access; Shows help messages or with no parameter shows all commands, seriously..., use: !help, !help !<command>", user.name);return;
						case "!togglelink":Main.whisper("mod access; Toggles if the bot should timeout people who are not permitted to post links", user.name);return;
					}	
				}
				else
				{
					switch(params[1].toLowerCase())
					{
						
						case "!uptime":Main.whisper("Shows time stream has been active, use: !uptime [nameofstream]", user.name);return;
						case "!vote":Main.whisper("Votes on current poll, use: !vote <option>", user.name);return;
					}
				}
				for(int i=0;i<commands.size();i++)
				{
					String[] cmds=commands.get(i);
					if(cmds[0].equalsIgnoreCase(params[1]))
					{
						if(isMod(user))
						{
							Main.whisper(cmds[1]+" access; Command "+cmds[0]+" with message "+cmds[2], user.name);
						}
						if(!isMod(user) && !cmds[1].equals("mod"))
						{
							Main.whisper("Command "+cmds[0]+" with message "+cmds[2], user.name);
						}
						return;
					}
					
				}
				if(isMod(user))
				{
					for(int i=0;i<Poll.polls.size();i++)
					{
						Poll cmds=Poll.polls.get(i);
						if(cmds.command.equalsIgnoreCase(params[1]))
						{
							String strp="";
							for(Option o : cmds.options)
							{
								strp+=o.name+" ";
							}
							Main.whisper("mod access; Begins poll "+cmds.command+" with message "+cmds.message+" with options "+strp, user.name);
							return;
						}
					}
				}
				Main.whisper("Command not found", user.name);
			}
			else if(params.length==1)
			{
				String str = "";
				if(isMod(user))
				{
					str="Mod commands: !help !addcmd !removecmd !addpoll !removepoll !permit !ping !uptime !endpoll !togglelink";
					for(int i=0;i<commands.size();i++)
					{
						String[] cmds=commands.get(i);
						if(cmds[1].equals("mod"))
						{
							str+=" "+cmds[0];
						}
					}
					str+=" User Commands: ";
					for(int i=0;i<commands.size();i++)
					{
						String[] cmds=commands.get(i);
						if(cmds[1].equals("user"))
						{
							str+=" "+cmds[0];
						}
					}
					str+=" Polls: ";
					for(int i=0;i<Poll.polls.size();i++)
					{
						str+=" "+Poll.polls.get(i).command;
					}
				}
				else
				{
					str="Commands: !vote";
					for(int i=0;i<commands.size();i++)
					{
						String[] cmds=commands.get(i);
						if(!cmds[1].equals("mod"))
						{
							str+=" "+cmds[0];
						}
					}
				}
				Main.whisper(str, user.name);
			}
			return;
		}
		try
		{
			if(params[0].equalsIgnoreCase("whatis")||params[0].equalsIgnoreCase("whatisinfo")){
				Item item = Main.searcher.searchFor(Main.combine(Arrays.copyOfRange(params,1,params.length)));
				String info=Main.combine(item.info);
				Main.whisper(info, user.name);
				return;
			}
			if(params[0].equalsIgnoreCase("whatispools")){
				Item item = Main.searcher.searchFor(Main.combine(Arrays.copyOfRange(params,1,params.length)));
				Main.whisper("Pools: "+Main.combine(item.pools), user.name);
				return;
			}
			if(params[0].equalsIgnoreCase("whatispickup")){
				Item item = Main.searcher.searchFor(Main.combine(Arrays.copyOfRange(params,1,params.length)));
				Main.whisper(item.pickup, user.name);
				return;
			}
			if(params[0].equalsIgnoreCase("!whatisid")){
				Item item = Main.searcher.searchFor(Main.combine(Arrays.copyOfRange(params,1,params.length)));
				Main.whisper("ID: "+item.id, user.name);
				return;
			}
			if(params[0].equalsIgnoreCase("whatisunlock")){
				Item item = Main.searcher.searchFor(Main.combine(Arrays.copyOfRange(params,1,params.length)));
				Main.whisper(item.unlock, user.name);
				return;
			}
		}
		catch(Exception e)
		{
			
		}
	}
	public void commandParser(String inmsg) throws IOException, NoSuchMethodException, SecurityException
	{
		String sender = inmsg.substring(1, inmsg.indexOf("!"));
		User user = User.getUser(sender);
		user.lastMsg = new Date().getTime();
		String message = inmsg.substring(inmsg.indexOf(":",1)+1).trim();
		System.out.println(sender+": "+message);
		if(!user.isMod&&activePoll==null)
			if(new Date().getTime()-user.lastCmd<10000||new Date().getTime()-lastcmd<5000)
				return;
		if(!message.startsWith("!")||user.name.equals(Main.name))
		{
			return;
		}
		user.lastCmd=new Date().getTime();
		lastcmd=new Date().getTime();
		String[] params = message.split(" ");
		if(params[0].startsWith("!addcmd")&&isMod(user))
		{
			String alevel="user";
			if(params[0].contains(":"))
			{
				alevel="mod";
			}
			try{
				String[] newcmd={params[1],alevel,Main.combine(Arrays.copyOfRange(params, 2, params.length))};
				commands.push(newcmd);
				Main.chatMsg(".me Added command "+newcmd[0]+" to do '"+newcmd[2]+"' at "+newcmd[1]+" access level");
			}catch(Exception e){
				Main.chatMsg(".me Error: "+e.getMessage());
				e.printStackTrace();
			}
			return;
		}
		if(params[0].equalsIgnoreCase("!removecmd")&&isMod(user)){
			for(int i=0;i<commands.size();i++){
				String[] curcmd = commands.get(i);
				if(params[1].equals(curcmd[0])){
					commands.remove(i);
					Main.chatMsg("Removed command: "+params[1]);
					return;
				}
			}
			Main.chatMsg("Unable to find command "+params[1]);
			return;
		}
		if(params[0].equalsIgnoreCase("!addpoll")&&isMod(user)){
			String msgp2=Main.combine(Arrays.copyOfRange(params,2,params.length));
			System.out.println(msgp2);
			String[] options = Main.combine(Arrays.copyOfRange(msgp2.split(";"),1,msgp2.split(";").length),";").trim().split(";");
			new Poll(params[1],msgp2.split(";")[0],options);
			Main.chatMsg("Added Poll: "+params[1]);
		}
		if(params[0].equalsIgnoreCase("!save")&&isMod(user))
		{
			Main.chatMsg("Saving...");
			save();
		}
		if(params[0].equalsIgnoreCase("!removepoll")&&isMod(user)){
			String cmdtoremove=params[1];
			for(Poll poll : Poll.polls){
				if(poll.command.equalsIgnoreCase(cmdtoremove))
				{
					Main.chatMsg("Removed Poll "+cmdtoremove);
					Poll.polls.remove(poll);
					break;
				}
			}
		}
		
//		if(params[0].equalsIgnoreCase("!commands")&&isMod(user)){
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
//			Main.chatMsg(str);
//			Main.chatMsg(str2);
//		}
		/////////////Main Commands ////////////////// 
		if(params[0].equalsIgnoreCase("!draw")&&isMod(user)){
			User drawUser;
			long drawTime = 600000; // default
			if(params.length>1)
				try{drawTime = Integer.valueOf(params[1]) * 1000;}catch(Exception e){e.printStackTrace();}
			do{
				drawUser = User.users.get((int)(Math.random()*((double)User.users.size())));
				if(User.users.size()<1)
				{
					break;
				}
			}while(!(drawUser.isReal&&new Date().getTime() - drawUser.lastMsg < drawTime&&!drawUser.name.equalsIgnoreCase("engimedbot")));
			Main.chatMsg("Random draw of chatters in the past " + drawTime/1000 + " seconds : " + drawUser.name);
		}
		if(params[0].equalsIgnoreCase("!togglelink")&&isMod(user))
		{
			blockLinks = !blockLinks;
			if(blockLinks)
			{
				Main.chatMsg("I block links now");
			}
			else
			{
				Main.chatMsg("I dont block links now");
			}
			saveLinks();
		}
		if(params[0].equalsIgnoreCase("!permit")&&isMod(user))
		{
			permitted=User.getUser(params[1].replace("@",""));
			if(permitted.isReal)
			{
				permitted.linkPermitted = true;
				Main.chatMsg(permitted.name+" Permitted, post your link now");
			}
			else
			{
				Main.chatMsg(sender+", User not found");
			}
		}
		if (params[0].equalsIgnoreCase("!ping") && isMod(user)) 
	    {
	    	Main.chatMsg("Pong");
	    }
		try{
			if(isMod(user))
			{
				if(params[0].equalsIgnoreCase("!whatis")||params[0].equalsIgnoreCase("!whatisinfo")){
					Item item = Main.searcher.searchFor(Main.combine(Arrays.copyOfRange(params,1,params.length)));
					String info=Main.combine(item.info);
					Main.chatMsg(info.substring(0, info.length()>200?200:info.length())+(info.length()>200?" ... platinumgod.co.uk for more":""));
					return;
				}
				if(params[0].equalsIgnoreCase("!whatispools")){
					Item item = Main.searcher.searchFor(Main.combine(Arrays.copyOfRange(params,1,params.length)));
					Main.chatMsg("Pools: "+Main.combine(item.pools));
					return;
				}
				if(params[0].equalsIgnoreCase("!whatispickup")){
					Item item = Main.searcher.searchFor(Main.combine(Arrays.copyOfRange(params,1,params.length)));
					Main.chatMsg(item.pickup);
					return;
				}
				if(params[0].equalsIgnoreCase("!whatisid")){
					Item item = Main.searcher.searchFor(Main.combine(Arrays.copyOfRange(params,1,params.length)));
					Main.chatMsg("ID: "+item.id);
					return;
				}
				if(params[0].equalsIgnoreCase("!whatisunlock")){
					Item item = Main.searcher.searchFor(Main.combine(Arrays.copyOfRange(params,1,params.length)));
					Main.chatMsg(item.unlock);
					return;
				}
			}
		}catch(NullPointerException e){
			e.printStackTrace();
			Main.chatMsg("Item not found");
			return;
		}
		/////////////////End//////////////////////
		for(int i=0;i<commands.size();i++)// loops through all stored commands
		{
			String[] curcmd = commands.get(i);
			if(params[0].equalsIgnoreCase(curcmd[0]))
			{
				if(!curcmd[1].equals("mod")||(curcmd[1].equals("mod")&&isMod(user)))
					Main.chatMsg(curcmd[2]);
				return;
			}
		}
		if(isMod(user))
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
		if(params[0].equalsIgnoreCase("!follower") && isMod(user))
		{
			JEditorPane jep = new JEditorPane("https://api.twitch.tv/kraken/channels/recursiveblaze/follows?direction=DESC&limit=1&offset=0");
			String text = jep.getText();
			Main.chatMsg("The latest follower is "+ItemSearch.getContent(text, "\"display_name\":\"", "\",\""));
		}
		if(params[0].equalsIgnoreCase("!timeoutlink"))
		{
			long time = new Date().getTime();
			if(params[1].equalsIgnoreCase("help"))
			{
				Main.chatMsg("d = day, w = week, m = minute, nothing = hour, ex \"!timeoutlink example 1d\" will timeout for a day but \"!timeoutlink example 1\" will be for one hour");
			}
			try
			{
				if(params.length>=3)
				{
					if(params[2].contains("d"))
					{
						Main.chatMsg("Timing out "+params[1]+" for "+params[2].substring(0, params[2].length()-1)+" day(s)");
						time = time+Integer.parseInt(params[2].substring(0, params[2].length()-1))*86400000;
					}
					else if(params[2].contains("w"))
					{
						Main.chatMsg("Timing out "+params[1]+" for "+params[2].substring(0, params[2].length()-1)+" week(s)");
						time = time+Integer.parseInt(params[2].substring(0, params[2].length()-1))*86400000*7;
					}
					else if(params[2].contains("m"))
					{
						Main.chatMsg("Timing out "+params[1]+" for "+params[2].substring(0, params[2].length()-1)+" minute(s)");
						time = time+Integer.parseInt(params[2].substring(0, params[2].length()-1))*60000;
					}
					else
					{
						Main.chatMsg("Timing out "+params[1]+" for "+params[2].substring(0, params[2].length()-1)+" hour(s)");
						time = time+Integer.parseInt(params[2])*3600000;
					}
				}
			}
			catch(NumberFormatException e)
			{
				Main.chatMsg("not formated correctly, use !timeoutlink help for help");
			}
			String[] data = {User.getUser(params[1].replace("@","")).name, String.valueOf(time)};
			User.linkTimedOut.push(data);
		}
		if(params[0].equalsIgnoreCase("!uptime"))
		{
			String channelt=Main.channel.substring(1);
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
					Main.chatMsg("Sorry, "+channelt+" is not live");
				}
				else
				{
					Main.chatMsg(channelt+" has been live for "+inputLine);
				}
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				Main.chatMsg("Error");
			}
			
		}
		if(params[0].equalsIgnoreCase("!users")&&isMod(user))
		{
			String string = "Current Users: ";
			for(int i = 0; i<User.users.size(); i++)
			{
				if(!User.users.get(i).isMod)
				{
					string += User.users.get(i).name+", ";
				}
			}
			string += "Current Mods: ";
			for(int i = 0; i<User.users.size(); i++)
			{
				if(User.users.get(i).isMod)
				{
					string += User.users.get(i).name+", ";
				}
			}
			Main.chatMsg(string);
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
								if(Poll.vote(activePoll,user, Main.combine(Arrays.copyOfRange(params,1,params.length)))){
									total++;
									voted.push(user);
									counted+=1;
								}else{
									Main.chatMsg("That is an invalid option "+user.name);
								}
							}
							else
							{
								if(Poll.vote(activePoll,user, Main.combine(Arrays.copyOfRange(params,1,params.length)))){
									voted.push(user);
									counted+=1;
								}else{
									Main.chatMsg("That is an invalid option "+user.name);
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
								if(Poll.vote(activePoll,user, Main.combine(Arrays.copyOfRange(params,1,params.length)))){
									total++;
									voted.push(user);
									counted+=1;
								}else{
									Main.chatMsg("That is an invalid option "+user.name);
								}
							}
							else
							{
								if(Poll.vote(activePoll,user, Main.combine(Arrays.copyOfRange(params,1,params.length)))){
									voted.push(user);
									counted+=1;
								}else{
									Main.chatMsg("That is an invalid option "+user.name);
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
						Main.chatMsg(user.name + " Please wait until twitch registers you");
					else if(activePoll==null)
						Main.chatMsg(user.name + " There is no active poll");
				}
		if(params[0].equalsIgnoreCase("!endPoll"))
		{
			total = 0;
			activePoll.endPoll();
			voted=new Stack<User>();
			activePoll=null;
		}
	}
	int delay=0;
	int counted=0;
	int total=0;
	Stack<User> voted = new Stack<User>();
	public void run() {
		while(true){
			try {
				if(delay>0&activePoll!=null){
					delay-=1;
					
				}
				if(delay==0){
					if(counted>0)
						Main.chatMsg(counted + " Votes counted, "+total+" total");//oh yeh
					counted=0;
					delay=50;
				}
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public void modParser(String inmsg)
	{
		String[] params = inmsg.split(" ");
		if(params[3].equals("+o")){
			User.getUser(params[4]).isMod=true;
			System.out.println("Promoted "+params[4]);//i have never seen one but it probably has the same +o tag
		}
	}
	public static void userParser()
	{
		try
		{
			JEditorPane jep = new JEditorPane("http://tmi.twitch.tv/group/user/"+Main.channel.substring(1)+"/chatters");
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
			User.users = new Stack<User>();
			User.users.addAll(users);
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
			e.printStackTrace();
		}
	}
	User permitted=null;
	String[] protocols = new String[]{"http://","https://"};
	public void checkShortened(String inmsg, User user) throws IOException
	{
		boolean shortened = false;
//		for(int i = 0; i<BadURLS.size(); i++)
//		{
//			if(inmsg.contains(BadURLS.get(i)))
//			{
//				shortened = true;
//			}
//		}
		String message = inmsg.substring(inmsg.indexOf(":",1)+1).trim();
		String[] words = message.split(" ");
		boolean permit=user.equals(permitted);
		if((!isMod(user) || permit) && blockLinks)
		{
			for(int i=0;i<words.length;i++){
				char[] chars = words[i].toCharArray();
				int count=0;
				try{
					for(int ii=0;ii<chars.length;ii++)
						if(".".equals(""+chars[ii]))
							if(Character.isAlphabetic(chars[ii-2])&&Character.isAlphabetic(chars[ii-1])&&Character.isAlphabetic(chars[ii+2])&&Character.isAlphabetic(chars[ii+1]))
								count++;
					if(count>0&&count<4){
						if(count==1){
							String word=words[i];
							
							if(word.contains("."))
								try{
									
									new JEditorPane((word.contains("://") ? "" : "http://") +word);
									
									shortened=true;
								}catch(MalformedURLException e){
									
								}catch(UnknownHostException e){
									
								}catch(IOException e){
									shortened=true;
								}
						}else
							shortened=true;
						if(count>1&&words[i].indexOf('/',words[i].indexOf('.'))>=0)
							shortened=true;
						break;
					}
				}catch(IndexOutOfBoundsException e){
					//e.printStackTrace();
				}
			}
			boolean timed = false;
			for(int i=0; i<User.linkTimedOut.size(); i++)
			{
				if(User.linkTimedOut.get(i)[0].equals(user.name))
				{
					timed = true;
				}
			}
			if(permit)
			{
				permitted=null;
				return;
			}
			if(shortened)
			{
				if(permit)
				{
					permitted=null;
					return;
				}
				if(timed)
				{
					Main.chatMsg(user.name+" you are timed out from posting links");
				}
				else
				{
					Main.chatMsg(user.name+" Links are disallowed, ask for permission from a mod");
				}
		    	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	Main.chatMsg(".timeout "+user.name.trim()+" 1");
			}
		}
	}
	public static void save()throws IOException{
		StackUtils.saveSA("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/timed.cfg",User.linkTimedOut);
		Poll.savePolls("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/polls.cfg");
		StackUtils.saveSA("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/commands.cfg",commands);
		saveLinks();
		System.err.println("Saving commands");
	}
	static void saveLinks()
	{
		StackUtils.SaveString("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/blockLinks.cfg", String.valueOf(blockLinks));
	}
	public static void load()throws IOException{
		User.linkTimedOut = StackUtils.loadSA("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/timed.cfg");
		Poll.loadPolls("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/polls.cfg");
		commands = StackUtils.loadSA("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/commands.cfg");
		blockLinks = Boolean.parseBoolean(StackUtils.LoadString("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/blockLinks.cfg"));
		System.err.println("Loading commands");
	}
	public static boolean isMod(User user)
	{
		return user.isMod;
	}
	public static void updateTimeouts()
	{
		for(int i=0; i<User.linkTimedOut.size(); i++)
		{
			if(Long.parseLong(User.linkTimedOut.get(i)[1])<new Date().getTime())
			{
				for(int ii=0; ii<User.users.size(); ii++)
				{
					if(User.users.get(ii).name.equalsIgnoreCase(User.linkTimedOut.get(i)[0]))
					{
						Main.chatMsg(User.linkTimedOut.get(i)[0]+", Your link timeout has been lifted");
					}
				}
				System.out.println("Removing "+User.linkTimedOut.get(i)[0]+" from timeout list");
				User.linkTimedOut.remove(i);
			}
		}
	}
}
