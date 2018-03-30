import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class ClientType implements Runnable{
	protected int port;
	protected String targetIP;
	protected boolean active=false;
	protected Socket socket=null;
	protected InputStream inputStream=null;
	protected OutputStream outputStream=null;
	protected Thread thread;
	private PrintWriter writer=null;
	protected ArrayList<String> recieved=new ArrayList<String>();
	protected boolean connected=false;
	public ClientType(String targetIP,int port) {
		this.port=port;
		this.targetIP=targetIP;
	}
	public void start() {
		active=true;
		connected=false;
		thread=new Thread(this);
		thread.start();
	}
	public void run() {
		try {
			socket=new Socket(targetIP,port);
			inputStream=socket.getInputStream();
			outputStream=socket.getOutputStream();
			writer=new PrintWriter(outputStream,true);
		}catch(IOException ex) {
			System.out.println("Failed to connect");
			ex.printStackTrace();
			active=false;
			return;
		}
		//Upon connecting ask for the new port
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		writer.println("findport");
		String inputLine=null;
		try {
			while ((inputLine = in.readLine()) != null) {//wait for response
				port=Integer.parseInt(inputLine.trim());
				System.out.println("Switching to port "+port+".");
				Thread.yield();//Thread.sleep(40);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			active=false;
			return;
		}
		//Open a new socket to connect to the new port
		System.out.println("Connecting step 2");
		try{
			inputStream.close();
			outputStream.close();
			socket.close();
			socket=new Socket(targetIP,port);
			inputStream=socket.getInputStream();
			outputStream=socket.getOutputStream();
			writer=new PrintWriter(outputStream,true);
		}catch(IOException ex) {ex.printStackTrace(); active=false; return;}
		//Now it should be connected properly
		writer.println("connecting");
		connected=true;
		active=true;
		System.out.println("Connected?");
		//Main Loop for recieving data
		in = new BufferedReader(new InputStreamReader(inputStream));
		inputLine=null;
		try {
			while(active) {
				while ((inputLine = in.readLine()) != null) {//wait for response
					recieved.add(inputLine);
					System.out.println("Recieved: "+inputLine);
					Thread.yield();//try {Thread.sleep(10);}catch(InterruptedException ex) {ex.printStackTrace();};
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			active=false;
			connected=false;
			return;
		}
		System.out.println("exited loop");
		connected=false;
		active=false;
	}
	
	public boolean output(String msg) {
		if(active==false) return false;
		writer.println(msg);
		return true;
	}
	
	public String getNext(){
		String out=null;
		if(recieved.size()>0 && active) {
			out=recieved.get(0);
			recieved.remove(0);
		}
		return out;
	}
	public void quit() {
		active=false;
		try{
			if(socket!=null) socket.close();
		}catch(IOException ex) {ex.printStackTrace();}
	}
}