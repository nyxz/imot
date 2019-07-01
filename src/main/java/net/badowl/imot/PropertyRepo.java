package net.badowl.imot;

import java.util.Collection;
import java.util.List;

public interface PropertyRepo {
    void insert(Collection<Property> properties);

    List<PropertyEmailData> findAllToDisplay();
}
