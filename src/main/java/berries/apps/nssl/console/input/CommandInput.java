package berries.apps.nssl.console.input;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

public class CommandInput {
    private final ExecutorService service;
    private Future<?> ef;
    private boolean paused = false;

    CommandInput() {
        this.service = Executors.newSingleThreadExecutor();
    }

    public void startIn(Consumer<String> onInput) {
        if (ef != null) {
            throw new IllegalStateException();
        }

        this.ef = this.service.submit(() -> {
            Scanner sn = new Scanner(System.in);
            while (!service.isShutdown()) {
                if (paused) {
                    continue;
                }

                if (sn.hasNext()) {
                    onInput.accept(sn.nextLine().trim());
                }
            }
        });
    }

    public void pause() {
        this.paused = true;
    }

    public void cont() {
        this.paused = false;
    }

    public void stop() {
        this.ef = null;
        this.service.shutdown();
    }
}
