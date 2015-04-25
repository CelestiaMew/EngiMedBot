package blazebot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;


public class Main {
	static String server = "irc.twitch.tv";
	static String name = "EngiMedBot";
	//static String pass = "oauth:4klv2yyxwvyuoq8lltzd4zrbnr94pl";
	static String pass = "oauth:j6mop9v1grb3rizwgr2j1vo4uyyzhx";
	//static String channel = "#maxfojtikbot";
	static String channel = "#recursiveblaze";
	//static String channel = "#darkmagiciangirl_";
	static BufferedWriter writer=null;
	static BufferedReader reader=null;
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
			chatMsg(".me is now Online");
			Parser parser = new Parser();
			Parser.load();
			new Ticker(parser,Parser.class.getMethod("save"),600,true);
			while ((line=reader.readLine())!=null) {
				System.out.println(line);
	            if (line.toLowerCase().contains("ping ")) 
	            {
	            	serverMsg("PONG " + line.substring(5));
	            }
	            else 
	            {
	            	if(line.contains("353")||line.contains("JOIN")||line.contains("PART"))
	            	{
	            		parser.userParser(line);
	            	}
	            	else if(line.startsWith(":jtv MODE"))
	            	{
	            		parser.modParser(line);
	            	}
	            	else if(line.contains("PRIVMSG"))
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
	public static String combine(String[] args){
		String str="";
		for(int i=0;i<args.length;i++){
			str+=args[i];
			if(i!=args.length-1){
				str+=" ";
			}
		}
		return str;
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
}
