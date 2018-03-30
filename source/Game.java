//Contains all global information on the game
import java.util.ArrayList;

public class Game{
	protected ArrayList<Player> players;
	protected ArrayList<SupplyPile> actionPiles;
	protected ArrayList<SupplyPile> basicPiles;
	protected CardSet trash;
	protected CardSet blackMarket;
	
	protected SupplyPile estatePile;//saved for easier access
	protected SupplyPile provincePile;
	
	public Player turnPlayer;
	
	boolean OneScreenMode=true;
	
	static final int SCREEN_GAME=0;
	static final int SCREEN_PAUSE=1;
	static final int SCREEN_VICTORY=2;
	
	public int pilesForVictory=3;
	public int screen=SCREEN_GAME;
	
	boolean resetting=false;
	protected Settings settings;
	
	
	Game(Settings settings){
		this.settings=settings;
		int numberOfPlayers=settings.players;
		trash=new CardSet();
		pilesForVictory=3;
		screen=SCREEN_GAME;
		trash.isTrash=true;
		players=new ArrayList<Player>();
		actionPiles=new ArrayList<SupplyPile>();
		basicPiles=new ArrayList<SupplyPile>();
		for(int x=0;x<numberOfPlayers;x++){
			players.add(new Player(x));
			players.get(x).theGame=this;
		}
		turnPlayer=players.get(0);
		estatePile=new SupplyPile(CardLibrary.findCard("Estate"),this);
		basicPiles.add(estatePile);
		basicPiles.add(new SupplyPile(CardLibrary.findCard("Duchy"),this));
		provincePile=new SupplyPile(CardLibrary.findCard("Province"),this);
		basicPiles.add(provincePile);
		basicPiles.add(new SupplyPile(CardLibrary.findCard("Copper"),this));
		basicPiles.add(new SupplyPile(CardLibrary.findCard("Silver"),this));
		basicPiles.add(new SupplyPile(CardLibrary.findCard("Gold"),this));
		basicPiles.add(new SupplyPile(CardLibrary.findCard("Curse"),this));
		
		//make black market
		generateBlackMarket();
	}
	
	void pickSupplyPiles(ArrayList<DominionCard> selected){
		boolean usingPotions=false;
		for(int x=0;x<selected.size();x++){
			actionPiles.add(new SupplyPile(selected.get(x),this));
			selected.get(x).onSetup(this);
			if(selected.get(x).potionCost)
				usingPotions=true;
			for(int a=0;a<blackMarket.cards.size();a++) {//remove from black market
				if(blackMarket.cards.get(a).dominionCard==selected.get(x)) {
					blackMarket.cards.get(a).move(null,false);
				}
			}
		}
		if(usingPotions) {
			basicPiles.add(6,new SupplyPile(CardLibrary.findCard("Potion"),this));
		}
	}
	
	SupplyPile findSupply(DominionCard card){
		for(int x=0;x<basicPiles.size();x++){
			if(basicPiles.get(x).card==card)
				return basicPiles.get(x);
		}
		for(int x=0;x<actionPiles.size();x++){
			if(actionPiles.get(x).card==card)
				return actionPiles.get(x);
		}
		return null;
	}
	
	void clearRevealed(){
		for(int x=0;x<players.size();x++){
			players.get(x).revealedCards.clear();
		}
	}
	
	int countEmptyPiles(){
		int out=0;
		for(int x=0;x<actionPiles.size();x++){
			if(actionPiles.get(x).cardsLeft()==0)
				out+=1;
		}
		for(int x=0;x<basicPiles.size();x++){
			if(basicPiles.get(x).cardsLeft()==0)
				out+=1;
		}
		return out;
	}
	
	boolean checkEndGame(){
		boolean out=false;
		if(countEmptyPiles()>=pilesForVictory) out=true;
		if(provincePile.cardsLeft()==0) out=true;
		return out;
	}
	
	/*void returnToMainMenu(){
		//old
		DominionGame.dominionGameInstance.PlayGame();
	}*/
	
	void generateBlackMarket() {
		blackMarket=new CardSet();
		for(int x=0;x<CardLibrary.AllCards.size();x++){
			DominionCard card=CardLibrary.AllCards.get(x);
			if(card.action==true && card.cost>1 && card.noBM==false){
				blackMarket.addToTop(new Card(card));
			}
		}
		blackMarket.shuffle();
	}
	
	
}