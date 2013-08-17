package entities;

import java.io.IOException;

import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import actions.Jump;
import actions.Step;
import actions.Turn;

import world.World;

import static entities.Agent.direction.*;

public class Hero extends Agent {
	int row;
	int column;
	
	//Actions
	Step step;
	Turn turn;
	Jump jump;
	
	/**
	 * Constructor
	 */
	public Hero()
	{
		super();
	}
	
	@Override
	protected void setActions()
	{
		super.setActions();
		step = new Step();
		turn = new Turn();
		jump = new Jump();
	}
	
	@Override
	public void initState()
	{
		super.initState();
		setDir(down);
		setSpeed(2);
		setFootstep(left);
		setHeight(2);
	}
	
	@Override
	public void decideNextAction(World world) 
	{
		if (currentAction.isFinished())
		{
			currentAction = idle;
			args.clear();
		}
	}

	@Override
	public void loadTextures() 
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/characters/char1.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Getter for the hero's step action, this is required to allow the keyboard polling to set
	 * the agent's action from outside of the scope of the class
	 * @return the agent's step action
	 */
	public Step getStepAction()
	{
		return step;
	}
	
	/**
	 * Getter for the hero's turn action, this is required to allow the keyboard polling to set
	 * the agent's action from outside of the scope of the class
	 * @return the agent's turn action
	 */
	public Turn getTurnAction()
	{
		return turn;
	}
	
	/**
	 * Getter for the hero's jump action, this is required to allow the keyboard polling to set
	 * the agent's action from outside of the scope of the class
	 * @return the agent's jump action
	 */
	public Jump getJumpAction()
	{
		return jump;
	}
	
	/**
	 * Getter for whether or not the hero is idle, this is required to allow the keyboard polling to determine
	 * whether it should change the agent's action from outside the scope of the class
	 * @return true if the agent's current action is idle
	 */
	public boolean isIdle()
	{
		if (currentAction == idle)
			return true;
		else
			return false;
	}
}
