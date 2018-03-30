//Experiment with JFrame and Canvas
//Update to Window3, now can have swing components over the drawing
//Not backwards compatable
//Might have screwed up the double buffering?

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
//import java.awt.image.BufferStrategy; 
import java.awt.image.*;

public class BlakeWindow extends JFrame implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener{

	private JPanel MainPanel;
	private BufferedImage Buff;
	private BufferedImage Fore;
	private boolean EditingForeground;
	private long Timer;
	private boolean painted=false;//to fix flickering
	
	protected boolean[] Key =new boolean[256];
	protected int[] KeyTime =new int[256];
	protected boolean[] KeyPress =new boolean[256];
	protected int MouseX=0,MouseY=0;
	protected int MouseStartX=0,MouseStartY=0;
	protected int MouseStopY=0,MouseStopX=0;
	protected boolean MouseClick=false,MouseRightClick=false,MouseMidClick=false;
	protected boolean MouseDown=false,MouseRightDown=false,MouseMidDown=false;
	
	protected boolean MouseOnScreen=false;
	
	
	public BlakeWindow(int width,int height,String name){
		super(name);
		super.setSize(width,height);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE); 
		//setAlwaysOnTop(true);
		
		addKeyListener(this); 
		addMouseListener(this);
		addMouseMotionListener(this);
		
		
		BuffUp(width,height);
		
		MainPanel=new JPanel(){
			public void paint(Graphics g){
				g.drawImage(Buff,0,0,null);
				super.paint(g);
				g.drawImage(Fore,0,0,null);
				painted=true;
				//super.paintChildren(g);
				//g.drawImage(Fore,0,0,null);
			}
		};
		MainPanel.setBounds(0,0,width,height);
		MainPanel.setOpaque(false);
		add(MainPanel);
		
		Timer=System.currentTimeMillis();
		
		refreshKeys();
		
	}
	
	
	
	private void BuffUp(int w,int h){
		Buff=new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);
		Fore=new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	public void clearScreen(Color color){
		Graphics2D G = Buff.createGraphics();
		G.setColor(color);
		G.fillRect(0,0,Buff.getWidth(),Buff.getHeight());
		G.dispose();
		//clearImage(Fore);
	}
	
	
	private void clearImage(BufferedImage img){
		WritableRaster Rast = img.getRaster();
		int[] pix = {0,0,0,0};
		int w=img.getWidth();
		int h=img.getHeight();
		for(int a=0;a<w;a++){
			for(int b=0;b<h;b++){
				Rast.setPixel(a,b,pix);
			}
		}
	}
	
	private void clearImage(int red,int green,int blue, int alpha,BufferedImage img){
		WritableRaster Rast = img.getRaster();
		int[] pix = {red,green,blue,alpha};
		int w=img.getWidth();
		int h=img.getHeight();
		for(int a=0;a<w;a++){
			for(int b=0;b<h;b++){
				Rast.setPixel(a,b,pix);
			}
		}
	}
	
	public JPanel getPanel(){
		return MainPanel;
	}
	
	public void drawStuff(){
		Buff.flush();
		painted=false;
		MainPanel.repaint(0);
		while(painted==false) Thread.yield();//waits for painting to finish
	}
	
	public void sync(int SyncTime)
	{//Has nothing to deal with timer overflow and probably does not wait the first time it is called
		refreshKeys();
		refreshMouse();
		drawStuff();
		while(Timer+(long)SyncTime>System.currentTimeMillis())
		{
			try {Thread.sleep(1);} 
		catch(InterruptedException ex){}
        catch(NullPointerException nex){}
		}
		Timer=System.currentTimeMillis();
	}
	
	public void setSize(int w,int h){
		super.setSize(w,h);
		MainPanel.setBounds(0,0,w,h);
		BuffUp(w,h);
	}
	public void rename(String str){
		setTitle(str);
	}
	public void close(){
      this.dispose();
   }
	
	
	public void drawString(int X,int Y,String Message,Font text,Color color){
		Graphics2D G =Buff.createGraphics();
		G.setFont(text);
		G.setColor(color);
		if(Message!=null)
			((Graphics2D)G).drawString(Message,X+5,Y+44);
		G.dispose();
	}
	public void drawString(String message,int x,int y,Font f,Color color){
		drawString(x,y,message,f,color);
	}
	
	public void drawRect(int x,int y,int w,int h,Color color){
		Graphics2D G = Buff.createGraphics();
		G.setColor(color);
		G.fillRect(x,y,w,h);
		G.dispose();
	}
	
	public void drawOutlineRect(int x,int y,int w,int h,Color color)
	{
		Graphics2D G =Buff.createGraphics();
		G.setColor(color);
		G.drawRect(x,y,w,h);
		G.dispose();
	}
	
	public void drawLine(int x1,int y1,int x2,int y2,Color color){
		Graphics2D G = Buff.createGraphics();
		G.setColor(color);
		G.drawLine(x1,y1,x2,y2);
		G.dispose();
	}
	
	public void drawElipse(int x,int y,int w,int h,Color color){
		Graphics2D G =Buff.createGraphics();
		G.setColor(color);
		G.fillOval(x,y,w,h);
		G.dispose();
	}
	
    public void drawOutlineElipse(int x,int y,int w,int h,Color color){
		Graphics2D G =Buff.createGraphics();
		G.setColor(color);
		G.drawOval(x,y,w,h);
		G.dispose();
	}
	
	public void drawImage(BufferedImage Img,int x,int y){
		Graphics2D G = Buff.createGraphics();
		G.drawImage(Img,x,y,null);
		G.dispose();
	}
	
	public void drawCroppedImage(BufferedImage Img,int dstx1,int dsty1,int dstx2,int dsty2,int srcx1,int srcy1,int srcx2, int srcy2)
	{      //dst=destination, src=image within image
		Graphics2D G = Buff.createGraphics();
		G.drawImage(Img,dstx1,dsty1,dstx2,dsty2,srcx1,srcy1,srcx2,srcy2,null);
		G.dispose();
	}
	
	public void drawScaleImageFit(BufferedImage Img,int x,int y,int width,int height){
		Graphics2D G = Buff.createGraphics();
		G.drawImage(Img,x,y,width,height,null);
		G.dispose();
	}
	
	public void drawScaleImage(BufferedImage Img,int x,int y,double XScale,double YScale){
		Graphics2D G = Buff.createGraphics();
		int height=(int)(Img.getHeight()*YScale);
		int width=(int)(Img.getWidth()*XScale);
		G.drawImage(Img,x,y,width,height,null);
		G.dispose();
	}
	
	public void drawDot(int x,int y,Color c){
		if(x>=0 && x<Buff.getWidth() && y>=0 && y<Buff.getHeight()){
			Buff.getRaster().setPixel(x,y,new int[]{c.getRed(),c.getGreen(),c.getBlue(),255});
		}
	}
	
	public void drawPolygon(Polygon P,Color c){
		Graphics2D G = Buff.createGraphics();
		G.setColor(c);
		G.fillPolygon(P);
		G.dispose();
	}
	
	public void drawPolygon(int[] a,int[] b,Color c){
		drawPolygon(new Polygon(a,b,a.length),c);
	}
	
	public void drawPolygon(int x,int y,int[] a,int[] b,Color c){
		for(int t=0;t<a.length;t++)
		{
			a[t]+=x;
			b[t]+=y;
		}
		drawPolygon(a,b,c);
	}
	
	public void drawOutlinePolygon(Polygon P,Color c){
		Graphics2D G = Buff.createGraphics();
		G.setColor(c);
		G.drawPolygon(P);
		G.dispose();
		Buff.flush();
	}
	
	public void drawOutlinePolygon(int[] a,int[] b,Color c){
		drawOutlinePolygon(new Polygon(a,b,a.length),c);
	}
	
	public void drawOutlinePolygon(int x,int y,int[] a,int[] b,Color c){
		for(int t=0;t<a.length;t++)
		{
			a[t]+=x;
			b[t]+=y;
		}
		drawOutlinePolygon(a,b,c);
	}
	
	public void clearGUI(){
		MainPanel.removeAll();
		//MainPanel.setLayout(new FlowLayout());
	}
	
	
	public void refreshKeys()
	{
		for(int K=0;K<256;K++){
			if(KeyTime[K]==1)
				KeyPress[K]=true;
			else
				KeyPress[K]=false;
			if(KeyTime[K]>0){
				KeyTime[K]++;
			}
		}
	}
	
	public void refreshMouse(){
		MouseClick=false;
		MouseMidClick=false;
		MouseRightClick=false;
	}
	
	public boolean SPKey(String Name)//Test if special key is down
	{
		boolean Out=false;
		Name=Name.toLowerCase();
		int ID=32;//default to spacebar
		if(Name.equals("space"))//these used to use .matches
			ID=32;
		if(Name.equals("up"))
			ID=38;
		if(Name.equals("down"))
			ID=40;
		if(Name.equals("left"))
			ID=37;
		if(Name.equals("right"))
			ID=39;
		if(Name.equals("enter"))
			ID=10;
		if(Name.equals("shift"))
			ID=16;
		if(Name.equals("alt"))
			ID=18;
		if(Name.equals("esc") || Name.equals("escape"))
			ID=27;
		if(Name.equals("backspace"))
			ID=8;
		if(Key[ID])
			Out=true;
		return Out;
	}
	public boolean SPKeyPress(String Name)//Test if special key is pressed
	{
		int ID=Name.charAt(0);
		Name=Name.toLowerCase();
		if(Name.equals("space"))
			ID=32;
		if(Name.equals("up"))
			ID=38;
		if(Name.equals("down"))
			ID=40;
		if(Name.equals("left"))
			ID=37;
		if(Name.equals("right"))
			ID=39;
		if(Name.equals("enter"))
			ID=10;
		if(Name.equals("shift"))
			ID=16;
		if(Name.equals("alt"))
			ID=18;
		if(Name.equals("esc") || Name.equals("escape"))
			ID=27;
		if(Name.equals("backspace"))
			ID=8;
		return KeyPress[ID];
	}
	public boolean LetterKey(char Let)
	{
		return (Key[(int)Let]);

	}
	public boolean LetterKeyPress(char Let)
	{
		return (KeyPress[(int)Let]);
	}
	public boolean Key(char c){return LetterKey(c);}//better names
	public boolean KeyPress(char c){return LetterKeyPress(c);}
	
	public void addComponent(Component c){
		MainPanel.add(c);
	}
	
	
	
	
	public void mouseExited(MouseEvent M){
		MouseOnScreen=false;
	}
	
	public void mouseEntered(MouseEvent M){
		MouseOnScreen=true;
		MouseX=M.getX();
		MouseY=M.getY();
	}
	
	public void mouseReleased(MouseEvent M){
		if(M.getButton()==MouseEvent.BUTTON1)
		{
			MouseDown=false;
			MouseStopY=M.getY();
			MouseStopX=M.getX();
			//use mousestart and stop for drag and drop
		}
		if(M.getButton()==MouseEvent.BUTTON2){
			MouseMidDown=false;
		}
		if(M.getButton()==MouseEvent.BUTTON3){
			MouseRightDown=false;
		}
		MouseX=M.getX();
		MouseY=M.getY();
	}
	
	public void mousePressed(MouseEvent M){
		if(M.getButton()==MouseEvent.BUTTON1){
			MouseDown=true;
			//MouseClick=true;//
			MouseStartY=M.getY();
			MouseStartX=M.getX();
		}
		if(M.getButton()==MouseEvent.BUTTON2){
			MouseMidDown=true;
		}
		if(M.getButton()==MouseEvent.BUTTON3){
			MouseRightDown=true;
		}
		MouseX=M.getX();
		MouseY=M.getY();
	}
	
	public void mouseClicked(MouseEvent M){
		if(M.getButton()==MouseEvent.BUTTON1)
			MouseClick=true;
		if(M.getButton()==MouseEvent.BUTTON2)
			MouseMidClick=true;
		if(M.getButton()==MouseEvent.BUTTON3)
			MouseRightClick=true;
		//MouseX=M.getX();
		//MouseY=M.getY();
	}
	public void mouseWheelMoved(MouseWheelEvent M)//not yet used
	{//interface override
	
	}
	public void mouseDragged(MouseEvent M)
	{//interface override
		MouseX=M.getX();
		MouseY=M.getY();
	}
	public void mouseMoved(MouseEvent M)
	{//interface override
		MouseX=M.getX();
		MouseY=M.getY();
	}
	
	public void keyPressed(KeyEvent e) { 
		if(e.getKeyCode()<256)
		{
			Key[e.getKeyCode()] = true;
			KeyTime[e.getKeyCode()]=1;
		}
	} 
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()<256)
			{
				Key[e.getKeyCode()] = false;
				KeyTime[e.getKeyCode()]=0;
			}
	} 
	
	public void keyTyped(KeyEvent e){
		
	}
	
}