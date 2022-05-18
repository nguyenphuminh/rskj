/*
 * This file is part of RskJ
 * Copyright (C) 2018 RSK Labs Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package co.rsk.rpc.netty.http.modules;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class HealthCheckModuleTest {

    private HealthCheckModule healthCheckModule;

    @Before
    public void setup() {
        healthCheckModule = new HealthCheckModule();
    }

    @Test
    public void testProcessRequest_getMethod_pingUri_returnsPongMessage() {
        // Given
        String url = "/health-check/ping";
        HttpMethod method = HttpMethod.GET;

        // When
        DefaultFullHttpResponse response = healthCheckModule.processRequest(url, method);

        // Then
        Assert.assertNotNull(response);
        Assert.assertEquals(HttpResponseStatus.OK, response.getStatus());
        Assert.assertEquals(Unpooled.copiedBuffer("pong", StandardCharsets.UTF_8),
                response.content());
    }

    @Test
    public void testProcessRequest_unsupportedMethod_pingUri_returnsNull() {
        // Given
        String url = "/health-check/ping";
        HttpMethod method = HttpMethod.POST;

        // When
        DefaultFullHttpResponse response = healthCheckModule.processRequest(url, method);

        // Then
        Assert.assertNull(response);
    }

    @Test
    public void testProcessRequest_unsupportedUri_returnsNull() {
        // Given
        String url = "/health-check/foo";

        // When
        DefaultFullHttpResponse response = healthCheckModule.processRequest(url, HttpMethod.GET);

        // Then
        Assert.assertNull(response);
    }

}
