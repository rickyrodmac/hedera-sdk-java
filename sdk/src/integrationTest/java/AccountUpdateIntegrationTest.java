import com.google.errorprone.annotations.Var;
import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.AccountInfoQuery;
import com.hedera.hashgraph.sdk.AccountUpdateTransaction;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import com.hedera.hashgraph.sdk.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.threeten.bp.Duration;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountUpdateIntegrationTest {
    @Test
    @DisplayName("Can update account with a new key")
    void canUpdateAccountWithNewKey() throws Exception {
        var testEnv = new IntegrationTestEnv(1);

        var key1 = PrivateKey.generateED25519();
        var key2 = PrivateKey.generateED25519();

        var response = new AccountCreateTransaction()
            .setKey(key1)
            .execute(testEnv.client);

        var accountId = Objects.requireNonNull(response.getReceipt(testEnv.client).accountId);

        @Var var info = new AccountInfoQuery()
            .setAccountId(accountId)
            .execute(testEnv.client);

        assertEquals(accountId, info.accountId);
        assertFalse(info.isDeleted);
        assertEquals(key1.getPublicKey().toString(), info.key.toString());
        assertEquals(new Hbar(0), info.balance);
        assertEquals(Duration.ofDays(90), info.autoRenewPeriod);
        assertNull(info.proxyAccountId);
        assertEquals(Hbar.ZERO, info.proxyReceived);

        new AccountUpdateTransaction()
            .setAccountId(accountId)
            .setKey(key2.getPublicKey())
            .freezeWith(testEnv.client)
            .sign(key1)
            .sign(key2)
            .execute(testEnv.client)
            .getReceipt(testEnv.client);

        info = new AccountInfoQuery()
            .setAccountId(accountId)
            .execute(testEnv.client);

        assertEquals(info.accountId, accountId);
        assertFalse(info.isDeleted);
        assertEquals(info.key.toString(), key2.getPublicKey().toString());
        assertEquals(info.balance, new Hbar(0));
        assertEquals(info.autoRenewPeriod, Duration.ofDays(90));
        assertNull(info.proxyAccountId);
        assertEquals(info.proxyReceived, Hbar.ZERO);

        testEnv.close(accountId, key2);
    }

    @Test
    @DisplayName("Cannot update account when account ID is not set")
    void cannotUpdateAccountWhenAccountIdIsNotSet() throws Exception {
        var testEnv = new IntegrationTestEnv(1);

        var error = assertThrows(ReceiptStatusException.class, () -> {
            new AccountUpdateTransaction()
                .execute(testEnv.client)
                .getReceipt(testEnv.client);
        });

        assertTrue(error.getMessage().contains(Status.INVALID_ACCOUNT_ID.toString()));

        testEnv.close();
    }
}
