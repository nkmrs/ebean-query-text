package com.github.nkmrs.utils;

import com.avaje.ebean.Query;
import models.Example1;
import models.UserAge;
import models.UserName;
import org.avaje.agentloader.AgentLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class QueryTextTest {

    @BeforeClass
    public static void setup() {
        AgentLoader.loadAgentFromClasspath("ebean-agent", "");

        createUserName("Johannes"); // id = 1
        createUserName("Riko"); // id = 2
        createUserName("Marry"); // id = 3

        createUserAge(15); // id = 1
        createUserAge(16); // id = 2
        createUserAge(17); // id = 3
    }

    private static void createUserName(String name) {
        UserName n = new UserName();
        n.name = name;
        n.save();
    }

    private static void createUserAge(Integer name) {
        UserAge n = new UserAge();
        n.age = name;
        n.save();
    }

    @Test
    public void findAll() {
        Query<Example1> q = QueryText.getDefault().getQuery("example1", Example1.class);
        List<Example1> list = q.findList();
        assertEquals(list.size(), 3);
    }

    @Test
    public void order() {
        String orderByString = "order by name desc";
        Query<Example1> q = QueryText.getDefault().getQuery("example1", Example1.class, orderByString);
        List<Example1> list = q.findList();
        assertEquals((int) list.get(0).id, 2);
        assertEquals((int) list.get(1).id, 3);
        assertEquals((int) list.get(2).id, 1);
    }

    @Test
    public void where() {
        String orderByString = "order by name";
        Query<Example1> q = QueryText.getDefault().getQuery("example1", Example1.class, orderByString);
        List<Example1> list = q.where()
                .ge("age", 16)
                .findList();
        assertEquals(list.size(), 2);
        assertEquals((int) list.get(0).id, 3);
        assertEquals((int) list.get(1).id, 2);
    }

}
