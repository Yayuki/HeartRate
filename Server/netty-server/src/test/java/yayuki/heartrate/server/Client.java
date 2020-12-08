package yayuki.heartrate.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import yayuki.heartrate.net.protobuf.LoginProto;
import yayuki.heartrate.net.protobuf.PatientProto;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public int id;

    public static void connect(int count) {
        ExecutorService pool = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            int finalI = i;
            pool.execute(() -> {
                Client client = new Client();
                client.id = finalI + 1;
                client.connect();
            });
        }
    }

    private void connect() {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new CustomProtobufEncoder());
                p.addLast(new CustomProtobufDecoder());
                p.addLast(new LH());
            }
        });
        Channel channel = bootstrap.connect("localhost", 10600).channel();
        System.out.println(Thread.currentThread().getName());
        LoginProto.Credentials credentials = LoginProto.Credentials.newBuilder()
                .setAccount("pa" + id)
                .setPassword("pp" + id)
                .build();
        LoginProto.Request request = LoginProto.Request.newBuilder()
                .setStatus(LoginProto.Request.Status.PATIENT)
                .setCredentials(credentials)
                .build();
        channel.writeAndFlush(request);
    }

    static class LH extends SimpleChannelInboundHandler<LoginProto.Result> {
        ChannelHandlerContext ctx;
        PatientProto.State.Builder builder = PatientProto.State.newBuilder();
        Random rand = new Random();

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            this.ctx = ctx;
            super.channelActive(ctx);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, LoginProto.Result msg) throws Exception {
            System.out.println(msg);
            builder.setId(msg.getUser().getId());
            ctx.executor().execute(r);
        }

        protected Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    builder.setHeartRate(rand.nextInt(20) + 60);
                    ctx.writeAndFlush(builder.build());
                    ctx.executor().execute(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}