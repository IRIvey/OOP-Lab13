import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class FlightScheduler {
    private static final int NUM_OF_FLIGHTS = 15;
    private static final List<Flight> flightList = new ArrayList<>();
    private final RandomGenerator randomGenerator = new RandomGenerator();

    public void scheduleFlights() {
        for (int i = 0; i < NUM_OF_FLIGHTS; i++) {
            String[][] chosenDestinations = randomGenerator.randomDestinations();
            String[] distanceBetweenCities = FlightDistance.calculateDistance(
                    Double.parseDouble(chosenDestinations[0][1]), Double.parseDouble(chosenDestinations[0][2]),
                    Double.parseDouble(chosenDestinations[1][1]), Double.parseDouble(chosenDestinations[1][2])
            );

            String flightSchedule = createNewFlightsAndTime();
            String flightNumber = randomGenerator.randomFlightNumbGen(2, 1).toUpperCase();
            int numOfSeats = randomGenerator.randomNumOfSeats();
            String gate = randomGenerator.randomFlightNumbGen(1, 30).toUpperCase();

            flightList.add(new Flight(flightSchedule, flightNumber, numOfSeats, chosenDestinations, distanceBetweenCities, gate));
        }
    }

    private String createNewFlightsAndTime() {
        Calendar calendar = Calendar.getInstance();
        Flight.nextFlightDay += Math.random() * 7;
        calendar.add(Calendar.DATE, Flight.nextFlightDay);
        calendar.add(Calendar.HOUR, Flight.nextFlightDay);
        calendar.set(Calendar.MINUTE, ((calendar.get(Calendar.MINUTE) * 3) - (int) (Math.random() * 45)));

        LocalDateTime date = Instant.ofEpochMilli(calendar.getTime().getTime())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        date = FlightTimeCalculator.getNearestHourQuarter(date);
        return date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, HH:mm a "));
    }


    public void displayFlightSchedule() {

        Iterator<Flight> flightIterator = flightList.iterator();
        System.out.println();
        System.out.print("+------+-------------------------------------------+-----------+------------------+-----------------------+------------------------+---------------------------+-------------+--------+------------------------+\n");
        System.out.printf("| Num  | FLIGHT SCHEDULE\t\t\t   | FLIGHT NO | Available Seats  | \tFROM ====>>       | \t====>> TO\t   | \t    ARRIVAL TIME       | FLIGHT TIME |  GATE  |   DISTANCE(MILES/KMS)  |%n");
        System.out.print("+------+-------------------------------------------+-----------+------------------+-----------------------+------------------------+---------------------------+-------------+--------+------------------------+\n");
        int i = 0;
        while (flightIterator.hasNext()) {
            i++;
            Flight f1 = flightIterator.next();
            System.out.println(f1.toString(i));
            System.out.print("+------+-------------------------------------------+-----------+------------------+-----------------------+------------------------+---------------------------+-------------+--------+------------------------+\n");
        }
    }


    public static List<Flight> getFlightList() {
        return flightList;
    }
}