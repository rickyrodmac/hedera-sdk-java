package com.hedera.hashgraph.sdk;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.proto.ConsensusMessageChunkInfo;
import com.hedera.hashgraph.sdk.proto.ConsensusServiceGrpc;
import com.hedera.hashgraph.sdk.proto.ConsensusSubmitMessageTransactionBody;
import com.hedera.hashgraph.sdk.proto.SchedulableTransactionBody;
import com.hedera.hashgraph.sdk.proto.TransactionBody;
import com.hedera.hashgraph.sdk.proto.TransactionID;
import com.hedera.hashgraph.sdk.proto.TransactionResponse;
import io.grpc.MethodDescriptor;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Submit a message for consensus.
 * <p>
 * Valid and authorized messages on valid topics will be ordered by the consensus service, gossipped to the
 * mirror net, and published (in order) to all subscribers (from the mirror net) on this topic.
 * <p>
 * The submitKey (if any) must sign this transaction.
 * <p>
 * On success, the resulting TransactionReceipt contains the topic's updated topicSequenceNumber and
 * topicRunningHash.
 */
public final class TopicMessageSubmitTransaction extends ChunkedTransaction<TopicMessageSubmitTransaction> {
    @Nullable
    private TopicId topicId = null;

    public TopicMessageSubmitTransaction() {
    }

    TopicMessageSubmitTransaction(LinkedHashMap<TransactionId, LinkedHashMap<AccountId, com.hedera.hashgraph.sdk.proto.Transaction>> txs) throws InvalidProtocolBufferException {
        super(txs);
        initFromTransactionBody();
    }

    TopicMessageSubmitTransaction(com.hedera.hashgraph.sdk.proto.TransactionBody txBody) {
        super(txBody);
        initFromTransactionBody();
    }

    @Nullable
    public TopicId getTopicId() {
        return topicId;
    }

    public TopicMessageSubmitTransaction setTopicId(TopicId topicId) {
        Objects.requireNonNull(topicId);
        requireNotFrozen();
        this.topicId = topicId;
        return this;
    }

    public ByteString getMessage() {
        return getData();
    }

    public TopicMessageSubmitTransaction setMessage(ByteString message) {
        return setData(message);
    }

    public TopicMessageSubmitTransaction setMessage(byte[] message) {
        return setData(message);
    }

    public TopicMessageSubmitTransaction setMessage(String message) {
        return setData(message);
    }

    void initFromTransactionBody() {
        var body = sourceTransactionBody.getConsensusSubmitMessage();
        if (body.hasTopicID()) {
            topicId = TopicId.fromProtobuf(body.getTopicID());
        }

        try {
            for (var i = 0; i < innerSignedTransactions.size(); i += nodeAccountIds.isEmpty() ? 1 : nodeAccountIds.size()) {
                data = data.concat(
                    TransactionBody.parseFrom(innerSignedTransactions.get(i).getBodyBytes())
                        .getConsensusSubmitMessage().getMessage()
                );
            }
        } catch (InvalidProtocolBufferException exc) {
            throw new IllegalArgumentException(exc.getMessage());
        }
    }

    ConsensusSubmitMessageTransactionBody.Builder build() {
        var builder = ConsensusSubmitMessageTransactionBody.newBuilder();
        if (topicId != null) {
            builder.setTopicID(topicId.toProtobuf());
        }
        builder.setMessage(data);

        return builder;
    }

    @Override
    void validateChecksums(Client client) throws BadEntityIdException {
        if (topicId != null) {
            topicId.validateChecksum(client);
        }
    }

    @Override
    MethodDescriptor<com.hedera.hashgraph.sdk.proto.Transaction, TransactionResponse> getMethodDescriptor() {
        return ConsensusServiceGrpc.getSubmitMessageMethod();
    }

    @Override
    void onFreeze(TransactionBody.Builder bodyBuilder) {
        bodyBuilder.setConsensusSubmitMessage(build());
    }

    @Override
    void onFreezeChunk(TransactionBody.Builder body, @Nullable TransactionID initialTransactionId, int startIndex, int endIndex, int chunk, int total) {
        if (total == 1) {
            body.setConsensusSubmitMessage(build().setMessage(data.substring(startIndex, endIndex)));
        } else {
            body.setConsensusSubmitMessage(build().setMessage(data.substring(startIndex, endIndex))
                .setChunkInfo(ConsensusMessageChunkInfo.newBuilder()
                    .setInitialTransactionID(Objects.requireNonNull(initialTransactionId))
                    .setNumber(chunk + 1)
                    .setTotal(total)
                )
            );
        }

    }

    @Override
    void onScheduled(SchedulableTransactionBody.Builder scheduled) {
        scheduled.setConsensusSubmitMessage(build().setMessage(data));
    }
}
