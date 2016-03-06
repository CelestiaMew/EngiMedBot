package blazebot.Parsers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import javax.swing.JEditorPane;

import jdk.nashorn.internal.ir.LiteralNode.ArrayLiteralNode.ArrayUnit;

import org.json.JSONException;
import org.json.JSONObject;

import blazebot.CrashGUI;
import blazebot.FTLItemSearch;
import blazebot.Item;
import blazebot.BotMain;
import blazebot.Poll;
import blazebot.StackUtils;
import blazebot.User;
import blazebot.Poll.Option;

public class whisperParser 
{
	@SuppressWarnings("unused")
	public static void parse(String inmsg) throws IOException
	{
		String sender = inmsg.substring(1, inmsg.indexOf("!"));
		User user = User.getUser(sender.toLowerCase());
		String message = inmsg.substring(inmsg.indexOf(":",1)+1).trim();
		System.out.println(sender+": "+message);
		String[] params = message.split(" ");
		Thread QuoteTimer = null;
		if(params[0].equalsIgnoreCase("help"))
		{
			if(params.length==2)
			{
				if(user.isMod)
				{
					switch(params[1].toLowerCase())
					{	
						case "!uptime":BotMain.whisper("user access; Shows time stream has been active, use: !uptime [nameofstream]", user.name);return;
						case "!timeoutlink":BotMain.whisper("mod access; times out a user from posting links, use: !timeoutlink <user>", user.name);return;
						case "!follower":BotMain.whisper("mod access; Shows the lastest follower, use: !follower", user.name);return;
						case "!draw":BotMain.whisper("mod access; Draws a random chatter that has chatted in the last "+commandParser.drawTime+", use: !draw", user.name);return;
						case "addcmd":BotMain.whisper("mod access; Adds new command, (: means mod only) use: !addcmd<:> !<command> <command text>", user.name);return;
						case "removecmd":BotMain.whisper("mod access; Shows time stream has been active, use: !removecmd !<command>", user.name);return;
						case "addpoll":BotMain.whisper("mod access; Adds a poll command, use: !addpoll !<pollcommand> <poll message>;<option 1>;[option2]...", user.name);return;
						case "removepoll":BotMain.whisper("mod access; Removes a poll, use: !removepoll !<pollcommand>", user.name);return;
						case "!permit":BotMain.whisper("mod access; Permits a user for posting a link, use: !permit user (note: with BTTV you can click their name and there is a button for it)", user.name);return;
						case "!ping":BotMain.whisper("mod access; Returns pong (note: used for testing, is not in commands stack)", user.name);return;
						case "ping":BotMain.whisper("mod access; Returns pong (note: used for testing, is not in commands stack)", user.name);return;
						case "!vote":BotMain.whisper("user access; Votes on current poll, use: !vote <option>", user.name);return;
						case "!endpoll":BotMain.whisper("mod access; Ends and counts current poll, use: !endpoll", user.name);return;
						case "help":BotMain.whisper("user access; Shows help messages or with no parameter shows all commands, seriously..., use: help, help !<command>", user.name);return;
						case "togglelink":BotMain.whisper("mod access; Toggles if the bot should timeout people who are not permitted to post links, use: togglelink", user.name);return;
						case "reloadblocked":BotMain.whisper("mod access; reloads blocked links, use: reloadblocked", user.name);return;
						case "accept":BotMain.whisper("mod access; accepts your current request, use: accept", user.name);return;
						case "decline":BotMain.whisper("mod access; declines your current request, use: decline", user.name);return;
						case "requestquote":BotMain.whisper("user access; requests to add a quote, any mod can decline or accept the new quote, use: requestquote <this is an example quote>", user.name);return;
						case "delquote":BotMain.whisper("mod access; delets a quote, you must accept or decline this action, use: delquote <number>", user.name);return;
						case "setracers":BotMain.whisper("mod access; sets the racers in the !split command, returns a example message, use: setracers [racer 1] [racer 2]...", user.name);return;
						case "detectr":BotMain.whisper("mod access; tries to automaticly detect the url in the title; use only when the title is the link for the multisteam, use: detectr", user.name);return;
						case "!split":BotMain.whisper("mod access; announces the way to watch both streams; set racers woth the \"setracers\" command, use: !split", user.name);return;
						case "banQuote":BotMain.whisper("mod access; bans a person from requesting to add quotes, use: banQuote <person>", user.name);return;
						case "pardonQuote":BotMain.whisper("mod access; pardons a person from requesting to add quotes, use: pardonQuote <person>", user.name);return;
					}	
				}
				else
				{
					switch(params[1].toLowerCase())
					{
						
						case "!uptime":BotMain.whisper("Shows time stream has been active, use: !uptime [nameofstream]", user.name);return;
						case "!vote":BotMain.whisper("Votes on current poll, use: !vote <option>", user.name);return;
						case "help":BotMain.whisper("mod access; Shows help messages or with no parameter shows all commands, seriously..., use: help, help !<command>", user.name);return;
						case "requestquote":BotMain.whisper("requests to add a quote, any mod can decline or accept the new quote, use: requestquote <this is an example quote>", user.name);return;
					}
				}
				for(int i=0;i<commandParser.commands.size();i++)
				{
					String[] cmds=commandParser.commands.get(i);
					if(cmds[0].equalsIgnoreCase(params[1]))
					{
						if(user.isMod)
						{
							BotMain.whisper(cmds[1]+" access; Command "+cmds[0]+" with message "+cmds[2], user.name);
						}
						if(!user.isMod && !cmds[1].equals("mod"))
						{
							BotMain.whisper("Command "+cmds[0]+" with message "+cmds[2], user.name);
						}
						return;
					}
					
				}
				if(user.isMod)
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
							BotMain.whisper("mod access; Begins poll "+cmds.command+" with message "+cmds.message+" with options "+strp, user.name);
							return;
						}
					}
				}
				BotMain.whisper("Command not found", user.name);
			}
			else if(params.length==1)
			{
				String str = "";
				BotMain.whisper("If the command has a ! before it, it is a normal command, if it dosent then its a whisper command", user.name);
				if(user.isMod)
				{
					str="Mod commands: !follower !timeoutlink !draw help addcmd removecmd addpoll removepoll !permit !ping ping !uptime !endpoll togglelink !whatis !whatispools !whatispickup !whatisid !whatisunlock accept decline delquote setracers detectr !split";
					for(int i=0;i<commandParser.commands.size();i++)
					{
						String[] cmds=commandParser.commands.get(i);
						if(cmds[1].equals("mod"))
						{
							str+=" "+cmds[0];
						}
					}
					str+=" User Commands: !vote whatis whatispools whatispickup whatisid whatisunlock requestquote !quote";
					for(int i=0;i<commandParser.commands.size();i++)
					{
						String[] cmds=commandParser.commands.get(i);
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
					str="Commands: !vote !quote whatis whatispools whatispickup whatisid whatisunlock requestquote";
					for(int i=0;i<commandParser.commands.size();i++)
					{
						String[] cmds=commandParser.commands.get(i);
						if(!cmds[1].equals("mod"))
						{
							str+=" "+cmds[0];
						}
					}
				}
				BotMain.whisper(str, user.name);
			}
			return;
		}
		try
		{
			String info = "";
			if(params[0].equalsIgnoreCase("whatis")||params[0].equalsIgnoreCase("whatisinfo")){
				Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
				if(!(item == null))
				{
					info=BotMain.combine(item.info);
				}
				Stack<String> information = FTLItemSearch.search(Arrays.copyOfRange(params, 1, params.length));
				if(!information.isEmpty())
				{
					whisperStack(information, user);
				}
			}
			if(params[0].equalsIgnoreCase("whatispools")){
				Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
				info="Pools: "+BotMain.combine(item.pools);
			}
			if(params[0].equalsIgnoreCase("whatispickup")){
				Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
				info=item.pickup;
			}
			if(params[0].equalsIgnoreCase("whatisid")){
				Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
				info="ID: "+item.id;
			}
			if(params[0].equalsIgnoreCase("whatisunlock")){
				Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
				info=item.unlock;
			}
			if(!info.equals(""))
			{
				whisperRecursive(info, user);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(params[0].equalsIgnoreCase("delquote")&&user.isMod)
		{
			try
			{
				int quotenum = -1;
				try
				{
					quotenum = Integer.parseInt(params[1]);
					BotMain.whisper("Do you confirm to delete: "+commandParser.quotes.get(quotenum-1),user.name);
					commandParser.delQuote[0] = user.name;
					commandParser.delQuote[1] = String.valueOf(quotenum);
				}
				catch(NumberFormatException e)
				{
					String[] keys = Arrays.copyOfRange(params,1,params.length);
					int[] best = new int[2];
					for(int i = 0; i < keys.length; i++)
					{
						keys[i] = keys[i].toLowerCase();
					}
					for(int i = 0; i < commandParser.quotes.size(); i++)
					{
						int score = 0;
						String[] words = commandParser.quotes.get(i).split(" ");
						for(int ii = 0; ii < words.length; ii++)
						{
							if(Arrays.asList(keys).contains(words[ii].replace(",", "").replace("\"", "").toLowerCase()))
							{
								score++;
							}
						}
						if(score>best[0])
						{
							best[0] = score;
							best[1] = i;
						}
					}
					if(best[0]==0)
					{
						BotMain.whisper("Not Found",user.name);
					}
					else
					{
						BotMain.whisper(commandParser.quotes.get(best[1]),user.name);
					}
				}
			}
			catch(Exception e)
			{
				BotMain.chatMsg("Error");
			}
		}
		if(params[0].toLowerCase().startsWith("addblocked"))
		{
			BotMain.BadURLS.add(BotMain.combine(Arrays.copyOfRange(params,1,params.length)).trim());
			StackUtils.saveS("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/bannedLinks.cfg", BotMain.BadURLS);
		}
		if(params[0].toLowerCase().startsWith("requestquote"))
		{
			boolean good = true;
			long time = 0;
//			for(int i = 0; i < commandParser.quoteCooldown.size(); i++)
//			{
//				if(Long.parseLong(commandParser.quoteCooldown.get(i)[1])>new Date().getTime() && user.name.equals(commandParser.quoteCooldown.get(i)[0]))
//				{
//					good = false;
//					time = Long.parseLong(commandParser.quoteCooldown.get(i)[1]);
//				}
//				if(Long.parseLong(commandParser.quoteCooldown.get(i)[1])<new Date().getTime())
//				{
//					commandParser.quoteCooldown.remove(i);
//				}
//			}
			for(int i = 0; i < commandParser.blacklistedRequesters.size(); i++)
			{
				if(user.name.equals(commandParser.blacklistedRequesters.get(i)))
				{
					good = false;
				}
			}
			if(good)
			{
				String game = "Nothing";
				try{game = new JSONObject(new JEditorPane("https://api.twitch.tv/kraken/streams/recursiveblaze").getText()).getJSONObject("stream").getString("game");}catch(Exception e){}
				SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yyyy");
				if(BotMain.combine(Arrays.copyOfRange(params,1,params.length)).equals(""))
				{
					BotMain.whisper("you need to add the quote to the command", user.name);
				}
				else
				{
					commandParser.requestQuote[0] = "\""+(BotMain.combine(Arrays.copyOfRange(params,1,params.length))) + "\" - Blaze : " + sdf.format(new Date())+" : "+game;
					commandParser.requestQuote[1] = user.name;
					BotMain.announce(user.name+" requests to add: "+commandParser.requestQuote[0]);
					BotMain.whisper("Your quote request has been submitted to mods for review", user.name);
					//commandParser.quoteCooldown.push(new String[]{commandParser.requestQuote[1], String.valueOf(new Date().getTime()+300000)});
					if(QuoteTimer == null)
					{
						QuoteTimer = new Thread(new quoteTimer());
						QuoteTimer.start();
					}
					else
					{
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						QuoteTimer = new Thread(new quoteTimer());
					}
				}
			}
			else
			{
				try
				{
					//BotMain.whisper("You cant request another quote for "+getDurationBreakdown(time-new Date().getTime()), user.name);
					BotMain.whisper("You are banned from requesting quotes", user.name);
				}
				catch(Exception e){}
			}
		}
		if(params[0].startsWith("banQuote")&&user.isMod)
		{
			if(params.length>1)
			{
				commandParser.blacklistedRequesters.add(params[1]);
				BotMain.whisper("Banned "+params[1], user.name);
				commandParser.save();
			}
			else
			{
				BotMain.whisper("Error", user.name);
			}
		}
		if(params[0].startsWith("pardonQuote")&&user.isMod)
		{
			for(int i = 0; i < commandParser.blacklistedRequesters.size(); i++)
			{
				if(params.length>1)
				{
					if(params[1].equals(commandParser.blacklistedRequesters.get(i)))
					{
						commandParser.blacklistedRequesters.remove(i);
						BotMain.whisper("Pardoned "+params[1], user.name);
					}
				}
				else
				{
					BotMain.whisper("Error", user.name);
				}
			}
		}
		if(params[0].startsWith("accept")||params[0].startsWith("confirm")&&user.isMod)
		{
			if(commandParser.delQuote[0]!=null)
			{
				BotMain.announce(commandParser.delQuote[0]+" Deleted "+commandParser.quotes.get(Integer.parseInt(commandParser.delQuote[1])-1));
				commandParser.quotes.remove(Integer.parseInt(commandParser.delQuote[1])-1);
				commandParser.delQuote[0] = null;
				commandParser.delQuote[1] = null;
				try {
					StackUtils.saveS("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/quotes.cfg", commandParser.quotes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(commandParser.requestQuote[0]!=null)
			{
				exit = true;
				commandParser.quotes.push(commandParser.requestQuote[0]);
				try {
					StackUtils.saveS("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/quotes.cfg", commandParser.quotes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BotMain.announce(user.name+" Accepted the new quote, Num: "+commandParser.quotes.size());
				BotMain.whisper(user.name+" Accepted your quote!! The number of your new quote is "+commandParser.quotes.size(),commandParser.requestQuote[1]);
			}
		}
		if(params[0].startsWith("decline")&&user.isMod)
		{
			if(commandParser.delQuote[0]!=null)
			{
				BotMain.whisper("Confirmed, not deleted",user.name);
				commandParser.delQuote[0] = null;
				commandParser.delQuote[1] = null;
			}
			if(commandParser.requestQuote[0]!=null)
			{
				exit = true;
				if(params.length>1)
				{
					BotMain.whisper("A moderator declined your quote",commandParser.requestQuote[1]+", because "+Arrays.copyOfRange(params,1,params.length));
				}
				else
				{
					BotMain.whisper("A moderator declined your quote",commandParser.requestQuote[1]);
				}
				//commandParser.quoteCooldown.push(new String[]{commandParser.requestQuote[1], String.valueOf(new Date().getTime()+600000)});
				BotMain.announce(user.name+" Delined the quote");
				commandParser.requestQuote[0] = null;
				commandParser.requestQuote[1] = null;
			}
		}
		if(params[0].toLowerCase().startsWith("detectr")&&user.isMod)
		{
			String rawParse = null;
			try
			{
				rawParse = new JSONObject(new JEditorPane("https://api.twitch.tv/kraken/streams/recursiveblaze").getText()).getJSONObject("stream").getJSONObject("channel").getString("status")+" ";
			}
			catch(Exception e)
			{
				BotMain.whisper("Detection failed; invalid JSON", user.name);
			}
			if(rawParse!=null)
			{
				try
				{
					int start = rawParse.indexOf("http");
					int end = rawParse.indexOf(" ", start+1);
					commandParser.racersURL = rawParse.substring(start, end);
					BotMain.whisper("Detection Succeeded!", user.name);
					BotMain.whisper("Example message: Get the best of all steams! see the split view at "+commandParser.racersURL, user.name);
				}
				catch(Exception e)
				{
					BotMain.whisper("Detection failed; no URL in title", user.name);
				}
			}
		}
		if(params[0].toLowerCase().startsWith("setracers")&&user.isMod)
		{
			String[] racers = Arrays.copyOfRange(params,1,params.length);
			commandParser.racersURL = "http://kadgar.net/live/recursiveblaze/"+BotMain.combine(racers,"/");
			BotMain.whisper("Example message: Get the best of all steams! see the split view at "+commandParser.racersURL, user.name);
		}
		if(params[0].startsWith("Kappa"))
		{
			BotMain.whisper("Kappa",user.name);
		}
		if(params[0].startsWith("reloadblocked")&&user.isMod)
		{
			try {
				BotMain.whisper("Reloading..., current blocked length: "+BotMain.BadURLS.size(), user.name);
				int olength = BotMain.BadURLS.size();
				BotMain.BadURLS = StackUtils.loadS("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/bannedLinks.cfg");
				BotMain.announce("Done reloading blocked links, old length: "+olength+", new blocked link length: "+BotMain.BadURLS.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(params[0].startsWith("chat")&&user.isMod)
		{
			BotMain.chatMsg(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
		}
		if(params[0].startsWith("addcmd")&&user.isMod)
		{
			String alevel="user";
			if(params[0].contains(":"))
			{
				alevel="mod";
			}
			try{
				String[] newcmd={params[1],alevel,BotMain.combine(Arrays.copyOfRange(params, 2, params.length))};
				commandParser.commands.push(newcmd);
				BotMain.whisper(".me Added command "+newcmd[0]+" to do '"+newcmd[2]+"' at "+newcmd[1]+" access level", sender);
			}catch(Exception e){
				BotMain.whisper(".me Error: "+e.getMessage(), sender);
				CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
			}
			return;
		}
		if(params[0].equalsIgnoreCase("removecmd")&&user.isMod){
			for(int i=0;i<commandParser.commands.size();i++){
				String[] curcmd = commandParser.commands.get(i);
				if(params[1].equals(curcmd[0])){
					commandParser.commands.remove(i);
					BotMain.whisper("Removed command: "+params[1],sender);
					return;
				}
			}
			BotMain.chatMsg("Unable to find command "+params[1]);
			return;
		}
		if(params[0].equalsIgnoreCase("addpoll")&&user.isMod){
			String msgp2=BotMain.combine(Arrays.copyOfRange(params,2,params.length));
			System.out.println(msgp2);
			String[] options = BotMain.combine(Arrays.copyOfRange(msgp2.split(";"),1,msgp2.split(";").length),";").trim().split(";");
			new Poll(params[1],msgp2.split(";")[0],options);
			BotMain.whisper("Added Poll: "+params[1],sender);
		}
		if(params[0].equalsIgnoreCase("save")&&user.isMod)
		{
			BotMain.whisper("Saving...",sender);
			try {
				commandParser.save();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
			}
		}
		if(params[0].equalsIgnoreCase("removepoll")&&user.isMod){
			String cmdtoremove=params[1];
			for(Poll poll : Poll.polls){
				if(poll.command.equalsIgnoreCase(cmdtoremove))
				{
					BotMain.whisper("Removed Poll "+cmdtoremove,sender);
					Poll.polls.remove(poll);
					break;
				}
			}
		}
		if(params[0].equalsIgnoreCase("togglelinks")&&user.isMod)
		{
			BotMain.whisper(String.valueOf(commandParser.blockLinks),sender);
		}
		if(params[0].equalsIgnoreCase("togglelink")&&user.isMod)
		{
			commandParser.blockLinks = !commandParser.blockLinks;
			if(commandParser.blockLinks)
			{
				BotMain.announce("I block links now");
			}
			else
			{
				BotMain.announce("I dont block links now");
			}
			commandParser.saveLinks();
		}
		if (params[0].equalsIgnoreCase("ping") && user.isMod) 
	    {
			BotMain.whisper("Pong",user.name);
	    }
		if(params[0].equalsIgnoreCase("users")&&user.isMod)
		{
			User[] userList = new User[User.hmusers.size()];
			User.hmusers.values().toArray(userList);
			String string = "Current Users: ";
			boolean first = true;
			for(int k=0;k<User.hmusers.size();k++)
			{
				if(!(userList[k].isMod))
				{
					if(first)
					{
						string += userList[k].name;
						first = false;
					}
					else
						string += ", " + userList[k].name;
				}	
			}
			first = true;
			string += " Current Mods: ";
			for(int k=0;k<User.hmusers.size();k++)
			{
				if((userList[k].isMod))
				{
					if(first)
					{
						string += userList[k].name;
						first=false;
					}
					else
						string += ", " + userList[k].name;
				}
			}
			BotMain.whisper(string,sender);
		}
	}
	static String[] combineStringArrays(String[] first, String[] second) {
	    List<String> both = new ArrayList<String>(first.length + second.length);
	    Collections.addAll(both, first);
	    Collections.addAll(both, second);
	    return both.toArray(new String[both.size()]);
	}
	static boolean exit = false;
	static class quoteTimer implements Runnable
	{
		@Override
		public void run() 
		{
			boolean exited = false;
			for(int i = 0; i < 30000; i++)
			{
				if(exit)
				{
					exit = false;
					exited = true;
					i = 999999;
				}
				System.out.println(i);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(!exited)
			{
				BotMain.announce("Quote declined silently after 5 minutes");
				commandParser.requestQuote[0] = null;
				commandParser.requestQuote[1] = null;
			}
		}
	}
	static void whisperRecursive(String string, User user)
	{
		if(string.length()>480)
		{
			BotMain.whisper(string.substring(0, 480), user.name);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			whisperRecursive(string.substring(480), user);
		}
		else
		{
			BotMain.whisper(string, user.name);
		}
	}
	public static String getDurationBreakdown(long millis)
    {
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return(sb.toString());
    }
	static void whisperStack(Stack<String> string, User user)
	{
		(new Thread(new whisperStackTimer(string, user))).start();
	}
	public static class whisperStackTimer implements Runnable
	{
		Stack<String> string;
		User user;
		public whisperStackTimer(Stack<String> string, User user)
		{
			this.string = string;
			this.user = user;
		}
		public void run() 
		{
			for(int i = 0; i<string.size(); i++)
			{
				BotMain.whisper(string.get(i), user.name);
			}
		}
	}
}
