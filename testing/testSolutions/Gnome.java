import java.util.*;

public class Gnome {
    public static void main(String[] args) {
        System.out.println("Gnomes:");
        Scanner scan = new Scanner(System.in);
        for (int n = scan.nextInt(); n > 0; n--) {
            int a = scan.nextInt();
            int b = scan.nextInt();
            int c = scan.nextInt();
            if (a > b && b > c) {
                System.out.println("Ordered");
            } else if (a < b && b < c) {
                System.out.println("Ordered");
            } else {
                System.out.println("Unordered");
            }
        }
    }
}