/*
 * Copyright 2016 LINE Corporation
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.util.Base64Utils;

import com.linecorp.bot.callback.LineSignatureValidator;

public class LineSignatureValidatorTest {
    private static final String channelSecret = "SECRET";

    LineSignatureValidator target = new LineSignatureValidator(channelSecret);

    @Test
    public void validateSignature() throws Exception {
        LineSignatureValidator target = new LineSignatureValidator(channelSecret);

        String httpRequestBody = "{}";

        assertThat(target.validateSignature(httpRequestBody, "3q8QXTAGaey18yL8FWTqdVlbMr6hcuNvM4tefa0o9nA="))
                .isTrue();

        assertThat(target.validateSignature(httpRequestBody, "596359635963"))
                .isFalse();
    }

    @Test
    public void generateSignature() throws Exception {
        String httpRequestBody = "{}";
        byte[] headerSignature = target.generateSignature(httpRequestBody);

        assertThat(Base64Utils.encodeToString(headerSignature))
                .isEqualTo("3q8QXTAGaey18yL8FWTqdVlbMr6hcuNvM4tefa0o9nA=");
    }
}
