package application;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import util.Environment;
import util.Police;
import util.Ambulance;
import util.FireTruck;
import util.nodes.Borough;
import util.nodes.CustomDSA.NeighbourNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main extends Application{
	private Environment env = new Environment();
	private final int fps = 4;
	private final long frameInterval = 1_000_000_000L/fps;
	
	private long lastUpdate = 0;
	private long lastUpdateAll = 0;
	
	private VBox leftContent = new VBox(10);
	private VBox detailsContainer = new VBox();
	@Override
	public void init() {
		String locations = "Locations.csv";
		String neighborCSV = "NeighborGraph.csv";
		Map<String, Integer> map = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(locations))){
			String line;
			boolean skipHeader = true;
			int i = 1;
			while((line = br.readLine()) != null) {
				if (skipHeader) {
					skipHeader = false;
					continue;
				}
				line = line.trim();
				Borough New = new Borough(i, line);
				map.put(line, i);
				for (int j = 0; j<3; j++) {
					Police p = new Police(New, env.getNewServiceId());
					env.addService(p);
					FireTruck f = new FireTruck(New, env.getNewServiceId());
					env.addService(f);
					Ambulance a = new Ambulance(New, env.getNewServiceId());
					env.addService(a);
					New.addService(a);
					New.addService(p);
					New.addService(f);
					
				}
				
				env.addNode(i, New);
				i ++;
				
			}
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	
		util.nodes.Node nd = env.getNodes().get(4);
		ObservableMap<String, Integer> mpa = FXCollections.observableHashMap();
		mpa.put(Police.Type, 100);
		mpa.put(Ambulance.Type, 100);
		mpa.put(FireTruck.Type, 100);
		nd.requiredServices = mpa;
		
		try( BufferedReader br = new BufferedReader(new FileReader(neighborCSV))){
			String line;
			boolean skipHeader = true;
			Map<Integer, util.nodes.Node> mp = env.getNodes();
			while((line = br.readLine()) != null) {
				if (skipHeader) {
					skipHeader = false;
					continue;
				}
				String[] parts = line.split(",");
				String l1 = parts[0].trim();
				String l2 = parts[1].trim();
				float dist = Float.parseFloat(parts[2].trim());
				util.nodes.Node nd1 = mp.get(map.get(l1));
				util.nodes.Node nd2 = mp.get(map.get(l2));
				nd1.addNeighbor(new NeighbourNode(nd2, dist));
				nd2.addNeighbor(new NeighbourNode(nd1, dist));
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private void rebuildUI() {
	    leftContent.getChildren().clear();
	    for(util.nodes.Node n: env.getNodes().values()) {			
			VBox card = new NodeCard(n, env, detailsContainer).createNodeCard();
            VBox.setMargin(card, new Insets(6));
            leftContent.getChildren().add(card);
	    }
	}
	
	
	public void start(Stage primaryStage) throws Exception {
		// ENVIRONMENT 
		
		
		//  RIGHT DETAILS CONTAINER 
        
        detailsContainer.setPadding(new Insets(10));
        detailsContainer.setPrefWidth(600);
        detailsContainer.getChildren().add(new NodeDetailPane(null, null));

        // LEFT NODE LIST 
        leftContent.setPadding(new Insets(10));
        	
        rebuildUI();
        
        ScrollPane leftScroll = new ScrollPane(leftContent);
        leftScroll.setFitToWidth(true);

        // CONSOLE
        TextArea console = new TextArea();
        console.setEditable(false);
        console.setPrefHeight(220);
        console.setMinHeight(180);
        console.setMaxWidth(Double.MAX_VALUE);
        console.setStyle("-fx-font-family: monospace;");

        System.setOut(new PrintStream(new Console(console), true));
        System.setErr(new PrintStream(new Console(console), true));

        VBox leftColumn = new VBox(5, leftScroll, console);
        leftColumn.setFillWidth(true);
        VBox.setVgrow(leftScroll, Priority.ALWAYS);
        
        // root
        HBox root = new HBox(10, leftColumn, detailsContainer);
        root.setPadding(new Insets(10));

        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        HBox.setHgrow(detailsContainer, Priority.ALWAYS);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Security System");
        primaryStage.show();		
        loop();
	}
	
	public static void main(String[] args) {
		
		launch(args);
		
	}
	
	public void loop() {
		AnimationTimer timer = new AnimationTimer() {
			@Override
            public void handle(long now) {
				if (lastUpdate == 0) {
                    lastUpdate = now;
                    lastUpdateAll = now;
                    return;
				}
                if (now - lastUpdate < frameInterval) return;                
                
                lastUpdate = now;
                
                if ((now-lastUpdateAll) >= 10_000_000_000l) {
                	lastUpdateAll = now;
                	env.update(true);
                	return;
                }
                env.update(false);
                
            }
		};
		timer.start();
	}
	
}
	