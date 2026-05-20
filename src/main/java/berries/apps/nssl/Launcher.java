package berries.apps.nssl;

import berries.apps.nssl.command.Commands;
import berries.apps.nssl.console.input.Inputs;
import berries.apps.nssl.console.output.Printers;

import java.util.Arrays;

public class Launcher {
    public static final AppInfo INFO = AppInfo.get();

    private static Launcher instance;

    public ServerProcess process = null;
    private final String[] execArgs;

    private Launcher(String[] args) {
        this.execArgs = args;
    }

    public static void init(String[] args) {
        if (instance == null) {
            instance = new Launcher(args);
        }

        instance.init();
    }

    public static Launcher getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    public void init() {
        Config.read();
        Config.getInstance().save();
        Commands.init();
        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));
        Printers.CYCLE_PAINTER.stop();
        Inputs.COMMAND_INPUT.startIn(this::onInput);
    }

    private void exit() {
        IO.println("Launcher is shutting down");
        Inputs.COMMAND_INPUT.stop();
        Printers.CYCLE_PAINTER.stop();
    }

    public String[] getArgs() {
        return execArgs;
    }

    public void onInput(String in) {
        boolean b = !(in.startsWith(Commands.COMMAND_PREFIX) && !in.equals(Commands.COMMAND_PREFIX));
        if (process != null && b) {
            process.input(in);
            return;
        }

        if (b) {
            Printers.LOGGER.error("{} is not a launcher command, and the process is not started", in);
            return;
        }

        String[] cs = in.replaceFirst(Commands.COMMAND_PREFIX, "").split(" ");
        if (!Commands.execute(cs[0], cs.length > 1 ? (Arrays.copyOfRange(cs, 1, cs.length - 1)) : new String[0])) {
            Printers.LOGGER.error("Launcher command: {} is not found", cs[0]);
        }
    }
}
