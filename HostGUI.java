import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;




public class HostGUI extends DominionGUI implements Runnable{

	HostGUI(Player player, Game theGame, BlakeWindow window) {
		super(player, theGame, window);
		// TODO Auto-generated constructor stub
		
	}
	
	ArrayList<ClickableCard> clientClickables=new ArrayList<>();//holds all clickables sent to other players
	ArrayList<Integer> clientClickableIDs=new ArrayList<>();//holds the numbers that identify which card a client clicked on
	int nextClickableID=0;
	protected Thread readerThread=null;
	private boolean pauseReader=false;
	private long reactionStartTime;
	protected boolean awaitingReaction=false;
	protected boolean someoneReacting=false;
	private Player attacker=null;//so that the run function can access this from the reactionChance function
	
	public void play() {
		running=true;
		readerThread=new Thread(this);
		readerThread.start();
		while(running){
			window.clearScreen(Color.BLACK);
			
			checkAll();//needs to stay in the same thread as the window's updating of the mouse
			
			if(theGame.turnPlayer==player) {
				if(buyClicked) {
					buyClicked=false;
					player.EnterBuyPhase();
					refresh();
				}
				if(endClicked) {
					endClicked=false;
					nextTurn();
				}
			}
			
			draw();
			window.sync(50);
		}
	}
	
	
	public void run() {
		//processes responses from the clients
		while(running) {
			if(pauseReader==false) {
				for(int a=0;a<theGame.settings.clientList.length;a++) {
					ClientConnection con=theGame.settings.clientList[a];
					String str=con.getNext();
					while(str!=null) {
						Player p=theGame.players.get(a+1);
						processInput(str,p);
						str=con.getNext();
					}
				}
			}
			Thread.yield();
		}
	}
	
	
	public synchronized void processInput(String str,Player p) {
		try {
			if(str.substring(0,2).equals("X:")) {//clicked on a card
				int id=Integer.parseInt(str.substring(2));
				int slot=-1;
				for(int x=0;x<clientClickableIDs.size();x++) {
					if(clientClickableIDs.get(x).intValue()==id) {
						slot=x; break;
					}
				}
				if(slot>=0) {//apply to the card as if the card was clicked on this screen
					clientClickables.get(slot).clicked(this);
					System.out.println("Clicking");
					refresh();
				}
			}
			if(str.substring(0,2).equals("R:")) {
				p.awaitingResponse=false;
				p.responseChoice=Integer.parseInt(str.substring(2));
			}
			if(str.substring(0,2).equals("B:")) {
				if(theGame.turnPlayer==p) {
					if(str.substring(2).equals("buy")) {
						System.out.println("buy button clicked");
						theGame.turnPlayer.EnterBuyPhase();
						refresh();
					}
					if(str.substring(2).equals("end")) {
						nextTurn();
					}
				}
				if(str.substring(2).equals("done")) {
					if(p.responseType==Player.RESPONSE_ORDER) {
						if(p.selectedCards.size()==p.responseList.size() || p.selectedCards.size()==0) {
							if(p.selectedCards.size()==0) {//allow clicking done to keep same order
								for(Card c:p.responseList)
									p.selectedCards.add(c);
							}
							p.awaitingResponse=false;
							theGame.settings.hostObject.output(p.playerNum-1,"guirefresh");
							theGame.settings.hostObject.output(p.playerNum-1,"M:");
						}
					}else if(someoneReacting) {
						//react with the selected cards
						p.awaitingResponse=false;
						ArrayList<Card> toPlay=new ArrayList<Card>();//copy here because selected can change during the resolution
						for(int a=0;a<p.selectedCards.size();a++) {
							toPlay.add(p.selectedCards.get(a));
						}
						p.clearSelected();
						for(int a=0;a<toPlay.size();a++) {
							if(toPlay.get(a).dominionCard.canReact(p,toPlay.get(a)))
								toPlay.get(a).dominionCard.react(p,toPlay.get(a),attacker);
						}
						p.awaitingReact=false;
					}else {
						if(p.CheckRequirements(getSelected(p),p.responseReq)) {
							p.awaitingResponse=false;
							theGame.settings.hostObject.output(p.playerNum-1,"guirefresh");
							theGame.settings.hostObject.output(p.playerNum-1,"M:");
						}
					}
				}
				if(str.substring(2).equals("react")) {
					if(p.responseType==Player.RESPONSE_REACTION) {
						someoneReacting=true;
						p.selectReaction(attacker);
					}
				}
			}
			if(str.equals("requestrefresh")) {
				refresh();
			}
		}catch(StringIndexOutOfBoundsException ex) {}
	}
	
	public void refresh() {
		clickables.clear();
		clientClickableIDs.clear();//might not want to clear this because the client could still click on the old ones before the message reaches
		clientClickables.clear();
		theGame.settings.hostObject.outputAll("refresh");
		//draw deck and discard
		
		//output price change
		if(theGame.turnPlayer.costChange!=0) theGame.settings.hostObject.outputAll("costChange "+theGame.turnPlayer.costChange);
		
		//your deck and discard (moved down during opponent's turn)
		for(int a=0;a<theGame.settings.players;a++) {
			Player p=theGame.players.get(a);
			if(theGame.turnPlayer!=theGame.players.get(a)) {//not your turn
				if(theGame.turnPlayer.discard.cards.size()>0) {
					ClickableCard cc=new ClickableCard(theGame.turnPlayer.discard.cards.get(0),50,330,0.2);
					cc.number=theGame.turnPlayer.discard.cards.size();
					cc.clickable=false;
					addClickableCard(cc,a);
				}
				if(theGame.turnPlayer.deck.cards.size()>0) {
					ClickableCard cc=new ClickableCard(theGame.turnPlayer.deck.cards.get(0),1030,330,0.2);
					cc.number=theGame.turnPlayer.deck.cards.size();
					cc.faceDown=true;
					cc.clickable=false;
					addClickableCard(cc,a);
				}
			}
			if(p.discard.cards.size()>0) {
				ClickableCard cc=new ClickableCard(p.discard.cards.get(0),20,470,0.2) {
					public void clicked(DominionGUI gui) {//clicked on discard pile
						if(p.awaitingResponse==false)
							gui.viewList(p,p.discard.cards);
					}
				};
				cc.number=p.discard.cards.size();
				//cc.clickable=false;
				addClickableCard(cc,a);
			}
			if(p.deck.cards.size()>0) {
				ClickableCard cc=new ClickableCard(p.deck.cards.get(0),1060,470,0.2);
				cc.number=p.deck.cards.size();
				cc.faceDown=true;
				cc.clickable=false;
				addClickableCard(cc,a);
			}
			makeDurations(p);
			makeExtraPiles(p);
		}
		
		if(theGame.turnPlayer.phase==Player.PHASE_BUY) {
			makeSupply(theGame.turnPlayer.playerNum);
		}else {
			makePlayed(); //played should be hidden when you are making a choice
		}
		for(int a=0;a<theGame.settings.players;a++) {
			//Message
			if(a>0)theGame.settings.hostObject.output(a-1,"M:"+theGame.players.get(a).message);
			//Hand
			if(theGame.turnPlayer==theGame.players.get(a)) {
				//the turn players hand is sometimes not drawn
				if(theGame.turnPlayer.phase!=Player.PHASE_BUY && (theGame.turnPlayer.awaitingResponse==false||theGame.turnPlayer.responseType==Player.RESPONSE_HAND)) {
					makeHand(a);
				}
			}else {
				//non turn players hand is always drawn
				makeHand(a);
			}
		}
		
		theGame.settings.hostObject.outputAll("I:Actions: "+theGame.turnPlayer.actions+"  Coins: "+theGame.turnPlayer.coins+"  Buys: "+theGame.turnPlayer.buys+"  VP: "+theGame.turnPlayer.GetScore());
		
		
		
		//draw stuff for choices
		for(int a=0;a<theGame.players.size();a++) {
			Player p=theGame.players.get(a);
			if(p.awaitingResponse) {
				p.currentlyViewing=null;
				switch(p.responseType) {
					case Player.RESPONSE_HAND:
						makeHand(p.playerNum);
					break;
					case Player.RESPONSE_SUPPLY:
						makeSupply(p.playerNum);
					break;
					case Player.RESPONSE_LIST:
						makeList(p.responseList,p.playerNum);
					break;
					case Player.RESPONSE_ORDER:
						makeReorder(p.playerNum);
					break;
					case Player.RESPONSE_CHOICE:
						//nothing I guess
					break;
					case Player.RESPONSE_CONFIRM_REVEALED:
						makePlayersRevealed(p,p.revealingToAll);
					break;
				}
			}else {//not awaiting response
				if(p.currentlyViewing!=null)
					makeView(p);
			}
		}
		
		//Draw things that appear on top of everything else
		makeSetViewBox();
		
	}
	

	
	public void makeHand(int playerNum) {
		//generates a hand (of ClickableCards) for a single player (THEIR hand)
		Player p=theGame.players.get(playerNum);
		
		int x1=200,x2=875;
		
		if(p!=theGame.turnPlayer && p.awaitingResponse==false) {
			//if it is not your turn then you also need to draw the turn player's hand face down (below yours)
			int count=theGame.turnPlayer.hand.cards.size();
			for(int x=0;x<count;x++) {
				int xpos=x1;
				if(count>1) xpos+=x*((x2-x1)/(count-1)); else xpos=(x1+x2)/2;
				int ypos=300;
				ClickableCard cc=new ClickableCard(theGame.turnPlayer.hand.cards.get(x),xpos,ypos,0.25);
				cc.faceDown=!(theGame.turnPlayer.phase==Player.PHASE_BUY && cc.card.dominionCard.treasure);//reveal treasure cards during buy phase
				cc.clickable=false;
				addClickableCard(cc,playerNum);
			}
		}
		
		int count=p.hand.cards.size();
		
		for(int x=0;x<count;x++){
			ClickableCard cc=null;
			int xpos=x1;
			if(count>1) xpos+=x*((x2-x1)/(count-1)); else xpos=(x1+x2)/2;
			int ypos=300;
			if(p==theGame.turnPlayer || (p.awaitingResponse && p.responseType==Player.RESPONSE_HAND)) {
				//the players turn  Since it is your turn only your hand is shown
				cc=new ClickableCard(p.hand.cards.get(x),xpos,ypos,0.25) {
					public void clicked(DominionGUI gui){
						if(player.awaitingResponse){
							if(player.canSelect(this)) {
								System.out.println("selected");
								selected=!selected;
								player.toggleCard(card);
								gui.refresh();
							}
						}else{
							if(card.canPlay(player)){
								card.play(player);
							}
						}
					}
				};
				cc.player=p;
				if(p.awaitingResponse){
					if(p.canSelect(cc)==false)
						cc.darken=true;
				}else {
					cc.canZoom=true;
				}
			}else {
				cc=new ClickableCard(p.hand.cards.get(x),xpos,ypos+150,0.25) {
					public void clicked(DominionGUI gui){
						//not your turn use for reactions
					}
				};
			}
			//if(p==theGame.turnPlayer) {
				if(cc!=null) addClickableCard(cc,playerNum);
			//}
		}
	}
	
	
	
	public void makeSupply(int playerNum) {
		Player p=theGame.players.get(playerNum);
		
		for(int x=0;x<theGame.basicPiles.size();x++){
			if(theGame.basicPiles.get(x).pile.cards.size()>0){
				ClickableCard cc=new ClickableCard(theGame.basicPiles.get(x).pile.cards.get(0),20+x*140,40,0.2){
					public void clicked(DominionGUI gui){
						if(player.canSelect(this)){
							selected=!selected;
							player.toggleCard(card);
							gui.refresh();
						}else{
							if(player.CanBuy(card.dominionCard)){
								player.BuyCard(card.dominionCard);
							}
						}
					}
				};
				cc.player=p;
				if(p.awaitingResponse){
					if(p.CheckRequirements(cc.card,p.responseReq)==false){
						cc.darken=true;
					}
				}else if(p.CanBuy(theGame.basicPiles.get(x))==false){
					cc.darken=true;
				}
				cc.number=theGame.basicPiles.get(x).pile.cards.size();
				if(theGame.turnPlayer!=p) cc.clickable=false;
				addClickableCard(cc,playerNum);
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
						if(player.canSelect(this)){
							selected=!selected;
							player.toggleCard(card);
							gui.refresh();
						}else{
							if(player.CanBuy(card.dominionCard)){
								player.BuyCard(card.dominionCard);
							}
						}
					}
				};
				cc.player=p;
				if(p.awaitingResponse){
					if(p.CheckRequirements(cc.card,p.responseReq)==false){
						cc.darken=true;
					}
				}else if(p.CanBuy(theGame.actionPiles.get(x))==false){
					cc.darken=true;
				}
				cc.number=theGame.actionPiles.get(x).pile.cards.size();
				cc.canZoom=true;
				if(theGame.turnPlayer!=p) cc.clickable=false;
				addClickableCard(cc,playerNum);
			}
			
		}
	}
	
	public void makePlayed() {
		//make clickable cards for the already played cards previously drawn manually
		//applies to all players since every player will see the same thing.
		
		for(int a=0;a<theGame.turnPlayer.playedCards.cards.size();a++) {
			int xpos=120+a*140;
			int ypos=150;
			ClickableCard cc=new ClickableCard(theGame.turnPlayer.playedCards.cards.get(a),xpos,ypos,0.2);
			cc.clickable=false;
			for(int p=0;p<theGame.settings.players;p++) {
				//hide played cards while making a decision
				if(theGame.players.get(p).awaitingResponse==false)
					addClickableCard(cc,p);
			}
		}
		for(int a=0;a<theGame.turnPlayer.durations.cards.size();a++){
			int xpos=200+a*140;
			int ypos=560;
			ClickableCard cc=new ClickableCard(theGame.turnPlayer.durations.cards.get(a),xpos,ypos,0.15);
			if(theGame.turnPlayer.durations.cards.get(a).counters>0) {
				cc.number=theGame.turnPlayer.durations.cards.get(a).counters; //counters on duration card
			}
			cc.clickable=false;
			for(int p=0;p<theGame.settings.players;p++)
				if(theGame.players.get(p).awaitingResponse==false)
					addClickableCard(cc,p);
			
		}
		
	}
	
	public void makeList(ArrayList<Card> list,int playerNum) {
		int sep=150;
		if(list.size()>6) sep=100;
		if(list.size()>10) sep=50;
		for(int x=0;x<list.size();x++){
			ClickableCard cc=new ClickableCard(list.get(x),100+sep*x,200,0.2){
				public void clicked(DominionGUI gui){
					if(player.canSelect(this)) {
						selected=!selected;
						player.toggleCard(card);
						gui.refresh();
					}
				}
			};
			cc.player=theGame.players.get(playerNum);
			cc.canZoom=true;
			if(cc.player.canSelect(cc)==false) cc.darken=true;
			addClickableCard(cc,playerNum);
		}
	}
	
	public void makeReorder(int playerNum) {
		System.out.println("makeReorder("+playerNum+")");
		Player p=theGame.players.get(playerNum);
		int t1=100;
		int t2=100;
		int spacing=150;
		System.out.println("list "+p.responseList.size()+"   selected "+p.selectedCards.size());
		for(int a=0;a<p.selectedCards.size();a++) {
			ClickableCard cc=new ClickableCard(p.selectedCards.get(a),t1,450,0.2) {
				public void clicked(DominionGUI gui) {
					player.toggleCard(card);
				}
			};
			cc.player=p;
			t1+=spacing;
			addClickableCard(cc,playerNum);
		}
		for(int a=0;a<p.responseList.size();a++) {
			if(p.selectedCards.contains(p.responseList.get(a))==false) {
				ClickableCard cc=new ClickableCard(p.responseList.get(a),t2,200,0.2) {
					public void clicked(DominionGUI gui) {
						player.toggleCard(card);
					}
				};
				cc.player=p;
				t2+=spacing;
				addClickableCard(cc,playerNum);
			}
		}
		
		/*for(int a=0;a<p.responseList.size();a++) {
			ClickableCard cc=new ClickableCard(p.responseList.get(a),0,0,0.2) {
				public void clicked(DominionGUI gui) {
					player.toggleCard(card);
				}
			};
			cc.player=p;
			if(p.selectedCards.contains(p.responseList.get(a))){
				cc.xpos=t1;
				cc.ypos=450;
				t1+=spacing;
			}else {
				cc.xpos=t2;
				cc.ypos=200;
				t2+=spacing;
			}
			addClickableCard(cc,p.playerNum);
		}*/
	}
	
	
	
	
	public void addClickableCard(ClickableCard cc,int p) {
		//gives a clickable card to a player
		//numbers start at 0      the host is player 0				
		cc.selected=theGame.players.get(p).selectedCards.contains(cc.card);
		if(p==0) {	
			clickables.add(cc);
		}else {
			theGame.settings.hostObject.output(p-1,cc.getCode(nextClickableID,cc.faceDown));
			clientClickables.add(cc);
			clientClickableIDs.add(new Integer(nextClickableID));
			nextClickableID++;
		}
	}
	
	public void addClickableImage(ClickableImage ci,int p) {
		//gives a clickable image to a player
		//does not require listening for a response
		if(p==0) {
			clickables.add(ci);	
		}else {
			theGame.settings.hostObject.output(p-1,ci.getCode());
		}
	}
	
	
	
	public void makeButtons() {
		window.clearGUI();
		window.getPanel().setLayout(null);
		if(theGame.screen==Game.SCREEN_GAME){
			//create the buttons needed for the turn
			if(theGame.turnPlayer==player) {
				//JPanel MainMenu=new JPanel();
				//MainMenu.setOpaque(false);
				JButton buyphase=new JButton("Buy Phase");
				buyphase.addActionListener(new MyListener(this){
					public void actionPerformed(ActionEvent e){
						if(gui.theGame.turnPlayer.playerNum==0){
							if(gui.player.phase==0 && gui.player.awaitingResponse==false){
								gui.buyClicked=true;
							}
						}
					}
				});
				JButton endphase=new JButton("End Turn");
				endphase.addActionListener(new MyListener(this){
					public void actionPerformed(ActionEvent e){
						if(gui.theGame.turnPlayer.playerNum==0 && gui.player.awaitingResponse==false){
							gui.endClicked=true;
						}
					}
				});
				buyphase.setBounds(window.getWidth()/2-110,10,100,26);
				endphase.setBounds(window.getWidth()/2+10,10,100,26);
				//MainMenu.add(buyphase);
				//MainMenu.add(endphase);
				//MainMenu.setLocation(0,0);
				//window.getPanel().add(MainMenu);
				window.getPanel().add(buyphase);
				window.getPanel().add(endphase);
			}
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
	
	public void nextTurn() {
		theGame.turnPlayer.EndTurn();
		//put check end game here
		theGame.turnPlayer=theGame.turnPlayer.getLeft();
		theGame.turnPlayer.StartTurn();
		/*for(int a=0;a<theGame.players.size();a++) {
			if(theGame.players.get(a)==theGame.turnPlayer)
				theGame.settings.hostObject.output(a,"yourturn");
			else
				theGame.settings.hostObject.output(a,"otherturn");
		}*/
		theGame.settings.hostObject.outputAll("turn"+theGame.turnPlayer.playerNum);
		refresh();
		theGame.settings.hostObject.outputAll("guirefresh");
		makeButtons();
	}
	
	ArrayList<Card> getSelected(Player p){
		ArrayList<Card> out=new ArrayList<Card>();
		for(int a=0;a<p.selectedCards.size();a++) {
			out.add(p.selectedCards.get(a));
		}
		return out;
	}
	
	
	public ArrayList<Card> playerResponse(Player p){
		//this only works to get a response from a single player
		if(p.playerNum==0) return super.playerResponse(p);
		//send information to the client then wait for a response
		refresh();
		theGame.settings.hostObject.output(p.playerNum-1,"M:"+p.message);
		theGame.settings.hostObject.output(p.playerNum-1,"confirm");//makes a done button
		if(p.responseType==Player.RESPONSE_CONFIRM_REVEALED) {//turn on the card reveal background
			theGame.settings.hostObject.output(p.playerNum-1,"boxes on");//might be redundant
		}
		System.out.println("Awaiting response from playerNum "+p.playerNum);
		while(p.awaitingResponse) {
			String str=theGame.settings.hostObject.getNext(p.playerNum-1);
			while(str!=null) {
				processInput(str,p);
				str=theGame.settings.hostObject.getNext(p.playerNum-1);
			}
			Thread.yield();
		}
		p.awaitingResponse=false;
		theGame.settings.hostObject.output(p.playerNum-1,"clear");//clear special image toggles
		return getSelected(p);
	}
	
	public ArrayList<Card> orderCards(ArrayList<Card> list,Player p){
		if(p.playerNum==0) return super.orderCards(list,p);
		return playerResponse(p);
	}
	
	public int multipleChoice(String question,String[] choices,Player p){
		if(p.playerNum==0) return super.multipleChoice(question,choices,p);//might cause problems because it will stop communicating during the choice
		String msg="Q:"+question;
		for(int a=0;a<choices.length;a++) {
			msg+="/"+choices[a];
		}
		msg+="/";
		p.awaitingResponse=true;
		p.responseType=Player.RESPONSE_CHOICE;
		p.responseChoice=-1;
		theGame.settings.hostObject.output(p.playerNum-1,msg);
		//pauseReader=true;
		while(p.awaitingResponse) {//Will only work for one player at a time (won't work for torturer)
			String str=theGame.settings.hostObject.getNext(p.playerNum-1);
			while(str!=null) {
				processInput(str,p);
				System.out.println("input processed in mc "+str);
				str=theGame.settings.hostObject.getNext(p.playerNum-1);
			}
			Thread.yield();
		}
		//pauseReader=false;
		System.out.println("Response="+p.responseChoice);
		p.awaitingResponse=false;
		return p.responseChoice;
	}
	
	
	/*public void showRevealed(Player p) {
		//Unfinished
		if(p.playerNum==0) { super.showRevealed(); return;}
		String msg="S:";//s for show
		for(int a=0;a<theGame.players.size();a++) {
			for(int b=0;b<theGame.players.get(a).revealedCards.size();b++) {
				msg+=theGame.players.get(a).revealedCards.get(b).dominionCard.name;
				msg+="/";
			}
			msg+=";";
		}
		theGame.settings.hostObject.output(p.playerNum-1,msg);
		theGame.settings.hostObject.output(p.playerNum-1,"confirm");
		p.awaitingResponse=true;
	}*/
	
	/*public void showPlayersRevealed(Player p,boolean toAll) {
		//shows revealed cards until player p clicks done
		//if toAll is true then all players see it
		
		for(Player screenPlayer:theGame.players) {//the player whose screen the card is drawn on
			if(screenPlayer==p || toAll) {
				if(screenPlayer.playerNum==0)
					clickables.clear(); else theGame.settings.hostObject.output(screenPlayer.playerNum-1,"refresh");
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
						addClickableCard(cc,x);
					}
				}
			}
		}
		
		p.awaitingResponse=true;
		p.responseType=Player.RESPONSE_CONFIRM_REVEALED;
		playerResponse(p);
	

	}*/
	
	void makeDurations(Player p) {
		//Create Clickable images for duration cards
		//If the card has cards attached to it, it needs to have a set under it
		for(int a=0;a<p.durations.cards.size();a++) {
			Card card=p.durations.cards.get(a);
			ClickableCard cc=new ClickableCard(card,450+80*a,80,0.15);
			if(card.overlay.cards.size()>0) {//attached cards
				cc.printSize=true;
				cc.linkedSet=card.overlay;
			}
			addClickableCard(cc,p.playerNum);
		}
	}
	
	void makeExtraPiles(Player p) {
		//creates the native village pile and island pile
		int extraPiles=0;
		if(p.nativeVillage.cards.size()>0) {
			ClickableImage ci=new ClickableImage("Native Village Icon.jpg",820,10+90*extraPiles,0.2);
			ci.type=ClickableImage.TYPE_SET;
			ci.printSize=true;
			ci.linkedSet=p.nativeVillage;
			ci.spName="Native Village";
			addClickableImage(ci,p.playerNum);
			extraPiles+=1;
		}
		if(p.island.cards.size()>0) {
			ClickableImage ci=new ClickableImage("Island Icon.jpg",820,10+90*extraPiles,0.2);
			ci.type=ClickableImage.TYPE_SET;
			ci.printSize=true;
			ci.linkedSet=p.island;
			ci.spName="Island";
			addClickableImage(ci,p.playerNum);
			extraPiles+=1;
		}
		
	}
	
	
	
	
	
	public void reactionChance(Player attacker) {
		this.attacker=attacker;
		awaitingReaction=false;
		someoneReacting=false;
		System.out.println("Reaction Chance");
		for(int a=0;a<theGame.players.size();a++) {
			Player p = theGame.players.get(a);
			if(p!=attacker) {
				if(p.canReact()) {
					awaitingReaction=true;
					p.awaitingResponse=true;
					p.responseType=Player.RESPONSE_REACTION;
					reactionStartTime=System.currentTimeMillis();
					if(a==0) {//host player
						makeReactButton();
					}else {//client player
						theGame.settings.hostObject.output(p.playerNum-1,"react");
					}
				}
			}
		}
		if(awaitingReaction==false) return;
		while((awaitingReaction && System.currentTimeMillis()<reactionStartTime+6000)||someoneReacting) {
			//process input from those awaiting reaction
			awaitingReaction=false;
			for(int a=0;a<theGame.players.size();a++) {
				Player p=theGame.players.get(a);
				if(p.awaitingResponse) {
					awaitingReaction=true;
					if(a==0) {
						if(reactClicked) {
							reactClicked=false;
							someoneReacting=true;
							p.selectReaction(attacker);
							ArrayList<Card> sel=new ArrayList<Card>();
							for(int b=0;b<p.selectedCards.size();b++) {
								sel.add(p.selectedCards.get(b));
							}
							for(int b=0;b<sel.size();b++) {
								sel.get(b).dominionCard.react(p,sel.get(b),attacker);
							}
							p.awaitingReact=false;
						}
					}else {
						String str=theGame.settings.hostObject.getNext(p.playerNum-1);
						while(str!=null) {
							processInput(str,p);
							System.out.println("input processed in reaction chance: "+str);
							str=theGame.settings.hostObject.getNext(p.playerNum-1);
						}
					}
				}
			}
			if(someoneReacting) {//exit when everyone who reacted has finished
				someoneReacting=false;
				for(int a=0;a<theGame.players.size();a++) {
					if(theGame.players.get(a).awaitingReact)
						someoneReacting=true;
				}
			}
			Thread.yield();
		}
		if(awaitingReaction) {//out of time to react
			System.out.println("Out of time to react");
			awaitingReaction=false;
			for(int a=0;a<theGame.players.size();a++) {
				theGame.players.get(a).awaitingResponse=false;
			}
		}
		refresh();
		makeButtons();
		theGame.settings.hostObject.outputAll("guirefresh");
	}
	
}


