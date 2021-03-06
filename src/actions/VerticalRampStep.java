package actions;

import utils.planners.PathPlannerUtils;
import world.Position;
import world.World;
import entities.Agent;
import entities.Agent.direction;
import entities.Hero;
import entities.Placeholder;
import static entities.Agent.direction.*;

public class VerticalRampStep implements Action {

	private final direction dir;
	private boolean initialized;
	private boolean finished;
	private boolean specialCase; //tracks special cases (step off for ascending, step on for descending)

	public VerticalRampStep(direction dir)
	{
		this.dir = dir;
		this.initialized = false;
		this.finished = false;
	}
	
	@Override
	public void execute(Agent agent, World world)
	{		
		if (finished)
			return;
		
		//initialize ramp step
		if (!initialized)
		{
			Position pos;
			switch (dir)
			{
			case up:
				//Ascending a vertical ramp
				if (PathPlannerUtils.canStepVerticalRamp(agent, world, agent.getPos(), up))
				{
					Position checkPos = new Position(agent.getPos());
					checkPos.y ++;
					//Regular case: moving upwards
					if (world.hasThing(checkPos) && world.getThingsAt(checkPos).hasRamp() && world.getThingsAt(checkPos).getRampDir() == up)
					{
						world.moveAgent(agent, 0, 0, 1);
						agent.setOffsetY(-16);
						pos = agent.getPos();
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y, pos.z - 1));
						world.addAgent(h1);
						agent.setRampAscending(true);
						specialCase = false;
					}
					//Special case: stepping off of ramp
					else
					{
						world.moveAgent(agent, 0, 1, 0);
						agent.setOffsetY(-16);
						pos = agent.getPos();
						//set placeholder to render instead of agent (fixes a graphical glitch caused by tile render order)
						agent.setRenderOnPlaceholder(true);
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y - 1, pos.z));
						h1.setTransparent(true);
						world.addAgent(h1);
						agent.setRampAscending(true);
						specialCase = true;
					}
				}
				else
				{
					finished = true;
					return;
				}
			break;
				
			case down:
				//Descending a vertical ramp
				pos = agent.getPos();
				if (PathPlannerUtils.canStepVerticalRamp(agent, world, pos, down))
				{
					//Special case: stepping on
					if (world.getTerrainAt(pos.x, pos.y, pos.z - 1).isBlocking() || (world.hasThing(pos) && world.getThingsAt(pos).isCrossable()))
					{
						world.moveAgent(agent, 0, -1, 0);
						agent.setOffsetY(16);
						pos = agent.getPos();
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y + 1, pos.z));
						world.addAgent(h1);
						agent.setRampDescending(true);
						specialCase = true;
					}
					//Regular case: moving downwards
					else
					{
						world.moveAgent(agent, 0, 0, -1);
						agent.setOffsetY(16);
						pos = agent.getPos();
						//set placeholder to render instead of agent (fixes a graphical glitch caused by tile render order)
						agent.setRenderOnPlaceholder(true);
						Placeholder h1 = new Placeholder(agent, new Position(pos.x, pos.y, pos.z + 1));
						h1.setTransparent(true);
						world.addAgent(h1);
						agent.setRampDescending(true);
						specialCase = false;
					}
				}
				else
				{
					finished = true;
					return;
				}
			break;
				
			case left:
				pos = agent.getPos();
				if (PathPlannerUtils.canStepVerticalRamp(agent, world, pos, left))
				{
					world.moveAgent(agent, -1, 0, 0);
					agent.setOffsetX(16);
					pos = agent.getPos();
					Placeholder h1 = new Placeholder(agent, new Position(pos.x + 1, pos.y, pos.z));
					world.addAgent(h1);
					agent.setRampAscending(true);
				}
				else
				{
					finished = true;
					return;
				}
			break;
				
			case right:
				pos = agent.getPos();
				if (PathPlannerUtils.canStepVerticalRamp(agent, world, pos, right))
				{
					world.moveAgent(agent, 1, 0, 0);
					agent.setOffsetX(-16);
					pos = agent.getPos();
					Placeholder h1 = new Placeholder(agent, new Position(pos.x - 1, pos.y, pos.z));
					world.addAgent(h1);
					agent.setRampAscending(true);
				}
				else
				{
					finished = true;
					return;
				}
			break;
			}
			
			initialized = true;
		}
		
		Position pos;
		//Execute step (many cases...)
		switch (dir)
		{
		case up:
			agent.incrementYOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() >= 0)
			{
				swapFootstep(agent);
				pos = agent.getPos();
				if (specialCase)
				{
					world.removeAgentAt(pos.x, pos.y - 1, pos.z);
					agent.setRenderOnPlaceholder(false);
				}
				else
					world.removeAgentAt(pos.x, pos.y, pos.z - 1);
				agent.setOffsetY(0);
				agent.setRampAscending(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockV())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		break;
		case down:
			agent.incrementYOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetY() <= 0)
			{
				pos = agent.getPos();
				if (specialCase)
					world.removeAgentAt(pos.x, pos.y + 1, pos.z);
				else
				{
					world.removeAgentAt(pos.x, pos.y, pos.z + 1);
					agent.setRenderOnPlaceholder(false);
				}
				swapFootstep(agent);
				agent.setOffsetY(0);
				agent.setRampDescending(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockV())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		break;
			
		case left:
			agent.incrementXOffset(-agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() <= 0)
			{
				pos = agent.getPos();
				world.removeAgentAt(pos.x + 1, pos.y, pos.z);
				swapFootstep(agent);
				agent.setOffsetX(0);
				agent.setRampAscending(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockH())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		break;
		case right:
			agent.incrementXOffset(agent.getSpeed() * 16.0f / 32.0f);
			if (agent.getOffsetX() >= 0)
			{
				pos = agent.getPos();
				world.removeAgentAt(pos.x - 1, pos.y, pos.z);
				swapFootstep(agent);
				agent.setOffsetX(0);
				agent.setRampAscending(false);
				finished = true;
			}
			
			//Camera for Hero
			if (agent.getClass().equals(Hero.class))
			{
				if (world.isCameraLockH())
					world.updateCameraScrollLock();
				world.updateCamera();
			}
		break;
		}
	}

	@Override
	public boolean isFinished() 
	{
		return finished;
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

	@Override
	public boolean requestInterrupt() {
		return finished;
	}

	@Override
	public boolean isInterruptable() {
		return true;
	}
}
