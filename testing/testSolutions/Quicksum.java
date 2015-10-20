import java.util.*;

public class Quicksum {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        for (;;) {
            String s = scan.nextLine();
            if (s.trim().equals("#")) return;
            int sum = 0;
            int count = 1;
            for (Character c : s.toCharArray()) {
                if (c == ' ') {
                    count++;
                    continue;
                }
                sum += count++ * (c - 'A' + 1);
            }
            System.out.println(sum);
        }
    }
}