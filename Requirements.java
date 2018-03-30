import java.util.ArrayList;
public class Requirements{
	//requirements for any time an effect makes the player select cards
	int min=0;
	int max=100;
	boolean allowAction=true;
	boolean allowTreasure=true;
	boolean allowVictory=true;
	boolean allowCurse=true;
	int mincost=0;
	int maxcost=100;
	int mintotalcost=0;
	int maxtotalcost=100;
	boolean mustBePlayable=false;
	ArrayList<DominionCard> except;
	ArrayList<Card> exceptCard;
	boolean order=false;
	boolean allowPotionCost=true;
	Requirements(int min,int max){
		this.min=min;
		this.max=max;
		except=new ArrayList<DominionCard>();
		exceptCard=new ArrayList<Card>();
	}
	static Requirements playableAction(int min,int max){
		Requirements out=new Requirements(min,max);
		out.allowTreasure=false;
		out.allowVictory=false;
		out.allowCurse=false;
		out.mustBePlayable=true;
		return out;
	}
	boolean check(ArrayList<Card> list, Player player) {
		int found=0;
		//does not support total cost yet
		for(int x=0;x<list.size();x++){
			boolean good=false;
			DominionCard card=list.get(x).dominionCard;
			if(except.contains(card)) continue;//specific disallowed cards
			if(exceptCard.contains(list.get(x))) continue;//used to prevent cards from targeting itself
			if(allowPotionCost==false && card.potionCost) continue;//disallow cards with potion in its cost
			if(mustBePlayable){
				//because this function is called by canPlay, any canPlay function that calls canPlay needs to set requirements
				//to prevent checking itself to prevent infinite loop.
				if(card.canPlay(player,list.get(x))) good=true;
			}else{
				if(allowAction && card.action) good=true;
				if(allowTreasure && card.treasure) good=true;
				if(allowVictory && card.victory) good=true;
				if(allowCurse && card.curse) good=true;
			}
			if(card.getCost(player,list.get(x))<mincost || card.getCost(player,list.get(x))>maxcost) good=false;
			if(good) found+=1;
		}
		return found>=min && found<=max;
	}
	
	boolean check(Card card,Player player) {
		if(except.contains(card.dominionCard)) return false;
		if(exceptCard.contains(card)) return false;
		if(allowPotionCost==false && card.dominionCard.potionCost) return false;
		boolean good=false;
		if(mustBePlayable){
			if(card.canPlay(player)) good=true;
		}else{
			if(allowAction && card.dominionCard.action) good=true;
			if(allowTreasure && card.dominionCard.treasure) good=true;
			if(allowVictory && card.dominionCard.victory) good=true;
			if(allowCurse && card.dominionCard.curse) good=true;
		}
		if(card.dominionCard.getCost(player,card)<mincost || card.dominionCard.getCost(player,card)>maxcost) good=false;
		return good;
	}
}	