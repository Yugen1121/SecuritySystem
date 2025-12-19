package application;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;

import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import util.FireTruck;
import util.Ambulance;
import util.Environment;
import util.Incident;
import util.Police;
import util.Service;
import util.nodes.Node;



public class NodeDetailPane extends VBox {

    private final VBox detailBlock = new VBox(8);
    private Environment env;
    private Node node;
    
    public NodeDetailPane(Node node, Environment env) {
    	
    	this.env = env;
    	this.node = node;
        buildUI();
    }
    
    
    private void buildUI() {
    	if (node == null || env == null) {
    		return;
    	}
    	getChildren().clear();
    	setSpacing(10);
        setPadding(new Insets(10));

        HBox row1 = new HBox(10);
        row1.setPrefHeight(300);

        VBox infoPane = createInfoPane(node);
        
        ScrollPane policePane = createServicePane(Police.Type, node);
        ScrollPane firePane = createServicePane(FireTruck.Type, node);
        ScrollPane ambulancePane = createServicePane(Ambulance.Type, node);

        HBox.setHgrow(policePane, Priority.ALWAYS);
        HBox.setHgrow(firePane, Priority.ALWAYS);
        HBox.setHgrow(ambulancePane, Priority.ALWAYS);

        row1.getChildren().addAll(infoPane, policePane, firePane, ambulancePane);

        HBox row2 = new HBox(10);
        row2.setPrefHeight(300);
        
        HBox row3 = new HBox(10);
        row3.setPrefHeight(30);
        HBox functions = createFunctionButton(node);
        row3.getChildren().add(functions);
        ScrollPane incidentsPane = createIncidentPane(node);
        detailBlock.getChildren().clear();
        ScrollPane ongoingIncidentPane = createOngoingIncidentPane(node);
        detailBlock.setStyle("-fx-border-color: gray;");
        detailBlock.setPadding(new Insets(10));

        HBox.setHgrow(detailBlock, Priority.ALWAYS);

        row2.getChildren().addAll(incidentsPane, ongoingIncidentPane, detailBlock);

        getChildren().addAll(row1, row2, row3);
        VBox.setVgrow(row1, Priority.ALWAYS);
        VBox.setVgrow(row2, Priority.ALWAYS);
        VBox.setVgrow(row3, Priority.ALWAYS);
    }

    // INFO

    private VBox createInfoPane(Node node) {
    	VBox box = new VBox(5);
    	ObservableList<javafx.scene.Node> boxChildren = box.getChildren();
        Label id = new Label("ID: " + node.getID());
        Label name = new Label(node.getLocationName());
        ObservableMap<String, ObservableMap<Integer, Incident>> ls = node.getIncidents();
        Label incidentHeader = new Label("Incidents");
        name.setStyle("-fx-font-weight: bold;");
        boxChildren.addAll(id, name);
        int total = 0;
        for (String s: ls.keySet()) {
        	int num = ls.get(s).size();
        	Label lb = new Label(s +": " + num);
        	box.getChildren().add(lb);
        	total += num;
        }
        Label lTotal = new Label("total: "+ total);
        Label required = new Label("Required");
        required.setStyle("-fx-font-weight: bold;");
        incidentHeader.setStyle("-fx-font-weight: bold;");
        boxChildren.addAll(lTotal, required);
        
        Map<String, Integer>req = node.getRequiredServices();
        total = 0;
        for (String s: req.keySet()) {
        	int num = req.get(s); 
        	Label lb = new Label(s + ": " + num);
        	boxChildren.add(lb);
        }
        Label rTotal = new Label("Total: "+total);
        
        boxChildren.add(rTotal);
      
        box.setPadding(new Insets(8));
        box.setPrefWidth(160);
        box.setStyle("-fx-border-color: lightgray;");

        if (node.hasIncident()) {
            box.setStyle("-fx-border-color: red; -fx-background-color: #ffecec;");
        }

        return box;
    }

    // SERVICES

    private ScrollPane createServicePane(String type, Node node) {
        VBox content = new VBox(6);
        content.setPadding(new Insets(6));

        Map<Integer, Service> services = node.getServices().get(type);
        
        
        if (services != null) {
        	Label header = new Label(type + " (" + services.size() + ")");
        	content.getChildren().add(header);
            for (Service s : services.values()) {
                VBox card = createServiceCard(s);
                content.getChildren().add(card);
            }	
        }
        else {
        	Label header = new Label(type + " (" + 0 + ")");
        	content.getChildren().add(header);
        }

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setPrefWidth(200);
        sp.setStyle("-fx-border-color: lightgray;");

        return sp;
    }

    private VBox createServiceCard(Service service) {
        Label id = new Label("Service ID: " + service.getID());
        Label status = new Label(service.getAvailability() ? "Available" : "Busy");

        VBox card = new VBox(4, id, status);
        card.setPadding(new Insets(6));
        card.setStyle("-fx-border-color: gray;");
        card.setOnMouseClicked(e -> showServiceDetails(service));

        return card;
    }

    private void showServiceDetails(Service s) {
        detailBlock.getChildren().setAll(
                new Label("SERVICE DETAILS"),
                new Label("ID: " + s.getID()),
                new Label("Available: " + s.getAvailability())
        );
    }


    private ScrollPane createIncidentPane(Node node) {
        VBox content = new VBox(6);
        content.setPadding(new Insets(6));
        int count = 0;
        
        for (Map<Integer, Incident> map : node.getIncidents().values()) {
            for (Incident i : map.values()) {
                VBox card = createIncidentCard(i);
                content.getChildren().add(card);
            }
            count += map.size();
        }
        
        Label lb = new Label("Incidents" + " (" + count +")");
        content.getChildren().add(0, lb);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setPrefWidth(220);
        sp.setStyle("-fx-border-color: lightgray;");
        return sp;
    }
    
    private ScrollPane createOngoingIncidentPane(Node node) {
    	VBox content = new VBox(8);
    	
    	Label header = new Label("Ongoing Incident");
    	content.getChildren().add(header);
    	PriorityQueue<Incident> queue = node.getRunningIncident();
    	for (Incident s: queue) {
    		VBox card = createIncidentCard(s);
            content.getChildren().add(card);
    	}
    		
    	ScrollPane sp = new ScrollPane(content);
    	sp.setFitToWidth(true);
        sp.setPrefWidth(220);
        sp.setStyle("-fx-border-color: lightgray;");
    	return sp;
    }
    
    private VBox createIncidentCard(Incident incident) {
        Label id = new Label("Incident #" + incident.getId());
        Label type = new Label("Type: " + incident.getType());

        VBox card = new VBox(4, id, type);
        card.setPadding(new Insets(6));
        card.setStyle("-fx-border-color: darkred;");

        card.setOnMouseClicked(e -> showIncidentDetails(incident));
        return card;
    }

    private void showIncidentDetails(Incident inc) {
    	HBox hb = new HBox();
    	Map<String, ArrayList<Integer>> map = inc.getDispatchList();
    	for (String s: map.keySet()) {
    		VBox ls = new VBox(8);
    		int count = 0;
    		for (int i: map.get(s)) {
    			ls.getChildren().add(new Label("" + i));
    			count += 1;
    		}
    		ls.getChildren().add(0, new Label(s + " " + count));
    		ScrollPane sp = new ScrollPane(ls);
            sp.setFitToWidth(true);
            sp.setPrefWidth(100);
            sp.setStyle("-fx-border-color: lightgray;");
    		hb.getChildren().add(sp);
    	}
    	detailBlock.getChildren().setAll(
                new Label("INCIDENT DETAILS"),
                new Label("ID: " + inc.getId()),
                new Label("Type: " + inc.getType()),
                new Label("Level: " + inc.getIncidentLevel()),
                hb
                
        );
    	if (inc.getRunning() == true) {
    		Button btn = new Button("Set incident delt with");
    		btn.setOnAction(e -> {
    			env.incidentDelt(inc);
    			buildUI();
    			
    		});
    		detailBlock.getChildren().add(btn);
    	}
    	
        
    }
    
    private HBox createFunctionButton(Node node) {
    	HBox hb = new HBox();
    	VBox makeIncidentRequest = new VBox();
    	Label lb = new Label("Make a new Incident request");
    	makeIncidentRequest.getChildren().add(lb);
    	makeIncidentRequest.setPadding(new Insets(6));
        makeIncidentRequest.setStyle("-fx-border-color: gray;");
        makeIncidentRequest.setOnMouseClicked(e -> showIncidentRequest(node));
        hb.getChildren().add(makeIncidentRequest);
    	return hb;
    }
    
    private void showIncidentRequest(Node node) {
    	Spinner<Integer> level = new Spinner<>(1, 10, 0);
    	Label lb1 = new Label("Incident level: ");
    	Label lb2 = new Label("Incident type: ");
    	level.setEditable(true);
    	level.setPromptText("Select the incident level");
    	Spinner<Integer> type = new Spinner<>(1,3,0);
    	type.setEditable(true);
    	type.setPromptText("Select type");
    	Button btn = new Button("Submit");
    	btn.setOnAction(e -> {
    		env.MakeRequest(type.getValue(), level.getValue(), env.getNodes().get(node.getID()));
    		buildUI();
    	});
    	detailBlock.getChildren().setAll(
    			new Label("Make request"),
    			new HBox(10, lb1, level),
    			new HBox(10, lb2, type),
    			btn
    			);
    }
}
