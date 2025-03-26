public class FlightDistance {

    private static final double NAUTICAL_MILE_CONVERSION = 0.8684;
    private static final double KILOMETER_CONVERSION = 1.609344;
    private static final double MILES_CONVERSION = 1.1515;
    private static final double DEGREE_TO_RADIAN_FACTOR = Math.PI / 180.0;
    private static final double RADIAN_TO_DEGREE_FACTOR = 180.0 / Math.PI;
    private static final double EARTH_DISTANCE_FACTOR = 60.0;

    public static String[] calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double distance = Math.sin(degreeToRadian(lat1)) * Math.sin(degreeToRadian(lat2)) +
                Math.cos(degreeToRadian(lat1)) * Math.cos(degreeToRadian(lat2)) *
                        Math.cos(degreeToRadian(theta));

        distance = Math.acos(distance);
        distance = radianToDegree(distance);
        distance = distance * EARTH_DISTANCE_FACTOR * MILES_CONVERSION;

        String[] distanceString = new String[3];
        distanceString[0] = String.format("%.2f", distance * NAUTICAL_MILE_CONVERSION);
        distanceString[1] = String.format("%.2f", distance * KILOMETER_CONVERSION);
        distanceString[2] = String.format("%.2f", distance);

        return distanceString;
    }

    private static double degreeToRadian(double deg) {
        return deg * DEGREE_TO_RADIAN_FACTOR;
    }

    private static double radianToDegree(double rad) {
        return rad * RADIAN_TO_DEGREE_FACTOR;
    }
}
