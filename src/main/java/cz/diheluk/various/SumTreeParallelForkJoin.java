package cz.diheluk.various;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class SumTreeParallelForkJoin {

   static class SumPrefixRecursiveAction extends RecursiveAction {
        private long[] input;
        private int startIndex, endIndex;

        public SumPrefixRecursiveAction(long[] input, int startIndex, int endIndex) {
            this.input = input;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        protected void compute() {
            if (startIndex == endIndex) {
                return;
            }

            int middleIndex = (startIndex + endIndex) / 2;

            SumPrefixRecursiveAction left = new SumPrefixRecursiveAction(input, startIndex, middleIndex);
            left.fork();

            new SumPrefixRecursiveAction(input, middleIndex + 1, endIndex).compute();

            left.join();

//            IntStream.range(middleIndex + 1, end + 1)
//                    .parallel()
//                    .forEach(i -> input[i] += input[middleIndex]);

            for(int i = middleIndex + 1; i < endIndex +1; ++i) {
                input[i] += input[middleIndex];
            }
        }
    }

    public static void main(String[] args) {
        ForkJoinPool pool = ForkJoinPool.commonPool();

        // even length
        long[] input = { 1, 2, 3, 4 };

        SumPrefixRecursiveAction action = new SumPrefixRecursiveAction(input, 0, input.length - 1);
        pool.invoke(action);

        System.out.println(Arrays.toString(input));
    }
}
