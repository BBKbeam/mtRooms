package bbk_beam.mtRooms.reservation.dto;

import java.util.List;

public class ReservationDTO {
    private CustomerDTO customer;
    private Integer id;
    private Integer payment_method_id;
    private DiscountDTO discount;
    private List<RoomDTO> rooms;

}
