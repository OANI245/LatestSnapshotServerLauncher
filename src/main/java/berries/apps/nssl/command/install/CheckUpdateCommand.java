package berries.apps.nssl.command.install;

import berries.apps.nssl.Launcher;
import berries.apps.nssl.console.output.Printers;
import berries.apps.nssl.install.ServerInstaller;
import berries.apps.nssl.install.Versioning;

import java.io.IOException;
import java.util.List;

public interface CheckUpdateCommand {
    static void run(List<String> args) {
        if (Versioning.shouldUpdate()) {
            Printers.LOGGER.info("Found new Snapshot version, Launcher will start update");
            ServerInstaller.startUpdate(false);
        } else {
            Printers.LOGGER.info("The version is the latest version, no need to update");
        }
    }
}
