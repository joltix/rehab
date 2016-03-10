package com.rehab.world;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.rehab.animation.Drawable;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class RenderLoop extends Thread {

	// Layers to sort game objects
	private LayerManager mLayers = new LayerManager(3);

	// Drawing surface and components
	private Canvas mCanvas;
	private GraphicsContext mGfx;
	private WritableImage mBuffer1 = new WritableImage(720, 480),
							mBuffer2 = new WritableImage(720, 480),
							mSelBuffer = mBuffer1;
	
	// Singleton instance
	private static RenderLoop mInstance;
	
	// Loop control
	private boolean mLoop = true;
	private long mTarRate, mInterval;
	
	// Drawing buffer for dynamically created game objects
	private ConcurrentLinkedQueue<Drawable> mAddToDraw = new ConcurrentLinkedQueue<Drawable>();
	
	/**
	 * Gets an instance of the RenderLoop. This method should only be used if getInstance(int, Canvas)
	 * was ever called at least once. Calling this method first will throw an IllegalStateException.
	 * @throws IllegalStateException
	 * 		if getInstance(int, Canvas) has never been called.
	 */
	public static RenderLoop getInstance() {
		synchronized (RenderLoop.class) {
			if (mInstance == null) throw new IllegalStateException("getInstance(int, Canvas) has never been called");
			return mInstance;
		}
	}
	
	/**
	 * Gets an instance of the RenderLoop.
	 * @param fps
	 * 		the desired frames per second.
	 * @param arena
	 * 		the Canvas to draw on.
	 */
	public static RenderLoop getInstance(int fps, Canvas c) {
		synchronized (RenderLoop.class) {
			if (mInstance == null) mInstance = new RenderLoop(fps, c);
			return mInstance;
		}
	}
	
	/**
	 * Checks whether or not the RenderLoop has begun.
	 * @return
	 * 		true if the RenderLoop has begun, false otherwise.
	 */
	public static boolean isRunning() {
		if (mInstance == null) return false;
		return mInstance.isAlive();
	}
	
	/**
	 * Constructor for specifying a framerate and a drawing surface.
	 * @param fps
	 * 		the desired frames per second.
	 * @param c
	 * 		the Canvas to draw on.
	 */
	private RenderLoop(int fps, Canvas c) {
		mCanvas = c;
		mGfx = mCanvas.getGraphicsContext2D();
		// Loop time control
		mTarRate = fps;
		mInterval = 1000000000 / mTarRate;
	}
	
	/**
	 * Add all loaded Entities from the InstanceManager for drawing.
	 */
	public void reloadLayers() {
		Iterable<Entity> ents = InstanceManager.getInstance().getLoadedEntities();
		for (Entity e : ents)
			mLayers.add(e);
	}
	
	/**
	 * Adds a Drawable into the RenderLoop's drawing layers. The given Drawable will be
	 * sent to a buffer to be drawn in the next draw frame.
	 * 
	 * This method is meant to be
	 * called when a Drawable is created after RenderLoop has begun drawing and its
	 * reloadLayers() has already been called.
	 * @param drawable
	 * 		the new Drawable to render.
	 */
	public void addDrawable(Drawable drawable) { mAddToDraw.add(drawable); }
	
	@Override
	public void run() {
		super.run();
		
		while (mLoop) {
			long frameStart = System.nanoTime();
			
			// Swap buffers to work on
			if (mSelBuffer == mBuffer1) mSelBuffer = mBuffer2;
			else mSelBuffer = mBuffer1;
			
			PixelWriter writer = mSelBuffer.getPixelWriter();
			// Clear the buffer from previous display
			clearBuffer(writer);
			
			
			// Draw every object needed
			Iterable<Drawable> layer = mLayers.getLayer(0);
			for (Drawable obj : layer)
				drawToBuffer(obj, writer);
			
			// Add new Drawables for next draw loop
			for (Drawable toAdd : mAddToDraw)
				mLayers.add(toAdd);
			
			
			// Set the buffer to be displayed onscreen
			mGfx.drawImage(mSelBuffer, 0, 0);
			
			// Skip next frame if previous took too long
			long frameDur = System.nanoTime() - frameStart;
			if (frameDur > mInterval) {
				try {
					Thread.sleep((frameDur - mInterval) / 1000000);
				} catch (InterruptedException e) { e.printStackTrace(); }
			} else if (frameDur < mInterval) {
				try {
					Thread.sleep((mInterval - frameDur) / 1000000);
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		
		}
		
	}
	
	/**
	 * Erases the currently selected buffer using a dark grey color.
	 * @param writer
	 * 		the PixelWriter to draw on.
	 */
	private void clearBuffer(PixelWriter writer) {
		// Color in ARGB int format (dark grey)
		int blank = 255;
		blank = (blank << 8) + 40;
		blank = (blank << 8) + 40;
		blank = (blank << 8) + 40;
		
		// Paint over buffer to erase previous img
		for (int x = 0, w = (int) mSelBuffer.getWidth(); x < w; x++)
			for (int y = 0, h = (int) mSelBuffer.getHeight(); y < h; y++)
				writer.setArgb(x, y, blank);

	}
	
	/**
	 * Draws a Drawable object to the currently selected buffer.
	 * @param obj
	 * 		the Drawable to draw onto the buffer.
	 * @param writer
	 * 		the PixelWriter of the buffer.
	 */
	private void drawToBuffer(Drawable obj, PixelWriter writer) {
		
		// Get location offset relative to overall screen
		int offX = (int) obj.getX();
		int offY = (int) (480 - obj.getY());
		
		obj.getSprite().draw(writer, offX, offY);
	}

	
	
}
