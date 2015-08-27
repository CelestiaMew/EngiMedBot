package blazebot;

import java.lang.reflect.Method;
import java.util.Stack;

public class Ticker implements Runnable{
	public Ticker(Object instance, Method method,int seconds, boolean lop){
		inst=instance;
		m=method;
		secs=seconds;
		loop = lop;
		t=new Thread(this);
		t.start();
	}
	public Ticker(Method method,int seconds, boolean lop){
		inst=null;
		m=method;
		secs=seconds;
		loop = lop;
		t=new Thread(this);
		t.start();
	}
	Thread t = null;
	public void stop()
	{
		stop=true;
	}
	public void run() 
	{
		while(true){
			try {
				Thread.sleep(secs*1000);
				if(stop)
					break;
				
				m.invoke(inst, new Object[]{});
				
				if(!loop)
					break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				CrashGUI G = new CrashGUI(e.toString()); G.printStackTrace(e.getStackTrace());
			}
			
		}
	}
	Method m = null;
	Object inst = null;
	boolean stat = false;
	int secs=-1;
	boolean loop = false;
	boolean stop = false;
}
