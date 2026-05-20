package berries.apps.nssl.console.output;

import java.util.Timer;
import java.util.TimerTask;

public class AsyncLoopPrinter {
    private final Timer timer;
    private boolean started = false;
    private boolean newline = false;

    AsyncLoopPrinter() {
        this.timer = new Timer("AsyncCyclePrinterTimer");
    }

    public void setAndStart(String text, long duration) {
        newline = start(text, duration, false);
    }

    public void setLnAndStart(String text, long duration) {
        newline = start(text, duration, true);
    }

    public boolean start(String text, long duration, boolean newline) {
        if (started) {
            return newline;
        }
        this.timer.schedule(new PrintStringTask(text, newline), 0, duration);
        started = true;

        return newline;
    }

    public void stop() {
        if (!started) {
            return;
        }
        this.timer.cancel();
        if (!newline) {
            IO.println();
        }
        started = false;
        newline = false;
    }

    public static class PrintStringTask extends TimerTask {
        public final String text;
        public final boolean newline;

        PrintStringTask(String text, boolean newline) {
            this.text = text;
            this.newline = newline;
        }

        @Override
        public void run() {
            if (newline) {
                IO.println(text);
            } else {
                IO.print(text);
            }
        }
    }
}
