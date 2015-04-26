package blazebot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	static boolean O2Spamming = false;
	static boolean aboutSpamming = false;
	public void commandParser(String inmsg) throws IOException, NoSuchMethodException, SecurityException
	{
		String sender = inmsg.substring(1, inmsg.indexOf("!"));
		User user = User.getUser(sender);
		String message = inmsg.substring(inmsg.indexOf(":",1)+1).trim();
		System.out.println(sender+": "+message);
		if(!message.startsWith("!")||user.name.equals(Main.name))
			return;
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
				if(poll.command.equalsIgnoreCase(cmdtoremove)){
					Poll.polls.remove(poll);
					break;
				}
			}
		}
		
		if(inmsg.toLowerCase().contains("blaze") && inmsg.toLowerCase().contains("favorite ship"))
		{
			Main.chatMsg("Blaze would say Zoltan C");
		}
		/////////////Main Commands //////////////////
		
		
		if(params[0].equalsIgnoreCase("!permit")&&isMod(user))
		{
			permitted=User.getUser(params[1].replace("@",""));
			if(permitted.isReal)
			{
				permitted.linkPermitted = true;
				Main.chatMsg(permitted.name+" Permmited, post your link now");
			}
			else
			{
				Main.chatMsg(sender+", User not found");
			}
		}
//		if (params[0].equalsIgnoreCase("!O2") && !O2Spamming) 
//	    {
//			O2Spamming = true;
//	    	Main.chatMsg("O2 is a privilege, not a right");
//	    	//example Parser.class.getMethod("save")
//	    }
//		if (params[0].equalsIgnoreCase("!aboutBot") && !aboutSpamming) 
//	    {
//			aboutSpamming = true;
//	    	Main.chatMsg("EngiMedBot is a bot made by DarkMagicianGirl_ and Maxfojtik. the name is from pikmanfan72");
//	    }
		if (params[0].equalsIgnoreCase("!ping") && isMod(user)) 
	    {
	    	Main.chatMsg("Pong");
	    }
		/////////////////End//////////////////////
		for(int i=0;i<commands.size();i++)
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
		if(params[0].equalsIgnoreCase("!changevote")||params[0].equalsIgnoreCase("!vote")||params[0].equalsIgnoreCase("!pullout"))
				if(activePoll!=null&&user.isReal)
				{
					if(params[0].equalsIgnoreCase("!vote"))
					{
						if(params.length > 1)
						{
							if(!voted.contains(user))
								if(Poll.vote(activePoll,user, Main.combine(Arrays.copyOfRange(params,1,params.length)))){
									total++;
									voted.push(user);
									counted+=1;
								}else{
									Main.chatMsg("That is an invalid option "+user.name);
								}
						}
					}
					if(params[0].equalsIgnoreCase("!changevote"))
					{
						if(params.length > 1)
						{
							activePoll.new Vote(user, Main.combine(Arrays.copyOfRange(params,1,params.length)));
							//use the Poll.vote(thing, user, optionname) for checking these
							Main.chatMsg("Changed Vote");
						}
					}
					if(params[0].equalsIgnoreCase("!pullout"))
					{
						
						//ShipPoll.removeVote(sender);
					}
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
	int total=0;;
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
					delay=500;
				}
				Thread.sleep(10);
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
			System.out.println("Promoted "+params[4]);
		}
	}
	public void userParser(String inmsg)
	{
		if(inmsg.contains("353")){
			String message = inmsg.substring(inmsg.indexOf(":",1)+1);
			String[] params = message.split(" ");
			for(int i=0;i<params.length;i++)
				User.addUser(params[i]);
		}else if(inmsg.contains("JOIN")){
			String sender = inmsg.substring(1, inmsg.indexOf("!"));
			User.addUser(sender);
		}else if(inmsg.contains("PART")){
			String sender = inmsg.substring(1, inmsg.indexOf("!"));
			User.removeUser(sender);
		}
	}
	User permitted=null;
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
		boolean permit=false;
		
		permit=user.equals(permitted);
		if(permit)
		{
			permitted=null;
		}
		if(!(isMod(user)||permit))
			for(int i=0;i<words.length;i++){
				char[] chars = words[i].toCharArray();
				int count=0;
				try{
					for(int ii=0;ii<chars.length;ii++)
						if(".".equals(""+chars[ii]))
							if(Character.isAlphabetic(chars[ii-2])&&Character.isAlphabetic(chars[ii-1])&&Character.isAlphabetic(chars[ii+2])&&Character.isAlphabetic(chars[ii+1]))
								count++;
					if(count>0&&count<3){
						shortened=true;
						break;
					}
				}catch(IndexOutOfBoundsException e){
					//e.printStackTrace();
				}
			}
		if(shortened)
		{
			Main.chatMsg(user.name+" Links are disallowed, ask for permission from a mod");
	    	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	Main.chatMsg(".timeout "+user.name+" 1");
		}
	}
	public static void save()throws IOException{
		Poll.savePolls("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/polls.cfg");
		StackUtils.saveSA("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/commands.cfg",commands);
		System.err.println("Saving commands");
	}
	public static void load()throws IOException{
		Poll.loadPolls("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/polls.cfg");
		commands = StackUtils.loadSA("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/commands.cfg");
		System.err.println("Loading commands");
	}
	public static boolean isMod(User user)
	{
		//return true;
		if(user.name.equalsIgnoreCase("maxfojtik") || user.name.equalsIgnoreCase("darkmagiciangirl_"))
		{
			return true;//sure
		}
		return user.isMod;
	}
	
}
