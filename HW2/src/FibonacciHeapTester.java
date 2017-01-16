/**
 * Created by JonathanGescheit on 1/7/17.
 */
public class FibonacciHeapTester {
    public static void main(String[] args) {
        FibonacciHeap heap1 = new FibonacciHeap();
        FibonacciHeap heap2 = new FibonacciHeap();

        heap1.insert(6);
        heap2.insert(8);
        heap1.meld(heap2);
        heap1.insert(1);
        heap1.insert(2);
        heap1.insert(3);
        heap1.insert(4);
        heap1.deleteMin();
        heap1.deleteMin();
        System.out.println("done");
    }
}
