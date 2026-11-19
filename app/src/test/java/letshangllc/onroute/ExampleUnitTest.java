package letshangllc.onroute;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test.
 */
public class ExampleUnitTest {
    @Test
    public void direction_parcelFields_roundTrip() {
        Direction direction = new Direction(1, "Turn left", 0.5);
        assertEquals(1, direction.getRouteNum());
        assertEquals("Turn left", direction.getDirection());
        assertEquals(0.5, direction.getLength(), 0.0001);
    }
}
