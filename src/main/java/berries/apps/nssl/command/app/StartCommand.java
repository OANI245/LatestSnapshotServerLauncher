package berries.apps.nssl.command.app;

import berries.apps.nssl.Launcher;
import berries.apps.nssl.console.output.Printers;

import java.io.IOException;
import java.util.List;

public interface StartCommand {
    static void run(List<String> args) {
        Launcher l = Launcher.getInstance();
        if (Launcher.getInstance().process != null && Launcher.getInstance().process.running()) {
            Printers.LOGGER.error("Minecraft Server is running");
            return;
        } else {
            try {
                if (l.process != null) {
                    l.process.start();
                    l.process.onProcessExit(StartCommand::getOnExit);
                }
            } catch (IOException e) {
                Printers.LOGGER.error("Unable to start server: ", e);
            }
        }
    }

    private static void getOnExit(Integer j) {
        if (j == 0)
            Printers.LOGGER.info("Process is exit, enter command #exit to exit the launcher, and enter command #start to restart~");
        else Printers.LOGGER.error("Process exit not correctly, Please look the log in logs folder! Exit Code: {}", j);
    }
}
