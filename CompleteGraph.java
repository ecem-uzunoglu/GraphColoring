package org.example.project1;

import java.util.List;

public class CompleteGraph {


public static boolean isCompleteGraph(List<GraphRenderer.Vertex> vertices,List<GraphRenderer.Edge> edges) {
    int n=vertices.size();
    //checking the edge count
    int expectedEdges=n*(n-1)/2;
    if(edges.size()!=expectedEdges){
        return false;
    }

    //building a degree array to count how many edges each vertex has
    int[] degree=new int[n+1];

    for(GraphRenderer.Edge e:edges){
        int u=e.getVertex1().getId();
        int v=e.getVertex2().getId();
        //counting degrees for each endpoints
        degree[u]++;
        degree[v]++;
    }

    //In a complete graph , each veretx ahs degree n-1
    for(GraphRenderer.Vertex v:vertices){
        if(degree[v.getId()] !=(n-1)){
            return false;
        }
    }
    return true;
}
public static int CompleteGraphChromaticNumber(List<GraphRenderer.Vertex> vertices,List<GraphRenderer.Edge> edges){
    return vertices.size();
}
}

