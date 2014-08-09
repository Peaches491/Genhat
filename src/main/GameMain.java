package main;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import entities.Agent;
import entities.Hero;
import entities.Wanderer;

import things.HorizontalBar;
import things.LightSource;
import things.Stairs;
import things.StairsIndoor;
import things.StairsIndoorBottom;
import things.VerticalBar;
import world.Terrain;
import world.World;

import static world.Terrain.terrainType.*;
import static entities.Agent.direction.*;
import static world.World.timeOfDay.*;


public class GameMain {
	
	World world;
	
	int arrowKeyInputCount = 0;
	
	/**
	 * Main game loop, this is where everything happens
	 */
	public void gameLoop()
	{		
		//Initialization
		initGL();
		initWorld();
		loadTextures();
		
		//Main game loop
		while(!Display.isCloseRequested())	//exits when window is closed
		{
			//update everything
			world.updateAgents();
			world.updateCameraScrollLock();
			world.updateCamera();
			
			//render everything
			renderGL();
			
			//get user input
			pollKeyboardInput();
			
			//update the screen
			Display.update();
			Display.sync(32);			
		}
		
		//Exit
		Display.destroy();
	}
	
	/**
	 * Poll for input on the keyboard
	 */
	public void pollKeyboardInput()
	{
		//Check for key state changes
		while (Keyboard.next())
		{
			//Debugging stuff
			if (Keyboard.getEventKeyState())
			{
				/*
				if (Keyboard.getEventKey() == Keyboard.KEY_Q)
				{
					world.rotateCC();
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_E)
				{
					world.rotateC();
				}
				*/
				if (Keyboard.getEventKey() == Keyboard.KEY_T)
				{
					world.cycleTimeOfDay();
				}
				
				//Pressed Action Keys
				if (Keyboard.getEventKey() == Keyboard.KEY_Z)
				{
					
				}
				else if (Keyboard.getEventKey() == Keyboard.KEY_X)
				{
					Hero player = world.getPlayer();
					if (player != null)
					{
						if (player.isIdle())
						{
							ArrayList<String> newArgs = new ArrayList<String>();
							player.setArgs(newArgs);
							player.setCurrentAction(player.getJumpAction());
						}
					}
				}
			}
		}
		
		//Held Action Keys	
		if (Keyboard.isKeyDown(Keyboard.KEY_C))
		{
			Hero player = world.getPlayer();
			if (player != null && player.isIdle())
				player.setSpeed(4);
		}
		else
		{
			Hero player = world.getPlayer();
			if (player != null && player.isIdle())
				player.setSpeed(2);
		}
		
		//Arrow Keys
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
		{
			Hero player = world.getPlayer();
			if (player != null)
			{
				arrowKeyInputCount ++;
				if (player.isIdle())
				{
					ArrayList<String> newArgs = new ArrayList<String>();
					newArgs.add("down");
					player.setArgs(newArgs);
					if (arrowKeyInputCount < 3)
						player.setCurrentAction(player.getTurnAction());
					else
					{
						player.setCurrentAction(player.getStepAction());
						//camera scroll lock check
						if (!world.isCameraLockV())
						{
							//TODO: conditions for vertical camera lock
						}
					}
				}
			}
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_UP))
		{
			Hero player = world.getPlayer();
			if (player != null)
			{
				arrowKeyInputCount ++;
				if (player.isIdle())
				{
					ArrayList<String> newArgs = new ArrayList<String>();
					newArgs.add("up");
					player.setArgs(newArgs);
					if (arrowKeyInputCount < 3)
						player.setCurrentAction(player.getTurnAction());
					else
					{
						player.setCurrentAction(player.getStepAction());
						//camera scroll lock check
						if (!world.isCameraLockV())
						{
							//TODO: conditions for vertical camera lock
						}
					}
				}
			}
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		{
			Hero player = world.getPlayer();
			if (player != null)
			{
				arrowKeyInputCount ++;
				if (player.isIdle())
				{
					ArrayList<String> newArgs = new ArrayList<String>();
					newArgs.add("right");
					player.setArgs(newArgs);
					if (arrowKeyInputCount < 3)
						player.setCurrentAction(player.getTurnAction());
					else
					{
						player.setCurrentAction(player.getStepAction());
						//camera scroll lock check
						if (!world.isCameraLockH())
						{
							//if (player.getPos()[0] >= world.getWidth() - 8)
								//world.setCameraLockH(true);
						}
					}
				}
			}
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
		{
			Hero player = world.getPlayer();
			if (player != null)
			{
				arrowKeyInputCount ++;
				if (player.isIdle())
				{
					ArrayList<String> newArgs = new ArrayList<String>();
					newArgs.add("left");
					player.setArgs(newArgs);
					if (arrowKeyInputCount < 3)
						player.setCurrentAction(player.getTurnAction());
					else
						player.setCurrentAction(player.getStepAction());
				}
			}
		}
		else
		{
			arrowKeyInputCount = 0;
		}
	}
	
	/**
	 * Initialize the game window and all OpenGL-related setup
	 */
	public void initGL()
	{
		//Create game window
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		GL11.glClearColor(0.3f, 0.7f, 1.0f, 1.0f);
		
		//Allow transparent colors in textures
		GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    	
    	//Setup 800 by 600 window
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 0, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		//Disable 3D effects
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
	}
	
	/**
	 * Initialize the world
	 */
	public void initWorld()
	{
		//genTestWorldHeroTest();
		//genTestWorldStairs();
		//genTestWorldJump();
		genLargeWorld();
	}
	
	/**
	 * Load all textures
	 */
	public void loadTextures()
	{
		world.loadTextures();
	}
	
	/**
	 * Render any graphics (currently just the hero)
	 */
	public void renderGL()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		GL11.glPushMatrix();
			world.renderWorld();
		GL11.glPopMatrix();
	}
	
	/**
	 * Main class, simply constructs and runs the game
	 * 
	 * @param args the usual...
	 */
	public static void main(String[] args)
	{
		GameMain gameMain = new GameMain();
		gameMain.gameLoop();
	}
	
	
	/*#################################################################################
	 * Test Worlds
	 *#################################################################################*/
	private void genTestWorld0()
	{
		int xs = 10, ys = 10, zs = 10;
		Terrain[][][] t = new Terrain[xs][ys][zs];
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				for (int k = 0; k < zs; k ++)
				{
					t[i][j][k] = new Terrain(air);
				}
			}
		}
		
		t[xs/2][ys/2][zs/2] = new Terrain(grass);
		
		world = new World(xs, ys, zs);
		world.setTerrain(t);
	}
	
	private void genTestWorld1()
	{
		int xs = 10, ys = 10, zs = 10;
		Terrain[][][] t = new Terrain[xs][ys][zs];
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				t[i][j][0] = new Terrain(rock);
				t[i][j][1] = new Terrain(grass);
			}
		}
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				for (int k = 2; k < zs; k ++)
				{
					t[i][j][k] = new Terrain(air);
				}
			}
		}
		
		t[2][2][1] = new Terrain(rock);
		t[3][2][1] = new Terrain(rock);
		t[4][2][1] = new Terrain(rock);
		t[2][3][1] = new Terrain(rock);
		t[3][3][1] = new Terrain(rock);
		t[4][3][1] = new Terrain(rock);
		t[3][4][1] = new Terrain(rock);
		t[4][4][1] = new Terrain(rock);
		t[3][4][2] = new Terrain(rock);
		t[4][4][2] = new Terrain(rock);
		
		t[2][2][2] = new Terrain(grass);
		t[3][2][2] = new Terrain(grass);
		t[4][2][2] = new Terrain(grass);
		t[2][3][2] = new Terrain(grass);
		t[3][3][2] = new Terrain(grass);
		t[4][3][2] = new Terrain(grass);
		
		t[3][4][3] = new Terrain(dirt);
		t[4][4][3] = new Terrain(dirt);
		
		t[7][3][1] = new Terrain(rock);
		t[7][3][2] = new Terrain(rock);
		
		t[7][3][3] = new Terrain(dirt, grass);
		
		t[6][8][1] = new Terrain(rock);
		t[6][8][2] = new Terrain(rock);
		t[7][8][1] = new Terrain(rock);
		t[7][8][2] = new Terrain(rock);
		t[8][8][1] = new Terrain(rock);
		t[8][8][2] = new Terrain(rock);
		t[7][8][3] = new Terrain(grass);
		t[8][8][3] = new Terrain(grass);
		t[6][8][3] = new Terrain(rock);
		t[6][8][4] = new Terrain(rock);
		t[6][8][5] = new Terrain(grass);
		
		t[0][9][1] = new Terrain(dirt);
		t[0][8][1] = new Terrain(dirt);
		t[0][7][1] = new Terrain(dirt);
		t[0][6][1] = new Terrain(dirt);
		t[1][9][1] = new Terrain(dirt);
		t[1][8][1] = new Terrain(air);
		t[1][7][1] = new Terrain(air);
		
		world = new World(xs, ys, zs);
		world.setTerrain(t);
	}
	
	private void genTestWorldHeroTest()
	{
		genTestWorld1();
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		Hero hero = new Hero();
		int[] pos = {1, 1, 2};
		world.setDisplayCenter(pos);
		hero.setPos(pos);
		agents.add(hero);
		
		int[] wPos = {8, 6, 2};
		Wanderer wanderer = new Wanderer(wPos, 32, 2);
		wanderer.setPos(wPos);
		agents.add(wanderer);
		
		world.addAgents(agents);
		world.setPlayer(hero);
	}
	
	private void genTestWorldStairs()
	{
		int xs = 6, ys = 6, zs = 6;
		Terrain[][][] t = new Terrain[xs][ys][zs];
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				for (int k = 0; k < zs; k ++)
				{
					t[i][j][k] = new Terrain(air);
				}
			}
		}
		
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				t[i][j][0] = new Terrain(grass);
			}
		}
		
		t[2][2][1] = new Terrain(grass);
		t[2][3][1] = new Terrain(grass);
		t[3][2][1] = new Terrain(grass);
		t[3][3][1] = new Terrain(grass);
		t[2][2][2] = new Terrain(dirt);
		t[2][3][2] = new Terrain(dirt);
		t[1][2][1] = new Terrain(grass);
		t[0][2][1] = new Terrain(grass);
		t[0][3][1] = new Terrain(grass);
		t[0][3][2] = new Terrain(dirt);
		t[1][3][1] = new Terrain(grass);
		t[1][3][2] = new Terrain(dirt);
		t[0][4][1] = new Terrain(grass);
		t[0][4][2] = new Terrain(dirt);
		t[1][4][1] = new Terrain(grass);
		t[1][4][2] = new Terrain(dirt);
		t[5][5][1] = new Terrain(grass);
		//t[4][1][1] = new Terrain(grass);
		//t[3][0][1] = new Terrain(grass);
		//t[4][0][1] = new Terrain(grass);
		
		
		Stairs s1 = new Stairs();
		Stairs s2 = new Stairs();
		Stairs s3 = new Stairs(right);
		Stairs s4 = new Stairs(right);
		Stairs s5 = new Stairs(right);
		Stairs s6 = new Stairs(left);
		Stairs s7 = new Stairs();
		Stairs s8 = new Stairs();
		Stairs s9 = new Stairs(left);
		Stairs s10 = new Stairs(left);
		
		world = new World(xs, ys, zs);
		world.setTerrain(t);
		world.addThing(s1, 3, 1, 1);
		world.addThing(s2, 2, 1, 1);
		world.addThing(s3, 4, 2, 1);
		world.addThing(s4, 4, 3, 1);
		world.addThing(s5, 3, 3, 2);
		world.addThing(s6, 1, 2, 2);
		world.addThing(s7, 0, 1, 1);
		world.addThing(s8, 0, 2, 2);
		world.addThing(s9, 4, 5, 1);
		world.addThing(s10, 5, 5, 2);
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		Hero hero = new Hero();
		int[] pos = {5, 1, 1};
		world.setDisplayCenter(pos);
		hero.setPos(pos);
		agents.add(hero);
		
		world.addAgents(agents);
		world.setPlayer(hero);
	}
	
	private void genTestWorldJump()
	{
		int xs = 6, ys = 6, zs = 8;
		Terrain[][][] t = new Terrain[xs][ys][zs];
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				for (int k = 0; k < zs; k ++)
				{
					t[i][j][k] = new Terrain(air);
				}
			}
		}
		
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				t[i][j][0] = new Terrain(grass);
			}
		}
		
		t[2][2][1] = new Terrain(grass);
		t[2][3][1] = new Terrain(grass);
		t[3][2][1] = new Terrain(grass);
		t[3][3][1] = new Terrain(grass);
		t[2][2][2] = new Terrain(grass);
		t[2][3][2] = new Terrain(grass);
		t[1][2][1] = new Terrain(grass);
		t[0][2][1] = new Terrain(grass);
		t[0][3][1] = new Terrain(grass);
		t[0][3][2] = new Terrain(grass);
		t[1][3][1] = new Terrain(grass);
		t[1][3][2] = new Terrain(grass);
		t[0][4][1] = new Terrain(grass);
		t[0][4][2] = new Terrain(grass);
		t[1][4][1] = new Terrain(grass);
		t[1][4][2] = new Terrain(grass);
		t[5][5][1] = new Terrain(grass);
		
		world = new World(xs, ys, zs);
		world.setTerrain(t);
		
		world.setTod(sunrise);
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		Hero hero = new Hero();
		int[] pos = {4, 1, 1};
		world.setDisplayCenter(pos);
		hero.setPos(pos);
		agents.add(hero);
		
		world.addAgents(agents);
		world.setPlayer(hero);
	}
	
	private void genLargeWorld()
	{
		int xs = 50, ys = 50, zs = 10;
		
		world = new World(xs, ys, zs);
		
		Terrain[][][] t = new Terrain[xs][ys][zs];
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				for (int k = 0; k < zs; k ++)
				{
					t[i][j][k] = new Terrain(air);
				}
			}
		}
		
		Random rand = new Random();
		for (int i = 0; i < xs; i ++)
		{
			for (int j = 0; j < ys; j ++)
			{
				if (rand.nextInt(2) == 0)
					t[i][j][0] = new Terrain(grass);
				else
					t[i][j][0] = new Terrain(dirt);
			}
		}
		
		/*
		//screen edge tests top
		for (int n = 40; n < 50; n ++)
		{
			for (int i = 1; i < 10; i ++)
			{
				for (int j = 0; j < i; j ++)
				{
					t[20+i][n][j] = new Terrain(grass, dirt);
				}
			}
		}
		//screen edge tests bottom
		for (int n = 0; n < 15; n ++)
		{
			for (int i = 1; i < 10; i ++)
			{
				for (int j = 0; j < i; j ++)
				{
					t[20+i][n][j] = new Terrain(grass, dirt);
				}
			}
		}
		*/
		
		//house test
		//floor
		for (int i = 25; i < 45; i ++)
		{
			for (int j = 30; j < 38; j ++)
			{
				t[i][j][0] = new Terrain(dirt, woodPlank);
			}
		}
		//walls
		for (int i = 25; i < 45; i ++)
		{
			for (int k = 0; k < 4; k ++)
			{
				t[i][37][k] = new Terrain(rock);
				if (i < 34 || i > 35)
					t[i][30][k] = new Terrain(rock);
			}
		}
		for (int j = 30; j < 38; j ++)
		{
			for (int k = 1; k < 4; k ++)
			{
				t[25][j][k] = new Terrain(rock);
				t[44][j][k] = new Terrain(rock);
			}
		}
		
		//windows
		t[28][30][2] = new Terrain(glass, air);
		t[29][30][2] = new Terrain(glass, air);
		t[30][30][2] = new Terrain(glass, air);
		
		//house objects
		HorizontalBar b1 = new HorizontalBar(left);
		world.addThing(b1, 27, 35, 1);
		for (int i = 0; i < 3; i ++)
		{
			HorizontalBar bar = new HorizontalBar(down);
			world.addThing(bar, 28+i, 35, 1);
		}
		HorizontalBar b2 = new HorizontalBar(right);
		world.addThing(b2, 31, 35, 1);
		HorizontalBar b3 = new HorizontalBar(left);
		world.addThing(b3, 33, 35, 1);
		HorizontalBar b4 = new HorizontalBar(right);
		world.addThing(b4, 34, 35, 1);
		VerticalBar b5 = new VerticalBar(up);
		world.addThing(b5, 27, 36, 1);
		VerticalBar b6 = new VerticalBar(up);
		world.addThing(b6, 34, 36, 1);
		
		StairsIndoor s1 = new StairsIndoor(right, down);
		StairsIndoor s2 = new StairsIndoor(right, left);
		StairsIndoor s3 = new StairsIndoor(right, up);
		StairsIndoorBottom sb1 = new StairsIndoorBottom(right);
		StairsIndoorBottom sb2 = new StairsIndoorBottom(right);
		world.addThing(s1, 40, 36, 1);
		world.addThing(s2, 41, 36, 2);
		world.addThing(s3, 42, 36, 3);
		world.addThing(sb1, 41, 36, 1);
		world.addThing(sb2, 42, 36, 2);
		
		LightSource l1 = new LightSource();
		world.addThing(l1, 33, 33, 2);
		
		//shadow tests
		for (int i = 1; i < 8; i ++)
		{
			t[22][28][i] = new Terrain(dirt, grass);
			t[18][25][i] = new Terrain(dirt, grass);
		}
		t[19][25][7] = new Terrain(dirt, grass);
		t[19][24][7] = new Terrain(dirt, grass);
		t[17][26][7] = new Terrain(dirt, grass);
		t[17][25][7] = new Terrain(dirt, grass);
		
		//vertical transparency test
		for (int i = 1; i < 4; i ++)
		{
			for (int j = 13; j <= 18; j ++)
			{
				t[j][13][i] = new Terrain(dirt, grass);
				t[j][14][i] = new Terrain(dirt, grass);
				t[j][15][i] = new Terrain(dirt, grass);
			}
		}
		
		world.setTerrain(t);
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		Hero hero = new Hero();
		int[] pos = {25, 25, 1};
		world.setDisplayCenter(pos);
		hero.setPos(pos);
		agents.add(hero);
		
		world.setTod(sunrise);
		
		world.addAgents(agents);
		world.setPlayer(hero);
	}
}
