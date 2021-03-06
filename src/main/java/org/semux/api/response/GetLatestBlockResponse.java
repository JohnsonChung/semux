/**
 * Copyright (c) 2017 The Semux Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.semux.api.response;

import org.semux.api.ApiHandlerResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetLatestBlockResponse extends ApiHandlerResponse {

    @JsonProperty("result")
    public final GetBlockResponse.Result block;

    public GetLatestBlockResponse(
            @JsonProperty("success") Boolean success,
            @JsonProperty("result") GetBlockResponse.Result block) {
        super(success, null);
        this.block = block;
    }
}
