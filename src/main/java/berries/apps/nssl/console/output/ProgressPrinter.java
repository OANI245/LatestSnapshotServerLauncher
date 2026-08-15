package berries.apps.nssl.console.output;

import java.util.function.Consumer;

public class ProgressPrinter implements Consumer<Integer> {
    private boolean printed = false;

    ProgressPrinter() {}

    @Override
    public void accept(Integer i) {
        print(i);
    }

    public void print(int progress) {
        printed = true;
        IO.print("\r" + PrintColors.RESET + "Progress: " + progress + "% " + PrintColors.RED);
        for (int i = 0; i < progress / 2; ++i) {
            IO.print("/");
        }
        IO.print(PrintColors.RESET);
        for (int i = progress / 2; i < 50; ++i) {
            IO.print("|");
        }
    }

    public void endPrint() {
        if (!printed) {
            return;
        }
        IO.println();
    }
}
