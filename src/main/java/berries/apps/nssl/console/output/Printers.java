package berries.apps.nssl.console.output;

import berries.apps.nssl.Launcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Printers {
    Logger LOGGER = LoggerFactory.getLogger(Launcher.INFO.getShortName());
    AsyncLoopPrinter CYCLE_PAINTER = new AsyncLoopPrinter();
    ProgressPrinter PROGRESS_PRINTER = new ProgressPrinter();
}
