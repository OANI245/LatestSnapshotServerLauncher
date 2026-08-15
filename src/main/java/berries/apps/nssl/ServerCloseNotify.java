package berries.apps.nssl;

public interface ServerCloseNotify {
    static void sendUpdate() {
        var sp = Launcher.getInstance().process;
        sp.input("/title @a actionbar \"§b§l检测到有版本更新，服务器将关闭§r§r\"");
        send("更新版本");
    }

    static void send(String message) {
        try {
            Thread.sleep(1500);
            var sp = Launcher.getInstance().process;
            for (int i = 0; i < 5; i++) {
                sp.input("/title @a actionbar \"§l服务器即将关闭：" + (5 - i) + "§r\"");
                Thread.sleep(1000);
            }
            sp.input("/title @a subtitle \"§l"+ message +"§r\"");
            sp.input("/title @a title \"§a§l服务器即将关闭§r§r\"");
            Thread.sleep(4600);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
