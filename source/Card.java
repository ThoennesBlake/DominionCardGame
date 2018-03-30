
public class Card{
	//a single instance of a card
	DominionCard dominionCard;
	protected CardSet overlay;//only used for duration cards that require you remove cards from play
	int counters=0;//only used for duration cards
	CardSet location=null;//the current location of the card
	//a card should never be in more than one cardset at a time
	
	boolean temporary=false;// For cards drawn by dancing monkeys
	
	public Card(DominionCard dominion){
		dominionCard=dominion;
		overlay=new CardSet();
	}
	
	void move(CardSet destination, boolean onTop){
		//remove from old location
		boolean discarded=false;
		boolean trashed=false;
		if(location!=null){
			if(location.cards.contains(this)) location.cards.remove(this);
			else System.out.println("Error: card not present in its location");
			if(destination==null) return;
			discarded = destination.isDiscard && location.isHand && location.owner==destination.owner && destination.owner.theGame.turnPlayer.phase==Player.PHASE_MAIN;
			trashed = destination.isTrash && location.owner!=null;
		}
		if(destination==null) return;//the card is lost forever
		//place in new location
		CardSet oldloc=location;
		location=destination;
		if(onTop) location.addToTop(this);
		else location.addToBottom(this);
		
		if(location.isDiscard && (oldloc.owner==location.owner)){
			dominionCard.whenSentToDiscard(location.owner,this);
		}
		
		//effects when cards are discarded/trashed (from the hand on the turn player's main phase)
		if(discarded){
			dominionCard.whenDiscarded(location.owner,this);
			for(int x=0;x<location.owner.playedCards.cards.size();x++){
				location.owner.playedCards.cards.get(x).dominionCard.afterDiscard(location.owner,location.owner.playedCards.cards.get(x),this);
			}
			if(oldloc.owner!=null) oldloc.owner.hasDiscarded=true;
		}
		if(trashed){
			dominionCard.whenTrashed(oldloc.owner,this);
			//if(oldloc.isHand){
				for(int x=0;x<oldloc.owner.playedCards.cards.size();x++){
					oldloc.owner.playedCards.cards.get(x).dominionCard.afterTrash(oldloc.owner,oldloc.owner.playedCards.cards.get(x),this);
				}
			//}
			if(oldloc.owner!=null) oldloc.owner.hasTrashed=true;
		}
		
		//When Gained
		if(oldloc.owner!=location.owner) {
			dominionCard.whenGained(location.owner,this);
		}
	}
	
	void send(CardSet destination, boolean onTop){
		//send a card to a location without counting as discarding (or triggering anything other than whenSentToDiscard)
		if(location!=null){
			if(location.cards.contains(this)) location.cards.remove(this);
			else System.out.println("Error: card not present in its location");
		}
		//place in new location
		CardSet oldloc=location;
		location=destination;
		if(onTop) location.addToTop(this);
		else location.addToBottom(this);
		
		if(destination.isDiscard && (oldloc.owner==destination.owner && oldloc.isDiscard==false)){
			dominionCard.whenSentToDiscard(location.owner,this);
		}
	}
	
	void play(Player player){
		dominionCard.play(player,this);
		player.clearSelected();
	}
	boolean canPlay(Player player){
		return dominionCard.canPlay(player,this); 
	}
	
	int getCost(Player player){
		return dominionCard.getCost(player,this);
	}
	
	int getVP(Player player){
		int out=dominionCard.getVP(player,this);
		for(int x=0;x<overlay.cards.size();x++){
			out+=overlay.getVP(player);
		}
		return out;
	}
	
	public String toString(){
		return dominionCard.name;
	}
	

}