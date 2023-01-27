/*
   Ivan Fransazov D block Datastructures Project 8 Huffman Coding
   Goal: create a HuffmanTree class that builds a HuffmanTree given an array of information,
   use that tree to create a .code file, to reconstruct a HuffmanTree from input from a .code file,
   and to decode an encoded message using the rebuilt tree and output that into a file.
*/
import java.util.PriorityQueue;
import java.util.Queue;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Stack;

public class HuffmanTree
{
   private Queue<HuffmanNode> HuffmanNodes; // priority queue of the all HuffmanNodes
   
   // creates a binary HuffmanTree from a passed in array where the index is the ASCII value
   // and the value at that index is its occurrences
   public HuffmanTree(int[] counts)
   {
      HuffmanNodes = new PriorityQueue<HuffmanNode>();
      
      createQueue(counts); // populate queue with solo HuffmanNodes
      createTree(); // merge them together to create HuffmanTree
   }
   
   // given an array as mentioned in the constructor, create single HuffmanNodes with the ASCII
   // values and frequency, then populate HuffmanNodes with the "solo" HuffmanNodes
   private void createQueue(int[] counts)
   {
      for (int i = 0; i < counts.length; i++)
      {
         if (counts[i] == 0) // if it doesn't occur (frequency is 0)
         {
            continue; 
         }
         // otherwise, add a single HuffmanNode to HuffmanNodes
         // frequency is value at index i, value is the index i
         HuffmanNodes.add(new HuffmanNode(counts[i], (char) i)); 
      }
      //End Of File (EOF) node
      // occurs once (frequency = 1) and has ASCII value one larger than the array
      HuffmanNodes.add(new HuffmanNode(1, (char) counts.length));
   }
   
   // combines all the single HuffmanNodes into a HuffmanTree
   // starts by removing the front two nodes (smallest frequency), combining them, and creating
   // a node w/ ASCII value of 0 (null) and adds that mini tree back into the queue
   // continues with that process until there is one HuffmanNode left (overallRoot)
   private void createTree()
   {
      while (HuffmanNodes.size() > 1) // while there are multiple nodes left...
      {
         HuffmanNode one = HuffmanNodes.remove(); // remove the first node (lowest freq)
         HuffmanNode two = HuffmanNodes.remove(); // remove the next front node (next lowest freq)
      
         int totalFrequency = one.frequency + two.frequency;
         // create a new node that connects the previous removed nodes, and has a value of NULL
         HuffmanNode newRoot = new HuffmanNode(totalFrequency, (char) 0, one, two);
         HuffmanNodes.add(newRoot); // add it back into the queue
      }
   }
   
   // given a PrintStream object, writes the code file into the determined file
   public void write(PrintStream output)
   {
      BitOutputStream bitOutput = new BitOutputStream(output, true);
      write(HuffmanNodes.peek(), bitOutput, output, "");
   }
   // given a HuffmanNode, a BitOutputStream, PrintStream, and a String for accumulation of bits
   // traverses the tree until it reaches a leaf, then writes its char value as an int
   // and the code to reach that node (in 0s and 1s)
   private void write(HuffmanNode root, BitOutputStream bitOutput, PrintStream output, String bits)
   {
      if (root.left == null && root.right == null) // if root is a leaf...
      {
         // output the char value as an int, and move down a line
         output.print((int) root.value + "\n");
         
         // for each character in the String bits...
         for (char c : bits.toCharArray())
         {
            // '0' - '0' = 0 and '1' - '0' = 1
            // bc of the ASCII table ordering
            bitOutput.writeBit(c - '0'); // write it as an int
         }
         
         output.print("\n");
         return;
      }
      
      bits += "0"; // add a 0, and traverse the left side
      write(root.left, bitOutput, output, bits);
      // remove the previous addition, after jumping out of the left tree
      bits = bits.substring(0, bits.length() - 1); 
      
      bits += "1"; // add a 1, and traverse right side
      write(root.right, bitOutput, output, bits);
      // remove the previous addition, after jumping out of the right tree
      bits = bits.substring(0, bits.length() - 1);
   }
   
   //////////////
   //decryption//
   //////////////
   
   // builds a HuffmanTree given a scanner of a .code file
   public HuffmanTree(Scanner input)
   {
      HuffmanNodes = new PriorityQueue<HuffmanNode>();
      // create dummy root w/ negative frequency and NULL value
      HuffmanNode overallRoot = new HuffmanNode(-1, (char) 0);
      
      HuffmanNodes.add(overallRoot);
      
      // build tree
      overallRoot = buildTree(overallRoot, input);
   }
   
   // given a HuffmanNode, and a Scanner on the .code file,
   // reads the input and builds the HuffmanTree
   private HuffmanNode buildTree(HuffmanNode root, Scanner input)
   { 
      while (input.hasNextLine()) // while there is input left...
      {
         // get the ASCII value as an int (I.E charInt = 97)
         int charInt = Integer.parseInt(input.nextLine());
         // get the path to the leaf as a String (I.E charCode = "01") 
         String charCode = input.nextLine(); 
         // convert the charCode from a String int a stack of 0s and 1s
         Stack<Integer> bitsStack = stringToStack(charCode);
         
         // find farthest EXISTING node in the current tree
         HuffmanNode farthestNode = findFarthestNode(root, bitsStack);
         
         // add the rest of the path from that node
         addPath(farthestNode, charInt, bitsStack);
      }
      return root;
   }
   
   // given a String of 0s and 1s, returns a Stack that mimics the order
   // I.E: "011" --> (top) 0, 1, 1 (bottom)
   private Stack<Integer> stringToStack(String bits)
   {
      Stack<Integer> bitsStack = new Stack<Integer>();
      // start at the end of the String bc Stack adds from the top
      for (int i = bits.length() - 1; i >= 0; i--)
      {
         int bit = bits.charAt(i) - '0'; // get the bit as an int
         bitsStack.push(bit); 
      }
      return bitsStack;
   }
   
   // given a HuffmanNode, a charInt (ASCII value), and a Stack of the path to leaf
   // adds the rest of the path to the leaf on the tree
   private void addPath(HuffmanNode root, int charInt, Stack<Integer> bitsStack)
   {
      int nextMove = bitsStack.peek(); 
      // finds out if next node on path is left/right (true means next move is left)
      boolean isLeft = (nextMove == 0); 
      
      if (bitsStack.size() == 1) // if the next node will be the leaf w/ ASCII value
      {
         if (isLeft) // if it is a left move...
         {
            // add the node w/ charInt to the left of root
            root.left = new HuffmanNode(-1, (char) charInt); 
         }
         else
         {
            // otherwise, add the node w/ charInt to the right of root
            root.right = new HuffmanNode(-1, (char) charInt);
         }
         return;
      }
      
      // otherwise, add the next dummy node on the path (ASCII value = 0)
      nextNode(root, charInt, bitsStack, isLeft);
   }
   
   // given a HuffmanNode, the charInt, the Stack of the code path, 
   // and the left or right path boolean, adds the next dummy node to continue the path
   private void nextNode(HuffmanNode root, int charInt, Stack<Integer> bitsStack, boolean isLeft)
   {  
      bitsStack.pop(); // removes next step, it's about to finish moving
      if (isLeft) // if the next move is left...
      {
         root.left = new HuffmanNode(-1, (char) 0); // add the dummy node to the left
         root = root.left; // move root
      }
      else // otherwise...
      {
         root.right = new HuffmanNode(-1, (char) 0); // add the dummy node to the right
         root = root.right; // move root
      }
      addPath(root, charInt, bitsStack); 
   }
   
   // given a HuffmanNode, and the Stack of integers that represent the code path,
   // return the farthest existing HuffmanNode along the Stack code path
   private HuffmanNode findFarthestNode(HuffmanNode root, Stack<Integer> bitsStack)
   {
      int nextPath = bitsStack.peek(); // gets the next int
      
      // if the int is 0 and the left path is null, or the int is 1 and the right path is null
      // return the root, that is the farthest existing node
      if ((nextPath == 0 && root.left == null) || nextPath == 1 && root.right == null)
      {
         return root;
      }
      
      bitsStack.pop(); // pop from the top to update the stack before moving the root
      
      // if the int was 0, traverses the left tree, if the int was 1, traverses the right tree
      return (nextPath == 0 ? findFarthestNode(root.left, bitsStack) : 
                  findFarthestNode(root.right, bitsStack));
   }
   
   // given a BitInputStream object, a PrintStream object, and the integer for the EOF value
   // reads each bit from the encoded file and traverse the tree accordingly, once it has found a 
   // leaf, outputs that character and starts over until the EOF char has been found (not printed)
   public void decode(BitInputStream input, PrintStream output, int eof)
   {  
      while (true)
      {   
         // gets the next char to print
         char outputChar = findChar(HuffmanNodes.peek(), input);
         
         if (outputChar == 256) // if that char is the EOF char...
         {
            break; // stop decoding
         }
         
         output.write(outputChar); // otherwise, print that char and start over  
      } 
   }
   
   // given a HuffmanNode, and the BitInputStream of the encoded file,
   // traverses the HuffmanTree using the bits until it reaches a leaf,
   // it returns the char value of the leaf 
   private char findChar(HuffmanNode root, BitInputStream input)
   {
      if (root.left == null && root.right == null) // if it's a leaf
      {
         return root.value; // return the char value
      }
      
      // otherwise, if the next bit is 0, find the char in the left tree, 
      // else find the char in the right tree 
      return (input.readBit() == 0 ? findChar(root.left, input) : findChar(root.right, input));
   }
   
   /////////////////////
   //HuffmanNode Class//
   /////////////////////
   
   private class HuffmanNode implements Comparable<HuffmanNode>
   {
      public int frequency;     // number of occurrences 
      public char value;        // value of the node 
      public HuffmanNode left;  // left Node
      public HuffmanNode right; // right Node
      
      // given a frequency and value
      // constructs a single HuffmanNode with null left and right children
      public HuffmanNode(int frequency, char value)
      {
         this (frequency, value, null, null);
      }
      
      // given a frequency, a value, a left node, and a right node,
      // constructs a single HuffmanNode
      public HuffmanNode(int frequency, char value, HuffmanNode left, HuffmanNode right)
      {
         this.frequency = frequency;
         this.value = value;
         this.left = left;
         this.right = right;
      }
      
      // orders HuffmanNodes from smallest --> largest based on frequency
      public int compareTo(HuffmanNode other)
      {
         return this.frequency - other.frequency;
      }
      
      // returns a String with information about the frequency and value
      public String toString()
      {
         return (value == 0 ? "freq: " + frequency + ", value: NULL" : "freq: " + frequency + ", value: " + value);
      }
   }
   // fin :)
}