package util.nodes;

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
	
	public Borough(int id, String LocationName) {
		super(id, LocationName);
	
	}


}
