package util.nodes;

import java.util.Map;

import util.ServiceLinkedList;
import util.nodes.CustomDSA.AvlTree;

/*
 * Represents a Borough object in the system 
 * <p>
 * This is a child class of Node. It defines the node Type "Borough" and uses inherited functions and variables 
 * from the parent class. 
 * </p> 
 */

public class Borough extends CityArea {


	@Override
	public String getNodeType() { 
		return "Borough";
		
	}
	
	public Borough(String name) {
		super.LocationName = name;
	}

	
	// Adds a new location to the neighbour variable 
	public void addNeightbour(int dist, Borough x) {
		this.Neighbours.insert(this.Neighbours.getRoot(), x, dist);
	}
	
	// sets a new value to neighbour
	public void addNeighbour(AvlTree x) {
		this.Neighbours = x;
	}

}
