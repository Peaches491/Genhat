package things;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import things.Stairs.StairsBuilder;
import things.Stairs.stairsType;
import things.Thing.connectionContext;
import entities.Agent.direction;
import static entities.Agent.direction.right;
import static things.Stairs.stairsType.indoorWooden;
import static things.Stairs.stairsType.outdoorWooden;
import static things.Thing.connectionContext.*;
import static entities.Agent.direction.*;

public class Rope extends ClimbingSurface {

	private Rope(RopeBuilder builder)
	{
		super();
		
		this.horizontalConnection = builder.horizontalConnection;
		this.verticalConnection = builder.verticalConnection;
		this.dir = builder.dir;
		
		this.texRow = 4;
		this.texCol = 0;
		
		loadTextures();
	}
	
	@Override
	public void loadTextures()
	{
		try {
			texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("graphics/objects/thing3.png"));
		} catch (IOException e) {e.printStackTrace();}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void renderThing(int pixelSize, int terrainTextureSize)
	{
		GL11.glPushMatrix();
			
			texture.bind();
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
			float tConvX = ((float)TEXTURE_SIZE_X)/((float)TEXTURE_SHEET_WIDTH);
			float tConvY = ((float)TEXTURE_SIZE_Y)/((float)TEXTURE_SHEET_HEIGHT);
			
			int texX = texCol * 4;
			int texY = texRow;
			
			switch (getVerticalConnection())
			{
			case middle:		texY += 1;	break;
			case start:			texY += 2;	break;
			case standalone:	texY += 3;	break;
			}
				
			switch (getHorizontalConnection())
			{
			case middle:
				texX += 1;
				break;
			case end:
				texX += 2;
				break;
			case standalone:
				texX += 3;
				break;
			}
			
			int xMin = pixelSize * ((terrainTextureSize - TEXTURE_SIZE_X) / 2);
			int xMax = xMin + pixelSize * (TEXTURE_SIZE_X);
			int yMin = 0;
			int yMax = yMin + pixelSize * (TEXTURE_SIZE_Y);
			
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(texX * tConvX, texY*tConvY + tConvY);
				GL11.glVertex2f(xMin, yMin);
				GL11.glTexCoord2f(texX*tConvX + tConvX, texY*tConvY + tConvY);
				GL11.glVertex2f(xMax, yMin);
				GL11.glTexCoord2f(texX*tConvX + tConvX, texY * tConvY);
				GL11.glVertex2f(xMax, yMax);
				GL11.glTexCoord2f(texX*tConvX, texY * tConvY);
				GL11.glVertex2f(xMin, yMax);
			GL11.glEnd();
			
		GL11.glPopMatrix();
	}
	
	public static class RopeBuilder
	{
		private connectionContext horizontalConnection = standalone;
		private connectionContext verticalConnection = standalone;
		private direction dir = up;
		
		public RopeBuilder horizontalConnection(connectionContext horizontalConnection)
		{
			this.horizontalConnection = horizontalConnection;
			return this;
		}
		
		public RopeBuilder verticalConnection(connectionContext verticalConnection)
		{
			this.verticalConnection = verticalConnection;
			return this;
		}
		
		public RopeBuilder dir(direction dir)
		{
			this.dir = dir;
			return this;
		}
		
		public Rope build()
		{
			return new Rope(this);
		}
	}
}
