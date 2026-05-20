package berries.apps.nssl;

import berries.apps.nssl.console.output.Printers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static berries.apps.nssl.Paths.*;

public class ServerProcess {
    private volatile Process process = null;
    private Consumer<Integer> onProcessExit;

    public ServerProcess() {
    }

    public void start() throws IOException {
        File coreFile = new File(SERVER_FILE_PATH);
        if (!coreFile.exists()) {
            throw new IllegalStateException("File not found");
        }

        String javaPath = ProcessHandle.current()
                .info()
                .command()
                .orElse("java");

        List<String> command = new ArrayList<>();
        command.add(javaPath);
        command.addAll(List.of(Config.getInstance().jvmArgs));
        command.addAll(List.of("-jar", coreFile.getAbsolutePath()));
        command.addAll(List.of(Config.getInstance().coreArgs));

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(coreFile.getParentFile());

        Printers.LOGGER.info("Execute: {}", command);

        this.process = pb.start();
        Thread t = new Thread(() -> {
            try {
                process.waitFor();
                int ev = process.exitValue();
                onProcessExit.accept(ev);
                process = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.setName("Server-Core-Daemon");
    }

    public void kill() {
        process.destroy();
    }

    public void onProcessExit(Consumer<Integer> onProcessExit) {
        this.onProcessExit = onProcessExit;
    }

    public boolean running() {
        return process != null;
    }

    public void input(String input) {
    }
}
