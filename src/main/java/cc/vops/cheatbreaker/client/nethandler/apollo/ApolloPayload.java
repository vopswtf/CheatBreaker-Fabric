package cc.vops.cheatbreaker.client.nethandler.apollo;

import com.google.protobuf.Any;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class ApolloPayload implements CustomPacketPayload {
    private final Any protobuf;

    public static final CustomPacketPayload.Type<ApolloPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(ApolloNetHandler.APOLLO_CHANNEL);
    public static final StreamCodec<FriendlyByteBuf, ApolloPayload> STREAM_CODEC = CustomPacketPayload.codec(ApolloPayload::write, ApolloPayload::new);

    public ApolloPayload(Any protobuf) {
        this.protobuf = protobuf;
    }

    public ApolloPayload(FriendlyByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        Any parsed = null;

        try {
            parsed = Any.parseFrom(bytes);
        } catch (Exception ignored) {
        }

        this.protobuf = parsed;
    }

    public void write(FriendlyByteBuf buf) {
        byte[] bytes = this.protobuf.toByteArray();
        buf.writeBytes(bytes);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }

    public Any protobuf() {
        return this.protobuf;
    }
}
