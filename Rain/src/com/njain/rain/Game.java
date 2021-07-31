package com.njain.rain;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.njain.rain.graphics.Screen;
import com.njain.rain.input.Keyboard;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public static int width = 300;
	public static int height = (width / 16 ) * 9; //16:9 aspect ratio 
	public static int scale = 3; 
		
	Thread thread;
	private JFrame frame;
	private boolean running = false;
	private Screen screen;
	
	//Creating an image
	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	
	//Access that image using this 1-d array
	private int pixels[] = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	
	private Keyboard key;
	private int xOffset = 0, yOffset = 0;

	public Game() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size); //set the size of canvas
		screen = new Screen(width, height);
		frame = new JFrame();
		key = new Keyboard();
		addKeyListener(key);
	}
	
	public synchronized void start() { //not the threasd.start() method
		System.out.println("Starting!");
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}
	
	public synchronized void stop() {
		System.out.println("Stopping!");
		running = false;
		try {
		 	thread.join();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	}	
	
	
	public void run() {
		long lastTime = System.nanoTime();
		double timeForEachUpdate = 1000000000.0 / 60.0; 
		double delta = 0.0; // times updates need to be called
		
		long timer = System.currentTimeMillis();
		int frames = 0, updates = 0;
		
		while (running) { //game loop	
			long now = System.nanoTime();
			delta += (now - lastTime) / timeForEachUpdate;
			lastTime = now; //taken updates till 'now' into account
			while (delta >= 1.0) {
				update();
				delta--;
				updates++;
			}
			render();
			frames++;
			if (System.currentTimeMillis() - timer >= 1000) {
				//one second has passed
				timer += 1000;
				frame.setTitle("Rain | " + updates + "ups, " + frames + "fps");
				updates = 0;
				frames = 0;
			}
		}
		stop();
	}
	
	public void update() {
		key.keyUpdate();
		if (key.up) yOffset--;
		if (key.down) yOffset++;
		if (key.left) xOffset--;
		if (key.right) xOffset++; 
		
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		//clear previous frame
		screen.clear();
		
		//render based on game state
		screen.render(xOffset, yOffset);
		
		//set the image 
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
	
	public static void main(String arg[]) throws Exception{
		Game game = new Game();
		game.frame.setResizable(false); //avoid graphics errors
		game.frame.setTitle("Rain");
		game.frame.add(game);
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);
		game.start();
	}
}
