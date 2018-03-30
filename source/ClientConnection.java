import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ClientConnection implements Runnable{//both a listener and a speaker
	protected int port;
	protected ServerSocket socket=null;
	protected Socket client=null;
	protected InputStream inputStream=null;
	protected OutputStream outputStream=null;
	protected boolean active=false;
	protected Thread thread=null;
	protected ArrayList<String> recieved=new ArrayList<String>();
	protected PrintWriter writer=null;
	ClientConnection(int port){
		this.port=port;
	}
	
	public void start() {
		thread=new Thread(this);
		thread.start();
	}
	public void run() {
		try {
			socket = new ServerSocket(port);
		}catch(IOException ex) {
			ex.printStackTrace(); return;
		}
		System.out.println("Waiting for client on port "+port+".");
		try {
			client=socket.accept();
			System.out.println("A client was found.");
			inputStream=client.getInputStream();
			outputStream=client.getOutputStream();
			active=true;
		}catch(IOException ex) {
			System.out.println("Connection Error");
			ex.printStackTrace();
			return;
		}
		writer = new PrintWriter(outputStream, true);
		writer.println("hello");
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		String inputLine=null;
		try {
			while(active) {
				while ((inputLine = in.readLine()) != null) {
		            recieved.add(inputLine);
		            writer.println("recieved");
		            System.out.println("Recieved: "+inputLine+" from port "+port+".");
		            try {Thread.sleep(10);}catch(InterruptedException ex) {ex.printStackTrace();};
		        }
			}
		}catch(IOException ex) {
			System.out.println("Connection Lost");
			ex.printStackTrace();
		}
		System.out.println("Connection Ended");
		active=false;
		try {
			client.close();
			socket.close();
			inputStream.close();
			outputStream.close();
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean output(String msg) {
		if(active==false) return false;
		writer.println(msg);
		return true;
	}
	
	public String getNext(){
		String out=null;
		try {
			if(recieved.size()>0 && active) {//i can't figure out why this sometimes throws index out of bounds exceptions
				out=recieved.get(0);
				recieved.remove(0);
			}
		}catch(Exception ex) {ex.printStackTrace();}
		return out;
	}
	public void stop() {
		active=false;
	}
}