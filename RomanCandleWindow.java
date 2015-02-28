//This class contains the window in which the Roman Candle is rendered.  This includes a GUI to 
//faciliate the changing of variables as well as allowing to Play, Pause, Stop or Exit the Simulation.

//Possible Extra marks include the background, the tool-tips, the pause feature
//As well as the play pause and stop icons used in the button.

//There are two threads in this animation: clock and painter.
//The clock thread calculates the particles position, updating the fireworks array
//The painter thread draws the images on the screen.  Having these threads seperately allows
//for the animation to stop running, but allowing to adjust the windspeed or launch angle while
//letting the animation paint the tilting launch tube.
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class RomanCandleWindow extends JFrame{

	private final int MIN_WIDTH = 600;
	private final int MIN_HEIGHT = 600;
	private final int MAX_WIDTH = 1920;
	private final int MAX_HEIGHT = 1080;
	private final int ICON_SIZE = 16;
	private final int LAUNCH_TUBE_CONVERT = 22;		//px/px
	
	private Graphics2D graphic;
	private ImagePanel img;
	private JPanel bottom;
	BufferedImage image;
	Timer painter;
	Timer clock;
	boolean running;
	boolean begin;
	
	JButton start;
	JButton stop;
	SpinnerNumberModel windSpinModel;
	JSpinner windVelSpinner;
	SpinnerNumberModel tiltSpinModel;
	JSpinner tiltSpinner;
	ImageIcon playIcon;
	ImageIcon pauseIcon;
	ImageIcon stopIcon;
	
	double wind;
	double launchAngle;
	ArrayList<Particle> fireworks = null;
	int timeInterval = 1000/60;		// 60 fps
	double time;
	ParticleManager manager = null;
	
	public RomanCandleWindow()
	{
		super();
		//Window Properties
		begin = true;
		wind = 0;
		launchAngle = 0;
		setMinimumSize(new Dimension(MIN_WIDTH,MIN_HEIGHT));
		setMaximumSize(new Dimension(MAX_WIDTH,MAX_HEIGHT));
		setResizable(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowEventHandler());	//Dispose and close on Exit
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		bottom = new JPanel(new FlowLayout());
		
		//GUI_Interface: Spinners
		windSpinModel = new SpinnerNumberModel(0, -20, 20, 1);
		windVelSpinner = new JSpinner(windSpinModel);
		windVelSpinner.addChangeListener(new WindListener());
		JLabel windLabel = new JLabel("Wind Speed km/h");
		windLabel.setToolTipText("Positive is to the Right");
		bottom.add(windLabel);
		windVelSpinner.setToolTipText("Spinn-a-ding-ding");
		bottom.add(windVelSpinner);
		tiltSpinModel = new SpinnerNumberModel(0, -15, 15, 1);
		tiltSpinner = new JSpinner(tiltSpinModel);
		tiltSpinner.addChangeListener(new TiltListener());
		JLabel tiltLabel = new JLabel("     Launch Angle Degrees");
		tiltLabel.setToolTipText("Positive tilts to the right");
		bottom.add(tiltLabel);
		tiltSpinner.setToolTipText("Spinn-a-ding-ding");
		bottom.add(tiltSpinner);
		wind = windSpinModel.getNumber().intValue();
		launchAngle = tiltSpinModel.getNumber().intValue();
		add(bottom, BorderLayout.SOUTH);
		
		//GUI_Interface: Buttons
		start = new JButton();
		start.setToolTipText("Play");
		playIcon = new ImageIcon("play.png");
		playIcon = new ImageIcon(getScaledImage(playIcon.getImage(), ICON_SIZE, ICON_SIZE));
		pauseIcon = new ImageIcon("pause.png");
		pauseIcon = new ImageIcon(getScaledImage(pauseIcon.getImage(), ICON_SIZE, ICON_SIZE));
		start.setIcon(playIcon);
		start.addActionListener(new PlayListener());
		bottom.add(start);
		
		stop = new JButton();
		stopIcon = new ImageIcon("stop.png");
		stopIcon = new ImageIcon(getScaledImage(stopIcon.getImage(), ICON_SIZE, ICON_SIZE));
		stop.setIcon(stopIcon);
		stop.addActionListener(new StopListener());
		stop.setToolTipText("Reset Animation");
		bottom.add(stop);
		
		JButton exit = new JButton("Exit");
		exit.addActionListener(new ExitListener());
		exit.setToolTipText("Exit Application");
		bottom.add(exit);
		img = new ImagePanel();
		img.setToolTipText("ToolTips all around for extra marks?");
		add(img,BorderLayout.CENTER);
		
		//Setting up Threads
		clock = new Timer(timeInterval, new ClockListener());
		painter = new Timer(timeInterval, new PaintListener());
		running = false;
		reset();	//Stops clock thread and resets Particle Manager and Button statuses
		painter.start();
	}
	
	//Pauses the animation, allowing it to resume later
	private void pause()
	{
		begin = false;
		clock.stop();
		start.setText("Start");
		start.setIcon(playIcon);
		running = false;
	}
	
	//Resumes the animation
	private void resume()
	{
		begin = false;
		fireworks = manager.getFireworks(time);
		clock.start();
		start.setText("Pause");
		start.setIcon(pauseIcon);
		running = true;
	}
	
	//Starts the particle manager
	private void start()
	{
		manager.start(time);
	}
	
	//Clears all stars on screen and resets the buttons
	private void reset()
	{
		clock.stop();
		time = 0;
		begin = true;
		running = false;
		start.setText("Start");
		start.setIcon(playIcon);
		try {
			manager = new ParticleManager(wind, launchAngle);
		} catch (EnvironmentException except) {
			System.out.println(except.getMessage());
			return;
		} catch (EmitterException except) {
			System.out.println(except.getMessage());			
			return;
		}
	}
	
	//Stops all threads and disposes of the graphic and flushes the image before
	//Exiting the program.  Called by the Exit button and the window's closes "X" button
	private void close()
	{
		if(clock.isRunning())
			clock.stop();
		if(painter.isRunning())
			painter.stop();
		graphic.dispose();
		image.flush();
		System.exit(0);
	}

	//Used by the exit button to launch the close method which exits the program while stopping all threads
	private class ExitListener implements ActionListener
	{
		public ExitListener(){
			super();
		}
		public void actionPerformed(ActionEvent e)
		{
			close();
		}
	}
	
	//Starts, resumes or pauses the animation depending on what stage the animation is at:
	//(Running, Pauses or reset)
	private class PlayListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(begin)
				start();
			if(!running)
				resume();
			else
				pause();
		}
	}
	
	//Stops and resets the animation
	private class StopListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			reset();
			fireworks = new ArrayList<Particle>();
		}
	}
	
	//Scales an image to fit inside the Buttons
	private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
	
	//Closes Window properly (stopping threads) upon clicking window's exit button "X"
	private class WindowEventHandler extends WindowAdapter {
		  public void windowClosing(WindowEvent evt) {
		    close();
		  }
		}
		
	//Thread that updates the fireworks array once per frame and advances the time by the same about
	//If the manager is completed firing all 8 fireworks, reset the animation
	private class ClockListener implements ActionListener
	{
		public ClockListener(){
			super();
		}
		public void actionPerformed(ActionEvent e)
		{
			if(!manager.checkIfFinished())
			{
			fireworks = manager.getFireworks(time);
			time += timeInterval/1000.0;
			}
			else
			{
				reset();
			}
		}
	}
		
	//Paints the animation every frame, layer by layer.  Background, then launch tube, then particles
	private class PaintListener implements ActionListener
	{
		public PaintListener(){
			super();
		}
		public void actionPerformed(ActionEvent e)
		{
			int width = img.getWidth();
			int height = img.getHeight();
			ImageIcon background = new ImageIcon("background.png");
			image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			graphic = image.createGraphics();
			graphic.drawImage(background.getImage(), 0, 0, width, height, null);	//Draw background
			int endX = drawLaunchTube(width,height);		//Draw LaunchTube
			if(fireworks != null && fireworks.size() != 0)	//If fireworks draw fireworks
			{
				for (Particle firework : fireworks) {
					int[] pixelPos = positionScale(width,height, firework.getPosition());
					if(firework instanceof LaunchSpark)
						drawLaunchSpark(pixelPos[0], pixelPos[1], firework);
					else
						drawStar(pixelPos[0], pixelPos[1], endX, firework);
				}
			}
			img.repaint();	//Paint image
			bottom.repaint();	//Paint bottom frame
		}
	}
	
	//Used by the WindVel spinner to update the managers wind velocity upon value change
	private class WindListener implements ChangeListener
	{
		public WindListener(){
			super();
		}
		public void stateChanged(ChangeEvent e)
		{
			wind = windSpinModel.getNumber().intValue();
			if(manager != null)
			{
				try {
					manager.setWindVelocity(wind);
					
				} catch (EnvironmentException e1) {
				}
				bottom.repaint();
			}
		}
	}
	
	//Used by the tilt spinner to update the managers firing angle upon value change
	private class TiltListener implements ChangeListener
	{
		public TiltListener(){
			super();
		}
		public void stateChanged(ChangeEvent e)
		{
			launchAngle = tiltSpinModel.getNumber().intValue();
			if(manager != null)
			{
				try {
					manager.setFiringAngle(launchAngle);
				} catch (EmitterException e1) {
				}
				bottom.repaint();
			}
		}
	}
	
	//Used to scale a array or 2 doubles from meters to pixels using the window's width and height
	//The height of the window is always 22m.  Returned is the position in pixels
	private int[] positionScale(int width, int height, double[] pos)
	{
		int[] returnPos = new int[2];
		double convert = height/22.0;	//px/m
		returnPos[0] = (int)(pos[0]*convert + width/2.0);	//x
		returnPos[1] = (int)(height - pos[1]*convert);		//y
		return returnPos;
	}//end returnPos
	
	//Draw a star as an circle based on its size and colour.  Uses positionScale to calculate
	//the position in pixels, as well as having an offset for the star to line it up with the launch tube's
	//tip (endX) upon launch
	private void drawStar(int posX, int posY, int endX, Particle star)
	{
		int size = star.getRenderSize();
		graphic.setColor(translateColour(star.getColour()));
		graphic.fillOval(posX-size/2 + endX,posY-size/2,size,size);
	}
	
	//Draw the launchTube as a series of 10 lines stacked side by side with a gradient.
	//Multiple lines are used as it is easier to draw a line on a slant than a rectangle.
	//Returns the tip of the launch tube's x position in pixels.
	private int drawLaunchTube(int width, int height)
	{
		int size = img.getHeight()/LAUNCH_TUBE_CONVERT;
		int posY = (int)(size * Math.cos(Math.toRadians(launchAngle)));
		int posX = (int)(size * Math.sin(Math.toRadians(launchAngle)));
		graphic.setPaint(new GradientPaint(0, height-size, Color.BLUE, 0, height, Color.GREEN));
		for(int i = 0; i<10; i++)
		{
			graphic.drawLine(width/2 - 5 + i, height, width/2 + posX - 5 + i, height - posY);
		}
		return posX;
	}

	//Draws a launch spark as a line according to it's colour
	private void drawLaunchSpark(int posX, int posY, Particle spark)
	{
		int size = spark.getRenderSize();
		graphic.setColor(translateColour(spark.getColour()));
		graphic.drawLine(posX, posY, posX + size, posY + size);	
	}
	
	//Translates a string containing a colour to its appropriate colour object
	private Color translateColour(String colour) {
		Color returnColour = Color.BLACK;
		switch (colour.toLowerCase()) {
			case "blue" :
				returnColour = Color.BLUE;
				break;
			case "green" :
				returnColour = Color.GREEN;
				break;
			case "orange" :
				returnColour = Color.ORANGE;
				break;
			case "red" :
				returnColour = Color.RED;
				break;
			case "yellow" :
				returnColour = Color.YELLOW;
				break;
			case "white" :
				returnColour = Color.WHITE;
				break;
			case "cyan" :
				returnColour = Color.CYAN;
				break;
			case "magenta" :
				returnColour = Color.MAGENTA;
		}
		return returnColour;
	}
	
	//Panel that is drawn to and used to create to create the animation.
	//The overrided paint function allows for the drawing of the image created
	//by the painter thread.
	private class ImagePanel extends JPanel
	{
		public ImagePanel()
		{
			super();
		}
		@Override
		public void paint(Graphics g)
		{
			g.clearRect(0, 0, getWidth(), getHeight());
			g.drawImage(image, 0, 0, null);
		}
	}

}