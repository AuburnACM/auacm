import java.util.*;

public class CreateCompetition {

    public static final int COMPETITION_NAME_INDEX = 0;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        int startTime = (int) (System.currentTimeMillis() / 1000);
        System.out.println("USE acm;");
        System.out.printf("INSERT INTO comp_names (name, start, stop, closed) VALUES (\'%s\', \'%d\', \'%d\', 0);%n",
                in.nextLine(), startTime, startTime + 5 * 60 * 60);

        System.out.printf("SELECT cid FROM comp_names WHERE start=%d;%n", startTime);
    }
}
