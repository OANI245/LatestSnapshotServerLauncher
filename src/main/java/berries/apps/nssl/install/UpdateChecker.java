package berries.apps.nssl.install;

import berries.apps.nssl.Config;
import berries.apps.nssl.console.output.Printers;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateChecker {
    public static final UpdateChecker INSTANCE = new UpdateChecker();

    private final Timer timer;

    public UpdateChecker() {
        this.timer = new Timer("Server Update Checker");
    }

    public void start(Date startDate, long loopMillis) {
        timer.schedule(new UpdateCheckTask(), startDate, loopMillis);
    }

    public void stop() {
        timer.cancel();
    }

    public static class UpdateCheckTask extends TimerTask {
        @Override
        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!ServerInstaller.isInstalling()) {
                Printers.LOGGER.info("Checking update...");
                if (Versioning.shouldUpdate()) {
                    Printers.LOGGER.info("Found new Snapshot version, Launcher will start update");
                    ServerInstaller.startUpdate(false);
                }
            }
        }
    }
}
