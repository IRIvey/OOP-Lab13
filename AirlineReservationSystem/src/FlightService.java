import java.util.ArrayList;
import java.util.List;

public class FlightService {
    private List<Customer> registeredCustomers = new ArrayList<>();

    public void addNewCustomerToFlight(Customer customer) {
        registeredCustomers.add(customer);
    }

    public boolean isCustomerAlreadyAdded(List<Customer> customersList, Customer customer) {
        return customersList.stream().anyMatch(c -> c.getUserID().equals(customer.getUserID()));
    }

    public void deleteFlight(String flightNumber) {
        FlightScheduler.getFlightList().removeIf(flight -> flight.getFlightNumber().equalsIgnoreCase(flightNumber));
    }
}
