package berries.apps.nssl.command.app;

import berries.apps.nssl.Config;
import berries.apps.nssl.console.input.Inputs;
import berries.apps.nssl.console.output.Printers;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public interface ConfigCommand {
    static void run(List<String> args) {
        if (args.isEmpty()) {
            return;
        }

        Config.read();
        try {
            if (args.getFirst().equals("get") && args.size() > 1) {
                String s = args.get(1);
                Config c = Config.getInstance();
                var tu = TimeUnit.NANOSECONDS;
                switch (s) {
                    case "check_updates" -> Printers.LOGGER.info("Auto Check Updates: {}", c.checkUpdates);
                    case "auto_start" -> Printers.LOGGER.info("Auto Start Server: {}", c.autoStart);
                    case "check_update_time" ->
                            Printers.LOGGER.info("Check Update Start Time: (\n{}\n)", c.checkUpdateTime);
                    case "check_update_interval" -> Printers.LOGGER.info(
                            "Check Update Interval: {}",
                            c.interval);
                    case "jvm_args" -> Printers.LOGGER.info("Java Virtual Machine Arguments: {}", c.jvmArgs);
                    case "core_args" -> Printers.LOGGER.info("Server Core Arguments: {}", c.coreArgs);
                    case "input_encoding" -> Printers.LOGGER.info("Input Encoding: {}", c.encoding);
                }
            } else if (args.getFirst().equals("set") && args.size() > 1) {
                String s = args.get(1);
                Config c = Config.getInstance();
                Scanner sn = new Scanner(System.in);
                switch (s) {
                    case "check_updates" -> c.checkUpdates = Boolean.parseBoolean(args.get(2));
                    case "auto_start" -> c.autoStart = Boolean.parseBoolean(args.get(2));
                    case "check_update_time" -> {
                        Inputs.COMMAND_INPUT.pause();
                        int hour = 0, minute = 0, second = 0;
                        IO.print("Enter Hour: ");
                        if (sn.hasNext()) {
                            hour = Integer.parseInt(sn.nextLine().trim());
                        }
                        IO.print("Enter Minute: ");
                        if (sn.hasNext()) {
                            minute = Integer.parseInt(sn.nextLine().trim());
                        }
                        IO.print("Enter Second: ");
                        if (sn.hasNext()) {
                            second = Integer.parseInt(sn.nextLine().trim());
                        }
                        c.checkUpdateTime = new Config.UpdateLoopStartTime(
                                Math.min(hour, 23),
                                Math.min(minute, 59),
                                Math.min(second, 59)
                        );
                        Printers.LOGGER.info("Complete");
                        Inputs.COMMAND_INPUT.cont();
                    }
                    case "check_update_interval" -> {
                        Inputs.COMMAND_INPUT.pause();
                        int day = 0, hour = 0, minute = 0, second = 0;
                        IO.print("Enter Days: ");
                        if (sn.hasNext()) {
                            day = Integer.parseInt(sn.nextLine().trim());
                        }
                        IO.print("Enter Hours: ");
                        if (sn.hasNext()) {
                            hour = Integer.parseInt(sn.nextLine().trim());
                        }
                        IO.print("Enter Minutes: ");
                        if (sn.hasNext()) {
                            minute = Integer.parseInt(sn.nextLine().trim());
                        }
                        IO.print("Enter Seconds: ");
                        if (sn.hasNext()) {
                            second = Integer.parseInt(sn.nextLine().trim());
                        }
                        c.interval = ((long) Math.min(day, 5) * 1000 * 60 * 60 * 24) +
                                ((long) Math.min(hour, 23) * 1000 * 60 * 60) +
                                ((long) Math.min(minute, 59) * 1000 * 60) +
                                (Math.min(second, 59) * 1000L);
                        Printers.LOGGER.info("Complete");
                        Inputs.COMMAND_INPUT.cont();
                    }
                    case "jvm_args" -> c.jvmArgs = List.of(Arrays.copyOfRange(args.toArray(new String[0]), 2, args.size()));
                    case "core_args" -> c.coreArgs = List.of(Arrays.copyOfRange(args.toArray(new String[0]), 2, args.size()));
                    case "input_encoding" -> {
                        try {
                            c.encoding = Charset.forName(args.get(2));
                            Inputs.COMMAND_INPUT.notifyEncodingChange();
                        } catch (Exception e) {
                            Printers.LOGGER.error("Input charset is not found, Please input other one", e);
                        }
                    }
                }
            } else if (args.getFirst().equals("list")) {
                Printers.LOGGER.info("Configs: check_updates, auto_start, check_update_time, check_update_interval, jvm_args, core_args, input_encoding");
            } else if (args.getFirst().equals("reset")) {
                Config.reset();
                Printers.LOGGER.info("Complete");
                return;
            }
        } catch (Exception e) {
            Printers.LOGGER.info("Failed Execute: ", e);
        }
        Config.getInstance().save();
    }
}
