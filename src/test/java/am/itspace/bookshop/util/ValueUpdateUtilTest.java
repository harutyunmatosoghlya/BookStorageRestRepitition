package am.itspace.bookshop.util;

import am.itspace.bookshop.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValueUpdateUtilTest {
    private final ValueUpdateUtil util = new ValueUpdateUtil();

    @Test
    void returnsCurrentWhenIncomingIsNull() {
        String current = "default";
        String incoming = null;
        assertEquals(current, util.getOrDefault(current, incoming));
    }

    @Test
    void returnsCurrentWhenIncomingIsEmptyString() {
        String current = "default";
        String incoming = "";
        assertEquals(current, util.getOrDefault(current, incoming));
    }

    @Test
    void returnsCurrentWhenIncomingIsWhitespaceString() {
        String current = "default";
        String incoming = "   ";
        assertEquals(current, util.getOrDefault(current, incoming));
    }

    @Test
    void returnsIncomingWhenIncomingIsNonEmptyString() {
        String current = "default";
        String incoming = "hello";
        assertEquals("hello", util.getOrDefault(current, incoming));
    }

    @Test
    void returnsIncomingWhenNonStringAndNotNull() {
        Integer current = 5;
        Integer incoming = 42;
        assertEquals(42, util.getOrDefault(current, incoming));
    }

    @Test
    void returnsCurrentWhenNonStringIncomingIsNull() {
        Integer current = 5;
        Integer incoming = null;
        assertEquals(5, util.getOrDefault(current, incoming));
    }

    @Test
    void returnsIncomingWhenIncomingIsBooleanTrue() {
        Boolean current = false;
        Boolean incoming = true;
        assertEquals(true, util.getOrDefault(current, incoming));
    }

    @Test
    void returnsCurrentWhenIncomingIsEmptyArray() {
        String[] current = {"a", "b"};
        String[] incoming = {};
        assertEquals(incoming, util.getOrDefault(current, incoming));
    }

    @Test
    void returnsCurrentWhenIncomingIsSameAsCurrent() {
        String current = "same";
        String incoming = "same";
        assertEquals(current, util.getOrDefault(current, incoming));
    }

    @Test
    void returnsIncomingWhenIncomingIsCustomObject() {
        User current = User.builder()
                .name("custom")
                .build();
        User incoming = User.builder()
                .name("new name")
                .build();
        assertEquals(incoming, util.getOrDefault(current, incoming));
    }

    @Test
    void returnsCurrentWhenIncomingIsNullCustomObject() {
        User current = User.builder()
                .name("custom")
                .build();
        User incoming = null;
        assertEquals(current, util.getOrDefault(current, incoming));
    }
}