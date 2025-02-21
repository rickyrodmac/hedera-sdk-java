package com.hedera.hashgraph.sdk;

import com.google.common.base.MoreObjects;
import com.google.protobuf.InvalidProtocolBufferException;

import javax.annotation.Nullable;
import java.util.Objects;

public class TokenAllowance {
    @Nullable
    public final TokenId tokenId;
    @Nullable
    public final AccountId spenderAccountId;
    public final long amount;

    TokenAllowance(@Nullable TokenId tokenId, @Nullable AccountId spenderAccountId, long amount) {
        this.tokenId = tokenId;
        this.spenderAccountId = spenderAccountId;
        this.amount = amount;
    }

    static TokenAllowance fromProtobuf(com.hedera.hashgraph.sdk.proto.TokenAllowance allowanceProto) {
        return new TokenAllowance(
            allowanceProto.hasTokenId() ? TokenId.fromProtobuf(allowanceProto.getTokenId()) : null,
            allowanceProto.hasSpender() ? AccountId.fromProtobuf(allowanceProto.getSpender()) : null,
            allowanceProto.getAmount()
        );
    }

    public static TokenAllowance fromBytes(byte[] bytes) throws InvalidProtocolBufferException {
        return fromProtobuf(com.hedera.hashgraph.sdk.proto.TokenAllowance.parseFrom(Objects.requireNonNull(bytes)));
    }

    com.hedera.hashgraph.sdk.proto.TokenAllowance toProtobuf() {
        var builder = com.hedera.hashgraph.sdk.proto.TokenAllowance.newBuilder()
            .setAmount(amount);
        if (tokenId != null) {
            builder.setTokenId(tokenId.toProtobuf());
        }
        if (spenderAccountId != null) {
            builder.setSpender(spenderAccountId.toProtobuf());
        }
        return builder.build();
    }

    public byte[] toBytes() {
        return toProtobuf().toByteArray();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("tokenId", tokenId)
            .add("spenderAccountId", spenderAccountId)
            .add("amount", amount)
            .toString();
    }
}
