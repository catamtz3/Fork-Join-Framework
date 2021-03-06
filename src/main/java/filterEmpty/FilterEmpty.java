package filterEmpty;

import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();

    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns an array with the lengths of the non-empty strings from arr (in order)
     * For example, if arr is ["", "", "cse", "332", "", "hw", "", "7", "rox"], then
     * main.java.filterEmpty(arr) == [3, 3, 2, 1, 3].
     *
     * A parallel algorithm to solve this problem in O(lg n) span and O(n) work is the following:
     * (1) Do a parallel map to produce a bit set
     * (2) Do a parallel prefix over the bit set
     * (3) Do a parallel map to produce the output
     *
     * In lecture, we wrote parallelPrefix together, and it is included in the gitlab repository.
     * Rather than reimplementing that piece yourself, you should just use it. For the other two
     * parts though, you should write them.
     *
     * Do not bother with a sequential cutoff for this exercise, just have a base case that processes a single element.
     */
    public static int[] filterEmpty(String[] arr) {
        int[] bits = mapToBitSet(arr);
        int[] bitsadd = ParallelPrefixSum.parallelPrefixSum(bits);
        return mapToOutput(arr, bits, bitsadd);
    }

    public static int[] mapToBitSet(String[] arr) {
        int[] set = new int[arr.length];
        POOL.invoke(new BitSet(set, arr, 0, set.length, 1));
        return set;
    }

    public static class BitSet extends RecursiveAction{
        int[] set;
        int lo, hi, cutOff;
        String[] arr;

        public BitSet(int[] set, String[] arr, int lo, int hi, int cutOff){
            this.set = set;
            this.lo = lo;
            this.hi = hi;
            this.cutOff = cutOff;
            this.arr = arr;
        }

        public void compute(){
            if(hi - lo >  cutOff){
                int mid = lo + (hi-lo) / 2;
                BitSet l = new BitSet(set, arr, lo, mid, cutOff);
                BitSet r = new BitSet(set, arr, mid, hi, cutOff);
                r.fork();
                l.compute();
                r.join();
            } else {
                for(int i = lo; i < hi; i++){
                    set[i] = (arr[i].isEmpty() || arr[i] == null) ? 0 : 1;
                }
            }
        }
    }

    public static int[] mapToOutput(String[] input, int[] bits, int[] bitsadd) {
        int[] res = new int[bitsadd.length > 0 ? bitsadd[bits.length-1] : 0];
        POOL.invoke(new Finish(res, input, bitsadd, 0, input.length, 1));
        return res;
    }

    public static class Finish extends RecursiveAction{
        String[] arr;
        int[] set;
        int[] bitsadd;
        int lo, hi, cutOff;

        public Finish(int[] tempSet, String[] arr, int[] bitsum, int lo, int hi, int cutOff){
            this.set = tempSet;
            this.arr = arr;
            this.bitsadd = bitsum;
            this.lo = lo;
            this.hi = hi;
            this.cutOff = cutOff;
        }

        @Override
        protected void compute() {
            if(hi-lo > cutOff){
                int mid = (hi - lo) / 2 + lo;
                Finish left = new Finish(set, arr, bitsadd, lo, mid, cutOff);
                Finish right = new Finish(set, arr, bitsadd, mid, hi, cutOff);
                right.fork();
                left.compute();
                right.join();
            } else {
                for(int i = lo; i < hi; i++){
                    if((i > 0 ? bitsadd[i - 1] : 0) < bitsadd[i]){
                        set[bitsadd[i]-1] = arr[i].length();
                    }
                }
            }
        }
    }

    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}