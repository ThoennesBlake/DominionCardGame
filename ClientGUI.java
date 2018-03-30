import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;



public class ClientGUI extends DominionGUI implements Runnable{
	//In game GUI for a client in a game running on a different computer
	
	
	protected Thread readerThread=null;
	boolean myTurn=false;
	String turnData="";
	int turnPlayerNumber=0;
	 
	
	//updated by the messages by the host for drawing things other than clickable cards
	/*DominionCard topDiscard=null;//top face up card of the turn player's discard pile
	int discardSize=0;//cards in the discard pile
	int deckSize=0;//cards in the deck
	ArrayList<DominionCard> playedCards=new ArrayList<>();//list of played cards to draw
	*/
	
	ClientGUI(Player player, Game theGame, BlakeWindow window) {
		super(player, theGame, window);
		// TODO Auto-generated constructor stub
	}
	
	public void play() {
		running=true;
		readerThread=new Thread(this);
		readerThread.start();
		window.clearGUI();
		window.revalidate();
		theGame.screen=Game.SCREEN_GAME;
		while(running) {
			
			window.clearScreen(Color.BLACK);
			draw();
			checkAll();
			window.sync(50);
		}
	}
	
	public void run() {
		//checks for messages from the client in a separate thread
		while(running) {
			String str=theGame.settings.clientObject.getNext();
			while(str!=null) {
				try {
					if(str.equals("refresh")) {
						clickables.clear();
						player.costChange=0;
					}
					if(str.substring(0,2).equals("C:")) {//a new clickable card
						clickables.add(ClickableCard.makeFromCode(str));
					}
					if(str.equals("yourturn")) {
						myTurn=true;
					}
					if(str.equals("otherturn")) {
						myTurn=false;
					}
					if(str.substring(0,2).equals("M:")) {
						player.message=str.substring(2);
					}
					if(str.equals("guirefresh")) {
						makeButtons();
					}
					if(str.substring(0,4).equals("turn")) {
						turnPlayerNumber=Integer.parseInt(str.substring(4));
						myTurn=(turnPlayerNumber==player.playerNum);
					}
					if(str.substring(0,2).equals("I:")) {
						turnData=str.substring(2);
					}
					if(str.substring(0,2).equals("Q:")) {
						processQuestion(str.substring(2));
						makeButtons();
					}
					if(str.equals("confirm")) {
						makeDoneButton();
					}
					if(str.equals("react")) {
						makeReactButton();
					}
					if(str.equals("clear")) {
						drawingRevealBoxes=false;
						
					}
					if(str.equals("boxes on")) {
						drawingRevealBoxes=true;
					}
					if(str.substring(0,10).equals("costChange")) {
						player.costChange=Integer.parseInt(str.substring(11));
					}
					if(str.substring(0,2).equals("A:")) {//clickable image
						clickables.add(ClickableImage.makeFromCode(str));
					}
				}catch(StringIndexOutOfBoundsException ex) {}
				str=theGame.settings.clientObject.getNext();
				
			}
			if(buyClicked) {
				buyClicked=false;
				theGame.settings.clientObject.output("B:buy");
			}
			if(endClicked) {
				endClicked=false;
				theGame.settings.clientObject.output("B:end");
			}
			if(doneClicked) {
				doneClicked=false;
				theGame.settings.clientObject.output("B:done");
			}
			if(reactClicked) {
				reactClicked=false;
				theGame.settings.clientObject.output("B:react");
			}
			Thread.yield();
		}
	}
	
	public void refresh() {
		//overriding so that this function does nothing
		makeSetViewBox();
	}
	
	public void closeCardSet() {
		setViewBox=null;
		theGame.settings.clientObject.output("requestrefresh");
	}
	
	public void draw() {
		
		if(drawingRevealBoxes)
			drawRevealBoxes(theGame.players.size(),player.playerNum);

		for(int x=0;x<clickables.size();x++){//draw unclickable cards below clickable cards
			if(clickables.get(x).clickable==false)
				clickables.get(x).draw(this,window);
		}
		
		//draw a border to section off the cards in your hand when it is not your turn
		
		for(int x=0;x<clickables.size();x++){
			if(clickables.get(x).clickable==true)
				clickables.get(x).draw(this,window);
		}
		//overlay text when it is not your turn here
		window.drawString("Player "+(turnPlayerNumber+1),0,0,F36,Color.WHITE);
		window.drawString(turnData,0,20,F36,Color.WHITE);
		
		if(player.message!="");
			window.drawString(player.message,50,150,F36,Color.WHITE);
		
		
	}
	
	
	public void makeButtons() {
		window.clearGUI();
		window.getPanel().setLayout(null);
		//if(theGame.screen==Game.SCREEN_GAME){
			//create the buttons needed for the turn
			//if(myTurn) {
				System.out.println("making buttons");
				JButton buyphase=new JButton("Buy Phase");
				buyphase.addActionListener(new MyListener(this){
					public void actionPerformed(ActionEvent e){
						gui.buyClicked=true;
					}
				});
				window.getPanel().add(buyphase);
				buyphase.setBounds(window.getWidth()/2-110,10,100,26);
				JButton endphase=new JButton("End Turn");
				endphase.addActionListener(new MyListener(this){
					public void actionPerformed(ActionEvent e){
						gui.endClicked=true;
					}
				});
				window.getPanel().add(endphase);
				endphase.setBounds(window.getWidth()/2+10,10,100,26);
			//}
		//}
		makeRefreshButton();
		window.revalidate();
	}
	
	
	private void makeDoneButton() {
		window.clearGUI();
		window.getPanel().setLayout(null);
		//JPanel MainMenu=new JPanel();
		//MainMenu.setOpaque(false);
		JButton done=new JButton("Done");
		done.addActionListener(new MyListener(this) {
			public void actionPerformed(ActionEvent e) {
				gui.doneClicked=true;
			}
		});
		//MainMenu.add(done);
		window.getPanel().add(done);//.add(MainMenu);
		//done.setBounds(window.getWidth()/2-40,50,80,26);
		done.setBounds(window.getWidth()/2-40,50,80,26);
		makeRefreshButton();
		window.revalidate();
	}
	
	private void makeRefreshButton() {
		JButton button=new JButton("Refresh");
		button.addActionListener(new MyListener(this){
			public void actionPerformed(ActionEvent e){
				gui.theGame.settings.clientObject.output("requestrefresh");
			}
		});
		button.setBounds(1,1,80,26);
		window.getPanel().add(button);
		
	}
	
	private void processQuestion(String msg) {
		String question=msg.substring(0,msg.indexOf("/"));
		msg=msg.substring(msg.indexOf("/")+1);
		ArrayList<String> option=new ArrayList<>();
		while(msg.indexOf("/")>=0) {
			option.add(msg.substring(0,msg.indexOf("/")));
			msg=msg.substring(msg.indexOf("/")+1);
		}
		String[] arr=new String[option.size()];
		for(int a=0;a<option.size();a++) {
			arr[a]=option.get(a);
		}
		int out=multipleChoice(question,arr,player);
		player.awaitingResponse=false;
		theGame.settings.clientObject.output("R:"+out);
		
	}
	
}