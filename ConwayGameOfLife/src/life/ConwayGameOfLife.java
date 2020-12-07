package life;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class ConwayGameOfLife extends Frame implements MouseListener, MouseMotionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new ConwayGameOfLife();
	}
	
	public ConwayGameOfLife() {
		this.addMouseMotionListener(this);
		this.addMouseListener(this);		
		this.addWindowListener(window);
		setZoomLevels();
		setSpeedLevels();
		this.setSize(IMAGEWIDTH,IMAGEHEIGHT);
		this.setLayout(null);
		this.setVisible(true);
		int i;
		int j;
		this.img = new BufferedImage(IMAGEWIDTH,IMAGEHEIGHT,BufferedImage.TYPE_INT_RGB);
		for(i=0; i<IMAGEWIDTH; i++) {
			for(j=0; j<IMAGEHEIGHT; j++) {
				this.img.setRGB(i,j,0xFFFFFFFF);
			}
		}

		this.world = new World();
		Button startButton = new Button("start");
		Button oneStepButton = new Button("one step");
		Button zoomInButton = new Button("zoom in");
		Button zoomOutButton = new Button("zoom out");
		Button speedUpButton = new Button("speed up");
		Button slowDownButton = new Button("slow down");
		startButton.setBounds(0, 40, 80, 30);
		oneStepButton.setBounds(80, 40, 80, 30);
		zoomInButton.setBounds(160, 40, 80, 30);
		zoomOutButton.setBounds(240, 40, 80, 30);
		speedUpButton.setBounds(320, 40, 80, 30);
		slowDownButton.setBounds(400, 40, 80, 30);
		startButton.addActionListener(new ButtonActions(this, Buttons.START));
		oneStepButton.addActionListener(new ButtonActions(this, Buttons.ONESTEP));
		zoomInButton.addActionListener(new ButtonActions(this, Buttons.ZOOMIN));
		zoomOutButton.addActionListener(new ButtonActions(this, Buttons.ZOOMOUT));
		speedUpButton.addActionListener(new ButtonActions(this, Buttons.SPEEDUP));
		slowDownButton.addActionListener(new ButtonActions(this, Buttons.SLOWDOWN));
		this.add(startButton);
		this.add(oneStepButton);
		this.add(zoomInButton);
		this.add(zoomOutButton);
		this.add(speedUpButton);
		this.add(slowDownButton);
		this.startButton = startButton;
	}
	
	private BufferedImage img;
	
	private World world;

	/**
	 * the horizontal offset in pixels of the image
	 */
	private int horizontalOffset=0;

	/**
	 * the vertical offset in pixels of the image
	 */
	private int verticalOffset=0;
	
	private int squareWidth = 20;
	
	private int timeLapse = 800;
	
	protected boolean active = false;
	
	private boolean isTerminated = false;
	
	private final static int IMAGEWIDTH = 1000;

	private final static int IMAGEHEIGHT = 600;
	
	private final static int LIVESQUARERGB = 0xFF5050FF;

	private final static int BACKGROUNDRGB = 0xFFFFFFFF;
	
	private int mouseX;

	private int mouseY;
	
	private Button startButton;
	
	private Timer timer;
	
	private WindowAdapter window = new Window(this);

	private void paint() {
		Graphics g=getGraphics();
		g.drawImage(this.img, 0, 0,this);
	}
	
	private void terminate() {
		if(this.active) {
			this.isTerminated = true;
		}else {
			this.dispose();
		}
	}
	
	private int getXPosition(int pixelX) {
		return myDivision(pixelX + this.horizontalOffset,this.squareWidth);
	}

	private int getYPosition(int pixelY) {
		return myDivision(pixelY + this.verticalOffset,this.squareWidth);
	}

	private int getLeftmostPixel(int squareX) {
		return (squareX*this.squareWidth-this.horizontalOffset);
	}
	
	private int getTopPixel(int squareY) {
		return (squareY*this.squareWidth-this.verticalOffset);
	}
	
	/**
	 * if square is alive, it becomes dead and vice versa
	 * @param pixelX
	 * @param pixelY
	 */
	private void clickSquare(int pixelX, int pixelY) {
		int x = this.getXPosition(pixelX);
		int y = this.getYPosition(pixelY);
		boolean status = !this.world.getSquare(x, y);
		this.world.changeSquare(x, y, status);
		int xx = getLeftmostPixel(x);
		int yy = getTopPixel(y);
		for(int xxx=xx; xxx<xx+this.squareWidth;xxx++) {
			for(int yyy=yy; yyy<yy+this.squareWidth;yyy++) {
				if (status) this.img.setRGB(xxx, yyy, LIVESQUARERGB);
				else this.img.setRGB(xxx, yyy, BACKGROUNDRGB);
			}
		}
		this.paint();
//		this.updateImage();
	}
	
	private void updateImage() {
		for(int x = 0; x<IMAGEWIDTH; x++) {
			for(int y = 0; y<IMAGEHEIGHT; y++) {
				int xx = this.getXPosition(x);
				int yy = this.getYPosition(y);
				int rgb;
				if(this.world.getSquare(xx, yy)) rgb = LIVESQUARERGB;
				else rgb = BACKGROUNDRGB;
				this.img.setRGB(x, y, rgb);
			}
		}
		this.paint();
	}
	
	private static int[] zoomLevels = new int[10];
	
	private static void setZoomLevels() {
		zoomLevels[0] = 1;
		zoomLevels[1] = 2;
		zoomLevels[2] = 4;
		zoomLevels[3] = 7;
		zoomLevels[4] = 12;
		zoomLevels[5] = 20;
		zoomLevels[6] = 30;
		zoomLevels[7] = 40;
		zoomLevels[8] = 60;
		zoomLevels[9] = 90;
	}
	
	private void zoomOut() {
		int zoom = this.squareWidth;
		for(int i = 9; i>=0; i--) {
			if(zoomLevels[i]<zoom) {
				this.adjustOffsets(zoom,zoomLevels[i]);
				this.squareWidth = zoomLevels[i];
				this.updateImage();
				return;
			}
		}
		this.adjustOffsets(zoom,zoomLevels[0]);
		this.squareWidth = zoomLevels[0];
		this.updateImage();
	}

	private void zoomIn() {
		int zoom = this.squareWidth;
		for(int i = 0; i<=9; i++) {
			if(zoomLevels[i]>zoom) {
				this.adjustOffsets(zoom,zoomLevels[i]);
				this.squareWidth = zoomLevels[i];
				this.updateImage();
				return;
			}
		}
		this.adjustOffsets(zoom,zoomLevels[9]);
		this.squareWidth = zoomLevels[9];
		this.updateImage();
	}
	
	private void adjustOffsets(int oldSquareWidth, int newSquareWidth) {
		double centerOfScreenX = IMAGEWIDTH/2;
		double centerOfScreenY = IMAGEHEIGHT/2;
		double centralXPosition = (centerOfScreenX+this.horizontalOffset)/oldSquareWidth;
		double centralYPosition = (centerOfScreenY+this.verticalOffset)/oldSquareWidth;
		this.horizontalOffset = (int) (newSquareWidth * centralXPosition - centerOfScreenX);
		this.verticalOffset = (int) (newSquareWidth * centralYPosition - centerOfScreenY);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		this.horizontalOffset -= e.getX()-this.mouseX;
		this.verticalOffset -= e.getY()-this.mouseY;
		this.mouseX = e.getX();
		this.mouseY = e.getY();
		this.updateImage();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
		this.clickSquare(e.getX(), e.getY());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	protected void buttonClicked(Buttons button) {
		switch(button.getNumber()) {
			case 0:{this.startButtonClicked(); break;}
			case 1:{this.oneStepButtonClicked(); break;}
			case 2:{this.zoomIn(); break;}
			case 3:{this.zoomOut(); break;}
			case 4:{this.speedUp(); break;}
			case 5:{this.slowDown(); break;}
		}
	}
	
	private void startButtonClicked() {
		this.active=!this.active;
		if(this.active) {
			this.timer = new Timer();
			this.timer.schedule(new TimerStep(this), this.timeLapse);
			this.startButton.setLabel("stop");
		}else {
			this.startButton.setLabel("start");			
		}
	}
	
	private void oneStepButtonClicked() {
		this.world.advanceTime();
		this.updateImage();
	}
	
	private static int[] speedLevels = new int[10];
	
	private static void setSpeedLevels() {
		speedLevels[0] = 10;
		speedLevels[1] = 20;
		speedLevels[2] = 40;
		speedLevels[3] = 70;
		speedLevels[4] = 100;
		speedLevels[5] = 150;
		speedLevels[6] = 200;
		speedLevels[7] = 300;
		speedLevels[8] = 500;
		speedLevels[9] = 800;
	}
	

	
	private void speedUp() {
		int speed = this.timeLapse;
		for(int i = 9; i>=0; i--) {
			if(speedLevels[i]<speed) {
				this.timeLapse = speedLevels[i];
				return;
			}
		}
	}

	private void slowDown() {
		int speed = this.timeLapse;
		for(int i = 0; i<=9; i++) {
			if(speedLevels[i]>speed) {
				this.timeLapse = speedLevels[i];
				return;
			}
		}
	}

	class TimerStep extends TimerTask{
		public TimerStep(ConwayGameOfLife life) {
			this.life = life;
		}
		
		ConwayGameOfLife life;

		@Override
		public void run() {
			if(life.isTerminated) {
				life.dispose();
			}else {
				if(life.active) {
					life.world.advanceTime();
					life.updateImage();
					life.timer = new Timer();
					life.timer.schedule(new TimerStep(life), life.timeLapse);
				}
			}
		}
	}
	
	class Window extends WindowAdapter{
		
		Window(ConwayGameOfLife life){
			this.life = life;
		}
		
		ConwayGameOfLife life;
		
		public void windowClosing(WindowEvent e) {
//			life.dispose();
			life.terminate();
		}
	}

	/**
	 * denominator>0
	 * @param numerator
	 * @param denominator
	 * @return
	 */
	private static int myDivision(int numerator, int denominator){
		int d = Math.max(denominator, -denominator);
		if(numerator>=0) {
			return numerator/d;
		}else {
			return (numerator+1-d)/d;
		}
	}
}