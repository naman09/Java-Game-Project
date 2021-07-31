package com.njain.rain.graphics;

import java.util.Random;

public class Screen {
	private int width, height;
	public int pixels[];
	
	private final int rowTilesCnt = 19, colTilesCnt = 11;
	private int tw, th; //tile width and height
	public int tiles[] = new int[rowTilesCnt * colTilesCnt];
	private Random random = new Random();
	
	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		
		for (int i = 0; i < rowTilesCnt * colTilesCnt; i++) {
			tiles[i] = random.nextInt(0xffffff);
		}
		tw = (width + rowTilesCnt - 1)/ rowTilesCnt;
 		th = (height + colTilesCnt - 1)/ colTilesCnt;
	}
	
	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}
	}
	
	public void render(int xOffset, int yOffset) {
		int xx = 0, yy = 0;
		for (int y = 0; y < height; y++) {
			yy = y + yOffset;
			yy /= th;
			for (int x = 0; x < width; x++) {
				xx = x + xOffset;
				xx /= tw;
				int tileIndex = (xx & 18) 
						+ ((yy & 10) * rowTilesCnt);
 				pixels[x + y * width] = tiles[tileIndex];
			}
		}
	}

}
