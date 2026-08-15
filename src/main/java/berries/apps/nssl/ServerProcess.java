package berries.apps.nssl;

import berries.apps.nssl.console.output.Printers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static berries.apps.nssl.Paths.*;

public class ServerProcess {
    private volatile Process process = null;
    private Consumer<Integer> onProcessExit = (_) -> {};

    public ServerProcess() {
    }

    public void start() throws IOException {
        onProcessExit = (_) -> {};

        File coreFile = new File(FABRIC_FILE_PATH);
        if (!coreFile.exists()) {
            throw new IllegalStateException("File not found");
        }

        String javaPath = ProcessHandle.current()
                .info()
                .command()
                .orElse("java");

        List<String> command = new ArrayList<>();
        command.add(javaPath);
        command.addAll(Config.getInstance().jvmArgs);
        command.addAll(List.of("-jar", coreFile.getAbsolutePath()));
        command.addAll(Config.getInstance().coreArgs);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(coreFile.getParentFile());

        Printers.LOGGER.info("Execute: {}", command);

        this.process = pb.start();
        Thread t = new Thread(() -> {
            try {
                /*Scanner sn = new Scanner(process.getInputStream());
                while (process.isAlive()) {
                    if (sn.hasNext()) {
                        IO.println(sn.nextLine());
                    }
                }*/
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String l;
                while ((l = reader.readLine()) != null) {
                    IO.println(l);
                }

                process.waitFor();
                IO.println();
                int ev = process.exitValue();
                onProcessExit.accept(ev);
                process = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setName("Server-Core-Daemon");

        t.start();
    }

    public void stop(long millisTimeout) {
        if (!running()) {
            return;
        }

        input("/stop");
        try {
            process.onExit().orTimeout(millisTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            kill();
        }
    }

    public void kill() {
        process.destroy();
    }

    public void onProcessExit(Consumer<Integer> onProcessExit) {
        this.onProcessExit = onProcessExit;
    }

    public boolean running() {
        return process != null && process.isAlive();
    }

    public void input(String input) {
        try {
            var os = process.getOutputStream();
            os.write((input + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            os.flush();
        } catch (Exception e) {
            Printers.LOGGER.error("Cannot send commands to process: ", e);
        }
    }

    public Consumer<Integer> getOnProcessExit() {
        return onProcessExit;
    }
}
