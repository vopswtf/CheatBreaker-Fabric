package cc.vops.cheatbreaker.client.util;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ByteBufWrapper {

    private final ByteBuf buf;

    public ByteBufWrapper(ByteBuf buf) {
        this.buf = buf;
    }

    public void writeVarInt(int b) {
        while ((b & 0xFFFFFF80) != 0x0) {
            this.buf.writeByte((b & 0x7F) | 0x80);
            b >>>= 7;
        }
        this.buf.writeByte(b);
    }

    public int readVarInt() {
        int i = 0;
        int chunk = 0;
        byte b;
        do {
            b = this.buf.readByte();
            i |= (b & 0x7F) << chunk++ * 7;
            if (chunk > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b & 0x80) == 0x80);
        return i;
    }

    public String readStringFromBuffer(int maxLength) throws IOException {
        int byteLen = this.readVarInt();

        if (byteLen > maxLength * 4) {
            throw new IOException("The received encoded string buffer length is longer than maximum allowed ("
                    + byteLen + " > " + maxLength * 4 + ")");
        } else if (byteLen < 0) {
            throw new IOException("The received encoded string buffer length is less than zero!");
        }

        ByteBuf slice = this.buf.readBytes(byteLen);

        byte[] bytes = new byte[byteLen];
        slice.getBytes(0, bytes);

        String str = new String(bytes, Charsets.UTF_8);

        if (str.length() > maxLength) {
            throw new IOException("The received string length is longer than maximum allowed ("
                    + str.length() + " > " + maxLength + ")");
        }

        return str;
    }


    public <T> void writeOptional(T obj, Consumer<T> consumer) {
        this.buf.writeBoolean(obj != null);
        if (obj != null) {
            consumer.accept(obj);
        }
    }

    public <T> T readOptional(Supplier<T> supplier) {
        boolean isPresent = this.buf.readBoolean();
        return isPresent ? supplier.get() : null;
    }

    public void writeString(String s) {
        byte[] arr = s.getBytes(Charsets.UTF_8);
        this.writeVarInt(arr.length);
        this.buf.writeBytes(arr);
    }

    public String readString() {
        int len = this.readVarInt();
        byte[] buffer = new byte[len];
        this.buf.readBytes(buffer);
        return new String(buffer, Charsets.UTF_8);
    }

    public void writeUUID(UUID uuid) {
        this.buf.writeLong(uuid.getMostSignificantBits());
        this.buf.writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUUID() {
        long mostSigBits = this.buf.readLong();
        long leastSigBits = this.buf.readLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public void writeBytes(byte[] bytes) {
        this.writeVarInt(bytes.length);
        this.buf.writeBytes(bytes);
    }

    public byte[] readBytes() {
        int len = this.readVarInt();
        byte[] bytes = new byte[len];
        this.buf.readBytes(bytes);
        return bytes;
    }

    public void writeLong(long value) {
        this.buf.writeLong(value);
    }

    public long readLong() {
        return this.buf.readLong();
    }

    public ByteBuf buf() {
        return this.buf;
    }
}
