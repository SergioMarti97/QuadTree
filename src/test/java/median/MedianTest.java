package median;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class MedianTest {

    public static int calMedianIndex(int left, int right) {
        return (right - left) / 2;
    }

    public static float calMedian(List<Integer> list, int left, int right) {
        int m = calMedianIndex(left, right);
        if (list.size() % 2 == 0) {
            return (list.get(m - 1) + list.get(m)) / 2f;
        } else {
            return list.get(m);
        }
    }

    public static void main(String[] args) {

        final int NUM = 10;

        List<Integer> list = new ArrayList<>();

        Random rnd = new Random();
        rnd.setSeed(123);
        for (int i = 0; i < NUM; i++) {
            list.add(rnd.nextInt(255));
        }

        // Calcule the median
        list.sort(Integer::compareTo);

        int left = 0;
        int right = list.size();
        int m = calMedianIndex(left, right);
        float median = calMedian(list, left, right);

        // Show results
        System.out.printf("Número de elementos: %d\n", list.size());
        StringBuilder out = new StringBuilder("Números:\n");
        for (int i = 0; i < list.size(); i++) {
            out.append("Elemento ").append(i).append(": ").append(list.get(i)).append("\n");
        }
        System.out.println(out);
        System.out.printf("Índice mediana: %d\n", m);
        System.out.printf("Mediana: %.3f\n", median);
    }

}
