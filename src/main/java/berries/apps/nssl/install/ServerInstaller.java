package berries.apps.nssl.install;

import berries.apps.nssl.console.output.Printers;

import java.io.File;

import static berries.apps.nssl.Paths.*;

public class ServerInstaller {
    private static boolean installing = false;
    private volatile static int progress = 100;

    public static void startInstall(Runnable onFinish) {
        progress = 0;
        installing = true;
        try {
            if (!Versioning.checkInstalled()) {
                new Thread(() -> {
                    try {
                        ServerInstaller.installMode(onFinish);
                    } catch (Exception e) {
                        Printers.LOGGER.error("Download failed: ", e);
                    }
                }).start();
            } else if (Versioning.shouldUpdate()) {
                new Thread(() -> {
                    try {
                        ServerInstaller.updateMode(onFinish);
                    } catch (Exception e) {
                        Printers.LOGGER.error("Download failed: ", e);
                    }
                }).start();
            }
        } catch (Exception e) {
            Printers.LOGGER.error("Download failed: ", e);
        }
    }

    private static void installMode(Runnable onFinish) throws Exception {
        try {
            var latest = Versioning.getLatestMinecraftVersion();
            if (latest == null) {
                throw new Exception("Can't get metadata from Mojang.");
            }
            //Download Core
            FileDownloader.startDownload(
                    latest.meta().downloads.server.url,
                    new File(SERVER_FILE_PATH),
                    (p) -> progress = (p * 3) / 7
            );
        } finally {
            installing = false;
            progress = 100;
        }
    }

    private static void updateMode(Runnable onFinish) throws Exception {
        try {
            var latest = Versioning.getLatestMinecraftVersion();
            if (latest == null) {
                throw new Exception("Can't get metadata from Mojang.");
            }
        } finally {
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
}
