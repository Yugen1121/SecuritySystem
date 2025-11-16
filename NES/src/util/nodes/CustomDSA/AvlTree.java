package util.nodes.CustomDSA;
import util.nodes.Node;
import util.nodes.City;

/*
 * An AVL tree that stores AvlNode object, where each node contains distance value associated with the Node class.
 * 
 *  <p> This AVL tree supports insertion with automatic height update and balancing via rotations like left, right, right left, left right
 *  rotation, The tree also maintains the pointer to the left most pointer acting as a psudo-min-heap.</p>
 *  
 *   <h2>Features</h2>
 *   <ul>
 *   <li>Self balancing</li>
 *   <li>Left-most tracking</li>
 *   <li>IN-order traversal</li>
 *   <li>Supports duplicate index(dist)</li>
 *   </ul>
 */

public class AvlTree {
	
	public static void main(String[] args) {

		AvlTree x = new AvlTree();				
		
		for (int i= 0; i<5; i ++) {
			x.root = x.insert(x.root, new City("name"), i);
		}
		x.inOrder();
		x.updateLeftMost();
	}
	
	private AvlNode root;
	private AvlNode leftMost;
	
	private int getHeight(AvlNode node) {
	return node == null ? 0 : node.height;
	}
	
	private int getBalance(AvlNode node) {
		return node == null ? 0 : (getHeight(node.left) - getHeight(node.right)); 
	}
	
	private AvlNode rightRotate(AvlNode y) {
			AvlNode x = y.left;
			AvlNode z = x.right;
			
			// Rotation
			x.right = y;
			y.left = z;
			
			// height update 
			y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
			x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
			
			return x;
	}
	private AvlNode leftRotate(AvlNode y) {
		AvlNode x = y.right;
		AvlNode z = x.left;
		
		// Rotation
		x.left = y;
		y.right = z;
		
		// height update 
		y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
		x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
		
		return x;
}
	public AvlNode insert(AvlNode node, Node x, int dist) {
		if (node == null) return new AvlNode(x, dist);
		
		if (dist < node.dist) node.left = insert(node.left, x, dist);
		else node.right = insert(node.right, x, dist);
		
		node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
		
		int balance = getBalance(node);
		int balanceLeft = getBalance(node.left);
		int balanceRight = getBalance(node.right);
		
		// Right rotation
		if (balance > 1) {
			// left right rotation
			if (balanceLeft < 0) {
				node.left = leftRotate(node.left);
			}
			return rightRotate(node);
			
		}
		// left rotation
		if (balance < -1) {
			// right left rotation
			if (balanceRight > 0) {
				node.right = rightRotate(node.right);
			}
			return leftRotate(node);
		}
		
		
		
		//no rotation
		return node;
		
	}
	
	public void updateLeftMost() {
		leftMost = root;

		while (leftMost.left != null) {
			leftMost = leftMost.left;
		}
	}
	
	public void inOrder(AvlNode x) {
		if (x == null) {
			return;
		}
		inOrder(x.left);
		System.out.println(x.dist);
		inOrder(x.right);
	}
	
	public void inOrder() {
		inOrder(root);
	}
	
		
}

