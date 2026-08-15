package berries.apps.nssl.command.install;

import berries.apps.nssl.Launcher;
import berries.apps.nssl.console.output.Printers;
import berries.apps.nssl.install.ServerInstaller;

import java.util.List;

public interface ForceInstallCommand {
    static void run(List<String> args) {
        if (Launcher.getInstance().process != null && Launcher.getInstance().process.running()) {
            Printers.LOGGER.error("Minecraft Server is running");
            return;
        }
        IO.println();
        ServerInstaller.forceInstall(Printers.PROGRESS_PRINTER::endPrint, Printers.PROGRESS_PRINTER::print);
    }
}
