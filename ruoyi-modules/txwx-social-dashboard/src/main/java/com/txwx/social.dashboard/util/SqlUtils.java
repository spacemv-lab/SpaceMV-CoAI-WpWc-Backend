package com.txwx.social.dashboard.util;

public class SqlUtils {

    public static String deleteSqlWithDate(String table) {
        return "DELETE FROM " + table +
                " WHERE account_id = ?" +
                "  AND ref_date BETWEEN ? AND ?";
    }

    public static String deleteSql (String table) {
        return "DELETE FROM "+ table+" WHERE account_id = ?";
    }
}
