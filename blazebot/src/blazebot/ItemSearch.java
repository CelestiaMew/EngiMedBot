package blazebot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class ItemSearch {
	public ItemSearch()
	{
		BufferedReader br = null;
		
		try {
			//br = new BufferedReader(new FileReader("C:/Users/"+System.getProperty("user.name")+"/Dropbox/EngiMedBot/pg.html"));
			br = new BufferedReader(new InputStreamReader(new URL("http://platinumgod.co.uk/afterbirth").openStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String HTML="";
		String line="";
		try {
			while((line=br.readLine())!=null){
				HTML+=line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
		int index = 0;
		while((index=HTML.indexOf("<span>",index+6))!=-1){
			String section = HTML.substring(index+6, HTML.indexOf("</span>",index));
			//public Item(String title, int id, String pickup, String[] info, String[] types, String[] pools)
			Item item = new Item();
			
			if(contains(section,"<p class=\"item-title\">")>0)
				item.title=getContent(section,"<p class=\"item-title\">","</p>");
			if(contains(section,"<p class=\"r-itemid\">ItemID: ")>0)
				item.id=Integer.valueOf(getContent(section,"<p class=\"r-itemid\">ItemID: ","</p>"));
			if(contains(section,"<p class=\"pickup\">")>0)
				item.pickup=getContent(section,"<p class=\"pickup\">","</p>");
			if(contains(section,"<p>")>0)
				item.info=getContents(section,"<p>&#8226","</p>");
			if(contains(section,"<p>Type: ")>0)
				item.types=getContent(section,"<p>Type: ","</p>").split(", ");
			if(contains(section,"<p>Item Pool: ")>0)
				item.pools=getContent(section,"<p>Item Pool: ","</p>").split(", ");
			if(contains(section,"<p class=\"r-unlock\">")>0)
				item.unlock=getContent(section,"<p class=\"r-unlock\">","</p>");
			
		}
	}
	public Item searchFor(String regex){
		for(int i=0;i<Item.items.size();i++){
			Item item = Item.items.get(i);
			
			if(item.title.equalsIgnoreCase(regex)){
				return item;
			}
		}
		return null;
	}
	public static String getContent(String content, String tag, String endtag){
		return content.substring(content.indexOf(tag)+tag.length(), content.indexOf(endtag,content.indexOf(tag)+tag.length()));
	}
	public static int contains(String str, String regex){
		return str.split(regex).length-1;
	}
	public static String[] getContents(String content,String tag, String endtag){
		int index = 0;
		int i=0;
		String[] elements = new String[contains(content,tag)];
		while((index=content.indexOf(tag,index+tag.length()))!=-1){
			String section = content.substring(index+tag.length(), content.indexOf(endtag,index));
			elements[i]=section;
			i++;
		}
		return elements;
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
}
