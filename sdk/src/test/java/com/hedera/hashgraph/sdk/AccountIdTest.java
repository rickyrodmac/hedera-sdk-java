package com.hedera.hashgraph.sdk;

import com.google.protobuf.InvalidProtocolBufferException;
import io.github.jsonSnapshot.SnapshotMatcher;
import org.bouncycastle.util.encoders.Hex;
import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountIdTest {

    static Client mainnetClient;
    static Client testnetClient;
    static Client previewnetClient;

    @BeforeAll
    public static void beforeAll() {
        SnapshotMatcher.start();
        mainnetClient = Client.forMainnet();
        testnetClient = Client.forTestnet();
        previewnetClient = Client.forPreviewnet();
    }

    @AfterClass
    public static void afterAll() throws TimeoutException {
        mainnetClient.close();
        testnetClient.close();
        previewnetClient.close();
        SnapshotMatcher.validateSnapshots();
    }

    @Test
    void fromString() {
        SnapshotMatcher.expect(AccountId.fromString("0.0.5005").toString()).toMatchSnapshot();
    }

    @Test
    void fromStringWithChecksumOnMainnet() {
        SnapshotMatcher.expect(AccountId.fromString("0.0.123-vfmkw").toStringWithChecksum(mainnetClient)).toMatchSnapshot();
    }

    @Test
    void fromStringWithChecksumOnTestnet() {
        SnapshotMatcher.expect(AccountId.fromString("0.0.123-rmkyk").toStringWithChecksum(testnetClient)).toMatchSnapshot();
    }

    @Test
    void fromStringWithChecksumOnPreviewnet() {
        SnapshotMatcher.expect(AccountId.fromString("0.0.123-ntjly").toStringWithChecksum(previewnetClient)).toMatchSnapshot();
    }

    @Test
    void goodChecksumOnMainnet() throws BadEntityIdException {
        AccountId.fromString("0.0.123-vfmkw").validateChecksum(mainnetClient);
    }

    @Test
    void goodChecksumOnTestnet() throws BadEntityIdException {
        AccountId.fromString("0.0.123-rmkyk").validateChecksum(testnetClient);
    }

    @Test
    void goodChecksumOnPreviewnet() throws BadEntityIdException {
        AccountId.fromString("0.0.123-ntjly").validateChecksum(previewnetClient);
    }

    @Test
    void badChecksumOnPreviewnet() {
        assertThrows(BadEntityIdException.class, () -> {
            AccountId.fromString("0.0.123-ntjli").validateChecksum(previewnetClient);
        });
    }

    @Test
    void malformedIdString() {
        assertThrows(IllegalArgumentException.class, () -> {
            AccountId.fromString("0.0.");
        });
    }

    @Test
    void malformedIdChecksum() {
        assertThrows(IllegalArgumentException.class, () -> {
            AccountId.fromString("0.0.123-ntjl");
        });
    }

    @Test
    void malformedIdChecksum2() {
        assertThrows(IllegalArgumentException.class, () -> {
            AccountId.fromString("0.0.123-ntjl1");
        });
    }

    @Test
    void malformedAliasKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            AccountId.fromString("0.0.302a300506032b6570032100114e6abc371b82dab5c15ea149f02d34a012087b163516dd70f44acafabf777");
        });
    }

    @Test
    void malformedAliasKey2() {
        assertThrows(IllegalArgumentException.class, () -> {
            AccountId.fromString("0.0.302a300506032b6570032100114e6abc371b82dab5c15ea149f02d34a012087b163516dd70f44acafabf777g");
        });
    }

    @Test
    void malformedAliasKey3() {
        assertThrows(IllegalArgumentException.class, () -> {
            AccountId.fromString("0.0.303a300506032b6570032100114e6abc371b82dab5c15ea149f02d34a012087b163516dd70f44acafabf7777");
        });
    }

    @Test
    void fromStringWithAliasKey() {
        SnapshotMatcher.expect(AccountId.fromString("0.0.302a300506032b6570032100114e6abc371b82dab5c15ea149f02d34a012087b163516dd70f44acafabf7777")).toMatchSnapshot();
    }

    @Test
    void fromSolidityAddress() {
        SnapshotMatcher.expect(AccountId.fromSolidityAddress("000000000000000000000000000000000000138D").toString()).toMatchSnapshot();
    }

    @Test
    void fromSolidityAddressWith0x() {
        SnapshotMatcher.expect(AccountId.fromSolidityAddress("0x000000000000000000000000000000000000138D").toString()).toMatchSnapshot();
    }

    @Test
    void toBytes() throws InvalidProtocolBufferException {
        SnapshotMatcher.expect(Hex.toHexString(new AccountId(5005).toProtobuf().toByteArray())).toMatchSnapshot();
    }

    @Test
    void toBytesAlias() throws InvalidProtocolBufferException {
        SnapshotMatcher.expect(Hex.toHexString(AccountId.fromString("0.0.302a300506032b6570032100114e6abc371b82dab5c15ea149f02d34a012087b163516dd70f44acafabf7777").toBytes())).toMatchSnapshot();
    }

    @Test
    void fromBytes() throws InvalidProtocolBufferException {
        SnapshotMatcher.expect(AccountId.fromBytes(new AccountId(5005).toBytes()).toString()).toMatchSnapshot();
    }

    @Test
    void fromBytesAlias() throws InvalidProtocolBufferException {
        SnapshotMatcher.expect(AccountId.fromBytes(AccountId.fromString("0.0.302a300506032b6570032100114e6abc371b82dab5c15ea149f02d34a012087b163516dd70f44acafabf7777").toBytes()).toString()).toMatchSnapshot();
    }

    @Test
    void toSolidityAddress() {
        SnapshotMatcher.expect(new AccountId(5005).toSolidityAddress()).toMatchSnapshot();
    }
}
