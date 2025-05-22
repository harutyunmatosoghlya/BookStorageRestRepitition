package am.itspace.bookshop.util;

import org.springframework.stereotype.Component;

@Component
public class ValueUpdateUtil {
    public  <T> T getOrDefault(T current, T incoming) {
        if (incoming == null) return current;
        if (incoming instanceof String && ((String) incoming).trim().isEmpty()) return current;
        return incoming;
    }
}