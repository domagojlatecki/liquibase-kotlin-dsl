import java.time.LocalDateTime
import java.util.ArrayList

private fun Int.twoDigits(): String {
    return if (this < 10) {
        "0$this"
    } else {
        "$this"
    }
}

fun changeSet(id: String, author: String, comment: String? = null, changes: () -> Unit) {
    println("databaseChangeLog:")

    if (comment != null) {
        println("    # $comment")
    }

    val time = LocalDateTime.now()
    val timeString = "${time.year}${time.month.value.twoDigits()}${time.dayOfMonth.twoDigits()}" +
            "${time.hour.twoDigits()}${time.minute.twoDigits()}"

    println("    - changeSet:")
    println("        id: $timeString-$id")
    println("        author: $author")
    println("        changes:")

    changes()
}

class TableContext(private val tableName: String, private val schemaName: String) {
    val add = this
    infix fun column(columnName: String) = ColumnDefinition(tableName, columnName)
}

class TableInSchemaDefinition(private val tableName: String, private val schemaName: String) {
    infix fun schema(tableDef: TableContext.() -> Unit) {
        println("        - createTable:")
        println("            schemaName: $schemaName")
        println("            tableName: $tableName")
        println("            columns:")

        tableDef(TableContext(tableName, schemaName))
    }
}

class TableDefinition(private val tableName: String) {
    infix fun inside(schemaName: String) = TableInSchemaDefinition(tableName, schemaName)
}

class IndexContext(private val columns: MutableList<String>) {

    val add = this
    var unique: Boolean = false

    infix fun column(name: String) {
        this.columns.add("                - column:\n                    name: $name")
    }
}

class IndexOnTableInSchemaDefinition(private val indexName: String,
                                     private val tableName: String,
                                     private val schemaName: String) {
    infix fun schema(indexDef: IndexContext.() -> Unit) {
        val columns: MutableList<String> = ArrayList()
        val context = IndexContext(columns)

        indexDef(context)

        println("        - createIndex:")
        println("            schemaName: $schemaName")
        println("            tableName: $tableName")
        println("            indexName: idx_${tableName}_$indexName")
        println("            unique: ${context.unique}")
        println("            columns:")

        columns.forEach { println(it) }
    }
}

class IndexOnTableDefinition(private val indexName: String, private val tableName: String) {
    infix fun inside(schemaName: String) = IndexOnTableInSchemaDefinition(indexName, tableName, schemaName)
}

class IndexDefinition(private val indexName: String) {
    infix fun on(tableName: String) = IndexOnTableDefinition(indexName, tableName)
}

object create {
    infix fun table(name: String) = TableDefinition(name)
    infix fun index(name: String) = IndexDefinition(name)
}

class ForeignKeyConstraint {

    var value: String? = null

    infix fun on(value: String) {
        this.value = value
    }
}

class Constraints {
    var unique: Boolean? = null
    var nullable: Boolean? = null
    var primaryKey: Boolean? = null
    val foreignKey: ForeignKeyConstraint = ForeignKeyConstraint()
}

class TypedColumnDefinition(private val tableName: String,
                            private val columnName: String,
                            private val type: ColumnType) {
    infix fun constraints(constraintDef: Constraints.() -> Unit) {
        println("            - column:")
        println("                name: $columnName")
        println("                type: ${type.name}")
        println("                constraints:")

        val constraints = Constraints()

        constraintDef(constraints)

        if (constraints.nullable != null) {
            println("                    nullable: ${constraints.nullable}")
        }

        if (constraints.unique == true) {
            println("                    unique: true")
        }

        if (constraints.primaryKey == true) {
            println("                    primaryKey: true")
            println("                    primaryKeyName: pk_$tableName")
        }

        if (constraints.foreignKey.value != null) {
            val updatedName = constraints.foreignKey.value!!.replace('(', '_').replace(')', ' ').trim()

            println("                    references: ${constraints.foreignKey.value}")
            println("                    foreignKeyName: fk_${tableName}_$updatedName")
        }
    }
}

class ColumnDefinition(private val tableName: String, private val columnName: String) {
    infix fun type(type: String) = TypedColumnDefinition(tableName, columnName, object : ColumnType {
        override val name: String = type
    })

    infix fun type(type: ColumnType) = TypedColumnDefinition(tableName, columnName, type)
}

