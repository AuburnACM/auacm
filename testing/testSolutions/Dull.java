import java.util.*;

public class Dull {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        for (int n = scan.nextInt(); n != 0; n = scan.nextInt()) {
            int p = scan.nextInt();
            int s = scan.nextInt();

            HashMap<Character, Integer> dlls = new HashMap<Character, Integer>();
            for (char c = 'A'; c - 'A' < n; c++) {
                dlls.put(c, scan.nextInt());
            }

            ArrayList<Prog> progs = new ArrayList<>();
            for (int i = 0; i < p; i++) {
                progs.add(new Prog(scan.nextInt(), scan.next().toCharArray()));
            }

            int memory = 0;
            int max = 0;
            DllManager manager = new DllManager(dlls);
            for (int i = 0; i < s; i++) {
                int index = scan.nextInt();
                if (index < 0) {
                    manager.remove(progs.get(Math.abs(index) - 1));
                    memory -= progs.get(Math.abs(index) - 1).mem;
                } else {
                    manager.add(progs.get(index - 1));
                    memory += progs.get(index - 1).mem;
                }
                if (memory + manager.memory() > max) {
                    max = memory + manager.memory();
                }
            }
            System.out.println(max);
        }

    }

    private static class Prog {
        int mem;
        char[] dlls;

        public Prog(int memory, char[] dlls) {
            mem = memory;
            this.dlls = dlls;
        }
    }

    private static class DllManager extends HashMap<Character, Integer> {
        HashMap<Character, Integer> dlls;

        public DllManager(HashMap<Character, Integer> dlls) {
            this.dlls = dlls;
        }

        public int memory() {
            int sum = 0;
            for (Character c : keySet()) {
                sum += dlls.get(c);
            }
            return sum;
        }

        public void add(Prog prog) {
            for (char c : prog.dlls) {
                add(c);
            }
        }

        private void add(Character c) {
            put(c, (containsKey(c) ? get(c) : 0) + 1);
        }

        public void remove(Prog prog) {
            for (char c : prog.dlls) {
                decrement(c);
            }
        }

        private void decrement(Character c) {
            int num = get(c);
            if (num == 1) {
                remove(c);
            } else {
                put(c, num - 1);
            }
        }
    }

}