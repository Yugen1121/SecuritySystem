package util.nodes;

import java.util.Map;
import java.util.PriorityQueue;
import util.ServiceLinkedList;
import util.nodes.CustomDSA.AvlTree;

/*
 * Represents a City object in the system 
 * <p>
 * This is a child class of Node. It defines the node Type "City" and uses inherited functions and variables 
 * from the parent class. 
 * </p> 
 */

public class City extends CityArea{
	@Override
	public String getNodeType() {
		return "City";
		
	}
	
	public City(String name) {
		super.LocationName = name;
	}

	
	// Adds a new location to the neighbour variable 
	public void addNeightbour(int dist, City x) {
		this.Neighbours.insert(this.Neighbours.getRoot(), x, dist);
	}
	
	// sets a new value to neighbour
	public void addNeighbour(AvlTree x) {
		this.Neighbours = x;
	}
	
	

}
