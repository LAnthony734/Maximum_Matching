//
// Edge.java
//
// This class describes an edge of a graph with a start and end node ID.
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

class Edge
{
    public int startNodeId;
    public int endNodeId;
    
    //
    // Edge
    //
    // Overloaded constructor. Constructs with a given start and end node ID.
    //
    public Edge(int startNodeId, int endNodeId)
    {
        this.startNodeId = startNodeId;
        this.endNodeId   = endNodeId;
    }
    
    //
    // toString
    //
    // Converts the edge to a string value of format "startNodeID-->endNodeID".
    //
    // Returns the string.
    //
    public String toString()
    {
        return startNodeId + "-->" + endNodeId;
    }
}
