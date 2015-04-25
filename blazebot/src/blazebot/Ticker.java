package blazebot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;

public class Ticker implements Runnable{
	public Ticker(Object instance, Method method,int seconds, boolean lop){
		inst=instance;
		m=method;
		secs=seconds;
		loop = lop;
		tickers.push(this);
		t=new Thread(this);
		t.start();
	}
	public Ticker(Method method,int seconds, boolean lop){
		inst=null;
		m=method;
		secs=seconds;
		loop = lop;
		tickers.push(this);
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
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	Method m = null;
	Object inst = null;
	boolean stat = false;
	int secs=-1;
	boolean loop = false;
	boolean stop = false;
	public static Stack<Ticker> tickers = new Stack<Ticker>();
}
