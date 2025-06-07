package com.tty.lib.tool;
import java.text.DecimalFormat;

public class FormatUtils {

    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT =
            ThreadLocal.withInitial(() -> new DecimalFormat("#.00"));

    private static final String ID_NAME_REGEX = "^[a-zA-Z0-9_]+$";
    private static final String NAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
    private static final String PERMISSION_NODE_REGEX = "^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$";

    /**
     * 格式化数字保留两位小数
     * @param number 需要格式化的数字（支持所有Number子类）
     * @return 格式化后的字符串，输入为null时返回"0.00"
     */
    public static String formatTwoDecimalPlaces(Number number) {
        if (number == null) return "0.00";
        return DECIMAL_FORMAT.get().format(number);
    }

    /**
     * 检查ID名称合法性（字母数字下划线）
     * @param content 待检查字符串
     * @return 空值或不符合格式返回false
     */
    public static boolean checkIdName(String content) {
        return content != null && content.matches(ID_NAME_REGEX);
    }

    /**
     * 检查名称合法性（支持中文字符）
     * @param content 待检查字符串
     * @return 空值或不符合格式返回false
     */
    public static boolean checkName(String content) {
        return content != null && content.matches(NAME_REGEX);
    }

    /**
     * 验证Minecraft权限节点格式
     * @param node 权限节点字符串
     * @return 空值或不符合格式返回false
     */
    public static boolean isValidPermissionNode(String node) {
        return node != null && node.matches(PERMISSION_NODE_REGEX);
    }
}
