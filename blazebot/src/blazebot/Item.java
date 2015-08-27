package blazebot;

import java.util.Stack;

import javax.swing.JOptionPane;

public class Item {
	public Item(){
		items.push(this);
	}
	public String title="This item has no title";
	public String pickup="This item cannot be picked up";
	public String unlock="This item is default";
	public String[] info={"No information available"};
	public String[] types={"No Types available"};
	public String[] pools={"No information on this item's pools"};
	public int id=-1;
	public static Stack<Item> items = new Stack<Item>();
	public void debugDisplay(){
		JOptionPane.showMessageDialog(null, "Title: "+title+"\nID: "+id+"\npickup msg "+pickup+"\ninfo "+ItemSearch.combine(info, " ")+"\ntypes "+ItemSearch.combine(types, " ")+"\npools "+ItemSearch.combine(pools, " "));
	}
}
