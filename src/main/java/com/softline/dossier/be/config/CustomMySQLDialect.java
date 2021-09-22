package com.softline.dossier.be.config;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.dialect.MySQLDialect;

import java.sql.Types;

public class CustomMySQLDialect extends MySQLDialect {
    public CustomMySQLDialect() {
        registerHibernateType(Types.OTHER, StringArrayType.class.getName());
        registerHibernateType(Types.OTHER, IntArrayType.class.getName());
        registerHibernateType(Types.OTHER, JsonStringType.class.getName());
        registerHibernateType(Types.OTHER, JsonBinaryType.class.getName());
        registerHibernateType(Types.OTHER, JsonNodeBinaryType.class.getName());

    }
}
