package com.youku.bgmovie.radar.db.phoenix.connection;

import org.apache.phoenix.util.PhoenixRuntime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class PhoenixConnectionManager {

	private static String quorum = "10.140.60.198,10.140.60.199,10.140.60.200:2181";
    private static String PHOENIX_CONNECTIONLESS_JDBC_URL = PhoenixRuntime.JDBC_PROTOCOL + 
    		PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR;

    public static final Properties TEST_PROPERTIES = new Properties();

    public static Connection getConnection(String quorum) throws Exception {
        String url = getUrl(quorum);
        Properties props = new Properties(TEST_PROPERTIES);
        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        return DriverManager.getConnection(url, props);
    }
    /**
     * set quorum by conf.
     * @return
     * @throws Exception
     */
    @Deprecated
    public static Connection getConnection() throws Exception {
    	return getConnection(quorum);
    }
    @Deprecated
    protected static String getUrl() {
        return PHOENIX_CONNECTIONLESS_JDBC_URL + quorum;
    }
    protected static String getUrl(String quorum) {
    	PhoenixConnectionManager.quorum = quorum;
        return getUrl();
    }
}
