package net.badowl.imot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyEmailData {
    private String type;
    private String area;
    private String address;
    private Long price;
    private Long size;
    private String url;
    private Integer buildYear;
    private Integer floor;
    private Integer totalFloors;
}
