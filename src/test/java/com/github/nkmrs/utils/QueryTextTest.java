package com.github.nkmrs.utils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
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

        EbeanServer server1 = Ebean.getServer("db");
        EbeanServer server2 = Ebean.getServer("db2");

        // default server
        createUserName("Johannes", server1); // id = 1
        createUserName("Riko", server1); // id = 2
        createUserName("Marry", server1); // id = 3
        createUserAge(15, server1); // id = 1
        createUserAge(16, server1); // id = 2
        createUserAge(17, server1); // id = 3

        // additional server
        createUserName("Chika", server2); // id = 1
        createUserName("Zura", server2); // id = 2
        createUserAge(16, server2); // id = 1
        createUserAge(15, server2); // id = 2
    }


    private static void createUserName(String name, EbeanServer server) {
        UserName n = new UserName();
        n.name = name;
        server.save(n);
    }

    private static void createUserAge(Integer name, EbeanServer server) {
        UserAge n = new UserAge();
        n.age = name;
        server.save(n);
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

    @Test
    public void additionalServer() {
        Query<Example1> q = QueryText.get("db2").getQuery("example1", Example1.class);
        List<Example1> list = q.where()
                .eq("name", "Chika")
                .findList();
        assertEquals(list.size(), 1);
        assertEquals((int) list.get(0).age, 16);
    }
}
