package util;
import java.util.Map;
import util.nodes.Node;
import java.util.ArrayList;
import java.util.HashMap;

public class Incident {
	/** stores the incident id **/
	private final int id;
	
	/** stores the node id where the incident took place **/
	private final int nodeId;
	
	/** stores the node where the incident took place **/
	private final Node node;
	
	/** stores the incident level **/
	private int incidentLevel;
	
	/** stores weather the incident is running or not **/
	private Boolean running = true;
	
	/** stores the incident type **/
	private int incidentType;
	
	/** a map of Service type pointing to a array of service id used too store the info of all the dispatched service**/
	private Map<String, ArrayList<Integer>> dispatchList = new HashMap<>();
	
	/**
	 * a constructor
	 * @param id incident id
	 * @param node node where the incident took place
	 * @param incidentLevel incident level
	 * @param incidentType type
	 */
	public Incident(int id, Node node, int incidentLevel, int incidentType) {
		this.id = id;
		this.nodeId = node.getID();
		this.node = node;
		this.incidentType = incidentType;
		this.incidentLevel = incidentLevel;
		dispatchList.put(Police.Type, new ArrayList<Integer>());
		dispatchList.put(Ambulance.Type, new ArrayList<Integer>());
		dispatchList.put(FireTruck.Type, new ArrayList<Integer>());
	}
	
	/** returns weather the incident is ongoing **/
	public Boolean getRunning() {
		return running;
	}
	
	/** sets teh incident of **/
	public void offRunning() {
		this.running = false;
	}
	
	/**
	 * adds a service to dispatch list
	 * @param type of service
	 * @param id id of the service
	 */
	public void addToDispatchList(String type, int id) {
		ArrayList<Integer> list = this.dispatchList.get(type);
		if(list != null) {	
			list.add(id);
		}
		else {
			this.dispatchList.put(type, new ArrayList<Integer>());
			ArrayList<Integer> list2 = this.dispatchList.get(type);
			list2.add(id);
 			
		}
	}
	
	
	/**
	 * 
	 * @return incident id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * 
	 * @return node id of the node where incident took place
	 */
	public int getNodeId() {
		return this.nodeId;
	}

	/**
	 * 
	 * @return node where the incident took place
	 */
	public Node getNode() {
		return this.node;
	}

	/**
	 * 
	 * @return incident level
	 */
	public int getIncidentLevel() {
		return this.incidentLevel;
	}

	/**
	 * 
	 * @return incident type in string form
	 */
	public String getType() {
		if (this.incidentType == 3) {
			return Police.Type;
		}
		else if(this.incidentType == 2) {
			return Ambulance.Type;
		}
		else if(this.incidentType == 1){
			return FireTruck.Type;
		}
		else {
			return Police.Type;
		}
	}
	
	/**
	 * 
	 * @return the dispatch list
	 */
	public Map<String, ArrayList<Integer>> getDispatchList(){
		return this.dispatchList;
	}

}
