interface ColumnType {
    val name: String
}

object serial : ColumnType {
    override val name: String = "serial\n                autoIncrement: true"
}

class varchar(length: Int) : ColumnType {
    override val name: String = "varchar($length)"
}

object `timestamp with time zone` : ColumnType {
    override val name: String = "timestamp with time zone"
}

object integer : ColumnType {
    override val name: String = "integer"
}

object uuid : ColumnType {
    override val name: String = "uuid"
}

object text : ColumnType {
    override val name: String = "text"
}
