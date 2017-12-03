package bbk_beam.mtRooms.test_data;

public class TestDataDBQueries {
    //Building table
    public static String building = "INSERT INTO Building( name, address1, address2, postcode, country, telephone ) "
            + "VALUES( \"Test Building\", \"1 Test road\", null, \"AB1 C34\", \"UK\", \"01234 567 890\" )"; //id = 1
    //Floor table
    public static String floors = "INSERT INTO Floor( id, building_id, description ) VALUES "
            + "( 1, 1, \"Ground level\" ), "    //id = 1
            + "( 2, 1, \"First floor\" ), "     //id = 2
            + "( 3, 1, \"Second floor\" )";     //id = 3
    //RoomCategory table
    public static String roomCategories = "INSERT INTO RoomCategory( capacity, dimension ) VALUES "
            + "( 10, 10 ), "    //id = 1
            + "( 20, 20 ), "    //id = 2
            + "( 30, 30 ), "    //id = 3
            + "( 30, 30 ), "    //id = 4
            + "( 50, 50 ), "    //id = 5
            + "( 100, 100 )";   //id = 6
    //RoomPrice table
    public static String roomPrices = "INSERT INTO RoomPrice( price, year ) VALUES "
            + "( 40, 2007 ), "  //id = 1
            + "( 45, 2007 ), "  //id = 2
            + "( 60, 2007 ), "  //id = 3
            + "( 65, 2007 ), "  //id = 4
            + "( 80, 2007 ), "  //id = 5
            + "( 100, 2007 ), " //id = 6
            + "( 45, 2008 ), "  //id = 7
            + "( 50, 2008 ), "  //id = 8
            + "( 65, 2008 ), "  //id = 9
            + "( 70, 2008 ), "  //id = 10
            + "( 85, 2008 ), "  //id = 11
            + "( 110, 2008 )";  //id = 12
    //RoomFixtures table //TODO
    public static String roomFixtures = "INSERT INTO RoomFixtures( fixed_chairs, catering_space, whiteboard, projector ) VALUES "
            + "( 0, 0, 1, 0 ), " //id = 1 (whiteboard)
            + "( 0, 0, 1, 1 ), " //id = 2 (whiteboard, projector)
            + "( 0, 1, 1, 1 ), " //id = 3 (catering space, whiteboard, projector)
            + "( 0, 1, 0, 1 ), " //id = 4 (catering space, projector )
            + "( 1, 0, 0, 1 ), " //id = 5 (fixed chairs, projector )
            + "( 1, 0, 0, 0 )";  //id = 6 (catering space)
    //Room table
    public static String rooms = "INSERT INTO Room( id, floor_id, building_id, room_category_id, description ) VALUES "
            + "( 1, 1, 1, 1, \"Small room 1\" ), "     //id = 1
            + "( 2, 1, 1, 1, \"Small room 2\" ), "     //id = 2
            + "( 3, 1, 1, 2, \"Medium room 1\" ), "    //id = 3
            + "( 4, 2, 1, 3, \"Medium room 2\" ), "    //id = 4
            + "( 5, 2, 1, 4, \"Medium room 3\" ), "    //id = 5
            + "( 6, 2, 1, 4, \"Large room 1\" ), "     //id = 6
            + "( 7, 3, 1, 5, \"Large room 2\" ), "     //id = 7
            + "( 8, 3, 1, 6, \"Theatre\" )";           //id = 8
    //Room_has_RoomPrice table
    public static String room_has_RoomPrice = "INSERT INTO Room_has_RoomPrice( room_id, floor_id, building_id, price_id ) VALUES "
            + "( 1, 1, 1, 1 ), "    //2007, Small room 1 @40
            + "( 2, 1, 1, 1 ), "    //2007, Small room 2 @40
            + "( 3, 1, 1, 2 ), "    //2007, Medium room 1 @45
            + "( 4, 2, 1, 3 ), "    //2007, Medium room 2 @60
            + "( 5, 2, 1, 4 ), "    //2007, Medium room 3 @65
            + "( 6, 2, 1, 4 ), "    //2007, Large room 1 @65
            + "( 7, 3, 1, 5 ), "    //2007, Large room 2 @80
            + "( 8, 3, 1, 6 ), "    //2007, Theatre @100
            + "( 1, 1, 1, 7 ), "    //2008, Small room 1 @45
            + "( 2, 1, 1, 7 ), "    //2008, Small room 2 @45
            + "( 3, 1, 1, 8 ), "    //2008, Medium room 1 @50
            + "( 4, 2, 1, 9 ), "    //2008, Medium room 2 @65
            + "( 5, 2, 1, 10 ), "   //2008, Medium room 3 @70
            + "( 6, 2, 1, 10 ), "   //2008, Large room 1 @70
            + "( 7, 3, 1, 11 ), "   //2008, Large room 1 @85
            + "( 8, 3, 1, 12 )";    //2008, Theatre @110
    //Room_has_RoomFixtures table //TODO
    public static String room_has_RoomFixtures = "INSERT INTO Room_has_RoomFixtures( room_id, floor_id, building_id, room_fixture_id ) VALUES "
            + "( 1, 1, 1, 6 ), " //Small room 1 with catering space
            + "( 2, 1, 1, 1 ), " //Small room 2 with whiteboard
            + "( 3, 1, 1, 2 ), " //Medium room 1 with whiteboard, projector
            + "( 4, 2, 1, 3 ), " //Medium room 2 with catering space, whiteboard, projector
            + "( 5, 2, 1, 3 ), " //Medium room 3 with catering space, whiteboard, projector
            + "( 6, 2, 1, 4 ), " //Large room 2 with catering space, projector
            + "( 7, 3, 1, 4 ), " //Large room 2 with catering space, projector
            + "( 8, 3, 1, 5 )";  //Theatre with fixed chairs, projector
    //DiscountCategory table
    public static String discountCategories = "INSERT INTO DiscountCategory( description ) VALUES "
            //( "None" )            //id = 1 -> already hardcoded in DB build code
            + "( \"Student\" ), "   //id = 2
            + "( \"Member\" )";     //id = 3
    //MembershipType table
    public static String membershipTypes = "INSERT INTO MembershipType( description, discount_category_id ) VALUES "
            //( "None", 1 )                 //id = 1 -> already hardcoded in DB build code
            + "( \"Student\", 2 ), "        //id = 2
            + "( \"Full Member\", 3 )";     //id = 3
    //Discount table
    public static String discounts = "INSERT INTO Discount( discount_rate, discount_category_id ) VALUES "
            //( 0., 1 )         //id = 1 ->already hardcoded in DB build code
            + "( 25., 2 ), "    //id = 2
            + "( 10., 3 )";     //id = 3
    //Customer table
    public static String customers = "INSERT INTO Customer( membership_type_id, customer_since, title, name, surname, "
            + "address_1, address_2, city, county, country, postcode, telephone_1, telephone_2, email ) VALUES "
            //Customer id = 1
            + "( 1, \"2015-10-15 16:15:12\", \"Mrs\", \"Joanne\", \"Bouvier\", "
            + "\"Flat 4\", \"21 big road\", \"London\", \"London\", \"UK\", \"W1 4AQ\", \"+44 9876 532 123\", null, \"jbouvier@mail.com\" ), "
            //Customer id = 2
            + "( 2, \"2016-03-26 12:42:03\", \"Mr\", \"John\", \"Dagart\", "
            + "\"26 main road\", null, \"Norwich\", \"Norfolk\", \"UK\", \"NR2 18D\", \"+44 1234 567 765\", null, \"jd445@netmail.com\" ), "
            //Customer id = 3
            + "( 3, \"2017-06-05 10:36:41\", \"Miss\", \"Alice\", \"Beniccio\", "
            + "\"5 Highbury road\", null, \"London\", \"London\", \"UK\", \"N4 6SW\", \"+44 1020 304 050\", \"+44 9080 706 050\", \"aliceb01@othermail.com\" )";
    //Payment table //TODO
    public static String payments = "INSERT INTO PaymentMethod( amount, payment_method, timestamp ) VALUES "
            + "(), "
            + "()";
    //Reservation_has_Payment table //TODO
    public static String reservation_has_payments = "INSERT INTO Reservation_has_Payment( reservation_id, payment_id ) VALUES "
            + "(), "
            + "()";
    //Reservation table //TODO
    public static String reservations = "INSERT INTO Reservation( created_timestamp, customer_id, discount_id ) VALUES "
            + "(), "
            + "()";
    //Room_has_Reservation table //TODO
    public static String room_has_reservation = "INSERT INTO Room_has_Reservation( room_id, floor_id, building_id, "
            + "reservation_id, timestamp_in, timestamp_out, note, cancelled_flag ) VALUES "
            + "(), "
            + "(), "
            + "(), "
            + "()";
}