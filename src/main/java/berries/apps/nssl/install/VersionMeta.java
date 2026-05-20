package berries.apps.nssl.install;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VersionMeta {
    @JsonProperty("downloads")
    public Downloads downloads;

    public static class Downloads {
        @JsonProperty("server")
        public Download server;

        public static class Download {
            @JsonProperty("sha1")
            public String sha1code;

            @JsonProperty("size")
            public long size;

            @JsonProperty("url")
            public String url;
        }
    }
}
