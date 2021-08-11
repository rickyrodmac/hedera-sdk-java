package com.hedera.hashgraph.sdk;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.proto.FreezeTransactionBody;
import com.hedera.hashgraph.sdk.proto.TransactionBody;
import com.hedera.hashgraph.sdk.proto.SchedulableTransactionBody;
import com.hedera.hashgraph.sdk.proto.FreezeServiceGrpc;
import com.hedera.hashgraph.sdk.proto.TransactionResponse;
import io.grpc.MethodDescriptor;

import java.time.Instant;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import java.util.LinkedHashMap;

/**
 * Set the freezing period in which the platform will stop creating events and accepting transactions.
 * This is used before safely shut down the platform for maintenance.
 */
public final class FreezeTransaction extends Transaction<FreezeTransaction> {
    int startHour = 0;
    int startMinute = 0;
    int endHour = 0;
    int endMinute = 0;

    public FreezeTransaction() {
    }

    FreezeTransaction(LinkedHashMap<TransactionId, LinkedHashMap<AccountId, com.hedera.hashgraph.sdk.proto.Transaction>> txs) throws InvalidProtocolBufferException {
        super(txs);
        initFromTransactionBody();
    }

    FreezeTransaction(com.hedera.hashgraph.sdk.proto.TransactionBody txBody) {
        super(txBody);
        initFromTransactionBody();
    }

    @SuppressWarnings("FromTemporalAccessor")
    public Instant getStartTime() {
        return Instant.from(OffsetTime.of(startHour, startMinute, 0, 0, ZoneOffset.UTC));
    }

    /**
     * Sets the start time (in UTC).
     *
     * @param hour   The hour to be set
     * @param minute The minute to be set
     * @return {@code this}
     */
    public FreezeTransaction setStartTime(int hour, int minute) {
        requireNotFrozen();

        startHour = hour;
        startMinute = minute;

        return this;
    }

    @SuppressWarnings("FromTemporalAccessor")
    public Instant getEndTime() {
        return Instant.from(OffsetTime.of(endHour, endMinute, 0, 0, ZoneOffset.UTC));
    }

    /**
     * Sets the end time (in UTC).
     *
     * @param hour   The hour to be set
     * @param minute The minute to be set
     * @return {@code this}
     */
    public FreezeTransaction setEndTime(int hour, int minute) {
        requireNotFrozen();

        endHour = hour;
        endMinute = minute;

        return this;
    }

    @Override
    void validateChecksums(Client client) throws BadEntityIdException {
    }


    @Override
    MethodDescriptor<com.hedera.hashgraph.sdk.proto.Transaction, TransactionResponse> getMethodDescriptor() {
        return FreezeServiceGrpc.getFreezeMethod();
    }

    void initFromTransactionBody() {
        var body = sourceTransactionBody.getFreeze();
        startHour = body.getStartHour();
        startMinute = body.getStartMin();
        endHour = body.getEndHour();
        endMinute = body.getEndMin();
    }

    FreezeTransactionBody.Builder build() {
        var builder = FreezeTransactionBody.newBuilder();
        builder.setStartHour(startHour);
        builder.setStartMin(startMinute);
        builder.setEndHour(endHour);
        builder.setEndMin(endMinute);
        return builder;
    }

    @Override
    void onFreeze(TransactionBody.Builder bodyBuilder) {
        bodyBuilder.setFreeze(build());
    }

    @Override
    void onScheduled(SchedulableTransactionBody.Builder scheduled) {
        scheduled.setFreeze(build());
    }
}
