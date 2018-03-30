import java.net.Socket;

import javax.swing.JLabel;

public class ClientSearcher implements Runnable{
		private JLabel label;
		private Thread thread;
		protected Socket socket=null;
		private String ip;
		protected boolean searching=false;
		protected ClientType client=null;

		ClientSearcher(JLabel label){
			this.label=label;
		}
		public void search(String ip) {
			this.ip=ip.trim();
			searching=true;
			thread=new Thread(this);
			thread.start();
		}
		
		public void run() {
			client=new ClientType(ip,Settings.port);
			client.start();
			label.setText("Attempting to Connect...");
			while(client.connected==false && client.active==true) {//wait for connection
				try {Thread.sleep(10);}catch(InterruptedException ex) {ex.printStackTrace();};
			}
			if(client.connected) {
				//Successful connection
				label.setText("Connection Successful.  Waiting for host to start game.");
			}else {
				//Failed connection
				label.setText("Connection Failed");
			}
			searching=false;
		}
		public void quit() {
			client.quit();
		}
	}