package org.example.project1;

import java.util.ArrayList;
import java.util.List;

public class ChordalGraph {

    //checking if the graph is chordal
    public static boolean isChordal(int[][] adjacencyMatrix){
        int numV=adjacencyMatrix.length;
        int[] peo=getPEO(adjacencyMatrix,numV);
        if(peo==null){
            return false;
        }

        //validating if the graph is chordal: the earlier neighbors in [erfect eliimination ordering should form a clique
        for(int i=0;i<numV;i++){
            int vertex=peo[i];
            List<Integer> earlierNeigh=new ArrayList<>();

            for(int j=i+1;j<numV;j++){
                if(adjacencyMatrix[vertex][peo[j]]==1){
                    earlierNeigh.add(peo[j]);
                }
            }

            if(!fromsClique(earlierNeigh,adjacencyMatrix)){
                return false;
            }


        }
        return true;

    }


    //finding chromatic number for chordal graph
    public static int ChordalChromaticNum(int[][] adjacencyMatrix){
        if(!isChordal(adjacencyMatrix)){
            throw new IllegalArgumentException("The graph is not chordal");
        }

        int numV=adjacencyMatrix.length;
        int[] peo=getPEO(adjacencyMatrix,numV);

        //calculating size of the largest clique
        int maxCliqueSize=1;
        int[] peoIndex=new int[numV];
        for(int i=0;i<numV;i++){
            peoIndex[peo[i]]=i;
        }

        for(int i=0;i<numV;i++){
            int vertex=peo[i];
            int cliqueSize=1; //this is the vertex itself
            for(int j=i+1;j<numV;j++){
                if(adjacencyMatrix[vertex][peo[j]]==1 && peoIndex[peo[j]]<peoIndex[vertex]){
                    cliqueSize++;
                }
            }
            maxCliqueSize=Math.max(maxCliqueSize,cliqueSize);
        }

        return maxCliqueSize;//the chromatic number is the size of the largest clique
    }

    //performing maximum cardinality search
    private static int[] getPEO(int[][] adjacencyMatrix, int numV){
        int[] order=new int[numV];
        boolean[] visited=new boolean[numV];
        int[] weights=new int[numV];


        for(int i=numV-1;i>=0;i--){
            int maxWeight=-1,chosenVertex=-1;

            for(int v=0;v<numV;v++){
                if(!visited[v] && weights[v]>maxWeight){
                    maxWeight=weights[v];
                    chosenVertex=v;
                }
            }

            if(chosenVertex==-1){
                return null;//A valid performance elimination is not found
            }

            order[i]=chosenVertex;
            visited[chosenVertex]=true;

            for(int neighbor=0;neighbor<numV;neighbor++){
                if(adjacencyMatrix[chosenVertex][neighbor]==1 && !visited[neighbor]){
                    weights[neighbor]++;
                }
            }
        }
        return order;
    }

    //checking if a list of vertices a=is forming a clique
    private static boolean fromsClique(List<Integer> vertices,int[][] adjacencyMatrix){
        for(int i=0;i<vertices.size();i++){
            for(int j=i+1;j<vertices.size();j++){
                if(adjacencyMatrix[vertices.get(i)][vertices.get(j)]!=1){
                    return false;
                }
            }
        }
        return true;
    }
}
