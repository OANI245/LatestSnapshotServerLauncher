package berries.apps.nssl.install;

import berries.apps.nssl.Launcher;
import berries.apps.nssl.ServerCloseNotify;
import berries.apps.nssl.console.input.Inputs;
import berries.apps.nssl.console.output.Printers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static berries.apps.nssl.Paths.*;

public class ServerInstaller {
    private volatile static boolean installing = false;
    private static int progress = 100;
    private static String step = "";

    public static Thread startInstall(Runnable onFinish, Consumer<Integer> onProgressChange) {
        try {
            if (installing) {
                Printers.LOGGER.error("Another Installer is running");
                return null;
            }

            if (Launcher.getInstance().process.running()) {
                Printers.LOGGER.error("Server Process is running, Install is canceled");
                return null;
            }

            if (!Versioning.checkInstalled() || Versioning.shouldUpdate()) {
                return forceInstall(onFinish, onProgressChange);
            }
        } catch (Exception e) {
            Printers.LOGGER.error("Download failed: ", e);
        }
        return null;
    }

    public static void startUpdate(boolean force) {
        if (Launcher.getInstance().process != null && Launcher.getInstance().process.running()) {
            var f = Launcher.getInstance().process.getOnProcessExit();
            Launcher.getInstance().process.onProcessExit((_) -> {
                Runnable r = () -> {
                    Printers.PROGRESS_PRINTER.endPrint();
                    Printers.LOGGER.info("Finally...Restart! (/≧▽≦)/");
                    try {
                        Launcher.getInstance().process.start();
                        Launcher.getInstance().process.onProcessExit(f);
                    } catch (IOException e) {
                        Printers.LOGGER.error("Unable to start server: ", e);
                    }
                };
                if (force) {
                    forceInstall(r, Printers.PROGRESS_PRINTER::print);
                } else {
                    ServerInstaller.startInstall(r, Printers.PROGRESS_PRINTER::print);
                }
            });
            ServerCloseNotify.sendUpdate();
            Launcher.getInstance().process.stop(30000);
        } else {
            Runnable r = () -> {
                Printers.PROGRESS_PRINTER.endPrint();
                Printers.LOGGER.info("Update complete, Restarting server...");
                try {
                    Launcher.getInstance().process.start();
                    Launcher.getInstance().process.onProcessExit(System::exit);
                } catch (IOException e) {
                    Printers.LOGGER.error("Unable to start server: ", e);
                }
            };
            if (force) {
                forceInstall(r, Printers.PROGRESS_PRINTER::print);
            } else {
                ServerInstaller.startInstall(r, Printers.PROGRESS_PRINTER::print);
            }
        }
    }

    public static Thread forceInstall(Runnable onFinish, Consumer<Integer> onProgressChange) {
        if (installing) {
            return null;
        }
        progress = 0;
        onProgressChange.accept(progress);
        installing = true;
        step = "Starting install";
        Inputs.COMMAND_INPUT.pause();
        Thread t = new Thread(() -> {
            try {
                ServerInstaller.install(onFinish, onProgressChange);
            } catch (Exception e) {
                Printers.LOGGER.error("Download failed: ", e);
            }
        });
        t.start();
        return t;
    }

    private static void install(Runnable onFinish, Consumer<Integer> onProgressChange) throws Exception {
        try {
            step = "Getting metadata";
            var latest = Versioning.getLatestMinecraftVersion(false);
            if (latest == null) {
                throw new InstallFailedException("Can't get metadata from Mojang.");
            }

            step = "Downloading Minecraft";
            File serverCoreFile = new File(SERVER_FILE_PATH);
            //Download Core
            FileDownloader.startDownload(
                    latest.meta().downloads.server.url,
                    serverCoreFile,
                    (p) -> {
                        progress = (p * 3) / 7;
                        onProgressChange.accept(progress);
                    }
            );

            step = "Downloading Fabric";
            File installerFile = new File(FABRIC_INSTALLER_FILE_PATH);
            //Download Fabric
            FileDownloader.startDownload(
                    "https://maven.fabricmc.net/net/fabricmc/fabric-installer/1.1.1/fabric-installer-1.1.1.jar",
                    installerFile,
                    (p) -> {
                        progress = ((100 + p) * 3) / 7;
                        onProgressChange.accept(progress);
                    }
            );

            step = "Verifying File";
            String msha1 = latest.meta().downloads.server.sha1code.toLowerCase(Locale.ROOT);
            String fsha1;
            try (FileInputStream i = new FileInputStream(SERVER_FILE_PATH)) {
                byte[] buf = new byte[1024];
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                int br;

                while ((br = i.read(buf)) != -1) {
                    md.update(buf, 0, br);
                }

                byte[] dg = md.digest();
                StringBuilder sb = new StringBuilder();

                for (byte b : dg) {
                    sb.append(String.format("%02x", b));
                }

                fsha1 = sb.toString();
            }

            progress = 92;
            onProgressChange.accept(progress);

            if (!msha1.equals(fsha1)) {
                throw new InstallFailedException("Metadata SHA-1 code is not same for the downloaded file");
            }

            step = "Installing Fabric";
            String javaPath = ProcessHandle.current()
                    .info()
                    .command()
                    .orElse("java");

            List<String> command = List.of(new String[]{
                    javaPath, "-Xmx2G", "-jar", installerFile.getAbsolutePath(), "server", "-mcversion",
                    latest.id()
            });

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(installerFile.getParentFile());
            var p = pb.start();
            p.waitFor();
            if (p.exitValue() != 0) {
                throw new InstallFailedException("Failed to install fabric");
            }

            progress = 99;
            onProgressChange.accept(progress);

            if (serverCoreFile.exists() && installerFile.exists() && new File(FABRIC_FILE_PATH).exists()) {
                if (!installerFile.delete()) {
                    Printers.LOGGER.warn("Cannot delete fabric installer");
                }
                progress = 100;
                onProgressChange.accept(progress);
                onFinish.run();
                Printers.LOGGER.info("Install complete!");
            }
        } finally {
            step = "";
            Inputs.COMMAND_INPUT.cont();
            installing = false;
            progress = 100;
        }
    }

    public static boolean isInstalling() {
        return installing;
    }

    public static int getProgress() {
        return progress;
    }

    public static String getStep() {
        return step;
    }
}
