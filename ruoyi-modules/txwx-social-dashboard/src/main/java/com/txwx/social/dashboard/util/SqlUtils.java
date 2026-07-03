/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

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
