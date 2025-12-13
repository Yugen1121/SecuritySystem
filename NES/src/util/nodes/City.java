package util.nodes;


import java.util.PriorityQueue;
import util.ServiceLinkedList;

/*
 * Represents a City object in the system 
 * <p>
 * This is a child class of Node. It defines the node Type "City" and uses inherited functions and variables 
 * from the parent class. 
 * </p> 
 */

public class City extends CityArea{
	public City(int id, String LocationName) {
		super(id, LocationName);
	}

	@Override
	public String getNodeType() {
		return "City";
		
	}
	

}
