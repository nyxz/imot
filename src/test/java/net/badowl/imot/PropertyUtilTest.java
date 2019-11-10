package net.badowl.imot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Property utilities tests")
class PropertyUtilTest {
    private static final String FLOORS_FULL_SMALL = "2ри от 4";
    private static final String FLOORS_FULL_BIG = "31ви от 45";
    private static final String FLOORS_PARTIAL = "11ти";
    private static final String FLOORS_MISSING = " - ";

    @Test
    void toFloor_withSmallFloorNumbers() {
        final Integer floor = PropertyUtil.toFloor(FLOORS_FULL_SMALL);
        assertNotNull(floor);
        assertEquals(2, floor.intValue());
    }

    @Test
    void toFloor_withBigFloorNumbers() {
        final Integer floor = PropertyUtil.toFloor(FLOORS_FULL_BIG);
        assertNotNull(floor);
        assertEquals(31, floor.intValue());
    }

    @Test
    void toTotalFloors_withSmallNumbers() {
        final Integer floor = PropertyUtil.toTotalFloors(FLOORS_FULL_SMALL);
        assertNotNull(floor);
        assertEquals(4, floor.intValue());
    }

    @Test
    void toTotalFloors_withBigNumbers() {
        final Integer floor = PropertyUtil.toTotalFloors(FLOORS_FULL_BIG);
        assertNotNull(floor);
        assertEquals(45, floor.intValue());
    }

    @Test
    void toFloor_withPartialTextShouldHaveValue() {
        final Integer floor = PropertyUtil.toFloor(FLOORS_PARTIAL);
        assertNotNull(floor);
        assertEquals(11, floor.intValue());
    }

    @Test
    void toFloor_withMissingTextShouldBeNull() {
        assertNull(PropertyUtil.toFloor(FLOORS_MISSING));
    }

    @Test
    void toTotalFloors_withPartialTextShouldBeNull() {
        assertNull(PropertyUtil.toTotalFloors(FLOORS_PARTIAL));
    }

    @Test
    void toTotalFloors_withMissingTextShouldBeNull() {
        assertNull(PropertyUtil.toTotalFloors(FLOORS_MISSING));
    }
}