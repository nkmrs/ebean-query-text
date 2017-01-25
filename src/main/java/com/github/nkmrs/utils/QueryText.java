package com.github.nkmrs.utils;

import com.avaje.ebean.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Load query text for Ebean.
 *
 * @author s_nakamura
 */
public class QueryText {

    private static final String DEFAULT_QUERY_DIR = "conf/queries/";
    private static final String DEFAULT_QUERY_EXT = ".sql";

    /**
     * SQL query cache
     */
    private final Map<String, String> queries;

    private final EbeanServer server;

    private QueryText(EbeanServer server, String queryDir, String queryExt) {
        this.server = server;
        this.queries = loadQueries(queryDir, queryExt);
    }

    /**
     * getSqlQueryText new instance by default server
     *
     * @return new instance by default server
     */
    public static QueryText getDefault() {
        return new QueryText(Ebean.getDefaultServer(), DEFAULT_QUERY_DIR, DEFAULT_QUERY_EXT);
    }

    private static Map<String, String> loadQueries(String basedir, String ext) {
        Map<String, String> map = new HashMap<>();

        File rootdir = new File(basedir);
        for (File f : rootdir.listFiles((dir, name) -> (name.endsWith(ext)))) {
            String name = f.getName()
                    .replaceAll(ext + "$", "");
            String queryText = loadText(f.toPath())
                    .replaceAll("^SELECT", "select ");
            map.put(name, queryText);
        }

        return map;
    }

    private static String loadText(Path path) {
        try {
            return Files.readAllLines(path).stream()
                    .collect(Collectors.joining(" "));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * getSqlQueryText SQL query text by query file name.
     *
     * @param key query filename (without extension)
     * @return SQL query text
     */
    private String getSqlQueryText(String key) {
        if (!queries.containsKey(key)) {
            throw new IllegalArgumentException("Query text not found: " + key);
        }
        return queries.get(key);
    }

    /**
     * building Query object by query file name.
     *
     * @param key   query filename (without extension)
     * @param clazz type of Query result
     * @param <T>   type of Query result
     * @return SQL query text
     */
    public <T> Query<T> getQuery(String key, Class<T> clazz) {
        return getQuery(key, clazz, "");
    }

    /**
     * building Query object by query file name.
     *
     * @param key           query filename (without extension)
     * @param clazz         type of Query result
     * @param <T>           type of Query result
     * @param orderByString Order by string
     * @return SQL query text
     */
    public <T> Query<T> getQuery(String key, Class<T> clazz,
                                 String orderByString) {
        String sql = getSqlQueryText(key) + " " + orderByString;
        return getQueryBySql(sql, clazz);
    }

    /**
     * building Query object by another SQL text.
     *
     * @param sql   SQL query text
     * @param clazz type of Query result
     * @param <T>   type of Query result
     * @return Query object
     */
    public <T> Query<T> getQueryBySql(String sql, Class<T> clazz) {
        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        Query<T> query = server.find(clazz).setRawSql(rawSql);
        return query;
    }
}
