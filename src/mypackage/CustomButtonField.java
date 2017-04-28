package mypackage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;


public class CustomButtonField extends Field 
{
	private String label;
	private int backgroundColor;
	private int foregroundColor;
	
	public CustomButtonField(String label, int foregroundColor,
			int backgroundColor, long style)
	{
		super(style);
		this.label = label;
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
	}

	public int getPreferredHeight()
	{
		return getFont().getHeight() + 8;
	}
	
	public int getPrefferedWidth()
	{
		return getFont().getAdvance(label) + 8;
	}

	protected void layout(int width, int height) 
	{
		setExtent(Math.min(width, getPreferredWidth()), Math.min
				(height, getPreferredHeight()));		
	}
	protected void paint(Graphics graphics) 
	{
		graphics.setColor(backgroundColor);
		graphics.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 12, 12);
		graphics.setColor(foregroundColor);
		graphics.drawText(label, 4, 4);
	}
	
	public boolean isFocusable()
	{
		return true;
	}
}

