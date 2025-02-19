package org.example.project1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGraphGenerator {

    public static class Graph {
        //--class representing a graph with vertices and edges
        private List<Vertex> vertices;
        private List<Edge> edges;

        public Graph(List<Vertex> vertices, List<Edge> edges) {
            this.vertices = vertices;
            this.edges = edges;
        }

        public List<Vertex> getVertices() {
            return vertices; //--get list of vertices
        }

        public List<Edge> getEdges() {
            return edges; //--get list of edges
        }
    }



    public static Graph generateRandomGraph(int numVertices, int numEdges) {
        //--generate a random graph with specified number of vertices and edges
        if (numEdges < numVertices - 1 || numEdges > numVertices * (numVertices - 1) / 2) {
            throw new IllegalArgumentException(
                    "Invalid number of edges. A connected graph requires at least " + (numVertices - 1)
                            + " edges and at most " + (numVertices * (numVertices - 1) / 2) + " edges."
            );
        }

        List<Vertex> vertices = new ArrayList<>(); //--list to store vertices
        List<Edge> edges = new ArrayList<>(); //--list to store edges
        Random random = new Random(); //--random number generator

        //--create vertices
        for (int i = 0; i < numVertices; i++) {
            vertices.add(new Vertex(i + 1, random.nextDouble() * 800 + 50, random.nextDouble() * 600 + 50));
        }

        //--create a connected graph (minimum spanning tree)
        List<Vertex> connected = new ArrayList<>(); //--list to track connected vertices
        connected.add(vertices.get(0));
        while (connected.size() < numVertices) {
            Vertex v1 = connected.get(random.nextInt(connected.size()));
            Vertex v2 = vertices.get(connected.size());
            edges.add(new Edge(v1, v2)); //--add edge to the connected graph
            connected.add(v2);
        }

        //--add random edges until the desired number of edges is reached
        while (edges.size() < numEdges) {
            Vertex v1 = vertices.get(random.nextInt(numVertices));
            Vertex v2 = vertices.get(random.nextInt(numVertices));
            if (v1 != v2 && !edgeExists(edges, v1, v2)) { //--ensure unique and valid edges
                edges.add(new Edge(v1, v2));
            }
        }

        return new Graph(vertices, edges); //--return generated graph
    }

    private static boolean edgeExists(List<Edge> edges, Vertex v1, Vertex v2) {
        //--check if an edge already exists between two vertices
        for (Edge edge : edges) {
            if ((edge.getVertex1() == v1 && edge.getVertex2() == v2) ||
                    (edge.getVertex1() == v2 && edge.getVertex2() == v1)) {
                return true;
            }
        }
        return false;
    }

    public static class Vertex {
        //--class representing a vertex
        private int id;
        private double x, y;

        public Vertex(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public int getId() {
            return id; //--get vertex ID
        }

        public double getX() {
            return x; //--get x-coordinate
        }

        public double getY() {
            return y; //--get y-coordinate
        }
    }

    public static class Edge {
        //--class representing an edge between two vertices
        private Vertex vertex1;
        private Vertex vertex2;

        public Edge(Vertex vertex1, Vertex vertex2) {
            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
        }

        public Vertex getVertex1() {
            return vertex1; //--get first vertex of the edge
        }

        public Vertex getVertex2() {
            return vertex2; //--get second vertex of the edge
        }
    }
}
