package co.rsk.rpc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if (msg instanceof HttpRequest) {
            logger.info("Client address: {}", ctx.channel().remoteAddress());

            HttpRequest httpRequest = (HttpRequest) msg;

            URI uri = new URI(httpRequest.getUri());

            if ("/favicon.ico".equals(uri.getPath())) {
                return;
            }

            ByteBuf content = Unpooled.copiedBuffer("Test", StandardCharsets.UTF_8);

            if ("/ping".equals(uri.getPath())) {
                content = Unpooled.copiedBuffer("pong", StandardCharsets.UTF_8);
                logger.info("Ping Received");
            }

            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            response.headers().set(CONTENT_TYPE, "text/plain;charset=UTF-8");
            response.headers().set(CONTENT_LENGTH, content.readableBytes());

            ctx.writeAndFlush(response);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}
