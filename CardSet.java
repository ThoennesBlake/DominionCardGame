import java.util.ArrayList;
import java.util.Collections;

public class CardSet{
	ArrayList<Card> cards=new ArrayList<Card>();;
	
	Player owner=null;
	boolean isHand=false;//these let the card know where it has been sent to
	boolean isDiscard=false;
	boolean isTrash=false;
	boolean isPlayed=false;
	
	public CardSet(){

	}
	
	public CardSet(Player owner,boolean isHand,boolean isDiscard){
		this.owner=owner;
		this.isHand=isHand;
		this.isDiscard=isDiscard;
	}
	
	
	
	static CardSet startingDeck(){
		//make a new deck with 3 estates and 7 coppers
		DominionCard e=CardLibrary.findCard("Estate");
		DominionCard c=CardLibrary.findCard("Copper");
		CardSet out=new CardSet();
		for(int x=0;x<3;x++) out.addToTop(new Card(e));
		for(int x=0;x<7;x++) out.addToTop(new Card(c));
		return out;
	}
	
	//The front of the array is considered the top
	public void addToBottom(Card card){
		//called by card's move function
		cards.add(card);
		card.location=this;
	}
	public void addToBottom(CardSet set){
		//take each card from the top of the given set and place it on the bottom of this set
		while(set.cards.size()>0){
			set.cards.get(0).move(this,false);
		}
	}
	public void addToTop(Card card){
		cards.add(0,card);
		card.location=this;
	}
	public void addToTop(CardSet set){
		//take each card from the bottom of the given set and place it on the top of this set
		while(set.cards.size()>0){
			set.cards.get(set.cards.size()-1).move(this,true);
		}
	}
	public void shuffle(){
		Collections.shuffle(cards);
	}
	public Card checkTop(){
		if(cards.size()==0) return null;
		return cards.get(0);
	}
	public ArrayList<Card> checkTop(int num){
		ArrayList<Card> out=new ArrayList<Card>();
		for(int x=0;x<num && x<cards.size();x++){
			out.add(cards.get(x));
		}
		return out;
	}
	
	public int getValue(Player player){
		//return the total coin value of cards in the set
		int out=0;
		for(int x=0;x<cards.size();x++){
			out+=cards.get(x).dominionCard.getValue(player,cards.get(x));
		}
		return out;
	}
	public int getVP(Player player){
		int out=0;
		for(int x=0;x<cards.size();x++){
			out+=cards.get(x).getVP(player);
		}
		return out;
	}
	
	public String toString(){
		String out="(";
		for(int x=0;x<cards.size();x++){
			if(x!=0) out+=", ";
			out+=cards.get(x).toString();
		}
		out+=")";
		return out;
	}
	
	public boolean contains(DominionCard d) {
		for(Card c:cards) {
			if(c.dominionCard==d) return true;
		}
		return false;
	}
	public boolean contains(String name) {
		DominionCard d=CardLibrary.findCard(name);
		if(d==null) return false;
		return contains(d);
	}
}