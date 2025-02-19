package org.example.project1;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.function.Consumer;
import javafx.scene.shape.Line;
import java.util.*;
import javafx.stage.Popup;



import static org.example.project1.IntervalGraph.isIntervalGraph;

public class GraphRenderer {
    public static final Map<String, Color> colorMap = new HashMap<>();
    private static final List<String> colorOrder = new ArrayList<>();
    private Map<Circle, EventHandler<MouseEvent>> hintClickHandlers = new HashMap<>();
    private ComboBox<String> currentColorPicker = null;
    private Consumer<Vertex> vertexSelectedCallback;

    private Map<Vertex, ComboBox<String>> vertexColorPickers = new HashMap<>();
    private int chromaticNumber=-1;
    private String algorithmUsed="Unknown";


    public String getAlgorithmUsed(){
        return algorithmUsed;
    }

    public int getChromaticNumber(){
        return chromaticNumber;
    }



    protected void onWinCondition() {

    }

    static {
        colorMap.put("Brown", Color.BROWN);
        colorMap.put("Gold", Color.GOLD);
        colorMap.put("Red", Color.RED);
        colorMap.put("Green", Color.GREEN);
        colorMap.put("Blue", Color.BLUE);
        colorMap.put("Yellow", Color.YELLOW);
        colorMap.put("Orange", Color.ORANGE);
        colorMap.put("Purple", Color.PURPLE);
        colorMap.put("Pink", Color.PINK);
        colorMap.put("Gray", Color.GRAY);
        colorMap.put("Black", Color.BLACK);
        colorMap.put("White", Color.WHITE);
        colorMap.put("Cyan", Color.CYAN);
        colorMap.put("Magenta", Color.MAGENTA);
        colorMap.put("Violet", Color.VIOLET);
        colorMap.put("Indigo", Color.INDIGO);
        colorMap.put("Silver", Color.SILVER);
        colorMap.put("Teal", Color.TEAL);
        colorMap.put("Lime", Color.LIME);
        colorOrder.addAll(colorMap.keySet());
    }

    private Pane graphPane;
    private int gameMode;
    private Stack<Vertex> colorHistory = new Stack<>();
    private List<Vertex> vertexOrder;
    private int currentVertexIndex = 0;
    private Map<Vertex, Circle> vertexCircleMap = new HashMap<>();
    private List<Edge> edges;
    private Set<Vertex> verticesWithActiveColorPicker = new HashSet<>();
    private List<Vertex> vertices;

    public GraphRenderer(Pane graphPane, int gameMode, Button scoreButton) {
        this.graphPane = graphPane;
        this.gameMode = gameMode;
    }

    public void renderGraph(List<Vertex> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;

        applyForceDirectedLayout(vertices, edges);

        graphPane.getChildren().clear();
        vertexCircleMap.clear();
        currentVertexIndex = 0;
        colorHistory.clear();
        vertexColorPickers.clear();
        verticesWithActiveColorPicker.clear();

        Map<Edge, Line> edgeLineMap = new HashMap<>();


        for (Edge edge : edges) {
            Vertex v1 = edge.getVertex1();
            Vertex v2 = edge.getVertex2();
            Line line = new Line(v1.getX(), v1.getY(), v2.getX(), v2.getY());
            line.setStroke(Color.BLACK);
            graphPane.getChildren().add(line);
            edgeLineMap.put(edge, line);
        }

        if (gameMode == GraphApp.RANDOM_ORDER || gameMode == GraphApp.I_CHANGED_MY_MIND) {
            vertexOrder = new ArrayList<>(vertices);
            Collections.shuffle(vertexOrder);
            currentVertexIndex = 0;
        }


        for (Vertex vertex : vertices) {
            double radius = Math.max(5, Math.min(20, 600 / vertices.size()));
            Circle circle = new Circle(vertex.getX(), vertex.getY(), radius);
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.BLACK);
            graphPane.getChildren().add(circle);
            vertexCircleMap.put(vertex, circle);

            if (gameMode == GraphApp.TO_THE_BITTER_END) {
                setupToTheBitterEnd(circle, vertex);
            } else {
                setupRandomOrderModes(circle, vertex);
            }

            setupDragFunctionality(circle, vertex, edgeLineMap);
        }

        if (gameMode == GraphApp.RANDOM_ORDER || gameMode == GraphApp.I_CHANGED_MY_MIND) {
            highlightCurrentVertex();
        }
    }

    private Vertex selectedVertex = null;

    public Vertex getSelectedVertex() {
        return selectedVertex;
    }

    public void enableVertexSelection(boolean enable) {
        for (Map.Entry<Vertex, Circle> entry : vertexCircleMap.entrySet()) {
            Circle circle = entry.getValue();
            Vertex vertex = entry.getKey();

            if (enable) {
                circle.setOnMouseClicked(event -> {
                    if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                        selectedVertex = vertex;
                        circle.setStroke(Color.RED);

                        // Trigger the callback if set
                        if (vertexSelectedCallback != null) {
                            vertexSelectedCallback.accept(vertex);
                        }
                    }
                });
            } else {
                circle.setOnMouseClicked(null); // Remove the mouse click event handler
                circle.setStroke(Color.BLACK);  // Reset the stroke color
            }
        }
    }

    public void resetSelectedVertex() {
        selectedVertex = null;
    }

    public void setOnVertexSelected(Consumer<Vertex> callback) {
        this.vertexSelectedCallback = callback;
    }

    private void setupDragFunctionality(Circle circle, Vertex vertex, Map<Edge, Line> edgeLineMap) {
        circle.setOnMousePressed(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                boolean canDrag = true;
                if (gameMode != GraphApp.TO_THE_BITTER_END) {
                    canDrag = vertex == getCurrentVertex();
                }

                if (verticesWithActiveColorPicker.contains(vertex) || !canDrag) {
                    e.consume();
                    return;
                }
                circle.setUserData(new double[]{e.getSceneX(), e.getSceneY()});
            }
        });

        circle.setOnMouseDragged(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                if (verticesWithActiveColorPicker.contains(vertex)) {
                    e.consume();
                    return;
                }

                double[] previousCoordinates = (double[]) circle.getUserData();
                double offsetX = e.getSceneX() - previousCoordinates[0];
                double offsetY = e.getSceneY() - previousCoordinates[1];
                circle.setCenterX(circle.getCenterX() + offsetX);
                circle.setCenterY(circle.getCenterY() + offsetY);
                vertex.x = circle.getCenterX();
                vertex.y = circle.getCenterY();

                for (Edge edge : edges) {
                    if (edge.getVertex1() == vertex || edge.getVertex2() == vertex) {
                        Line line = edgeLineMap.get(edge);
                        if (line != null) {
                            line.setStartX(edge.getVertex1().getX());
                            line.setStartY(edge.getVertex1().getY());
                            line.setEndX(edge.getVertex2().getX());
                            line.setEndY(edge.getVertex2().getY());
                        }
                    }
                }
                circle.setUserData(new double[]{e.getSceneX(), e.getSceneY()});
            }
        });
    }

    private void setupToTheBitterEnd(Circle circle, Vertex vertex) {
        circle.setOnMouseClicked(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                if (e.isShiftDown()) {
                    circle.setFill(Color.WHITE); // Un-color the vertex
                } else {
                    showColorPicker(circle, vertex); // Show color picker
                }
            }
        });
    }

    private void setupRandomOrderModes(Circle circle, Vertex vertex) {
        circle.setDisable(true);
        circle.setOnMouseClicked(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY && !circle.isDisable()) {
                showColorPicker(circle, vertex);
            }
        });
    }

    //this part is the color wheel
    public void showColorWheel(Circle circle,Vertex vertex) {
        //creating pop-up for color wheel
        Popup popup = new Popup();
        ColorWheelPicker colorWheel=new ColorWheelPicker(300,300,150);



        //we handle the color selection
        colorWheel.getView().setOnMouseClicked(event->{
            Color selectedColor=colorWheel.getColor(event);
            if(selectedColor!=null) {
                if (isColorValid(circle, selectedColor)) {
                    circle.setFill(selectedColor);
                    popup.hide();//popup closes after user selects a color

                    //updating the combo box with the color code from the color wheel

                    final ComboBox<String> colorComboBox = vertexColorPickers.computeIfAbsent(vertex, v -> {
                        System.out.println("ComboBox not found for Vertex: " + v.getId() + ". Creating a new one.");
                        return new ComboBox<>();
                    });

                    String colorCode = toHexCode(selectedColor);

                    //Adding the custom color to colorMap and colorOrder if it's not already there
                    if (!colorMap.containsKey(colorCode)) {
                        colorMap.put(colorCode, selectedColor);
                        colorOrder.add(colorCode);
                    }
                    Platform.runLater(() -> {
                        if (!colorComboBox.getItems().contains(colorCode)) {
                            colorComboBox.getItems().add(colorCode);
                        }
                        colorComboBox.setValue(colorCode);//set the combobox to the selected color code
                        System.out.println("ComboBox updated for vertex: " + vertex.getId());
                    });

                    //we are updating the game state
                    updateVertexColorState(vertex, selectedColor);

                    //checking for winning condition
                    if (gameMode == GraphApp.TO_THE_BITTER_END || gameMode == GraphApp.RANDOM_ORDER || gameMode == GraphApp.I_CHANGED_MY_MIND) {
                        if (areAllVerticesColored()) {
                            onWinCondition();
                        } else if (gameMode == GraphApp.I_CHANGED_MY_MIND || gameMode == GraphApp.RANDOM_ORDER) {
                            moveToNextVertex();
                        }
                    }

                } else {
                    showError("Invalid color", "Adjacent vertices cannot have the same color.");
                }
            }

        });


        popup.getContent().add(colorWheel.getView());
        popup.setAutoHide(true);//close automatically when user clickes somewhere else

        //showing popup near the vertex
        popup.show(graphPane.getScene().getWindow(), circle.getCenterX()+graphPane.getLayoutX(),circle.getCenterY()+graphPane.getLayoutY()+graphPane.getLayoutY());
    }

    private void updateVertexColorState(Vertex vertex,Color color){
        //updating vertex's color state
        vertexColorPickers.remove(vertex);
        verticesWithActiveColorPicker.remove(vertex);

        //pushing to color history
        if(gameMode==GraphApp.RANDOM_ORDER ||gameMode==GraphApp.I_CHANGED_MY_MIND){
            colorHistory.push(vertex);
        }
    }

    private String toHexCode(Color color) {
        int r=(int) (color.getRed()*255);
        int g=(int) (color.getGreen()*255);
        int b=(int) (color.getBlue()*255);
        return String.format("#%02x%02x%02x", r,g,b);//generatuing the hex code
    }


    private void highlightCurrentVertex() {
        if (currentVertexIndex < vertexOrder.size()) {
            Vertex currentVertex = vertexOrder.get(currentVertexIndex);
            Circle currentCircle = vertexCircleMap.get(currentVertex);
            currentCircle.setDisable(false);
            currentCircle.setStroke(Color.RED);

            //allowing combobox interaction for the current vertex
            currentCircle.setOnMouseClicked(e ->{
                if(e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                    showColorPicker(currentCircle,currentVertex);
                }
            });
        }
    }

    private void showColorPicker(Circle circle, Vertex vertex) {
        if (vertexColorPickers.containsKey(vertex)) {
            return;
        }


        //creating combobox for fixed colors

        ComboBox<String> colorComboBox = new ComboBox<>();
        colorComboBox.getItems().addAll(colorOrder);
        colorComboBox.getItems().add("Custom color");//adding options for custom colors
        colorComboBox.setLayoutX(circle.getCenterX());
        colorComboBox.setLayoutY(circle.getCenterY());
        graphPane.getChildren().add(colorComboBox);
        vertexColorPickers.put(vertex, colorComboBox);
        currentColorPicker = colorComboBox;


        System.out.println("ComboBox created for vertex " + vertex.getId());

        colorComboBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                graphPane.getChildren().remove(colorComboBox);
                currentColorPicker = null;
                vertexColorPickers.remove(vertex);
                verticesWithActiveColorPicker.remove(vertex);
            }
        });

        colorComboBox.setOnAction(event -> {
            String selectedColor = colorComboBox.getValue();
            if (selectedColor != null) {
                if (selectedColor.equals("Custom color")) {
                    //opening color wheel if "custom color" is selected
                    graphPane.getChildren().remove(colorComboBox);
                    currentColorPicker = null;
                    vertexColorPickers.remove(vertex);
                    verticesWithActiveColorPicker.remove(vertex);
                    showColorWheel(circle, vertex);
                } else if (colorMap.containsKey(selectedColor)) {
                    //use the selected fixed colot
                    Color selectedColorValue = colorMap.get(selectedColor);


                    if (isColorValid(vertex, selectedColorValue)) {
                        circle.setFill(selectedColorValue);
                        if (gameMode == GraphApp.RANDOM_ORDER || gameMode == GraphApp.I_CHANGED_MY_MIND) {
                            colorHistory.push(vertex);
                            moveToNextVertex();
                        }

                        if (areAllVerticesColored()) {
                            onWinCondition();
                        }
                    } else {
                        GraphApp app = (GraphApp) graphPane.getScene().getWindow().getUserData();
                        if (app != null) {
                            app.incrementMistakes();
                        }
                        showError("Invalid Color Selection", "Adjacent vertices cannot have the same color.");
                        circle.setFill(Color.WHITE);
                    }
                }

                Platform.runLater(() -> {
                    if (!colorComboBox.getItems().contains(selectedColor)) {
                        colorComboBox.getItems().add(selectedColor);
                        System.out.println("Color added to ComboBox for vertex: " + vertex.getId());
                    }
                    colorComboBox.setValue(selectedColor);
                    System.out.println("ComboBox updated for vertex: " + vertex.getId() + " with color: " + selectedColor);
                });


                graphPane.getChildren().remove(colorComboBox);
                currentColorPicker = null;
                vertexColorPickers.remove(vertex);
                verticesWithActiveColorPicker.remove(vertex);
            }
        });
    }



        private boolean areAllVerticesColored() {
        for (Circle circle : vertexCircleMap.values()) {
            if (circle.getFill().equals(Color.WHITE)) {
                return false;
            }
        }
        return true;
    }

    private void moveToNextVertex() {
        if (currentVertexIndex < vertexOrder.size()) {
            Vertex currentVertex = vertexOrder.get(currentVertexIndex);
            Circle currentCircle = vertexCircleMap.get(currentVertex);
            currentCircle.setStroke(Color.BLACK);
            currentCircle.setDisable(true);

            currentVertexIndex++;
            if (currentVertexIndex < vertexOrder.size()) {
                highlightCurrentVertex();
            }
        }
    }

    private boolean isColorValid(Circle circle, Color color) {
        Vertex vertex=null;
        for(Map.Entry<Vertex,Circle> entry: vertexCircleMap.entrySet()){
            if(entry.getValue()==circle){
                vertex=entry.getKey();
                break;
            }
        }

        return (vertex!=null) && isColorValid(vertex,color);
    }

    private boolean isColorValid(Vertex vertex, Color color) {
        for (Edge edge : edges) {
            Vertex adjacentVertex = null;
            if (edge.getVertex1() == vertex) adjacentVertex = edge.getVertex2();
            else if (edge.getVertex2() == vertex) adjacentVertex = edge.getVertex1();

            if (adjacentVertex != null) {
                Circle adjacentCircle = vertexCircleMap.get(adjacentVertex);
                if (adjacentCircle != null && adjacentCircle.getFill().equals(color)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void undoLastColoring() {
        if (!colorHistory.isEmpty()) {
            currentVertexIndex--;

            Vertex lastVertex = colorHistory.pop();
            Circle lastCircle = vertexCircleMap.get(lastVertex);
            lastCircle.setFill(Color.WHITE);
            lastCircle.setDisable(false);
            lastCircle.setStroke(Color.RED);

            if (currentVertexIndex + 1 < vertexOrder.size()) {
                Vertex nextVertex = vertexOrder.get(currentVertexIndex + 1);
                Circle nextCircle = vertexCircleMap.get(nextVertex);
                nextCircle.setDisable(true);
                nextCircle.setStroke(Color.BLACK);
            }
        } else {
            showInfo("Undo", "No actions to undo.");
        }
    }

    private void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public Vertex getCurrentVertex() {
        if (currentVertexIndex < vertexOrder.size()) {
            return vertexOrder.get(currentVertexIndex);
        }
        return null;
    }

    public boolean hasMoreVerticesToColor() {
        return currentVertexIndex < vertexOrder.size();
    }

    public List<Color> getValidColorsForVertex(Vertex vertex) {
        List<Color> validColors = new ArrayList<>();
        for (String colorName : colorOrder) {
            Color color = colorMap.get(colorName);
            if (isColorValid(vertex, color)) {
                validColors.add(color);
            }
        }
        return validColors;
    }

    public Color getVertexColor(Vertex vertex) {
        Circle circle = vertexCircleMap.get(vertex);
        return circle != null ? (Color)circle.getFill() : Color.WHITE;
    }

    public void resetState() {
        colorHistory.clear();
        currentVertexIndex = 0;
    }

    public Map<Vertex, Circle> getVertexCircleMap() {
        return vertexCircleMap;
    }

    public static class Vertex {
        private double radius;
        private double x;
        private double y;
        private int id;

        public Vertex(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.radius = 15;
        }

        public double getX() { return x; }
        public double getY() { return y; }
        public int getId() { return id; }
        public double getRadius() { return radius; }
        public void setRadius(double radius) { this.radius = radius; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return id == vertex.id;
        }

        @Override
        public int hashCode() { return id; }
    }

    public static class Edge {
        private Vertex vertex1;
        private Vertex vertex2;

        public Edge(Vertex vertex1, Vertex vertex2) {
            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
        }

        public Vertex getVertex1() { return vertex1; }
        public Vertex getVertex2() { return vertex2; }
    }

    public static List<Integer>[] toAdjacencyList(List<Vertex> vertices, List<Edge> edges) {
        List<Integer>[] adjacencyList = new ArrayList[vertices.size() + 1];
        for (int i = 1; i <= vertices.size(); i++) {
            adjacencyList[i] = new ArrayList<>();
        }

        for (Edge edge : edges) {
            int u = edge.getVertex1().getId();
            int v = edge.getVertex2().getId();

            adjacencyList[u].add(v);
            adjacencyList[v].add(u);
        }

        return adjacencyList;
    }

    private void applyForceDirectedLayout(List<Vertex> vertices, List<Edge> edges) {
        double width = graphPane.getPrefWidth();
        double height = graphPane.getPrefHeight();
        double area = width * height;
        double k = Math.sqrt(area / vertices.size());

        double repulsion = 500;
        double attraction = 0.001;

        Map<Vertex, double[]> displacements = new HashMap<>();
        for (Vertex vertex : vertices) {
            displacements.put(vertex, new double[]{0, 0});
        }

        int iterations = 500;
        for (int iter = 0; iter < iterations; iter++) {
            for (Vertex v1 : vertices) {
                double[] disp = displacements.get(v1);
                disp[0] = 0;
                disp[1] = 0;

                for (Vertex v2 : vertices) {
                    if (v1 == v2) continue;

                    double dx = v1.getX() - v2.getX();
                    double dy = v1.getY() - v2.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy) + 0.01; // Avoid division by zero
                    double force = repulsion / (distance * distance);

                    disp[0] += (dx / distance) * force;
                    disp[1] += (dy / distance) * force;
                }
            }

            for (Edge edge : edges) {
                Vertex v1 = edge.getVertex1();
                Vertex v2 = edge.getVertex2();

                double dx = v1.getX() - v2.getX();
                double dy = v1.getY() - v2.getY();
                double distance = Math.sqrt(dx * dx + dy * dy) + 0.01; //Avoid division by zero
                double force = attraction * (distance * distance) / k;

                displacements.get(v1)[0] -= (dx / distance) * force;
                displacements.get(v1)[1] -= (dy / distance) * force;
                displacements.get(v2)[0] += (dx / distance) * force;
                displacements.get(v2)[1] += (dy / distance) * force;
            }

            for (Vertex vertex : vertices) {
                double[] disp = displacements.get(vertex);
                double dispLength = Math.sqrt(disp[0] * disp[0] + disp[1] * disp[1]) + 0.01; //Avoid division by zero
                vertex.x += (disp[0] / dispLength) * Math.min(dispLength, k);
                vertex.y += (disp[1] / dispLength) * Math.min(dispLength, k);

                vertex.x = Math.max(50, Math.min(vertex.x, graphPane.getPrefWidth() - 50));
                vertex.y = Math.max(50, Math.min(vertex.y, graphPane.getPrefHeight() - 50));
            }
        }
    }

    int calculateChromaticNumber(List<Vertex> vertices, List<Edge> edges){
        int numV=vertices.size();
        int[][] adjacencyMatrix=adjacencyMatrix(vertices,edges);

        //for interval graph
        List<IntervalGraph.Interval> intervals = new ArrayList<>();
        List<ColEdge> colEdges = convertEdgesToColEdges(edges);

        //converting each vertex into an interval
        for(Vertex v:vertices){
            int start=(int)(Math.random()*50)+1;
            int end=start+(int)(Math.random()*10)+1;
            intervals.add(new IntervalGraph.Interval(start,end,v.getId()));


        }

        //calling the intervalGraph method
        boolean isInterval=isIntervalGraph(intervals);


        //checking graph type and calcualting their chromatic number
        if(BipartiteGraph.isBipartite(adjacencyMatrix,vertices.size())){
            chromaticNumber= 2;//bipartite graphs always have a chromatic number of 2
            algorithmUsed="Bipartite (2-coloring";
        } else if(CycleGraph.isCycleGraph(edges,vertices.size())){
            chromaticNumber= CycleGraph.chromaticNumber(vertices.size());
            algorithmUsed="Cycle Graph Check";
        }else if(PlanarGraph.isPlanar(adjacencyMatrix)){
           chromaticNumber= PlanarGraph.PlanarChromaticNumber(adjacencyMatrix);
           algorithmUsed="Planar Graph Check";
        }else if(ChordalGraph.isChordal(adjacencyMatrix)){
            return ChordalGraph.ChordalChromaticNum(adjacencyMatrix);

        }else  if(TreeGraph.isTreeGraph(adjacencyMatrix,vertices.size())){
            return 2;//tree graphs have a chromatic number of 2
        }else if(isInterval){
            chromaticNumber=IntervalGraph.calculateChromaticNumber(intervals);
            algorithmUsed="Interval Graph Check";
        }else if (CompleteGraph.isCompleteGraph(vertices,edges)){
            chromaticNumber=CompleteGraph.CompleteGraphChromaticNumber(vertices,edges);
            algorithmUsed="Complete Graph Check";
        }else{
            //using the general chromatic number algorithm for other graphs
            List<Integer>[] connectionList=toAdjacencyList(vertices,edges);
            int[][] connectionAndVertices=new int[numV][2];

            //preparing vertex degrees or sorting
            for(int i=1;i<=numV;i++){
                connectionAndVertices[i-1][0]=connectionList[i].size();
                connectionAndVertices[i-1][1]=i;
            }

            //sorting vertices by degree
            Arrays.sort(connectionAndVertices,(a,b)->Integer.compare(b[0],a[0]));

            int[] colors=new int[numV+1];

            //we start with greedy algorithnm
            chromaticNumber=ChromaticNumber.greedyColoring(connectionList,numV);
            algorithmUsed="Greedy Algorithm";

            //checking if upper and lower bounds confirm the chromatic number
            int upperBound=ChromaticNumber.calculateUpperBound(numV,colEdges.toArray(new ColEdge[0]));
            int lowerBound=ChromaticNumber.lowerbounds(numV,colEdges.toArray(new ColEdge[0]));


            if(chromaticNumber>lowerBound && chromaticNumber<upperBound){
                //if this is not satisfied ,we run backtracking
                chromaticNumber=ChromaticNumber.findChromaticNumber(connectionList,colors,connectionAndVertices,numV);
                algorithmUsed="Chromatic Number";
            }
        }

        this.chromaticNumber = chromaticNumber; //Storing the result
        this.algorithmUsed = algorithmUsed; //Storing the algorithm name
        return chromaticNumber;
    }

    private List<ColEdge> convertEdgesToColEdges(List<Edge> edges){
        List<ColEdge> colEdges=new ArrayList<>();
        for(Edge e:edges){
            ColEdge colEdge=new ColEdge();
            colEdge.u=e.getVertex1().getId();
            colEdge.v=e.getVertex2().getId();
            colEdges.add(colEdge);
        }
        return colEdges;
    }

    private int[][] adjacencyMatrix(List<Vertex> vertices ,List<Edge> edges){
        int numV=vertices.size();
        int[][] adjacencyMatrix=new int[numV][numV];

        //pppulating the adjacency matrix using the edges
        for(Edge edge:edges){
            int u=edge.getVertex1().getId()-1;
            int v=edge.getVertex2().getId()-1;
            adjacencyMatrix[u][v]=1;
            adjacencyMatrix[v][u]=1;
        }

        return adjacencyMatrix;
    }

}