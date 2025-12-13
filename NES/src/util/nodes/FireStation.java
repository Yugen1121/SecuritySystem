package util.nodes;

public class FireStation extends ServiceNode{

	public FireStation(int id, String LocationName) {
		super(id, LocationName);
	}

	String getNodeType() {
		return "Fire Station";
	}
}
