import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.util.ArrayList;

//import java.io.File;
//import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;

//Any image that obstructs you from clicking on something underneath it

public class ClickableImage{
	BufferedImage image;
	String imageFileName=null;
	int xpos;
	int ypos;
	int width;
	int height;
	double scale=1;
	boolean darken=false;
	protected boolean isCard=false;//is a ClickableCard which extends this
	public boolean clickable=true;
	public CardSet linkedSet=null;
	public boolean printSize=false;
	public int type=0;
	boolean stretch=false;
	String spName="x";//to be passed onto a menu that opens by clicking on it
	
	final static int TYPE_NONE=0;
	final static int TYPE_CARD=1;//used for cards you can click on to play or select them
	final static int TYPE_SET=2;//discard pile, native village
	final static int TYPE_HIDDEN_SET=3;//draw pile
	final static int TYPE_CLOSE_SET=4;//button to close viewing a set
	final static int TYPE_SCROLL_LEFT=5;
	final static int TYPE_SCROLL_RIGHT=6;
	
	private static ArrayList<LoadedImage> LoadedImages=new ArrayList<>();//so it only loads each image once and stores it in memory (for non cards)
	
	ClickableImage(BufferedImage image,int xpos,int ypos,double scale){
		this.image=image;
		this.xpos=xpos;
		this.ypos=ypos;
		this.scale=scale;
		width=image.getWidth();
		height=image.getHeight();
	}
	
	ClickableImage(String imageName,int xpos,int ypos,double scale){
		imageFileName=imageName;
		image=findImage(imageName);
		this.xpos=xpos;
		this.ypos=ypos;
		this.scale=scale;
		width=image.getWidth();
		height=image.getHeight();
	}
	public boolean check(BlakeWindow window,DominionGUI gui){
		//call every frame, but if one is true don't call the rest
		if(window.MouseClick){
			if(window.MouseX>=xpos && window.MouseY-30>=ypos && window.MouseX<xpos+(width*scale) && window.MouseY-30<ypos+(height*scale)){
				clicked(gui);
				return true;
			}
		}
		return false;
	}
	
	public boolean checkHover(BlakeWindow window){
		return window.MouseX>=xpos && window.MouseY-30>=ypos && window.MouseX<xpos+(width*scale) && window.MouseY-30<ypos+(height*scale);
	}
	
	public void clicked(DominionGUI gui){
		//overwrite this for each use
		System.out.println("clicked image");
		if(type==TYPE_SET) {
			if(linkedSet!=null) {
				gui.viewCardSet(linkedSet,spName);
			}
			gui.refresh();
		}
	}
	
	public void draw(DominionGUI gui,BlakeWindow window){
		if(stretch)
			window.drawScaleImageFit(image,xpos,ypos,width,height);
		else
			window.drawScaleImage(image,xpos,ypos,scale,scale);
		if(printSize && linkedSet!=null) {
			window.drawString(Integer.toString(linkedSet.cards.size()),xpos+5,ypos+10,new Font("Areal Black",Font.PLAIN,28),Color.YELLOW);
		}
	}
	
	
	public String getCode() {
		String out="A:";//b
		out+=type+" ";//arr[0]
		if(imageFileName!=null) {
			out+=imageFileName.replace(' ','_')+" ";//arr[1]
		}else out+="error ";
		out+=Integer.toString(xpos)+" ";//arr[2]
		out+=Integer.toString(ypos)+" ";//arr[3]
		out+=Double.toString(scale)+" ";//arr[4]
		out+=printSize?"1 ":"0 ";//arr[5]
		out+=(linkedSet!=null)?"1 ":"0 ";//arr[6]
		out+=spName.replace(' ','_')+" ";//arr[7]
		if(linkedSet!=null) {
			for(Card c:linkedSet.cards) {
				out+=Integer.toString(c.dominionCard.index)+",";//arr[8]
			}
		}
		return out;
	}
	
	public static ClickableImage makeFromCode(String code) {
		try {
			String[] arr=splitCode(code);
			ClickableImage ci=new ClickableImage(arr[1].replace('_',' '),Integer.parseInt(arr[2]),Integer.parseInt(arr[3]),Double.parseDouble(arr[4]));
			ci.type=Integer.parseInt(arr[0]);
			ci.printSize=arr[5].equals("1");
			ci.spName=arr[7].replace('_',' ');
			if(arr[6].equals("1")) {
				ci.linkedSet=new CardSet();
				String extra=arr[8];
				while(extra.indexOf(',')>=0) {//the last one has a comma after it too
					int id=Integer.parseInt(extra.substring(0,extra.indexOf(',')));
					if(id<CardLibrary.AllCards.size())
						ci.linkedSet.addToTop(new Card(CardLibrary.AllCards.get(id)));
					extra=extra.substring(extra.indexOf(',')+1);
				}
			}
			return ci;		
		}catch(Exception ex) {
			System.out.println("Bad input for clickable image "+code);
			ex.printStackTrace();
			return null;
		}
	}
	
	private static String[] splitCode(String code) {
		String[] out=new String[9];
		code=code.substring(2);
		int n=0;
		while(code.indexOf(' ')>=0 && n<8) {
			out[n]=code.substring(0,code.indexOf(' '));
			code=code.substring(code.indexOf(' ')+1);
			n++;
		}
		out[8]=code;
		return out;
	}
	
	static BufferedImage findImage(String name) {
		//So that each image only has to be loaded once
		for(int a=0;a<LoadedImages.size();a++) {
			if(name.equals(LoadedImages.get(a).name)) {
				return LoadedImages.get(a).img;
			}
		}
		BufferedImage image=null;
		try{
			image=ImageIO.read(new File("images/"+name));
		}catch(IOException ex){
			System.out.println("\nImage not found, "+name);
			image=DominionCard.DefaultImage();
		}
		LoadedImages.add(new LoadedImage(name,image));
		return image;
	}
	
	
}

class LoadedImage{
	BufferedImage img;
	String name;
	LoadedImage(String name,BufferedImage img){
		this.name=name; this.img=img;
	}
}