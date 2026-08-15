package berries.apps.nssl.command.server;

import berries.apps.nssl.Launcher;
import berries.apps.nssl.ServerCloseNotify;
import berries.apps.nssl.console.output.Printers;

import java.util.List;

public interface StopToCommandOnlyCommand {
    static void run(List<String> args) {
        ServerCloseNotify.send(!args.isEmpty() ? args.getFirst() : "普通维护");
        Launcher l = Launcher.getInstance();
        l.process.onProcessExit((_) -> {
            Printers.LOGGER.info("You can enter command #start to restart the server.");
            Printers.LOGGER.info("Command Only");
            Printers.LOGGER.info(Launcher.INFO.getName());
            Printers.LOGGER.info(Launcher.INFO.getVersionName());
        });
        l.process.stop(30000);
    }
}
