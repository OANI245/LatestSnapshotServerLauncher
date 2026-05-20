package berries.apps.nssl;

import berries.apps.nssl.console.output.Printers;

public class Bootstrap {
    static void main(String[] args) {
        Config.read();
        if (!Config.getInstance().accept) {
            Disclaimers.show();
        }

        IO.print("Starting");
        Printers.CYCLE_PAINTER.setAndStart(".", 200);

        Launcher.init(args);
    }
}
