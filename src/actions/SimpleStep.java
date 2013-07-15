package actions;

import java.util.ArrayList;

import world.World;
import entities.Agent;
import entities.Agent.direction;

import static entities.Agent.direction.*;

public class SimpleStep implements Action {

	boolean finishedStep = true;
	
	@Override
	public void execute(Agent agent, World world, ArrayList<String> args)
	{		
		//invalid arguments, do nothing
		if (args.size() < 1)
		{
			System.out.println("Invalid arguments to action SimpleStep.");
			System.out.println("SimpleStep must take 1 argument denoting direction, as either: {up, down, left, right}");
			return;
		}
			
		String arg1 = args.get(0);
		
		if (arg1.equals("up"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(up);
				
				if(canStep(agent, world, up))
				{
					world.moveAgent(agent, 0, 1, 0);
					agent.setOffsetY(-16);
					finishedStep = false;
					agent.setStepping(true);
				}
				else
				{
					finishedStep = true;
					return;
				}
			}
			
			agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() >= 0)
			{
				swapFootstep(agent);
				agent.setOffsetY(0);
				agent.setStepping(false);
				finishedStep = true;
			}
		}
		else if (arg1.equals("down"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(down);

				if(canStep(agent, world, down))
				{
					world.moveAgent(agent, 0, -1, 0);
					agent.setOffsetY(16);
					finishedStep = false;
					agent.setStepping(true);
				}
				else
				{
					finishedStep = true;
					return;
				}
			}

			agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() <= 0)
			{
				swapFootstep(agent);
				agent.setOffsetY(0);
				agent.setStepping(false);
				finishedStep = true;
			}
		}
		else if (arg1.equals("left"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(left);

				if(canStep(agent, world, left))
				{
					world.moveAgent(agent, -1, 0, 0);
					agent.setOffsetX(16);
					finishedStep = false;
					agent.setStepping(true);
				}
				else
				{
					finishedStep = true;
					return;
				}
			}
			
			agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() <= 0)
			{
				swapFootstep(agent);
				agent.setOffsetX(0);
				agent.setStepping(false);
				finishedStep = true;
			}
		}
		else if (arg1.equals("right"))
		{
			if (!agent.isStepping())
			{
				agent.setDir(right);

				if(canStep(agent, world, right))
				{
					world.moveAgent(agent, 1, 0, 0);
					agent.setOffsetX(-16);
					finishedStep = false;
					agent.setStepping(true);
				}
				else
				{
					finishedStep = true;
					return;
				}
			}
			
			agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() >= 0)
			{
				swapFootstep(agent);
				agent.setOffsetX(0);
				agent.setStepping(false);
				finishedStep = true;
			}
		}
		else
		{
			System.out.println("Invalid arguments to action Step.");
			System.out.println("Step must take 1 argument denoting direction, as either: {up, down, left, right}");
			return; //invalid arguments, do nothing
		}
	}

	@Override
	public boolean isFinished() 
	{
		return finishedStep;
	}
	
	/**
	 * Determine whether it is possible to step to the next location, incorporating bounds checking,
	 * collision checking with things and objects, and ensuring that the next location either has
	 * solid ground below it or a crossable thing on it
	 * 
	 * @param agent the agent taking the action
	 * @param world the world
	 * @param dir the direction of the step
	 * @return true if the agent can step in the given direction, false otherwise
	 */
	private boolean canStep(Agent agent, World world, direction dir)
	{
		int[] pos = agent.getPos();
		int x = pos[0];
		int y = pos[1];
		
		switch (dir)
		{
		case up:
			y += 1;
		break;
		case down:
			y -= 1;
		break;
		case left:
			x -= 1;
		break;
		case right:
			x += 1;
		break;
		}
		
		for (int z = pos[2]; z < pos[2] + agent.getHeight(); z ++)
		{
			//grid bounds check
			if (!world.isInBounds(x, y, z))
			{
				return false;
			}
			//collision check
			if (world.isBlocked(x, y, z))
			{
				return false;
			}
		}
		//ground check
		if (!world.isCrossable(x, y, pos[2]))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Determine whether it is possible to step to the next location, where that location is at the top
	 * of a ramp, incorporating bounds checking,
	 * collision checking with things and objects, and ensuring that the next location either has
	 * solid ground below it or a crossable thing on it
	 * 
	 * @param agent the agent taking the action
	 * @param world the world
	 * @param dir the direction of the step
	 * @return true if the agent can step in the given direction, false otherwise
	 */
	private boolean canStepRamp(Agent agent, World world, direction dir)
	{
		int[] pos = agent.getPos();
		int x = pos[0];
		int y = pos[1];
		int z = pos[2];
		
		switch (dir)
		{
		case up:
			y += 1;
			z += 1;
		break;
		case down:
			y -= 1;
			z -= 1;
		break;
		}
		
		for (int k = z; k < pos[2] + agent.getHeight(); k ++)
		{
			//grid bounds check
			if (!world.isInBounds(x, y, k))
			{
				return false;
			}
			//collision check
			if (world.isBlocked(x, y, k))
			{
				return false;
			}
		}
		//ground check
		if (!world.isCrossable(x, y, z))
		{
			return false;
		}
		
		return true;
	}
	
	private void swapFootstep(Agent agent)
	{
		if (agent.getFootstep() == right)
		{	
			agent.setFootstep(left);
		}
		else
		{
			agent.setFootstep(right);
		}
	}
}
