package net.badowl.imot.jdbc;

import net.badowl.imot.Property;
import net.badowl.imot.PropertyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class JdbcPropertyRepo implements PropertyRepo {

    @Autowired
    private JdbcTemplate template;

    @Override
    public void insert(Collection<Property> properties) {
        for (Property property : properties) {
            template.update(
                    " INSERT INTO properties (" +
                            "   type, " +
                            "   area, " +
                            "   address, " +
                            "   price, " +
                            "   raw_price, " +
                            "   description, " +
                            "   size, " +
                            "   raw_size, " +
                            "   url, " +
                            "   build_type, " +
                            "   build_year," +
                            "   raw_floor," +
                            "   floor," +
                            "   total_floors, " +
                            "   seller_phone, " +
                            "   seller_name " +
                            " ) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                            " ON CONFLICT (url) DO UPDATE " +
                            "   SET date_modified = now() ",
                    property.getType(),
                    property.getArea(),
                    property.getAddress(),
                    property.getPrice(),
                    property.getRawPrice(),
                    property.getDescription(),
                    property.getSize(),
                    property.getRawSize(),
                    property.getUrl(),
                    property.getBuildType(),
                    property.getBuildYear(),
                    property.getRawFloor(),
                    property.getFloor(),
                    property.getTotalFloors(),
                    property.getSellerPhone(),
                    property.getSellerName());
        }
    }
}
