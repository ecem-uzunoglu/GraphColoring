
## README

### Project Overview

This project implements a suite of tools and visualizations for working with graphs. The main focus is to compute the chromatic number of a graph, analyze its properties, and provide users with interactive and programmatic tools for graph exploration. Additionally, it includes a visualization component and supports user-defined or randomly generated graphs.

### How to Run the Program

1. **Compile the Code**:
   - Open your CLI.
   - Navigate to the directory containing the main Java files.
   - Compile the necessary files using:
     ```
     javac ChromaticNumber.java GraphApp.java GraphRenderer.java RandomGraphGenerator.java ReadGraph.java
     ```

2. **Run the Program**:
   - After compilation, execute the program using:
     ```
     java GraphApp
     ```
   - This will launch the graphical application where users can interact with graph data.

### Features

1. **Graph Chromatic Number Computation**:
   - The program includes algorithms for calculating the chromatic number using various methods such as greedy coloring and optimized backtracking.

2. **Graph Visualization**:
   - The graphical interface allows users to visualize vertices, edges, and color assignments dynamically.
   - Multiple game modes provide interactive challenges for coloring graphs with minimal colors.

3. **File-Based Graph Loading**:
   - Graphs can be loaded from external files with a specific format:
     ```
     VERTICES = <number of vertices>
     EDGES = <number of edges>
     <vertex1> <vertex2>
     ...
     ```
   - Example:
     ```
     VERTICES = 3
     EDGES = 3
     1 2
     1 3
     2 3
     ```

4. **Random Graph Generation**:
   - Users can specify the number of vertices and edges to generate random graphs for testing and exploration.

5. **Game Modes**:
   - *To The Bitter End*: Color the graph with minimal colors as fast as possible.
   - *Random Order*: Vertices are presented in random order for coloring.
   - *I Changed My Mind*: Similar to Random Order but allows undoing color choices.

### Inputting a New Graph File

To use a custom graph file:

1. **Create the Graph File**:
   - Ensure the file contains the correct format as described above.

2. **Load the File**:
   - Use the "Select File" option in the graphical interface to load and visualize your graph.

### Notes

- Ensure your graph file format matches the expected input style.
- The program will provide helpful error messages for invalid inputs.
- For debugging or advanced analysis, enable the `DEBUG` flag in the code.

### Code Structure

- **ChromaticNumber.java**:
  - Core algorithms for computing the chromatic number.
- **GraphApp.java**:
  - Main graphical application.
- **GraphRenderer.java**:
  - Handles graph visualisation and interaction.
- **RandomGraphGenerator.java**:
  - Generates random graphs with specified parameters.
- **ReadGraph.java**:
  - Reads and parses graph data from files.
- **ColorWheelPicker.java**:
  - Enhances the coloring availability, therefore you can't run out of different colors.
- **TreeGraph.java**:
  - Tree graph detection logic.
- **BipartiteGraph.java**:
  - Bipartite graph detection logic.
- **IntervalGraph.java**:
  - Interval graph detection logic.
- **CompleteGraph.java**:
  - Complete graph detection logic.
- **CycleGraph.java**:
  - Cycle graph detection logic.
- **ChordalGraph.java**:
  - Chordal graph detection logic.
- **PlanarGraph.java**:
  - Planar graph detection logic.

### Requirements

- Java Development Kit (JDK) 8 or higher.
- JavaFX for the graphical interface.

