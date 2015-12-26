import java.util.*;

public class SetUpCompetition {

    public static final String[] LABELS = {"A", "B", "C", "D", "E", "F"};
    public static final String[] USERNAMES = {"mitchp", "brandonm", "brian",
            "will", "dave", "willa"};
    public static final String[] PROBLEMS = {"33", "23", "55", "19", "12", "44"};

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("USE acm;");
        in.next();
        int cid = in.nextInt();
        for (String s : USERNAMES) {
            System.out.printf("INSERT INTO comp_users (cid, username, team) "
                + "VALUES (\'%d\', \'%s\', \'%s\');%n", cid, s, s);
        }

        for (int i = 0; i < PROBLEMS.length; i++) {
            System.out.printf("INSERT INTO comp_problems (label, cid, pid) "
                    + "VALUES (\'%s\', \'%d\', \'%s\');%n",
                    LABELS[i], cid, PROBLEMS[i]);
        }
    }
}
