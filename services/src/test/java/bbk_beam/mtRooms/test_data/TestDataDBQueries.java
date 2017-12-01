package bbk_beam.mtRooms.test_data;

public class TestDataDBQueries {
    //Building table
    public static String building = "INSERT INTO Building( name, address1, address2, postcode, country, telephone ) "
            + "VALUES( \"Test Building\", \"1 Test road\", null, \"AB1 C34\", \"UK\", \"01234 567 890\" )";
    //Floor table
    public static String floors = "INSERT INTO Floor( id, building_id, description ) VALUES "
            + "( 1, 1, \"Ground level\" ), "
            + "( 2, 1, \"First floor\" ), "
            + "( 3, 1, \"Second floor\" )";
    //RoomCategory table //TODO
    public static String roomCategories = "INSERT INTO RoomCategory( capacity, dimension ) VALUES "
            + "( 10, 25 ), "
            + "( 50, 20 ), "
            + "( 10, 25 ), "
            + "( 10, 25 ), "
            + "( 10, 25 ), "
            + "( 10, 25 ), "
            + "( 10, 25 )";
    //Room table //TODO
    public static String rooms = "INSERT INTO Room( flood_id, building_id, room_category_id, description ) VALUES "
            + "( 1, 1, 1, \"\" ), "
            + "( 1, 1, 1, \"\" ), "
            + "( 1, 1, 1, \"\" ), "
            + "( 1, 1, 1, \"\" ), "
            + "( 1, 1, 1, \"\" ), "
            + "( 1, 1, 1, \"\" ), "
            + "( 1, 1, 1, \"\" ), "
            + "( 1, 1, 1, \"\" )";
}
