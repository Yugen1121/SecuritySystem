package util;
import util.nodes.*;
public class ServiceLinkedList {
	private Service value;
	private boolean available = true; 
	private ServiceLinkedList next = null;
	
	public ServiceLinkedList(Service x) {
		this.value = x;
	}
	
	public void setNext(ServiceLinkedList next) {
		this.next = next;
	}
	
	public ServiceLinkedList getNext() {
		return this.next;
	}
	
	public void removeNext() {
		this.next = null;
	}
	public void switchAvailability() {
		this.available = !this.available;
	}
	public Service getValue() {
		return this.value;
	}
}
