package berries.apps.nssl;

import berries.apps.nssl.console.output.PrintColors;

import java.util.Locale;
import java.util.Scanner;

public interface Disclaimers {
    static void show() {
        IO.println(PrintColors.GREEN + "Ciallo～(∠・ω )⌒☆" + PrintColors.RESET);
        IO.println("The Launcher could possibly download files and send requests when running.");
        IO.println("If you worry about it, You could backup the server files.");
        IO.println("Press " + PrintColors.YELLOW + "[Y]" + PrintColors.RESET + " to continue...");
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
