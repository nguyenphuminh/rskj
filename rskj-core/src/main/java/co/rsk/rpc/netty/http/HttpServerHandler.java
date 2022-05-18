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

import co.rsk.rpc.netty.http.dto.ModuleConfigDTO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    private HttpServerDispatcher dispatcher;

    private HttpServerHandler() { }

    public HttpServerHandler(ModuleConfigDTO moduleConfigDTO) {
        this.dispatcher = new HttpServerDispatcher(moduleConfigDTO);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if (msg instanceof HttpRequest) {
            logger.info("Client address: {}", ctx.channel().remoteAddress());

            HttpRequest httpRequest = (HttpRequest) msg;

            DefaultFullHttpResponse response = dispatcher.dispatch(httpRequest);

            if (response == null) {
                logger.info("Omitted response.");
                return;
            }

            ctx.writeAndFlush(response);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception caught {}", cause.getMessage());
        ctx.close();
    }

}
