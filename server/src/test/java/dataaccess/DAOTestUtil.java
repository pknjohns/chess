package dataaccess;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DAOTestUtil {
    public static <T> void assertCollectionEqual(Collection<T> expected, Collection<T> actual) {
        assertEquals(expected.size(), actual.size(), "Expected and Actual are not the same length");
        assertEquals(new HashSet<>(expected), new HashSet<>(actual), "Expected and Actual elements are not the same");
    }
}
