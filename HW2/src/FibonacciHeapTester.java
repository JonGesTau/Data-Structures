/**
 * Created by JonathanGescheit on 1/7/17.
 */
public class FibonacciHeapTester {
    public static void main(String[] args) {
//        FibonacciHeap heap2 = new FibonacciHeap();

//        heap1.insert(6);
//        heap2.insert(8);
//        heap1.meld(heap2);
//        heap1.insert(1);
//        heap1.insert(2);
//        heap1.insert(3);
//        heap1.insert(4);
//        heap1.deleteMin();
//        heap1.deleteMin();

//        heap1.insert(4);
//        FibonacciHeap.HeapNode node = heap1.insert(134);
//        heap1.insert(76);
//        heap1.insert(26);
//        heap1.insert(13);
//        heap1.insert(6);
//        heap1.deleteMin();
//        System.out.println("done");

        // Asymptotic tests
        FibonacciHeap heap1 = new FibonacciHeap();
        int rounds = 1000;

        long startTime = System.nanoTime();

        for (int j = rounds; j > 0; j--) {
            heap1.insert(j);
        }

//        for (int j = rounds; j > 0; j -= 2) {
//            heap1.deleteMin();
//            System.out.println("Total links, round " + j + ": " + heap1.totalLinks());
//        }

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000;
        System.out.println(rounds + " time: " + duration);
        System.out.println("Total links: " + heap1.totalLinks());
        System.out.println("Total cuts: " + heap1.totalCuts());
        System.out.println("Potential: " + heap1.potential());
    }
}
