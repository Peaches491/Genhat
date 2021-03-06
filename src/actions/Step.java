package actions;


import utils.planners.PathPlannerUtils;
import world.World;
import entities.Agent;
import entities.Agent.direction;
import static entities.Agent.direction.*;

public class Step implements Action {

	private final direction dir;
	boolean finished;
	SimpleStep simpleStep;
	Action rampStep;
	
	public Step(direction dir)
	{
		this.dir = dir;
		finished = false;
	}
	
	@Override
	public void execute(Agent agent, World world)
	{
		//begin a new step if one is not already in progress, otherwise continue executing the step in progress
		if (!continueStepping(agent, world))
		{
			//set direction if necessary
			if (agent.getDir() != dir && (dir == left || dir == right || !PathPlannerUtils.isOnRampHorizontal(world, agent.getPos())))
			{
				agent.setDir(dir);
			}
			
			//check for horizontal ramp steps
			boolean horizontalRampCheck[] = PathPlannerUtils.checkForHorizontalRampStep(world, agent.getPos(), dir);
			if (horizontalRampCheck[0])
			{
				rampStep = new HorizontalRampStep(dir, horizontalRampCheck[1]);
				rampStep.execute(agent, world);
			}
			else
			{
				//check for vertical ramp steps
				if (PathPlannerUtils.checkForVerticalRampStep(world, agent.getPos(), dir))
				{
					rampStep = new VerticalRampStep(dir);
					rampStep.execute(agent, world);
				}
				//all special cases checked for, use a simple step instead
				else
				{
					simpleStep = new SimpleStep(dir);
					simpleStep.execute(agent, world);
				}
			}
		}
		
		finished = (simpleStep == null || simpleStep.isFinished()) && (rampStep == null || rampStep.isFinished());
	}

	@Override
	public boolean isFinished() 
	{
		return finished;
	}
	
	private boolean continueStepping(Agent agent, World world)
	{
		if (agent.isRampAscending() || agent.isRampDescending())
		{
			rampStep.execute(agent, world);
			return true;
		}
		
		if (agent.isStepping())
		{
			simpleStep.execute(agent, world);
			return true;
		}
		
		return false;
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
