package getLeftMostIndex;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLeftMostIndex {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the index of the left-most occurrence of needle in haystack (think of needle and haystack as
     * Strings) or -1 if there is no such occurrence.
     *
     * For example, main.java.getLeftMostIndex("cse332", "Dudecse4ocse332momcse332Rox") == 9 and
     * main.java.getLeftMostIndex("sucks", "Dudecse4ocse332momcse332Rox") == -1.
     *
     * Your code must actually use the sequentialCutoff argument. You may assume that needle.length is much
     * smaller than haystack.length. A solution that peeks across subproblem boundaries to decide partial matches
     * will be significantly cleaner and simpler than one that does not.
     */
    private static int CUTOFF;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {
        CUTOFF = sequentialCutoff;
        return POOL.invoke(new GetLeftMostIndexTask(needle, 0, haystack.length, haystack));
    }

    public static int sequential(char[] needle, int lo, int hi, char[] haystack){
        for(int i = lo; i < hi; i++){
            if (haystack[i] == needle[0]){
                for(int j = 0; j < needle.length; j++){
                    if (i + j >= haystack.length){
                        return -1;
                    } else if (needle[j] != haystack[i+j]){
                        break;
                    } else if (j == needle.length - 1){
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private static class GetLeftMostIndexTask extends RecursiveTask<Integer>{
        char[] needle;
        char[] haystack;
        int lo, hi;

        public GetLeftMostIndexTask(char[] needle, int lo, int hi, char[] haystack){
            this.needle = needle;
            this.haystack = haystack;
            this.lo = lo;
            this.hi = hi;
        }
        @Override
        protected Integer compute() {
            if (hi-lo <= CUTOFF){
                return sequential(needle, lo, hi, haystack);
            }
            int mid = lo + (hi-lo)/2;
            GetLeftMostIndexTask left = new GetLeftMostIndexTask(needle, lo, mid, haystack);
            GetLeftMostIndexTask right = new GetLeftMostIndexTask(needle, mid, hi, haystack);
            left.fork();
            int rRes = right.compute();
            int lRes = left.join();
            if(lRes != -1 && rRes != -1) {
                return Math.min(lRes, rRes);
            }
            return Math.max(lRes, rRes);
        }
    }


    private static void usage() {
        System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        char[] needle = args[0].toCharArray();
        char[] haystack = args[1].toCharArray();
        try {
            System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}
