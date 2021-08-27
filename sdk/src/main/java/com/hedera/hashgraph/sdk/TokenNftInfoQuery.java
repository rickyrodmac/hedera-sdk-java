package com.hedera.hashgraph.sdk;

import com.hedera.hashgraph.sdk.proto.Query;
import com.hedera.hashgraph.sdk.proto.QueryHeader;
import com.hedera.hashgraph.sdk.proto.Response;
import com.hedera.hashgraph.sdk.proto.ResponseHeader;
import com.hedera.hashgraph.sdk.proto.TokenGetAccountNftInfosQuery;
import com.hedera.hashgraph.sdk.proto.TokenGetNftInfoQuery;
import com.hedera.hashgraph.sdk.proto.TokenGetNftInfosQuery;
import com.hedera.hashgraph.sdk.proto.TokenServiceGrpc;
import io.grpc.MethodDescriptor;
import java8.util.concurrent.CompletableFuture;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TokenNftInfoQuery extends com.hedera.hashgraph.sdk.Query<List<TokenNftInfo>, TokenNftInfoQuery> {
    @Nullable
    private NftId nftId = null;
    @Nullable
    private TokenId tokenId = null;
    @Nullable
    private AccountId accountId = null;
    private long start = 0;
    private long end = 0;

    public TokenNftInfoQuery() {
    }

    private static List<TokenNftInfo> infosFromProtos(List<com.hedera.hashgraph.sdk.proto.TokenNftInfo> protoList) {
        var infoList = new ArrayList<TokenNftInfo>();
        for (var proto : protoList) {
            infoList.add(TokenNftInfo.fromProtobuf(proto));
        }
        return infoList;
    }

    /**
     * @deprecated use setNftId instead
     * Sets the NFT ID for which information is requested.
     *
     * @param nftId The NftId to be set
     * @return {@code this}
     */
    @Deprecated
    public TokenNftInfoQuery byNftId(NftId nftId) {
        return setNftId(nftId);
    }

    /**
     * Sets the NFT ID for which information is requested.
     *
     * @param nftId The NftId to be set
     * @return {@code this}
     */
    public TokenNftInfoQuery setNftId(NftId nftId) {
        Objects.requireNonNull(nftId);
        this.nftId = nftId;
        return this;
    }

    @Nullable
    public NftId getNftId() {
        return nftId;
    }

    /**
     * @deprecated with no replacement
     * Sets the Token ID and the index range for which information is requested.
     *
     * @param tokenId The ID of the token for which information is requested
     * @return {@code this}
     */
    @Deprecated
    public TokenNftInfoQuery byTokenId(TokenId tokenId) {
        Objects.requireNonNull(tokenId);
        this.tokenId = tokenId;
        return this;
    }

    @Nullable
    @Deprecated
    public TokenId getTokenId() {
        return tokenId;
    }

    /**
     * @deprecated with no replacement
     * Sets the Account ID for which information is requested.
     *
     * @param accountId The Account ID for which information is requested
     * @return {@code this}
     */
    @Deprecated
    public TokenNftInfoQuery byAccountId(AccountId accountId) {
        Objects.requireNonNull(accountId);
        this.accountId = accountId;
        return this;
    }

    @Nullable
    @Deprecated
    public AccountId getAccountId() {
        return accountId;
    }

    @Deprecated
    public long getStart() {
        return start;
    }

    /**
     * @deprecated with no replacement
     * Sets the start of the index range for which information is requested.
     *
     * @param start The start index (inclusive) of the range of NFTs to query for. Value must be in the range [0; ownedNFTs-1]
     * @return {@code this}
     */
    @Deprecated
    public TokenNftInfoQuery setStart(@Nonnegative long start) {
        this.start = start;
        return this;
    }

    @Deprecated
    public long getEnd() {
        return end;
    }

    /**
     * @deprecated with no replacement
     * Sets the end of the index range for which information is requested.
     *
     * @param end The end index (exclusive) of the range of NFTs to query for. Value must be in the range (start; ownedNFTs]
     * @return {@code this}
     */
    @Deprecated
    public TokenNftInfoQuery setEnd(@Nonnegative long end) {
        this.end = end;
        return this;
    }

    @Override
    void validateChecksums(Client client) throws BadEntityIdException {
        if (nftId != null) {
            nftId.tokenId.validateChecksum(client);
        }
    }

    @Override
    CompletableFuture<Void> onExecuteAsync(Client client) {
        if (nftId == null) {
            throw new IllegalStateException("TokenNftInfoQuery must be by NftId");
        }
        return super.onExecuteAsync(client);
    }

    @Override
    void onMakeRequest(com.hedera.hashgraph.sdk.proto.Query.Builder queryBuilder, QueryHeader header) {
        queryBuilder.setTokenGetNftInfo(
            TokenGetNftInfoQuery.newBuilder()
                .setNftID(nftId.toProtobuf())
                .setHeader(header)
        );
    }

    @Override
    ResponseHeader mapResponseHeader(Response response) {
        return response.getTokenGetNftInfo().getHeader();
    }

    @Override
    QueryHeader mapRequestHeader(com.hedera.hashgraph.sdk.proto.Query request) {
        return request.getTokenGetInfo().getHeader();
    }

    @Override
    List<TokenNftInfo> mapResponse(Response response, AccountId nodeId, com.hedera.hashgraph.sdk.proto.Query request) {
        return Collections.singletonList(TokenNftInfo.fromProtobuf(response.getTokenGetNftInfo().getNft()));
    }

    @Override
    MethodDescriptor<Query, Response> getMethodDescriptor() {
        return TokenServiceGrpc.getGetTokenNftInfoMethod();
    }

    @Override
    public CompletableFuture<Hbar> getCostAsync(Client client) {
        // deleted accounts return a COST_ANSWER of zero which triggers `INSUFFICIENT_TX_FEE`
        // if you set that as the query payment; 25 tinybar seems to be enough to get
        // `Token_DELETED` back instead.
        return super.getCostAsync(client).thenApply((cost) -> Hbar.fromTinybars(Math.max(cost.toTinybars(), 25)));
    }
}
