package com.kinespherept.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.Table
import java.lang.reflect.Field

@Service
class LookupDao {

    JdbcTemplate jdbcTemplate

    LookupDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate
    }

    /**
     * Returns the max ID value from the table associated to the given class type, which is
     * expected to be an @Entity with a declared @Table and @Id field.
     */
    Object getMaxIdValue(Class clazz) {
        Table table = clazz.getAnnotation(Table)

        if(!table) {
            throw new IllegalArgumentException("Class type [${clazz.name}] is not an @Entity, and is therefore not a valid class type for this method")
        }

        Field field = clazz.getDeclaredFields().find {
            Field f -> f.getAnnotation(Id) != null
        }

        Column column = field.getAnnotation(Column)

        jdbcTemplate.queryForObject("select max(${column.name()}) from ${table.name()}", null, field.getType())
    }
}
