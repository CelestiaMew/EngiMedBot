package blazebot;

import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

import blazebot.ShipPoll.Ship;

public class Poll 
{
	
	public Poll(String command, String message, String[] options)
	{
		int i=0;
		this.command=command;
		this.options = new Option[options.length];
		for(String option : options)
		{
			Option temp = new Option(option);
			this.options[i] = temp;
			i++;
		}
		this.message = message;//yeh
		polls.add(this);//stored
		
	}
	String command;
	//hmm how can i explain this
	//so when this instance is made, there will be one instance per poll
	//each poll will have a certain amount of options
	//the vote class will be instantated off of this instance
	//so each vote that is called will know what options are availible and 
	//yeh but the constructor is just for the poll to say, this is what i should react to but it wont run from this
	public void startPoll()
	{
		Parser.activePoll=this;// for what will be outputted like 'Vote for your favourite ship' etc
		Main.chatMsg(message);
	}
	String message;//this will be stored from when constructor ran
	//so all we are doing, is new Poll("!poll","message",new String(){"stuff"}); then it will auto store itself in the poll stack right? so we can then use a static method to search the stack for
	//so, which parts are confusing? do you want me to run through program?
	public void changePoll(User user, String options)
	{
		
	}
	public void endPoll()
	{
		for(int i = 0;i<votes.size();i++)
		{
			votes.get(i).option.votecount++;
			System.out.println(votes.get(i).option);
		}
		Option bestest = new Option("Nothing");//no it doesnt, but atm the options[] is empty because you are not adding to it
		for(int i=0;i<options.length;i++)
		{
			if(options[i].votecount>bestest.votecount)
			{
				bestest=options[i];//what you mean
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
			Main.chatMsg("The option with the most votes is "+bestest.name+" with "+bestest.votecount+" votes");
		}
		else
		{
			Main.chatMsg("Tie detected between "+bestest.name+" and "+tiedString.substring(0, tiedString.length()-4));
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
		public Vote(User user,String option)// it only just occured to me
		{
			System.out.println(option);
			for(int i=0;i<votes.size();i++)
			{
				if(votes.get(i).user.equals(user))
				{
					votes.remove(i);
				}
			}
			boolean good = false;
			for(Option curoption : options){// instances dont work like numbers all you store is a pointer to that instance, so if i do x = new stack or something a=x a litterally equals x so 
				//this is efficient you can do implements Cloneable if you need it, to do instance.clone() to create an exact copy but usually its not needed
				if(curoption.name.equalsIgnoreCase(option)){//work betteer? this searches it
					System.out.println(curoption.name);
					this.user=user;
					this.option=curoption; //up here no need for search, sry :x
					valid=true;
					votes.push(this);// do you understand this?
					break;
				}
			}
			if(!good)
			{
				Main.chatMsg(user.name+", Invalid Option");
			}
		}
		User user;
		Option option;
	}
	class Option
	{
		int votecount = 0;
		String name = "";
		public Option(String name)
		{
			this.name = name;
		}
	}
	Option[] options = null;//so do you see where we are going so far?
	Stack<Vote> votes = new Stack<Vote>();
	public static Stack<Poll> polls = new Stack<Poll>();
}