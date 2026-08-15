package berries.apps.nssl.command.app;

import berries.apps.nssl.Launcher;
import berries.apps.nssl.console.output.Printers;

import java.util.List;

public interface VerCommand {
    static void run(List<String> args) {
        Printers.LOGGER.info(Launcher.INFO.getName());
        Printers.LOGGER.info(Launcher.INFO.getVersionName());
        StringBuilder authorTextBuilder = new StringBuilder();
        authorTextBuilder.append("Created by: ");
        Launcher.INFO.getAuthors().forEach((v) -> {
            authorTextBuilder.append(v.get("primary"))
                    .append(" (").append(v.get("other")).append(")");
        });
        Printers.LOGGER.info(authorTextBuilder.toString());
    }
}
