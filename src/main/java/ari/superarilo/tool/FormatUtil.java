package ari.superarilo.tool;
import java.text.DecimalFormat;
public class FormatUtil {
    private final DecimalFormat decimalFormat = new DecimalFormat("#.00");
    private static final String idNameRegex = "^[a-zA-Z0-9_]+$";
    private static final String nameRegex = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    public String format_2(Double ins) {
        return this.decimalFormat.format(ins);
    }
    public String format_2(Integer ins) {
        return this.decimalFormat.format(ins);
    }
    public boolean checkIdName(String content) {
        return content.matches(idNameRegex);
    }
    public boolean checkName(String content) {
        return content.matches(nameRegex);
    }
}
