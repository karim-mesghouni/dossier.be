package com.softline.dossier.be;

import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.File;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.metamodel.EntityType;

interface Value {
}

interface Function {
}

@SuppressWarnings("unchecked")
@SpringBootTest
public class ApplicationTest {

    @Test
    void contextLoads() {

        var em = Database.em();
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(File.class);
        var er = cq.from(File.class);
        var et = (EntityType<File>) Database.getEntityType(File.class);
        var join = er.join("fileActivities").join("activity");
        cq.where(cb.equal(join.get("id"), 1));
        // user => user.maxActivities > SIZE(user.activities.?[SIZE(fileTasks) != 0 or done = true]) != 0 and user.createdAt > '2020-20-11'
        var root = new Root("user");
        var activities = root.get("activities");
        new Expression<>(
                new Expression<>(
                        new Expression<>(
                                root.get("maxActivities"),
                                ">",
                                new Size<>(
                                        new Filter(
                                                activities,
                                                new Expression<>(
                                                        new Expression<>(
                                                                new Size<>(
                                                                        activities.get("fileTasks")
                                                                ),
                                                                "!=",
                                                                0
                                                        ),
                                                        "or",
                                                        new Expression<>(
                                                                activities.get("done"),
                                                                "=",
                                                                true
                                                        )
                                                )
                                        )
                                )
                        ),
                        "!=",
                        0
                ),
                "and",
                new Expression<>(
                        root.get("createdAt"),
                        ">",
                        "2020-20-11"
                )
        );
    }

}

class Root extends Path {
    public Root(String property) {
        super(property, null);
    }
}

class Path implements Value {
    public final String property;
    public final Path parent;

    public Path(String property, Path parent) {
        this.property = property;
        this.parent = parent;
    }

    Path get(String property) {
        return new Path(property, this);
    }
}

class Expression<L, R> {
    public final L left;
    public final String operator;
    public final R right;

    public Expression(L property, String operator, R value) {
        this.left = property;
        this.operator = operator;
        this.right = value;
    }
}

class Size<L> implements Function {
    public final L list;

    Size(L list) {
        this.list = list;
    }
}

class Filter implements Function {
    public final Expression<?, ?> filter;
    public final Value list;

    Filter(Value list, Expression<?, ?> filter) {
        this.filter = filter;
        this.list = list;
    }
}