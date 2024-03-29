//
// Flow.java
//
// This class maintains a flow graph with a computable and retrievable maximum flow.
// The maximum flow calculation is separate from construction, so member variables hold no 
// significant value until the calculation is actually performed via a method call.
//
// The MIT License (MIT)
//
// Copyright (c) 2022 Luke Andrews. All Rights Reserved.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this
// software and associated documentation files (the "Software"), to deal in the Software
// without restriction, including without limitation the rights to use, copy, modify, merge,
// publish, distribute, sub-license, and/or sell copies of the Software, and to permit persons
// to whom the Software is furnished to do so, subject to the following conditions:
// 
// * The above copyright notice and this permission notice shall be included in all copies or
//   substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
// FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.
//
package maxflowalgorithm;

class Flow
{
    private int[][] graph;
    private int     source;
    private int     sink;
    
    private int[][] flowGraph;
    private int[][] residualGraph;
    private int     maxFlow;
    
    //
    // Overloaded constructor.
    //
    // Constructs with a weighted graph, source node index and sink node index.
    // The expected format of the weighted graph interprets positive numbers as 
    // indicating an edge and 0 indicating the absence of an edge.
    //
    //      [in] graph      - the weighted graph
    //      [in] source     - the index of source node
    //      [in] sink       - the index of the sink node
    //
    public Flow(int[][] graph, int source, int sink)
    {
        System.out.println("Flow from " + source + " to " + sink + " in graph: ");
        printGraph(graph);
        
        this.graph  = graph;
        this.source = source;
        this.sink   = sink;
        
        if(!_inputCheck())
        {
            System.out.println("\nIllegal Argument to Flow()");
            
            throw new IllegalArgumentException();
        }
        
        this.flowGraph     = _createFlowGraph();
        this.residualGraph = _createResidual();
        this.maxFlow       = 0;
    }
    
    //
    // getFlowGraph
    //
    // Gets the flow graph.
    //
    public int[][] getFlowGraph()
    {
        return flowGraph;
    }
    
    //
    // getMaxFlow
    //
    // Gets the maximum flow.
    //
    public int getMaxFlow()
    {
        return maxFlow;
    }
    
    //
    // computeMaxFlowFordFulkerson
    //
    // Computes the maximum flow of the graph using the Ford Fulkerson method.
    // Algorithm progress is printed to the console.
    //
    public void computeMaxFlowFordFulkerson()
    {
        //
        // Reset flow graph (no flow to start):
        //
        flowGraph = _createFlowGraph();
        System.out.println("\nFlow Graph:");
        printGraph(flowGraph);
        
        //
        // Reset residual graph (starts as a copy of the graph):
        //
        residualGraph = _createResidual();
        System.out.println("\nResidual Graph:");
        printGraph(residualGraph);
        
        //
        // Get a path through the residual graph:
        //
        Node pathStart = _getPath();
        
        //
        // While we have a valid path:
        //
        while(pathStart != null)
        {
            System.out.print("\nPath: ");
            Node curr = pathStart;
            
            while(curr.next != null)
            {
                System.out.print(curr.value.startNodeId + "->");
                curr = curr.next;
            }
            
            System.out.print(curr.value.startNodeId + "->");
            System.out.println(curr.value.endNodeId);
            
            //
            // Get the minimum cost of the path:
            //
            int minCost = _getMinCost(pathStart);
            System.out.println("Min Cost: " + minCost);
            
            //
            // Update the flow:
            //
            _updateFlow(pathStart, minCost);
            System.out.println("\nFlow Graph:");
            printGraph(flowGraph);
            
            //
            // Update the residual:
            //
            _updateResidual(pathStart, minCost);
            System.out.println("\nResidual Graph:");
            printGraph(residualGraph);
            
            //
            // Get a new path through the residual graph:
            //
            pathStart = _getPath();
        }
        
        //
        // Get max flow from flow graph:
        //
        maxFlow = _computeMaxFlow();
        System.out.println("\nMax Flow: " + maxFlow);
    }
    
    //
    // printGraph
    //
    // Prints a given graph to the console.
    //
    public void printGraph(int[][] graph)
    {
        for (int row = 0; row < graph.length; row++)
        {
            System.out.print("{");
            
            for (int col = 0; col < graph[row].length; col++)
            {
                if (graph[row][col] == Integer.MAX_VALUE)
                {
                    System.out.printf(" inf ", graph[row][col]);
                }
                else
                {
                    System.out.printf("%4d ", graph[row][col]);
                }
            }
            
            System.out.println("}");
        }
    }
    
    //
    // inputCheck
    //
    // Used for construction. Validates values of member variables.
    //
    // Returns whether all validation checks were passed.
    //
    private boolean _inputCheck()
    {
        if (graph == null)
        {
            return false;
        }
        
        if (source < 0 || source >= graph.length)
        {
            return false;
        }
        
        if (sink < 0 || sink >= graph.length || sink == source)
        {
            return false;
        }
        
        for (int i = 0; i < graph.length; i++)
        {
            if (graph[i].length != graph.length)
            {
                return false;
            }
            
            for (int j = 0; j < graph[i].length; j++)
            {
                if (graph[i][j] < 0)
                {
                    return false;
                }
                
                if (graph[i][j] > 0 && graph[j][i] > 0)
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    //
    // createFlowGraph
    //
    // Creates an empty graph the size of the input graph (Flow graphs are empty to start).
    //
    // Returns the empty graph.
    //
    private int[][] _createFlowGraph()
    {
        return new int[graph.length][graph.length];
    }
    
    //
    // createResidual
    //
    // Create a copy of the input graph (Residual graphs are a copy to start).
    //
    // Returns the copy.
    //
    private int[][] _createResidual()
    {
        int[][] residualGraph = new int[graph.length][graph.length];
        
        for (int i = 0; i < graph.length; i++)
        {
            for (int j = 0; j < graph.length; j++)
            {
                residualGraph[i][j] = graph[i][j];
            }
        }
        
        return residualGraph;
    }
    
    //
    // getPath
    //
    // Computes a path by performing a breadth-first search from the source to the sink.
    //
    // Returns the start node of the path.
    //
    private Node _getPath()
    {
        //
        // Initialize queue of nodes encountered:
        //
        int[] queue = new int[residualGraph.length];
        
        //
        // Initialize parents list / in-queue list (serves both purposes, if they are
        // the source, or if they have a parent, they must be in the queue):
        //
        int[] parents = new int[residualGraph.length];
        
        for (int i = 0; i < parents.length; i++)
        {
            parents[i] = -1;
        }
        
        //
        // Put the source node in the queue:
        //
        int qSize = 1;
        queue[0] = source;
        
        //
        // Do a breadth first traversal:
        //
        for (int i = 0; i < qSize; i++)
        {
            int queueNode = queue[i];
            
            for (int j = 0; j < residualGraph.length; j++)
            {
                if (j != source && parents[j] == -1 && residualGraph[queueNode][j] > 0)
                {
                    queue[qSize] = j;
                    parents[j] = queueNode;
                    qSize++;
                }
            }
        }
        
        System.out.print("\nQueue:   ");
        
        for (int i = 0; i < queue.length; i++)
        {
            System.out.print(queue[i] + " ");
        }
        
        System.out.println();
        System.out.print("Parents: ");
        
        for(int i = 0; i < parents.length; i++)
        {
            System.out.print(parents[i] + " ");
        }
        
        System.out.println();
        
        //
        // No path found :(
        //
        if (parents[sink] == -1)
        {
            return null;
        }
        
        //
        // Path found! Create list of edges starting at the destination
        // and following the parent pointers:
        //
        int  currNode   = sink;
        int  currParent = parents[sink];
        Node path       = new Node(new Edge(currParent, currNode));
        
        while (currParent != source)
        {
            //
            // Go up to the parent:
            //
            currNode = currParent;
            
            //
            // Get the new parent:
            //
            currParent = parents[currNode];
            
            //
            // Prepend to the path:
            //
            path = new Node(new Edge(currParent, currNode), path);
        }
        
        return path;
    }
    
    //
    // getMinCost
    //
    // Determines the minimum cost of an edge along a given path.
    //
    //      [in] path - the start node of the path
    //
    // Returns the minimum cost.
    //
    private int _getMinCost(Node path)
    {
        int min = Integer.MAX_VALUE;
        
        while (path != null)
        {
            int cost = residualGraph[path.value.startNodeId][path.value.endNodeId];
            
            min  = Math.min(min, cost);
            
            path = path.next;
        }
        
        return min;
    }
    
    //
    // updateFlow
    //
    // Updates the flow graph along a given path with a given minimum edge cost.
    //
    //      [in] path       - the start node of the path
    //      [in] minCost    - the minimum edge cost
    //
    private void _updateFlow(Node path, int minCost)
    {
        //
        // Go to each edge in the path:
        //
        while (path != null)
        {
            int startId = path.value.startNodeId;
            int endId   = path.value.endNodeId;
          
            //
            // If it's a "real" edge, we're increasing the flow:
            //
            if (graph[startId][endId] != 0)
            {
                flowGraph[startId][endId] += minCost;
            }
            //
            // Otherwise, it's a backwards flowing edge, so we're decreasing the flow
            // in the original direction:
            //
            else
            {
                flowGraph[endId][startId] -= minCost;
            }
            
            //
            // Next edge on the path:
            //
            path = path.next;
        }
    }
    
    //
    // updateResidual
    //
    // Updates the residual graph along a given path with a given minimum edge cost.
    //
    //      [in] path       - the start node of the path
    //      [in] minCost    - the minimum edge cost
    //
    private void _updateResidual(Node path, int minCost)
    {
        //
        // Go to each edge in the path:
        //
        while (path != null)
        {
            int startId = path.value.startNodeId;
            int endId = path.value.endNodeId;
          
            //
            // Decrease along the path chosen:
            //
            residualGraph[startId][endId] -= minCost;
            
            //
            // Increase in the opposite direction:
            //
            residualGraph[endId][startId] += minCost;
            
            //
            // Next edge on the path:
            //
            path = path.next;
        }
    }
    
    //
    // computeMaxFlow
    //
    // Computes the maximum flow of the flow graph.
    //
    // Returns the maximum flow.
    //
    private int _computeMaxFlow()
    {
        int flowTotal = 0;
        
        for (int i = 0; i < flowGraph.length; i++)
        {
            flowTotal += flowGraph[i][sink];
        }
        
        return flowTotal;
    }
    
    //
    // main
    //
    // Optional entry point. This is a demo of the Ford Fulkerson algorithm.
    // Uncomment lines to demo more graphs.
    //
    public static void main(String[] args)
    {
        int[][] graphAdjMatrix = null;
        Flow    f              = null;
        
        graphAdjMatrix = new int[][] {{0, 1}, {0, 0}};
        f              = new Flow(graphAdjMatrix, 0, 1);
        
        System.out.println("\nFlow calculation using Ford Fulkerson: ");
        f.computeMaxFlowFordFulkerson();
        
        System.out.println();
        System.out.println();
        
        /*
        graphAdjMatrix = new int[][] {{0, 1, 1}, {0, 0, 1}, {0, 0, 0}};
        f = new Flow(graphAdjMatrix, 0, 1);
        System.out.println();
        System.out.println();
        
        graphAdjMatrix = new int[][] {{0, 1, 1}, {0, 0, 1}, {0, 0, 0}};
        f = new Flow(graphAdjMatrix, 0, 2);
        System.out.println();
        System.out.println();
        
        graphAdjMatrix = new int[][] {{0, 1, 1}, {0, 0, 1}, {0, 0, 0}};
        f = new Flow(graphAdjMatrix, 1, 2);
        System.out.println();
        System.out.println();
        
        graphAdjMatrix = new int[][] {{0, 1, 2, 0}, {0, 0, 0, 2}, {0, 1, 0, 1}, {0, 0, 0, 0}};
        f = new Flow(graphAdjMatrix, 0, 3);
        System.out.println();
        System.out.println();
        
        graphAdjMatrix = new int[][] {{0, 1, 2, 0}, {0, 0, 0, 2}, {0, 1, 0, 1}, {0, 0, 0, 0}};
        f = new Flow(graphAdjMatrix, 2, 3);
        System.out.println();
        System.out.println();
        
        graphAdjMatrix = new int[][] {{0, 100, 100, 0}, {0, 0, 0, 101}, {0, 1, 0, 99}, {0, 0, 0, 0}};
        f = new Flow(graphAdjMatrix, 0, 3);
        System.out.println();
        System.out.println();
        */
    }
}