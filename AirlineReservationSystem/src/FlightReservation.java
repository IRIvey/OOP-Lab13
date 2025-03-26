import java.util.List;
import java.util.Scanner;

public class FlightReservation implements DisplayClass {

    Flight flight = new Flight();
    int flightIndexInFlightList;


    public void bookFlight(String flightNo, int numOfTickets, String userID) {
        FlightManager selectedFlight = new FlightManager(flightNo);
        if (selectedFlight == null) {
            System.out.println("Invalid Flight Number...! No flight with the ID \"" + flightNo + "\" was found...");
            return;
        }

        Customer customer = findCustomerByID(userID);
        if (customer == null) {
            System.out.println("Invalid User ID...! No customer with ID \"" + userID + "\" was found...");
            return;
        }

        processBooking(selectedFlight, customer, numOfTickets);
        System.out.printf("\n %50s You've booked %d tickets for Flight \"%5s\"...", "", numOfTickets, flightNo.toUpperCase());
    }

    private Flight findFlightByNumber(String flightNo) {
        return flight.getFlightList().stream()
                .filter(f -> flightNo.equalsIgnoreCase(f.getFlightNumber()))
                .findFirst()
                .orElse(null);
    }

    private Customer findCustomerByID(String userID) {
        return Customer.customerCollection.stream()
                .filter(c -> userID.equals(c.getUserID()))
                .findFirst()
                .orElse(null);
    }

    private void processBooking(FlightManager flight, Customer customer, int numOfTickets) {
        flight.getFlight().setNoOfSeatsInTheFlight(flight.getFlight().getNoOfSeats() - numOfTickets);

        if (!flight.isCustomerAlreadyAdded(flight.getListOfRegisteredCustomersInAFlight(), customer)) {
            flight.addNewCustomerToFlight(customer);
        }

        if (isFlightAlreadyAddedToCustomerList(customer.getFlightsRegisteredByUser(), flight.getFlight())) {
            addNumberOfTicketsToAlreadyBookedFlight(customer, numOfTickets);
            int flightIndex = flightIndex(flight.getFlight().getFlightList(), flight.getFlight());
            if (flightIndex != -1) {
                customer.addExistingFlightToCustomerList(flightIndex, numOfTickets);
            }
        } else {
            customer.addNewFlightToCustomerList(flight.getFlight());
            addNumberOfTicketsForNewFlight(customer, numOfTickets);
        }
    }


    public void cancelFlight(String userID) {
        Customer customer = findCustomerByID(userID);
        if (customer == null) {
            System.out.println("ERROR!!! No customer found with ID \"" + userID + "\".");
            return;
        }

        if (customer.getFlightsRegisteredByUser().isEmpty()) {
            System.out.println("No flights have been registered by you.");
            return;
        }

        displayRegisteredFlights(customer);
        String flightNum = getFlightNumberFromUser();
        int numOfTickets = getNumberOfTicketsFromUser();

        processFlightCancellation(customer, flightNum, numOfTickets);
    }

    private void displayRegisteredFlights(Customer customer) {
        System.out.printf("%50s %s Here is the list of all the Flights registered by you %s%n",
                " ", "++++++++++++++", "++++++++++++++");
        displayFlightsRegisteredByOneUser(customer.getUserID());
    }

    private String getFlightNumberFromUser() {
        Scanner read = new Scanner(System.in);
        System.out.print("Enter the Flight Number of the flight you want to cancel: ");
        return read.nextLine();
    }

    private int getNumberOfTicketsFromUser() {
        Scanner read = new Scanner(System.in);
        System.out.print("Enter the number of tickets to cancel: ");
        return read.nextInt();
    }

    private void processFlightCancellation(Customer customer, String flightNum, int numOfTickets) {
        List<Flight> registeredFlights = customer.getFlightsRegisteredByUser();
        List<Integer> ticketsBooked = customer.getNumOfTicketsBookedByUser();

        for (int i = 0; i < registeredFlights.size(); i++) {
            Flight flight = registeredFlights.get(i);

            if (!flightNum.equalsIgnoreCase(flight.getFlightNumber())) {
                continue;
            }

            int ticketsForFlight = ticketsBooked.get(i);
            if (numOfTickets > ticketsForFlight) {
                System.out.println("ERROR!!! Number of tickets cannot be greater than " + ticketsForFlight + " for this flight.");
                return;
            }

            updateFlightAndCustomerRecords(customer, flight, numOfTickets, i);
            System.out.printf("\n %50s Successfully canceled %d tickets for Flight \"%5s\"...\n", "", numOfTickets, flightNum.toUpperCase());
            return;
        }

        System.out.println("ERROR!!! Couldn't find Flight with ID \"" + flightNum.toUpperCase() + "\".....");
    }

    private void updateFlightAndCustomerRecords(Customer customer, Flight flight, int numOfTickets, int index) {
        List<Integer> ticketsBooked = customer.getNumOfTicketsBookedByUser();
        int remainingTickets = ticketsBooked.get(index) - numOfTickets;

        if (remainingTickets == 0) {
            ticketsBooked.remove(index);
            customer.getFlightsRegisteredByUser().remove(index);
        } else {
            ticketsBooked.set(index, remainingTickets);
        }

        flight.setNoOfSeatsInTheFlight(flight.getNoOfSeats() + numOfTickets);
    }

    void addNumberOfTicketsToAlreadyBookedFlight(Customer customer, int numOfTickets) {
        int newNumOfTickets = customer.getNumOfTicketsBookedByUser().get(flightIndexInFlightList) + numOfTickets;
        customer.getNumOfTicketsBookedByUser().set(flightIndexInFlightList, newNumOfTickets);
    }

    void addNumberOfTicketsForNewFlight(Customer customer, int numOfTickets) {
        customer.getNumOfTicketsBookedByUser().add(numOfTickets);
    }

    boolean isFlightAlreadyAddedToCustomerList(List<Flight> flightList, Flight flight) {
        boolean addedOrNot = false;
        for (Flight flight1 : flightList) {
            if (flight1.getFlightNumber().equalsIgnoreCase(flight.getFlightNumber())) {
                this.flightIndexInFlightList = flightList.indexOf(flight1);
                addedOrNot = true;
                break;
            }
        }
        return addedOrNot;
    }

    String flightStatus(Flight flight) {
        boolean isFlightAvailable = false;
        for (Flight list : flight.getFlightList()) {
            if (list.getFlightNumber().equalsIgnoreCase(flight.getFlightNumber())) {
                isFlightAvailable = true;
                break;
            }
        }
        if (isFlightAvailable) {
            return "As Per Schedule";
        } else {
            return "   Cancelled   ";
        }
    }

    public String toString(int serialNum, Flight flights, Customer customer) {
        return String.format("| %-5d| %-41s | %-9s | \t%-9d | %-21s | %-22s | %-10s  |   %-6sHrs |  %-4s  | %-10s |", serialNum, flights.getFlightSchedule(), flights.getFlightNumber(), customer.getNumOfTicketsBookedByUser().get(serialNum - 1), flights.getFromWhichCity(), flights.getToWhichCity(), flights.fetchArrivalTime(), flights.getFlightTime(), flights.getGate(), flightStatus(flights));
    }

    @Override
    public void displayFlightsRegisteredByOneUser(String userID) {
        System.out.println();
        System.out.print("+------+-------------------------------------------+-----------+------------------+-----------------------+------------------------+---------------------------+-------------+--------+-----------------+\n");
        System.out.printf("| Num  | FLIGHT SCHEDULE\t\t\t   | FLIGHT NO |  Booked Tickets  | \tFROM ====>>       | \t====>> TO\t   | \t    ARRIVAL TIME       | FLIGHT TIME |  GATE  |  FLIGHT STATUS  |%n");
        System.out.print("+------+-------------------------------------------+-----------+------------------+-----------------------+------------------------+---------------------------+-------------+--------+-----------------+\n");
        for (Customer customer : Customer.customerCollection) {
            List<Flight> f = customer.getFlightsRegisteredByUser();
            int size = customer.getFlightsRegisteredByUser().size();
            if (userID.equals(customer.getUserID())) {
                for (int i = 0; i < size; i++) {
                    System.out.println(toString((i + 1), f.get(i), customer));
                    System.out.print("+------+-------------------------------------------+-----------+------------------+-----------------------+------------------------+---------------------------+-------------+--------+-----------------+\n");
                }
            }
        }
    }


    public String toString(int serialNum, Customer customer, int index) {
        return String.format("%10s| %-10d | %-10s | %-32s | %-7s | %-27s | %-35s | %-23s |       %-7s  |", "", (serialNum + 1), customer.randomIDDisplay(customer.getUserID()), customer.getName(),
                customer.getAge(), customer.getEmail(), customer.getAddress(), customer.getPhone(), customer.getNumOfTicketsBookedByUser().get(index));
    }

    @Override
    public void displayHeaderForUsers(FlightManager flight, List<Customer> c) {
        System.out.printf("\n%65s Displaying Registered Customers for Flight No. \"%-6s\" %s \n\n", "+++++++++++++", flight.getFlight().getFlightNumber(), "+++++++++++++");
        System.out.printf("%10s+------------+------------+----------------------------------+---------+-----------------------------+-------------------------------------+-------------------------+----------------+\n", "");
        System.out.printf("%10s| SerialNum  |   UserID   | Passenger Names                  | Age     | EmailID\t\t       | Home Address\t\t\t     | Phone Number\t       | Booked Tickets |%n", "");
        System.out.printf("%10s+------------+------------+----------------------------------+---------+-----------------------------+-------------------------------------+-------------------------+----------------+\n", "");
        int size = flight.getListOfRegisteredCustomersInAFlight().size();
        for (int i = 0; i < size; i++) {
            System.out.println(toString(i, c.get(i), flightIndex(c.get(i).getFlightsRegisteredByUser(), flight.getFlight())));
            System.out.printf("%10s+------------+------------+----------------------------------+---------+-----------------------------+-------------------------------------+-------------------------+----------------+\n", "");
        }
    }

    @Override
    public void displayRegisteredUsersForAllFlight() {
        System.out.println();
        for (Flight flight : FlightManager.flightList) {
            FlightManager flightManager = new FlightManager(flight.getFlightNumber()); // Use FlightManager
            List<Customer> registeredCustomers = flightManager.getListOfRegisteredCustomersInAFlight();
            if (!registeredCustomers.isEmpty()) {
                displayHeaderForUsers(flightManager, registeredCustomers); // Pass FlightManager
            }
        }
    }

    int flightIndex(List<Flight> flightList, Flight flight) {
        int i = -1;
        for (Flight flight1 : flightList) {
            if (flight1.equals(flight)) {
                i = flightList.indexOf(flight1);
            }
        }
        return i;
    }

    @Override
    public void displayRegisteredUsersForASpecificFlight(String flightNum) {
        System.out.println();
        for (Flight flight : flight.getFlightList()) {
            FlightManager flightManager = new FlightManager(flight.getFlightNumber()); // Use FlightManager
            List<Customer> registeredCustomers = flightManager.getListOfRegisteredCustomersInAFlight();
            if (flight.getFlightNumber().equalsIgnoreCase(flightNum)) {
                displayHeaderForUsers(flightManager, registeredCustomers);
            }
        }
    }


}