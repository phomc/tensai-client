/*
 * This file is part of tensai, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2023 PhoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
