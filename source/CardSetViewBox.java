import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;

//The display for looking at a set of cards
//Put in its own object because the GUI classes started getting too complex
public class CardSetViewBox{
	CardSet set;
	String setName="";
	BufferedImage backgroundImage=null;//for native village and island
	int width=650;
	int height=250;
	int xpos=150;
	int ypos=240;
	int sep=100;
	int scroll=0;
	ClickableImage closeButton=null;
	ClickableImage leftScroll=null;
	ClickableImage rightScroll=null;
	ClickableImage background=null;
	CardSetViewBox(CardSet set,String setName){
		this.set=set;
		this.setName=setName;
	}
	
	void makeComponents(DominionGUI gui) {
		if(backgroundImage!=null) {
			background=new ClickableImage(backgroundImage,xpos,ypos,1);
			background.width=width;//streach it
			background.height=height;
			background.stretch=true;
			gui.clickables.add(background);
		}
		closeButton=new ClickableImage("no.png",xpos+width-32,ypos,0.5) {
			public void clicked(DominionGUI gui) {
				gui.closeCardSet();
			}
		};
		closeButton.type=ClickableImage.TYPE_CLOSE_SET;
		gui.clickables.add(closeButton);//directly add to array instead of sending message
		for(int a=0;a<set.cards.size();a++) {
			ClickableCard cc=new ClickableCard(set.cards.get(a),xpos+32+a*sep-scroll,ypos+50,0.2);
			gui.clickables.add(cc);
		}
		
	}
	
	void draw(DominionGUI gui,BlakeWindow window) {

		window.drawString(setName,xpos+20,ypos+20,new Font("Areal Black",Font.PLAIN,32),Color.RED);
		
	}
	
	
}