import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;

public class ClickableCard extends ClickableImage{
	//object used in GUI
	Card card;
	boolean selected=false;
	int xpos;
	int ypos;
	int width;
	int height;
	double scale=1;
	boolean darken=false;
	private static BufferedImage selector=null;
	private static BufferedImage darkener=null;
	int number=-1;
	boolean canZoom=false;
	protected String clientResponse=null;
	
	public boolean faceDown=false;
	public Player player=null;//so it can be accessed when overriding the clicked method 
	
	ClickableCard(Card card,int xpos,int ypos,double scale){
		super(card.dominionCard.getImage(),xpos,ypos,scale);
		this.card=card;
		this.xpos=xpos;
		this.ypos=ypos;
		this.scale=scale;
		width=card.dominionCard.getImage().getWidth();
		height=card.dominionCard.getImage().getHeight();
		isCard=true;
		type=1;
		linkedSet=card.overlay;
	}
	
	static BufferedImage SelectorImage(){
		if(selector==null){
			try{
				selector=ImageIO.read(new File("images/selected.png"));
			}catch(Exception ex){
				System.out.println("Image not found:  images/selected.png");
			}
		}
		return selector;
	}
	static BufferedImage DarkenerImage(){
		if(darkener==null){
			try{
				darkener=ImageIO.read(new File("images/darken.png"));
			}catch(Exception ex){
				System.out.println("Image not found:  images/darken.png");
			}
		}
		return darkener;
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
	
	public boolean checkHover(BlakeWindow window){
		return window.MouseX>=xpos && window.MouseY-30>=ypos && window.MouseX<xpos+(width*scale) && window.MouseY-30<ypos+(height*scale);
	}
	
	public void clicked(DominionGUI gui){
		//overwrite this for every card
	}
	
	public void draw(DominionGUI gui,BlakeWindow window){
		BufferedImage img=DominionCard.DefaultImage();
		for(int a=card.overlay.cards.size()-1;a>=0;a--) {//draw attached cards under the main card
			window.drawScaleImage(card.overlay.cards.get(a).dominionCard.getImage(),(int)(xpos+(50*scale*(a+1))),(int)(ypos-(120*scale*(a+1))),scale,scale);
		}
		if(faceDown==false && card!=null) img=card.dominionCard.getImage();
		window.drawScaleImage(img,xpos,ypos,scale,scale);
		if(darken) window.drawScaleImage(DarkenerImage(),xpos,ypos,scale,scale);
		if(selected)
			window.drawScaleImage(SelectorImage(),xpos+20,ypos+20,scale*3,scale*3);
		if(number>=0){
			window.drawString(Integer.toString(number),xpos+10,ypos+20,new Font("Areal Black",Font.PLAIN,28),Color.RED);
		}
		if(card.getCost(gui.player)!=card.dominionCard.cost){
			window.drawString(Integer.toString(card.getCost(gui.player)),xpos,(int)(ypos+700*scale),new Font("Areal Black",Font.BOLD,28),new Color(245,200,30));
		}
		if(printSize && linkedSet!=null) {
			window.drawString(Integer.toString(linkedSet.cards.size()),xpos+5,ypos+10,new Font("Areal Black",Font.PLAIN,28),Color.YELLOW);
		}
		//zoom in on the card so you can read the text
		if(checkHover(window) && canZoom){
			gui.drawZoom(950,20,card.dominionCard);
		}
	}
	
	public String getCode(int id,boolean faceDown) {
		String out="C:";
		out+="X:"+Integer.toString(id)+" ";
		out+=Integer.toString(card.dominionCard.index)+" ";//name.replace(' ','_')+" ";
		out+=Integer.toString(xpos)+" ";
		out+=Integer.toString(ypos)+" ";
		out+=Double.toString(scale)+" ";
		out+=Integer.toString(number)+" ";
		out+=selected?"1 ":"0 ";
		out+=darken?"1 ":"0 ";
		out+=canZoom?"1 ":"0 ";
		out+=(faceDown||this.faceDown)?"1 ":"0 ";
		out+=clickable?"1 ":"0 ";
		for(Card c:card.overlay.cards) {
			out+=Integer.toString(c.dominionCard.index)+",";
		}
		return out;
	}
	
	public static ClickableCard makeFromCode(String code) {
		try {
			String[] arr=splitCode(code);
			Card temp=new Card(CardLibrary.AllCards.get(Integer.parseInt(arr[1])));//findCard(arr[1].replace('_',' ')));
			ClickableCard out=new ClickableCard(temp,Integer.parseInt(arr[2]),Integer.parseInt(arr[3]),Double.parseDouble(arr[4])) {
				public void clicked(DominionGUI gui) {
					gui.theGame.settings.clientObject.output(clientResponse);
				}
			};
			out.number=Integer.parseInt(arr[5]);
			out.selected=arr[6].equals("1");
			out.darken=arr[7].equals("1");
			out.canZoom=arr[8].equals("1");
			out.faceDown=arr[9].equals("1");
			out.clickable=arr[10].equals("1");
			out.clientResponse=arr[0];
			String extra=arr[11].trim();//overlay info
			while(extra.indexOf(',')>=0) {//the last one has a comma after it too
				int id=Integer.parseInt(extra.substring(0,extra.indexOf(',')));
				if(id<CardLibrary.AllCards.size())
					out.card.overlay.cards.add(new Card(CardLibrary.AllCards.get(id)));
				extra=extra.substring(extra.indexOf(',')+1);
			}
			return out;
		}catch(Exception ex) {
			System.out.println("Bad input for clickable card "+code);
			ex.printStackTrace();
			return null;
		}
	}
	
	private static String[] splitCode(String code) {
		String[] out=new String[12];
		code=code.substring(2);
		int n=0;
		while(code.indexOf(' ')>=0 && n<11) {
			out[n]=code.substring(0,code.indexOf(' '));
			code=code.substring(code.indexOf(' ')+1);
			n++;
		}
		out[11]=code;
		return out;
	}
	
}