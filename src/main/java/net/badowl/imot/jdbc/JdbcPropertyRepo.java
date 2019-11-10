package net.badowl.imot.jdbc;

import net.badowl.imot.Property;
import net.badowl.imot.PropertyEmailData;
import net.badowl.imot.PropertyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class JdbcPropertyRepo implements PropertyRepo {

    @Value("${scraper.notification.sql.property.filter}")
    private String filter;

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
                            "   seller_name, " +
                            "   provider_website " +
                            " ) " +
                            " VALUES ( " +
                            "   :type, " +
                            "   :area, " +
                            "   :address, " +
                            "   :price, " +
                            "   :rawPrice, " +
                            "   :description, " +
                            "   :size, " +
                            "   :rawSize, " +
                            "   :url, " +
                            "   :buildType, " +
                            "   :buildYear," +
                            "   :rawFloor," +
                            "   :floor," +
                            "   :totalFloors, " +
                            "   :sellerPhone, " +
                            "   :sellerName, " +
                            "   :providerWebsite " +
                            " ) " +
                            " ON CONFLICT (url) DO UPDATE " +
                            "   SET date_modified = now() ",
                    new BeanPropertyRowMapper<>(Property.class), property);
        }
    }

    @Override
    public List<PropertyEmailData> findAllToDisplay() {
        final String sql = "SELECT * FROM properties WHERE "
                + filter
                + " ORDER BY area ";
        return template.query(sql, new BeanPropertyRowMapper<>(PropertyEmailData.class));
    }
}
