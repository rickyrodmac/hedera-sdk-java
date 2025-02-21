package com.hedera.hashgraph.sdk;

public enum FreezeType {
    /**
     * An (invalid) default value for this enum, to ensure the client explicitly sets
     * the intended type of freeze transaction.
     */
    UNKNOWN_FREEZE_TYPE(com.hedera.hashgraph.sdk.proto.FreezeType.UNKNOWN_FREEZE_TYPE),

    /**
     * Freezes the network at the specified time. The start_time field must be provided and
     * must reference a future time. Any values specified for the update_file and file_hash
     * fields will be ignored. This transaction does not perform any network changes or
     * upgrades and requires manual intervention to restart the network.
     */
    FREEZE_ONLY(com.hedera.hashgraph.sdk.proto.FreezeType.FREEZE_ONLY),

    /**
     * A non-freezing operation that initiates network wide preparation in advance of a
     * scheduled freeze upgrade. The update_file and file_hash fields must be provided and
     * valid. The start_time field may be omitted and any value present will be ignored.
     */
    PREPARE_UPGRADE(com.hedera.hashgraph.sdk.proto.FreezeType.PREPARE_UPGRADE),

    /**
     * Freezes the network at the specified time and performs the previously prepared
     * automatic upgrade across the entire network.
     */
    FREEZE_UPGRADE(com.hedera.hashgraph.sdk.proto.FreezeType.FREEZE_UPGRADE),

    /**
     * Aborts a pending network freeze operation.
     */
    FREEZE_ABORT(com.hedera.hashgraph.sdk.proto.FreezeType.FREEZE_ABORT),

    /**
     * Performs an immediate upgrade on auxilary services and containers providing
     * telemetry/metrics. Does not impact network operations.
     */
    TELEMETRY_UPGRADE(com.hedera.hashgraph.sdk.proto.FreezeType.TELEMETRY_UPGRADE);

    final com.hedera.hashgraph.sdk.proto.FreezeType code;

    FreezeType(com.hedera.hashgraph.sdk.proto.FreezeType code) {
        this.code = code;
    }

    static FreezeType valueOf(com.hedera.hashgraph.sdk.proto.FreezeType code) {
        switch (code) {
            case UNKNOWN_FREEZE_TYPE:
                return UNKNOWN_FREEZE_TYPE;
            case FREEZE_ONLY:
                return FREEZE_ONLY;
            case PREPARE_UPGRADE:
                return PREPARE_UPGRADE;
            case FREEZE_UPGRADE:
                return FREEZE_UPGRADE;
            case FREEZE_ABORT:
                return FREEZE_ABORT;
            case TELEMETRY_UPGRADE:
                return TELEMETRY_UPGRADE;
            case UNRECOGNIZED:
                // NOTE: Protobuf deserialization will not give us the code on the wire
                throw new IllegalArgumentException(
                    "network returned unrecognized response code; your SDK may be out of date");
        }

        // NOTE: This should be unreachable as error prone has enum exhaustiveness checking
        throw new IllegalArgumentException(
            "response code "
                + code.name()
                + " is unhandled by the SDK; update your SDK or open an issue");
    }

    @Override
    public String toString() {
        return code.name();
    }
}
