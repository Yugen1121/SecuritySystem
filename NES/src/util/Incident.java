package util;
import java.util.Map;
import util.nodes.Node;
import java.util.ArrayList;

public class Incident {
	private final int id;
	private final int nodeId;
	private final Node node;
	private int incidentLevel;
	private int incidentType;
	private Map<String, ArrayList<Integer>> dispatchList;
	private boolean incidentOn = true;
	
	public Incident(int id, Node node, int incidentLevel, int incidentType) {
		this.id = id;
		this.nodeId = node.getID();
		this.node = node;
		this.incidentType = incidentType;
		this.incidentLevel = incidentLevel;
	}
	
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
	
	public int getId() {
		return this.id;
	}
	
	public int getNodeId() {
		return this.nodeId;
	}

	public Node getNode() {
		return this.node;
	}

	public int getIncidentLevel() {
		return this.incidentLevel;
	}

	public int getType() {
		return this.incidentType;
	}
	
	public Map<String, ArrayList<Integer>> getDispatchList(){
		return this.dispatchList;
	}
	
	public boolean getIncidentOn() {
		return this.incidentOn;
	}
	
	public void setIncidentOf() {
		this.incidentOn = false;
	}
	

}
