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
import javax.swing.JFrame;


public class Main {
	static String server = "irc.twitch.tv";
	static String name = "EngiMedBot";
	//static String pass = "oauth:4klv2yyxwvyuoq8lltzd4zrbnr94pl";
	static String pass = "oauth:j6mop9v1grb3rizwgr2j1vo4uyyzhx";
	static String channel = "#maxfojtik";
	//static String channel = "#recursiveblaze";
	//static String channel = "#darkmagiciangirl_";
	static BufferedWriter writer=null;
	static BufferedReader reader=null;
	static ItemSearch searcher = new ItemSearch();
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setLayout(null);
		//f.setUndecorated(true);
		//f.setAlwaysOnTop(true);
		f.setBounds(0,0, 300, 150);
		f.setTitle("Engi Med Bot");
		f.setVisible(true);
		JButton ExitB = new JButton("Exit");
		f.add(ExitB);
		ExitB.setBounds(0,0,300,110);
		ExitB.addActionListener(new ExitButtonHandler());
		try{
			Socket socket = new Socket(server,6667);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			serverMsg("PASS "+pass);
			serverMsg("NICK "+name);
			writer.flush();
			String line="";
			while((line=reader.readLine())!=null){
				System.out.println(line);
				if(line.indexOf("004") >= 0){
					break;
				}
			}
			writer.write("JOIN "+channel+"\r\n");
			writer.flush();
			if(checkUpdated())
			{
				chatMsg(".me Just Updated");
			}
			else
			{
				chatMsg(".me is now Online");
			}
			Parser parser = new Parser();
			Parser.load();
			new Ticker(parser,Parser.class.getMethod("save"),600,true);
			new Ticker(parser,Parser.class.getMethod("updateTimeouts"),60,true);
			checkForUpdates();
			new Ticker(Main.class,Main.class.getMethod("checkForUpdates"),10,true);
			while ((line=reader.readLine())!=null) {
				System.out.println(line);
	            if (line.toLowerCase().contains("ping ")) 
	            {
	            	serverMsg("PONG " + line.substring(5));
	            }
	            else 
	            {
	            	if(line.contains("353")||line.contains("JOIN")||line.contains("PART"))//look in User class
	            	{
	            		parser.userParser(line);
	            	}
	            	else if(line.startsWith(":jtv MODE"))
	            	{
	            		parser.modParser(line);
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
			
			socket.close();
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
	public static void chatMsg(String msg){
		serverMsg("PRIVMSG "+channel+" :"+msg);
	}
	static int msgcounter=0;
	public static void serverMsg(String msg){
		System.err.println("OUT : "+msg);
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
}
