package berries.apps.nssl.console.input;

import berries.apps.nssl.Config;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CommandInput {
    private final ExecutorService service;
    private Future<?> ef;
    private boolean paused = false;
    private Runnable onEncodingChanged = null;

    CommandInput() {
        this.service = Executors.newSingleThreadExecutor();
    }

    public void startIn(Consumer<String> onInput) {
        if (ef != null) {
            throw new IllegalStateException();
        }

        this.ef = this.service.submit(() -> {
            AtomicReference<Scanner> sn = new AtomicReference<>();
            onEncodingChanged = () -> {
                sn.set(new Scanner(System.in, Config.getInstance().encoding));
            };
            onEncodingChanged.run();
            while (!service.isShutdown()) {
                if (paused) {
                    continue;
                }

                if (sn.get().hasNext()) {
                    onInput.accept(sn.get().nextLine().trim());
                }
            }
            onEncodingChanged = null;
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

    public void notifyEncodingChange() {
        onEncodingChanged.run();
    }
}
