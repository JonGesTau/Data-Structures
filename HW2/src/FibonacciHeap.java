import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

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
     	return; // should be replaced by student code
     	
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

        if (heap1.empty()) {
            heap1.size = heap2.size;
            heap1.min = heap2.min;
            heap1.roots = heap2.roots;
        } else {
            heap1.size += heap2.size;
            heap1.min = heap1.min.key < heap2.min.key ? heap1.min : heap2.min;

//            heap1.roots.getLast().next = heap2.roots.getFirst();
//            heap2.roots.getLast().next = heap1.roots.getFirst();
//            heap1.roots.getFirst().previous = heap2.roots.getLast();
            heap1.roots.addAll(heap2.roots);
            updateListLInks(roots);
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
    	return size; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
	    int[] arr = new int[42];
	    arr[min.rank]++;
        HeapNode node = min.next;
        while (node != min) {
            arr[node.rank]++;
            node = node.next;
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
    public void delete(HeapNode x) 
    {    
    	return; // should be replaced by student code
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	return; // should be replaced by student code
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
        int numRoots = 0;
        for (int numTrees : countersRep()) {
            if (numTrees > 0) {
                numRoots++;
            }
        }
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

    public void clear() {
        size = 0;
        min = null;
        marked = 0;
    }

    public void link(HeapNode node1, HeapNode node2) {
        HeapNode parent;
        HeapNode child;
        if (node1.key < node2.key) {
            parent = node1;
            child = node2;
        } else {
            parent = node2;
            child = node1;
        }

        child.previous.next = child.next;
        child.next.previous = child.previous;

        if (parent.child == null) {
            parent.child = child;
            child.next = child.previous = child;
        } else {
            child.previous = parent.child;
            child.next = parent.child.next;

            parent.child.next = child.next.next = child;
        }

        parent.rank++;
        markNode(child, false);

        links++;
    }

    public void cut(HeapNode node1, HeapNode node2) {
        HeapNode parent;
        HeapNode child;
        if (node1.key < node2.key) {
            parent = node1;
            child = node2;
        } else {
            parent = node2;
            child = node1;
        }

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

    }

    public void markNode(HeapNode node, boolean mark) {
        node.isMarked = mark;
        marked = mark ? marked + 1 : marked - 1;
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
