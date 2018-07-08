# liquibase-kotlin-dsl
Kotlin DSL which translates to Liquibase YAML.

Compile: `kotlinc -d out src` (make sure `out` directory exists)  
Run example: `kotlinc -classpath out -script example.kts`  
  
Example code:  
```kotlin
changeSet(id = "example-changeset", author = "domagoj-latecki", comment = "This is an example changeset.") {
    create table "user" inside "public" schema {
        add column "id" type serial constraints {
            primaryKey = true
            nullable = false
        }

        add column "uuid" type uuid constraints {
            unique = true
            nullable = false
        }

        add column "username" type varchar(255) constraints {
            unique = true
            nullable = false
        }

        add column "last_login" type `timestamp with time zone` constraints {}

        add column "login_count" type integer constraints {}

        add column "custom_type" type "custom type" constraints {}
    }

    create index "username_idx" on "user" inside "public" schema {
        unique = false

        add column "username"
    }
}
```

Provided example will generate following YAML-fomatted output:
```yaml
databaseChangeLog:
    # This is an example changeset.
    - changeSet:
        id: 201807082030-example-changeset
        author: domagoj-latecki
        changes:
        - createTable:
            schemaName: public
            tableName: user
            columns:
            - column:
                name: id
                type: serial
                autoIncrement: true
                constraints:
                    nullable: false
                    primaryKey: true
                    primaryKeyName: pk_user
            - column:
                name: uuid
                type: uuid
                constraints:
                    nullable: false
                    unique: true
            - column:
                name: username
                type: varchar(255)
                constraints:
                    nullable: false
                    unique: true
            - column:
                name: last_login
                type: timestamp with time zone
                constraints:
            - column:
                name: login_count
                type: integer
                constraints:
            - column:
                name: custom_type
                type: custom type
                constraints:
        - createIndex:
            schemaName: public
            tableName: user
            indexName: username_idx
            unique: false
            columns:
                - column:
                    name: username
```
