package ari.superarilo.enumType.sql;


public class PlayerHome {

    public static final String tableName = "player_home";

    public enum Column {

        ID("id", "BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY", "记录ID"),
        HOME_ID("home_id", "VARCHAR(16) NOT NULL", "家id名称" ),
        HOME_NAME("home_name", "VARCHAR(16) NOT NULL", "家名称");
        private final String keyName;
        private final String c_setting;
        private final String c_mark;

        Column(String keyName, String setting, String mark) {
            this.keyName = keyName;
            this.c_setting = setting;
            this.c_mark = mark;
        }


        public String getC_setting() {
            return c_setting;
        }

        public String getC_mark() {
            return c_mark;
        }

        public String getKeyName() {
            return keyName;
        }

    }
}
