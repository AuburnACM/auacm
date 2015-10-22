import java.util.*;

public class SetUpCompetition {

    public static final String[] USERNAMES = {"mitchp", "brandonm", "brian", "will", "dave", "willa"};
    public static final String[] PROBLEMS = {"MissingPages", "Islands", "SortMe", "Gnome", "Dull", "Quicksum"};

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        
        System.out.println("USE acm;");
        in.next();
        int cid = in.nextInt();
        for (String s : USERNAMES) {
            System.out.printf("INSERT INTO comp_users (cid, username, team) VALUES (\'%d\', \'%s\', \'%s\');%n",
                cid, s, s);
        }

        for (String s : PROBLEMS) {
            System.out.printf("INSERT INTO comp_problems (cid, pid) VALUES (\'%d\', \'%s\');%n", cid, s);
        }
    }
}
