package blazebot.Parsers;

import java.io.IOException;
import java.util.Arrays;

import blazebot.CrashGUI;
import blazebot.Item;
import blazebot.BotMain;
import blazebot.Poll;
import blazebot.User;
import blazebot.Poll.Option;

public class whisperParser 
{
	public static void parse(String inmsg)
	{
		String sender = inmsg.substring(1, inmsg.indexOf("!"));
		User user = User.getUser(sender.toLowerCase());
		String message = inmsg.substring(inmsg.indexOf(":",1)+1).trim();
		System.out.println(sender+": "+message);
		String[] params = message.split(" ");
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
						case "help":BotMain.whisper("mod access; Shows help messages or with no parameter shows all commands, seriously..., use: !help, !help !<command>", user.name);return;
						case "togglelink":BotMain.whisper("mod access; Toggles if the bot should timeout people who are not permitted to post links", user.name);return;
					}	
				}
				else
				{
					switch(params[1].toLowerCase())
					{
						
						case "!uptime":BotMain.whisper("Shows time stream has been active, use: !uptime [nameofstream]", user.name);return;
						case "!vote":BotMain.whisper("Votes on current poll, use: !vote <option>", user.name);return;
						case "help":BotMain.whisper("mod access; Shows help messages or with no parameter shows all commands, seriously..., use: !help, !help !<command>", user.name);return;
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
					str="Mod commands: !follower !timeoutlink !draw help addcmd removecmd addpoll removepoll !permit !ping ping !uptime !endpoll togglelink !whatis !whatispools !whatispickup !whatisid !whatisunlock";
					for(int i=0;i<commandParser.commands.size();i++)
					{
						String[] cmds=commandParser.commands.get(i);
						if(cmds[1].equals("mod"))
						{
							str+=" "+cmds[0];
						}
					}
					str+=" User Commands: !vote whatis whatispools whatispickup whatisid whatisunlock";
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
					str="Commands: !vote whatis whatispools whatispickup whatisid whatisunlock";
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
				info=BotMain.combine(item.info);
				BotMain.whisper(info, user.name);
			}
			if(params[0].equalsIgnoreCase("whatispools")){
				Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
				info="Pools: "+BotMain.combine(item.pools);
			}
			if(params[0].equalsIgnoreCase("whatispickup")){
				Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
				info=item.pickup;
			}
			if(params[0].equalsIgnoreCase("!whatisid")){
				Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
				info="ID: "+item.id;
			}
			if(params[0].equalsIgnoreCase("whatisunlock")){
				Item item = BotMain.searcher.searchFor(BotMain.combine(Arrays.copyOfRange(params,1,params.length)));
				info=item.unlock;
			}
			whisperRecursive(info, user);
		}
		catch(Exception e)
		{
			
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
}
