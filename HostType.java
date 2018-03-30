import java.util.ArrayList;

class HostType implements Runnable{
	ArrayList<ClientConnection> clientConnections=new ArrayList<ClientConnection>();
	PortRedirector redirector;
	int firstPort;
	int redirectPort;
	protected Thread thread=null;
	boolean active=false;
	
	HostType(int redirectPort,int firstPort){
		this.redirectPort=redirectPort;
		this.firstPort=firstPort;
	}
	
	public void start() {
		thread=new Thread(this);
		thread.start();
	}
	public void run() {
		active=true;
		redirector=new PortRedirector(redirectPort,this);
		redirector.start();
		//redirector is now searching for new clients and calling addClient when one wants to connect
		//Maintain connections and delete disconnected items
		while(active) {
			for(int a=0;a<clientConnections.size();a++) {
				ClientConnection con=clientConnections.get(a);
				if(con.client!=null && (con.client.isClosed()==true)) {
					//client has disconnected
					con.stop();
					clientConnections.remove(a);
					a-=1;
				}
			}
			Thread.yield();
		}
	}

	public int findOpenPort() {
		int out=firstPort;
		boolean done=false;
		while(done==false) {
			boolean found=false;
			for(int a=0;a<clientConnections.size();a++) {
				if(clientConnections.get(a).port==out) {
					out++;
					found=true;
					break;
				}
			}
			if(found==false) done=true;
		}
		return out;
	}
	
	public void cutoff() {
		//cut off new clients
		redirector.stop();
	}
	
	public void addClient(int toPort) {
		ClientConnection con=new ClientConnection(toPort);
		clientConnections.add(con);
		con.start();
	}
	
	public boolean output(int slot,String str) {
		if(slot<0 || slot>=clientConnections.size()) return false;
		ClientConnection con=clientConnections.get(slot);
		return con.output(str);
	}
	
	public void outputAll(String str) {
		for(int a=0;a<clientConnections.size();a++) {
			output(a,str);
		}
	}
	
	public String getNext(int slot) {
		if(slot<0 || slot>=clientConnections.size()) return null;
		ClientConnection con=clientConnections.get(slot);
		return con.getNext();
	}
	
	public void quit() {
		//end the game or when the player pressed quit while searching for clients
		active=false;
		redirector.stop();
		for(int a=0;a<clientConnections.size();a++) {
			clientConnections.get(a).stop();
		}
	}
}