//
// Node.java
//
// This class describes a node of a graph with a reference to the edge it belongs to and
// a reference to the next node it connects to.
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

class Node
{
    public Edge value;
    public Node next;

    //
    // Node
    //
    // Default constructor.
    //
    public Node()
    {
        value = null;
        next  = null;
    }
    
    //
    // Node
    //
    // Overloaded constructor. Constructs with a given edge.
    //
    public Node(Edge value)
    {
        this.value = value;
        this.next  = null;
    }
    
    //
    // Node
    //
    // Overloaded constructor. Constructs with a given edge and next node.
    //
    public Node(Edge value, Node next)
    {
        this.value = value; 
        this.next  = next;
    }
    
    //
    // toString
    //
    // Converts the node to a string value of format "Edge.toString NextEdge.toString ...".
    // See Edge.toString for more details.
    //
    // Returns the string.
    //
    public String toString()
    {
        StringBuilder ret  = new StringBuilder();
        Node          curr = this;
        
        while (curr != null)
        {
            ret.append(curr.value);
            
            if (curr.next != null) 
            {
                ret.append(" ");
            }
            
            curr = curr.next;
        }
        
        return ret.toString();
    }
}