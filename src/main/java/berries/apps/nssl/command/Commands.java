package berries.apps.nssl.command;

import berries.apps.nssl.command.app.ExitCommand;
import berries.apps.nssl.command.app.HelpCommand;
import berries.apps.nssl.command.app.VerCommand;
import berries.apps.nssl.console.output.Printers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Commands {
    private static final Map<String, Consumer<List<String>>> COMMANDS = new HashMap<>();
    public static final String COMMAND_PREFIX = "#";

    public static void init() {
        register("exit", ExitCommand::run);
        register("help", HelpCommand::run);
        register("ver", VerCommand::run);
    }

    public static boolean execute(String name, String[] args) {
        if (!COMMANDS.containsKey(name)) {
            return false;
        }

        COMMANDS.getOrDefault(name, (_) ->
                Printers.LOGGER.error("Launcher command: {} is not found", name)).accept(List.of(args));
        return true;
    }

    public static void register(String name, Consumer<List<String>> exec) {
        if (COMMANDS.containsKey(name)) {
            throw new IllegalArgumentException();
        }

        COMMANDS.put(name, exec);
    }

    public static void forEachCommands(BiConsumer<String, Consumer<List<String>>> e) {
        COMMANDS.forEach(e);
    }
}
