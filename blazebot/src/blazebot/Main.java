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
import java.net.DatagramSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;


public class Main {
	static String server = "irc.twitch.tv";
	static String serverw = "199.9.253.119";
	static String name = "EngiMedBot";
	//static String pass = "oauth:4klv2yyxwvyuoq8lltzd4zrbnr94pl";
	static String pass = "oauth:ibawewtly5behf876jfepvtrulhejm";
	static String channel = "#recursiveblaze";
	//static String channel = "#recursiveblaze";
	//static String channel = "#darkmagiciangirl_";
	static BufferedWriter writer=null;
	static BufferedReader reader=null;
	static BufferedWriter writerw=null;
	static BufferedReader readerw=null;
	static ItemSearch searcher = new ItemSearch();
	static boolean channelonline = true;
	static int downtime = 0;
	static boolean rebooting = false;
	static Socket socket;
	static Socket socketw;
	static JConsole console = new JConsole();
	static void init()
	{
		try {
			if(rebooting)
			{
				Thread.sleep(2000);
			}
			socket = new Socket(server,6667);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketw = new Socket(serverw,6667);
			writerw = new BufferedWriter(new OutputStreamWriter(socketw.getOutputStream()));
			readerw = new BufferedReader(new InputStreamReader(socketw.getInputStream()));
			serverMsg("PASS "+pass);
			serverMsg("NICK "+name);
			Thread.sleep(100);
			wServerMsg("PASS "+pass);
			wServerMsg("NICK "+name);
			Thread.sleep(100);
			wServerMsg("CAP REQ :twitch.tv/commands");
			serverMsg("JOIN "+channel);
			Thread.sleep(100);
			if(checkUpdated())
			{
				chatMsg(".me Just Updated");
			}
			else
			{
				if(rebooting)
				{
					chatMsg(".me Activating!!!");
				}
				else
				{
					chatMsg(".me is now Online");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rebooting = false;
	}
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		JButton ExitB = new JButton("Exit");
		f.setLayout(null);
		//f.setUndecorated(true);
		//f.setAlwaysOnTop(true);
		f.setBounds(0,0, 700, 150+500);
		f.setTitle("Engi Med Bot");
		console.setBounds(0, 110, 700, 500);
		console.init();
		f.add(ExitB);
		f.add(console);
		f.setResizable(false);
		f.setVisible(true);
		ExitB.setBounds(0,0,300,110);
		ExitB.addActionListener(new ExitButtonHandler());
		try
		{
			init();
			String line="";
			Parser parser = new Parser();
			Parser.load();
			new Ticker(parser,Parser.class.getMethod("save"),600,true);
			new Ticker(parser,Parser.class.getMethod("updateTimeouts"),60,true);
			//new Ticker(parser,Main.class.getMethod("ping"),60,true);
			checkForUpdates();
			new Ticker(Main.class,Main.class.getMethod("checkForUpdates"),10,true);
			new Ticker(Parser.class,Parser.class.getMethod("userParser"),60,true);
			Parser.userParser();
			new Ticker(Main.class,Main.class.getMethod("checkToReconnect"),10,true);
			boolean proc = false;
			while (true) 
			{
				while(rebooting)
				{
					Thread.sleep(1000);
				}
				if(reader.ready())
				{
					line=reader.readLine();
					proc = true;
					System.out.println("IN: "+line);
					if (line.toLowerCase().contains("ping ") && !line.toLowerCase().contains("PRIVMSG")) 
		            {
						System.out.println(line);
						console.print("INPING: "+line);
		            	serverMsg("PONG tmi.twitch.tv");
		            	proc = false;
		            }
				}
				else if(readerw.ready())
				{
					line=readerw.readLine();
					proc = true;
					System.out.println("INW: "+line);
					if (line.toLowerCase().contains("ping ") && !line.toLowerCase().contains("PRIVMSG")) 
		            {
						console.print("INPINGW: "+line);
						wServerMsg("PONG tmi.twitch.tv");
						proc = false;
		            }
				}
				else
				{
					Thread.sleep(100);
					proc = false;
				}
				if(proc)
				{
					console.print("IN : "+line);
	            	if(line.startsWith(":jtv MODE"))
	            	{
	            		parser.modParser(line);
	            	}
	            	else if(line.contains("WHISPER"))// you dont have to worry about any of this for commands
	            	{
	            		Parser.whisperParser(line);
	            	}
	            	else if(line.contains("PRIVMSG"))// you dont have to worry about any of this for commands
	            	{
	            		parser.checkShortened(line, User.getUser(line.substring(1, line.indexOf("!"))));
	            		parser.commandParser(line);
	            	}
	            	else
	            	{
	            		//System.out.println(line);
	            	}
				}
	        }
		}catch(Exception e){
			e.printStackTrace();
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
	public static void ping()
	{
		serverMsg("PONG");
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
		wServerMsg("PRIVMSG "+channel+" :/w "+user+" "+msg+"\r\n");
	}
	public static void wServerMsg(String msg){
		System.err.println("OUTW : "+msg);
		console.print("OUTW : "+msg);
		msgcounter++;
		try{
			writerw.write(msg+"\r\n");
			writerw.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	static int msgcounter=0;
	public static void serverMsg(String msg){
		System.err.println("OUT : "+msg);
		console.print("OUT : "+msg);
		msgcounter++;
		try{
			writer.write(msg+"\r\n");
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
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
		Main.chatMsg("/me is now Offline");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Parser.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	public static void checkForUpdates() throws IOException
	{
		if(new File("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/BotUpdate.jar").exists())
		{
			new File("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/BotUpdate.txt").createNewFile();
			chatMsg("Updating...");
			Process proc = Runtime.getRuntime().exec("java -jar "+"C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/updater.jar");
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
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		try {
			JEditorPane jep = new JEditorPane("https://api.twitch.tv/kraken/streams/"+channel.substring(1));
			if(jep.getText().contains("\"stream\":{"))
			{
				channelonline = true;
			}
			else
			{
				downtime++;
				channelonline = false;
			}
			if(channelonline && downtime>10)
			{
				downtime = 0;
				serverMsg("PART "+channel);
				rebooting = true;
				Thread.sleep(100);
				socket.close();
				init();
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
