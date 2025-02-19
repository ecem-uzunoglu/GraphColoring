package org.example.project1;


import java.io.*;
import java.util.*;

public class ReadGraph {

    public final static boolean DEBUG = true; //--debug flag for development
    public final static String COMMENT = "//"; //--comment line prefix

    public static Graph loadGraphFromFile(String inputfile, int graphIndex) throws IOException {
        //--load graph data from file and return a Graph object
        BufferedReader br = new BufferedReader(new FileReader(inputfile));
        String record;
        int currentGraphIndex = 0;
        int n = -1; //--number of vertices
        int m = -1; //--number of edges
        List<ColEdge> edges = new ArrayList<>(); //--list to store edges

        while ((record = br.readLine()) != null) {
            if (record.startsWith(COMMENT) || record.trim().isEmpty()) continue; //--skip comments and empty lines

            //--check for new graph definition
            if (record.startsWith("VERTICES = ")) {
                if (currentGraphIndex == graphIndex) {
                    n = Integer.parseInt(record.substring(11)); //--parse number of vertices
                    m = Integer.parseInt(br.readLine().substring(8)); //--parse number of edges
                    for (int i = 0; i < m; i++) {
                        String[] edge = br.readLine().split(" ");
                        edges.add(new ColEdge(Integer.parseInt(edge[0]), Integer.parseInt(edge[1])));
                    }
                    break; //--exit loop after finding the specified graph
                } else {
                    currentGraphIndex++;
                    //--skip lines for the current graph until next graph definition
                    while (!(record = br.readLine()).startsWith("VERTICES = ") && record != null) { }
                }
            }
        }

        //--read and process remaining lines if needed
        while ((record = br.readLine()) != null) {
            if (record.startsWith(COMMENT) || record.trim().isEmpty()) continue; //--skip comments and empty lines

            if (record.startsWith("VERTICES = ")) {
                System.out.println("Processing graph at index: " + currentGraphIndex); //--log processing info
                if (currentGraphIndex == graphIndex) {
                    n = Integer.parseInt(record.substring(11));
                    m = Integer.parseInt(br.readLine().substring(8));
                    for (int i = 0; i < m; i++) {
                        String[] edge = br.readLine().split(" ");
                        edges.add(new ColEdge(Integer.parseInt(edge[0]), Integer.parseInt(edge[1])));
                    }
                    break;
                } else {
                    currentGraphIndex++;
                    while (!(record = br.readLine()).startsWith("VERTICES = ") && record != null) { }
                }
            }
        }

        br.close();

        //--check for valid graph data
        if (n == -1 || m == -1) {
            throw new IllegalArgumentException("Invalid graph index or file format.");
        }

        return new Graph(n, edges); //--return constructed graph object
    }

    public static class Graph {
        //--class representing a graph
        private int numVertices;
        private List<ColEdge> edges;

        public Graph(int numVertices, List<ColEdge> edges) {
            this.numVertices = numVertices;
            this.edges = edges;
        }

        public int getNumVertices() {
            return numVertices; //--get number of vertices
        }

        public List<ColEdge> getEdges() {
            return edges; //--get list of edges
        }
    }

    public static class ColEdge {
        //--class representing an edge in the graph
        int u;
        int v;

        public ColEdge(int u, int v) {
            this.u = u;
            this.v = v;
        }

        public int getU() {
            return u; //--get one endpoint of the edge
        }

        public int getV() {
            return v; //--get other endpoint of the edge
        }
    }
}
