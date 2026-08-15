package berries.apps.nssl.command.install;

import berries.apps.nssl.console.output.Printers;
import berries.apps.nssl.install.ServerInstaller;
import berries.apps.nssl.install.Versioning;

import java.util.List;

public interface DebugUpdateCommand {
    static void run(List<String> args) {
        Printers.LOGGER.info("Debugging Update");
        ServerInstaller.startUpdate(true);
    }
}
