package net.badowl.imot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyEmailData {
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DECIMAL_FORMAT = new DecimalFormat("###,###.##", symbols);
    }

    private String type;
    private String area;
    private String address;
    private Long price;
    private Long size;
    private String url;
    private Integer buildYear;
    private Integer floor;
    private Integer totalFloors;

    public String getPriceFmt() {
        return Optional.ofNullable(price).map(DECIMAL_FORMAT::format).orElse(null);
    }
}
