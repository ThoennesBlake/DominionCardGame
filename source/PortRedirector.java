import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class PortRedirector implements Runnable{
	//constantly searches for new clients
	//when it finds one it responds with the next open port number then closes the connection
	int port;
	HostType host;
	boolean active=false;
	protected Thread thread;
	PortRedirector(int port, HostType host){
		this.port=port; this.host=host;
	}
	public void start() {
		active=true;
		thread=new Thread(this);
		thread.start();
	}
	public void run() {
		ServerSocket socket;
		try {
			socket=new ServerSocket(port);
		}catch(IOException ex) {
			ex.printStackTrace(); active=false; return;
		}
		while(active) {
			try {
				Socket client=socket.accept();
				InputStream inputStream=client.getInputStream();
				OutputStream outputStream=client.getOutputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
				PrintWriter write = new PrintWriter(outputStream, true);
				String inputLine=null;
				try {
					while ((inputLine = in.readLine()) != null) {//wait for question from client
						if(inputLine.equals("findport")) {
							int p=host.findOpenPort();
							host.addClient(p);
							write.println(p);
							break;
						}
						Thread.sleep(20);
					}
					client.close();
					inputStream.close();
					outputStream.close();
					write.close();
					in.close();
				}catch(Exception ex) {
					ex.printStackTrace();
					active=false;
					return;
				}
			}catch(IOException ex) {
				 ex.printStackTrace();
			}
			try {Thread.sleep(10);}catch(InterruptedException ex) {ex.printStackTrace();};
		}
		try {
			socket.close();
		}catch(IOException ex) {ex.printStackTrace();}
	}
	public void stop() {
		active=false;
	}
}