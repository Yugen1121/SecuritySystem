package application;
import java.util.Map;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import util.Environment;
import util.Incident;
import util.nodes.Node;

public class NodeCard extends VBox{
	private VBox detailsPane;
	private Environment env;
	private util.nodes.Node node = null;
	private final Label incidents = new Label();
	private final Label services = new Label();
	
	public void bindValues() {
		
		
		Observable[] incidentBindings = node.getIncidents()
											.values()
											.stream()
											.toArray(size -> new Observable[size]);
		
		this.styleProperty().bind(
				Bindings.createStringBinding(
						() -> {
							if (node.hasIncident()) {
								return String.format("-fx-border-color: red; -fx-background-color: #ffecec;");
							}else {
								return String.format("-fx-border-color: lightgray; -fx-background-color: white;");
							}
						}, incidentBindings
						)
				
				);
		
		incidents.textProperty().bind(
			    Bindings.createStringBinding(
			        () -> "Incidents: " + node.getIncidents()
			                                  .values()
			                                  .stream()
			                                  .mapToInt(Map::size)
			                                  .sum()
			    , incidentBindings
			        
			    )
			);
		
		Observable[] servicesBinding = node.getServices()
										   .values()
										   .stream()
										   .toArray(size -> new Observable[size]);
				
				
		services.textProperty().bind(
				Bindings.createStringBinding(
						() -> "Services: " + node.getServices()
												 .values()
												 .stream()
												 .mapToInt(Map::size)
						 						 .sum()
						, servicesBinding
						)
				
				);
	}
    public NodeCard(util.nodes.Node node,Environment env, VBox detailsPane) {
    	this.node = node;
    	this.env = env;
    	this.detailsPane = detailsPane;
    	
    	bindValues();
    	
    }
    
    public VBox createNodeCard() {
    	Label id = new Label("ID: " + node.getID() + " ");

        Label name = new Label(node.getLocationName());
        name.setStyle("-fx-font-weight: bold;");

        HBox r1 = new HBox();
        r1.getChildren().addAll(id, name);
        HBox r2 = new HBox();                

        this.getChildren().addAll(r1, this.incidents, this.services);
        this.setPadding(new Insets(8));
        this.setCursor(Cursor.HAND);

        this.setOnMouseClicked(e ->
            detailsPane.getChildren().setAll(
                new NodeDetailPane(node, env)
            )
        );
        return this;
    	
    }
    
}