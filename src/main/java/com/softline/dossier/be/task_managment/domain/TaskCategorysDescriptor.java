package com.softline.dossier.be.task_managment.domain;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.spi.JdbcRecommendedSqlTypeMappingContext;

import java.util.Properties;

public class TaskCategorysDescriptor extends AbstractTypeDescriptor<TaskCategory> {

    protected TaskCategorysDescriptor() {
        super(TaskCategory.class, new ImmutableMutabilityPlan<>());
    }

    @Override
    public String toString(TaskCategory value) {
        return null;
    }

    @Override
    public TaskCategory fromString(String string) {
        return null;
    }

    @Override
    public <X> X unwrap(TaskCategory value, Class<X> type, WrapperOptions options) {
        return null;
    }

    @Override
    public <X> TaskCategory wrap(X value, WrapperOptions options) {
        return null;
    }

    @Override
    public SqlTypeDescriptor getJdbcRecommendedSqlType(JdbcRecommendedSqlTypeMappingContext context) {
        return null;
    }


}
