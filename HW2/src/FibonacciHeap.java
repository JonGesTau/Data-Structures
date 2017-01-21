import java.util.*;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap {
    private int size;
    private HeapNode min;

    private int marked;
    private int numRoots;

    private static int links;
    private static int cuts;

    public FibonacciHeap() {
        this.size = 0;
        this.min = null;
        this.marked = 0;
        this.numRoots = 0;
    }


   /**
    * public boolean empty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean empty()
    {
    	return size == 0;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)
    {
        // Create a heap with only one node
        HeapNode node = new HeapNode(key);
        node.previous = node.next = node;

        FibonacciHeap heap = new FibonacciHeap();
        heap.size = 1;
        heap.numRoots = 1;
        heap.min = node;

        // Meld the created heap with the destination heap
        this.meld(heap);

        return node;
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
     	if (min != null) {
     	    HeapNode originalMin = min;
     	    if (originalMin.child != null) {
     	        // If the origial min node has children, remove the original min as their parent
                HeapNode[] children = getSiblingsArray(originalMin.child);
     	        for (HeapNode child : children) {
     	            child.parent = null;
                }

                // insert original min's children to the roots list
                if (originalMin.next != originalMin) {
     	            // The case where there are more roots other than the original min
                    children[0].previous = originalMin.previous;
                    children[children.length - 1].next = originalMin.next;
                    originalMin.previous.next = children[0];
                    originalMin.next.previous = children[children.length - 1];
                } else {
     	            // The case where original min was the only root
                    children[0].previous = children[children.length - 1];
                    children[children.length - 1].next = children[0];
                }

                min = originalMin.child;
            } else {
     	        // If the original min has no children, simply remove it from the roots list
                originalMin.previous.next = originalMin.next;
                originalMin.next.previous = originalMin.previous;

                if (originalMin == originalMin.next) {
                    // If the original min was the only root, and it had no children, our heap is empty
                    min = null;
                } else {
                    // Otherwise, we'll set the new min as the original min's sibling for now (will update later)
                    min = originalMin.next;
                }
            }

            if (min != null) {
     	        // If our heap is not empty, find the new min and consolidate
                HeapNode[] roots = getRootsArray();
                HeapNode newMin = roots[0];

                // Iterate over roots list and get the new minimum
                for (HeapNode root : roots) {
                    if (root.key < newMin.key) {
                        newMin = root;
                    }
                }

                min = newMin;
                consolidate();
                size--;
            } else {
     	        this.clear();
            }

        }
     	
    }

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
        return min;
    }
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
        FibonacciHeap heap1 = this;

        if (heap2.empty()) {
            System.out.println("Other heap is empty");
        } else if (heap1.empty()) {
            // If the current heap is empty, and the other heap isn't, the current heap just becomes the other heap
            heap1.size = heap2.size;
            heap1.min = heap2.min;
        } else {
            // If both heaps are not empty, concatenate roots
            heap1.min.next.previous = heap2.min.previous;
            heap2.min.previous.next = heap1.min.next;

            heap1.min.next = heap2.min;
            heap2.min.previous = heap1.min;

            // Update size of the heap
            heap1.size += heap2.size;

            // Update num of marked nodes in the heap
            heap1.marked += heap2.marked;

            // Update num of roots in the heap
            heap1.numRoots += heap2.numRoots;

            // Update minimum of the heap
            heap1.min = heap1.min.key < heap2.min.key ? heap1.min : heap2.min;
        }
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
        int bucketsSize = (int) (Math.floor(Math.log(size) / (Math.log((1.0 + Math.sqrt(5.0)) / 2.0))) + 1);
        int[] arr = new int[bucketsSize];
	    HeapNode[] roots = getRootsArray();

	    // Iterate over roots list and update the array
	    for (HeapNode root : roots) {
	        arr[root.rank]++;
        }

        return arr;
    }

   /**
    * public void arrayToHeap()
    *
    * Insert the array to the heap. Delete previous elements in the heap.
    * 
    */
    public void arrayToHeap(int[] array)
    {
        FibonacciHeap heap = this;

        // Delete previous elements in the heap
        heap.clear();

        // Insert every element in the array to the heap
        for (int key : array) {
            heap.insert(key);
        }
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) {
        // Decrease key of the node until it's smaller than the minimum
    	decreaseKey(x, -1);

    	// Delete the minimum (which is the node we want to delete)
    	deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta) {
    	HeapNode node = x;

    	// If delta is -1, we prepare the node for deletion
    	int newKey = delta == -1 ? -1 : node.key - delta;

    	if (delta != -1 && newKey < 0) {
    	    throw new IllegalArgumentException("Delta must be equal or smaller than the current key");
        }

        node.key = newKey;
    	HeapNode parent = node.parent;

    	if (parent != null && newKey < parent.key) {
    	    // If the node has a parent and it's new key is smaller than it's parent, cut it from it's parent and perform a cascading cut on it's parent
    	    cut(parent, node);
    	    cascadingCut(parent);
        }

        if (newKey < min.key) {
    	    // Update the minimum if the new key is smaller than the minimum's
    	    min = node;
        }
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {
    	return numRoots + 2 * marked;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return links;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return cuts;
    }

    /**
     * Clear the heap. Resets all of it's relevant params
     */
    private void clear() {
        size = 0;
        min = null;
        marked = 0;
        numRoots = 0;
    }

    /**
     * Link two nodes
     * @param parent the parent node
     * @param child  the child node
     */
    private void link(HeapNode parent, HeapNode child) {
        // Remove child from it's current position
        child.previous.next = child.next;
        child.next.previous = child.previous;

        if (parent.child == null) {
            // If the parent has no other children, make child the parent's only child
            parent.child = child;
            child.next = child.previous = child;
        } else {
            // Otherwise, insert child in to the parent's children list
            child.previous = parent.child;
            child.next = parent.child.next;
            child.next.previous = child;
            child.previous.next = child;

            // Update the child's parent reference
            child.parent = parent;
        }

        // Update the parent's rank - added one child
        parent.rank++;

        // Unmark child
        markNode(child, false);

        // Update total links counter
        links++;
    }

    /**
     * Cut one node from the other
     * @param node1 first node
     * @param node2 second node
     */
    private void cut(HeapNode node1, HeapNode node2) {
        // Decide who's the parent and who's the child
        HeapNode parent = node1.key < node2.key ? node1 : node2;
        HeapNode child = parent == node1 ? node2 : node1;

        // Remove child from the children list
        child.previous.next = child.next;
        child.next.previous = child.previous;

        // Update the parent's rank (down by 1)
        parent.rank--;

        if (parent.rank == 0) {
            // If after removing the child the parent's rank is 0, he has no children
            parent.child = null;
        } else if (parent.child == child) {
            // Otherwise, update the child reference
            parent.child = child.next;
        }

        // Make child a root
        child.previous = min.previous;
        child.next = min;
        min.previous = child.previous.next = child;

        child.parent = null;

        // Unmark child
        markNode(child, false);

        // Update total cuts counter
        cuts++;
    }

    /**
     * Perform a cascading cut
     * @param node the node we want to cascade cut from
     */
    private void cascadingCut(HeapNode node) {
        HeapNode parent = node.parent;
        if (parent != null) {
            // If there's a parent
            if (!node.isMarked) {
                // If the node is not marked, mark it
                markNode(node, true);
            } else {
                // Otherwise, cut the node and perform a cascading cut on it's parent
                cut(parent, node);
                cascadingCut(parent);
            }
        }
    }

    /**
     * Consolidate the heap
     */
    private void consolidate() {
        int bucketsSize = (int) (Math.floor(Math.log(size) / (Math.log((1.0 + Math.sqrt(5.0)) / 2.0))) + 1);
        HeapNode[] buckets = new HeapNode[bucketsSize];
        HeapNode[] roots = getRootsArray();

        // Iterate over roots
        for (HeapNode root : roots) {
            HeapNode node1 = root;
            int rank = node1.rank;
            while (buckets[rank] != null) {
                // We get here if we saved a tree of the current rank for later
                HeapNode node2 = buckets[rank];
                HeapNode parent = node1.key < node2.key ? node1 : node2;
                HeapNode child = parent == node1 ? node2 : node1;

                // Link two trees with the same rank
                link(parent, child);

                // Remove the tree we saved for later
                buckets[rank] = null;

                // Update the parent's rank
                parent.rank++;
                node1 = parent;
            }

            // If we haven't saved any tree for later, save this one in case we find another one with the same rank
            buckets[rank] = node1;
        }

        // Find the new min and rebuild roots list after consolidation
        min = null;
        numRoots = 0;

        // Iterate all of the new roots
        for (HeapNode node : buckets) {
              if (node != null) {
                numRoots++;
                // If there's a node in this index
                if (min != null) {
                    // If we've already set some node as the minimum, insert this node to the root list and see if it's the new minimum
                    node.previous.next = node.next;
                    node.next.previous = node.previous;

                    node.previous = min;
                    node.next = min.next;
                    min.next = node;
                    node.next.previous = node;

                    min = node.key < min.key ? node : min;
                } else {
                    // If this is the first time we see a node, set it as the minimum
                    min = node;
                }
            }
        }
    }

    /**
     * Mark or unmark a node
     * @param node the node we want to mark or unmark
     * @param mark true => mark : false => unmark
     */
    private void markNode(HeapNode node, boolean mark) {
        if (node.isMarked != mark) {
            // Mark or unmark node
            node.isMarked = mark;

            // Update total marks counter
            marked = mark ? marked + 1 : marked - 1;

            // Can't have a negative number of marked nodes
            if (marked < 0) {
                marked = 0;
            }
        }
    }

    /**
     * Get the roots list
     * @return an array containing all root nodes
     */
    private HeapNode[] getRootsArray() {
        // Get the min node's siblings (the other roots)
        return getSiblingsArray(min);
    }

    /**
     * Get the siblings of a node
     * @param node the node we want to get siblings for
     * @return an array containing all sibling nodes
     */
    private HeapNode[] getSiblingsArray(HeapNode node) {
        ArrayList<HeapNode> siblings = new ArrayList<>();
        HeapNode firstNode = node;

        if (node != null) {
            // Add the given node to the array and move to the next one
            siblings.add(node);
            node = node.next;

            // Get all of the next nodes and insert them in to the array
            while (node != firstNode) {
                siblings.add(node);
                node = node.next;
            }
        }

        HeapNode[] result = siblings.toArray(new HeapNode[siblings.size()]);

        return result;
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{
        int key;
        int rank;
        boolean isMarked;
        HeapNode next;
        HeapNode previous;
        HeapNode parent;
        HeapNode child;

        public HeapNode(int key) {
            this.key = key;
        }
    }
}
