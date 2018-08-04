package bbk_beam.mtRooms.ui.model.frontdesk;

import bbk_beam.mtRooms.reservation.dto.Payment;
import bbk_beam.mtRooms.ui.model.SessionManager;
import bbk_beam.mtRooms.ui.model.common.GenericModelTable;
import javafx.util.Pair;

import java.util.Collection;
import java.util.List;

public class CustomerPaymentsTable extends GenericModelTable<Pair<Integer, List<Payment>>, CustomerPaymentsModel> {

    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public CustomerPaymentsTable(SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void loadData(Collection<Pair<Integer, List<Payment>>> payments) {
        if (!this.observableList.isEmpty())
            this.observableList.clear();
        for (Pair<Integer, List<Payment>> reservation_payments : payments) {
            for (Payment payment : reservation_payments.getValue())
                this.observableList.add(new CustomerPaymentsModel(reservation_payments.getKey(), payment));
        }
    }

    @Override
    public void appendData(Collection<Pair<Integer, List<Payment>>> payments) {
        for (Pair<Integer, List<Payment>> reservation_payments : payments) {
            for (Payment payment : reservation_payments.getValue())
                this.observableList.add(new CustomerPaymentsModel(reservation_payments.getKey(), payment));
        }
    }

    /**
     * Adds data to table incrementally
     *
     * @param reservation_id Reservation ID of payments
     * @param payments       List of Payments for the reservation
     */
    public void loadData(Integer reservation_id, List<Payment> payments) {
        for (Payment payment : payments)
            this.observableList.add(new CustomerPaymentsModel(reservation_id, payment));
    }
}
