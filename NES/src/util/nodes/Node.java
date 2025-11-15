
package util.nodes;
import java.util.Map;
import util.ServiceLinkedList;
import util.Service;


/*
 * This is the base entity for anything related to Node/Location in the system.
 * 
 * <p>
 * This class provides attributes and function for all the locations has unique id and is associated
 * with ServiceLinkedList to keep track of services in the location.
 * </p>
 * 
 * <h2>
 * Responsibilities 
 * </h2>
 * <ul>
 * <li>Store the id of the Location.</li>
 * <li>Store and manage the current location and its services.</li>
 * <li>Enforce that all the Node subclass has their services.</li>
 * </ul>
 * 
 * <h2>Subclass requirement</h2>
 * <ul>
 * <li>Must implement the getNodeType().</li>
 * <li>Must assign id and LocationName.</li>
 * </ul>
 */

public abstract class Node {
	private int id;
	protected String LocationName;
	protected Map<String, ServiceLinkedList> availableService;
	
	public String getLocation() {
		return this.LocationName;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() { 
		return this.id;
	}
	
	public void addService(Service x) {
		if (this.availableService.containsKey(x.getServiceType())) {
			String Type = x.getServiceType();
			ServiceLinkedList list = this.availableService.get(Type);
			ServiceLinkedList ToAdd = new ServiceLinkedList(x);
			ToAdd.setNext(list);
			this.availableService.put(Type, ToAdd);
			
		}else {
			this.availableService.put(x.getServiceType(), new ServiceLinkedList(x));
		}
	}
	
	
	public void setService(Map<String, ServiceLinkedList> x) {
		this.availableService = x; 
	}
	
	public ServiceLinkedList popService(String Type) {
		if (this.availableService.containsKey(Type)) {
			ServiceLinkedList OldHead = this.availableService.get(Type);
			ServiceLinkedList NewHead = OldHead.getNext();
			this.availableService.put(Type, NewHead);
			return OldHead;
			
		}
		return null;
	}
	
	abstract String getNodeType();
	
}
