package util.nodes;

public class PoliceStation extends ServiceNode{

	public PoliceStation(int id, String LocationName) {
		super(id, LocationName);
	}

	String getNodeType() {
		return "Police Station";
	}
	
	
}
