package org.example.project1;

import java.util.*;

public class BipartiteGraph {
    static class Edge {
        int u, v;

        Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }

        @Override
        public String toString() {
            return "(" + u + " -> " + v + ")";
        }
    }

    public static List<Edge> generateBipartiteGraph(int setU, int setV) {
        if (setU <= 0 || setV <= 0) {
            throw new IllegalArgumentException("Both sets must have at least one vertex.");
        }

        List<Edge> edges = new ArrayList<>();
        for (int i = 1; i <= setU; i++) {
            for (int j = setU + 1; j <= setU + setV; j++) {
                edges.add(new Edge(i, j)); //Connect each vertex in set U to each vertex in set V
            }
        }
        return edges;
    }

    public static boolean isBipartite(int[][] graph, int n) {
        int[] colors = new int[n];
        Arrays.fill(colors, -1);

        for (int start = 0; start < n; start++) {
            if (colors[start] == -1) {
                Queue<Integer> queue = new LinkedList<>();
                queue.add(start);
                colors[start] = 0;

                while (!queue.isEmpty()) {
                    int node = queue.poll();

                    for (int neighbor = 0; neighbor < n; neighbor++) {
                        if (graph[node][neighbor] == 1) {
                            if (colors[neighbor] == -1) {
                                colors[neighbor] = 1 - colors[node];
                                queue.add(neighbor);
                            } else if (colors[neighbor] == colors[node]) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int[][] graph = {
                {0, 1, 1, 1, 0, 0},
                {1, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 1, 1},
                {0, 1, 1, 1, 0, 0},
                {0, 1, 1, 1, 0, 0}
        };

        if (isBipartite(graph, graph.length)) {
            System.out.println("The graph is bipartite.");
            System.out.println("Chromatic number: 2");
        } else {
            System.out.println("The graph is not bipartite.");
        }
    }
}

