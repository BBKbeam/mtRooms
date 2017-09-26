package bbk_beam.mtRooms.db.database;

public class ColProperty {
    private String table;
    private String name;
    private String type;
    private boolean not_null_flag;
    private String default_value;
    private int pk;

    public ColProperty(String table,
                       String name,
                       String type,
                       boolean not_null_flag,
                       String default_value,
                       int pk) {
        this.table = table;
        this.name = name;
        this.type = type;
        this.not_null_flag = not_null_flag;
        this.default_value = default_value;
        this.pk = pk;
    }

    public String getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int isNotNull() {
        return not_null_flag ? 1 : 0;
    }

    public String getDefaultValue() {
        return default_value;
    }

    public int getPk() {
        return pk;
    }

    @Override
    public String toString() {
        return this.table + "." + this.name;
    }
}
