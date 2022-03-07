package com.softline.dossier.be.task_managment.domain;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class TaskCategorys extends AbstractSingleColumnStandardBasicType<TaskCategory> {
    public TaskCategorys() {
        super(new VarcharTypeDescriptor(), new TaskCategorysDescriptor());
    }

    @Override
    public String getName() {
        return "TaskCategorys";
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) throws HibernateException {
        return null;
    }

}
