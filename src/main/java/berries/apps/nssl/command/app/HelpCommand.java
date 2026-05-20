package berries.apps.nssl.command.app;

import berries.apps.nssl.command.Commands;
import berries.apps.nssl.console.output.Printers;

import java.util.List;

public interface HelpCommand {
    static void run(List<String> args) {
        Commands.forEachCommands((k, v) -> {
            Printers.LOGGER.info("=> {}{}", Commands.COMMAND_PREFIX, k);
        });
    }
}
