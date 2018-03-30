//contains main menu functions and stores the game settings
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

import javax.swing.event.*;


public class Settings{
	int players=2;
	ArrayList<DominionCard> cards=new ArrayList<DominionCard>();
	Game theGame;
	BlakeWindow window;
	DominionCard selected=null;
	boolean online=false;
	boolean hosting=false;
	boolean back=false;//go back to main menu
	//ClientConnection[] clients=new ClientConnection[maxPlayers-1];
	protected static int port=4444;
	protected static int maxPlayers=4;
	HostSearcher hostSearcher;
	boolean connectClicked=false;
	ClientSearcher clientSearcher;
	HostType hostObject=null;
	ClientType clientObject=null;
	int myPlayerNum=0;//player number assigned by the server;
	ClientConnection[] clientList;
	
	boolean menuDone=false;
	public void cardMenu(BlakeWindow window){
		window.clearGUI();
		this.window=window;
		menuDone=false;
		JPanel panel=window.getPanel();
		//panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.setLayout(null);
		//panel.add(new JLabel(" "));
		
		JPanel ppan=new JPanel();//Number of players
		ppan.setLayout(new BoxLayout(ppan,BoxLayout.PAGE_AXIS));
		if(online==false) {
			JPanel ppan2=new JPanel();
			
			ppan2.add(new JLabel("Players"));
			ButtonGroup playersel=new ButtonGroup();
			for(int x=0;x<4;x++){
				JRadioButton button=new JRadioButton(Integer.toString(x+1),x==players-1);
				button.addActionListener(new MyListener(this,x+1){
					public void actionPerformed(ActionEvent e){
						set.players=variable;
						System.out.println(variable);
					}
				});
				playersel.add(button);
				ppan2.add(button);
			}
			ppan.add(ppan2);
		}else {
			ppan.add(new JLabel(" "));
		}
		ppan.add(new JLabel(" "));
		ppan.add(new JLabel("Select 10-12 cards."));
		panel.add(ppan);
		ppan.setBounds(0,40,window.getWidth(),30);
		JPanel cardpanel=new JPanel();
		panel.add(cardpanel);	
		cardpanel.setBounds(220,ppan.getHeight()+ppan.getY(),window.getWidth()-220,500);
		cardpanel.setLayout(new GridLayout(22,6));
		for(int x=0;x<CardLibrary.AllCards.size();x++){
			DominionCard card=CardLibrary.AllCards.get(x);
			if(card.actionpile && ((online && card.allowOnline)||(!online && card.allowOffline))){
				JCheckBox box=new JCheckBox(card.name,cards.contains(card));
				box.addMouseListener(new MyListener(this,x,card){
				   public void mouseEntered(MouseEvent evt) {
				      //show the picture
						set.selected=dcard;
					}
				});
				box.addChangeListener(new MyListener(this,x,card){
					public void stateChanged(ChangeEvent evt) {//This seems to be getting called whenever you mouse over and off of it too
				      //select the card
						if(set.cards.contains(dcard)){
							set.cards.remove(dcard);
							//if(set.online && set.hosting)//remove card from list
								//set.hostObject.outputAll("R:"+dcard.name);
						}else{
							set.cards.add(dcard);
							//if(set.online && set.hosting)//add card to list
								//set.hostObject.outputAll("A:"+dcard.name);
						}
					}
				});

				cardpanel.add(box);
			}
		}
		
		JButton startbutton=new JButton("Start");
		startbutton.addActionListener(new MyListener(this,0){
			public void actionPerformed(ActionEvent e){
				if(set.checkDone()) 
					set.menuDone=true;
			}
		});
		startbutton.setBounds(window.getWidth()/2-45,window.getHeight()-80,90,26);
		panel.add(startbutton);
		window.revalidate();
		while(menuDone==false){
			window.clearScreen(new Color(238,238,238));
			if(selected!=null){
				window.drawScaleImage(selected.getImage(),20,100,0.4,0.4);
			}
			window.sync(50);
		}
		CardLibrary.sortByCost(cards);
		if(online && hosting) {
			//send cards to clients
			for(int a=0;a<cards.size();a++) {
				hostObject.outputAll("A:"+cards.get(a).name);
			}
			//hostObject.outputAll("begin");
		}
	}
	
	private boolean checkDone(){
		if(cards.size()<10 || cards.size()>12){
			return false;
		}
		return true;
	}
	
	
	public void firstMenu(BlakeWindow window) {
		//select either online or hotseat
		this.window=window;
		window.clearGUI();
		menuDone=false;
		back=false;
		myPlayerNum=0;
		JPanel panel=new JPanel();
		window.getPanel().add(panel);
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.add(new JLabel(" "));
		panel.add(new JLabel(" "));
		//make jbuttons
		panel.add(new JLabel("Mode Select"));
		panel.add(new JLabel(" "));
		JButton hotseatButton=new JButton("Play Offline");
		hotseatButton.addActionListener(new MyListener(this,0) {
			public void actionPerformed(ActionEvent e){
				set.online=false;
				set.menuDone=true;
			}
		});
		panel.add(hotseatButton);
		panel.add(new JLabel(" "));
		JButton hostButton = new JButton("Host Online");
		hostButton.addActionListener(new MyListener(this,0) {
			public void actionPerformed(ActionEvent e){
				set.online=true;
				set.hosting=true;
				set.menuDone=true;
			}
		});
		panel.add(hostButton);
		panel.add(new JLabel(" "));
		JButton joinButton = new JButton("Join Online");
		joinButton.addActionListener(new MyListener(this,0) {
			public void actionPerformed(ActionEvent e){
				set.online=true;
				set.hosting=false;
				set.menuDone=true;
			}
		});
		panel.add(joinButton);
		window.revalidate();
		while(menuDone==false) {
			window.clearScreen(new Color(238,238,238));
			window.sync(50);
		}
		/*if(online && hosting) {
			hostOnlineMenu(window);
		}
		if(online && !hosting) {
			joinOnlineMenu(window);
			//go to special code from here
			return;
		}
		cardMenu(window);*/
	}
	
	public void hostOnlineMenu(BlakeWindow window) {
		String ip=getIPAddress();
		String ip2=getIPAddressLAN();
		window.clearGUI();
		menuDone=false;
		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		window.getPanel().add(panel);
		panel.add(new JLabel(" "));
		panel.add(new JLabel("Hosting Online Game"));
		panel.add(new JLabel(" "));
		panel.add(new JLabel(" "));
		panel.add(new JLabel("Your WAN IP address is "+ip));
		panel.add(new JLabel(" "));
		panel.add(new JLabel("Your LAN IP address is "+ip2));
		panel.add(new JLabel(" "));
		JLabel label1=new JLabel("Waiting for players...");
		panel.add(label1);
		panel.add(new JLabel(" "));
		JLabel[] playerLabels=new JLabel[maxPlayers-1];
		for(int a=0;a<maxPlayers-1;a++) {
			playerLabels[a]=new JLabel("Player "+(a+1)+": - - -     ");
			panel.add(playerLabels[a]);
			panel.add(new JLabel(" "));
		}
		panel.add(new JLabel(" "));
		JButton startbutton=new JButton("Start Game");
		startbutton.addActionListener(new MyListener(this,0) {
			public void actionPerformed(ActionEvent e) {
				if(set.hostSearcher.canStartGame()) {
					set.hostObject=set.hostSearcher.host;
					set.menuDone=true;
					set.clientList=set.hostSearcher.getClientList();
					set.players=set.clientList.length+1;//self included
					set.hostObject.cutoff();
				}
				
			}
		});
		panel.add(startbutton);
		panel.add(new JLabel(" "));
		JButton quitbutton=new JButton("Quit");
		quitbutton.addActionListener(new MyListener(this,0) {
			public void actionPerformed(ActionEvent e) {
				set.back=true;
				set.menuDone=true;
				if(set.hostObject!=null)
					set.hostObject.quit();
			}
		});
		panel.add(quitbutton);
		panel.add(new JLabel(" "));
		window.revalidate();
		
		hostSearcher=new HostSearcher(this,label1,playerLabels);
		hostSearcher.start();
		
		while(menuDone==false) {
			window.clearScreen(new Color(238,238,238));
			window.sync(50);
		}
		
		if(back) {
			//back=false;
			//firstMenu(window);
			hostSearcher.quit();
			return;
		}
		System.out.println("Informing clients");
		//Send a message to each client letting them know what player number they are and that it is moving to the settings page
		for(int a=0;a<clientList.length;a++) {
			System.out.print(a);
			clientList[a].output("settings");
			clientList[a].output("P:"+(a+2));
		}
		
	}
	
	
	
	
	
	
	public void joinOnlineMenu(BlakeWindow window) {
		window.clearGUI();
		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		window.getPanel().add(panel);
		panel.add(new JLabel(" "));
		panel.add(new JLabel("Joining Online Game"));
		panel.add(new JLabel(" "));
		panel.add(new JLabel("Enter IP address of the game's host."));
		JTextField field=new JTextField();
		panel.add(field);
		panel.add(new JLabel(" "));
		JButton connect=new JButton("Connect");
		connect.addActionListener(new MyListener(this,0) {
			public void actionPerformed(ActionEvent e){
				set.connectClicked=true;
			}
		});
		panel.add(connect);
		panel.add(new JLabel(" "));;
		JLabel resultLabel=new JLabel(" ");
		panel.add(resultLabel);
		panel.add(new JLabel(" "));
		JButton quitbutton=new JButton("Exit");
		quitbutton.addActionListener(new MyListener(this,0) {
			public void actionPerformed(ActionEvent e){
				set.back=true;
				set.menuDone=true;
			}
		});
		panel.add(quitbutton);
		window.revalidate();
		menuDone=false;
		
		clientSearcher=new ClientSearcher(resultLabel);
		
		while(menuDone==false) {
			window.clearScreen(new Color(238,238,238));
			if(connectClicked) {
				connectClicked=false;
				if(clientSearcher.client==null || clientSearcher.client.connected==false) {
					clientSearcher.search(field.getText());
				}
			}
			if(clientSearcher.client!=null/* && clientSearcher.client.connected*/) {
				clientObject=clientSearcher.client;
				//check for the host starting the game
				//receive messages from the host
				String str=clientSearcher.client.getNext();
				while(str!=null) {
					System.out.println("Recieved: "+str);
					if(str.equals("settings")){
						menuDone=true;
						back=false;
					}
					if(str.substring(0,2).equals("P:")) {
						myPlayerNum=Integer.parseInt(str.substring(2));
						System.out.println("Assigned player number "+myPlayerNum);
					}
					if(str.equals("kick")) {
						//kick player 
						
					}
					str=clientSearcher.client.getNext();
				}
			}
			window.sync(50);
		}
		if(back) {
			//back=false;
			if(clientSearcher.client!=null)
				clientSearcher.quit();
			//firstMenu(window);  //returning to the first menu moved to DominionGame
			
		}
		
		
		
	}
	
	
	
	
	public void joinOnlineSettingsRoom(BlakeWindow window) {
		//the page where players wait for the host to select the cards
		window.clearGUI();
		JPanel panel=new JPanel();
		window.getPanel().add(panel);
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.add(new JLabel(" "));
		panel.add(new JLabel(" "));
		panel.add(new JLabel(" "));
		panel.add(new JLabel("Host player is selecting the cards"));
		panel.add(new JLabel(" "));
		panel.add(new JLabel(" "));
		panel.add(new JLabel("You are player "+(myPlayerNum)));
		window.revalidate();
		menuDone=false;
		cards.clear();
		while(menuDone==false) {
			window.clearScreen(new Color(238,238,238));
			String str=clientObject.getNext();
			while(str!=null) {
				//System.out.println("Recieved: "+str);
				if(str.equals("begin")){
					menuDone=true;
					back=false;
					break;
				}
				if(str.substring(0,2).equals("A:")){//add a card to the list
					cards.add(CardLibrary.findCard(str.substring(2)));
					System.out.println("Add "+str.substring(2));
				}
				if(str.substring(0,2).equals("R:")){//remove a card from the list
					cards.remove(CardLibrary.findCard(str.substring(2)));
				}
				str=clientObject.getNext();
			}
			//draw the cards that are being used
			for(int a=0;a<cards.size();a++) {
				window.drawScaleImage(cards.get(a).getImage(),50+a*80,300,0.3,0.3);
			}
			window.sync(50);
		}
		System.out.println("Exiting settings room");
	}
	
	/*public void hostOnlineSettingsRoom(BlakeWindow window) {
		cardMenu(window);
		initiateOnlineGame();
	}*/
	
	
	
	
	
	


	
	
	class MyListener implements ActionListener, ChangeListener, MouseListener{
		public int variable=0;
		public Settings set;
		public DominionCard dcard;
		MyListener(){
		}
		MyListener(Settings set,int var){
			variable=var;
			this.set=set;
		}
		MyListener(Settings set,int var,DominionCard card){
			variable=var;
			this.set=set;
			this.dcard=card;
		}
		public void actionPerformed(ActionEvent e){
		}
		public void stateChanged(ChangeEvent e){
		}
		public void mouseEntered(MouseEvent e){
		}
		public void mouseExited(MouseEvent e){
		}
		public void mouseReleased(MouseEvent e){
		}
		public void mousePressed(MouseEvent e){
		}
		public void mouseClicked(MouseEvent e){
		}
		
	}
	
	
	public static String getIPAddress() {
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		}catch(Exception ex){
			ex.printStackTrace();
			return "???";
		}
	}
	
	public static String getIPAddressLAN() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}catch(Exception ex) {
			ex.printStackTrace();
			return "???";
		}
	}
	
	
}


