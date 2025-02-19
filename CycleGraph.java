package org.example.project1;

import java.util.ArrayList;
import java.util.List;


public class CycleGraph {


    public static List<GraphRenderer.Edge> generateCycleGraph(int n) {
        if (n < 3) {
            throw new IllegalArgumentException("A cycle graph must have at least 3 vertices.");
        }

        List<GraphRenderer.Edge> edges = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            edges.add(new GraphRenderer.Edge(new GraphRenderer.Vertex(i,0,0),new GraphRenderer.Vertex(i+1,0,0))); // Connect consecutive vertices
        }
        edges.add(new GraphRenderer.Edge(new GraphRenderer.Vertex(n,0,0),new GraphRenderer.Vertex(1,0,0))); // Close the cycle

        return edges;
    }

    public static boolean isCycleGraph(List<GraphRenderer.Edge> edges, int numVertices) {
        if (edges.size() != numVertices) return false;

        int[] degree = new int[numVertices + 1];
        for (GraphRenderer.Edge edge : edges) {
            degree[edge.getVertex1().getId()]++;
            degree[edge.getVertex2().getId()]++;
        }

        for (int i = 1; i <= numVertices; i++) {
            if (degree[i] != 2) return false;
        }

        return true;
    }

    public static int chromaticNumber(int numVertices) {
        return (numVertices % 2 == 0) ? 2 : 3;
    }

    public static void main(String[] args) {
        int numVertices = 5;
        List<GraphRenderer.Edge> cycleGraph = generateCycleGraph(numVertices);

        System.out.println("Cycle Graph with " + numVertices + " vertices:");
        for (GraphRenderer.Edge edge : cycleGraph) {
            System.out.println(edge);
        }

        if (isCycleGraph(cycleGraph, numVertices)) {
            System.out.println("This is a cycle graph.");
            System.out.println("Chromatic number: " + chromaticNumber(numVertices));
        } else {
            System.out.println("This is not a cycle graph.");
        }
    }
}

