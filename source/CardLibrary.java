import java.util.ArrayList;
import java.awt.Color;
public class CardLibrary{
	static ArrayList<DominionCard> AllCards;
	public static void setup(){
		//create each card and overwrite its functions in an anonymous class
		AllCards=new ArrayList<DominionCard>();
		
		AllCards.add(new DominionCard(){
			void create(){
				name="Estate";
				cost=2;
				victory=true;
				vp=1;
				supply=30;
				actionpile=false;
			}
		});
		
		AllCards.add(new DominionCard(){
			void create(){
				name="Duchy";
				cost=5;
				victory=true;
				vp=3;
				supply=12;
				actionpile=false;
			}
		});
		
		AllCards.add(new DominionCard(){
			void create(){
				name="Province";
				cost=8;
				victory=true;
				vp=6;
				supply=12;
				actionpile=false;
			}
		});
		AllCards.add(new DominionCard(){
			void create(){
				name="Curse";
				cost=0;
				curse=true;
				vp=-1;
				supply=20;
				actionpile=false;
			}
		});
		
		AllCards.add(new DominionCard(){
			void create(){
				name="Copper";
				cost=0;
				treasure=true;
				value=1;
				supply=30;
				actionpile=false;
			}
		});
		
		AllCards.add(new DominionCard(){
			void create(){
				name="Silver";
				cost=3;
				treasure=true;
				value=2;
				supply=30;
				actionpile=false;
			}
		});
		
		AllCards.add(new DominionCard(){
			void create(){
				name="Gold";
				cost=6;
				treasure=true;
				value=3;
				supply=30;
				actionpile=false;
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Potion";
				cost=4;
				treasure=true;
				value=0;
				potion=true;
				supply=10;
				actionpile=false;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=2;
				player.coins+=1;
				player.DrawCard();
				player.Refresh();
			}
			void create(){
				name="Bazaar";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				player.coins+=1;
				player.buys+=1;
				player.DrawCard();
				player.Refresh();
			}
			void create(){
				name="Market";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				player.DrawCard();
				player.DrawCard();
				player.Refresh();
			}
			void create(){
				name="Laboratory";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=2;
				player.DrawCard();
				player.Refresh();
			}
			void create(){
				name="Village";
				cost=3;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				for(int x=0;x<2;x++) player.DrawCard();
				if(player.usedActions.size()==1){
					for(int x=0;x<2;x++) player.DrawCard();
				}
				player.Refresh();
			}
			void create(){
				name="Old Statues";
				cost=2;
				action=true;
			}
		});
		AllCards.add(new DominionCard(){
			void create(){
				name="Bank";
				cost=7;
				treasure=true;
			}
			int getValue(Player player,Card instance){
				int out=0;
				for(int x=0;x<player.hand.cards.size();x++){
					if(player.hand.cards.get(x).dominionCard.treasure)
						out+=1;
				}
				return out;
			}
		});
		
		AllCards.add(new DominionCard(){
			void create(){
				name="Throne Room";
				cost=4;
				action=true;
			}
			void play(Player player, Card instance){
				super.play(player,instance);
				Requirements req=new Requirements(1,1);//Requirements.playableAction(1,1);
				player.message="Select action card.";
				req.allowTreasure=false;
				req.allowCurse=false;
				req.allowVictory=false;
				req.allowAction=true;
				player.actions+=2;
				ArrayList<Card> sel=player.SelectFromHand(req);
				if(sel.size()>0){
					Card card=sel.get(0);
					card.play(player);
					if(card.canPlay(player))
						card.play(player);
					else player.actions-=1;
				}
			}
			boolean canPlay(Player player, Card instance){
				//require a playable action card in hand
				Requirements req=Requirements.playableAction(1,100);
				for(int a=0;a<player.hand.cards.size();a++) {//needed to prevent infinite loop
					if(player.hand.cards.get(a).dominionCard.name.equals("Throne Room")
							||player.hand.cards.get(a).dominionCard.name.equals("King's Court"))
						req.exceptCard.add(player.hand.cards.get(a));
				}
				return player.CheckHand(req);
			}
		});
		
		AllCards.add(new DominionCard(){
			void create(){
				name="King's Court";
				cost=7;
				action=true;
			}
			void play(Player player, Card instance){
				super.play(player,instance);
				Requirements req=new Requirements(1,1);//Requirements.playableAction(1,1);
				player.message="Select action card.";
				req.allowTreasure=false;
				req.allowCurse=false;
				req.allowVictory=false;
				req.allowAction=true;
				player.actions+=3;
				ArrayList<Card> sel=player.SelectFromHand(req);
				if(sel.size()>0){
					Card card=sel.get(0);
					card.play(player);
					if(card.canPlay(player))
						card.play(player);
					else player.actions-=1;
					if(card.canPlay(player))
						card.play(player);
					else player.actions-=1;
				}
			}
			boolean canPlay(Player player, Card instance){
				//require a playable action card in hand
				Requirements req=Requirements.playableAction(1,100);
				for(int a=0;a<player.hand.cards.size();a++) {//needed to prevent infinite loop
					if(player.hand.cards.get(a).dominionCard.name.equals("Throne Room")
							|| player.hand.cards.get(a).dominionCard.name.equals("King's Court"))
						req.exceptCard.add(player.hand.cards.get(a));
				}
				return player.CheckHand(req);
			}
		});
		
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				Requirements req=new Requirements(2,2);
				req.allowVictory=false;
				req.allowCurse=false;
				req.allowAction=false;
				req.allowTreasure=true;
				player.RevealUntilFound(req);
				for(int x=0;x<player.revealedCards.size();x++) player.revealedCards.get(x).move(player.hand,false);
				player.Refresh();
			}
			void create(){
				name="Adventurer";
				cost=6;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="Select cards to trash. (1-4)";
				Requirements req=new Requirements(1,4);//select 1 to 4 cards
				ArrayList<Card> sel=player.SelectFromHand(req);
				for(int x=0;x<sel.size();x++){
					sel.get(x).move(getTrash(player),true);//trash cards
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Chapel";
				cost=2;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.coins+=2;
				player.actions+=1;
				player.RevealCards(6);
				player.message="Select sort order.";
				ArrayList<Card> set=player.OrderCards(player.revealedCards);
				while(set.size()>0){
					set.get(set.size()-1).move(player.deck,true);
					set.remove(set.size()-1);
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Ancient Wisdom";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.buys+=1;
				player.message="";
				int hasEstate=-1;
				for(int x=0;x<player.hand.cards.size();x++){
					if(player.hand.cards.get(x).dominionCard.name.equals("Estate"))
						hasEstate=x;
				}
				int choice=1;
				if(hasEstate>=0)
					choice=player.Prompt("Select effect:",new String[]{"Discard an Estate for +4 coins","Gain an Estate"});
				if(choice==0){
					player.hand.cards.get(hasEstate).move(player.discard,true);
					player.coins+=4;
				}else{
					if(player.theGame.estatePile.cardsLeft()>0)
						player.theGame.estatePile.pile.cards.get(0).move(player.discard,true);
				}
				player.Refresh();
			}
			void create(){
				name="Baron";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.buys+=1;
				player.DrawCard();
				player.DrawCard();
				player.message="Select cards to discard (if any).";
				ArrayList<Card> set=player.SelectFromHand(new Requirements(0,100));
				for(int x=0;x<set.size();x++){
					set.get(x).move(player.discard,true);
					player.coins+=1;
				}
				player.Refresh();
			}
			void create(){
				name="Boutique";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.buys+=1;
				player.coins+=1;
				player.costChange-=1;
				player.Refresh();
			}
			void create(){
				name="Bridge";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				player.message="Select cards to discard (if any).";
				ArrayList<Card> set=player.SelectFromHand(new Requirements(0,100));
				for(int x=0;x<set.size();x++){
					set.get(x).move(player.discard,true);
					player.DrawCard();
				}
				player.Refresh();
			}
			void create(){
				name="Cellar";
				cost=2;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.coins+=2;
				if(player.playedCards.cards.size()>=3){
					player.actions+=1;
					player.DrawCard();
				}
				player.Refresh();
			}
			void create(){
				name="Conspirator";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				for(int x=0;x<3;x++) player.DrawCard();
				player.message="Select card to place on top of deck.";
				ArrayList<Card> set=player.SelectFromHand(new Requirements(1,1));
				if(set.size()>0) set.get(0).move(player.deck,true);
				player.Refresh();
			}
			void create(){
				name="Courtyard";
				cost=2;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				//trash a card and gain one costing up to 2 more than the trashed card
				player.message="Select a card to trash.";
				ArrayList<Card> set=player.SelectFromHand(new Requirements(1,1));
				player.clearSelected();
				if(set.size()>0){
					player.message="Select a card to gain.";
					int maxprice=set.get(0).getCost(player)+2;
					set.get(0).move(getTrash(player),true);
					Requirements req=new Requirements(0,1);
					req.maxcost=maxprice;
					req.allowPotionCost=false;
					set=player.SelectFromSupply(req);
					if(set.size()>0){//gain the card
						set.get(0).move(player.discard,true);
					}
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Remodel";
				cost=4;
				action=true;
			}
			boolean canPlay(Player player, Card instance){
				//requires another card in hand
				return player.hand.cards.size()>1;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.actions+=1;
				player.Refresh();
				//trash a card and gain one costing up to 2 more than the trashed card
				player.message="Select a card to trash.";
				ArrayList<Card> set=player.SelectFromHand(new Requirements(1,1));
				player.clearSelected();
				if(set.size()>0){
					player.message="Select a card to gain.";
					int maxprice=set.get(0).getCost(player)+1;
					set.get(0).move(getTrash(player),true);
					Requirements req=new Requirements(0,1);
					req.maxcost=maxprice;
					req.mincost=maxprice;
					req.allowPotionCost=false;
					set=player.SelectFromSupply(req);
					if(set.size()>0){//gain the card
						set.get(0).move(player.discard,true);
					}
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Upgrade";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				player.coins+=2;
				player.endTurnDraws-=1;
				if(player.endTurnDraws<0) player.endTurnDraws=0;
				player.Refresh();
			}
			void create(){
				name="Debtor's Prison";
				cost=3;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				player.coins+=1;
				player.buys+=1;
				player.Refresh();
			}
			void beforeCleanup(Player player, Card instance){
				//after you buy you may place one of the bought cards on top of your deck
				player.gui.window.clearScreen(Color.BLACK);
				player.gui.window.sync(40);
				player.message="Select cards to place on top of deck, if any.";
				if(player.boughtCards.size()>0){
					ArrayList<Card> set=player.SelectFromList(player.boughtCards,new Requirements(0,100));
					if(set.size()>0){
						set.get(0).move(player.deck,true);
					}
					player.clearSelected();
					player.Refresh();
				}
			}
			void create(){
				name="Delivery System";
				cost=3;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=2;
				player.buys+=1;
				player.coins+=2;
				player.Refresh();
			}
			void create(){
				name="Festival";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=2;
				player.message="Discard 1 card";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,1));
				if(sel.size()>0){
					sel.get(0).move(player.discard,true);
					Requirements req=new Requirements(2,2);
					req.allowVictory=false;
					req.allowCurse=false;
					req.allowAction=true;
					req.allowTreasure=false;
					player.RevealUntilFound(req);
					for(int x=0;x<player.revealedCards.size();x++) player.revealedCards.get(x).move(player.hand,false);
				}
				player.Refresh();
			}
			boolean canPlay(Player player, Card instance){
				//must have another card in hand
				return player.hand.cards.size()>1;
			}
			void create(){
				name="Customs Inspection";
				cost=6;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				player.message="Select card to add to hand";
				//move the top 2 cards of the black market aside
				for(int x=0;x<2;x++){
					if(player.theGame.blackMarket.cards.size()>0){
						player.theGame.blackMarket.cards.get(0).move(player.aside,true);
					}
				}
				ArrayList<Card> set=player.SelectFromList(player.aside.checkTop(2),new Requirements(1,1));
				for(int x=0;x<player.aside.cards.size();x++){
					player.aside.cards.get(x).move(player.theGame.blackMarket,false);
				}
				if(set.size()>0){
					set.get(0).move(player.hand,false);
					set.get(0).temporary=true;
				}
				player.message="";
				player.clearSelected();
				player.Refresh();
			}
			void create(){
				name="Dancing Monkeys";
				cost=4;
				action=true;
				noBM=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				//trash a card and gain one costing up to 2 more than the trashed card
				player.message="Select a card to trash.";
				ArrayList<Card> set=player.SelectFromHand(new Requirements(1,1));
				if(set.size()>0){
					player.message="Select a card to gain.";
					int maxprice=set.get(0).getCost(player)+3;
					set.get(0).move(getTrash(player),true);
					Requirements req=new Requirements(1,1);
					req.maxcost=maxprice;
					req.allowPotionCost=false;
					set=player.SelectFromSupply(req);
					if(set.size()>0){//gain the card
						set.get(0).move(player.discard,true);
					}
				}
				player.Refresh();
			}
			void create(){
				name="Expand";
				cost=7;
				action=true;
			}
			boolean canPlay(Player player, Card instance){
				//requires another card in hand
				return player.hand.cards.size()>1;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				// if your hand contains province, reveal it and gain a gold in hand, else gain a silver in hand
				boolean hasProvince=false;
				for(int x=0;x<player.hand.cards.size();x++){
					if(player.hand.cards.get(x).dominionCard.name.equals("Province"))
						hasProvince=true;
				}
				Card card;
				if(hasProvince) card=player.theGame.findSupply(findCard("Gold")).topCard();
				else card=player.theGame.findSupply(findCard("Silver")).topCard();
				if(card!=null){
					card.move(player.hand,false);
				}
				player.Refresh();
			}
			void create(){
				name="Explorer";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=2;
				player.coins+=1;
				player.Refresh();
			}
			void durationStartTurn(Player player, Card instance){
				player.actions+=1;
				player.coins+=1;
				instance.move(player.discard,true);
			}
			void create(){
				name="Fishing Village";
				cost=3;
				action=true;
				duration=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				instance.counters=0;
				player.endTurnDraws-=1;
				if(player.endTurnDraws<2) player.endTurnDraws=2;
				//no effect on activation
				player.Refresh();
			}
			void durationStartTurn(Player player, Card instance){
				instance.counters+=2;
			}
			void durationBeforeCleanup(Player player, Card instance){
				player.endTurnDraws-=1;
				if(player.endTurnDraws<2) player.endTurnDraws=2;
			}
			void durationShuffleDiscard(Player player, Card instance){
				player.victoryPoints+=instance.counters;
				instance.counters=0;
				instance.move(player.discard,true);
				player.Refresh();
			}
			void create(){
				name="Dragon Temple";
				cost=7;
				action=true;
				duration=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				int num=0;
				for(int x=0;x<player.playedCards.cards.size();x++){
					if(player.playedCards.cards.get(x).dominionCard.name.equals("Freedom Fighter"))
						num+=1;
				}
				if(num>3) num=3;
				player.coins+=num;
				player.actions+=1;
				player.Refresh();
			}
			void whenBought(Player player, Card instance){
				player.costChange+=1;
				player.buys+=1;
				
			}
			void create(){
				name="Freedom Fighter";
				cost=2;
				supply=12;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="Select a card to trash.";
				ArrayList<Card> set=player.SelectFromHand(new Requirements(1,1));
				player.message="";
				if(set.size()>0){
					set.get(0).move(getTrash(player),true);
					int choice=player.Prompt("Name Copper or Estate.",new String[]{"Copper","Estate"});
					boolean found=false;
					for(int x=0;x<6;x++){
						player.DrawCard();
						Card drawn = player.hand.cards.get(player.hand.cards.size()-1);
						if((choice==0 && drawn.dominionCard.name.equals("Copper")) || (choice==1 && drawn.dominionCard.name.equals("Estate"))){
							found=true;
							break;
						}
					}
					if(found==false){//failed, discard all
						while(player.hand.cards.size()>0)
							player.hand.cards.get(0).send(player.discard,true);
						player.buys=0; player.actions=0;//essentially ends the turn
						player.message="You did not draw the named card.";
					}
				}
				player.Refresh();
			}
			boolean canPlay(Player player, Card instance){
				return player.hand.cards.size()>1;
			}
			void create(){
				name="Gambler";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.actions+=1;
				player.Refresh();
			}
			void create(){
				name="Great Hall";
				cost=3;
				vp=1;
				action=true;
				victory=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void create(){
				name="Harem";
				cost=3;
				value=2;
				vp=2;
				treasure=true;
				victory=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				player.RevealCards(5);
				for(int x=0;x<player.revealedCards.size();x++){
					player.revealedCards.get(x).move(player.aside,true);
				}
				player.message="Select 2 cards to add to hand.";
				ArrayList<Card> sel=player.SelectFromList(player.aside.checkTop(5),new Requirements(2,2));
				for(int x=0;x<sel.size();x++){
					sel.get(x).move(player.hand,false);
				}
				player.message="Select order to return cards.";
				ArrayList<Card> set=player.OrderCards(player.aside.checkTop(3));
				while(set.size()>0){
					set.get(set.size()-1).move(player.deck,true);
					set.remove(set.size()-1);
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Helmsman";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="";
				boolean done=false;
				while(done==false){
					boolean drew=player.DrawCard();
					Card drawn=player.hand.cards.get(player.hand.cards.size()-1);
					if(drawn.dominionCard.action && drew){
						player.Refresh();
						int choice=player.Prompt("Drew a "+drawn.dominionCard.name+".  Keep it?",new String[]{"Yes","No"});
						if(choice==1){//the cards do need to be moved aside so they don't count as discarded from the hand
							drawn.move(player.aside,true);
						}
						
					}
					if(drew==false || player.hand.cards.size()>=7) 
						done=true;
				}
				//dump all set aside cards into the discard pile
				while(player.aside.cards.size()>0)
					player.aside.cards.get(0).move(player.discard,true);
				player.Refresh();
			}
			
			boolean canPlay(Player player, Card instance){
				//less than 7 other cards in hand
				return player.hand.cards.size()<=7;
			}
			
			void create(){
				name="Library";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=2;
				player.message="";
				int choice=1;
				if(player.hand.cards.size()>0){
					choice=player.Prompt("Discard your hand?",new String[]{"Yes","No"});
				}
				if(choice==0){
					int num=0;
					while(player.hand.cards.size()>0){
						player.hand.cards.get(0).move(player.discard,true);
						num+=1;
					}
					num+=1;
					if(num>5) num=5;
					for(int x=0;x<num;x++) player.DrawCard();
				}
				
				player.Refresh();
			}
			void create(){
				name="Market Crash";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				for(int x=0;x<player.hand.cards.size();x++){
					if(player.hand.cards.get(x).dominionCard.name.equals("Copper")){
						player.hand.cards.get(x).move(getTrash(player),true);
						player.coins+=3;
						break;
					}
				}
				player.Refresh();
			}
			boolean canPlay(Player player, Card instance){
				//requires a copper in hand
				for(int x=0;x<player.hand.cards.size();x++){
					if(player.hand.cards.get(x).dominionCard.name.equals("Copper"))
						return true;
				}
				return false;
			}
			void create(){
				name="Moneylender";
				cost=4;
				action=true;
			}
		});
		
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="";
				player.actions+=1;
				int choice=player.Prompt("Choose effect:",new String[]{"Discard 0-Cost and Estate cards","Trash this card to gain a Gold"});
				if(choice==0){
					player.message="Select cards to discard (0-4).";
					Requirements req=new Requirements(0,4);
					//so I don't have to change how requirements work
					for(int x=0;x<player.hand.cards.size();x++){
						if(player.hand.cards.get(x).dominionCard.name.equals("Estate") || player.hand.cards.get(x).dominionCard.cost==0){
						}else{
							req.exceptCard.add(player.hand.cards.get(x));
						}
					}
					ArrayList<Card> set=player.SelectFromHand(req);
					if(set.size()==4) player.coins+=2;
					for(int x=0;x<set.size();x++){
						set.get(x).move(player.discard,true);
						player.DrawCard();
					}
				}else{
					instance.move(getTrash(player),true);
					Card card=player.theGame.findSupply(findCard("Gold")).topCard();
					if(card!=null)
						card.move(player.discard,true);
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Monkey Idol";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.victoryPoints+=1;
				player.coins+=2;
				player.Refresh();
			}

			void create(){
				name="Monument";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="Discard up to 3 cards.";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,3));
				for(int x=0;x<sel.size();x++){
					sel.get(x).move(player.discard,true);
				}
				instance.counters=sel.size();
				player.Refresh();
			}
			
			void endTurn(Player player, Card instance){
				for(int x=0;x<2+instance.counters;x++)
					player.DrawCard();
			}
			
			boolean canPlay(Player player, Card instance){
				return player.hand.cards.size()>1;
			}

			void create(){
				name="Neutral Jing";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="";
				int choice=player.Prompt("Select effect:",new String[]{"+2 Actions","+3 Cards"});
				if(choice==0) player.actions+=2;
				if(choice==1){
					for(int x=0;x<3;x++){
						player.DrawCard();
					}
				}
				player.Refresh();
			}

			void create(){
				name="Nobles";
				cost=6;
				action=true;
				victory=true;
				vp=2;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="";
				int choice=player.Prompt("Choose first effect:",new String[]{"+1 Action","+1 Card","+1 Coin","+1 Buy"});
				if(choice==0){
					player.actions+=1;
					int choice2=player.Prompt("Choose second effect:",new String[]{"+1 Card","+1 Coin","+1 Buy"});
					if(choice2==0) player.DrawCard();
					if(choice2==1) player.coins+=1;
					if(choice2==2) player.buys+=1;
				}
				if(choice==1){
					player.DrawCard();
					int choice2=player.Prompt("Choose second effect:",new String[]{"+1 Action","+1 Coin","+1 Buy"});
					if(choice2==0) player.actions+=1;
					if(choice2==1) player.coins+=1;
					if(choice2==2) player.buys+=1;
				}
				if(choice==2){
					player.coins+=1;
					int choice2=player.Prompt("Choose second effect:",new String[]{"+1 Action","+1 Card","+1 Buy"});
					if(choice2==0) player.actions+=1;
					if(choice2==1) player.DrawCard();
					if(choice2==2) player.buys+=1;
				}
				if(choice==3){
					player.buys+=1;
					int choice2=player.Prompt("Choose second effect:",new String[]{"+1 Action","+1 Card","+1 Coin"});
					if(choice2==0) player.actions+=1;
					if(choice2==1) player.DrawCard();
					if(choice2==2) player.coins+=1;
				}
				player.Refresh();
			}

			void create(){
				name="Pawn";
				cost=2;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				player.DrawCard();
				if(player.deck.cards.size()==0) player.AddDiscardToDeck();
				player.Refresh();
				Card card=player.deck.cards.get(player.deck.cards.size()-1);
				player.message="";
				int choice=player.Prompt("Move "+card.dominionCard.name+" to the top of the deck?",new String[]{"Yes","No"});
				if(choice==0){
					card.move(player.deck,true);
				}
				player.Refresh();
			}

			void create(){
				name="Pearl Diver";
				cost=2;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				player.DrawCard();
				player.Refresh();
			}
			void whenDiscarded(Player player, Card instance){
				//player.DrawCard();
				player.drawPlan+=1;
				player.coins+=1;
			}
			void whenTrashed(Player player, Card instance){
				player.DrawCard();
				player.DrawCard();
				Card card=player.theGame.findSupply(findCard("Gold")).topCard();
				if(card!=null)
					card.move(player.discard,true);
			}
			void create(){
				name="Prototype";
				cost=2;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.coins+=2;
				if(player.discard.cards.size()>0) {
					Requirements req=new Requirements(1,10000);
					ArrayList<Card> list=player.GetDifferentCards(player.discard.cards,req);
					if(list.size()>0){
						player.message="Select a card to move all copies from discard to deck";
						ArrayList<Card> sel=player.SelectFromList(list,new Requirements(1,1));
						player.clearSelected();
						player.revealedCards.clear();
						for(int x=0;x<player.discard.cards.size();x++){
							if(player.discard.cards.get(x).dominionCard==sel.get(0).dominionCard)
								player.revealedCards.add(player.discard.cards.get(x));
						}
						for(int x=0;x<player.revealedCards.size();x++){
							player.revealedCards.get(x).move(player.deck,true);
							player.ShuffleDeck();
						}
					}
					player.message="";
				}
				player.DrawCard();
				player.Refresh();
			}
			void create(){
				name="Royal Advisor";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.buys+=1;
				if(player.hand.cards.size()>0){
					player.message="Select a card to trash";
					ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,1));
					if(sel.size()>0){
						sel.get(0).move(getTrash(player),true);
						player.coins+=sel.get(0).getCost(player);
					}
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Salvager";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.coins+=1;
				player.message="";
				Card gold=player.theGame.findSupply(findCard("Gold")).topCard();
				Card curse=player.theGame.findSupply(findCard("Curse")).topCard();
				if(curse!=null){
					int choice=player.Prompt("Place the gained Curse on top of your deck?",new String[]{"Yes","No"});
					if(choice==0)
						curse.move(player.deck,true);
					else curse.move(player.discard,true);
				}
				if(gold!=null){
					int choice=player.Prompt("Place the gained Gold on top of your deck?",new String[]{"Yes","No"});
					if(choice==0)
						gold.move(player.deck,true);
					else gold.move(player.discard,true);
				}
				player.Refresh();
			}

			void create(){
				name="Sarcasm";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				//doesn't reveal your hand
				player.actions+=2;
				Requirements req=new Requirements(1,1000);
				req.allowAction=true;
				req.allowVictory=false;
				req.allowCurse=false;
				req.allowTreasure=false;
				if(player.CheckHand(req)==false){
					player.DrawCard();
					player.DrawCard();
				}
				player.Refresh();
			}
			void create(){
				name="Shanty Town";
				cost=3;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				player.DrawCard();
				player.Refresh();
			}
			void create(){
				name="Smithy";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="";
				int choice=player.Prompt("Choose 1:",new String[]{"+2 Cards","+2 Coins","Trash 2 Cards"});
				if(choice==0){
					player.DrawCard();
					player.DrawCard();
				}
				if(choice==1)
					player.coins+=2;
				if(choice==2 && player.hand.cards.size()>=2){
					ArrayList<Card> sel=player.SelectFromHand(new Requirements(2,2));
					for(int x=0;x<sel.size();x++){
						sel.get(x).move(getTrash(player),true);
					}
				}
				player.Refresh();
			}
			void create(){
				name="Steward";
				cost=3;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.message="Select 2 cards";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(2,2));
				for(int x=0;x<sel.size();x++){
					sel.get(x).move(instance.overlay,true);
				}
				player.endTurnDraws-=1;
				player.Refresh();
			}
			void durationStartTurn(Player player, Card instance){
				while(instance.overlay.cards.size()>0){
					instance.overlay.cards.get(0).move(player.hand,false);
				}
				instance.move(player.discard,true);
				player.Refresh();
			}
			boolean canPlay(Player player, Card instance){
				return player.hand.cards.size()>1;
			}
			void create(){
				name="Submarine";
				cost=3;
				duration=true;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="";
				player.actions+=1;
				int choice=player.Prompt("Shuffle your discard pile into your deck?",new String[]{"Yes","No"});
				if(choice==0){
					player.AddDiscardToDeck();
					player.ShuffleDeck();
				}
				Requirements req=new Requirements(1,10000);
				ArrayList<Card> list=player.GetDifferentCards(player.deck.cards,req);
				if(list.size()>0){
					player.message="Select a card to move up to 5 copies to discard";
					ArrayList<Card> sel=player.SelectFromList(list,new Requirements(1,1));
					player.revealedCards.clear();
					for(int x=0;x<player.deck.cards.size() && player.revealedCards.size()<5;x++){
						if(player.deck.cards.get(x).dominionCard==sel.get(0).dominionCard)
							player.revealedCards.add(player.deck.cards.get(x));
					}
					for(int x=0;x<player.revealedCards.size();x++){
						player.revealedCards.get(x).send(player.discard,true);
						player.ShuffleDeck();
					}
					player.Refresh();
					if(player.revealedCards.size()==5){
						if(player.revealedCards.get(0).dominionCard.name.equals("Copper")==false){
							//add one action card from deck to hand
							player.message="Select card to add to your hand.";
							ArrayList<Card> options = new ArrayList<Card>();
							for(Card c:player.deck.cards) {
								if(c.dominionCard.action)
									options.add(c);
							}
							for(Card c:player.discard.cards) {
								if(c.dominionCard.action)
									options.add(c);
							}
							ArrayList<Card> set = player.SelectFromList(options,new Requirements(1,1));
							player.clearSelected();
							if(set.size()>0){
								set.get(0).move(player.hand,true);
							}
						}
					}
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Sword Master";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				player.coins+=1;
				//count the Estates in hand
				int count=0;
				for(int x=0;x<player.hand.cards.size();x++){
					if(player.hand.cards.get(x).dominionCard.name.equals("Estate"))
						count+=1;
				}
				//When you buy an estate +1 buy
				//effect is in Estate
				player.teaShops=1;//no longer stacks
				//draw cards
				if(count>4) count=4;
				for(int x=0;x<count;x++)
					player.DrawCard();
				player.Refresh();
			}
			void afterDraw(Player player, Card instance, Card drawn){
				//for the rest of this turn when you draw an estate +1 coin
				if(drawn.dominionCard.name.equals("Estate"))
					player.coins+=1;
			}
			void afterBuy(Player player, Card instance, Card bought){
				if(bought.dominionCard.name.equals("Estate")){
					if(bought.getCost(player)==2){
						player.buys+=1;
					}
				}
			}
			void create(){
				name="Tea Shop";
				cost=2;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="Trash 2 cards.";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(2,2));
				for(int x=0;x<sel.size();x++){
					sel.get(x).move(getTrash(player),true);
				}
				if(sel.size()==2){
					Card card=player.theGame.findSupply(findCard("Silver")).topCard();
					if(card!=null)
						card.move(player.hand,false);
				}
				player.message="";
				player.Refresh();
			}
			boolean canPlay(Player player, Card instance){
				return player.hand.cards.size()>2;
			}
			void create(){
				name="Trading Post";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				instance.move(getTrash(player),true);
				for(int x=0;x<player.hand.cards.size();x++){
					if(player.hand.cards.get(x).dominionCard.name.equals("Treasure Map")){
						player.hand.cards.get(x).move(getTrash(player),true);
						for(int y=0;y<4;y++){
							Card card=player.theGame.findSupply(findCard("Gold")).topCard();
							if(card!=null)
								card.move(player.deck,true);
						}
						break;
					}
				}
				player.Refresh();
			}
			boolean canPlay(Player player, Card instance){
				//must have atleast 2 treasue maps in hand
				int maps=0;
				for(int x=0;x<player.hand.cards.size();x++){
					if(player.hand.cards.get(x).dominionCard.name.equals("Treasure Map"))
						maps+=1;
				}
				return maps>=2;
			}
			void create(){
				name="Treasure Map";
				cost=4;
				action=true;
				noBM=true;
				supply=8;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.coins*=2;
				player.buys+=1;
				for(int x=0;x<player.playedCards.cards.size()-1;x++){
					player.DrawCard();
				}
				player.Refresh();
			}
			boolean canPlay(Player player, Card instance){
				//can only activate once per turn
				for(int x=0;x<player.playedCards.cards.size();x++){
					if(player.playedCards.cards.get(x).dominionCard.name.equals("Tsungi Horn"))
						return false;
				}
				return true;
			}
			void create(){
				name="Tsungi Horn";
				cost=7;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				player.DrawCard();
				player.actions+=1;
				player.message="Discard 3 cards";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(3,3));
				for(int x=0;x<sel.size();x++){
					sel.get(x).move(player.discard,true);
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Warehouse";
				cost=3;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				int choice=0;
				Requirements req=new Requirements(1,100);
				req.allowCurse=true;
				req.allowAction=false;
				req.allowTreasure=false;
				req.allowVictory=false;
				if(player.CheckHand(req))
					choice=player.Prompt("Select an effect:",new String[]{"+1 VP, +1 Card, and gain a Curse.","Return Curses to supply pile for +2 cards and +2 coin for each."});
				if(choice==0){
					player.victoryPoints+=1;
					player.actions+=1;
					Card card=player.theGame.findSupply(findCard("Curse")).topCard();
					if(card!=null)
						card.move(player.discard,true);
				}else{
					req.max=2;
					req.min=0;
					player.message="Return curse cards";
					ArrayList<Card> sel=player.SelectFromHand(req);
					for(int x=0;x<sel.size();x++){
						sel.get(x).move(player.theGame.findSupply(findCard("Curse")).pile,true);
						player.coins+=2;
						player.DrawCard();
						player.DrawCard();
						player.actions+=1;
					}
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Wheel of Punishment";
				cost=3;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.buys+=1;
				for(int x=0;x<player.playedCards.cards.size()-1;x++)
					player.DrawCard();
				player.Refresh();
			}
			void create(){
				name="White Lotus Tile";
				cost=2;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.buys+=1;
				player.coins+=2;
				player.Refresh();
			}
			void create(){
				name="Woodcutter";
				cost=3;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				player.DrawCard();
				player.Refresh();
			}
			void durationStartTurn(Player player,Card instance){
				player.DrawCard();
				instance.move(player.discard,true);
				player.Refresh();
			}
			void create(){
				name="Caravan";
				cost=4;
				duration=true;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.coins+=2;
				player.Refresh();
			}
			void durationStartTurn(Player player,Card instance){
				player.coins+=2;
				instance.move(player.discard,true);
				player.Refresh();
			}
			void create(){
				name="Merchant Ship";
				cost=5;
				duration=true;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.coins+=2;
				player.message="Select a card to buy (if any).";
				ArrayList<Card> drawn=new ArrayList<Card>();
				for(int x=0;x<3;x++){//set aside 3 cards from the Black Market Deck;
					if(player.theGame.blackMarket.cards.size()>0){
						drawn.add(player.theGame.blackMarket.cards.get(0));
						player.theGame.blackMarket.cards.get(0).move(player.aside,true);
					}
				}
				Requirements req=new Requirements(0,1);
				req.maxcost=player.GetCoins();
				req.allowPotionCost=player.hand.contains("Potion");
				ArrayList<Card> sel=player.SelectFromList(drawn,req);
				for(int x=0;x<drawn.size();x++){
					drawn.get(x).move(player.theGame.blackMarket,false);//move them to the bottom of the black market deck
				}
				if(sel.size()>0){
					sel.get(0).move(player.discard,true);
					player.coins-=sel.get(0).dominionCard.getCost(player,sel.get(0));
				}
				player.clearSelected();
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Black Market";
				cost=4;
				action=true;
				noBM=true;
			}
		});
		
		/*AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.victoryPoints+=1;
				//make players reveal cards
				ArrayList<Player> P = player.getAllOtherPlayers();
				for(int x=0;x<P.size();x++){
					P.get(x).RevealCards(2);
				}
				player.gui.showPlayersRevealed(player,true);
				for(int x=0;x<P.size();x++){
					P.get(x).revealedCards.clear();
				}
				player.Refresh();
			}
			void create(){
				name="Ancient Knowledge";
				cost=4;
				action=true;
			}
		});*/
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.revealedCards.clear();
				//make players reveal cards
				ArrayList<Player> P = player.getAllOtherPlayers();
				for(int x=0;x<P.size();x++){
					if(P.get(x).moat==false)
						P.get(x).RevealCards(2);
				}
				player.gui.showPlayersRevealed(player,true);
				Requirements req1=new Requirements(1,2);//for trashing a card
				req1.allowAction=true; req1.allowCurse=false;
				req1.allowTreasure=false; req1.allowVictory=false;
				Requirements req2=new Requirements(2,100);
				req2.allowAction=true; req2.allowCurse=false;
				req2.allowTreasure=false; req2.allowVictory=false;
				ArrayList<Card> trashed=new ArrayList<Card>();
				for(int x=0;x<P.size();x++){
					if(P.get(x).CheckRequirements(P.get(x).revealedCards,req1)){//has an action card revealed
						if(P.get(x).CheckRequirements(P.get(x).revealedCards,req2)){//had 2 action cards revealed
							//ask which to trash
							player.message="Select which card to trash from "+P.get(x).getName()+".";
							ArrayList<Card> sel=player.SelectFromList(P.get(x).revealedCards,req1);
							if(sel.size()>0){
								trashed.add(sel.get(0));
							}
						}else{//trash the one action card
							for(int a=0;a<P.get(x).revealedCards.size();a++){
								if(P.get(x).revealedCards.get(a).dominionCard.action){
									trashed.add(P.get(x).revealedCards.get(a));
								}
							}
						}
					}
					for(int a=0;a<P.get(x).revealedCards.size();a++){
						if(trashed.contains(P.get(x).revealedCards.get(a))){//trash the card
							P.get(x).revealedCards.get(a).move(getTrash(P.get(x)),true);
						}else{//discard the card
							P.get(x).revealedCards.get(a).move(P.get(x).discard,true);
						}
					}
				}
				player.Refresh();
				//gain one of the trashed cards
				if(trashed.size()>0) {
					player.message="Select a card to gain and add to hand (if any).";
					ArrayList<Card> sel=player.SelectFromList(trashed,new Requirements(0,1));
					player.clearSelected();
					if(sel.size()>0){
						sel.get(0).move(player.hand,false);
						if(player.buys>0) player.buys-=1;
					}
				}
				for(int x=0;x<P.size();x++){
					P.get(x).revealedCards.clear();
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Assassin";
				cost=5;
				action=true;
				attack=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.buys+=1;
				for(int x=0;x<4;x++)
					player.DrawCard();
				for(int x=0;x<player.theGame.players.size();x++)
					player.theGame.players.get(x).DrawCard();
				player.Refresh();
			}
			void create(){
				name="Council Room";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				ArrayList<Player> P=player.getAllOtherPlayers();
				for(int x=0;x<P.size();x++){
					if(P.get(x).moat==false) {
						P.get(x).revealHand();
						for(int y=P.get(x).hand.cards.size()-1;y>=0;y--){
							if(P.get(x).hand.cards.get(y).dominionCard.treasure)
								P.get(x).hand.cards.get(y).move(P.get(x).discard,true);
						}
						while(P.get(x).hand.cards.size()<4)
							P.get(x).DrawCard();
					}
				}
				player.gui.showPlayersRevealed(player,true);
				player.Refresh();
			}

			void create(){
				name="Pirates";
				cost=5;
				action=true;
				attack=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=2;
				if(player.hand.cards.size()>0) {
					player.message="Discard 1 card to draw 1 card (optional)";
					ArrayList<Card> sel=player.SelectFromHand(new Requirements(0,1));
					player.clearSelected();
					if(sel.size()>0) {
						sel.get(0).move(player.discard,true);
						player.DrawCard();
						player.coins-=1;//because the after discarding effect isn't supposed to apply until after this
					}
					
				}
				player.message="";
				player.Refresh();
			}
			void afterDiscarding(Player player, Card instance){
				player.coins+=1;
			}
			void afterTrashing(Player player, Card instance){
				player.coins+=1;
			}
			void create(){
				name="Prison Riot";
				//Effect stacks but won't work with throne room
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				instance.move(player.theGame.findSupply(findCard("Two-Headed Fish")).pile,true);
				player.Refresh();
			}
			void whenTrashed(Player player, Card instance){
				instance.move(player.theGame.findSupply(findCard("Two-Headed Fish")).pile,true);
			}
			void create(){
				name="Two-Headed Fish";
				cost=0;
				action=true;
				curse=true;
				noBM=true;
				supply=30;
				actionpile=false;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=1;
				ArrayList<Player> P=player.getAllOtherPlayers();
				for(int x=0;x<P.size();x++){
					if(P.get(x).moat==false) {
						Card card=null;
						for(int a=0;a<player.hand.cards.size();a++) {//default give the fish from your hand
							if(player.hand.cards.get(a).dominionCard.name.equals("Two-Headed Fish"))
								card=player.hand.cards.get(a);
						}
						if(card==null) {
							SupplyPile fish=player.theGame.findSupply(findCard("Two-Headed Fish"));
							if(fish!=null) {
								card=fish.topCard();
							}
						}
						if(card!=null){
							card.move(P.get(x).discard,true);
						}

					}
				}
				player.Refresh();
			}
			void onSetup(Game theGame){
				theGame.basicPiles.add(new SupplyPile(CardLibrary.findCard("Two-Headed Fish"),theGame));
			}
			void create(){
				name="River Ferry";
				cost=2;
				action=true;
				attack=true;
				noBM=true;//getting from black market causes a glitch since there is no fish pile
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				int choice=2;
				player.message="";
				if(player.hand.cards.size()>=6){
					choice=player.Prompt("Select an effect:",new String[]{"Discard 6 cards to gain a Duchy.","Trash 3 Cards to gain a Duchy.","Nothing"});
				}else if(player.hand.cards.size()>=3){
					choice=1+player.Prompt("Select an effect:",new String[]{"Trash 3 Cards to gain a Duchy.","Nothing"});
				}
				if(choice==0){//discard 6 cards
					ArrayList<Card> sel=player.SelectFromHand(new Requirements(6,6));
					for(int x=0;x<sel.size();x++){
						sel.get(x).move(player.discard,true);
					}
					Card card=player.theGame.findSupply(findCard("Duchy")).topCard();
					if(card!=null){
						card.move(player.discard,true);
					}
				}
				if(choice==1){//Trash 3 cards
					ArrayList<Card> sel=player.SelectFromHand(new Requirements(3,3));
					for(int x=0;x<sel.size();x++){
						sel.get(x).move(getTrash(player),true);
					}
					Card card=player.theGame.findSupply(findCard("Duchy")).topCard();
					if(card!=null){
						card.move(player.discard,true);
					}
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Dirty Old River Town";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.actions+=1;
				player.Refresh();
				ArrayList<Player> P = player.theGame.players;
				for(int x=0;x<P.size();x++){
					if(P.get(x).moat==false)
						P.get(x).RevealCards(1);
				}
				player.gui.showPlayersRevealed(player,true);
				player.message="";
				for(int x=0;x<P.size();x++){
					if(P.get(x).revealedCards.size()>0){
						String msg=P.get(x).getName();
						if(P.get(x)==player.theGame.turnPlayer)
							msg="You";
						msg+=" revealed ";
						msg+=P.get(x).revealedCards.get(0).dominionCard.name+".";
						int choice=player.Prompt(msg,new String[]{"Discard it.","Leave it on top."});
						if(choice==0){
							//it is first moved to the hand so that it will be treated as discarding and effects will activate
							P.get(x).revealedCards.get(0).move(P.get(x).hand,true);
							P.get(x).revealedCards.get(0).move(P.get(x).discard,true);
						}
					}
					P.get(x).revealedCards.clear();
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Spy";
				cost=4;
				action=true;
				attack=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.coins+=2;
				player.revealedCards.clear();
				ArrayList<Player> P = player.getAllOtherPlayers();
				for(int x=0;x<P.size();x++){
					if(P.get(x).moat==false)
						P.get(x).RevealCards(1);
				}
				player.gui.showPlayersRevealed(player,true);
				for(int x=0;x<P.size();x++){
					if(P.get(x).revealedCards.size()>0){
						P.get(x).revealedCards.get(0).move(getTrash(player),true);
						int cost=P.get(x).revealedCards.get(0).getCost(player);
						Requirements req=new Requirements(1,1);
						req.mincost=cost; req.maxcost=cost;
						req.allowPotionCost=P.get(x).revealedCards.get(0).dominionCard.potionCost;
						player.message=P.get(x).getName()+" trashed "+P.get(x).revealedCards.get(0).dominionCard.name+". Select a card to be gained.";
						ArrayList<Card> sel=player.SelectFromSupply(req);
						if(sel.size()>0){
							sel.get(0).move(P.get(x).discard,true);
						}
					}
				}
				for(int x=0;x<P.size();x++){
					P.get(x).revealedCards.clear();
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Swindler";
				cost=3;
				action=true;
				attack=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.revealedCards.clear();
				//make players reveal cards
				ArrayList<Player> P = player.getAllOtherPlayers();
				for(int x=0;x<P.size();x++){
					if(P.get(x).moat==false)
						P.get(x).RevealCards(2);
				}
				player.gui.showPlayersRevealed(player,true);
				Requirements req1=new Requirements(1,2);//for trashing a card
				req1.allowAction=false; req1.allowCurse=false;
				req1.allowTreasure=true; req1.allowVictory=false;
				Requirements req2=new Requirements(2,2);
				req2.allowAction=false; req2.allowCurse=false;
				req2.allowTreasure=true; req2.allowVictory=false;
				Requirements req3=new Requirements(1,1);
				req3.allowAction=false; req3.allowCurse=false;
				req3.allowTreasure=true; req3.allowVictory=false;
				ArrayList<Card> trashed=new ArrayList<Card>();
				for(int x=0;x<P.size();x++){
					if(P.get(x).CheckRequirements(P.get(x).revealedCards,req1)){//has an action card revealed
						if(P.get(x).CheckRequirements(P.get(x).revealedCards,req2)){//had 2 action cards revealed
							//ask which to trash
							player.message="Select which card to trash from "+P.get(x).getName()+".";
							ArrayList<Card> sel=player.SelectFromList(P.get(x).revealedCards,req3);
							player.clearSelected();
							if(sel.size()>0){
								trashed.add(sel.get(0));
							}
						}else{//trash the one treasure card
							for(int a=0;a<P.get(x).revealedCards.size();a++){
								if(P.get(x).revealedCards.get(a).dominionCard.treasure){
									trashed.add(P.get(x).revealedCards.get(a));
								}
							}
						}
					}
					for(int a=0;a<P.get(x).revealedCards.size();a++){
						if(trashed.contains(P.get(x).revealedCards.get(a))){//trash the card
							P.get(x).revealedCards.get(a).move(getTrash(P.get(x)),true);
						}else{//discard the card
							P.get(x).revealedCards.get(a).move(P.get(x).discard,true);
						}
					}
				}
				player.Refresh();
				//gain one of the trashed cards
				player.message="Select which cards to gain.";
				ArrayList<Card> sel=player.SelectFromList(trashed,new Requirements(0,100));
				player.clearSelected();
				if(sel.size()>0){
					sel.get(0).move(player.discard,true);
				}
				for(int x=0;x<P.size();x++){
					P.get(x).revealedCards.clear();
				}
				player.message="";
				player.Refresh();
			}
			void create(){
				name="Thief";
				cost=4;
				action=true;
				attack=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				Player left=player.getLeft();
				left.RevealCards(2);
				player.gui.showPlayersRevealed(player,true);
				for(int x=0;x<left.revealedCards.size();x++){
					left.revealedCards.get(x).move(left.discard,true);
					if(x==0 || left.revealedCards.get(x).dominionCard!=left.revealedCards.get(0).dominionCard){
						//the second card can not be the same as the first card
						if(left.revealedCards.get(x).dominionCard.action){
							player.actions+=2;
						}
						if(left.revealedCards.get(x).dominionCard.treasure){
							player.coins+=2;
						}
						if(left.revealedCards.get(x).dominionCard.victory){
							player.DrawCard();
							player.DrawCard();
						}
					}
				}
				player.Refresh();
			}
			void create(){
				name="Tribute";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				ArrayList<Player> P=player.getAllOtherPlayers();
				for(int x=0;x<P.size();x++){
					if(P.get(x).moat==false) {
						Card card=player.theGame.findSupply(findCard("Curse")).topCard();
						if(card!=null){
							card.move(P.get(x).discard,true);
						}
					}
				}
				player.Refresh();
			}
			void create(){
				name="Witch";
				cost=5;
				action=true;
				attack=true;
			}
		});
		
				
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="Select a card to return to the supply.";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,1));
				if(sel.size()>0){
					DominionCard dom=sel.get(0).dominionCard;
					SupplyPile supply=player.theGame.findSupply(dom);
					int choice=1;
					boolean have2nd=false;
					sel.get(0).move(player.aside,true);//move aside so it can look for a second copy more easily
					for(int x=0;x<player.hand.cards.size();x++){
						if(player.hand.cards.get(x).dominionCard==dom)
							have2nd=true;
					}
					if(have2nd){
						player.message="";
						choice=player.Prompt("Return a second copy?",new String[]{"Yes","No"});
					}
					ArrayList<Player> P = player.getAllOtherPlayers();
					if(supply!=null){
						sel.get(0).move(supply.pile,true);
						if(choice==0){//the second copy
							for(int x=0;x<player.hand.cards.size();x++){
								if(player.hand.cards.get(x).dominionCard==dom){
									player.hand.cards.get(x).move(supply.pile,true);
									break;
								}
							}
						}
						for(int x=0;x<P.size();x++){
							if(P.get(x).moat==false) {
								Card card=supply.pile.checkTop();
								if(card!=null){
									card.move(P.get(x).discard,true);
								}
							}
						}
					}else{
						//probably got the card from the black market
						//just make new cards to give the other players
						sel.get(0).move(getTrash(player),true);
						if(choice==0){//the second copy
							for(int x=0;x<player.hand.cards.size();x++){
								if(player.hand.cards.get(x).dominionCard==dom){
									player.hand.cards.get(x).move(getTrash(player),true);
									break;
								}
							}
						}
						/*for(int x=0;x<P.size();x++){
							if(P.get(x).moat==false) {
								Card card=new Card(dom);
								card.move(P.get(x).discard,true);
							}
						}*/
					}
				}
				player.Refresh();
			}
			boolean canPlay(Player player, Card instance){
				return player.hand.cards.size()>1;
			}
			void create(){
				name="Ambassador";
				cost=3;
				action=true;
				attack=true;
			}
		});
		
		
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.coins+=2;
				player.DrawCard();
				player.DrawCard();
				player.DrawCard();
				ArrayList<Player> P=player.getAllOtherPlayers();
				for(int a=0;a<P.size();a++) {
					if(P.get(a).moat==false) P.get(a).banquet=true;
				}
				player.Refresh();
			}
			void endTurn(Player player, Card instance){
				player.banquet=true;
			}
			void create(){
				name="Banquet";
				cost=5;
				action=true;
				attack=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.coins+=1;
				player.DrawCard();
				player.actions+=1;
				instance.counters=1;
				player.Refresh();
			}
			void afterBuy(Player player,Card instance,Card bought){
				if(bought.dominionCard.victory){
					instance.counters=0;
				}
			}
			void beforeCleanup(Player player,Card instance){
				if(instance.counters==1){
					int choice=player.Prompt("Place Treasury on top of your deck?",new String[]{"Yes","No"});
					if(choice==0){
						instance.move(player.deck,true);
					}
				}
			}
			void create(){
				name="Treasury";
				cost=5;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				Card card=player.theGame.findSupply(findCard("Silver")).topCard();
				if(card!=null)
					card.move(player.deck,true);
				//each other player reveals a victory card from their hand and places it on top of their deck
				ArrayList<Player> others=player.getAllOtherPlayers();
				for(int x=0;x<others.size();x++){
					if(others.get(x).moat==false) {
						Requirements req=new Requirements(1,100);
						req.allowVictory=true; req.allowTreasure=false;
						req.allowCurse=false; req.allowAction=false;
						boolean hasVictory=others.get(x).CheckHand(req);
						req.max=1;
						/*if(hasVictory){
							others.get(x).gui.message=others.get(x).getName()+":  Add victory card to the top of your deck.";
							others.get(x).revealedCards=others.get(x).SelectFromHand(req);
							if(others.get(x).revealedCards.size()>0){
								others.get(x).revealedCards.get(0).move(others.get(x).deck,true);
							}
						}else{
							others.get(x).gui.message=others.get(x).getName()+":  You have no victory cards in your hand so you are unaffected.";
							req.min=0; req.max=0;//replace with reveal hand when I can have players on two different screens
							others.get(x).revealedCards=others.get(x).SelectFromHand(req);
						}*/
						if(hasVictory){
							player.message=others.get(x).getName()+":  Add victory card to the top of your deck.";
							others.get(x).revealedCards=player.SelectFromList(others.get(x).hand.cards,req);
							if(others.get(x).revealedCards.size()>0){
								others.get(x).revealedCards.get(0).move(others.get(x).deck,true);
							}
						}else{
							player.message=others.get(x).getName()+":  You have no victory cards in your hand so you are unaffected.";
							req.min=0; req.max=0;
							others.get(x).revealedCards=player.SelectFromList(others.get(x).hand.cards,req);
						}
					}
				}
				player.gui.showPlayersRevealed(player,true);
				player.Refresh();
			}
			void create(){
				name="Bureaucrat";
				//Will need to rewrite for multiple screens
				cost=4;
				action=true;
				attack=true;
			}
		});
		
		/*AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				player.Refresh();
			}
			
			void create(){
				name="Moat";
				cost=2;
				action=true;
				reaction=true;
			}
		});*/
		
		
		
		//First attempt at a reaction card
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				//discard any number of cards, +1 card per card discarded
				player.message="Discard one or more cards.";
				player.clearSelected();
				ArrayList<Card> set=player.SelectFromHand(new Requirements(1,100));
				System.out.println("Secret Chamber "+set.size()+"  "+set);
				
				for(int x=0;x<set.size();x++){
					set.get(x).move(player.discard,true);
					player.coins+=1;
				}
				player.message="";
				player.Refresh();
			}
			boolean canPlay(Player player, Card instance) {
				return player.hand.cards.size()>1;//atleast 1 card to discard
			}
			void react(Player player, Card instance, Player user){
				super.react(player,instance,user);
				player.DrawCard();
				player.DrawCard();
				player.Refresh();
				player.message="Return 2 cards to the top of the deck.";
				ArrayList<Card> set=player.SelectFromHand(new Requirements(2,2));
				for(int x=0;x<set.size();x++){
					set.get(x).move(player.deck,true);
				}
				player.Refresh();
			}
			void create(){
				name="Secret Chamber";
				cost=2;
				action=true;
				reaction=true;
				allowOffline=false;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=2;
				player.Refresh();
			}
			void beforeBuyPhase(Player player, Card instance) {
				player.coins+=player.actions;
			}
			void react(Player player, Card instance, Player user){
				super.react(player,instance,user);
				player.message="Select 2 cards from your discard pile to place on top of your deck.";
				ArrayList<Card> sel=player.SelectFromList(player.discard.cards,new Requirements(2,2));
				for(int a=0;a<sel.size();a++) {
					sel.get(a).move(player.deck,false);//I think it was moving it to the bottom so I changed it to false
				}
				player.message="";
				player.Refresh();
			}
			boolean canReact(Player player,Card instance) {
				return player.discard.cards.size()>=2;
			}
			void create(){
				name="Imperial Guards";
				cost=4;
				action=true;
				reaction=true;
				allowOffline=false;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				player.Refresh();
			}
			void react(Player player, Card instance, Player user){
				super.react(player,instance,user);
				player.moat=true;
				player.Refresh();
			}
			void create(){
				name="Moat";
				cost=2;
				action=true;
				reaction=true;
				allowOffline=false;
			}
		});
		
		AllCards.add(new DominionCard() {
			void play(Player player, Card instance) {
				super.play(player,instance);
				player.coins+=2;
				ArrayList<Player> P=player.getAllOtherPlayers();
				player.message="Waiting for other players to discard.";
				player.Refresh();
				for(int a=0;a<P.size();a++) {
					if(P.get(a).moat==false && P.get(a).hand.cards.size()>3) {
						P.get(a).message="Discard down to 3 cards.";
						ArrayList<Card> sel=P.get(a).SelectFromHand(new Requirements(P.get(a).hand.cards.size()-3,P.get(a).hand.cards.size()-3));
						for(int b=0;b<sel.size();b++) {
							sel.get(b).move(P.get(a).discard,true);
						}
						P.get(a).clearSelected();
						P.get(a).message="";
					}
				}
				player.message="";
				player.Refresh();
			}
			void create() {
				name="Militia";
				cost=4;
				action=true;
				attack=true;
				allowOffline=false;
			}
		});
		
		
		//a card with potion in its cost
		AllCards.add(new DominionCard() {
			void create() {
				name="Vineyard";
				cost=0;
				potionCost=true;
				victory=true;
				noBM=true;
			}
			int getVP(Player player,Card instance) {
				ArrayList<Card> all=player.getAllCards();
				int count=0;
				for(int a=0;a<all.size();a++) {
					if(all.get(a).dominionCard.action)
						count++;
				}
				return count/3;
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Alchemist";
				cost=3;
				potionCost=true;
				action=true;
				noBM=true;
			}
			void play(Player player, Card instance) {
				super.play(player,instance);
				player.actions+=1;
				player.DrawCard();
				player.DrawCard();
				player.Refresh();
			}
			void beforeCleanup(Player player,Card instance) {// PROBLEM  This triggers when you buy it!
				if(player.hand.contains("Potion")) {
					int choice=player.Prompt("Place Alchemist on top of your deck?",new String[]{"Yes","No"});
					if(choice==0){
						instance.move(player.deck,true);
					}
					player.message="";				
				}
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Apothecary";
				cost=2;
				potionCost=true;
				action=true;
				noBM=true;
			}
			void play(Player player, Card instance) {
				super.play(player,instance);
				player.actions+=1;
				player.DrawCard();
				player.Refresh();
				player.RevealCards(4);
				ArrayList<Card> set=new ArrayList<Card>();//to return to deck
				for(Card card:player.revealedCards) {
					if(card.dominionCard.name.equals("Copper")||card.dominionCard.name.equals("Potion")) {
						card.move(player.hand,false);
					}else {
						set.add(card);
					}
				}
				player.Refresh();
				if(set.size()>0) {
					if(set.size()>1) {
						player.message="Select order to return cards.";
						set=player.OrderCards(set);
						player.clearSelected();
						player.message="";
					}
					while(set.size()>0){
						set.get(set.size()-1).move(player.deck,true);
						set.remove(set.size()-1);
					}
				}
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Apprentice";
				action=true;
				cost=5;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.actions+=1;
				player.message="Select a card to trash";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,1));
				for(Card card:sel){
					card.move(getTrash(player),true);
					player.coins+=card.dominionCard.getCost(player,card);
					if(card.dominionCard.potionCost) player.coins++;
				}
				player.message="";
				player.Refresh();
			}
			boolean canPlay(Player player,Card instance) {
				return player.hand.cards.size()>1;
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="City";
				cost=5;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.actions+=2;
				player.DrawCard();
				int empty=player.theGame.countEmptyPiles();
				if(empty>=1) player.DrawCard();
				if(empty>=2) {player.coins+=1; player.buys+=1;}
				player.Refresh();
			}
		});
		
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.actions+=1;
				ArrayList<Player> P=player.getAllOtherPlayers();
				for(int x=0;x<P.size();x++){
					if(P.get(x).moat==false) {
						Card card=player.theGame.findSupply(findCard("Curse")).topCard();
						if(card!=null){
							card.move(P.get(x).discard,true);
						}
					}
				}
				player.Refresh();
			}
			void create(){
				name="Familiar";
				cost=3;
				potionCost=true;
				noBM=true;
				action=true;
				attack=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.message="Trash any number of cards.";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,100));
				int total=0;
				for(Card card:sel) {
					total+=card.dominionCard.getCost(player,card);
					card.move(getTrash(player),true);
				}
				player.clearSelected();
				player.message="Select a card costing "+total+" to gain.";
				Requirements req=new Requirements(0,1);
				req.maxcost=total;
				req.mincost=total;
				req.allowPotionCost=false;
				sel=player.SelectFromSupply(req);
				for(Card card:sel) {
					card.move(player.discard,true);
				}
				player.message="";
				player.clearSelected();
				player.Refresh();
			}
			void create(){
				name="Forge";
				cost=7;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard() {
			void play(Player player, Card instance) {
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				ArrayList<Player> P=player.getAllOtherPlayers();
				player.message="Waiting for other players to return cards.";
				player.Refresh();
				for(int a=0;a<P.size();a++) {
					if(P.get(a).moat==false && P.get(a).hand.cards.size()>3) {
						P.get(a).message="Return "+(P.get(a).hand.cards.size()-3)+" cards to the top of your deck.";
						ArrayList<Card> sel=P.get(a).SelectFromHand(new Requirements(P.get(a).hand.cards.size()-3,P.get(a).hand.cards.size()-3));
						for(int b=0;b<sel.size();b++) {
							sel.get(b).move(P.get(a).deck,true);
						}
						P.get(a).clearSelected();
						P.get(a).message="";
					}
				}
				player.message="";
				player.Refresh();
			}
			void create() {
				name="Ghost Ship";
				cost=5;
				action=true;
				attack=true;
				allowOffline=false;
			}
		});
		
		AllCards.add(new DominionCard() {
			void play(Player player, Card instance) {
				super.play(player,instance);
				player.coins+=2;
				player.buys+=1;
				ArrayList<Player> P=player.getAllOtherPlayers();
				player.message="Waiting for other players to discard.";
				player.Refresh();
				for(int a=0;a<P.size();a++) {
					if(P.get(a).moat==false && P.get(a).hand.cards.size()>3) {
						P.get(a).message="Discard down to 3 cards.";
						ArrayList<Card> sel=P.get(a).SelectFromHand(new Requirements(P.get(a).hand.cards.size()-3,P.get(a).hand.cards.size()-3));
						for(int b=0;b<sel.size();b++) {
							sel.get(b).move(P.get(a).discard,true);
						}
						P.get(a).clearSelected();
						P.get(a).message="";
					}
				}
				player.message="";
				player.Refresh();
			}
			void afterBuy(Player player, Card instance, Card bought){
				player.victoryPoints+=1;
			}
			void create() {
				name="Goons";
				cost=6;
				action=true;
				attack=true;
				allowOffline=false;
			}
		});
		
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				Requirements req=new Requirements(2,2);
				req.allowVictory=false;
				req.allowCurse=false;
				req.allowAction=true;
				req.allowTreasure=false;
				req.except.add(this);//except golem
				player.RevealUntilFound(req);
				if(player.revealedCards.size()>0) {
					player.message="Select order to activate revealed cards.";
					ArrayList<Card> toPlay=player.OrderCards(player.revealedCards);
					player.clearSelected();
					for(Card card:toPlay)
						card.move(player.aside,true);
					player.message="";
					player.actions+=toPlay.size();
					for(Card card:toPlay) {
						if(card.dominionCard.canPlay(player,card)) {
							card.dominionCard.play(player,card);
						}else {
							//notify of failure
							card.move(player.discard,true);
							player.actions-=1;//remove temporary action
						}
					}
				}else {
					//notify of failure
				}
				player.Refresh();
			}
			void create(){
				name="Golem";
				cost=4;
				potionCost=true;
				noBM=true;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void create() {
				name="Governor";
				cost=5;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.actions+=1;
				int choice=player.Prompt("Select effect",new String[]{"You: +3 Cards; Others: +1 Card","You: Gain a Gold; Others: Gain a Silver","All players may trash 1 card to gain a card costing (you 2; others 1) more"});
				ArrayList<Player> P=player.getAllOtherPlayers();
				if(choice==0) {
					player.DrawCard();
					player.DrawCard();
					player.DrawCard();
					for(Player p:P)
						p.DrawCard();
				}
				if(choice==1) {
					Card card=player.theGame.findSupply(findCard("Gold")).topCard();
					if(card!=null) card.move(player.discard,true);
					for(Player p:P) {
						card=player.theGame.findSupply(findCard("Silver")).topCard();
						if(card!=null) card.move(p.discard,true);
					}
				}
				if(choice==2) {
					if(player.hand.cards.size()>0) {
						player.message="Select a card to trash, if any. (To gain a card costing 2 more)";
						ArrayList<Card> sel=player.SelectFromHand(new Requirements(0,1));
						player.clearSelected();
						if(sel.size()>0) {
							player.message="Select a card to gain.";
							sel.get(0).move(getTrash(player),true);
							Requirements req=new Requirements(0,1);
							req.maxcost=sel.get(0).dominionCard.getCost(player,sel.get(0))+2;
							req.mincost=req.maxcost;
							req.allowPotionCost=sel.get(0).dominionCard.potionCost;
							sel=player.SelectFromSupply(req);
							if(sel.size()>0)
								sel.get(0).move(player.discard,true);
						}
					}else {
						//notify of failure
					}
					player.message="Waiting for other players' choices.";
					for(Player p:P) {
						p.message="Select a card to trash, if any. (To gain a card costing 1 more)";
						ArrayList<Card> sel=p.SelectFromHand(new Requirements(0,1));
						p.clearSelected();
						if(sel.size()>0) {
							p.message="Select a card to gain.";
							sel.get(0).move(getTrash(player),true);
							Requirements req=new Requirements(0,1);
							req.maxcost=sel.get(0).dominionCard.getCost(p,sel.get(0))+1;
							req.mincost=req.maxcost;
							req.allowPotionCost=sel.get(0).dominionCard.potionCost;
							sel=p.SelectFromSupply(req);
							if(sel.size()>0)
								sel.get(0).move(p.discard,true);
						}
						p.clearSelected();
						p.message="";
					}
				}
				player.message="";
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.DrawCard();
				player.actions+=1;
				player.message="Select a card to set aside.";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,1));
				for(int x=0;x<sel.size();x++){
					sel.get(x).move(instance.overlay,true);
				}
				player.Refresh();
			}
			void durationStartTurn(Player player, Card instance){
				while(instance.overlay.cards.size()>0){
					instance.overlay.cards.get(0).move(player.hand,false);
				}
				instance.move(player.discard,true);
				player.Refresh();
			}
			void create(){
				name="Haven";
				cost=2;
				duration=true;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.buys+=1;
				player.coins+=3;
				player.Refresh();
				player.message="Discard 2 cards.";
				ArrayList<Card> set=player.SelectFromHand(new Requirements(2,2));
				for(int x=0;x<set.size();x++){
					set.get(x).move(player.discard,true);
				}
				player.message="";
				player.Refresh();
			}
			void durationStartTurn(Player player, Card instance){
				player.DrawCard();
				instance.move(player.hand,false);
				player.Refresh();
			}
			void react(Player player,Card instance,Player attacker) {
				super.react(player,instance,attacker);
				instance.move(player.durations,true);
				player.Refresh();
			}
			boolean canPlay(Player player, Card instance){
				return player.hand.cards.size()>2;
			}
			void create(){
				name="Horse Traders";
				cost=4;
				action=true;
				reaction=true;
				//although I am using it like a duration, it is not a duration because the duration effect is on the reaction
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Ironworks";
				cost=4;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				//Gain a card costing up to 4
				//if it is a: Action: +1 Action  Treasure: +1 Coin  Victory: +1 Card
				Requirements req=new Requirements(1,1);
				req.maxcost=4;
				req.allowPotionCost=false;
				player.message="Select a card to gain.";
				ArrayList<Card> sel=player.SelectFromSupply(req);
				player.clearSelected();
				if(sel.size()>0) {
					sel.get(0).move(player.discard,true);
					if(sel.get(0).dominionCard.action) player.actions+=1;
					if(sel.get(0).dominionCard.treasure) player.coins+=1;
					if(sel.get(0).dominionCard.victory) player.DrawCard();
				}
				player.message="";
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Island";
				action=true;
				victory=true;
				cost=4;
				vp=2;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.message="Select a card to set aside.";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,1));
				player.clearSelected();
				if(sel.size()>0)
					sel.get(0).move(player.island,true);
				player.Refresh();
			}
			void onCleanup(Player player,Card instance) {
				instance.move(player.island,true);//moving done here so it will be in played cards
			}
			boolean canPlay(Player player,Card instance) {
				return player.hand.cards.size()>1;
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Lighthouse";
				cost=2;
				action=true;
				duration=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.actions+=1;
				player.coins+=1;
				player.Refresh();
			}
			void durationStartTurn(Player player,Card instance) {
				player.coins+=1;
				instance.move(player.discard,true);
			}
			void durationEndTurn(Player player,Card instance) {
				player.moat=true;
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Lookout";
				cost=3;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.actions+=1;
				player.RevealCards(3);
				if(player.revealedCards.size()==3) {
					player.message="Select a card to trash.";
					ArrayList<Card> sel=player.SelectFromList(player.revealedCards,new Requirements(1,1));
					player.clearSelected();
					if(sel.size()>0) {
						sel.get(0).move(getTrash(player),true);
						player.revealedCards.remove(sel.get(0));
					}
					player.message="Select a card to discard. (The other will go on top of the deck)";
					sel=player.SelectFromList(player.revealedCards,new Requirements(1,1));
					player.clearSelected();
					if(sel.size()>0) {
						sel.get(0).move(player.hand,true);//move to hand first to count as discarding 
						sel.get(0).move(player.discard,true);
						player.revealedCards.remove(sel.get(0));
					}
					player.revealedCards.get(0).move(player.deck,true);//it might already be there, I don't know if this is needed
				}else {
					//Notify of Failure (not enough cards left in deck + discard)
				}
				player.message="";
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Mandarin";
				cost=5;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.coins+=3;
				player.message="Return 1 card to the top of the deck.";
				player.clearSelected();
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,1));
				if(sel.size()>0) {
					sel.get(0).move(player.deck,true);
				}
				player.message="";
				player.Refresh();
			}
			boolean canPlay(Player player,Card instance) {
				return player.hand.cards.size()>1;
			}
			void whenGained(Player player,Card instance) {
				//Put all treasure cards you have in play on top of your deck.
				//Because this program doesn't put treasure cards into play this will return all in hand
				//Based on wiki rulings:
				//Return all treasure cards in your hand onto your deck,
				//then if it is not the buy phase gain coins equal to the value (do this first)
				
				//Note, buying Mandarin with Black Market can cause negative coins
				
				//Need to Inform player of cards activation!
				
				ArrayList<Card> toSend=new ArrayList<Card>();
				for(Card c:player.hand.cards){
					if(c.dominionCard.treasure) {
						toSend.add(c);
						if(player.phase!=Player.PHASE_BUY)
							player.coins+=c.dominionCard.getValue(player,c);
					}
				}
				if(toSend.size()>1) {
					player.message="Select order to return cards.";
					toSend=player.OrderCards(toSend);
					player.clearSelected();
				}
				while(toSend.size()>0){
					toSend.get(toSend.size()-1).move(player.deck,true);
					toSend.remove(toSend.size()-1);
				}
				player.message="";
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Masquerade";
				cost=3;
				allowOffline=false;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				player.Refresh();
				//All players set aside 1 card from their hand
				player.message="Select a card to pass to the player to your left.";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,1));
				player.clearSelected();
				if(sel.size()>0) {
					sel.get(0).move(player.aside,true);
				}
				player.message="Awaiting other players' responses.";
				ArrayList<Player> P=player.getAllOtherPlayers();
				for(Player p:P) {
					p.message="Select a card to pass to the player to your left. (Effect of Masquerade)";
					sel=p.SelectFromHand(new Requirements(1,1));
					p.message="";
					p.clearSelected();
					if(sel.size()>0) {
						sel.get(0).move(p.aside,true);
					}
				}
				for(Player p:player.theGame.players) {
					if(p.aside.cards.size()>0) {
						p.aside.cards.get(0).move(p.getLeft().hand,false);
					}
				}
				player.message="";
				for(Player p:player.theGame.players)
					p.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Menagerie";
				cost=3;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.actions+=1;
				//TODO Reveal Hand
				if(player.GetDifferentCards(player.hand.cards,null).size()==player.hand.cards.size()) {
					//if all cards in your hand are different
					player.DrawCard();
					player.DrawCard();
				}
				player.DrawCard();
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=2;
				player.DrawCard();
				player.Refresh();
				int choice=player.Prompt("Trash Mining Village for +2 coins?",new String[] {"Yes","No"});
				if(choice==0) {
					instance.move(getTrash(player),true);
					player.coins+=2;
				}
				player.Refresh();
			}
			void create(){
				name="Mining Village";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Minion";
				cost=5;
				attack=true;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				int choice=player.Prompt("Choose effect:",new String[] {"+2 Coins","All players discard hand and draw 4 cards."});
				player.message="";
				player.actions+=1;
				if(choice==0) {
					player.coins+=2;
				}
				if(choice==1) {
					for(Player p:player.theGame.players) {
						if(p.moat==false || p==player) {//can't moat to protect yourself
							if(p==player||p.hand.cards.size()>=5) {//size requirement doesn't apply to you
								ArrayList<Card> toDiscard=new ArrayList<Card>();//So that you won't discard cards drawn on When discarded effects
								for(Card c:p.hand.cards)
									toDiscard.add(c);
								for(Card c:toDiscard)
									c.move(p.discard,true);
								while(p.hand.cards.size()<4)
									p.DrawCard();
								p.Refresh();
							}
						}else {
							//Notify of protection?
						}
					}
				}
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Mountebank";
				cost=5;
				action=true;
				attack=true;
				
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.coins+=2;
				ArrayList<Player> P = player.getAllOtherPlayers();
				player.message="Awaiting other players' responses";
				for(Player p:P) {
					//need to include cards treated as curse as well
					if(p.moat==false) {
						boolean hasCurse=false;
						for(Card c:p.hand.cards) {
							if(c.dominionCard.curse) 
								hasCurse=true;
						}
						ArrayList<Card> sel=new ArrayList<Card>();
						if(hasCurse) {
							//choice as to which if any to discard
							Requirements req=new Requirements(0,1);
							req.allowTreasure=false;
							req.allowVictory=false;
							req.allowAction=false;
							req.allowCurse=true;
							p.message="Discard a curse avoid gaining a curse and a copper?";
							sel=p.SelectFromHand(req);
							p.clearSelected();
						}
						if(sel.size()>0) {
							//discard the selected one
							sel.get(0).move(p.discard,true);
						}else {
							//gain a curse and a copper
							Card card=p.theGame.findSupply(findCard("Copper")).topCard();
							if(card!=null)
								card.move(p.discard,true);
							card=p.theGame.findSupply(findCard("Curse")).topCard();
							if(card!=null)
								card.move(p.discard,true);
						}
						p.Refresh();
					}
				}
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Native Village";
				cost=2;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.actions+=2;
				int choice=0;
				if(player.nativeVillage.cards.size()>0) {
					choice=player.Prompt("Choose effect:",new String[]{"Place the top card of your deck in your Native Village.","Return all cards in the Native Village to your hand."});
					player.message="";
				}
				if(choice==0) {
					player.RevealCards(1);
					if(player.revealedCards.size()>0) {
						player.revealedCards.get(0).move(player.nativeVillage,false);
					}
					//TODO Need to add informing player what card was sent
					
				}else {
					while(player.nativeVillage.cards.size()>0)
						player.nativeVillage.cards.get(0).move(player.hand,false);
				}
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Peddler";
				cost=8;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.actions+=1;
				player.coins+=1;
				player.DrawCard();
				player.Refresh();
			}
			int getCost(Player player,Card instance) {
				int out=super.getCost(player,instance);
				if(player.phase==Player.PHASE_BUY) {
					for(Card c:player.playedCards.cards) {//may need to modify depending on rulings (If a played card trashed itself is it still in play?)
						if(c.dominionCard.action) out-=2;
					}
				}
				if(out<0) out=0;
				return out;
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Philosopher's Stone";
				treasure=true;
				cost=3;
				potionCost=true;
				value=0;
			}
			int getValue(Player player,Card instance) {
				return (player.deck.cards.size()+player.discard.cards.size()) / 5;
			}
		});
		
		
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Rogue";
				cost=5;
				action=true;
				attack=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.coins+=2;
				ArrayList<Card> options=new ArrayList<Card>();
				for(Card c:getTrash(player).cards){
					if(c.dominionCard.cost>=3 && c.dominionCard.cost<=6) {
						options.add(c);
					}
				}
				if(options.size()>0) {
					//gain a card
					player.message="Select a card to gain.";
					ArrayList<Card> sel=player.SelectFromList(options,new Requirements(1,1));
					player.clearSelected();
					if(sel.size()>0) {
						sel.get(0).move(player.discard,true);
					}
				}else {
					//attack, 
					player.message="No valid cards in trash, waiting for other players to select cards to trash.";
					ArrayList<Player> P=player.getAllOtherPlayers();
					Requirements req=new Requirements(1,1);
					req.mincost=3; req.maxcost=6;
					Requirements req2=new Requirements(1,2);//for checking if a choice needs to be made
					req2.mincost=3; req2.maxcost=6;
					for(Player p:P) {
						if(p.moat==false) {
							p.RevealCards(2);
							if(p.CheckRequirements(p.revealedCards,req2)) {
								p.message="Select a card to trash costing 3-6. (Effect of Rogue)";
								ArrayList<Card> sel=p.SelectFromList(p.revealedCards,req);
								p.clearSelected();
								if(sel.size()>0) {
									sel.get(0).move(getTrash(p),true);
									p.revealedCards.remove(sel.get(0));
								}
							}else {
								
							}
							for(Card c:p.revealedCards) {//discard the non trashed cards
								c.move(p.discard,true);
							}
						}
						p.message="";
						p.Refresh();
					}
				}
				player.message="";
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Scrying Pool";
				cost=2;
				potionCost=true;
				action=true;
				attack=true;
				noBM=true;
			}
			void play(Player player, Card instance) {
				super.play(player,instance);
				player.actions+=1;
				for(Player p:player.theGame.players) {
					p.revealedCards.clear();
					if(p.moat==false && p!=player) {
						p.RevealCards(1);
					}
				}
				player.gui.showPlayersRevealed(player,true);
				for(Player p:player.theGame.players) {
					if(p.revealedCards.size()>0) {
						int choice=player.Prompt((p==player?p.getName():"You")+" revealed a "+p.revealedCards.get(0).dominionCard.name+".",new String[] {"Discard it.","Return it."});
						if(choice==0)
							p.revealedCards.get(0).move(p.discard,true);
						else
							p.revealedCards.get(0).move(p.deck,true);
					}
					player.Refresh();
				}
				Requirements req=new Requirements(1,1);
				player.revealedCards.clear();
				req.allowAction=false;
				player.RevealUntil(req);
				for(Card c:player.revealedCards) {
					c.move(player.hand,false);
				}
				player.revealedCards.clear();
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Transmute";
				cost=0;
				potionCost=true;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.message="Trash a card.";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(1,1));
				player.clearSelected();
				if(sel.size()>0) {
					sel.get(0).move(getTrash(player),true);
					if(sel.get(0).dominionCard.action) {//gain a duchy
						Card c=player.theGame.findSupply(findCard("Duchy")).topCard();
						if(c!=null) c.move(player.discard,true);
					}
					if(sel.get(0).dominionCard.victory) {//gain a gold
						Card c=player.theGame.findSupply(findCard("Gold")).topCard();
						if(c!=null) c.move(player.discard,true);
					}
					if(sel.get(0).dominionCard.treasure) {//gain a transmute
						Card c=player.theGame.findSupply(findCard("Transmute")).topCard();
						if(c!=null) c.move(player.discard,true);
					}
				}
				player.message="";
				player.Refresh();
			}
			boolean canPlay(Player player,Card instance) {
				return player.hand.cards.size()>1;
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="University";
				cost=2;
				potionCost=true;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.actions+=2;
				Requirements req=new Requirements(0,1);
				player.message="Gain a card costing up to 5";
				req.allowPotionCost=false;
				req.maxcost=5;
				ArrayList<Card> sel=player.SelectFromSupply(req);
				player.clearSelected();
				if(sel.size()>0) {
					sel.get(0).move(player.discard,true);
				}
				player.message="";
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Vault";
				cost=5;
				action=true;
				allowOffline=false;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				player.Refresh();
				player.message="Discard any number of cards.  (+1 coin per discarded)";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(0,100));
				player.clearSelected();
				for(Card c:sel) {
					c.move(player.discard,true);
					player.coins+=1;
				}
				ArrayList<Player> P=player.getAllOtherPlayers();
				player.message="Waiting for other players.";
				player.Refresh();
				for(Player p:P) {
					int choice=p.Prompt("Discard 2 cards to draw 1?",new String[] {"Yes","No"});
					if(choice==0) {
						sel=p.SelectFromHand(new Requirements(2,2));
						p.clearSelected();
						if(sel.size()==2) {
							sel.get(0).move(p.discard,true);
							sel.get(1).move(p.discard,true);
							p.DrawCard();
							p.Refresh();
						}
					}
				}
				player.message="";
			}
		});
		
		AllCards.add(new DominionCard(){
			void play(Player player, Card instance){
				super.play(player,instance);
				player.actions+=2;
				player.buys+=1;
				player.DrawCard();
				player.Refresh();
			}
			void create(){
				name="Worker's Village";
				cost=4;
				action=true;
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Zookeeper";
				cost=3;
				action=true;
			}
			boolean canPlay(Player player,Card instance) {
				return player.hand.cards.size()>1;
			}
			void play(Player player, Card instance) {
				super.play(player,instance);
				player.message="Select a card to return.  If it is an action card it will be played.";
				ArrayList<Card> sel = player.SelectFromHand(new Requirements(1,1));
				player.clearSelected();
				if(sel.size()>0) {
					if(sel.get(0).dominionCard.action) {
						if(sel.get(0).dominionCard.canPlay(player,sel.get(0))) {
							player.Refresh();
							sel.get(0).dominionCard.play(player,sel.get(0));
						}
					}
					player.coins+=sel.get(0).dominionCard.getCost(player,instance);
					sel.get(0).move(player.theGame.findSupply(sel.get(0).dominionCard).pile,true);
				}
				player.message="";
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Air Temple";
				action=true;
				cost=5;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.coins+=2;
				player.RevealCards(10);
				for(Card c:player.revealedCards) {
					c.send(player.discard,true);
				}
				//confirm revealed cards here
				
				player.Refresh();
				if(player.discard.cards.size()>=3) {
					player.message="Select 3 cards to shuffle into the deck.";
					ArrayList<Card> sel=player.SelectFromList(player.discard.cards,new Requirements(3,3));
					player.clearSelected();
					for(Card c:sel) {
						c.move(player.deck,false);
					}
					player.deck.shuffle();
				}else {
					//notify of failure
				}
				player.message="";
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Cabbage Merchant";
				cost=2;
				action=true;
				reaction=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				player.actions+=1;
				for(Card c:player.hand.cards) {
					if(c.dominionCard.name.equals("Estate") || c.dominionCard.name.equals("Duchy") || c.dominionCard.name.equals("Province")) {
						player.coins+=1;
					}
				}
				player.Refresh();
			}
			void react(Player player, Card instance,Player user) {
				super.react(player,instance,user);
				player.DrawCard();
				player.DrawCard();
				player.Refresh();
				player.message="Return 2 cards to the top of the deck.";
				ArrayList<Card> sel=player.SelectFromHand(new Requirements(2,2));
				for(Card c:sel)
					c.move(player.deck,true);
				player.clearSelected();
				player.message="";
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Bayou";
				cost=3;
				action=true;
			}
			void play(Player player, Card instance) {
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				player.DrawCard();
				player.DrawCard();
				for(int a=0;a<player.hand.cards.size();a++) {
					if(player.hand.cards.get(a).dominionCard.getCost(player,player.hand.cards.get(a))>=3) {
						player.hand.cards.get(a).move(player.discard,true);
						a-=1;
					}						
				}
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="War Counsel";
				cost=6;
				action=true;
			}
			void play(Player player,Card instance) {
				super.play(player,instance);
				//You can discard any number of non action cards
				player.message="Discard any number on non-action cards. (optional)";
				Requirements req=new Requirements(0,100);
				req.allowAction=false;//may need to change reqirements to make it not allow Action-Victory cards
				if(player.hand.cards.size()>0) {
					ArrayList<Card> sel=player.SelectFromHand(req);
					while(sel.size()>0) {
						sel.get(0).move(player.discard,true);
						sel.remove(0);
					}
				}
				//draw until you have 4 cards in hand
				player.message="";
				while(player.hand.cards.size()<4 && player.deck.cards.size()+player.discard.cards.size() > 0) {
					player.revealedCards.clear();
					player.RevealCards(1);
					int choice=0;
					Card c=player.revealedCards.get(0);//You may discard any non action cards drawn this way
					if(c.dominionCard.action==false) {
						choice = player.Prompt("Keep "+c.dominionCard.name+"?",new String[] {"Yes","No"});
					}
					if(choice==0) {
						c.move(player.hand,false);
					}else {
						c.move(player.aside,false);
					}
					player.Refresh();
				}
				while(player.aside.cards.size()>0) {//send set aside cards to discard
					player.aside.cards.get(0).move(player.discard,true);
					//could add a manual discard trigger here due to the cards wording
				}
				player.message="";
				player.clearSelected();
				player.Refresh();
				if(player.playedCards.cards.size()<=1) {
					//if you have played no other action cards this turn
					//May need a sorted selection here
					Requirements req2=new Requirements(0,100);
					req2.allowTreasure=false; req2.allowVictory=false; req2.allowCurse=false;
					player.message="Select action cards from hand in the order you wish to play them.";
					ArrayList<Card> toPlay=player.SelectFromHand(req2);
					//discard the unselected action cards
					
					//TODO SECTION REMOVED FOR BUG TESTING   effect will differ from text
					/*for(int a=0;a<player.hand.cards.size();a++) {
						if(player.hand.cards.get(a).dominionCard.action && toPlay.contains(player.hand.cards.get(a))==false) {
							player.hand.cards.get(a).move(player.discard,true);
							a-=1;
						}
					}
					player.Refresh();//actually needed here for drawplan
					*/
					
					while(toPlay.size()>0) {//play the rest in any order
						player.clearSelected();
						player.message="";
						player.Refresh();
						if(player.hand.cards.contains(toPlay.get(0)) && toPlay.get(0).dominionCard.canPlay(player,toPlay.get(0))) {
							toPlay.get(0).dominionCard.play(player,toPlay.get(0));
						}else {
							//TODO inform of failure
							toPlay.get(0).move(player.discard,true);
						}
						toPlay.remove(0);
					}
					player.message="";
					player.Refresh();
				}
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Poetry House";
				cost=4;
				action=true;
			}
			void play(Player player, Card instance) {
				super.play(player,instance);
				//check other player's hands for a Poetry House
				boolean found=false;
				for(Player p:player.theGame.players) {
					p.revealedCards.clear();
					if(p!=player) {
						for(Card c:p.hand.cards) {
							if(c.dominionCard==this) {
								p.revealedCards.add(c);
								found=true;
							}
						}
					}
				}
				if(found) {
					player.message="Another player has revealed a Poetry House.";
					player.gui.showPlayersRevealed(player,true);
					player.DrawCard();
				}else {
					player.coins+=1;
					player.DrawCard();
					player.DrawCard();
					player.DrawCard();
				}
				player.Refresh();
				for(Player p:player.theGame.players) {
					p.revealedCards.clear();
				}
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Drop Off";
				cost=4;
				action=true;
			}
			void play(Player player, Card instance) {
				super.play(player,instance);
				player.actions+=1;
				for(int a=0;a<5;a++) {
					player.DrawCard();
				}
				//check hand for duplicates
				ArrayList<Card> found=new ArrayList<Card>();
				for(int a=0;a<player.hand.cards.size();a++) {
					Card card1=player.hand.cards.get(a);
					if(found.contains(card1)==false) {
						boolean dup=false;
						for(int b=a+1;b<player.hand.cards.size();b++) {
							Card card2=player.hand.cards.get(b);
							if(card1.dominionCard==card2.dominionCard) {
								found.add(card2);
								dup=true;
							}
						}
						if(dup) found.add(card1);
					}
				}
				player.Refresh();
				//Player reveals hand here
				
				//discard duplicate cards
				for(Card c:found) {
					c.move(player.discard,true);
				}
				player.Refresh();
			}
		});
		
		AllCards.add(new DominionCard() {
			void create() {
				name="Circus";
				duration=true;
				action=true;
				cost=5;
			}
			void play(Player player, Card instance) {
				super.play(player,instance);
				player.DrawCard();
				player.DrawCard();
				player.Refresh();
			}
			void durationStartTurn(Player player,Card instance) {
				player.DrawCard();
				player.Refresh();
			}
			void durationAfterRefresh(Player player, Card instance) {
				if(player.theGame.turnPlayer==player) {
					if(player.hand.cards.size()<=2) {
						instance.move(player.discard,true);
					}
				}
			}
		});
		
		
		for(int x=0;x<AllCards.size();x++){
			AllCards.get(x).create();
		}
		AllCards=Alphabetize();
		AllCards.trimToSize();
		for(int x=0;x<AllCards.size();x++) {
			AllCards.get(x).setIndex(x);
		}
	}
	
	
	
	
	private static ArrayList<DominionCard> Alphabetize(){
		//return an arraylist in alphabetical order
		ArrayList<DominionCard> out=(ArrayList<DominionCard>)AllCards.clone();
		for(int pos=0;pos<out.size();pos++){
			int first=pos;
			String firstName=out.get(pos).name.toUpperCase();
			for(int x=pos;x<out.size();x++){
				if(firstName.compareTo(out.get(x).name.toUpperCase())>0){
					first=x;
					firstName=out.get(x).name.toUpperCase();
				}
			}
			if(first!=pos){
				DominionCard c=out.remove(first);
				out.add(pos,c);
			}
		}
		return out;
	}
	
	public static ArrayList<DominionCard> sortByCost(ArrayList<DominionCard> out){
		for(int pos=0;pos<out.size();pos++){
			int first=pos;
			int firstCost=out.get(pos).cost;
			for(int x=pos;x<out.size();x++){
				if(out.get(x).cost<firstCost){
					first=x;
					firstCost=out.get(x).cost;
				}
			}
			if(first!=pos){
				DominionCard c=out.remove(first);
				out.add(pos,c);
			}
		}
		return out;
	}
	
	
	
	public static DominionCard findCard(String name){
		for(int x=0;x<AllCards.size();x++){
			if(AllCards.get(x).name.equals(name)) return AllCards.get(x);
			
		}
		System.out.println("Can not find any card named "+name);
		return AllCards.get(0);
	}
	
	public static ArrayList<DominionCard> pickRandom(Settings settings,int count){
		ArrayList<DominionCard> out=new ArrayList<>();
		
		
		return out;
	}
	
}


/*
There might be a bug with duration cards gained with dancing monkeys
*/