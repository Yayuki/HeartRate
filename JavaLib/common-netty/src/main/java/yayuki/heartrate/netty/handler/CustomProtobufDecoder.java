package yayuki.heartrate.netty.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class CustomProtobufDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) return;
        in.markReaderIndex();
        byte low = in.readByte();
        byte high = in.readByte();
        short clsNameLength = (short) ((high & 0xFF) << 8 | (low & 0xFF));

        low = in.readByte();
        high = in.readByte();
        short bodyLength = (short) ((high & 0xFF) << 8 | (low & 0xFF));

        if (in.readableBytes() < clsNameLength + bodyLength) {
            in.resetReaderIndex();
            return;
        }

        ByteBuf byteBuf = in.readBytes(clsNameLength);
        String className = bytebufToString(byteBuf);
        byteBuf.release();

        byteBuf = in.readBytes(bodyLength);
        MessageLite messageLite = bytebufToMessageLite(byteBuf, className);
        out.add(messageLite);
        byteBuf.release();
    }

    public static String bytebufToString(ByteBuf buf) {
        String str;
        if (buf.hasArray()) {
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }

    public static MessageLite bytebufToMessageLite(ByteBuf buf, String className) {
        byte[] array;
        int offset;

        int readableLen = buf.readableBytes();
        if (buf.hasArray()) {
            array = buf.array();
            offset = buf.arrayOffset() + buf.readerIndex();
        } else {
            array = new byte[readableLen];
            buf.getBytes(buf.readerIndex(), array, 0, readableLen);
            offset = 0;
        }
        return createMessageLite(className, array, offset, readableLen);
    }

    public static MessageLite createMessageLite(String className, byte[] data, int offset, int length) {
        try {
            Class<? extends MessageLite> cls = Class.forName(className).asSubclass(MessageLite.class);
            Method getDefaultInstance = cls.getMethod("getDefaultInstance");
            MessageLite message = (MessageLite) getDefaultInstance.invoke(null);
            return message.getParserForType().parseFrom(data, offset, length);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }
}
