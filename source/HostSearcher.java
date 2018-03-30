import javax.swing.JLabel;

public class HostSearcher implements Runnable{
		public boolean running;
		private JLabel[] labels;
		private JLabel topLabel;
		private Settings set;
		protected HostType host;
		protected Thread thread=null;
		HostSearcher(Settings set,JLabel topLabel, JLabel[] labels){
			this.set=set;
			this.topLabel=topLabel;
			this.labels=labels;
		}
		public void start() {
			thread=new Thread(this);
			thread.start();
		}
		public void run() {
			running=true;
			host=new HostType(Settings.port,Settings.port+1);
			host.start();
			while(running) {
				for(int a=0;a<labels.length;a++) {
					if(a<host.clientConnections.size()) {
						ClientConnection con=host.clientConnections.get(a);
						if(con.client!=null && con.client.isConnected()) {
							labels[a].setText("Player "+(a+2)+": "+con.client.getInetAddress());
						}else {
							labels[a].setText("Player "+(a+2)+": - - - ");
						}
					}else labels[a].setText("Player "+(a+2)+": - - - ");
				}
				try {Thread.sleep(10);}catch(InterruptedException ex) {ex.printStackTrace();};
			}
			host.cutoff();
		}
		public boolean canStartGame() {
			//if there is atleast one connected client
			for(int a=0;a<host.clientConnections.size();a++) {
				ClientConnection con=host.clientConnections.get(a);
				if(con.client!=null && con.client.isConnected()) return true;
			}
			return false;
		}
		public ClientConnection[] getClientList() {
			ClientConnection[] out=new ClientConnection[host.clientConnections.size()];
			for(int a=0;a<host.clientConnections.size();a++) {
				out[a]=host.clientConnections.get(a);
			}
			return out;
		}
		public void quit() {
			host.quit();
		}
	}