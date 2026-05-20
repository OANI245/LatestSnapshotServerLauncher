package berries.apps.nssl;

import java.util.Locale;
import java.util.Scanner;

public interface Disclaimers {
    static void show() {
        IO.println("Ciallo～(∠・ω )⌒☆");
        IO.println("The Launcher could possibly download files and send requests when running.");
        IO.println("If you worry about it, You could backup the server files.");
        IO.println("Press [Y] to continue...");
        Scanner sn = new Scanner(System.in);
        if (sn.hasNext()) {
            String t = sn.next();
            if (t.toUpperCase(Locale.ROOT).equals("Y")) {
                Config.getInstance().accept = true;
                Config.getInstance().save();
            } else {
                System.exit(0);
            }
        }
    }
}
