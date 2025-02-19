package org.example.project1;

import java.io.*;
import java.util.*;

 class ColEdge {
    int u;
    int v;
}

public class ChromaticNumber {

    public final static boolean DEBUG = true; //enable debug output
    public final static String COMMENT = "//"; //prefix for debug comments

    public static void main(String[] args) {

        String inputfile = args[0]; //get input file name from command-line arguments

        boolean seen[] = null; //array to track if vertices have been seen
        int n = -1; //number of vertices
        int m = -1; //number of edges
        ColEdge e[] = null; //array to store edges

        try {
            FileReader fr = new FileReader(inputfile); //create file to reading for input file
            BufferedReader br = new BufferedReader(fr); //create buffered reader for reading lines

            String record = new String();

            //skip comments and read until the first non-comment line
            while ((record = br.readLine()) != null) {
                if (record.startsWith("//")) continue; //ignore comment lines
                break; //stop when a non-comment line is found
            }

            if (record.startsWith("VERTICES = ")) {
                n = Integer.parseInt(record.substring(11));
                if (DEBUG) System.out.println(COMMENT + " number of vertices = " + n); //print number of vertices if debug is on
            }

            seen = new boolean[n + 1];

            record = br.readLine();
            if (record.startsWith("EDGES = ")) {
                m = Integer.parseInt(record.substring(8)); //parse number of edges
                if (DEBUG) System.out.println(COMMENT + " expected number of edges = " + m); //print number of edges if debug is on
            }

            e = new ColEdge[m]; //initialize edges array

            for (int d = 0; d < m; d++) {
                if (DEBUG) System.out.println(COMMENT + " reading edge " + (d + 1)); //print edge for reading info if debug is on
                record = br.readLine();
                String data[] = record.split(" "); //split line into vertex data
                if (data.length != 2) {
                    System.out.println("error! malformed edge line: " + record); //error handling for malformed edge line
                    System.exit(0);
                }
                e[d] = new ColEdge(); //initializing edge object
                e[d].u = Integer.parseInt(data[0]); //set first vertex
                e[d].v = Integer.parseInt(data[1]); //set second vertex

                seen[e[d].u] = true; //mark first vertex as seen
                seen[e[d].v] = true; //mark second vertex as seen

                if (DEBUG) System.out.println(COMMENT + " edge: " + e[d].u + " " + e[d].v); //print edges info if debug is on
            }
        } catch (IOException ex) {
            System.out.println("error! problem reading file " + inputfile); //error handling for file reading
            System.exit(0);
        }

        for (int x = 1; x <= n; x++) {
            if (!seen[x]) {
                if (DEBUG) {
                    System.out.println(COMMENT + " warning: vertex " + x + " didn't appear in any edge : it will be considered a disconnected vertex on its own."); //--warn if a vertex is disconnected
                }
            }
        }


        List<Integer>[] ConnectionList = new ArrayList[n + 1]; //initializing adjacency list for each vertex
        for (int i = 1; i <= n; i++) {
            ConnectionList[i] = new ArrayList<>(); //create empty list for each vertex
        }

        for (int i = 0; i < m; i++) {
            int u = e[i].u; //get first vertex of edge
            int v = e[i].v; //get second vertex of edge
            ConnectionList[u].add(v); //add v to u's adjacency list
            ConnectionList[v].add(u); //add u to v's adjacency list
        }

        int[][] ConnectionAndVertices = new int[n][2]; //array to store connection count and vertex number
        for (int i = 1; i <= n; i++) {
            ConnectionAndVertices[i - 1][0] = ConnectionList[i].size();
            ConnectionAndVertices[i - 1][1] = i; //store vertex number
        }


        for (int i = 0; i < ConnectionAndVertices.length - 1; i++) {
            for (int j = 0; j < ConnectionAndVertices.length - i - 1; j++) {
                if (ConnectionAndVertices[j][0] < ConnectionAndVertices[j + 1][0]) {
                    int[] temp = ConnectionAndVertices[j]; //--swap arrays
                    ConnectionAndVertices[j] = ConnectionAndVertices[j + 1];
                    ConnectionAndVertices[j + 1] = temp; //--swap arrays
                }
            }
        }

        int[] colors = new int[n + 1]; //array to store vertex colors

        int upperBound = calculateUpperBound(n, e);
        System.out.println("The upper bound for this graph is: " + upperBound);

        int lowerBound = lowerbounds(n, e);
        System.out.println("The lower bound for this graph is: " + lowerBound);

        int Greedynumber = greedyColoring(ConnectionList, n);
        System.out.println("The chromatic number using the greedy method is :" + Greedynumber);

        int chromaticNumber = findChromaticNumber(ConnectionList, colors, ConnectionAndVertices, n);
        System.out.println("Chromatic number: " + chromaticNumber);

    }

    //backtracking algorithm to find the chromatic number
    static int findChromaticNumber(List<Integer>[] ConnectionList, int[] colors, int[][] ConnectionAndVertices, int n) {
        for (int maxColors = 1; maxColors <= n; maxColors++) {
            Arrays.fill(colors, 0); //reset colors for each attempt
            if (colorGraph(ConnectionList, colors, ConnectionAndVertices, 0, maxColors)) {
                return maxColors; //return maxColors if successful
            }
        }
        return n; //return n if no solution found
    }

    //backtracking function to color the graph
    static boolean colorGraph(List<Integer>[] ConnectionList, int[] colors, int[][] ConnectionAndVertices, int index, int maxColors) {
        if (index == ConnectionAndVertices.length) {
            return true; //base case: if the all vertices are colored
        }

        int vertex = ConnectionAndVertices[index][1]; //get current vertex

        for (int color = 1; color <= maxColors; color++) {
            if (isSafe(ConnectionList, colors, vertex, color)) {
                colors[vertex] = color;
                if (colorGraph(ConnectionList, colors, ConnectionAndVertices, index + 1, maxColors)) {
                    return true;
                }
                colors[vertex] = 0; //reset color this is the backtrack
            }
        }
        return false; //return false if no color works
    }

    //check if assigning color to vertex is safe
    static boolean isSafe(List<Integer>[] ConnectionList, int[] colors, int vertex, int color) {
        for (int neighbor : ConnectionList[vertex]) {
            if (colors[neighbor] == color) {
                return false; //return false if neighbor has the same color
            }
        }
        return true; //return true if color is safe
    }

    public static int calculateUpperBound(int n, ColEdge[] edges) {

        int[][] adjacencyMatrix = new int[n][n];
        for (ColEdge edge : edges) {
            adjacencyMatrix[edge.u - 1][edge.v - 1] = 1;
            adjacencyMatrix[edge.v - 1][edge.u - 1] = 1;
        }

        //Array to store the color assigned to each vertex
        int[] colors = new int[n];
        Arrays.fill(colors, -1); //Initialize with -1


        boolean[] availableColors = new boolean[n];


        for (int vertex = 0; vertex < n; vertex++) {

            Arrays.fill(availableColors, true);


            for (int neighbor = 0; neighbor < n; neighbor++) {
                if (adjacencyMatrix[vertex][neighbor] == 1 && colors[neighbor] != -1) {
                    availableColors[colors[neighbor]] = false; // Mark color as unavailable
                }
            }


            int color;
            for (color = 0; color < n; color++) {
                if (availableColors[color]) {
                    break;
                }
            }


            colors[vertex] = color;
        }


        int maxColor = 0;
        for (int color : colors) {
            maxColor = Math.max(maxColor, color);
        }

        return maxColor + 1; //Return the upper bound
    }

    //calculate the lower bound for chromatic number
    public static int lowerbounds(int n, ColEdge[] edges) {
        int[] connections = new int[n + 1];

        for (ColEdge edge : edges) {
            connections[edge.u]++;
            connections[edge.v]++;
        }

        int min = Integer.MAX_VALUE;
        for (int k = 1; k <= n; k++) {
            if (connections[k] < min) {
                min = connections[k];
            }
        }

        return min + 1; //return minimum connections + 1
    }

    //greedy algorithm for coloring the graph
    static int greedyColoring(List<Integer>[] connectionList, int n) {
        int[] colors = new int[n + 1];
        Arrays.fill(colors, -1);

        for (int u = 1; u <= n; u++) {
            boolean[] available = new boolean[n + 1];
            Arrays.fill(available, true);

            for (int neighbor : connectionList[u]) {
                if (colors[neighbor] != -1) {
                    available[colors[neighbor]] = false;
                }
            }

            int availableColor;
            for (availableColor = 1; availableColor <= n; availableColor++) {
                if (available[availableColor]) {
                    break;
                }
            }
            colors[u] = availableColor;
        }

        int chromaticNumber = 0;
        for (int i = 1; i <= n; i++) {
            chromaticNumber = Math.max(chromaticNumber, colors[i]);
        }
        return chromaticNumber;
    }


}

