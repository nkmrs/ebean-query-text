package com.github.nkmrs.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

/**
 * Load query text for Ebean.
 *
 * @author s_nakamura
 */
public class QueryText {

	public static final String QUERY_DIR = "conf/queries/";
	public static final String QUERY_EXT = ".sql";
	public static final String ENCODING = "UTF-8";

	/**
	 * SQL query cache
	 */
	private static Map<String, String> queries = loadQueries();

	private QueryText() {}

	private static Map<String, String> loadQueries() {
		Map<String, String> map = new HashMap<>();

		File rootdir = new File(QUERY_DIR);
		for (File f : rootdir.listFiles((dir, name) -> (name.endsWith(QUERY_EXT)))) {
			String name = f.getName()
					.replaceAll(QUERY_EXT + "$", "");
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
	 * get SQL query text by query file name.
	 *
	 * @param key query filename (without extension)
	 * @return SQL query text
	 */
	private static String get(String key) {
		if (!queries.containsKey(key)) {
			throw new IllegalArgumentException("Query text not found: " + key);
		}
		return queries.get(key);
	}

	/**
	 * building Query object by query file name.
	 *
	 * @param key query filename (without extension)
	 * @param clazz type of Query result
	 * @param <T> type of Query result
	 * @return SQL query text
	 */
	public static <T> Query<T> getQuery(String key, Class<T> clazz) {
		return getQuery(key, clazz, "");
	}

	/**
	 * building Query object by query file name.
	 *
	 * @param key query filename (without extension)
	 * @param clazz type of Query result
	 * @param <T> type of Query result
	 * @param orderByString Order by string
	 * @return SQL query text
	 */
	public static <T> Query<T> getQuery(String key, Class<T> clazz,
			String orderByString) {
		String sql = get(key) + " " + orderByString;
		return getQueryBySql(sql, clazz);
	}

	/**
	 * building Query object by another SQL text.
	 *
	 * @param sql SQL query text
	 * @param clazz type of Query result
	 * @param <T> type of Query result
	 * @return Query object
	 */
	public static <T> Query<T> getQueryBySql(String sql, Class<T> clazz) {
		RawSql rawSql = RawSqlBuilder.parse(sql).create();
		Query<T> query = Ebean.find(clazz).setRawSql(rawSql);
		return query;
	}
}
