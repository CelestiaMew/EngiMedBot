package blazebot;

import java.util.Stack;

import javax.swing.JOptionPane;

public class Item {
	public Item(){
		items.push(this);
	}
	String title="This item has no title", pickup="This item cannot be picked up",unlock="This item is default";
	String[] info={"No information available"}, types={"No Types available"}, pools={"No information on this item's pools"};
	int id=-1;
	public static Stack<Item> items = new Stack<Item>();
	public void debugDisplay(){
		JOptionPane.showMessageDialog(null, "Title: "+title+"\nID: "+id+"\npickup msg "+pickup+"\ninfo "+ItemSearch.combine(info, " ")+"\ntypes "+ItemSearch.combine(types, " ")+"\npools "+ItemSearch.combine(pools, " "));
	}
}
