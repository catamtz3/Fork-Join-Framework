package getLongestSequence;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLongestSequence {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the length of the longest consecutive sequence of val in arr.
     * For example, if arr is [2, 17, 17, 8, 17, 17, 17, 0, 17, 1], then
     * getLongestSequence(17, arr) == 3 and getLongestSequence(35, arr) == 0.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument. We have provided you with an extra class SequenceRange. We recommend you use this class as
     * your return value, but this is not required.
     */
    private static int CUTOFF;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {
        CUTOFF = sequentialCutoff;
        return POOL.invoke(new GetLongestSequenceTask(val, 0, arr.length, arr));
    }

    public static int sequential(int val, int lo, int hi, int[] arr){
        int count = 0;
        int temp = 0;
        for(int i = lo; i < hi; i++){
            if(arr[i] == val){
                temp++;
            }
        }
        return -1;
    }

    private static class GetLongestSequenceTask extends RecursiveTask<Integer>{
        int[] arr;
        int lo, hi;
        int val;

        public GetLongestSequenceTask(int val, int lo, int hi, int[] arr){
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.val = val;
        }
        @Override
        protected Integer compute() {
            if (hi - lo <= CUTOFF){
                return sequential(val, lo, hi, arr);
            }
            int mid = lo + (hi - lo) / 2;
            GetLongestSequenceTask left = new GetLongestSequenceTask(val, lo, mid, arr);
            GetLongestSequenceTask right = new GetLongestSequenceTask(val, mid, hi, arr);
            left.fork();
            int rResult = right.compute();
            int lResult = left.join();
            return lResult;
        }
    }
//    public static boolean sequential(int[] arr, int lo, int hi, int val){
//        for(int i = lo; i < hi; i++){
//            if (arr[i] >= val){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private static class HasOverTask extends RecursiveTask<Boolean> {
//        int[] arr;
//        int val;
//        int lo, hi;
//
//        public HasOverTask(int[] arr, int lo, int hi, int val){
//            this.arr = arr;
//            this.val = val;
//            this.lo = lo;
//            this.hi = hi;
//        }
//
//        @Override
//        protected Boolean compute() {
//            if (hi - lo <= CUTOFF){
//                return sequential(arr, lo, hi, val);
//            }
//            int mid = lo + (hi - lo) / 2;
//            HasOverTask left = new HasOverTask(arr, lo, mid, val);
//            HasOverTask right = new HasOverTask(arr, mid, hi, val);
//            left.fork();
//            boolean rResult = right.compute();
//            boolean lResult = left.join();
//            return lResult || rResult;
//        }
//    }

    private static void usage() {
        System.err.println("USAGE: GetLongestSequence <number> <array> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int val = 0;
        int[] arr = null;

        try {
            val = Integer.parseInt(args[0]);
            String[] stringArr = args[1].replaceAll("\\s*", "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}