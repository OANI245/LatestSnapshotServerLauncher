package berries.apps.nssl.command.app;

import berries.apps.nssl.Launcher;
import berries.apps.nssl.console.output.Printers;

import java.util.List;

public interface ExitCommand {
    static void run(List<String> args) {
        if (Launcher.getInstance().process != null && Launcher.getInstance().process.running()) {
            Printers.LOGGER.error("Minecraft Server is running");
            return;
        }

        System.exit(0);
    }
}
