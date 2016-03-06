package blazebot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

public class StackUtils 
{
	public static void saveSA(String filename, Stack<String[]> stack)throws IOException{//this is for saving string array stacks instead of just string stacks okay?
		Stack<String> newstack = new Stack<String>();//so this is the first stack
		for(int i=0;i<stack.size();i++){//so what it does again is loop through whole stack
			String line = "";
			String[] arr = stack.get(i);//this gets the command in the form of {"!test","user","hello world"}
			for(int ii=0;ii<arr.length;ii++){
				line+=arr[ii];
				if(ii!=arr.length-1)
					line+="spch";//thats exactly what happens
			}
			newstack.push(line);//so now it reconstructs the stack but in these lines and then uses the other function to save it
		}
		saveS(filename,newstack);
	}
	public static String LoadString(String filename)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = "";
			String info = "";
			while(line!=null)
			{
				line = reader.readLine();
				info = info + line + "\n";
			}
			reader.close();
			return info;
		}
		catch (Exception e) 
		{
			//CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
		}
		return null;
	}
	public static void SaveString(String filename, String thing)
	{
		try
		{
			File file = new File(filename);
			file.createNewFile();
			FileOutputStream out;
			out = new FileOutputStream(filename);
			out.write(String.valueOf(thing).getBytes());
			out.close();
		} 
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public static void saveS(String filename, Stack<String> lines) throws IOException
	{
		PrintWriter pw = new PrintWriter(new File(filename));
		for(int i=0;i<lines.size();i++)
			pw.print(lines.get(i)+(i!=lines.size()-1?"\n":""));//okay so now we go up
		pw.flush();
		pw.close();
	}
	public static Stack<String[]> loadSA(String filename)throws IOException{
		Stack<String> in = loadS(filename);
		String line = null;
		Stack<String[]> arrs = new Stack<String[]>();
		for(int i = 0; i < in.size(); i++)
		{
			line=in.get(i);
			arrs.push(line.split("spch"));//nope they have alternate uses i forgot name
		}
		in.clear();
		return arrs;
	}
	public static Stack<String> loadS(String filename)throws IOException{
		BufferedReader bf = new BufferedReader(new FileReader(filename));
		String line = "";
		Stack<String> stack = new Stack<String>();
		String wholething = "";
		while((line=bf.readLine())!=null){
			stack.push(line);
			wholething+=line;
		}
		System.out.println("Loaded file "+filename+" at "+wholething.getBytes().length+" Bytes");
		bf.close();
		return stack;
	}
}
