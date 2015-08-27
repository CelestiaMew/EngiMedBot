package blazebot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;

import blazebot.Parsers.commandParser;
import blazebot.Parsers.userParser;
import blazebot.Parsers.whisperParser;


public class BotMain {
	public static String server = "irc.twitch.tv";
	public static String serverw = "199.9.253.119";
	public static String name = "EngiMedBot";
	//public static String pass = "oauth:za44chgpxyxq1mygjdw9z60s7u4ivk";
	//public static String name = "RecursiveBot";
	public static String pass = "oauth:ibawewtly5behf876jfepvtrulhejm";
	public static String channel = "#maxfojtik";
	//public static String channel = "#recursiveblaze";
	//static String channel = "#darkmagiciangirl_";
	public static BufferedWriter writer=null;
	public static BufferedReader reader=null;
	public static BufferedWriter writerw=null;
	public static BufferedReader readerw=null;
	public static ItemSearch searcher = new ItemSearch();
//	public static boolean channelonline = true;
//	public static int downtime = 0;
	public static Socket socket;
	public static Socket socketw;
	public static JConsole console = new JConsole();
	public static JConsole Mconsole = new JConsole();
	public static boolean wpinged = true;
	public static boolean pinged = true;
	public static boolean isServer = false;
	public static void connect() throws Exception
	{
		socket = new Socket(server,6667);
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		serverMsg("PASS "+pass);
		serverMsg("NICK "+name);
		serverMsg("JOIN "+channel);
	}
	public static void wconnect() throws Exception
	{
		socketw = new Socket(serverw,6667);
		writerw = new BufferedWriter(new OutputStreamWriter(socketw.getOutputStream()));
		readerw = new BufferedReader(new InputStreamReader(socketw.getInputStream()));
		wServerMsg("PASS "+pass);
		wServerMsg("NICK "+name);
		wServerMsg("CAP REQ :twitch.tv/commands");
	}
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		JButton ExitB = new JButton("Exit");
		f.setLayout(null);
		//f.setUndecorated(true);
		//f.setAlwaysOnTop(true);
		f.setBounds(0,0, 700, 150+500);
		f.setTitle(name+" bot");
		console.setBounds(0, 110, 700, 500);
		console.init();
		Mconsole.setBounds(300, 0, 400, 110);
		Mconsole.init();
		if (Files.exists(new File("C:/Users/Max/AppData/Roaming/EngiMedBot").toPath()))
		{
			isServer = true;
			f.setAlwaysOnTop(true);
		}
		f.add(ExitB);
		f.add(console);
		f.add(Mconsole);
		f.setResizable(false);
		f.setVisible(true);
		ExitB.setBounds(0,0,300,110);
		ExitB.addActionListener(new ExitButtonHandler());
		try
		{
			try {
				connect();
				//Thread.sleep(100);
				wconnect();
				//Thread.sleep(3000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
			}
			String line="";
			commandParser.load();
			new Ticker(commandParser.class,commandParser.class.getMethod("save"),600,true);
			new Ticker(BotMain.class,BotMain.class.getMethod("updateTimeouts"),60,true);
			//new Ticker(parser,Main.class.getMethod("ping"),60,true);
			checkForUpdates();
			new Ticker(BotMain.class,BotMain.class.getMethod("checkForUpdates"),10,true);
			new Thread(new userParser.ParerTimer()).start();
			new Ticker(BotMain.class,BotMain.class.getMethod("checkToReconnect"),360,true);
			new Ticker(BotMain.class,BotMain.class.getMethod("ping"),3,false);
			while (true) 
			{
				if(reader.ready())
				{
					line=reader.readLine();
					System.out.println("IN: "+line);
					if (line.toLowerCase().contains("ping ") && !line.toLowerCase().contains("privmsg")) 
		            {
						System.out.println(line);
						//console.print("INPING: "+line);
		            	serverMsg("PONG tmi.twitch.tv", false);
		            	pinged = true;
		            }
					else
					{
						if(line.toLowerCase().contains("privmsg"))
						{
							checkShortened(line, User.getUser(line.substring(1, line.indexOf("!"))));
		            		commandParser.parse(line);
		            		console.print("INPRV: "+line);
						}
						else
						{
							console.print("IN: "+line);
						}
					}
				}
				else if(readerw.ready())
				{
					line=readerw.readLine();
					System.out.println("INW: "+line);
					if (line.toLowerCase().contains("ping ") && !line.toLowerCase().contains("whisper")) 
		            {
						//console.print("INPINGW: "+line);
						wServerMsg("PONG tmi.twitch.tv", false);
						wpinged = true;
		            }
					else
					{
						if(line.toLowerCase().contains("whisper") && !line.toLowerCase().contains("notice"))
						{
							whisperParser.parse(line);
							console.print("INWISP: "+line);
						}
						else
						{
							console.print("INW: "+line);
						}
					}
				}
				else
				{
					Thread.sleep(100);
				}
	        }
		}catch(Exception e){
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		};
	}
	
	/**
	 * Misc commands stored here
	 */
	/**
	 * All command parsing here
	 * @param inmsg : raw chat message
	 * @throws IOException
	 */
	public static void ping() throws IOException
	{
		if(checkUpdated())
		{
			announce("Im back (Updated)");
		}
		else
		{
			announce("im now online");
		}
	}
	public static String combine(String[] args,String regex){
		String str="";
		for(int i=0;i<args.length;i++){
			str+=args[i];
			if(i!=args.length-1){
				str+=regex;
			}
		}
		return str;
	}
	public static String combine(String[] args){
		return combine(args," ");
	}
	public static void lower(){
		msgcounter--;
	}
	public static void chatMsg(String msg)
	{
		serverMsg("PRIVMSG "+channel+" :"+msg);
	}
	public static void whisper(String msg,String user)
	{
		wServerMsg("PRIVMSG "+channel+" :/w "+user+" "+msg);
	}
	public static void announce(String msg)
	{
		User[] userList = new User[User.hmusers.size()];
		User.hmusers.values().toArray(userList);
		for(int k=0;k<User.hmusers.size();k++)
		{
			if((userList[k].isMod) && !userList[k].name.equals("engimedbot"))
			{
				whisper(msg, userList[k].name);
			}	
		}
	}
	public static void wServerMsg(String msg)
	{
		System.err.println("OUTW : "+msg);
		console.print("OUTW : "+msg);
		msgcounter++;
		try{
			writerw.write(msg+"\r\n");
			writerw.flush();
		}catch(IOException e){
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
		
	}
	public static void wServerMsg(String msg, boolean print)
	{
		System.err.println("OUTW : "+msg);
		if(print)
		{
			console.print("OUTW : "+msg);
		}
		msgcounter++;
		try{
			writerw.write(msg+"\r\n");
			writerw.flush();
		}catch(IOException e){
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
		
	}
	static int msgcounter=0;
	public static void serverMsg(String msg)
	{
		System.err.println("OUT : "+msg);
		console.print("OUT : "+msg);
		msgcounter++;
		try{
			writer.write(msg+"\r\n");
			writer.flush();
		}catch(IOException e){
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
		
	}
	public static void serverMsg(String msg, boolean print)
	{
		System.err.println("OUT : "+msg);
		if(print)
		{
			console.print("OUT : "+msg);
		}
		msgcounter++;
		try{
			writer.write(msg+"\r\n");
			writer.flush();
		}catch(IOException e){
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
		
	}
	public static class ExitButtonHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Exit();
		}
	}
	public static void Exit()
	{
		
		announce("im now offline");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			commandParser.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
		System.exit(0);
	}
	public static void checkForUpdates() throws IOException
	{
		if(new File("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/BotUpdate.jar").exists())
		{
			new File("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/BotUpdate.txt").createNewFile();
			announce("im updating, be right back");
			try {
				commandParser.save();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
			}
			Runtime.getRuntime().exec("java -jar "+"C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/updater.jar");
			System.exit(0);
		}
	}
	public static boolean checkUpdated() throws IOException
	{
		boolean yes = false;
		try {
			BufferedReader reader = new BufferedReader(new FileReader("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/BotUpdate.txt"));
			String line = "false";
			line = reader.readLine();
			reader.close();
			yes = Boolean.parseBoolean(line);
		}
		catch (FileNotFoundException e) 
		{
			
		}
		catch (NullPointerException e)
		{
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
		File file = new File("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/BotUpdate.txt");
		file.createNewFile();
		FileOutputStream out;
		try
		{
			out = new FileOutputStream(file.toString());
			out.write("false".getBytes());
			out.close();
		} 
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return yes;
	}
	public static void checkToReconnect()
	{
		if(!pinged)
		{
			console.print("Reconnecting...");
			try
			{
				serverMsg("PART "+channel);
				Thread.sleep(1000);
				socket.close();
				connect();
			}
			catch(Exception e)
			{
				CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
			}
		}
		if(!wpinged)
		{
			console.print("ReconnectingW...");
			try
			{
				wServerMsg("PART "+channel);
				Thread.sleep(1000);
				socketw.close();
				wconnect();
			}
			catch(Exception e)
			{
				CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
			}
		}
		pinged = false;
		wpinged = false;
	}
//	public static void checkToReconnect()
//	{
//		try {
//			JEditorPane jep = new JEditorPane("https://api.twitch.tv/kraken/streams/"+channel.substring(1));
//			if(jep.getText().contains("\"stream\":{"))
//			{
//				channelonline = true;
//			}
//			else
//			{
//				downtime++;
//				channelonline = false;
//			}
//			if(channelonline && downtime>10)
//			{
//				downtime = 0;
//				serverMsg("PART "+channel);
//				rebooting = true;
//				Thread.sleep(100);
//				socket.close();
//				init();
//			}
//		} catch (IOException | InterruptedException e) {
//			// TODO Auto-generated catch block
//			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
//		}
//		
//	}
	public static void checkShortened(String inmsg, User user) throws IOException
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
		boolean permit=user.name.equals(commandParser.permitted);
		if(!user.isMod && !permit && commandParser.blockLinks)
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
					//CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
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
				commandParser.permitted=null;
				return;
			}
			if(shortened)
			{
				if(timed)
				{
					chatMsg(user.name+" you are timed out from posting links");
				}
				else
				{
					chatMsg(user.name+" Links are disallowed, ask for permission from a mod");
				}
		    	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
				}
		    	chatMsg(".timeout "+user.name.trim()+" 1");
			}
		}
	}
	public static void updateTimeouts()
	{
		for(int i=0; i<User.linkTimedOut.size(); i++)
		{
			if(Long.parseLong(User.linkTimedOut.get(i)[1])<new Date().getTime())
			{
				if(User.hmusers.containsKey(User.linkTimedOut.get(i)[0].toLowerCase()))
					chatMsg(User.linkTimedOut.get(i)[0]+", Your link timeout has been lifted");
				System.out.println("Removing "+User.linkTimedOut.get(i)[0]+" from timeout list");
				User.linkTimedOut.remove(i);
			}
		}
	}
}
