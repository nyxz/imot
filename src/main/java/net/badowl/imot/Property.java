package net.badowl.imot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Property {
    private UUID id;
    private String type;
    private String area;
    private String address;
    private Long price;
    private String rawPrice;
    private String description;
    private Long size;
    private String rawSize;
    private String url;
    private String buildType;
    private Integer buildYear;
    private String rawFloor;
    private Integer floor;
    private Integer totalFloors;
    private String sellerPhone;
    private String sellerName;
    private String providerWebsite;
    private Date dateCreated;
    private Date dateModified;
}
