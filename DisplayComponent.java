import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;

//For displaying images other than clickable cards
//First use for a symbol indicating on show revealed cards that a player is immune to attacks.

//I don't know if I will use this

class DisplayComponent{
	int xpos;
	int ypos;
	int width;
	int height;
	double scale=1;
	BufferedImage image;
	boolean clickable=false;
	int number=-1;
	protected String clientResponse=null;
	public Player player=null;
	
	public DisplayComponent(BufferedImage img,int xpos,int ypos,double scale){
		this.xpos=xpos;
		this.ypos=ypos;
		this.scale=scale;
		image=img;
		width=img.getWidth();
		height=img.getHeight();
	}
	
	public boolean check(BlakeWindow window,DominionGUI gui){
		//call every frame, but if one is true don't call the rest
		if(window.MouseClick && clickable){
			if(window.MouseX>=xpos && window.MouseY-30>=ypos && window.MouseX<xpos+(width*scale) && window.MouseY-30<ypos+(height*scale)){
				clicked(gui);
				return true;
			}
		}
		return false;
	}
	
	public void clicked(DominionGUI gui){
		//overwrite this for every card
	}
	
	public boolean checkHover(BlakeWindow window){
		return window.MouseX>=xpos && window.MouseY-30>=ypos && window.MouseX<xpos+(width*scale) && window.MouseY-30<ypos+(height*scale);
	}
	
	public void draw(DominionGUI gui,BlakeWindow window){
		if(image!=null)
			window.drawScaleImage(image,xpos,ypos,scale,scale);
	}
	

}