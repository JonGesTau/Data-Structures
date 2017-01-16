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

    private static int links;
    private static int cuts;

    public static final int MAX_BUCKETS = 42;

    public FibonacciHeap() {
        this.size = 0;
        this.min = null;
        this.marked = 0;
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
        HeapNode node = new HeapNode(key);
        node.previous = node.next = node;

        FibonacciHeap heap = new FibonacciHeap();
        heap.size = 1;
        heap.min = node;

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
                HeapNode[] children = getSiblingsArray(originalMin.child);
     	        for (HeapNode child : children) {
     	            child.parent = null;
                }

                HeapNode minPrevious = min.previous;
     	        HeapNode originallMinChildPrevious = originalMin.child.previous;
     	        min.previous = originallMinChildPrevious;
     	        originallMinChildPrevious.next = min;
     	        originalMin.child.previous = minPrevious;
     	        minPrevious.next = originalMin.child;
            }

            originalMin.previous.next = originalMin.next;
     	    originalMin.next.previous = originalMin.previous;

     	    if (originalMin == originalMin.next) {
     	        min = null;
            } else {
     	        min = originalMin.next;
     	        min = findMin();
     	        consolidate();
            }

            size--;
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
    	HeapNode[] roots = getRootsArray();
    	HeapNode newMin = roots[0];

    	for (HeapNode root : roots) {
    	    if (root.key < newMin.key) {
    	        newMin = root;
            }
        }

        return newMin;
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
            heap1.size = heap2.size;
            heap1.min = heap2.min;
        } else {
            heap1.min.next.previous = heap2.min.previous;
            heap2.min.previous.next = heap1.min.next;

            heap1.min.next = heap2.min;
            heap2.min.previous = heap1.min;

            heap1.size += heap2.size;
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
	    int[] arr = new int[MAX_BUCKETS];
	    HeapNode[] roots = getRootsArray();

	    for (HeapNode root : roots) {
	        arr[root.rank]++;
        }

        return arr; //	 to be replaced by student code
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
        heap.clear();
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
    	decreaseKey(x, -1);
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
    	int newKey = delta == -1 ? -1 : node.key - delta;

    	if (delta != -1 && newKey < 0) {
    	    throw new IllegalArgumentException("Delta must be equal or smaller than the current key");
        }

        node.key = newKey;
    	HeapNode parent = node.parent;

    	if (parent != null && newKey < parent.key) {
    	    cut(parent, node);
    	    cascadingCut(parent);
        }

        if (newKey < min.key) {
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
        HeapNode[] roots = getRootsArray();

    	return roots.length + 2 * marked;
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

    public void clear() {
        size = 0;
        min = null;
        marked = 0;
    }

    public void link(HeapNode node1, HeapNode node2) {
        HeapNode parent = parentAndChild(node1, node2)[0];
        HeapNode child = parentAndChild(node1, node2)[1];

        child.previous.next = child.next;
        child.next.previous = child.previous;

        if (parent.child == null) {
            parent.child = child;
            child.next = child.previous = child;
        } else {
            child.previous = parent.child;
            child.next = parent.child.next;

            parent.child.next = child.next.next = child;
            child.parent = parent;
        }

        parent.rank++;
        markNode(child, false);

        links++;
    }

    public void cut(HeapNode node1, HeapNode node2) {
        HeapNode parent = parentAndChild(node1, node2)[0];
        HeapNode child = parentAndChild(node1, node2)[1];

        child.previous.next = child.previous;
        child.next.previous = child.next;
        parent.rank--;

        if (parent.rank == 0) {
            parent.child = null;
        } else if (parent.child == child) {
            parent.child = child.next;
        }

        child.previous = min.previous;
        child.next = min;
        min.previous = child.previous.next = child;

        child.parent = null;
        markNode(child, false);

        cuts++;
    }

    public void cascadingCut(HeapNode node) {
        HeapNode parent = node.parent;
        if (parent != null) {
            if (!node.isMarked) {
                markNode(node, true);
            } else {
                cut(parent, node);
                cascadingCut(parent);
            }
        }
    }

    public void consolidate() {
        HeapNode[] buckets = new HeapNode[MAX_BUCKETS];
        HeapNode[] roots = getRootsArray();

        for (HeapNode root : roots) {
            HeapNode node1 = root;
            int rank = node1.rank;
            while (buckets[rank] != null) {
                HeapNode node2 = buckets[rank];
                HeapNode parent = parentAndChild(node1, node2)[0];
                HeapNode child = parentAndChild(node1, node2)[1];


                link(parent, child);
                buckets[rank] = null;
                rank++;
                node1 = parent;
            }

            buckets[rank] = node1;
        }

        min = null;

        for (HeapNode node : buckets) {
              if (node != null) {
                if (min != null) {
                    node.previous.next = node.next;
                    node.next.previous = node.previous;

                    node.previous = min;
                    node.next = min.next;
                    min.next = node;
                    node.next.previous = node;

                    min = node.key < min.key ? node : min;
                } else {
                    min = node;
                }
            }
        }
    }

    public void markNode(HeapNode node, boolean mark) {
        node.isMarked = mark;
        marked = mark ? marked + 1 : marked - 1;
        if (marked < 0) {
            marked = 0;
        }
    }

    public HeapNode[] parentAndChild(HeapNode node1, HeapNode node2) {
        HeapNode[] result = new HeapNode[2];
        HeapNode parent;
        HeapNode child;
        if (node1.key < node2.key) {
            parent = node1;
            child = node2;
        } else {
            parent = node2;
            child = node1;
        }

        result[0] = parent;
        result[1] = child;

        return result;
    }

    public HeapNode[] getRootsArray() {
        return getSiblingsArray(min);
    }

    public HeapNode[] getSiblingsArray(HeapNode node) {
        ArrayList<HeapNode> siblings = new ArrayList<>();
        HeapNode firstNode = node;

        if(node != null) {
            siblings.add(node);
            node = node.next;

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
        String info;
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
