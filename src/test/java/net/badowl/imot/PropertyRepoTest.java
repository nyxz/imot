package net.badowl.imot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyRepoTest extends SpringTest {

    @Autowired
    private PropertyRepo propertyRepo;

    @Test
    void insertBaseCase() {
        final String description = "Много слънчев, нов, готов за нанасяне. Строен е 1932г. лятото.";
        final String buildType = "тухла";
        final String rawPrice = "40000 EUR";
        final Long price = PropertyUtil.toPrice(rawPrice);
        final String rawFloor = "5ти от 10";
        final String url = "https://example.com";
        final String type = "3 Стаен";
        final String area = "Борово";
        final String address = "ул. Дойран";
        final String rawSize = "130 кв.м.";
        final Long size = PropertyUtil.toSize(rawSize);
        final String sellerPhone = "0888 999 212";
        final String sellerName = "Пешо Пешев";
        final String providerWebsite = "imot.bg";
        final Property property = Property.builder()
                .url(url)
                .type(type)
                .area(area)
                .address(address)
                .rawPrice(rawPrice)
                .price(price)
                .description(description)
                .size(size)
                .buildType(buildType)
                .buildYear(PropertyUtil.toBuildYear(buildType, description))
                .rawFloor(rawFloor)
                .floor(PropertyUtil.toFloor(rawFloor))
                .totalFloors(PropertyUtil.toTotalFloors(rawFloor))
                .sellerPhone(sellerPhone)
                .sellerName(sellerName)
                .providerWebsite(providerWebsite)
                .build();
        propertyRepo.insert(Collections.singletonList(property));
        final List<PropertyEmailData> allToDisplay = propertyRepo.findAllToDisplay();
        assertEquals(1, allToDisplay.size());

        final PropertyEmailData actual = allToDisplay.get(0);
        assertEquals(url, actual.getUrl());
        assertEquals(type, actual.getType());
        assertEquals(area, actual.getArea());
        assertEquals(address, actual.getAddress());
        assertEquals(price, actual.getPrice());
        assertEquals(size, actual.getSize());
        assertEquals(1932, actual.getBuildYear().intValue());
        assertEquals(type, actual.getType());
        assertEquals(5, actual.getFloor().intValue());
        assertEquals(10, actual.getTotalFloors().intValue());
    }
}
