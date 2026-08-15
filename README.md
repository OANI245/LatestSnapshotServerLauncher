# LSSL 最新快照版服务器启动器
## Alpha 2 更新内容
1. 加入了快照版本存在判断，新的Minecraft正式版发布后，服务器启动时不再会崩溃。
2. 更新循环起始时间现在不再直接保存时间戳，改为按时、分、秒保存。

## 使用方式
1. 将 lssl-x.x.jar 移动到快照版Minecraft服务器的同级目录中。
2. 将启动服务器的命令由`java [-XmxXG -XmsXG] -jar server.jar`改为`java -jar lssl-x.x.jar`。
3. 允许协议后，等待安装完成，服务器自动启动。

## 它能做什么？
1. 第一次启动自动下载最新核心文件，无需到其它网站下载。
2. 自动更新快照 Minecraft 版本，每天上午 5:00 （默认设置）检测一次更新，并自动关闭服务器替换核心文件。

## 有什么命令？
LSSL 的命令开头的符号是`#`，以区分于 Minecraft 的`/`，且 Minecraft 服务端开启时仍可使用，LSSL可用的命令如下：
1. `#help` 列出所有命令。
2. `#start` 启动服务器。
3. `#stoptocmdonly [原因]` 关闭服务器但不关闭启动器，可重新启动服务器，原因部分填写文字后会使用此段文字提醒玩家。
4. `#exit [force|原因]` （正常关闭服务器并）退出启动器，其中若带有`force`参数则会直接中止 Minecraft 服务端进程，带有其它文字后会使用此段文字提醒玩家。
5. `#ver` 查看关于信息。
6. `#checkupdate` 手动检查更新，若启动器检查更新太早，可使用此命令。
7. `#debugupdate` 强制启动更新，一般用于测试下载质量和过程的稳定性。
8. `#forceinstall` 强制重新安装，一般只有核心文件损坏后使用。
9. `#config [list|set|get]` 修改启动器配置。

## 启动器配置
LSSL 的配置可使用命令`#config`修改和获取，可使用`#config list`列出所有配置，使用`#config set`可设置配置，配置文件存储在运行目录的`nssl.json`文件中。
1. `check_updates` 启用检查更新。<br/>修改方式：`#config set check_updates (true|false)`
2. `auto_start` 在启动器启动时自动启动 Minecraft 服务端。<br/>修改方式：`#config set auto_start (true|false)`
3. `check_update_time` 启动器开始检查更新的时间，在`#config set check_update_time`中，会要求输入时、分、秒数值，默认为上午 5:00。<br/>修改方式：`#config set check_update_time`
4. `check_update_interval` 启动器检查更新的间隔，在`#config set check_update_interval`中，会要求输入天数、时、分、秒数值，默认为一天。<br/>修改方式：`#config set check_update_interval`
5. `jvm_args` Java虚拟机参数，例如`-Xmx4G -Xms4G`的分配内存参数存储于此。<br/>修改方式：`#config set jvm_args (JVM参数 如 -Xmx4G)`
6. `core_args` 服务器核心参数，例如`nogui`的参数存储于此。<br/>修改方式：`#config set core_args (核心参数 如 nogui)`
7. `input_encoding` 控制台输入字符编码，默认为`UTF-8`，若输入的中文等字符乱码，应修改此设置后重试。<br/>修改方式：`#config set input_encoding (UTF-8|GBK)`