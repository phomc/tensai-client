package dev.phomc.tensai.client.networking.message;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

import dev.phomc.tensai.networking.message.Message;

@Environment(EnvType.CLIENT)
public abstract class Messenger {
	protected final Identifier namespace;

	private final Map<Byte, MessageCaptureCallback> callbacks = new HashMap<>();

	public Messenger(Identifier namespace) {
		this.namespace = namespace;

		ClientPlayNetworking.registerGlobalReceiver(namespace, (client, handler, buf, responseSender) -> {
			byte[] bytes = ByteBufUtil.getBytes(buf);
			MessageCaptureCallback callback = callbacks.get(bytes[0]);
			if (callback != null) {
				callback.respond(bytes, responseSender);
			}
		});
	}

	public abstract void onInitialize();

	public void capture(byte pid, MessageCaptureCallback callback) {
		callbacks.put(pid, callback);
	}

	public void deliver(Message message, PacketSender consumer) {
		consumer.sendPacket(namespace, new PacketByteBuf(Unpooled.wrappedBuffer(message.pack())));
	}

	public void deliver(Message message) {
		deliver(message, ClientPlayNetworking.getSender());
	}
}
