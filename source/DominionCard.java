import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.*;
import javax.imageio.ImageIO;

public class DominionCard{
	//A card's profile, not an individial Card
	//functions are overwritten for each card
	private BufferedImage image=null;
	protected String name="???";
	protected int cost=0;
	protected int vp=0;
	protected int value=0;
	protected int supply=7;
	protected boolean reaction=false;
	protected boolean action=false;
	protected boolean attack=false;
	protected boolean victory=false;
	protected boolean treasure=false;
	protected boolean curse=false;
	protected boolean duration=false;
	protected boolean noBM=false;//not in black market (for actions)
	protected boolean actionpile=true;
	protected boolean allowOnline=true;
	protected boolean allowOffline=true;
	protected boolean allowSinglePlayer=true;
	protected boolean potionCost=false;
	protected boolean potion=false;
	protected int index;//used to shorten messages between client and host
	
	private static BufferedImage ImageNotFound=null;

	public DominionCard(){
		
	}
	
	static BufferedImage DefaultImage(){
		if(ImageNotFound==null){
			try{
				ImageNotFound=ImageIO.read(new File("images/cover_real2.png"));
			}catch(Exception ex){
				System.out.println("Default image not found:  images/cover_real2.png");
			}
		}
		return ImageNotFound;
	}
	
	BufferedImage getImage(){
		if(image==null){
			try{
				image=ImageIO.read(new File("images/"+name+".jpg"));
			}catch(IOException ex){
				System.out.println("\nImage not found, "+name);
				image=DefaultImage();
			}
		}
		return image;
	}
	
	CardSet getTrash(Player player){
		//shortcut to finding the trash pile
		return player.theGame.trash;
	}
	
	void setIndex(int i) {
		//for the library to assign an index number to allow for shorter messages and quicker access.
		index=i;
	}
	
	void create(){
		//gets overwridden, called by library after making the cards
	}
	
	void play(Player player, Card instance){
		//overwrite for each card but call super
		//leave the hand and go to the table, subtract 1 action
		if(player.actions>0) player.actions-=1;
		player.usedActions.add(this);
		instance.move(player.playedCards,true);//move from hand to the played cards (placed on top)
		player.message="";
		player.theGame.clearRevealed();
		player.clearSelected();
		player.awaitingResponse=false;
		//check for reactions here
		if(instance.dominionCard.attack) {
			player.gui.reactionChance(player);
		}
		//wait until nobody is awaiting response
		boolean done=false;
		while(done==false) {
			done=true;
			for(int a=0;a<player.theGame.players.size();a++) {
				if(player.theGame.players.get(a).awaitingResponse) done=false;
			}
			if(done==false) Thread.yield();
		}
		
		for(int x=0;x<player.playedCards.cards.size();x++){
			player.playedCards.cards.get(x).dominionCard.afterPlay(player,player.playedCards.cards.get(x));
		}
		player.message="";
	}
	
	void react(Player player, Card instance, Player user){
		player.clearSelected();
		player.awaitingResponse=false;
		player.message="";
		player.theGame.clearRevealed();
	}
	
	
	int getVP(Player player, Card instance){
		//get the victory points the card is worth
		return vp;
	}
	
	int getValue(Player player,Card instance){
		//get the coin value the card is worth
		return value;
	}
	
	int getCost(Player player, Card instance){
		int out = cost+player.costChange;
		if(out<0) out=0;
		return out;
	}
	
	void endTurn(Player player,Card instance){
		//called for each card on the field at the end of the turn for delayed effects
		//after clean up
	}
	
	void beforeBuyPhase(Player player,Card instance) {
		
	}
	
	void beforeCleanup(Player player, Card instance){
		
	}
	void onCleanup(Player player, Card instance) {
		//use to have a card go somewhere other than the discard pile during clean up
	}
	void afterCleanup(Player player, Card instance){
		//called after cleanup but before drawing until you have 5 cards
	}
	
	void whenBought(Player player, Card instance){
		//call after the card is placed on top of the discard when bought (not gained)
	}
	
	void whenGained(Player player, Card instance){
		
	}
	void whenDrawn(Player player, Card instance){
		
	}
	void whenDiscarded(Player player, Card instance){
		//called when moved from the hand to the discard pile except during the turn player's main phase
	}
	void whenTrashed(Player player, Card instance){
		
	}
	void whenSentToDiscard(Player player, Card instance){
		//whenever this card goes to the discard pile regardless of when
	}
	
	
	
	boolean canPlay(Player player, Card instance){
		//overwrite to change activation requirements
		//any canPlay function that calls canPlay or checks the hand for playable cards needs to set requirements to prevent
		//checking itself to prevent infinite loop.
		return action && (player.actions>0);
	}
	
	boolean canReact(Player player, Card instance){
		//can activate reaction card, called for each card in your hand when another player activates an action card, before that card resolves
		return reaction;
	}
	
	
	//"after" functions apply to cards that have effects for the rest of the turn, like Prison Riot
	void afterDiscard(Player player, Card instance, Card discarded){
	}
	void afterDraw(Player player, Card instance, Card drawn){
	}
	void afterTrash(Player player, Card instance, Card trashed){//only from hand
	}
	void afterBuy(Player player, Card instance, Card bought){
	}
	void afterRefresh(Player player,Card instance){
		//call whenever player refreshes and is not awaiting response
		//Do not call refresh in this functions
	}
	void afterPlay(Player player,Card instance){
		//call whenever player refreshes and is not awaiting response
	}
	void afterDiscarding(Player player,Card instance) {
		//Called each time cards are discarded by a card effect while the card is in play, but only once, not for every card discarded.
		//used for Prison Riot
		//These effects should not have any discarding or trashing
	}
	void afterTrashing(Player player,Card instance) {
		//Called each time card(s) are trashed while this card is in play
		//These effects should not have any discarding or trashing
	}
	
	
	
	void durationStartTurn(Player player, Card instance){
		//overwrite for duration cards
		//called for each card in the duration zone
	}
	void durationEndTurn(Player player, Card instance){
	}
	void durationBeforeCleanup(Player player,Card instance){
	}
	void durationShuffleDiscard(Player player, Card instance){
		//made for Dragon Temple
		//called for each duration card when your discard is shuffled and added to the deck
	}
	void durationStartOtherPlayersTurn(Player player,Card instance){
		//the player here is not the turn player, not the card's owner!
	}
	void durationAfterRefresh(Player player,Card instance) {
		//Called each time the turn player refreshes and is not awaiting response
		//Do not call refresh in this code
	}
	
	void onSetup(Game theGame){
		//called when a card is added to the Game
		//used for making a pile of Two-Headed Fish
	}
	
	
}