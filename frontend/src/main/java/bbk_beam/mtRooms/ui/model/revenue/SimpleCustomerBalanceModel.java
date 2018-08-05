package bbk_beam.mtRooms.ui.model.revenue;

import bbk_beam.mtRooms.revenue.dto.SimpleCustomerBalance;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SimpleCustomerBalanceModel {
    private IntegerProperty customer_id = new SimpleIntegerProperty();
    private IntegerProperty reservation_count = new SimpleIntegerProperty();
    private DoubleProperty cost = new SimpleDoubleProperty();
    private DoubleProperty paid = new SimpleDoubleProperty();
    private DoubleProperty balance = new SimpleDoubleProperty();

    /**
     * Constructor
     *
     * @param customerBalance SimpleCustomerBalance DTO
     */
    public SimpleCustomerBalanceModel(SimpleCustomerBalance customerBalance) {
        this.customer_id.set(customerBalance.getCustomerID());
        this.reservation_count.set(customerBalance.getReservationCount());
        this.cost.set(customerBalance.getTotalCost());
        this.paid.set(customerBalance.getTotalPaid());
        this.balance.set(customerBalance.getBalance());
    }

    /**
     * Gets the Customer ID property
     *
     * @return Customer ID property
     */
    public IntegerProperty customerIdProperty() {
        return customer_id;
    }

    /**
     * Gets the reservation count property
     *
     * @return Reservation count property
     */
    public IntegerProperty reservationCountProperty() {
        return reservation_count;
    }

    /**
     * Gets the cost property
     *
     * @return Cost property
     */
    public DoubleProperty costProperty() {
        return cost;
    }

    /**
     * Gets the paid property
     *
     * @return Paid property
     */
    public DoubleProperty paidProperty() {
        return paid;
    }

    /**
     * Gets the balance property
     *
     * @return Balance property
     */
    public DoubleProperty balanceProperty() {
        return balance;
    }
}
