package com.txwx.social.dashboard.util;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 时间校验工具类
 */
public class DateValidator {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);


    /**
     * 解析并验证日期字符串
     */
    public static LocalDate parseAndValidateDate(String dateStr, String fieldName) {
        if (StringUtils.isBlank(dateStr)) {
            throw new ServiceException(fieldName + "不能为空");
        }

        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw new ServiceException(fieldName + "格式错误，请使用 yyyy-MM-dd 格式");
        }
    }


    /**
     * 校验时间范围
     * 要求：
     * 1. 开始时间不能晚于结束时间
     * 2. 结束时间不能晚于当前日期的前一天
     *
     * @param startdate 开始日期，格式 yyyy-MM-dd
     * @param enddate 结束日期，格式 yyyy-MM-dd
     * @return 校验结果对象
     */
    public static DateValidationResult validateDateRange(String startdate, String enddate) {
        try {
            // 1. 验证参数非空
            if (startdate == null || startdate.trim().isEmpty()) {
                return DateValidationResult.error("开始日期不能为空");
            }

            if (enddate == null || enddate.trim().isEmpty()) {
                return DateValidationResult.error("结束日期不能为空");
            }

            // 2. 解析日期
            LocalDate start = LocalDate.parse(startdate.trim(), DATE_FORMATTER);
            LocalDate end = LocalDate.parse(enddate.trim(), DATE_FORMATTER);

            // 3. 计算当前日期和前一天
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            // 4. 校验规则
            if (start.isAfter(end)) {
                return DateValidationResult.error(
                        String.format("开始日期不能晚于结束日期: 开始日期=%s, 结束日期=%s", startdate, enddate)
                );
            }

            if (end.isAfter(yesterday)) {
                String yesterdayStr = yesterday.format(DATE_FORMATTER);
                return DateValidationResult.error(
                        String.format("结束日期不能晚于当前日期的前一天: 结束日期=%s, 可允许的最大结束日期=%s",
                                enddate, yesterdayStr)
                );
            }

            // 5. 返回成功结果
            return DateValidationResult.success(start, end, yesterday);

        } catch (DateTimeParseException e) {
            return DateValidationResult.error(
                    String.format("日期格式错误，请使用 %s 格式: 开始日期=%s, 结束日期=%s",
                            DATE_PATTERN, startdate, enddate)
            );
        } catch (Exception e) {
            return DateValidationResult.error("日期校验异常: " + e.getMessage());
        }
    }

    /**
     * 校验结果类
     */
    public static class DateValidationResult {
        private boolean valid;
        private String errorMessage;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate maxAllowedEndDate; // 可允许的最大结束日期（当前日期的前一天）

        private DateValidationResult(boolean valid, String errorMessage,
                                     LocalDate startDate, LocalDate endDate,
                                     LocalDate maxAllowedEndDate) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.startDate = startDate;
            this.endDate = endDate;
            this.maxAllowedEndDate = maxAllowedEndDate;
        }

        public static DateValidationResult success(LocalDate startDate, LocalDate endDate,
                                                   LocalDate maxAllowedEndDate) {
            return new DateValidationResult(true, null, startDate, endDate, maxAllowedEndDate);
        }

        public static DateValidationResult error(String errorMessage) {
            return new DateValidationResult(false, errorMessage, null, null, null);
        }

        // Getters
        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public LocalDate getMaxAllowedEndDate() { return maxAllowedEndDate; }

        /**
         * 如果校验失败，抛出异常
         */
        public void throwIfInvalid() {
            if (!valid) {
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    /**
     * 简易校验方法，直接抛出异常
     */
    public static void validate(String startdate, String enddate) {
        DateValidationResult result = validateDateRange(startdate, enddate);
        result.throwIfInvalid();
    }
}
