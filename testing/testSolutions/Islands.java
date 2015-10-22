import java.util.*;

public class Islands {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        for (int n = scan.nextInt(); n > 0; n--) {
            System.out.print(scan.nextInt() + " ");
            int[] islands = new int[15];
            for (int i = 0; i < 15; i++) {
                // System.out.print(i + "-");
                islands[i] = scan.nextInt();
            }

            int small = islands[0];
            int islandCount = 0;
            for (int num : islands) {
                if (num > small) {
                    islandCount++;
                } 
                small = num;
            }
            System.out.println(islandCount);
        }
    }

}