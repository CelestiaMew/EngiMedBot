package blazebot;

import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class ShipPoll
{
	static boolean isPolling = false;
	static Stack<String[]> Voters = new Stack<String[]>();
	static String[] Ships = {"Federation", "Engi", "Zoltan", "Mantis", "Stealth", "Slug", "Rock", "Lanuis", "Crystal", "Kestrel"};
	static boolean limited = false;
	static Stack<String> limitedShips = new Stack<String>();
	static int votes = 0;
	static Timer anounceTimer;
	public static void startPoll() throws NoSuchMethodException, SecurityException
	{
		if(!isPolling)
		{
			anounceTimer = null;
			anounceTimer = new Timer();
			setupTask();
			anounceTimer.scheduleAtFixedRate(taskPerformer, 0, 5000);
			votes = 0;
			restartPoll();
			isPolling = true;
			Main.chatMsg(".me is now counting votes!");
			Main.chatMsg("Vote with the command !vote, and example would be !vote Federation A, you can also change you vote with !changevote Mantis A; Commands are not case-sensitive");
		}
		else
		{
			Main.chatMsg("Poll already running");
		}
	}
	public static void restartPoll()
	{
		Ship.delAll();
		limited = false;
		Voters.clear();
	}
	static class winner
	{
		String name;
		int count;//done
	}
	public static void stopPoll()
	{
		if(isPolling)
		{
			anounceTimer.cancel();
			Main.chatMsg("Poll ended");
			Main.chatMsg("Counting Votes");
			isPolling = false;
			countUpVotes();
		}
		else
		{
			Main.chatMsg("Poll not running");
		}
	}
	public static void countUpVotes()
	{
		for(int i=0;i<Voters.size();i++) 
		{
			boolean exists = false;
			for(int i2=0;i2<Ship.Ships.size();i2++)
			{
				if(Ship.Ships.get(i2).Name.equalsIgnoreCase(Voters.get(i)[1]))
				{
					exists = true;
					Ship.Ships.get(i2).addVote();
				}
			}
			if(!exists)
			{
				Ship.newShip(Voters.get(i)[1]);
				for(int i2=0;i2<Ship.Ships.size();i2++)
				{
					if(Ship.Ships.get(i2).Name.equalsIgnoreCase(Voters.get(i)[1]))
					{
						Ship.Ships.get(i2).addVote();
					}
				}
			}
		}
		Ship bestest = new Ship("Nothing");
		for(int i=0;i<Ship.Ships.size();i++)
		{
			if(bestest.votes<Ship.Ships.get(i).votes)
			{
				bestest = Ship.Ships.get(i);
			}
		}
		boolean tied = false;
		String tiedString = "";
		for(int i=0;i<Ship.Ships.size();i++)
		{
			if((bestest.votes==Ship.Ships.get(i).votes) && !bestest.Name.equals(Ship.Ships.get(i).Name))
			{
				tied = true;
				tiedString = capShip(tiedString+Ship.Ships.get(i).Name)+" and ";
			}
		}
		String[] temp = bestest.Name.split(" ");
		for(int i=0;i<temp.length;i++)
		{
			temp[i]=temp[i].substring(0, 1).toUpperCase()+temp[i].substring(1);
		}
		bestest.Name = capShip(bestest.Name);
		if(!tied)
		{
			Main.chatMsg("The winning ship is "+bestest.Name+" with "+bestest.votes+" votes");
		}
		else
		{
			Main.chatMsg("tie detected between "+bestest.Name+" and "+tiedString.substring(0, tiedString.length()-4));
		}
	}
	public static String capShip(String ShipN)
	{
		String[] temp = ShipN.split(" ");
		for(int i=0;i<temp.length;i++)
		{
			temp[i]=temp[i].substring(0, 1).toUpperCase()+temp[i].substring(1);
		}
		ShipN = Main.combine(temp);
		return ShipN;
	}
	public static void changeVote(String Name, String ShipName) throws IOException
	{
		ShipName = ShipName.toLowerCase();
		for(int i=0;i<Voters.size();i++)
		{
			if(Voters.get(i).equals(Name))
			{
				Voters.remove(i);
			}
		}
		String[] data = {Name, ShipName};
		if(validateShip(ShipName))
		{
			Voters.push(data);
		}
		else
		{
			//Main.chatMsg(Name+", that is not a ship. You now dont have a vote counted");
		}
	}
	public static void limitShips(String message)
	{
		limited = true;
		String[] ships = Main.combine(Arrays.copyOfRange(message.split(" "),1,message.split(" ").length)).split(", ");
		ships[ships.length-1].trim();
		String Ships = "";
		for(int i=0;i<ships.length;i++)
		{
			Ships = Ships + ships[i] + ", ";
			limitedShips.push(ships[i]);
		}
		Main.chatMsg("Limited ships to: "+Ships.substring(0, Ships.length()-2));
		Stack<String> lostVoters = new Stack<String>();
		for(int i=0;i<Voters.size();i++)
		{
			boolean good = false;
			for(int i2=0;i2<limitedShips.size();i2++)
			{
				if(Voters.get(i)[1].equals(limitedShips.get(i2)))
				{
					good = true;
				}
			}
			if(!good)
			{
				lostVoters.push(Voters.get(i)[0]);
				//removeVote(Voters.get(i)[0]);
			}
		}
		String LostVoters = "";
		for(int i=0;i<lostVoters.size();i++)
		{
			LostVoters = LostVoters + lostVoters.get(i) + ", ";
		}
		if(LostVoters.length()>1)
		{
			LostVoters = LostVoters.substring(0, LostVoters.length()-2);
			LostVoters = LostVoters + " please revote with the new criteria";
			Main.chatMsg(LostVoters);
		}
	}
	public static void removeVote(String Name) throws IOException
	{
		for(int i=0;i<Voters.size();i++)
		{
			if(Voters.get(i)[0].equals(Name))
			{
				Voters.remove(i);
			}
		}
	}
	public static void countVote(String Name, String ShipName) throws IOException
	{
		ShipName = ShipName.toLowerCase();
		for(int i=0;i<Voters.size();i++)
		{
			if(Voters.get(i)[0].equals(Name))
			{
				Voters.remove(i);
			}
		}
		String[] data = {Name, ShipName};
		boolean good = false;
		if(limited)
		{
			for(int i=0;i<limitedShips.size();i++)
			{
				if(limitedShips.get(i).trim().equalsIgnoreCase(ShipName))
				{
					good = true;
				}
			}
		}
		else
		{
			good = true;
		}
		if(validateShip(ShipName))
		{
			if(good)
			{
				//Main.chatMsg(Name+", counted your vote");
				votes++;
				Voters.push(data);
			}
			else
			{
				//Main.chatMsg(Name+", please vote for a ship that is allowed");
			}
		}
		else
		{
			//Main.chatMsg(Name+", that is not a ship");
		}
	}
	public static boolean validateShip(String Name)
	{
		boolean good = false;
		String[] ShipAtt = Name.split(" ");
		for(int i=0;i<Ships.length;i++)
		{
			if(ShipAtt[0].toLowerCase().equals(Ships[i].toLowerCase()))
			{
				good = true;
				break;
			}
		}
		if(!(ShipAtt[1].equalsIgnoreCase("A") || ShipAtt[1].equalsIgnoreCase("B") || ShipAtt[1].equalsIgnoreCase("C")))
		{
			good = false;
		}
		if((ShipAtt[0].toLowerCase().equals("Lanius".toLowerCase()) || ShipAtt[0].toLowerCase().equals("Crystal".toLowerCase())) && ShipAtt[0].toLowerCase().equals("C".toLowerCase()))
		{
			good = false;
		}
		return good;
	}
	public static class Ship
	{
		static Stack<Ship> Ships = new Stack<Ship>();
		String Name = "";
		int votes = 0;
		public Ship(String name)
		{
			Name = name;
		}
		public void setName(String name)
		{
			Name = name;
		}
		public static void newShip(String name)
		{
			Ships.push(new Ship(name));
		}
		public void addVote()
		{
			votes++;
		}
		public int getVotes()
		{
			return votes;
		}
		public static void delAll()
		{
			Ships.clear();
		}
	}
	public static void announce() throws IOException
	{
		if(votes>=1)
		{
			Main.chatMsg(votes+" Counted");
		}
	}
	static TimerTask taskPerformer;
	public static void setupTask()
	{
		taskPerformer = new TimerTask() 
		{
			@Override
			public void run() 
			{
				try {
					announce();
					votes = 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
}


