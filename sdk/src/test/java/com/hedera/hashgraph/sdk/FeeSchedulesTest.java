package com.hedera.hashgraph.sdk;

import io.github.jsonSnapshot.SnapshotMatcher;
import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.threeten.bp.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeeSchedulesTest {
    @BeforeAll
    public static void beforeAll() {
        SnapshotMatcher.start();
    }

    @AfterClass
    public static void afterAll() {
        SnapshotMatcher.validateSnapshots();
    }

    FeeSchedules spawnFeeSchedulesExample()
    {
        return new FeeSchedules()
            .setCurrent(new FeeSchedule()
                .setExpirationTime(Instant.ofEpochSecond(1554158542))
                .setTransactionFeeSchedules(Collections.singletonList(new TransactionFeeSchedule()
                    .setFeeData(new FeeData()
                        .setNodeData(new FeeComponents())
                        .setNetworkData(new FeeComponents()
                            .setMin(2)
                            .setMax(5))
                        .setServiceData(new FeeComponents())))))
            .setNext(new FeeSchedule()
                .setExpirationTime(Instant.ofEpochSecond(1554158222))
                .setTransactionFeeSchedules(Collections.singletonList(new TransactionFeeSchedule()
                    .setFeeData(new FeeData()
                        .setNodeData(new FeeComponents()
                            .setMin(1)
                            .setMax(2))
                        .setNetworkData(new FeeComponents())
                        .setServiceData(new FeeComponents())))));
    }

    @Test
    void shouldSerialize() {
        assertDoesNotThrow(() -> {
            var originalFeeSchedules = spawnFeeSchedulesExample();
            byte[] feeSchedulesBytes = originalFeeSchedules.toBytes();
            var copyFeeSchedules = FeeSchedules.fromBytes(feeSchedulesBytes);
            assertTrue(originalFeeSchedules.toString().equals(copyFeeSchedules.toString()));
            SnapshotMatcher.expect(originalFeeSchedules.toString()).toMatchSnapshot();
        });
    }

    @Test
    void shouldSerializeNull() {
        assertDoesNotThrow(() -> {
            var originalFeeSchedules = new FeeSchedules();
            byte[] feeSchedulesBytes = originalFeeSchedules.toBytes();
            var copyFeeSchedules = FeeSchedules.fromBytes(feeSchedulesBytes);
            assertTrue(originalFeeSchedules.toString().equals(copyFeeSchedules.toString()));
        });
    }
}