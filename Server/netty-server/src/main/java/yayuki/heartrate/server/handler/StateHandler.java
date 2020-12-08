package yayuki.heartrate.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import yayuki.heartrate.net.protobuf.PatientProto;
import yayuki.heartrate.server.repository.ChannelRepository;

public class StateHandler extends SimpleChannelInboundHandler<PatientProto.State> {
    private int carerID;

    public StateHandler(int carerID) {
        this.carerID = carerID;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PatientProto.State msg) throws Exception {
        Channel carerChannel = ChannelRepository.INSTANCE.getCarerChannel(carerID);
        if (carerChannel == null) return;
        carerChannel.writeAndFlush(msg);
    }
}
