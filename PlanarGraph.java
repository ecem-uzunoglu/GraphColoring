package org.example.project1;

public class PlanarGraph {

    //checking if the graph is planar
    public static boolean isPlanar(int numV,int numE){
        //We use the Euler's fromula:numE<=3 * 3*numV-6
        if(numV<=2){
            return numE<=numV*(numV-1)/2;//this handles small cases

        }
        return numE<=(3*numV-6);
    }

    //checking using adjacency matrix
    public static boolean isPlanar(int[][] adjacencyMatrix){
        int numV = adjacencyMatrix.length;
        int numE = countEdges(adjacencyMatrix);
        return isPlanar(numV,numE);
    }

    //finding chromatic number for planar graph
    public static int PlanarChromaticNumber(int[][] adjacencyMatrix){
        int numV = adjacencyMatrix.length;
        int numE = countEdges(adjacencyMatrix);

        if(!isPlanar(numV,numE)){
            throw new IllegalArgumentException("The graoh is not  planar ");
        }

        //chromatic number for planar graohs:At most 4 and at msot 3 if the planar graph is bipartite
        if(BipartiteGraph.isBipartite(adjacencyMatrix,numV))
        {
            return 2;//Bipartite planar graph
        }else
        {
            return 4;//general planar graph
        }
    }
    public static int countEdges(int[][] adjacencyMatrix){
        int count=0;

        for(int i=0;i<adjacencyMatrix.length;i++){
            for(int j=0;j<adjacencyMatrix.length;j++){
                if(adjacencyMatrix[i][j]==1){
                    count++;
                }
            }
        }
        return count;
    }

}
