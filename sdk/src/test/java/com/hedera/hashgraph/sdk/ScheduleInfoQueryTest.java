package com.hedera.hashgraph.sdk;

import io.github.jsonSnapshot.SnapshotMatcher;
import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ScheduleInfoQueryTest {
    @BeforeAll
    public static void beforeAll() {
        SnapshotMatcher.start();
    }

    @AfterClass
    public static void afterAll() {
        SnapshotMatcher.validateSnapshots();
    }

    @Test
    void shouldSerialize() {
        SnapshotMatcher.expect(new ScheduleInfoQuery()
            .setScheduleId(ScheduleId.fromString("0.0.5005"))
            .setMaxQueryPayment(Hbar.fromTinybars(100_000))
            .toString()
        ).toMatchSnapshot();
    }
}
