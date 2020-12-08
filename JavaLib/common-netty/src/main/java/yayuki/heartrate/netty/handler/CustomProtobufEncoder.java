package yayuki.heartrate.netty.handler;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class CustomProtobufEncoder extends MessageToByteEncoder<MessageLite> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {
        byte[] name = msg.getClass().getName().getBytes(StandardCharsets.US_ASCII);
        byte[] body = msg.toByteArray();
        byte[] header = createHeader((short) name.length, (short) body.length);
        out.writeBytes(header);
        out.writeBytes(name);
        out.writeBytes(body);
    }

    public static byte[] createHeader(short clsNameLength, short bodyLength) {
        byte[] header = new byte[4];
        header[0] = (byte) (clsNameLength & 0xff);
        header[1] = (byte) ((clsNameLength >> 8) & 0xff);
        header[2] = (byte) (bodyLength & 0xff);
        header[3] = (byte) ((bodyLength >> 8) & 0xff);
        return header;
    }
}
