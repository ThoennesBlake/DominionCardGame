public class SupplyPile{
	protected DominionCard card;
	int count;
	protected CardSet pile;
	private Game theGame;
	
	public SupplyPile(DominionCard card,Game game){
		this.count=card.supply;
		this.card=card;
		theGame=game;
		pile=new CardSet();
		refill();
	}
	public SupplyPile(DominionCard card,int count,Game game){
		this.count=count;
		this.card=card;
		theGame=game;
		pile=new CardSet();
		refill();
	}
	
	public void refill(){
		while(pile.cards.size()<count){
			pile.addToTop(new Card(card));
		}
	}
	
	public int cardsLeft(){
		return pile.cards.size();
	}
	
	public int getCost(){
		return card.getCost(theGame.turnPlayer,null);//using turn player here might be a problem later
	}
	
	public Card topCard(){
		return pile.checkTop();
	}
	

}