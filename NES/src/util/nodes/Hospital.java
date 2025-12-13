package util.nodes;

public class Hospital extends ServiceNode{

	
	public Hospital(int id, String LocationName) {
		super(id, LocationName);
	}

	String getNodeType() {
		return "Hospital";
	}
	
}
