//
// Convert.java
//
// This class encapsulates the main program for testing the maximum flow algorithm.
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

import java.util.*;
import java.io.*;

class Convert
{
    //
    // main
    //
    // Main program entry point. Given an input file, constructs an answer for maximum flow
    // and prints it to the console. The input file name is expected as a command line argument.
    //
    public static void main(String[] args)
    {
        if(args.length != 1) 
        {
            System.out.println("Usage: java Convert filename");
            
            return;
        }
        
        File    inputFile = new File(args[0]);
        _Answer ans       = _getAnswer(inputFile);
        
        if (ans != null)
        {
            System.out.println("***************************************************");
            System.out.println("Max Flow: " + ans.maxFlow);
            System.out.println("Matches:");
            
            for(String setOneNode : ans.matches.keySet())
            {
                System.out.println("\t"+setOneNode + "-->" + ans.matches.get(setOneNode));
            }
            
            System.out.println("***************************************************");
        }
    }
    
    //
    // _Answer
    //
    // This class describes an answer to a particular bipartite matching problem with
    // a computed maximum flow and a collection of bipartite matches.
    //
    private static class _Answer
    {
        int maxFlow = -1;
        
        TreeMap<String,String> matches = new TreeMap<>();
    }
    
    // 
    // _getAnswer
    //
    // Reads a given file to construct an adjacency matrix and computes a maximum matching.
    // The maximum matching is computed using the Ford Fulkerson maximum flow algorithm.
    //
    private static _Answer _getAnswer(File inputFile)
    {
        _Answer answer = new _Answer();
        
        FileReader               fileReader      = null;
        BufferedReader           bufferedReader  = null;
        Vector<String>           srcIndexMap     = new Vector<String>();
        Vector<String>           dstIndexMap     = new Vector<String>();
        TreeMap<String,String[]> adjacencyList   = new TreeMap<String,String[]>();
        
        try
        {
            //
            // Create buffered file reader:
            //
            fileReader     = new FileReader(inputFile);
            bufferedReader = new BufferedReader(fileReader);
            
            //
            // Read each line in the input file and construct adjacency list:
            //
            String line = bufferedReader.readLine();
            
            while (line != null)
            {
                //
                // Separate source node from its destination nodes.
                // If the source node is not in its respective index map, add it:
                //
                String[] pairings = line.split(">", 2); // TODO: find a better name for this variable                
                String   srcNode  = pairings[0];
                
                if (!srcIndexMap.contains(srcNode))
                {
                    srcIndexMap.add(srcNode);
                }
                
                //
                // Separate the string of destination nodes into an array of single destination nodes.
                // For each node, if it is not in its respective index map, add it:
                //
                String[] dstNodes = pairings[1].split(",");

                for (String dstNode : dstNodes)
                {
                    if (!dstIndexMap.contains(dstNode))
                    {
                        dstIndexMap.add(dstNode);
                    }
                }
                
                //
                // Add new entry into adjacency list (ignoring duplicate source node entries):
                //
                if (!adjacencyList.containsKey(srcNode))
                {
                    adjacencyList.put(srcNode, dstNodes);
                }
                
                //
                // Read next line:
                //
                line = bufferedReader.readLine();
            }
            
            //
            // Find maximum matching:
            //
            int[][] flowGraph = _makeFlowGraph(adjacencyList, srcIndexMap, dstIndexMap);
            
            Flow flow = new Flow(flowGraph, 0, flowGraph.length - 1);
            
            flow.computeMaxFlowFordFulkerson();

            int[][] newFlowGraph = flow.getFlowGraph();
            
            answer.maxFlow = flow.getMaxFlow();
            answer.matches = _makeMatchesMap(newFlowGraph, srcIndexMap, dstIndexMap);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
        
        return answer;
    }
    
    //
    // _makeFlowGraph
    //
    // Helper for getAnswer(). Assumes all method parameters are valid.
    // Converts the adjacency list into a flow graph in the form of an adjacency matrix valid 
    // for the Ford Fulkerson algorithm. Uses index maps (implemented as vectors) to convert 
    // string names to integer positions.
    //
    //      [in] adjacencyList      - the resulting flow graph to convert
    //      [in] srcIndexMap        - the source node index map
    //      [in] dstIndexMap        - the destination node index map
    //
    // Returns the flow graph.
    //
    private static int[][] _makeFlowGraph(TreeMap<String, String[]> adjacencyList,
                                          Vector<String> srcIndexMap,
                                          Vector<String> dstIndexMap)
    {
        int     numSrcNodes = srcIndexMap.size();
        int     numDstNodes = dstIndexMap.size();
        int     length      = numSrcNodes + numDstNodes + 2; // +2 for source and sink
        int[][] array       = new int[length][length];
        
        //
        // Initialize array to all zeros (nothing connected):
        //
        for (int i = 0; i < length; ++i)
        {
            for (int j = 0; j < length; ++j)
            {
                array[i][j] = 0;
            }
        }

        //
        // Set flow source as connected to every src node:
        //
        for (int j = 1; j <= numSrcNodes; ++j)
        {
            array[0][j] = 1;
        }
        
        //
        // Set every dst node as connected to flow sink:
        //
        for (int i = numSrcNodes + 1; i < length - 1; ++i)
        {
            array[i][length - 1] = 1;
        }
        
        //
        // Set adjacency between src and dst nodes:
        //
        for (int i = 1; i <= numSrcNodes; ++i)
        {
            String   srcNode  = srcIndexMap.elementAt(i - 1);
            String[] dstNodes = adjacencyList.get(srcNode);
            
            for (int j = 0; j < dstNodes.length; ++j)
            {
                String dstNode  = dstNodes[j];
                int    dstIndex = dstIndexMap.indexOf(dstNode) + numSrcNodes + 1;
                
                array[i][dstIndex] = 1;
            }   
        }
        
        return array;
    }
    
    //
    // _makeMatchesMap
    //
    // Helper for getAnswer(). Assumes all method parameters are valid.
    // Converts the resulting flow graph from the Ford Fulkerson algorithm into a map of matches for printing. 
    // Uses index maps (implemented as vectors) to convert string names to integer positions.
    //
    //      [in] flowGraph      - the resulting flow graph to convert
    //      [in] srcIndexMap    - the source node index map
    //      [in] dstIndexMap    - the destination node index map
    //
    // Returns the map of matches.
    //
    private static TreeMap<String, String> _makeMatchesMap(int[][] flowGraph,
                                                           Vector<String> srcIndexMap,
                                                           Vector<String> dstIndexMap)
    {
        TreeMap<String, String> matches = new TreeMap<String, String>();
        
        int numSrcNodes = srcIndexMap.size();

        for (int i = 1; i <= numSrcNodes; ++i)
        {
            for (int j = numSrcNodes + 1; j < flowGraph.length - 1; ++j)
            {
                int value = flowGraph[i][j];
                
                if (value > 0)
                {
                    String srcNode = srcIndexMap.elementAt(i - 1);
                    String dstNode = dstIndexMap.elementAt(j - (numSrcNodes + 1));
                    
                    matches.put(srcNode, dstNode);
                }
            }
        }
        
        return matches;
    }
}
