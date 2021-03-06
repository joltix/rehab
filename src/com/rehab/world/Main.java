package com.rehab.world;


import java.util.LinkedList;

import com.rehab.animation.Drawable;
import com.rehab.animation.Sprite;
import com.rehab.user.Mouse;
import com.rehab.user.Movement;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Actor mDummy;

	public static void main(String[] args) {
		Application.launch(args);
	}
	
	public static Actor getDummy() {
		return mDummy;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Prep and display the window
		Canvas canvas = setupWindow(primaryStage);

		// The test components
		Actor player = initPlayer();
		Actor dummy = initDummy();
		Prop floor = initFloor();
		Arena lvl = initLevel(floor, player);

		// Setup keyboard control
		Movement move = new Movement(canvas, player);
		LinkedList<Drawable> drawables = new LinkedList<Drawable>();

		// Load all Actors
		for (Entity e : InstanceManager.getInstance().getLoadedActors()) {
			drawables.add(e);
		}

		// Setup mouse control
		Mouse mouse = new Mouse(canvas, drawables, player);

		// The logic and draw loops
		WorldLoop world = WorldLoop.getInstance(40, lvl);
		//RenderLoop render = RenderLoop.getInstance(50, canvas);
		ProtoRender render = ProtoRender.getInstance(60, canvas);


		
		// Prep game objs for drawing then begin
		render.loadLayers();
		render.setPriority(Thread.MAX_PRIORITY);
		render.start();

		// Begin game world
		world.setPriority(Thread.MAX_PRIORITY);
		world.start();
	}

	/**
	 * Sets up the game Window and displays it for visualizing the game's
	 * state.
	 * @param primary
	 * 		the Stage given by JavaFX
	 * @return
	 * 		the Canvas needed for drawing.
	 */
	private Canvas setupWindow(Stage primary) {
		Canvas canvas = new Canvas(720, 480);
		Group group = new Group();
		group.getChildren().add(canvas);
		primary.setScene(new Scene(group, 720, 480, Color.DARKBLUE));
		primary.show();
		return canvas;
	}

	/**
	 * Sets up the test Arena.
	 * 
	 * @param floor	the level's floor to fight on.
	 * @param player	the Actor representing the player.
	 * @return	the Arena.
	 */
	private Arena initLevel(Prop floor, Actor player) {
		Arena arena = new Arena("BASIC-TEST", 720, 480, floor);
		arena.setPlayer(player);

		InstanceManager instaMan = InstanceManager.getInstance();
		arena.setEntities(instaMan.getLoadedActors(), instaMan.getLoadedProjectiles(), instaMan.getLoadedProps());
		return arena;
	}

	/**
	 * Sets up the floor object at the bottom of the screen.
	 * 
	 * @return the floor object.
	 */
	private Prop initFloor() {
		// (Foregoing Obstacle for reval)
		// Initialize a non moving floor
		InstanceManager instMan = InstanceManager.getInstance();
		Prop p = instMan.createProp(new Sprite("bar.png"), 400, 32);

		//Move to proper position at bottom
		p.moveTo(0, 32);

		// Add to game world
		instMan.load(p);
		return p;
	}

	/**
	 * Sets up the player object high at the top of the screen.
	 * 
	 * @return the player obj.
	 */
	private Actor initPlayer() {
		// Initialize and setup the falling obj
		InstanceManager instaMan = InstanceManager.getInstance();
		Actor a = instaMan.createActor(62, 100);
		
		a.setCollisionModel(new Hitbox(0, 0, 32, 32));
		a.setEnableGravity(true);
		a.moveTo(0, 480);
		a.setSprite(new Sprite("git_icon.jpg"));
		
		// Setup projectile
		Projectile proj = instaMan.createProjectile(a, new Hitbox(0, 0, 24));
		proj.setSprite(new Sprite("git_icon.jpg"));
		
		// Setup weapon
		a.arm(1, 5, proj);
		
		// Make object known to manager
		instaMan.load(a);
		return a;
	}

	/**
	 * Sets up the dummy object high at the top of the screen.
	 * 
	 * @return the dummy obj.
	 */
	private Actor initDummy() {
		InstanceManager instaMan = InstanceManager.getInstance();
		mDummy = instaMan.createActor(62, 100);
		mDummy.setCollisionModel(new Hitbox(0, 0, 32, 32));
		mDummy.moveTo(360, 480);
		mDummy.setSprite(new Sprite("git_icon.jpg"));
		mDummy.setEnableGravity(true);

		Actor clone = instaMan.createActor(mDummy);
		clone.moveTo(128,  480);
		instaMan.load(clone);
		
		instaMan.load(mDummy);
		System.out.println("Dummy id: " + mDummy.getId());
		return mDummy;
	}

}
