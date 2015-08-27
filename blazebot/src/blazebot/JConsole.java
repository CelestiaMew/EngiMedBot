package blazebot;

import java.awt.Font;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.JTextArea;

public class JConsole extends JTextArea
{
	String[] buf;
	void init()
	{
		this.setEditable(false);
		Font originalFont = (Font)this.getClientProperty("originalfont");
		if (originalFont == null) { 
	        originalFont = this.getFont();
	        this.putClientProperty("originalfont", originalFont);
	    }
		int size = (int) Math.ceil(this.getHeight()/this.getFontMetrics(originalFont).getHeight());
		buf = new String[size];
		for(int i = 0; i<buf.length-1; i++)
		{
			buf[i] = "";
		}
		
	}
	public void clear()
	{
		for(int i = 0; i<buf.length-1; i++)
		{
			buf[i] = "";
		}
	}
	public void print(String thing)
	{
		buf = scroll(buf);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Detroit"));
		String date = (cal.get(Calendar.HOUR_OF_DAY))+" : "+cal.get(Calendar.MINUTE);
		buf[0]="("+date+") "+thing;
		if(this.getFontMetrics((Font)this.getClientProperty("originalfont")).stringWidth(buf[0])>this.getWidth())
		{
			int offset = 0;
			int offset2 = 0;
			int buflength = buf[0].length();
			while(this.getFontMetrics((Font)this.getClientProperty("originalfont")).stringWidth(buf[0])>this.getWidth()-15)
			{
				if(this.getFontMetrics((Font)this.getClientProperty("originalfont")).stringWidth(buf[0].substring(0, offset2))>this.getWidth()-15)
				{
					String buffer = buf[0];
					buf[0] = buf[0].substring(0, offset2);
					buf = scroll(buf);
					buf[0] = buffer.substring(offset2);
					offset = offset2+1;
					offset2 = 0;
				}
				offset2++;
			}
		}
		String compiled = "";
		for(int i = buf.length-1; i>=0; i--)
		{
			compiled = compiled+buf[i]+"\n";
		}
		this.setText(compiled);
	}
	String[] scroll(String[] buf)
	{
		buf[buf.length-1] = "";
		for(int i = buf.length-2; i>=0; i--)
		{
			buf[i+1] = buf[i];
		}
		return buf;
	}
}
