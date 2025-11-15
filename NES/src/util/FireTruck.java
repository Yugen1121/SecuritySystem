package util;

/*
 * Represents a Ambulance object in the system 
 * <p>
 * This is a child class of Service. It defines the service type "Fire Truck" and uses inherited functions and variables 
 * from the parent class. 
 * </p> 
 */

public
class FireTruck extends Service{
	
	public FireTruck(int id) {
		this.setID(id);
	}
	
	@Override
	public String getServiceType() {
		// TODO Auto-generated method stub
		return "Fire truck";
	}
	
}
