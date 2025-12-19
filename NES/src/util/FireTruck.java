package util;
import util.nodes.Node;
/*
 * Represents a Ambulance object in the system 
 * <p>
 * This is a child class of Service. It defines the service type "Fire Truck" and uses inherited functions and variables 
 * from the parent class. 
 * </p> 
 */

public
class FireTruck extends Service{
	public static final String Type = "Fire Truck";
	public FireTruck(Node place, int id) {
		super(place, id);
	}
	
	@Override
	public String getServiceType() {
		// TODO Auto-generated method stub
		return "Fire Truck";
	}	
}
