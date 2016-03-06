package blazebot;

import javax.swing.JFrame;

public class CrashGUI extends JFrame
{
	JConsole console = new JConsole();
	public CrashGUI(String log)
	{
		setLayout(null);
		add(console);
		setBounds(0,0,700,500);
		console.setBounds(0, 0, 700, 500-getInsets().top-getInsets().bottom);
		console.init();
		console.print(log);
		setVisible(true);
	}
	void print(String thing)
	{
		console.print(thing);
	}
	public void printStackTrace(StackTraceElement[] elements)
	{
		for(int i = 0; i<elements.length; i++)
		{
			print(elements[i].getClassName()+"("+elements[i].getMethodName()+":"+elements[i].getLineNumber()+")");
		}
	}
}
