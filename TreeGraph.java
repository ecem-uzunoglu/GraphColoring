package org.example.project1;

import java.util.*;

public class TreeGraph {


    public static boolean isTreeGraph(int[][] adjacencyMatrix, int numVertices) {
        int edgeCount = 0;
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = i + 1; j < adjacencyMatrix[i].length; j++) {
                if (adjacencyMatrix[i][j] == 1) edgeCount++;
            }
        }
        if (edgeCount != numVertices - 1) return false;

        boolean[] visited = new boolean[numVertices];
        if (!isConnected(adjacencyMatrix, visited)) return false;

        Arrays.fill(visited, false);
        if (hasCycle(adjacencyMatrix, visited, 0, -1)) return false;

        return true;
    }

    private static boolean isConnected(int[][] adjacencyMatrix, boolean[] visited) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);
        visited[0] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int neighbor = 0; neighbor < adjacencyMatrix.length; neighbor++) {
                if (adjacencyMatrix[current][neighbor] == 1 && !visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }

        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    private static boolean hasCycle(int[][] adjacencyMatrix, boolean[] visited, int current, int parent) {
        visited[current] = true;

        for (int neighbor = 0; neighbor < adjacencyMatrix.length; neighbor++) {
            if (adjacencyMatrix[current][neighbor] == 1) {
                if (!visited[neighbor]) {
                    if (hasCycle(adjacencyMatrix, visited, neighbor, current)) return true;
                } else if (neighbor != parent) {
                    return true;
                }
            }
        }

        return false;
    }
}
