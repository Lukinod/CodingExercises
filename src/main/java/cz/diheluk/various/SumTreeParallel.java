package cz.diheluk.various;

import java.util.Arrays;

public class SumTreeParallel {

    static void calculate(long[] input, int startIndex, int endIndex) {
        try {
            if (startIndex == endIndex) {
                return;
            }

            int middleIndex = (startIndex + endIndex) / 2;

            Thread t1 = new Thread(() -> calculate(input, startIndex, middleIndex));
            t1.start();

            Thread t2 = new Thread(() -> calculate(input, middleIndex + 1, endIndex));
            t2.start();

            t1.join();
            t2.join();

            for (int i = middleIndex + 1; i <= endIndex; ++i) {
                input[i] += input[middleIndex];
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // log
        }
    }

    public static void main(String[] args) {
        // even length
        long[] input = { 1, 2, 3, 4 };

        calculate(input, 0, input.length - 1);

        System.out.println(Arrays.toString(input));
    }
}
