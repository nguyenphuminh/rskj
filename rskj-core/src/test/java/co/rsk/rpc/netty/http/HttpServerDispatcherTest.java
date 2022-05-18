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
package co.rsk.rpc.netty.http;

import co.rsk.rpc.netty.http.modules.HealthCheckModule;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class HttpServerDispatcherTest {

    private HttpServerDispatcher httpServerDispatcher;

    @Before
    public void setup() {
        httpServerDispatcher = new HttpServerDispatcher(null);
    }

    @Test
    public void testDispatch_faviconUri_returnsNull() throws URISyntaxException {
        // Given
        String uri = "/favicon.ico";
        HttpRequest requestMock = mock(HttpRequest.class);
        doReturn(uri).when(requestMock).getUri();

        // When
        DefaultFullHttpResponse response = httpServerDispatcher.dispatch(requestMock);

        // Then
        Assert.assertNull(response);
    }

    @Test
    public void testDispatch_unsupportedUri_returnsNotFound() throws URISyntaxException {
        // Given
        String uri = "/unsupported.uri/does.not.exist";
        HttpRequest requestMock = mock(HttpRequest.class);
        doReturn(uri).when(requestMock).getUri();

        // When
        DefaultFullHttpResponse response = httpServerDispatcher.dispatch(requestMock);

        // Then
        Assert.assertNotNull(response);
        Assert.assertEquals(HttpResponseStatus.NOT_FOUND, response.getStatus());
        Assert.assertEquals(Unpooled.copiedBuffer("Not Found", StandardCharsets.UTF_8),
                response.content());
    }

    @Test
    public void testDispatch_healthCheckUri_executesAsExpected() throws URISyntaxException, NoSuchFieldException, IllegalAccessException {
        // Given
        String uri = "/health-check";
        HttpMethod method = HttpMethod.GET;

        HttpRequest requestMock = mock(HttpRequest.class);
        doReturn(uri).when(requestMock).getUri();
        doReturn(method).when(requestMock).getMethod();

        HealthCheckModule healthCheckModuleMock = mock(HealthCheckModule.class);
        doReturn(null).when(healthCheckModuleMock).processRequest(uri, method);

//        Field healthCheckModuleField = httpServerDispatcher.getClass()
//                .getDeclaredField("healthCheckModule");
//        healthCheckModuleField.setAccessible(true);
//        Field modifiers = Field.class.getDeclaredField("modifiers");
//        modifiers.setAccessible(true);
//        modifiers.setInt(healthCheckModuleField,
//                healthCheckModuleField.getModifiers() & ~Modifier.FINAL);
//        healthCheckModuleField.set(httpServerDispatcher, healthCheckModuleField);

        // When
        DefaultFullHttpResponse response = httpServerDispatcher.dispatch(requestMock);

        // Then
        Assert.assertNull(response);
    }

}
