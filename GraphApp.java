package org.example.project1;

import java.awt.Desktop;
import java.net.URI;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.io.File;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import java.util.*;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import javafx.util.Duration;

public class GraphApp extends Application {
    private File selectedFile;
    private GraphRenderer renderer;
    private List<GraphRenderer.Vertex> vertices = new ArrayList<>();
    private List<GraphRenderer.Edge> edges = new ArrayList<>();
    private Scene menuScene;

    public static final int TO_THE_BITTER_END = 1;
    public static final int RANDOM_ORDER = 2;
    public static final int I_CHANGED_MY_MIND = 3;
    private int currentGameMode = TO_THE_BITTER_END;

    private int timeSpent = 0;
    private int hintsUsed = 0;
    private int mistakes = 0;
    private int chromaticNumber = 0;
    private int colorsUsed = 0;
    private static final int MAX_SCORE = 1000;
    private static final int TIME_PENALTY = 2;
    private static final int HINT_PENALTY = 50;
    private static final int MISTAKE_PENALTY = 25;
    private static final int COLOR_EFFICIENCY_BONUS = 100;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Text title = new Text("CHROMATIC NUMBERS");
        title.setFont(Font.font("Montserrat", 50));
        title.setFill(Color.BLACK);

        Text gamePageTitle = new Text("GAMEMODES");
        gamePageTitle.setFont(Font.font("Montserrat", 60));
        gamePageTitle.setFill(Color.BLACK);

        Button playButton = new Button("PLAY");
        styleButton(playButton);

        Button firstGame = new Button("To The Bitter End");
        styleButton(firstGame);

        Button secondGame = new Button("Random Order");
        styleButton(secondGame);

        Button thirdGame = new Button("I Changed My Mind");
        styleButton(thirdGame);

        VBox vbox = new VBox(40, title, playButton);
        vbox.setAlignment(Pos.CENTER);

        VBox gameVBox = new VBox(30, gamePageTitle, thirdGame, firstGame, secondGame);
        gameVBox.setAlignment(Pos.CENTER);

        StackPane mainRoot = new StackPane();
        mainRoot.setStyle("-fx-background-color: white;");
        mainRoot.getChildren().add(vbox);

        StackPane gameRoots = new StackPane();
        gameRoots.setStyle("-fx-background-color: white;");
        gameRoots.getChildren().add(gameVBox);

        menuScene = new Scene(mainRoot, 1080, 940);
        Scene gamePage = new Scene(gameRoots, 1080, 940);

        primaryStage.setTitle("Graph Game");
        primaryStage.setScene(menuScene);
        primaryStage.show();

        playButton.setOnMousePressed(e -> primaryStage.setScene(gamePage));

        firstGame.setOnMousePressed(e -> {
            currentGameMode = TO_THE_BITTER_END;
            primaryStage.setScene(createGraphPaneScene(primaryStage, gamePage));
        });

        secondGame.setOnMousePressed(e -> {
            currentGameMode = RANDOM_ORDER;
            primaryStage.setScene(createGraphPaneScene(primaryStage, gamePage));
        });

        thirdGame.setOnMousePressed(e -> {
            currentGameMode = I_CHANGED_MY_MIND;
            primaryStage.setScene(createGraphPaneScene(primaryStage, gamePage));
        });

        firstGame.setOnMouseEntered(e -> createHoverFx(firstGame, true, false));
        firstGame.setOnMouseExited(e -> createHoverFx(firstGame, false, true));
        secondGame.setOnMouseEntered(e -> createHoverFx(secondGame, true, false));
        secondGame.setOnMouseExited(e -> createHoverFx(secondGame, false, true));
        thirdGame.setOnMouseEntered(e -> createHoverFx(thirdGame, true, false));
        thirdGame.setOnMouseExited(e -> createHoverFx(thirdGame, false, true));
        playButton.setOnMouseEntered(e -> createHoverFx(playButton, true, false));
        playButton.setOnMouseExited(e -> createHoverFx(playButton, false, true));
    }

    private Scene createGraphPaneScene(Stage primaryStage, Scene gameModesScene) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #010b13;");

        Pane graphPane = new Pane();
        graphPane.setPrefSize(600, 400);
        graphPane.setStyle("-fx-background-color: white; -fx-border-color: black;");
        mainLayout.setCenter(graphPane);

        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setStyle("-fx-background-color: black; -fx-padding: 10;");

        Label graphTitle = new Label("Graph Viewer");
        graphTitle.setFont(Font.font("Arial", 20));
        graphTitle.setStyle("-fx-text-fill: white;");

        Label timerLabel = new Label("Time: 0");
        timerLabel.setId("timerLabel");
        timerLabel.setFont(Font.font("Arial", 15));
        timerLabel.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 10;");

        Region timerSpacer = new Region();
        timerSpacer.setMinWidth(20);

        Button resetButton = new Button("Reset");
        Button returnButton = new Button("Return");
        Button hintButton = new Button("Hint");
        Button undoButton = new Button("↩");
        Button solveForMeButton = new Button("Solve for Me");
        Button solutionButton = new Button("Solution");

        resetButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        returnButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        hintButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        solutionButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        undoButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; " +
                        "-fx-font-size: 28px; -fx-border-color: transparent; -fx-padding: 5;"
        );
        solveForMeButton.setFont(Font.font("Arial", 10));
        solveForMeButton.setStyle(
                "-fx-background-color: black; -fx-text-fill: white; -fx-padding: 5 10; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-width: 0.5;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (currentGameMode == I_CHANGED_MY_MIND) {
            topBox.getChildren().addAll(graphTitle, timerSpacer, timerLabel, resetButton,
                    returnButton, hintButton, solutionButton, undoButton, spacer, solveForMeButton);
        } else {
            topBox.getChildren().addAll(graphTitle, timerSpacer, timerLabel, resetButton,
                    returnButton, hintButton, solutionButton, spacer, solveForMeButton);
        }

        mainLayout.setTop(topBox);

        VBox rightMenu = new VBox(10);
        rightMenu.setAlignment(Pos.TOP_CENTER);
        rightMenu.setStyle("-fx-padding: 20; -fx-background-color: black;");

        Text instructions = new Text(getInstructionsForMode());
        instructions.setFill(Color.WHITE);
        instructions.setFont(Font.font("Arial", 14));

        TextFlow textFlow = new TextFlow(instructions);
        textFlow.setPrefWidth(100);
        textFlow.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 10;");

        Button selectFileButton = new Button("Select File");
        styleButton(selectFileButton);

        Button loadRandomGraphButton = new Button("Load Random Graph");
        styleButton(loadRandomGraphButton);

        TextField verticesInput = new TextField();
        verticesInput.setPromptText("Vertices");
        TextField edgesInput = new TextField();
        edgesInput.setPromptText("Edges");

        rightMenu.getChildren().addAll(verticesInput, edgesInput, loadRandomGraphButton,
                selectFileButton, textFlow);
        mainLayout.setRight(rightMenu);

        renderer = new GraphRenderer(graphPane, currentGameMode, null) {
            @Override
            protected void onWinCondition() {
                primaryStage.setScene(createWinningScreen(primaryStage));
            }
        };
        primaryStage.getScene().getWindow().setUserData(this);
        resetGameState();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> {
            int currentSeconds = Integer.parseInt(timerLabel.getText().split(": ")[1]);
            timerLabel.setText("Time: " + (currentSeconds + 1));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        loadRandomGraphButton.setOnAction(event -> {
            try {
                int numVertices = Integer.parseInt(verticesInput.getText());
                int numEdges = Integer.parseInt(edgesInput.getText());
                generateRandomGraph(numVertices, numEdges, graphPane);
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid Input", "Please enter valid numbers for vertices and edges.");
            }
        });

        selectFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Graph File");
            selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                try {
                    displayGraphFromFile(graphPane);
                } catch (IOException ex) {
                    showErrorDialog("Error", "Unable to load graph from file.");
                }
            }
        });

        resetButton.setOnAction(event -> {
            graphPane.getChildren().clear();
            timerLabel.setText("Time: 0");
        });

        returnButton.setOnAction(event -> primaryStage.setScene(gameModesScene));
        undoButton.setOnAction(event -> renderer.undoLastColoring());
        solveForMeButton.setOnAction(event ->
                openYouTubeLink("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));

        solutionButton.setOnAction(event -> {
            if (vertices.isEmpty() || edges.isEmpty()) {
                showErrorDialog("Error", "No graph loaded. Please load a graph first.");
                return;
            }
            solveGraph(graphPane);
            showLosingScreenAfterDelay(primaryStage);
        });

        hintButton.setOnAction(event -> {
            if (currentGameMode == TO_THE_BITTER_END) {
                renderer.enableVertexSelection(true); // Enable vertex selection for hint
                showHintDialog("Hint", "Please select a vertex to get a hint.");

                renderer.setOnVertexSelected(vertex -> {
                    if (vertex != null) {
                        List<Color> possibleColors = renderer.getValidColorsForVertex(vertex);
                        if (!possibleColors.isEmpty()) {
                            String colorName = getColorName(possibleColors.get(0));
                            showHintDialog("Hint", "Suggested color for Vertex " + vertex.getId() + ": " + colorName);
                        } else {
                            showHintDialog("Hint", "No valid colors available for Vertex " + vertex.getId());
                        }

                        // Disable vertex selection after providing the hint
                        renderer.enableVertexSelection(false);
                        renderer.resetSelectedVertex();
                    }
                });
            } else {
                if (renderer.hasMoreVerticesToColor()) {
                    GraphRenderer.Vertex currentVertex = renderer.getCurrentVertex();
                    List<Color> possibleColors = renderer.getValidColorsForVertex(currentVertex);
                    if (!possibleColors.isEmpty()) {
                        String colorName = getColorName(possibleColors.get(0));
                        showHintDialog("Hint", "Suggested color for Vertex " + currentVertex.getId() + ": " + colorName);
                    } else {
                        showHintDialog("Hint", "No valid colors available for Vertex " + currentVertex.getId());
                    }
                } else {
                    showHintDialog("Hint", "No more vertices to color.");
                }
            }
        });

        resetButton.setOnMouseEntered(e -> createHoverFx(resetButton, true, false));
        resetButton.setOnMouseExited(e -> createHoverFx(resetButton, false, true));
        hintButton.setOnMouseEntered(e -> createHoverFx(hintButton, true, false));
        hintButton.setOnMouseExited(e -> createHoverFx(hintButton, false, true));
        returnButton.setOnMouseEntered(e -> createHoverFx(returnButton, true, false));
        returnButton.setOnMouseExited(e -> createHoverFx(returnButton, false, true));
        selectFileButton.setOnMouseEntered(e -> createHoverFx(selectFileButton, true, false));
        selectFileButton.setOnMouseExited(e -> createHoverFx(selectFileButton, false, true));
        loadRandomGraphButton.setOnMouseEntered(e -> createHoverFx(loadRandomGraphButton, true, false));
        loadRandomGraphButton.setOnMouseExited(e -> createHoverFx(loadRandomGraphButton, false, true));
        undoButton.setOnMouseEntered(e -> createHoverFx(undoButton, true, false));
        undoButton.setOnMouseExited(e -> createHoverFx(undoButton, false, true));
        solutionButton.setOnMouseEntered(e -> createHoverFx(solutionButton, true, false));
        solutionButton.setOnMouseExited(e -> createHoverFx(solutionButton, false, true));


        return new Scene(mainLayout, 1080, 970);
    }

    private void resetScore() {
        timeSpent = 0;
        hintsUsed = 0;
        mistakes = 0;
        chromaticNumber = 0;
        colorsUsed = 0;
    }

    public void incrementHints() {
        hintsUsed++;
    }

    public void incrementMistakes() {
        mistakes++;
    }

    private int calculateScore() {
        int score = MAX_SCORE;
        score -= timeSpent * TIME_PENALTY;
        score -= hintsUsed * HINT_PENALTY;
        score -= mistakes * MISTAKE_PENALTY;

        if (chromaticNumber > 0 && colorsUsed == chromaticNumber) {
            score += COLOR_EFFICIENCY_BONUS;
            System.out.println("Color Efficiency Bonus Applied!");
        } else {
            System.out.println("Color Efficiency Bonus Not Applied.");
        }

        return Math.max(0, score);
    }


    private void setColorsUsed(int colors) {
        this.colorsUsed = colors;
        System.out.println("Colors Used Set to: " + colors);
    }


    private Scene createWinningScreen(Stage primaryStage) {
        timeSpent = Integer.parseInt(((Label)primaryStage.getScene()
                .lookup("#timerLabel")).getText().split(": ")[1]);

        Set<Color> uniqueColors = new HashSet<>();
        for (GraphRenderer.Vertex vertex : vertices) {
            Color vertexColor = (Color)renderer.getVertexColor(vertex);
            if (vertexColor != Color.WHITE) {
                uniqueColors.add(vertexColor);
            }
        }
        setColorsUsed(uniqueColors.size());

        StackPane winRoot = new StackPane();
        winRoot.setStyle("-fx-background-color: white;");

        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);

        Text congratsText = new Text("YOU WON!");
        congratsText.setFont(Font.font("Montserrat", 80));
        congratsText.setFill(Color.BLACK);

        VBox scoreBox = new VBox(10);
        scoreBox.setAlignment(Pos.CENTER);

        Text scoreText = new Text("Score: " + calculateScore());
        scoreText.setFont(Font.font("Montserrat", 60));
        scoreText.setFill(Color.BLACK);

        Text breakdownText = new Text(String.format("""
        Base Score: %d
        Time Penalty: -%d (%ds × %d)
        Hint Penalty: -%d (%d hints × %d)
        Mistake Penalty: -%d (%d mistakes × %d)
        Color Efficiency Bonus: %s
        """,
                MAX_SCORE,
                timeSpent * TIME_PENALTY, timeSpent, TIME_PENALTY,
                hintsUsed * HINT_PENALTY, hintsUsed, HINT_PENALTY,
                mistakes * MISTAKE_PENALTY, mistakes, MISTAKE_PENALTY,
                (colorsUsed == chromaticNumber ? "+" + COLOR_EFFICIENCY_BONUS : "+0")
        ));
        breakdownText.setFont(Font.font("Montserrat", 20));
        breakdownText.setFill(Color.BLACK);

        scoreBox.getChildren().addAll(scoreText, breakdownText);

        Button playAgainButton = new Button("Play Again");
        styleButton(playAgainButton);
        playAgainButton.setFont(Font.font("Montserrat", 30));

        playAgainButton.setOnAction(e -> {
            resetScore();
            primaryStage.setScene(menuScene);
        });

        content.getChildren().addAll(congratsText, scoreBox, playAgainButton);
        winRoot.getChildren().add(content);

        return new Scene(winRoot, 1080, 940);
    }

    private Scene createLosingScreen(Stage primaryStage) {
        timeSpent = Integer.parseInt(((Label)primaryStage.getScene()
                .lookup("#timerLabel")).getText().split(": ")[1]);

        StackPane loseRoot = new StackPane();
        loseRoot.setStyle("-fx-background-color: white;");

        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);

        Text loseText = new Text("YOU LOSE!");
        loseText.setFont(Font.font("Montserrat", 80));
        loseText.setFill(Color.BLACK);

        Button playAgainButton = new Button("Play Again");
        styleButton(playAgainButton);
        playAgainButton.setFont(Font.font("Montserrat", 30));

        playAgainButton.setOnAction(e -> {
            resetScore();
            primaryStage.setScene(menuScene);
        });

        content.getChildren().addAll(loseText, playAgainButton);
        loseRoot.getChildren().add(content);

        return new Scene(loseRoot, 1080, 940);
    }

    private void showLosingScreenAfterDelay(Stage primaryStage) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), evt -> {
            primaryStage.setScene(createLosingScreen(primaryStage));
        }));
        timeline.play();
    }

    private boolean colorGraph(List<Integer>[] connectionList, int[] colors, int[][] connectionAndVertices, int index, int maxColors) {
        if (index == connectionAndVertices.length) {
            return true;
        }

        int vertex = connectionAndVertices[index][1];

        for (int color = 1; color <= maxColors; color++) {
            if (isSafe(connectionList, colors, vertex, color)) {
                colors[vertex] = color;
                if (colorGraph(connectionList, colors, connectionAndVertices, index + 1, maxColors)) {
                    return true;
                }
                colors[vertex] = 0;
            }
        }
        return false;
    }

    private boolean isSafe(List<Integer>[] connectionList, int[] colors, int vertex, int color) {
        for (int neighbor : connectionList[vertex]) {
            if (colors[neighbor] == color) {
                return false;
            }
        }
        return true;
    }

    private String getInstructionsForMode() {
        switch (currentGameMode) {
            case TO_THE_BITTER_END:
                return "To The Bitter End: Color the graph with minimal colors." +
                        " Two adjacent vertices can not be of the same color." +
                        "\n\nLeft click to move around a vertex." +
                        "\n\nRight click on a vertex to chose a color." +
                        "\n\nShift + Right click to un-color a vertex";
            case RANDOM_ORDER:
                return "Random Order: Vertices will be presented in random order." +
                        " Color them strategically to minimize the amount of colors use." +
                        " Once a vertex colored you can not change it's color." +
                        "\n\nLeft click to move around a vertex (you can only move the vertex that you currently have to color)." +
                        "\n\nRight click on a vertex to chose a color.";
            case I_CHANGED_MY_MIND:
                return "I Changed My Mind: Similar to Random order game mode, vertices will be presented in random order." +
                        " Color them strategically to minimize the amount of colors use." +
                        " However you can undo colors in the reverse order of coloring them." +
                        " \n\nLeft click to move around a vertex (you can only move the vertex that you currently have to color)." +
                        "\n\nRight click on a vertex to chose a color." +
                        "\n\n↩ to undo coloring";
            default:
                return "Select a game mode and follow the specific rules for that mode!";
        }
    }

    private void resetGameState() {
        vertices.clear();
        renderer.resetState();
    }


    private boolean isCycleGraph(List<GraphRenderer.Vertex> vertices, List<GraphRenderer.Edge> edges) {
        int n = vertices.size();

        if (edges.size() != n) {
            return false;
        }

        Map<Integer, Integer> degreeMap = new HashMap<>();
        for (GraphRenderer.Edge edge : edges) {
            degreeMap.put(edge.getVertex1().getId(), degreeMap.getOrDefault(edge.getVertex1().getId(), 0) + 1);
            degreeMap.put(edge.getVertex2().getId(), degreeMap.getOrDefault(edge.getVertex2().getId(), 0) + 1);
        }

        for (int degree : degreeMap.values()) {
            if (degree != 2) {
                return false;
            }
        }

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(vertices.get(0).getId());
        visited.add(vertices.get(0).getId());

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (GraphRenderer.Edge edge : edges) {
                if (edge.getVertex1().getId() == current && !visited.contains(edge.getVertex2().getId())) {
                    visited.add(edge.getVertex2().getId());
                    queue.add(edge.getVertex2().getId());
                } else if (edge.getVertex2().getId() == current && !visited.contains(edge.getVertex1().getId())) {
                    visited.add(edge.getVertex1().getId());
                    queue.add(edge.getVertex1().getId());
                }
            }
        }

        return visited.size() == n;
    }


    private ReadGraph.Graph convertToReadGraph(RandomGraphGenerator.Graph randomGraph) {
        int numVertices = randomGraph.getVertices().size();
        List<ReadGraph.ColEdge> colEdges = new ArrayList<>();

        for (RandomGraphGenerator.Edge edge : randomGraph.getEdges()) {
            ReadGraph.ColEdge colEdge = new ReadGraph.ColEdge(
                    edge.getVertex1().getId(),
                    edge.getVertex2().getId()
            );
            colEdges.add(colEdge);
        }

        return new ReadGraph.Graph(numVertices, colEdges);
    }




    private int[][] toAdjacencyMatrix(List<GraphRenderer.Vertex> vertices, List<GraphRenderer.Edge> edges) {
        int n = vertices.size();
        int[][] adjacencyMatrix = new int[n][n];

        for (GraphRenderer.Edge edge : edges) {
            int u = edge.getVertex1().getId() - 1;
            int v = edge.getVertex2().getId() - 1;
            adjacencyMatrix[u][v] = 1;
            adjacencyMatrix[v][u] = 1;
        }

        return adjacencyMatrix;
    }


    private void generateRandomGraph(int numVertices, int numEdges, Pane targetPane) {
        try {
            RandomGraphGenerator.Graph randomGraph = RandomGraphGenerator.generateRandomGraph(numVertices, numEdges);

            vertices.clear();
            for (RandomGraphGenerator.Vertex v : randomGraph.getVertices()) {
                vertices.add(new GraphRenderer.Vertex(v.getId(), v.getX(), v.getY()));
            }

            edges.clear();
            for (RandomGraphGenerator.Edge e : randomGraph.getEdges()) {
                edges.add(new GraphRenderer.Edge(
                        vertices.get(e.getVertex1().getId() - 1),
                        vertices.get(e.getVertex2().getId() - 1)
                ));
            }

            renderer.renderGraph(vertices, edges);

            List<ReadGraph.ColEdge> colEdges = new ArrayList<>();
            for (RandomGraphGenerator.Edge e : randomGraph.getEdges()) {
                colEdges.add(new ReadGraph.ColEdge(e.getVertex1().getId(), e.getVertex2().getId()));
            }
            ReadGraph.Graph graph = new ReadGraph.Graph(numVertices, colEdges);

            int[][] adjacencyMatrix = toAdjacencyMatrix(vertices, edges);
            boolean isBipartite = BipartiteGraph.isBipartite(adjacencyMatrix, vertices.size());
            boolean isCycle = isCycleGraph(vertices, edges);
            boolean isComplete = isCompleteGraph(vertices, edges);
            boolean isTree = TreeGraph.isTreeGraph(adjacencyMatrix, vertices.size());


            // Analyze the generated graph
            if (BipartiteGraph.isBipartite(adjacencyMatrix, vertices.size())) {
                chromaticNumber = 2; // Set chromatic number for bipartite graphs
                System.out.println("The graph is bipartite.");
                System.out.println("Chromatic number: " + chromaticNumber);
            } else if (isCycleGraph(vertices, edges)) {
                chromaticNumber = (vertices.size() % 2 == 0) ? 2 : 3; // Cycle chromatic number
                System.out.println("The graph is a cycle graph.");
                System.out.println("Chromatic number: " + chromaticNumber);
            } else if (isCompleteGraph(vertices, edges)) {
                chromaticNumber = vertices.size(); // Complete graph chromatic number
                System.out.println("The graph is a complete graph.");
                System.out.println("Chromatic number: " + chromaticNumber);
            } else if (TreeGraph.isTreeGraph(adjacencyMatrix, vertices.size())) {
                chromaticNumber = 2; // Tree chromatic number is always 2
                System.out.println("The graph is a tree graph.");
                System.out.println("Chromatic number: " + chromaticNumber);
            } else if (isIntervalGraph(vertices, edges)) {
            } else {
                chromaticNumber = -1;
                System.out.println("The graph is neither bipartite, cycle, complete, split, interval, nor a tree.");
            }
        } catch (Exception ex) {
            showErrorDialog("Error", "Failed to generate random graph. Please check the inputs.");
        }
    }

    private void displayGraphFromFile(Pane targetPane) throws IOException {
        if (selectedFile != null) {
            ReadGraph.Graph graph = ReadGraph.loadGraphFromFile(selectedFile.getAbsolutePath(), 0);

            vertices.clear();
            for (int i = 1; i <= graph.getNumVertices(); i++) {
                vertices.add(new GraphRenderer.Vertex(i, Math.random() * 600 + 50, Math.random() * 400 + 50));
            }

            edges.clear();
            for (ReadGraph.ColEdge edge : graph.getEdges()) {
                edges.add(new GraphRenderer.Edge(vertices.get(edge.getU() - 1), vertices.get(edge.getV() - 1)));
            }

            renderer.renderGraph(vertices, edges);

            int[][] adjacencyMatrix = toAdjacencyMatrix(vertices, edges);
            if (BipartiteGraph.isBipartite(adjacencyMatrix, vertices.size())) {
                chromaticNumber = 2;
                System.out.println("The graph is bipartite.");
                System.out.println("Chromatic number: " + chromaticNumber);
            } else if (isCycleGraph(vertices, edges)) {
                chromaticNumber = (vertices.size() % 2 == 0) ? 2 : 3;
                System.out.println("The graph is a cycle graph.");
                System.out.println("Chromatic number: " + chromaticNumber);
            } else if (isCompleteGraph(vertices, edges)) {
                chromaticNumber = vertices.size();
                System.out.println("The graph is a complete graph.");
                System.out.println("Chromatic number: " + chromaticNumber);
            } else if (TreeGraph.isTreeGraph(adjacencyMatrix, vertices.size())) {
                chromaticNumber = 2;
                System.out.println("The graph is a tree graph.");
                System.out.println("Chromatic number: " + chromaticNumber);
            } else if (isIntervalGraph(vertices, edges)) {
            } else {
                chromaticNumber = -1; // Reset chromatic number for non-special graphs
                System.out.println("The graph is neither bipartite, a cycle, complete, split, interval, nor a tree.");
            }
        } else {
            showErrorDialog("Error", "No file selected. Please select a valid graph file.");
        }
    }


    private boolean isCompleteGraph(List<GraphRenderer.Vertex> vertices, List<GraphRenderer.Edge> edges) {
        int n = vertices.size();

        int expectedEdges = n * (n - 1) / 2;
        if (edges.size() != expectedEdges) {
            return false;
        }

        Map<Integer, Integer> degreeMap = new HashMap<>();
        for (GraphRenderer.Edge edge : edges) {
            degreeMap.put(edge.getVertex1().getId(), degreeMap.getOrDefault(edge.getVertex1().getId(), 0) + 1);
            degreeMap.put(edge.getVertex2().getId(), degreeMap.getOrDefault(edge.getVertex2().getId(), 0) + 1);
        }

        for (int degree : degreeMap.values()) {
            if (degree != n - 1) {
                return false;
            }
        }

        return true;
    }

    private boolean isIntervalGraph(List<GraphRenderer.Vertex> vertices, List<GraphRenderer.Edge> edges) {
        List<IntervalGraph.Interval> intervals = new ArrayList<>();

        for (GraphRenderer.Vertex vertex : vertices) {
            int start = (int) (Math.random() * 50) + 1;
            int end = start + (int) (Math.random() * 10) + 1;
            intervals.add(new IntervalGraph.Interval(start, end, vertex.getId()));
        }

        if (IntervalGraph.isIntervalGraph(intervals)) {
            int chromaticNumber = IntervalGraph.calculateChromaticNumber(intervals);
            System.out.println("The graph is an interval graph.");
            System.out.println("Chromatic number: " + chromaticNumber);
            return true;
        }

        System.out.println("The graph is not an interval graph.");
        return false;
    }

    private void styleButton(Button button) {
        button.setFont(Font.font("Arial", 15));
        button.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 20; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 4;"
        );
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showHintDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getColorName(Color color) {
        for (Map.Entry<String, Color> entry : GraphRenderer.colorMap.entrySet()) {
            if (entry.getValue().equals(color)) {
                return entry.getKey();
            }
        }
        return "Unknown Color";
    }

    private void openYouTubeLink(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void createHoverFx(Button buttonName, boolean isOver, boolean isOff) {
        if (isOver) {
            ScaleTransition scaleUp = new ScaleTransition(javafx.util.Duration.millis(200), buttonName);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);
            scaleUp.play();
        }
        if (isOff) {
            ScaleTransition scaleDown = new ScaleTransition(javafx.util.Duration.millis(200), buttonName);
            scaleDown.setToX(1);
            scaleDown.setToY(1);
            scaleDown.play();
        }
    }

    private void solveGraph(Pane graphPane) {
        int numVertices = vertices.size();
        int[] colors = new int[numVertices];
        Arrays.fill(colors, -1);

        List<Integer>[] adjacencyList = GraphRenderer.toAdjacencyList(vertices, edges);

        int chromaticNumber = findChromaticNumber(adjacencyList, numVertices);
        colorGraph(adjacencyList, colors, chromaticNumber);

        for (int i = 0; i < numVertices; i++) {
            GraphRenderer.Vertex vertex = vertices.get(i);
            Circle circle = renderer.getVertexCircleMap().get(vertex);
            if (circle != null) {
                Color color = getColorFromIndex(colors[i]);
                circle.setFill(color);
            }
        }
    }

    private int findChromaticNumber(List<Integer>[] adjacencyList, int numVertices) {
        int[] colors = new int[numVertices];
        Arrays.fill(colors, -1);
        colors[0] = 0;

        for (int i = 1; i < numVertices; i++) {
            boolean[] available = new boolean[numVertices];
            Arrays.fill(available, true);

            for (int neighbor : adjacencyList[i + 1]) {
                if (colors[neighbor - 1] != -1) {
                    available[colors[neighbor - 1]] = false;
                }
            }

            int color;
            for (color = 0; color < numVertices; color++) {
                if (available[color]) break;
            }

            colors[i] = color;
        }

        return Arrays.stream(colors).max().getAsInt() + 1;
    }

    private void colorGraph(List<Integer>[] adjacencyList, int[] colors, int chromaticNumber) {
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == -1) {
                for (int color = 0; color < chromaticNumber; color++) {
                    if (isColorValid(adjacencyList, colors, i, color)) {
                        colors[i] = color;
                        break;
                    }
                }
            }
        }
    }

    private boolean isColorValid(List<Integer>[] adjacencyList, int[] colors, int vertex, int color) {
        for (int neighbor : adjacencyList[vertex + 1]) {
            if (colors[neighbor - 1] == color) {
                return false;
            }
        }
        return true;
    }

    private Color getColorFromIndex(int index) {
        String[] colorOrder = {"Red", "Green", "Blue", "Yellow", "Orange", "Purple", "Pink", "Gray", "Black", "White", "Cyan", "Magenta", "Violet", "Indigo", "Silver", "Teal", "Lime"};
        if (index >= 0 && index < colorOrder.length) {
            return GraphRenderer.colorMap.get(colorOrder[index]);
        }
        return Color.WHITE;
    }

    private static class ColEdge {
        int u, v;

        ColEdge(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}