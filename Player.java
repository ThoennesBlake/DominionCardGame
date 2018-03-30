import java.util.ArrayList;
import java.util.LinkedList;
public class Player{
	//CardSets are locations where cards can be
	protected CardSet hand;//private info
	protected CardSet deck;//hidden info
	protected CardSet discard;//public info
	protected CardSet playedCards;//public info
	protected CardSet durations;//public info  (Active duration cards)
	protected CardSet aside;//public info (used while revealing cards)
	//note that cards can be inside a duration card's overlay CardSet and still be in your possession
	protected CardSet island;
	protected CardSet nativeVillage;
	
	protected int playerNum;
	
	//lists of cards which are not CardSets because they do not describe a location;
	public ArrayList<Card> revealedCards=new ArrayList<Card>();
	
	static final int PHASE_MAIN=0;
	static final int PHASE_BUY=1;
	static final int PHASE_END=2;
	
	static final int RESPONSE_HAND=0;
	static final int RESPONSE_LIST=1;
	static final int RESPONSE_ORDER=2;
	static final int RESPONSE_CHOICE=3;
	static final int RESPONSE_SUPPLY=4;
	static final int RESPONSE_REACTION=5;
	static final int RESPONSE_CONFIRM=6;//for viewing a list of cards without making a choice
	static final int RESPONSE_CONFIRM_REVEALED=7;//shows a list of all players revealed cards
	
	Game theGame;//set by Game so card effects can see the whole game
	DominionGUI gui=null;
	
	int victoryPoints=0;
	
	//Turn Info
	public String message="";
	int actions=1;
	int buys=1;
	int coins=0;
	int phase=PHASE_MAIN;
	protected ArrayList<DominionCard> usedActions=new ArrayList<>();
	boolean awaitingResponse=false;
	int responseType=0;
	Requirements responseReq=null;
	ArrayList<Card> responseList=null;
	int costChange=0;//for the effects of bridge and freedomfighter
	int endTurnDraws=5;
	int teaShops=0;
	boolean banquet=false;//when true your first buy can't be an action card
	int drawPlan=0;//cards to draw the next time refresh is called (to fix bugs caused by drawing before action is complete)
	ArrayList<Card> boughtCards;
	int responseChoice=-1;
	boolean moat=false;
	boolean awaitingReact=false;
	int potions=0;
	boolean hasDiscarded=false;//keeps track of if the player has performed some kind of discard since the last refresh
	boolean hasTrashed=false;
	int responseTimeLimit=-1;
	protected long responseStartTime=0;
	boolean revealingToAll=false;
	
	//new way to store selected cards
	protected ArrayList<Card> selectedCards=new ArrayList<>();
	
	public ArrayList<Card> currentlyViewing=null;
	public String viewTitle = "";
	
	
	public Player(int num){
		hand=new CardSet(this,true,false);
		deck=CardSet.startingDeck();
		deck.owner=this;
		discard=new CardSet(this,false,true);
		playedCards=new CardSet(this,false,false);
		playedCards.isPlayed=true;
		durations=new CardSet(this,false,false);
		aside=new CardSet(this,false,false);
		nativeVillage=new CardSet(this,false,false);
		island=new CardSet(this,false,false);
		playerNum=num;
		deck.shuffle();
		for(int x=0;x<5;x++) DrawCard();
	}
	
	
	public void StartTurn(){
		actions=1;
		buys=1;
		coins=0;
		theGame.turnPlayer=this;
		phase=PHASE_MAIN;
		usedActions.clear();
		costChange=0;
		endTurnDraws=5;
		teaShops=0;
		
		boughtCards=new ArrayList<Card>();
		moat=false;
		
		if(gui!=null) gui.startTurn();
		//call start turn effects for duration cards
		for(int x=0;x<durations.cards.size();x++){
			durations.cards.get(x).dominionCard.durationStartTurn(this,durations.cards.get(x));
		}
		//call start turn effects for other player's duration cards
		ArrayList<Player> P=getAllOtherPlayers();
		for(int x=0;x<P.size();x++){
			for(int y=0;y<P.get(x).durations.cards.size();y++){
				P.get(x).durations.cards.get(y).dominionCard.durationStartOtherPlayersTurn(this,P.get(x).durations.cards.get(y));
			}
		}
	}
	
	public void EnterBuyPhase(){
		coins+=hand.getValue(this);
		potions=0;
		for(int x=0;x<playedCards.cards.size();x++) {
			playedCards.cards.get(x).dominionCard.beforeBuyPhase(this,playedCards.cards.get(x));
		}
		for(Card c:hand.cards) {
			if(c.dominionCard.potion)
				potions++;
		}
		phase=PHASE_BUY;
		if(gui!=null) gui.startBuyPhase();
	}
	
	public void ShuffleDeck(){
		deck.shuffle();
	}
	
	public void AddDiscardToDeck(){
		//shuffles the discard pile then adds it to the deck
		discard.shuffle();
		deck.addToBottom(discard);
		for(int x=0;x<durations.cards.size();x++){
			durations.cards.get(x).dominionCard.durationShuffleDiscard(this,durations.cards.get(x));
		}
		Refresh();
	}
	
	public boolean DrawCard(){
		boolean out=true;
		if(deck.cards.size()==0){//if no cards left in deck then shuffle discard into deck
			AddDiscardToDeck();
		}
		if(deck.cards.size()>0){
			Card card=deck.checkTop();
			card.move(hand,false);
			card.dominionCard.whenDrawn(this,card);
			LinkedList<Card> toCheck=new LinkedList<>();
			for(Card c:playedCards.cards) {
				toCheck.add(c);
			}
			while(toCheck.size()>0) {
				toCheck.get(0).dominionCard.afterDraw(this,toCheck.get(0),card);
				toCheck.remove(0);
			}
			/*for(int x=0;x<playedCards.cards.size();x++){
				playedCards.cards.get(x).dominionCard.afterDraw(this,playedCards.cards.get(x),card);
			}*/
			
		}else{
			//no cards left in deck or discard
			out=false;
		}
		return out;
	}
	
	public void EndTurn(){
		//clean up phase
		//Move duration cards to the duration zone
		//move all cards in hand and field to discard, then draw 5 cards
		phase=PHASE_MAIN;
		costChange=0;
		banquet=false;
		LinkedList<Card> toCheck=new LinkedList<>();
		for(Card c:playedCards.cards) {//This allows cards to leave the played card set while it is iterating
			toCheck.add(c);
		}
		while(toCheck.size()>0) {
			toCheck.get(0).dominionCard.beforeCleanup(this,toCheck.get(0));
			toCheck.remove(0);
		}
		/*for(int x=0;x<playedCards.cards.size();x++){//card effects that activate at the end of the turn (before discarding)
			playedCards.cards.get(x).dominionCard.beforeCleanup(this,playedCards.cards.get(x));
		}*/
		for(Card c:durations.cards) {
			toCheck.add(c);
		}
		while(toCheck.size()>0) {
			toCheck.get(0).dominionCard.durationBeforeCleanup(this,toCheck.get(0));
			toCheck.remove(0);
		}
		/*for(int x=0;x<durations.cards.size();x++){
			durations.cards.get(x).dominionCard.durationBeforeCleanup(this,durations.cards.get(x));
		}*/
		phase=PHASE_END;
		ArrayList<Card> temp=(ArrayList<Card>)(playedCards.cards.clone());
		CleanUp();
		for(Card c:temp) {
			toCheck.add(c);
		}
		while(toCheck.size()>0) {
			toCheck.get(0).dominionCard.afterCleanup(this,toCheck.get(0));
			toCheck.remove(0);
		}
		/*for(int x=0;x<playedCards.cards.size();x++){//card effects that activate at the end of the turn (before drawing)
			playedCards.cards.get(x).dominionCard.afterCleanup(this,playedCards.cards.get(x));
		}*/
		for(int x=0;x<endTurnDraws;x++) DrawCard();
		
		
		
		for(int x=0;x<temp.size();x++){//card effects that activate at the end of the turn (after drawing)
			temp.get(x).dominionCard.endTurn(this,temp.get(x));
		}
		
		for(Card c:durations.cards) {
			toCheck.add(c);
		}
		while(toCheck.size()>0) {
			toCheck.get(0).dominionCard.durationEndTurn(this,toCheck.get(0));
			toCheck.remove(0);
		}
		/*for(int x=0;x<durations.cards.size();x++){
			durations.cards.get(x).dominionCard.durationEndTurn(this,durations.cards.get(x));
		}*/
		
		if(gui!=null) gui.endTurn();
		
		if(theGame.checkEndGame()){
			theGame.screen=Game.SCREEN_VICTORY;
			//Need to improve this
			
			gui.makeButtons();
		}
	}
	
	public void CleanUp(){
		//discard.addToTop(playedCards);
		while(playedCards.cards.size()>0){
			Card card=playedCards.cards.get(0);
			card.dominionCard.onCleanup(this,card);
			if(card.location==playedCards) {
				if(card.dominionCard.duration)
					card.move(durations,true);
				else
					card.move(discard,true);
			}
		}
		discard.addToTop(hand);
		//remove any temporary cards in the discard pile
		for(int x=0;x<discard.cards.size();x++){
			if(discard.cards.get(x).temporary){
				discard.cards.get(x).temporary=false;
				discard.cards.get(x).move(theGame.trash,true);
				x-=1;
			}
		}
	}
	
	public ArrayList<Card> SelectFromHand(Requirements req){
		//prompt the player to select cards from their hand
		//ArrayList<Card> out=new ArrayList<Card>();
		clearSelected();
		if(hand.cards.size()==0) return new ArrayList<Card>();//can't select from empty hand
		responseType=RESPONSE_HAND;
		awaitingResponse=true;
		responseReq=req;
		return gui.playerResponse(this);
	}
	
	public ArrayList<Card> SelectFromSupply(Requirements req){
		clearSelected();
		responseType=RESPONSE_SUPPLY;
		awaitingResponse=true;
		responseReq=req;
		return gui.playerResponse(this);
	}
	
	public ArrayList<Card> SelectFromList(ArrayList<Card> cards,Requirements req){
		clearSelected();
		if(cards.size()==0) return new ArrayList<Card>();
		awaitingResponse=true;
		responseType=RESPONSE_LIST;
		responseReq=req;
		responseList=cards;
		return gui.playerResponse(this);
	}
	public ArrayList<Card> SelectFromListAnother(Player other,ArrayList<Card> cards,Requirements req){
		//other player selects a card from a list
		return null;
	}
	
	public ArrayList<Card> OrderCards(ArrayList<Card> cards){
		//prompt the player to select what order to put cards in
		//use when returning cards to the top of the deck
		clearSelected();
		if(cards.size()<=1) return cards;// can't reorder 1 or 0 cards
		awaitingResponse=true;
		responseReq=null;
		responseType=RESPONSE_ORDER;
		responseList=cards;
		return gui.orderCards(cards,this);
	}
	
	public int Prompt(String question,String[] choices){
		//card effect makes player choose
		awaitingResponse=true;
		responseType=RESPONSE_CHOICE;
		return gui.multipleChoice(question,choices,this);
	}
	public int PromptAnother(Player other,String question,String[] choices){
		//ask a different player the question
		return 0;
	}
	
	public boolean RevealCards(int amount){
		//reveal a number of cards from the top of the deck
		boolean out=true;
		revealedCards.clear();
		if(deck.cards.size()<amount){
			AddDiscardToDeck();
		}
		if(deck.cards.size()<amount){amount=deck.cards.size(); out=false;}
		revealedCards=deck.checkTop(amount);
		return out;
	}
	
	public boolean RevealUntil(Requirements req){
		//reveal cards until the whole set of revealed cards meets the requirements
		ArrayList<Card> list=new ArrayList<Card>();
		do{
			boolean ok=RevealCards(1);
			if(ok==false) return false;
			list.add(revealedCards.get(0));
		}while(CheckRequirements(list,req)==false);
		return true;
	}
	
	public boolean RevealUntilFound(Requirements req){
		//reveal cards until req.min cards are found that meet the requirements, the rest are discarded as they are drawn
		//the cards are then placed back on top of the deck where they stay revealed
		revealedCards.clear();
		boolean out=true;
		do{
			if(deck.cards.size()==0){
				if(discard.cards.size()>0){
					AddDiscardToDeck();
				}else{out=false; break;}//failed
			}
			Card card=deck.checkTop();
			if(CheckRequirements(card,req)){
				card.move(aside,true);
				revealedCards.add(card);
			}else{
				card.move(aside,true);
			}
		}while(CheckRequirements(revealedCards,req)==false);
		discard.addToTop(aside);//discard the revealed cards
		return out;
	}
	
	
	
	
	public void BuyCard(SupplyPile supply){
		//assume the supply pile has a card
		Card card=supply.pile.checkTop();
		if(card==null) return;
		card.move(discard,true);
		boughtCards.add(card);
		buys-=1;
		coins-=supply.getCost();
		potions-=card.dominionCard.potionCost?1:0;
		card.dominionCard.whenBought(this,card);
		banquet=false;
		LinkedList<Card> toCheck=new LinkedList<>();
		for(Card c:playedCards.cards){
			toCheck.add(c);
			//playedCards.cards.get(x).dominionCard.afterBuy(this,playedCards.cards.get(x),card);
		}
		while(toCheck.size()>0) {
			toCheck.get(0).dominionCard.afterBuy(this,toCheck.get(0),card);
			toCheck.remove(0);
		}
		Refresh();
	}
	
	public boolean BuyCard(DominionCard card){
		SupplyPile supply=theGame.findSupply(card);
		if(supply==null) return false;
		if(supply.pile.cards.size()==0) return false;
		BuyCard(supply);
		return true;
	}
	
	public boolean CanBuy(SupplyPile supply){
		if(buys==1 && banquet && supply.card.action){
			return false;
		}
		if(supply.pile.cards.size()>0 && buys>0){
			if(supply.card.potionCost) {
				if(potions==0)
					return false;
			}
			if(supply.card.getCost(this,supply.pile.cards.get(0))<=coins)
				return true;
		}
		return false;
	}
	
	public boolean CanBuy(DominionCard card){
		SupplyPile supply=theGame.findSupply(card);
		if(supply==null) return false;
		return CanBuy(supply);
	}
	
	
	
	public int CardsInHand(){
		//return how many cards player has in their hand
		return hand.cards.size();
	}
	
	boolean CheckRequirements(ArrayList<Card> list,Requirements req){
		
		if(req==null) return true;
		return req.check(list,this);
		
		//does not support total cost yet
		/*
		int found=0;
		for(int x=0;x<list.size();x++){
			boolean good=false;
			DominionCard card=list.get(x).dominionCard;
			if(req.except.contains(card)) continue;//specific disallowed cards
			if(req.exceptCard.contains(list.get(x))) continue;//used to prevent cards from targeting itself
			if(req.allowPotionCost==false && card.potionCost) continue;//disallow cards with potion in its cost
			if(req.mustBePlayable){
				//because this function is called by canPlay, any canPlay function that calls canPlay needs to set requirements
				//to prevent checking itself to prevent infinite loop.
				if(card.canPlay(this,list.get(x))) good=true;
			}else{
				if(req.allowAction && card.action) good=true;
				if(req.allowTreasure && card.treasure) good=true;
				if(req.allowVictory && card.victory) good=true;
				if(req.allowCurse && card.curse) good=true;
			}
			if(card.getCost(this,list.get(x))<req.mincost || card.getCost(this,list.get(x))>req.maxcost) good=false;
			if(good) found+=1;
		}
		return found>=req.min && found<=req.max;*/
	}
	
	boolean CheckRequirements(Card card,Requirements req){
		if(req==null) return true;
		return req.check(card,this);
		
		/*if(req.except.contains(card.dominionCard)) return false;
		if(req.exceptCard.contains(card)) return false;
		boolean good=false;
		if(req.mustBePlayable){
			if(card.canPlay(this)) good=true;
		}else{
			if(req.allowAction && card.dominionCard.action) good=true;
			if(req.allowTreasure && card.dominionCard.treasure) good=true;
			if(req.allowVictory && card.dominionCard.victory) good=true;
			if(req.allowCurse && card.dominionCard.curse) good=true;
		}
		if(card.dominionCard.getCost(this,card)<req.mincost || card.dominionCard.getCost(this,card)>req.maxcost) good=false;
		return good;*/
	}
	
	public boolean CheckHand(Requirements req){
		//check if the player's hand has cards that meet the requirements
		return CheckRequirements(hand.cards,req);
	}
	
	public ArrayList<Card> GetDifferentCards(ArrayList<Card> list,Requirements req){
		//create a list of one copy of each differently named card in the given list
		ArrayList<Card> out=new ArrayList<Card>();
		for(int x=0;x<list.size();x++){
			if(req==null || CheckRequirements(list.get(x),req)){
				boolean isCopy=false;
				for(int y=0;y<out.size();y++){
					if(out.get(y).dominionCard==list.get(x).dominionCard)
						isCopy=true;
				}
				if(isCopy==false){
					out.add(list.get(x));
				}
			}
		}
		return out;
	}
	
	public int GetCoins(){
		//return the coins gained from actions plus the value of your hand
		return hand.getValue(this)+coins;
	}
	
	public int GetScore(){
		//count all the victory points in the discard, hand, deck, and field
		int out=victoryPoints;
		out+=deck.getVP(this);
		out+=hand.getVP(this);
		out+=discard.getVP(this);
		out+=playedCards.getVP(this);
		return out;
	}
	
	public Player getLeft(){
		//return the player to your left
		int num=playerNum+1;
		if(num>=theGame.players.size()) num=0;
		return theGame.players.get(num);
	}
	public Player getRight(){
		//return the player to your right
		int num=playerNum-1;
		if(num<0) num=theGame.players.size()-1;
		return theGame.players.get(num);
	}
	public ArrayList<Player> getAllOtherPlayers(){
		ArrayList<Player> out=new ArrayList<Player>();
		Player p=getLeft();
		while(p!=this){
			out.add(p);
			p=p.getLeft();
		}
		return out;
	}
	
	void Refresh(){
		//call whenever a card moves
		clearSelected();// Just added this
		message="";
		while(drawPlan>0){
			DrawCard();
			drawPlan-=1;
		}
		
		if(awaitingResponse==false){
			LinkedList<Card> toCheck=new LinkedList<>();//using this to allow cards to be moved while the list is iterating
			for(Card c:playedCards.cards) {
				toCheck.add(c);
			}
			while(toCheck.size()>0) {
				toCheck.get(0).dominionCard.afterRefresh(this,toCheck.get(0));
				toCheck.remove(0);
			}
			for(Card c:durations.cards) {
				toCheck.add(c);
			}
			while(toCheck.size()>0) {
				toCheck.get(0).dominionCard.durationAfterRefresh(this,toCheck.get(0));
				toCheck.remove(0);
			}
			
			/*for(int x=0;x<playedCards.cards.size();x++){
				playedCards.cards.get(x).dominionCard.afterRefresh(this,playedCards.cards.get(x));
			}*/
			
			if(hasDiscarded) {
				hasDiscarded=false;
				for(Card c:playedCards.cards) {
					toCheck.add(c);
				}
				while(toCheck.size()>0) {
					toCheck.get(0).dominionCard.afterDiscarding(this,toCheck.get(0));
					toCheck.remove(0);
				}
				/*for(int x=0;x<playedCards.cards.size();x++){
					playedCards.cards.get(x).dominionCard.afterDiscarding(this,playedCards.cards.get(x));
				}*/
			}
			if(hasTrashed) {
				hasTrashed=false;
				for(Card c:playedCards.cards) {
					toCheck.add(c);
				}
				while(toCheck.size()>0) {
					toCheck.get(0).dominionCard.afterTrashing(this,toCheck.get(0));
					toCheck.remove(0);
				}
				/*for(int x=0;x<playedCards.cards.size();x++){
					playedCards.cards.get(x).dominionCard.afterTrashing(this,playedCards.cards.get(x));
				}*/
			}
		}
		if(gui!=null)
			gui.refresh();
	}
	
	public String getName(){
		return "Player "+(playerNum+1);
	}
	
	public String toString(){
		String out="";
		out+="Player "+Integer.toString(playerNum)+"/n";
		out+="Actions:"+actions+"  Buys:"+buys+"  Coins:"+coins+"\n";
		out+="Hand: "+hand.toString()+"\n";
		out+="Deck: "+deck.toString()+"\n";
		out+="Discard: "+discard.toString()+"\n";
		out+="Played: "+playedCards.toString()+"\n";
		if(durations.cards.size()>0)
			out+="Durations: "+durations.toString()+"\n";
		return out;
	}
	
	boolean canSelect(ClickableCard cc){
		if(awaitingResponse==false) return false;
		//if the card is not selected and the current set passes, you can not select it if it would make the set fail
		if(cc.selected==false){
			if(CheckRequirements(cc.card,responseReq)==false) return false;
			ArrayList<Card> sel=gui.getSelected(this);
			if(CheckRequirements(sel,responseReq)){
				sel.add(cc.card);
				if(CheckRequirements(sel,responseReq)==false) return false;
			}
		}
		return true;
	}
	
	public void toggleCard(Card c) {
		if(selectedCards.indexOf(c)>=0) {
			selectedCards.remove(selectedCards.indexOf(c));
		}else {
			selectedCards.add(c);
		}
	}
	
	public void clearSelected() {
		selectedCards.clear();
	}
	
	public boolean canReact() {
		for(int a=0;a<hand.cards.size();a++) {
			if(hand.cards.get(a).dominionCard.canReact(this,hand.cards.get(a))) return true;
		}
		return false;
	}
	
	public void selectReaction(Player attacker) {
		//might not use
		
		awaitingResponse=true;
		awaitingReact=true;
		responseType=RESPONSE_LIST;
		responseList=new ArrayList<Card>();
		for(int a=0;a<hand.cards.size();a++) {
			if(hand.cards.get(a).dominionCard.canReact(this,hand.cards.get(a))) {
				responseList.add(hand.cards.get(a));
			}
		}
		clearSelected();
		selectedCards=SelectFromList(responseList,new Requirements(0,100));
		
		/*
		ArrayList<Card> sel=SelectFromList(responseList,new Requirements(0,100));//can chain multiple reactions
		//activate all the selected cards
		for(int a=0;a<sel.size();a++) {
			sel.get(a).dominionCard.react(this,sel.get(a),attacker);
		}*/
	}
	
	
	public ArrayList<Card> getAllCards(){
		//use for vp calculation cards
		//return a list of all cards owned by the player
		ArrayList<Card> out=new ArrayList<Card>();
		for(Card c:hand.cards)
			out.add(c);
		for(Card c:deck.cards)
			out.add(c);
		for(Card c:discard.cards)
			out.add(c);
		for(Card c:playedCards.cards)
			out.add(c);
		for(Card c:durations.cards) {
			out.add(c);
			for(Card c2:c.overlay.cards) {
				out.add(c2);
			}
		}
		for(Card c:aside.cards)
			out.add(c);
		for(Card c:island.cards)
			out.add(c);
		for(Card c:nativeVillage.cards)
			out.add(c);
		return out;
	}
	
	public void revealHand() {
		//All this does is copy your hand to revealed cards
		revealedCards.clear();
		for(Card c:hand.cards)
			revealedCards.add(c);
	}
	
	
	public void confirm() {
		//waits for the player to hit the done button to convey a message
		awaitingResponse=true;
		responseType=RESPONSE_CONFIRM;
		gui.playerResponse(this);
	}
	public void confirm(int timeLimit) {
		awaitingResponse=true;
		responseType=RESPONSE_CONFIRM;
		responseTimeLimit=timeLimit;
		responseStartTime=System.currentTimeMillis();
		gui.playerResponse(this);
	}
	
	public void confirmRevealed() {
		awaitingResponse=true;
		responseType=RESPONSE_CONFIRM_REVEALED;
		gui.playerResponse(this);
	}
	
	public void confirmRevealed(int timeLimit) {
		awaitingResponse=true;
		responseType=RESPONSE_CONFIRM_REVEALED;
		responseTimeLimit=timeLimit;
		responseStartTime=System.currentTimeMillis();
		gui.playerResponse(this);
	}

}