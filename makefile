JFLAGS = -d ClassFiles/
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
CLASSES = \
	BlakeWindow.java \
	Card.java \
	CardSet.java \
	CardSetViewBox.java \
	ClickableCard.java \
	ClickableImage.java \
	ClientConnection.java \
	ClientGUI.java \
	ClientSearcher.java \
	ClientType.java \
	DisplayComponent.java \
	DominionCard.java \
	DominionGame.java \
	DominionGUI.java \
	Game.java \
	HostGUI.java \
	HostSearcher.java \
	HostType.java \
	OnlineGame.java \
	Player.java \
	PortRedirector.java \
	Requirements.java \
	Settings.java \
	SupplyPile.java 
default: classes
classes: $(CLASSES:.java=.class)

clean:
	$(RM) ClassFiles/*.class
