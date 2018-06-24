/*
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.linecorp.bot.client;

import com.linecorp.bot.liff.LiffView;
import com.linecorp.bot.liff.request.LiffAppAddRequest;
import com.linecorp.bot.liff.response.LiffAppAddResponse;
import com.linecorp.bot.liff.response.LiffAppsResponse;

import retrofit2.http.Body;
import retrofit2.http.Path;

public interface ChannelManagementSyncClient {
    static ChannelManagementClientBuilder builder(final ChannelTokenSupplier channelTokenSupplier) {
        return ChannelManagementClientBuilder.create(channelTokenSupplier);
    }

    LiffAppAddResponse addLiffApp(@Body LiffAppAddRequest liffView);

    void updateLiffApp(@Path("liffId") String liffId, @Body LiffView liffView);

    LiffAppsResponse getAllLiffApps();

    void deleteLiffApp(@Path("liffId") String liffId);
}
