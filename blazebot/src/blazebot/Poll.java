package blazebot;

import java.io.IOException;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Stack;

import blazebot.Parsers.commandParser;


public class Poll 
{
	
	public Poll(String command, String message, String[] inoptions)
	{
		int i=0;
		this.command=command;
		
		options = new Option[inoptions.length];
		for(String option : inoptions)
		{
			Option temp = new Option(option);
			this.options[i] = temp;
			i++;
		}
		this.message = message;
		polls.add(this);
		
	}
	public String command;
	public void startPoll()
	{
		commandParser.activePoll=this;
		BotMain.chatMsg(message + " Vote using !vote <option>");
	}
	public String message;
	public void changePoll(User user, String options)
	{
		
	}
	public void endPoll()
	{
		for(int i = 0;i<votes.size();i++)
		{
			votes.get(i).option.votecount++;
		}
		Option bestest = new Option("Nothing");
		for(int i=0;i<options.length;i++)
		{
			if(options[i].votecount>bestest.votecount)
			{
				bestest=options[i];
			}
		}
		boolean tied = false;
		String tiedString = "";
		for(int i=0;i<options.length;i++)
		{
			if((bestest.votecount==options[i].votecount) && !bestest.name.equals(options[i].name))
			{
				tied = true;
				tiedString = tiedString + options[i].name+" and ";
			}
		}
		String[] temp = bestest.name.split(" ");
		for(int i=0;i<temp.length;i++)
		{
			temp[i]=temp[i].substring(0, 1).toUpperCase()+temp[i].substring(1);
		}
		if(!tied)
		{
			BotMain.chatMsg("The option with the most votes is "+bestest.name+" with "+bestest.votecount+" votes");
		}
		else
		{
			BotMain.chatMsg("Tie between "+bestest.name+" and "+tiedString.substring(0, tiedString.length()-4)+" with "+bestest.votecount+" votes");// i didnt read i didnt think you were doing that my bad
		}
		options=null;
		votes=new Stack<Vote>();//there is 2 ways we could do it, have a static method that will look through the polls, or have the Parser class do it cause startPoll and endPoll are NOT static
	}
	public static boolean vote(Poll active,User user,String optionname){
		return active.new Vote(user,optionname).valid;
	}
	class Vote
	{
		
		boolean valid=false;
		public Vote(User user,String option)
		{
			for(int i=0;i<votes.size();i++)
			{
				if(votes.get(i).user.equals(user))
				{
					votes.remove(i);
				}
			}
			for(Option curoption : options){
				if(curoption.name.equalsIgnoreCase(option)){
					this.user=user;
					this.option=curoption;
					valid=true;
					votes.push(this);
					break;
				}
			}
		}
		User user;
		Option option;
	}
	public class Option
	{
		public int votecount = 0;
		public String name = "";
		public Option(String name)
		{
			this.name = name;
		}
	}
	public static void savePolls(String filename){
		Stack<String[]> stack = new Stack<String[]>();
		for(Poll poll : polls){
			stack.push(poll.arrSerialize());
		}
		try {
			StackUtils.saveSA(filename, stack);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
	}
	public static void loadPolls(String filename){
		Stack<String[]> stack=null;
		try {
			stack = StackUtils.loadSA(filename);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] cur = null;
		try{
			while((cur=stack.pop())!=null){
				new Poll(cur[0],cur[1],Arrays.copyOfRange(cur, 2, cur.length));
			}
		}catch(EmptyStackException e){}
	}
	public String[] arrSerialize(){
		String[] arr = new String[2+options.length];
		arr[0]=command;
		arr[1]=message;
		for(int i=0;i<options.length;i++){
			arr[2+i]=options[i].name;
		}
		return arr;
	}
	public Option[] options = null;//so do you see where we are going so far?
	public Stack<Vote> votes = new Stack<Vote>();
	public static Stack<Poll> polls = new Stack<Poll>();
}