package com.guidewire.devtraining.fireworks;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * This class contains the window in which the Roman Candle is rendered.  This includes a GUI to
 * faciliate the changing of variables as well as allowing to Play, Pause, Stop or Exit the Simulation.
 * <p>
 * Possible Extra marks include the background, the tool-tips, the pause feature
 * As well as the play pause and stopButton icons used in the button.
 * <p>
 * There are two threads in this animation: clock and painter.
 * The clock thread calculates the particles position, updating the fireworks array
 * The painter thread draws the images on the screen.  Having these threads seperately allows
 * for the animation to stopButton running, but allowing to adjust the windspeed or launch angle while
 * letting the animation paint the tilting launch tube.
 **/
public class RomanCandleWindow extends JFrame {

    private final static String PLAY_ICON = "images/play.png";
    private final static String PAUSE_ICON = "images/pause.png";
    private final static String STOP_ICON = "images/stop.png";
    private final static String BACKGROUND_IMAGE = "images/background.png";

    private final static String PAUSE_TEXT = "Pause";
    private final static String START_TEXT = "Start";

    private final int MIN_WIDTH = 800;
    private final int MIN_HEIGHT = 680;
    private final int MAX_WIDTH = 1920;
    private final int MAX_HEIGHT = 1080;
    BufferedImage image;
    Timer painter;
    Timer clock;
    boolean running;
    boolean begin;
    JButton playButton;
    JButton stopButton;
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
    int timeInterval = 1000 / 60;        // 60 fps
    double time;
    ParticleManager manager = null;
    private Graphics2D graphic;
    private ImagePanel imagePanel;
    private JPanel buttonBar;

    public RomanCandleWindow() {
        super();
        //Window Properties
        begin = true;
        wind = 0;
        launchAngle = 0;
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
        setResizable(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowEventHandler());    //Dispose and close on Exit
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        buttonBar = new JPanel(new FlowLayout());

        //GUI_Interface: Spinners
        windSpinModel = new SpinnerNumberModel(0, -20, 20, 1);
        windVelSpinner = new JSpinner(windSpinModel);
        windVelSpinner.addChangeListener(new WindListener());
        JLabel windLabel = new JLabel("Wind Speed km/h");
        windLabel.setToolTipText("Positive is to the Right");
        buttonBar.add(windLabel);
        windVelSpinner.setToolTipText("Spinn-a-ding-ding");
        buttonBar.add(windVelSpinner);
        tiltSpinModel = new SpinnerNumberModel(0, -15, 15, 1);
        tiltSpinner = new JSpinner(tiltSpinModel);
        tiltSpinner.addChangeListener(new TiltListener());
        JLabel tiltLabel = new JLabel("     Launch Angle Degrees");
        tiltLabel.setToolTipText("Positive tilts to the right");
        buttonBar.add(tiltLabel);
        tiltSpinner.setToolTipText("Spinn-a-ding-ding");
        buttonBar.add(tiltSpinner);
        wind = windSpinModel.getNumber().intValue();
        launchAngle = tiltSpinModel.getNumber().intValue();
        add(buttonBar, BorderLayout.SOUTH);

        //GUI_Interface: Buttons
        playIcon = Utilities.getScaledImageIcon(PLAY_ICON, this.getClass());
        pauseIcon = Utilities.getScaledImageIcon(PAUSE_ICON, this.getClass());
        stopIcon = Utilities.getScaledImageIcon(STOP_ICON, this.getClass());

        playButton = new JButton();
        playButton.setToolTipText("Play");
        playButton.setIcon(playIcon);
        playButton.addActionListener(new PlayListener());
        buttonBar.add(playButton);

        stopButton = new JButton();
        stopButton.setToolTipText("Stop");
        stopButton.setIcon(stopIcon);
        stopButton.addActionListener(new StopListener());
        stopButton.setToolTipText("Reset Animation");
        buttonBar.add(stopButton);

        JButton exit = new JButton("Exit");
        exit.setToolTipText("Exit Application");
        exit.addActionListener(new ExitListener());
        buttonBar.add(exit);
        imagePanel = new ImagePanel();
        imagePanel.setToolTipText("ToolTips all around for extra marks?");
        add(imagePanel, BorderLayout.CENTER);

        //Setting up Threads
        clock = new Timer(timeInterval, new ClockListener());
        painter = new Timer(timeInterval, new PaintListener());
        running = false;
        reset();    //Stops clock thread and resets Particle Manager and Button statuses
        painter.start();
    }

    //Pauses the animation, allowing it to resume later
    private void pause() {
        begin = false;
        clock.stop();
        playButton.setText("Start");
        playButton.setIcon(playIcon);
        running = false;
    }

    //Resumes the animation
    private void resume() {
        begin = false;
        fireworks = manager.getFireworks(time);
        clock.start();
        playButton.setText(PAUSE_TEXT);
        playButton.setIcon(pauseIcon);
        running = true;
    }

    //Starts the particle manager
    private void start() {
        manager.start(time);
    }

    //Clears all stars on screen and resets the buttons
    private void reset() {
        clock.stop();
        time = 0;
        begin = true;
        running = false;
        playButton.setText(START_TEXT);
        playButton.setIcon(playIcon);
        try {
            manager = new ParticleManager(wind, launchAngle);
        } catch (EnvironmentException | EmitterException except) {
            System.out.println(except.getMessage());
        }
    }

    //Stops all threads and disposes of the graphic and flushes the image before
    //Exiting the program.  Called by the Exit button and the window's closes "X" button
    private void close() {
        if (clock.isRunning())
            clock.stop();
        if (painter.isRunning())
            painter.stop();
        graphic.dispose();
        image.flush();
        System.exit(0);
    }

    /**
     * Used to scale a array or 2 doubles from meters to pixels using the window's width and height
     * The height of the window is always 22m.  Returned is the position in pixels
     *
     * @param width  width of the display
     * @param height height of the display
     * @param pos    position of the visible item
     * @return scaled position
     */
    private Point positionScale(int width, int height, Point2D.Double pos) {
        double convert = height / 22.0;    //px/m
        return new Point((int) (pos.x * convert + width / 2.0), (int) (height - pos.y * convert));
    }

    /**
     * Draw a star as an circle based on its size and color.  Uses positionScale to calculate
     * the position in pixels, as well as having an offset for the star to line it up with the launch tube's
     * tip (endX) upon launch
     *
     * @param posX starting x
     * @param posY starting y
     * @param endX ending x
     * @param star object to draw
     */
    private void drawStar(int posX, int posY, int endX, Particle star) {
        int size = star.getRenderSize();
        graphic.setColor(translateColor(star.getColor()));
        graphic.fillOval(posX - size / 2 + endX, posY - size / 2, size, size);
    }

    /**
     * Draw the launchTube as a series of 10 lines stacked side by side with a gradient.
     * Multiple lines are used as it is easier to draw a line on a slant than a rectangle.
     * Returns the tip of the launch tube's x position in pixels.
     *
     * @param width  width of the display
     * @param height height of the display
     * @return tip of the launch tube's position in pixels
     */
    private int drawLaunchTube(int width, int height) {
        int LAUNCH_TUBE_CONVERT = 22;
        int size = imagePanel.getHeight() / LAUNCH_TUBE_CONVERT;
        int posY = (int) (size * Math.cos(Math.toRadians(launchAngle)));
        int posX = (int) (size * Math.sin(Math.toRadians(launchAngle)));
        graphic.setPaint(new GradientPaint(0, height - size, Color.BLUE, 0, height, Color.GREEN));
        for (int i = 0; i < 10; i++) {
            graphic.drawLine(width / 2 - 5 + i, height, width / 2 + posX - 5 + i, height - posY);
        }
        return posX;
    }

    //Draws a launch spark as a line according to it's color
    private void drawLaunchSpark(int posX, int posY, Particle spark) {
        int size = spark.getRenderSize();
        graphic.setColor(translateColor(spark.getColor()));
        graphic.drawLine(posX, posY, posX + size, posY + size);
    }

//    private Color translateColor(String color, int alpha) {
//        Color result = translateColor(color);
//        return new Color(result.getRed()
//                , result.getGreen()
//                , result.getBlue()
//                , 255);
//    }

    //Translates a string containing a color to its appropriate color object
    private Color translateColor(String color) {
        Color returnColor;
        switch (color.toLowerCase()) {
            case "blue":
                returnColor = Color.BLUE;
                break;
            case "green":
                returnColor = Color.GREEN;
                break;
            case "orange":
                returnColor = Color.ORANGE;
                break;
            case "red":
                returnColor = Color.RED;
                break;
            case "yellow":
                returnColor = Color.YELLOW;
                break;
            case "white":
                returnColor = Color.WHITE;
                break;
            case "cyan":
                returnColor = Color.CYAN;
                break;
            case "magenta":
                returnColor = Color.MAGENTA;
                break;
            default:
                returnColor = Color.BLACK;
        }
        return returnColor;
    }

    //Used by the exit button to launch the close method which exits the program while stopping all threads
    private class ExitListener implements ActionListener {
        public ExitListener() {
            super();
        }

        public void actionPerformed(ActionEvent e) {
            close();
        }
    }

    //Starts, resumes or pauses the animation depending on what stage the animation is at:
    //(Running, Pauses or reset)
    private class PlayListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (begin)
                start();
            if (!running)
                resume();
            else
                pause();
        }
    }

    //Stops and resets the animation
    private class StopListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            reset();
            fireworks = new ArrayList<>();
        }
    }

    //Closes Window properly (stopping threads) upon clicking window's exit button "X"
    private class WindowEventHandler extends WindowAdapter {
        public void windowClosing(WindowEvent evt) {
            close();
        }
    }

    /**
     * Thread that updates the fireworks array once per frame and advances
     * the time by the same amount. If the manager is completed firing all
     * 8 fireworks, reset the animation
     */
    private class ClockListener implements ActionListener {
        public ClockListener() {
            super();
        }

        public void actionPerformed(ActionEvent e) {
            if (!manager.checkIfFinished()) {
                fireworks = manager.getFireworks(time);
                time += timeInterval / 1000.0;
            } else {
                reset();
            }
        }
    }

    /**
     * Paints the animation every frame, layer by layer.
     * <p>1) Background
     * <p>2) launch tube
     * <p>3) particles
     */
    private class PaintListener implements ActionListener {
        final int width = Math.max(imagePanel.getWidth(), 800);
        final int height = Math.max(imagePanel.getHeight(), 600);
        ImageIcon background = new ImageIcon(
                Utilities.getBufferedImageFromRelativePathToClass(BACKGROUND_IMAGE, this.getClass()));

        public PaintListener() {
            super();
        }

        public void actionPerformed(ActionEvent e) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            graphic = image.createGraphics();
            graphic.drawImage(background.getImage(), 0, 0, width, height, null);    //Draw background
            int endX = drawLaunchTube(width, height);        //Draw LaunchTube
            if (fireworks != null && fireworks.size() != 0) {
                for (Particle firework : fireworks) {
                    Point pixelPos = positionScale(width, height, firework.getPosition());
                    if (firework instanceof LaunchSpark) {
                        drawLaunchSpark(pixelPos.x, pixelPos.y, firework);
                    } else {
                        drawStar(pixelPos.x, pixelPos.y, endX, firework);
                    }
                }
            }
            imagePanel.repaint();
            buttonBar.repaint();
        }
    }

    //Used by the WindVel spinner to update the managers wind velocity upon value change
    private class WindListener implements ChangeListener {
        public WindListener() {
            super();
        }

        public void stateChanged(ChangeEvent e) {
            wind = windSpinModel.getNumber().intValue();
            if (manager != null) {
                try {
                    manager.setWindVelocity(wind);

                } catch (EnvironmentException ignored) {
                }
                buttonBar.repaint();
            }
        }
    }

    //Used by the tilt spinner to update the managers firing angle upon value change
    private class TiltListener implements ChangeListener {
        public TiltListener() {
            super();
        }

        public void stateChanged(ChangeEvent e) {
            launchAngle = tiltSpinModel.getNumber().intValue();
            if (manager != null) {
                try {
                    manager.setFiringAngle(launchAngle);
                } catch (EmitterException ignored) {
                }
                buttonBar.repaint();
            }
        }
    }

    //Panel that is drawn to and used to create to create the animation.
    //The overrided paint function allows for the drawing of the image created
    //by the painter thread.
    private class ImagePanel extends JPanel {
        public ImagePanel() {
            super();
        }

        @Override
        public void paint(Graphics g) {
            g.clearRect(0, 0, getWidth(), getHeight());
            g.drawImage(image, 0, 0, null);
        }
    }

}