package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for redstone wires
 */
public class RedstoneWire extends BlockModel
{

	private boolean isConnectable(String otherBlockId, boolean sameLevel)
	{
		if (blockId.equals(otherBlockId))
			return true;
		if (sameLevel && (otherBlockId.equals("minecraft:redstone_torch") || otherBlockId.equals("minecraft:redstone_wall_torch")))
			return true;
		return false;
	}
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		boolean on = Integer.parseInt(data.get("power")) > 0;
		String mtlCross = on ? materials.get(data,biome)[0] : materials.get(data,biome)[2];
		String mtlLine  = on ? materials.get(data,biome)[1] : materials.get(data,biome)[3];
		
		boolean conn_n_up = isConnectable(chunks.getBlockID(x, y+1, z-1), false);
		boolean conn_s_up = isConnectable(chunks.getBlockID(x, y+1, z+1), false);
		boolean conn_e_up = isConnectable(chunks.getBlockID(x+1, y+1, z), false);
		boolean conn_w_up = isConnectable(chunks.getBlockID(x-1, y+1, z), false);
		boolean conn_n_down = isConnectable(chunks.getBlockID(x, y-1, z-1), false);
		boolean conn_s_down = isConnectable(chunks.getBlockID(x, y-1, z+1), false);
		boolean conn_e_down = isConnectable(chunks.getBlockID(x+1, y-1, z), false);
		boolean conn_w_down = isConnectable(chunks.getBlockID(x-1, y-1, z), false);
		boolean conn_n = conn_n_up || conn_n_down || isConnectable(chunks.getBlockID(x, y, z-1), true);
		boolean conn_s = conn_s_up || conn_s_down || isConnectable(chunks.getBlockID(x, y, z+1), true);
		boolean conn_e = conn_e_up || conn_e_down || isConnectable(chunks.getBlockID(x+1, y, z), true);
		boolean conn_w = conn_w_up || conn_w_down || isConnectable(chunks.getBlockID(x-1, y, z), true);
		
		int nconn = (conn_n ? 1:0) + (conn_s ? 1:0) + (conn_e ? 1:0) + (conn_w ? 1:0);

		boolean straight = nconn == 1 || nconn == 2 && (conn_n && conn_s || conn_e && conn_w); 
		

		Vertex[] vertices = new Vertex[4];
		UV[] uv;
		
		// Straight ground wire
		if (straight)
		{
			if (conn_n || conn_s)
				uv = new UV[] { new UV(0,1), new UV(0,0), new UV(1,0), new UV(1,1) };
			else
				uv = new UV[] { new UV(0,1), new UV(1,1), new UV(1,0), new UV(0,0) };

			vertices[1] = new Vertex(x-0.5f, y-0.49f, z+0.5f);
			vertices[2] = new Vertex(x+0.5f, y-0.49f, z+0.5f);
			vertices[3] = new Vertex(x+0.5f, y-0.49f, z-0.5f);			
			vertices[0] = new Vertex(x-0.5f, y-0.49f, z-0.5f);
			obj.addFace(vertices, uv, null, mtlLine);
		} 
		// Intersection point
		else 
		{
			// The main intersection dot
			uv = new UV[] { new UV(0.75f,0.25f), new UV(0.25f,0.25f), new UV(0.25f,0.75f), new UV(0.75f, 0.75f) };			
			vertices[1] = new Vertex(x-0.25f, y-0.48f, z+0.25f);
			vertices[2] = new Vertex(x-0.25f, y-0.48f, z-0.25f);
			vertices[3] = new Vertex(x+0.25f, y-0.48f, z-0.25f);			
			vertices[0] = new Vertex(x+0.25f, y-0.48f, z+0.25f);			
			obj.addFace(vertices, uv, null, mtlCross);
			
			//If connects north
			if (conn_n) {
				uv = new UV[] { new UV(0,1), new UV(0,0.5f), new UV(1,0.5f), new UV(1,1) };				
				vertices[1] = new Vertex(x-0.5f, y-0.49f, z);
				vertices[2] = new Vertex(x+0.5f, y-0.49f, z);
				vertices[3] = new Vertex(x+0.5f, y-0.49f, z-0.5f);			
				vertices[0] = new Vertex(x-0.5f, y-0.49f, z-0.5f);
				obj.addFace(vertices, uv, null, mtlLine);					
			}
	
			//If connects south
			if (conn_s) {
				uv = new UV[] { new UV(0,0.5f), new UV(0,0), new UV(1,0), new UV(1,0.5f) };				
				vertices[1] = new Vertex(x-0.5f, y-0.49f, z+0.5f);
				vertices[2] = new Vertex(x+0.5f, y-0.49f, z+0.5f);
				vertices[3] = new Vertex(x+0.5f, y-0.49f, z);			
				vertices[0] = new Vertex(x-0.5f, y-0.49f, z);
				obj.addFace(vertices, uv, null, mtlLine);				
			}

			//If connects west
			if (conn_w) {
				uv = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,0), new UV(0,0) };				
				vertices[1] = new Vertex(x, y-0.49f, z+0.5f);
				vertices[2] = new Vertex(x-0.5f, y-0.49f, z+0.5f);
				vertices[3] = new Vertex(x-0.5f, y-0.49f, z-0.5f);			
				vertices[0] = new Vertex(x, y-0.49f, z-0.5f);
				obj.addFace(vertices, uv, null, mtlLine);					
			}	
			
			//If connects east
			if (conn_e) {
				uv = new UV[] { new UV(0,1), new UV(1,1), new UV(1,0.5f), new UV(0,0.5f) };				
				vertices[1] = new Vertex(x+0.5f, y-0.49f, z+0.5f);
				vertices[2] = new Vertex(x, y-0.49f, z+0.5f);
				vertices[3] = new Vertex(x, y-0.49f, z-0.5f);			
				vertices[0] = new Vertex(x+0.5f, y-0.49f, z-0.5f);
				obj.addFace(vertices, uv, null, mtlLine);					
			}					
			
		}
		
		// wall wire
		uv = new UV[] { new UV(0,1), new UV(0,0), new UV(1,0), new UV(1,1) };

		if (conn_n_up)
		{
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z-0.49f);
			vertices[2] = new Vertex(x+0.5f, y-0.5f, z-0.49f);
			vertices[3] = new Vertex(x+0.5f, y+0.5f, z-0.49f);
			vertices[0] = new Vertex(x-0.5f, y+0.5f, z-0.49f);
			obj.addFace(vertices, uv, null, mtlLine);
		}
		if (conn_s_up)
		{
			vertices[1] = new Vertex(x+0.5f, y-0.5f, z+0.49f);
			vertices[2] = new Vertex(x-0.5f, y-0.5f, z+0.49f);
			vertices[3] = new Vertex(x-0.5f, y+0.5f, z+0.49f);
			vertices[0] = new Vertex(x+0.5f, y+0.5f, z+0.49f);
			obj.addFace(vertices, uv, null, mtlLine);
		}
		if (conn_e_up)
		{
			vertices[1] = new Vertex(x+0.49f, y-0.5f, z-0.5f);
			vertices[2] = new Vertex(x+0.49f, y-0.5f, z+0.5f);
			vertices[3] = new Vertex(x+0.49f, y+0.5f, z+0.5f);
			vertices[0] = new Vertex(x+0.49f, y+0.5f, z-0.5f);
			obj.addFace(vertices, uv, null, mtlLine);
		}
		if (conn_w_up)
		{
			vertices[1] = new Vertex(x-0.49f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x-0.49f, y-0.5f, z-0.5f);
			vertices[3] = new Vertex(x-0.49f, y+0.5f, z-0.5f);
			vertices[0] = new Vertex(x-0.49f, y+0.5f, z+0.5f);
			obj.addFace(vertices, uv, null, mtlLine);
		}
	}

}
