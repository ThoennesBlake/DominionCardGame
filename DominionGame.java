//This is the main class
import java.util.ArrayList;
import java.awt.Color;
public class DominionGame{
	
	public static void main(String[] args){
		new DominionGame();
	}
	
	Game theGame;
	BlakeWindow window;
	Settings settings;
	
	static DominionGame dominionGameInstance;
	public boolean running=true;
	public boolean playAgain=true;//if true then reset will go to the card select menu. use to play a second round of online game
	
	DominionGame(){
		dominionGameInstance=this;
		window=new BlakeWindow(1200,700,"Dominion");
		CardLibrary.setup();
		settings=new Settings();
		
		//presets for testing
		
		/*settings.cards.add(CardLibrary.findCard("Transmute"));
		settings.cards.add(CardLibrary.findCard("River Ferry"));
		settings.cards.add(CardLibrary.findCard("Zookeeper"));
		settings.cards.add(CardLibrary.findCard("Black Market"));
		settings.cards.add(CardLibrary.findCard("Prison Riot"));
		settings.cards.add(CardLibrary.findCard("Market Crash"));
		settings.cards.add(CardLibrary.findCard("Island"));
		settings.cards.add(CardLibrary.findCard("Native Village"));
		settings.cards.add(CardLibrary.findCard("Mandarin"));
		settings.cards.add(CardLibrary.findCard("Imperial Guards"));
		settings.cards.add(CardLibrary.findCard("Rogue"));
		settings.cards.add(CardLibrary.findCard("Royal Advisor"));*/
		
		/*settings.cards.add(CardLibrary.findCard("Transmute"));
		settings.cards.add(CardLibrary.findCard("Ancient Wisdom"));
		settings.cards.add(CardLibrary.findCard("Thief"));
		settings.cards.add(CardLibrary.findCard("Mandarin"));
		settings.cards.add(CardLibrary.findCard("Wheel of Punishment"));
		settings.cards.add(CardLibrary.findCard("Black Market"));
		settings.cards.add(CardLibrary.findCard("Rogue"));
		settings.cards.add(CardLibrary.findCard("Prison Riot"));
		settings.cards.add(CardLibrary.findCard("Pirates"));
		settings.cards.add(CardLibrary.findCard("Peddler"));*/
		
		/*settings.cards.add(CardLibrary.findCard("Freedom Fighter"));
		settings.cards.add(CardLibrary.findCard("Dancing Monkeys"));
		settings.cards.add(CardLibrary.findCard("Bazaar"));
		settings.cards.add(CardLibrary.findCard("Trading Post"));
		settings.cards.add(CardLibrary.findCard("Market Crash"));
		settings.cards.add(CardLibrary.findCard("Sword Master"));
		settings.cards.add(CardLibrary.findCard("Cabbage Merchant"));
		settings.cards.add(CardLibrary.findCard("Dragon Temple"));
		settings.cards.add(CardLibrary.findCard("Smithy"));
		settings.cards.add(CardLibrary.findCard("War Counsel"));*/
		
		settings.cards.add(CardLibrary.findCard("Circus"));
		settings.cards.add(CardLibrary.findCard("Native Village"));
		settings.cards.add(CardLibrary.findCard("Imperial Guards"));
		settings.cards.add(CardLibrary.findCard("Boutique"));
		settings.cards.add(CardLibrary.findCard("Drop Off"));
		settings.cards.add(CardLibrary.findCard("Black Market"));
		settings.cards.add(CardLibrary.findCard("Menagerie"));
		settings.cards.add(CardLibrary.findCard("Poetry House"));
		settings.cards.add(CardLibrary.findCard("White Lotus Tile"));
		settings.cards.add(CardLibrary.findCard("Lighthouse"));
		//settings.cards.add(CardLibrary.findCard("Island"));
		
		while(running) {
			PlayGame();
		}
	}
	
	
	void PlayGame(){
		
		settings.firstMenu(window);
		playAgain=true;
		if(settings.online) {
			if(settings.hosting) {
				settings.hostOnlineMenu(window);
				if(settings.back) return;
				while(playAgain) {
					settings.cardMenu(window);
					//settings.hostOnlineSettingsRoom(window);
					initiateOnlineGame();
				}
			}else {
				settings.joinOnlineMenu(window);
				if(settings.back) return;
				while(playAgain) {
					settings.joinOnlineSettingsRoom(window);
					joinOnlineGame();
				}
			}
		}else {
			initiateOfflineGame();
		}
	}
	
	void initiateOfflineGame() {
		settings.cardMenu(window);
		theGame=new Game(settings);
		theGame.pickSupplyPiles(settings.cards);
		Player p1=theGame.players.get(0);
		
		DominionGUI gui=new DominionGUI(p1,theGame,window);

		p1.StartTurn();
		gui.play();
	}

	void initiateOnlineGame() {
		Game game=new Game(settings);
		game.pickSupplyPiles(settings.cards);
		Player p1=game.players.get(0);
		HostGUI gui=new HostGUI(p1,game,window);
		settings.hostObject.outputAll("begin");
		for(int a=0;a<game.players.size();a++)
			game.players.get(a).gui=gui;//for card references
		//need to wait until the clients have recieved the begin message to continue or it will cause serious problems
		try {Thread.sleep(250);}catch(Exception ex) {};//need to fix
		p1.StartTurn();
		gui.play();
	}
	
	public void joinOnlineGame() {
		Game game=new Game(settings);
		ClientGUI gui=new ClientGUI(new Player(settings.myPlayerNum),game,window);//setting these to null may cause crashes, but only where a less noticeable bug would happen.
		gui.play();
	}
}

//clockwise play


//If it starts crashing make sure major functions aren't being called by buttons