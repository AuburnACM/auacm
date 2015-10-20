import java.util.*;

public class SortMe {

    public static void main(String[] args) {
        int count = 1;
        Scanner scan = new Scanner(System.in);
        for (int num = scan.nextInt(); num > 0; ) {
            char[] values = scan.next().toCharArray();
            final int[] alphabet = new int[256];
            for (int i = 0; i < values.length; i++) {
                alphabet[values[i]] = i;
            }
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                strings.add(scan.next());
            }
            Collections.sort(strings, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    int loop = Math.min(s1.length(), s2.length());
                    for (int i = 0; i < loop; i++) {
                        int diff = alphabet[s1.charAt(i)] - alphabet[s2.charAt(i)];
                        if (diff != 0) {
                            return diff;
                        }
                    }
                    return s1.length() - s2.length();
                }

                public boolean equals(Object o) {
                    return false;
                }
            });

            System.out.println("year " + count++);
            for (int i = 0; i < num; i++) {
                System.out.println(strings.get(i));
            }
            num = scan.nextInt();
        }

    }

}