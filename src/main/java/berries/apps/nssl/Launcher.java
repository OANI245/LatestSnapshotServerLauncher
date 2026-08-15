package berries.apps.nssl;

import berries.apps.nssl.command.Commands;
import berries.apps.nssl.console.input.Inputs;
import berries.apps.nssl.console.output.Printers;
import berries.apps.nssl.install.ServerInstaller;
import berries.apps.nssl.install.UpdateChecker;
import berries.apps.nssl.install.Versioning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class Launcher {
    public static final AppInfo INFO = AppInfo.get();

    private static Launcher instance;

    public volatile ServerProcess process = null;
    private final String[] execArgs;

    public static int mcPort = 25565;
    public static String mcIp = "";
    public static boolean nativeTransport = false;

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
        initMCPort();
        Config.getInstance().save();
        Commands.init();
        process = new ServerProcess();
        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));
        Printers.CYCLE_PAINTER.stop();
        Printers.LOGGER.info("Hello!");
        Printers.LOGGER.info("Checking installed...");
        if (!Versioning.checkInstalled()) {
            Printers.LOGGER.info("Server core is not installed, Launcher is installing");
            try {
                ServerInstaller.startInstall(Printers.PROGRESS_PRINTER::endPrint, Printers.PROGRESS_PRINTER::print)
                        .join();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        } else if (Config.getInstance().checkUpdates) {
            UpdateChecker.INSTANCE.start(Config.getInstance().checkUpdateTime.getCalendar().getTime(), Config.getInstance().interval);
            Printers.LOGGER.info("Checking update...");
            if (Versioning.shouldUpdate()) {
                Printers.LOGGER.info("Found new Snapshot version, Launcher will start update");
                ServerInstaller.startUpdate(false);
            }
        }
        if (Config.getInstance().autoStart && !(execArgs.length > 0 && execArgs[0].equals("cmdonly"))) {
            Printers.LOGGER.info("O K ! Let's start! o((>ω< ))o");
            try {
                Launcher.getInstance().process.start();
                Launcher.getInstance().process.onProcessExit(System::exit);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        } else {
            Printers.LOGGER.info("Auto start is disabled, You can enter command #start to start the server.");
            Printers.LOGGER.info("Command Only");
            Printers.LOGGER.info(Launcher.INFO.getName());
            Printers.LOGGER.info(Launcher.INFO.getVersionName());
        }
        Inputs.COMMAND_INPUT.startIn(this::onInput);
    }

    private void exit() {
        IO.println("B y e ! See You Again! ヾ(•ω•`)o");
        if (process != null && process.running()) {
            process.kill();
        }
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
        if (!Commands.execute(cs[0], cs.length > 1 ? (Arrays.copyOfRange(cs, 1, cs.length)) : new String[0])) {
            Printers.LOGGER.error("Launcher command: {} is not found", cs[0]);
        }
    }

    public static void initMCPort() {
        File pf = new File(Paths.SERVER_PROPERTIES_PATH);
        if (!pf.exists()) {
            return;
        }

        try (BufferedReader r = new BufferedReader(new FileReader(pf))) {
            Properties pt = new Properties();
            pt.load(r);
            mcIp = pt.getProperty("server-ip");
            mcPort = Integer.parseInt(pt.getProperty("server-port"));
            nativeTransport = Boolean.parseBoolean(pt.getProperty("use-native-transport"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
