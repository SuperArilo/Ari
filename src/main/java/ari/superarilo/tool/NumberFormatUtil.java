package ari.superarilo.tool;
import java.text.DecimalFormat;
public class NumberFormatUtil {
    private final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    public String format_2(Double ins) {
        return this.decimalFormat.format(ins);
    }
    public String format_2(Integer ins) {
        return this.decimalFormat.format(ins);
    }
}
