package yayuki.heartrate.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import yayuki.heartrate.netty.handler.CustomProtobufDecoder;
import yayuki.heartrate.netty.handler.CustomProtobufEncoder;
import yayuki.heartrate.server.handler.LoginHandler;

public class NettyServer {
    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap sb = new ServerBootstrap();
        final LoginHandler loginHandler = new LoginHandler();
        sb.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new CustomProtobufDecoder());
                        p.addLast(new CustomProtobufEncoder());
                        p.addLast(new NioEventLoopGroup(), loginHandler);
                    }
                });
        try {
            ChannelFuture f = sb.bind(port).sync();
            System.out.println("Server Started");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
