package actions;

import entities.Agent;

import world.World;

public interface Action {	
	/**
	 * Execute one step of the action
	 * 
	 * @param agent the agent executing the action
	 * @param world the world which contains all information necessary for
	 * an action to be executed
	 */
	public void execute(Agent agent, World world);
	
	public boolean isFinished();
	
	public boolean requestInterrupt();
	
	public boolean isInterruptable();
}