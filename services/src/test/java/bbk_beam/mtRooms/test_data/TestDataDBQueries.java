package bbk_beam.mtRooms.test_data;

public class TestDataDBQueries {
    //Building table
    public static String building = "INSERT INTO Building( name, address1, address2, city, postcode, country, telephone ) "
            + "VALUES( \"Test Building\", \"1 Test road\", null, \"London\", \"AB1 C34\", \"UK\", \"01234 567 890\" )"; //id = 1
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
            + "( 40, 40 ), "    //id = 4
            + "( 50, 50 ), "    //id = 5
            + "( 100, 100 ), "  //id = 6
            + "( 112, 150 )";   //id = 7
    //RoomPrice table
    public static String roomPrices = "INSERT INTO RoomPrice( price, year ) VALUES "
            + "( 40.00, 2007 ), "  //id = 1
            + "( 45.00, 2007 ), "  //id = 2
            + "( 60.00, 2007 ), "  //id = 3
            + "( 65.00, 2007 ), "  //id = 4
            + "( 80.00, 2007 ), "  //id = 5
            + "( 100.00, 2007 ), " //id = 6
            + "( 45.00, 2008 ), "  //id = 7
            + "( 50.00, 2008 ), "  //id = 8
            + "( 65.00, 2008 ), "  //id = 9
            + "( 70.00, 2008 ), "  //id = 10
            + "( 85.00, 2008 ), "  //id = 11
            + "( 110.00, 2008 )";  //id = 12
    //RoomFixtures table
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
            + "( 7, 3, 1, 11 ), "   //2008, Large room 2 @85
            + "( 8, 3, 1, 12 )";    //2008, Theatre @110
    //Room_has_RoomFixtures table
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
            + "\"5 Highbury road\", null, \"London\", \"London\", \"UK\", \"N4 6SW\", \"+44 1020 304 050\", \"+44 9080 706 050\", \"aliceb01@othermail.com\" ), "
            //Customer id = 4
            + "( 1, \"2018-01-01 00:01:00\", \"Mr\", \"Nicholas\", \"Cage\", "
            + "\"1 Epic road\", null, \"Bruteville\", \"Bruteshire\", \"Kingdom of Epicness\", \"EP1C\", \"+1337 11111 11\", null, \"nickcage@awseome.com\" ), "
            //Customer id = 5
            + "( 1, \"2018-03-01 11:56:00\", \"Mr\", \"Peter\", \"Smith\", "
            + "\"23 Eccles road\", \"Apartment 12\", \"London\", \"London\", \"UK\", \"N1 1DD\", \"+44 8700 123 123\", null, \"ps8@dasmail.com\" )";
    //Payment table
    public static String payments = "INSERT INTO Payment( id, hash_id, amount, payment_method, timestamp, note ) VALUES "
            + "( 1, \"TestHashID0000001\", 77.00, 2, \"2018-02-02 19:00:00\", \"85*.90 credit c. Room L2\" ), " //@ 85*.90 debit c. Room L2
            + "( 2, \"TestHashID0000002\", 34.00, 1, \"2018-02-09 09:00:00\", \"45*.75 cash Room S1\" ), "      //@ 45*.75 cash Room S1
            + "( 3, \"TestHashID0000003\", 40.50, 2, \"2018-02-10 01:58:00\", \"65*.90 credit.c Room M3\" ), "  //@ pt.1 of 70*.90 debit.c Room M3
            + "( 4, \"TestHashID0000004\", 22.50, 2, \"2018-02-11 13:12:50\", \"65*.90 credit.c Room M3\" ) ";  //@ pt.2 of 70*.90 debit.c Room M3

    //Reservation_has_Payment table
    public static String reservation_has_payments = "INSERT INTO Reservation_has_Payment( reservation_id, payment_id ) VALUES "
            + "( 1, 1 ), "    // reservation 1 cash
            + "( 2, 2 ), "    // reservation 2 debit c.
            + "( 4, 3 ), "    // reservation 4 debit c.
            + "( 4, 4 )";     // reservation 4 debit c.

    //Reservation table
    public static String reservations = "INSERT INTO Reservation(id, created_timestamp, customer_id, discount_id ) VALUES "
            + "( 1, \"2018-02-09 10:00:00\", 3, 3 ), "   // Reservation id = 1
            + "( 2, \"2018-02-09 10:00:00\", 1, 2 ), "   // Reservation id = 2
            + "( 3, \"2018-06-13 15:00:00\", 3, 3 ), "   // Reservation id = 3
            + "( 4, \"2015-01-22 14:00:00\", 2, 1 ), "   // Reservation id = 4
            + "( 5, \"2016-05-15 16:59:01\", 4, 3 ), "   // Reservation id = 5
            + "( 6, \"2018-03-01 12:00:00\", 5, 1 )";    // Reservation id = 6

    //Room_has_Reservation table
    public static String room_has_reservation = "INSERT INTO Room_has_Reservation( room_id, floor_id, building_id, "
            + "reservation_id, timestamp_in, timestamp_out, room_price_id, seated_count, catering, notes, cancelled_flag ) VALUES "
            + "( 7, 3, 1, 1, \"2018-02-09 10:05:00\" , \"2018-02-09 11:00:00\", 11, 95, 1, \"nothing to note\" , 0 ), " // Room L2, Floor 3, Building 1, Reservation 1, Member 10%
            + "( 4, 2, 1, 1, \"2018-02-09 11:00:00\" , \"2018-02-09 12:00:00\", 9, 30, 0, \"nothing to note\" , 0 ), "  // Room M2, Floor 2, Building 1, Reservation 1, Member 10%
            + "( 1, 1, 1, 1, \"2018-02-09 11:00:00\" , \"2018-02-09 12:00:00\", 7, 8, 0, \"nothing to note\" , 1 ), "   // Room S1, Floor 0, Building 1, Reservation 1, Member 10%
            + "( 1, 1, 1, 2, \"2018-02-09 10:15:00\" , \"2018-02-09 12:30:00\", 7, 10, 0, \"\" , 0 ), "                 // Room S1, Floor 0, Building 1, Reservation 2, Student 25%
            + "( 5, 2, 1, 3, \"2018-06-13 15:00:00\" , \"2018-06-13 16:00:00\", 10, 35, 0, \" some text\" , 0 ), "      // Room M3, Floor 1, Building 1, Reservation 3, Member 10%
            + "( 1, 1, 1, 4, \"2015-01-22 14:00:00\" , \"2015-01-22 18:00:00\", 7, 10, 0, \"\", 1 ), "                  // Room S1, Floor 0, Building 1, Reservation 4, Non-member 0%
            + "( 5, 2, 1, 5, \"2015-01-25 09:00:00\" , \"2015-01-25 10:00:00\", 10, 40, 1, \"\", 0 ), "                 // Room M3, Floor 2, Building 1, Reservation 5, Member 10%
            + "( 1, 1, 1, 6, \"2018-03-10 09:00:00\" , \"2018-03-10 10:00:00\", 7, 10, 0, \"\", 0 ), "
            + "( 1, 1, 1, 6, \"2018-03-10 14:00:00\" , \"2018-03-10 17:00:00\", 7, 10, 0, \"\", 0 ), "
            + "( 3, 1, 1, 6, \"2018-03-10 09:00:00\" , \"2018-03-10 10:00:00\", 8, 18, 0, \"\", 0 ), "
            + "( 3, 1, 1, 6, \"2018-03-10 14:00:00\" , \"2018-03-10 17:00:00\", 8, 18, 0, \"\", 0 ), "
            + "( 4, 2, 1, 6, \"2018-03-10 09:00:00\" , \"2018-03-10 12:00:00\", 9, 30, 0, \"\", 0 ), "
            + "( 4, 2, 1, 6, \"2018-03-10 14:00:00\" , \"2018-03-10 17:00:00\", 9, 28, 0, \"\", 0 ), "
            + "( 5, 2, 1, 6, \"2018-03-10 09:00:00\" , \"2018-03-10 10:00:00\", 9, 37, 0, \"\", 0 ), "
            + "( 5, 2, 1, 6, \"2018-03-10 11:00:00\" , \"2018-03-10 12:00:00\", 10, 37, 1, \"\", 0 ), "
            + "( 5, 2, 1, 6, \"2018-03-10 12:30:00\" , \"2018-03-10 14:00:00\", 10, 40, 0, \"\", 0 ), "
            + "( 5, 2, 1, 6, \"2018-03-10 14:30:00\" , \"2018-03-10 16:00:00\", 10, 38, 0, \"\", 0 ), "
            + "( 5, 2, 1, 6, \"2018-03-10 18:00:00\" , \"2018-03-10 20:00:00\", 10, 36, 0, \"\", 0 ), "
            + "( 7, 3, 1, 6, \"2018-03-10 14:00:00\" , \"2018-03-10 18:00:00\", 11, 42, 1, \"\", 0 ), "
            + "( 8, 3, 1, 6, \"2018-03-10 14:00:00\" , \"2018-03-10 18:00:00\", 12, 89, 0, \"\", 0 )";
}