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
