import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.Font;
import java.awt.Color;
import java.awt.image.BufferedImage;


public class DominionGUI{
	Player player;
	Game theGame;
	BlakeWindow window;
	
	protected ArrayList<ClickableImage> clickables;
	
	Font F36;
	
	//String message="";
	
	boolean running=false;
	
	DominionGUI(Player player,Game theGame,BlakeWindow window){
		this.player=player; this.theGame=theGame; this.window=window;
		if(player!=null) player.gui=this;
		clickables=new ArrayList<ClickableImage>();
		F36 = new Font("Areal Black",Font.PLAIN,36);
	}
	
	boolean buyClicked=false;
	boolean endClicked=false;
	boolean doneClicked=false;//only used in client gui
	boolean reactClicked=false;
	boolean responseDone=false;
	boolean viewDoneClicked=false;
	
	public boolean drawingRevealBoxes=false;
	
	CardSetViewBox setViewBox=null;
	
	public void play(){
		running=true;
		while(running){
			
			checkAll();
			if(buyClicked){
				buyClicked=false;
				player.EnterBuyPhase();
			}
			if(endClicked){
				endClicked=false;
				player.EndTurn();
				player=player.getLeft();
				player.gui=this;
				player.StartTurn();
			}
			window.clearScreen(Color.BLACK);
			draw();
			window.sync(50);
		}
	}
	
	
	public ArrayList<Card> playerResponse(Player p){//Player is needed as an argument for overriding in HostGUI
		responseDone=false;
		if(p.responseType==Player.RESPONSE_CONFIRM_REVEALED)
			drawingRevealBoxes=true;
		refresh();
		JButton donebutton=new JButton("Done");
		window.getPanel().add(donebutton);
		donebutton.setBounds(window.getWidth()/2-40,50,80,26);
		donebutton.addActionListener(new MyListener(this){
			public void actionPerformed(ActionEvent e){
				if(gui.player.CheckRequirements(gui.getSelected(gui.player),gui.player.responseReq)){
					gui.responseDone=true;
					gui.player.awaitingResponse=false;
					((JButton)e.getSource()).setVisible(false);
				}
			}
		});
		window.revalidate();
		window.sync(50);
		while(responseDone==false){
			window.clearScreen(Color.BLACK);
			checkAll();
			draw();
			window.sync(50);
		}
		drawingRevealBoxes=false;
		ArrayList<Card> out= getSelected();
		return out;
	}
	

	
	protected ArrayList<Card> listA;//so the clickable cards can access the lists
	protected ArrayList<Card> listB;
	public ArrayList<Card> orderCards(ArrayList<Card> list,Player p){
		responseDone=false;
		listA=list;
		listB=new ArrayList<Card>();
		JButton donebutton=new JButton("Done");
		window.getPanel().add(donebutton);
		donebutton.addActionListener(new MyListener(this){
			public void actionPerformed(ActionEvent e){
				if(gui.listA.size()==0 || gui.listB.size()==0){
					if(gui.listB.size()==0) gui.listB=gui.listA;//click done without reordering
					gui.responseDone=true;
					gui.player.awaitingResponse=false;
					((JButton)e.getSource()).setVisible(false);
				}
			}
		});
		donebutton.setBounds(window.getWidth()/2-40,50,80,26);
		window.revalidate();
		window.sync(50);
		makeReorder();
		while(responseDone==false){
			window.clearScreen(Color.BLACK);
			checkAll();
			draw();
			window.sync(50);
		}
		return listB;
	}
	
	int response=0;
	JButton buttons[];
	public int multipleChoice(String question,String[] choices,Player p){
		responseDone=false;
		buttons=new JButton[choices.length];
		for(int x=0;x<choices.length;x++){
			buttons[x]=new JButton("Choice "+x);
			buttons[x].addActionListener(new MyListener(this,x){
				public void actionPerformed(ActionEvent e){
					gui.response=variable;
					responseDone=true;
					for(int y=0;y<gui.buttons.length;y++)
						gui.buttons[y].setVisible(false);
				}
			});
			window.getPanel().add(buttons[x]);
			buttons[x].setBounds(50,220+x*40,100,30);
		}
		while(responseDone==false){
			window.clearScreen(Color.BLACK);
			//checkAll();
			draw();
			window.drawString(80,150,question,F36,Color.WHITE);
			for(int x=0;x<choices.length;x++){
				window.drawString(150,200+x*40,choices[x],F36,Color.WHITE);
			}
			window.sync(50);
		}
		player.awaitingResponse=false;
		return response;
	}
	
	void makeReorder(){
		clickables.clear();
		for(int x=0;x<listA.size();x++){
			ClickableCard cc=new ClickableCard(listA.get(x),100+150*x,200,0.2){
				public void clicked(DominionGUI gui){
					gui.listB.add(card);
					gui.listA.remove(card);
					makeReorder();
				}
			};
			clickables.add(cc);
		}
		for(int x=0;x<listB.size();x++){
			ClickableCard cc=new ClickableCard(listB.get(x),100+150*x,450,0.2){
				public void clicked(DominionGUI gui){
					gui.listA.add(card);
					gui.listB.remove(card);
					makeReorder();
				}
			};
			clickables.add(cc);
		}
	}
	
	ArrayList<Card> getSelected(){
		ArrayList<Card> out=new ArrayList<Card>();
		for(int x=0;x<player.selectedCards.size();x++){
			out.add(player.selectedCards.get(x));
		}
		return out;
	}
	
	ArrayList<Card> getSelected(Player p){
		//to be overridden by HostGUI
		return getSelected();
	}
	
	public void startTurn(){
		makeButtons();
		refresh();
	}
	
	public void makeButtons(){
		window.clearGUI();
		if(theGame.screen==Game.SCREEN_GAME){
			//create the buttons needed for the turn
			//JPanel MainMenu=new JPanel();
			//MainMenu.setLayout(null);
			//MainMenu.setOpaque(false);
			JButton buyphase=new JButton("Buy Phase");
			buyphase.addActionListener(new MyListener(this){
				public void actionPerformed(ActionEvent e){
					if(gui.theGame.turnPlayer==gui.player){
						if(gui.player.phase==0 && gui.player.awaitingResponse==false){
							gui.buyClicked=true;
						}
					}
				}
			});
			buyphase.setBounds(window.getWidth()/2-110,10,100,26);
			window.getPanel().add(buyphase);
			JButton endphase=new JButton("End Turn");
			endphase.addActionListener(new MyListener(this){
				public void actionPerformed(ActionEvent e){
					if(gui.theGame.turnPlayer==gui.player && gui.player.awaitingResponse==false){
						gui.endClicked=true;
					}
				}
			});
			endphase.setBounds(window.getWidth()/2+10,10,100,26);
			window.getPanel().add(endphase);
			JButton menu=new JButton("Menu");
			menu.addActionListener(new MyListener(this){
				public void actionPerformed(ActionEvent e){
					gui.theGame.screen=Game.SCREEN_PAUSE;
					gui.makeButtons();
				}
			});
			
			window.getPanel().add(menu);
			//window.getPanel().add(MainMenu);
		}
		if(theGame.screen==Game.SCREEN_PAUSE){
			JPanel MainMenu=new JPanel();
			MainMenu.setOpaque(false);
			JButton resume=new JButton("Return to Game");
			resume.addActionListener(new MyListener(this){
				public void actionPerformed(ActionEvent e){
					gui.theGame.screen=Game.SCREEN_GAME;
					gui.makeButtons();
					
				}
			});
			MainMenu.add(resume);
			JButton reset=new JButton("Exit to Main Menu");
			reset.addActionListener(new MyListener(this){
				public void actionPerformed(ActionEvent e){
					gui.theGame.resetting=true;
				}
			});
			MainMenu.add(reset);
			JButton quit=new JButton("Quit Game");
			quit.addActionListener(new MyListener(this){
				public void actionPerformed(ActionEvent e){
					System.exit(0);
				}
			});
			MainMenu.add(quit);
			window.getPanel().add(MainMenu);
		}
		if(theGame.screen==Game.SCREEN_VICTORY){
			JPanel MainMenu=new JPanel();
			MainMenu.setOpaque(false);
			JButton reset=new JButton("Exit to Main Menu");
			reset.addActionListener(new MyListener(this){
				public void actionPerformed(ActionEvent e){
					gui.theGame.resetting=true;
				}
			});
			MainMenu.add(reset);
			JButton cont=new JButton("Keep Going");
			cont.addActionListener(new MyListener(this){
				public void actionPerformed(ActionEvent e){
					theGame.pilesForVictory+=1;
					theGame.screen=Game.SCREEN_GAME;
					gui.makeButtons();
					gui.refresh();
				}
			});
			MainMenu.add(cont);
			JButton quit=new JButton("Quit Game");
			quit.addActionListener(new MyListener(this){
				public void actionPerformed(ActionEvent e){
					System.exit(0);
				}
			});
			MainMenu.add(quit);
			window.getPanel().add(MainMenu);
		}
		window.revalidate();
	}
	
	public void startBuyPhase(){
		refresh();
	}
	
	public void endTurn(){
		refresh();		
	}
	
	public void refresh(){
		//not currently used for reorder
		clickables.clear();
		if(theGame.screen==Game.SCREEN_GAME){
			if(drawingRevealBoxes) {
				makePlayersRevealed(player,player.revealingToAll);
			}
			
			if(player.awaitingResponse){
				if(player.responseType==Player.RESPONSE_HAND){
					makeHand();
				}
				if(player.responseType==Player.RESPONSE_SUPPLY){
					makeSupply();
				}
				if(player.responseType==Player.RESPONSE_LIST){
					makeList(player.responseList);
				}
				if(player.responseType==Player.RESPONSE_CONFIRM) {
					makeUnclickableList(player.responseList);
				}
			}else{
				if(player.phase==Player.PHASE_BUY){
					makeSupply();
				}else{
					makeHand();
				}
			}
		}
	};
	
	void makeHand(){
		//makes clickable cards for cards in hand
		int count=player.hand.cards.size();
		if(count==0) return;
		int x1=200,x2=875;
		for(int x=0;x<count;x++){
			ClickableCard cc=null;
			int xpos=x1;
			if(count>1) xpos+=x*((x2-x1)/(count-1)); else xpos=(x1+x2)/2;
			int ypos=300;
			cc=new ClickableCard(player.hand.cards.get(x),xpos,ypos,0.25){
				public void clicked(DominionGUI gui){
					if(gui.player.awaitingResponse){
						if(gui.canSelect(this))
							selected=!selected;
					}else{
						if(card.canPlay(gui.player)){
							card.play(gui.player);
						}
					}
				}
			};
			if(player.awaitingResponse){
				if(canSelect(cc)==false)
					cc.darken=true;
			}else {
				cc.canZoom=true;
			}
			if(cc!=null) clickables.add(cc);
		}
	}
	
	
	void makeSupply(){
		for(int x=0;x<theGame.basicPiles.size();x++){
			if(theGame.basicPiles.get(x).pile.cards.size()>0){
				ClickableCard cc=new ClickableCard(theGame.basicPiles.get(x).pile.cards.get(0),20+x*140,40,0.2){
					public void clicked(DominionGUI gui){
						if(gui.canSelect(this)){
								selected=!selected;
						}else{
							if(gui.player.CanBuy(card.dominionCard)){
								gui.player.BuyCard(card.dominionCard);
							}
						}
					}
				};
				if(player.awaitingResponse){
					if(player.CheckRequirements(cc.card,player.responseReq)==false){
						cc.darken=true;
					}
				}else if(player.CanBuy(theGame.basicPiles.get(x))==false){
					cc.darken=true;
				}
				cc.number=theGame.basicPiles.get(x).pile.cards.size();
				
				clickables.add(cc);
			}
		}
		for(int x=0;x<theGame.actionPiles.size();x++){
			if(theGame.actionPiles.get(x).pile.cards.size()>0){
				Card card=theGame.actionPiles.get(x).pile.cards.get(0);
				int xpos=150+x*150;
				int ypos=220;
				if(x>=theGame.actionPiles.size()/2){
					ypos+=220;
					xpos-=150*(theGame.actionPiles.size()/2);
				};
				ClickableCard cc=new ClickableCard(card,xpos,ypos,0.25){
					public void clicked(DominionGUI gui){
						if(gui.canSelect(this)){
								selected=!selected;
						}else{
							if(gui.player.CanBuy(card.dominionCard)){
								gui.player.BuyCard(card.dominionCard);
							}
						}
					}
				};
				if(player.awaitingResponse){
						if(player.CheckRequirements(cc.card,player.responseReq)==false){
							cc.darken=true;
						}
					}else if(player.CanBuy(theGame.actionPiles.get(x))==false){
					cc.darken=true;
				}
				cc.number=theGame.actionPiles.get(x).pile.cards.size();
				clickables.add(cc);
				cc.canZoom=true;
				
			}
			
		}
	}
	
	
	void makeList(ArrayList<Card> list){
		//clickables.clear();
		int sep=150;
		if(list.size()>6) sep=100;
		if(list.size()>10) sep=50;
		for(int x=0;x<list.size();x++){
			ClickableCard cc=new ClickableCard(list.get(x),100+sep*x,200,0.2){
				public void clicked(DominionGUI gui){
					if(gui.canSelect(this)) {
						selected=!selected;
						
					}
				}
			};
			cc.canZoom=true;
			if(canSelect(cc)==false) cc.darken=true;
			clickables.add(cc);
		}
	}
	void makeUnclickableList(ArrayList<Card> list) {
		int sep=150;
		if(list.size()>6) sep=100;
		if(list.size()>10) sep=50;
		for(int x=0;x<list.size();x++){
			ClickableCard cc=new ClickableCard(list.get(x),100+sep*x,200,0.2);
			cc.canZoom=true;
			cc.clickable=false;
			clickables.add(cc);
		}
	}
	
	
	boolean canSelect(ClickableCard cc){
		if(player.awaitingResponse==false) return false;
		//if the card is not selected and the current set passes, you can not select it if it would make the set fail
		if(cc.selected==false){
			if(player.CheckRequirements(cc.card,player.responseReq)==false) return false;
			if(player.CheckRequirements(getSelected(),player.responseReq)){
				ArrayList<Card> temp=((ArrayList<Card>)getSelected().clone());// "unsafe operation"
				temp.add(cc.card);
				if(player.CheckRequirements(temp,player.responseReq)==false) return false;
			}
		}
		return true;
	}
	
	
	/*public void reactionChance(Player p) {
		
	}*/
	
	
	//Drawing functions
	void drawPlayedCards(){
		for(int x=0;x<player.playedCards.cards.size();x++){
			int xpos=120+x*140;
			int ypos=150;
			window.drawScaleImage(player.playedCards.cards.get(x).dominionCard.getImage(),xpos,ypos,0.2,0.2);
		}
		//draw the duration cards
		for(int x=0;x<player.durations.cards.size();x++){
			int xpos=200+x*140;
			int ypos=560;
			window.drawScaleImage(player.durations.cards.get(x).dominionCard.getImage(),xpos,ypos,0.15,0.15);
			//need to draw the number of counters
		}
		//draw the deck and discard
		drawDecks();	
	}
	

	
	
	public void showPlayersRevealed(Player p,boolean toAll) {
		//shows revealed cards until player p clicks done
		//if toAll is true then all players see it
		p.revealingToAll=toAll;
		
		p.awaitingResponse=true;
		p.responseType=Player.RESPONSE_CONFIRM_REVEALED;
		playerResponse(p);
		for(Player screenPlayer:theGame.players) {//the player whose screen the card is drawn on
			if(screenPlayer==p || toAll) {
				if(screenPlayer==player) {
					drawingRevealBoxes=false;
				}else {
					try {
						theGame.settings.hostObject.output(screenPlayer.playerNum-1,"clear");
					}catch(NullPointerException ex) {
						System.out.println("Error in showPlayersRevealed.\nIf this is in offline mode then it may be caused by a card that should only be used online.");
						throw ex;
					}
				}
			}
		}
	}
	
	public void makePlayersRevealed(Player p,boolean toAll) {
		//constructs the clickable cards
		for(Player screenPlayer:theGame.players) {//the player whose screen the card is drawn on
			if(screenPlayer==p || toAll) {
				if(screenPlayer==player) {
					clickables.clear(); 
					drawingRevealBoxes=true;
				}else {
					try {
						theGame.settings.hostObject.output(screenPlayer.playerNum-1,"refresh");//this shouldn't be called except when this is a hostgui
						theGame.settings.hostObject.output(screenPlayer.playerNum-1,"boxes on");
					}catch(NullPointerException ex) {
						System.out.println("Error in makePlayersRevealed.\nIf this is in offline mode then it may be caused by a card that should only be used online.");
						throw ex;
					}
				}
				for(int x=0;x<theGame.players.size();x++) {
					int bx=10+x*300;
					int count=theGame.players.get(x).revealedCards.size();
					int x1=bx+5;
					int x2=bx+180;
					for(int a=0;a<count;a++){
						int xpos=x1;
						if(count>1) xpos+=a*((x2-x1)/(count-1)); else xpos=(x1+x2)/2;
						int ypos=50;
						//window.drawScaleImage(P.get(x).revealedCards.get(a).dominionCard.getImage(),xpos,ypos,0.15,0.15);
						ClickableCard cc=new ClickableCard(theGame.players.get(x).revealedCards.get(a),xpos,ypos,0.15);
						addClickableCard(cc,screenPlayer.playerNum);
					}
				}
			}
		}
	}
	
	protected void drawRevealBoxes(int playerCount,int highlightedPlayer) {//New method
		for(int x=0;x<playerCount;x++) {
			int bx=10+x*300;
			window.drawRect(bx,0,280,300,Color.BLACK);
			window.drawOutlineRect(bx,0,280,300,Color.WHITE);
			window.drawString("Player "+(playerCount+1),bx,-10,F36,x==highlightedPlayer?new Color(255,255,128):Color.WHITE);
		}
	}
	
	
	
	public void drawDecks(){
		//draw the deck and discard
		if(player.discard.cards.size()>0)
			window.drawScaleImage(player.discard.cards.get(0).dominionCard.getImage(),20,430,0.2,0.2);
		window.drawString(Integer.toString(player.discard.cards.size()),30,450,F36,Color.RED);
		if(player.deck.cards.size()>0)
			window.drawScaleImage(DominionCard.DefaultImage(),1060,430,0.2,0.2);
		window.drawString(Integer.toString(player.deck.cards.size()),1070,450,F36,Color.RED);
	}
	
	public void drawZoom(int xpos,int ypos,DominionCard card){
		//draw the lower half of the card so you can read the text
		//assume 575x906 card picture
		BufferedImage img=card.getImage();
		int w=230;
		int h=180;
		window.drawCroppedImage(img, xpos, ypos, xpos+w, ypos+h, 0, 480, 560, 890);
		
	}
	
	public void checkAll(){
		for(int x=clickables.size()-1;x>=0;x--){
			boolean clicked=clickables.get(x).check(window,this);
			if(clicked) break;
		}
	}
	
	public void draw(){
		if(theGame.screen==Game.SCREEN_GAME){
			if(drawingRevealBoxes)
				drawRevealBoxes(theGame.players.size(),player.playerNum);
			if(player.awaitingResponse==false && player.phase!=Player.PHASE_BUY){
				drawPlayedCards();
			}else{
				drawDecks();
			}
			for(int x=0;x<clickables.size();x++){//draw unclickable cards before clickable cards
				if(clickables.get(x).clickable==false)
					clickables.get(x).draw(this,window);
			}
			for(int x=0;x<clickables.size();x++){
				if(clickables.get(x).clickable==true)
					clickables.get(x).draw(this,window);
			}
			if(player.awaitingResponse){
				window.drawString(player.message,50,150,F36,Color.WHITE);
			}
			window.drawString("Player "+(theGame.turnPlayer.playerNum+1),0,-15,F36,Color.WHITE);
			window.drawString("Actions: "+theGame.turnPlayer.actions+"  Coins: "+theGame.turnPlayer.coins+"  Buys: "+theGame.turnPlayer.buys+"  VP: "+theGame.turnPlayer.GetScore(),0,20,F36,Color.WHITE);
		}
		if(theGame.screen==Game.SCREEN_VICTORY){
			for(int x=0;x<theGame.players.size();x++){
				window.drawString("Player "+(x+1)+"    "+theGame.players.get(x).GetScore(),200,300+100*x,F36,Color.WHITE);
			}
		}
		/*if(theGame.resetting){
			theGame.returnToMainMenu();
		}*/
	}
	
	public void makeReactButton() {
		//use in host and in client
		JButton react=new JButton("Reaction!");
		react.addActionListener(new MyListener(this) {
			public void actionPerformed(ActionEvent e){
				gui.reactClicked=true;
			}
		});
		window.getPanel().setLayout(null);
		react.setBounds(500,300,100,30);
		window.getPanel().add(react);
		
		window.revalidate();
	}
	
	
	public void addClickableCard(ClickableCard cc,int playerNum) {
		//overridden by host and client guis
		if(playerNum==player.playerNum)
			clickables.add(cc);
	}
	
	public void addClickableImage(ClickableImage cc,int playerNum) {
		//overridden by host and client guis
		if(playerNum==player.playerNum)
			clickables.add(cc);
	}
	
	public void reactionChance(Player p) {
		
	}
	
	public void viewList(Player p, ArrayList<Card> list) {
		//allow the player to view a list of cards
		System.out.println("View List Called");
		p.currentlyViewing=list;
		JButton done=new JButton("Done");
		done.addActionListener(new MyListener(this) {
			public void actionPerformed(ActionEvent e){
				//gui.viewDoneClicked=true;
				p.currentlyViewing=null;
				done.setVisible(false);
			}
		});
		done.setBounds(window.getWidth()/2 - 40,(int)(window.getHeight()*0.25-80),80,28);
		window.getPanel().add(done);
	}
	
	public void makeView(Player p) {
		//draw the set of cards for a player to view
		System.out.println("Make View Called");
		if(p.currentlyViewing==null) return;//shouldn't have been called in the first place
		int size=p.currentlyViewing.size();
		//this does not allow the viewed cards to be selected, it is only for viewing
		int rows=1;
		int col=size;
		if(size>=8 && size<=24) {
			rows=2;
			col=(int) Math.ceil(size/2.0);
		}
		if(size>24) {
			rows=(int)Math.ceil(size/12.0);
			col=12;
		}
		for(int a=0;a<size;a++) {
			ClickableCard cc=new ClickableCard(p.currentlyViewing.get(a),(int) ((window.getWidth()*(0.75*(a%col)/((double)col)))+window.getWidth()*0.125),(int) (window.getHeight()*0.25+100*(a/col)),0.2);
			addClickableCard(cc,p.playerNum);
		}
	}
	
	void viewCardSet(CardSet set,String name) {
		//This player can look at a set of cards outside of selecting cards.
		//Client and Host should have th same code for this
		//Created after I decided to abandon hotseat mode
		setViewBox=new CardSetViewBox(set,name);
		//Background images for special sets
		if(name.equals("Native Village")) {
			setViewBox.backgroundImage=ClickableImage.findImage("Native Village Icon.jpg");
		}
		if(name.equals("Island")) {
			setViewBox.backgroundImage=ClickableImage.findImage("Island Icon.jpg");
		}
	}
	
	void makeSetViewBox() {
		if(setViewBox!=null) {
			setViewBox.makeComponents(this);
		}
	}
	
	void closeCardSet() {
		setViewBox=null;
		refresh();
	}
	
	
	class MyListener implements ActionListener, ChangeListener{
		public int variable=0;
		public DominionGUI gui=null;
		public Player player=null;
		MyListener(){
		}
		MyListener(int var){
			variable=var;
		}
		MyListener(DominionGUI gui){
			this.gui=gui;
		}
		MyListener(DominionGUI gui,int var){
			variable=var;
			this.gui=gui;
		}
		MyListener(Player p){
			player=p;
		}
		MyListener(DominionGUI gui,Player p){
			this.gui=gui; player=p;
		}
	
		public void actionPerformed(ActionEvent e){
		}
		public void stateChanged(ChangeEvent e){
		}
	}
}