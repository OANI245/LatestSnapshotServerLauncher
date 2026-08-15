package berries.apps.nssl.command.app;

import berries.apps.nssl.Launcher;
import berries.apps.nssl.ServerCloseNotify;
import berries.apps.nssl.console.output.Printers;

import java.util.List;

public interface ExitCommand {
    static void run(List<String> args) {
        if (!args.isEmpty() && args.getFirst().trim().equals("force")) {
            Launcher.getInstance().process.kill();
            System.exit(0);
            return;
        }

        if (Launcher.getInstance().process != null && Launcher.getInstance().process.running()) {
            ServerCloseNotify.send(!args.isEmpty() && !args.getFirst().trim().equals("force") ? args.getFirst() : "普通维护");
            Launcher l = Launcher.getInstance();
            l.process.onProcessExit(System::exit);
            l.process.stop(30000);
            return;
        }

        System.exit(0);
    }
}
