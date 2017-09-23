/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */

package omada17.newyorkapp;

public class Constants {
    
    public static final int CLIENT_PORT = 15554;

    public static final String MAP_ADDRESS[] = {"192.168.1.1","172.16.2.53","172.16.2.64", "172.16.2.63", "172.16.2.53"};
    public static final int MAP_PORT[] = {17001, 17002, 17003, 17004, 17005};

    public static final String REDUCER_ADDRESS = "192.168.1.1";
    public static final int REDUCER_PORT = 18000;

    public static final int MAP_NUM = 1;
    public static final int topk = 10;

    public static final double MIN_LAT = 40.55085246740427;
    public static final double MAX_LAT = 40.988331719265304;
    public static final double MIN_LONG = -74.27476644515991;
    public static final double MAX_LONG = -73.68382519820906;

    public static final long DOUBLE_TAP_TIME_DELTA = 5000;//milliseconds
}
