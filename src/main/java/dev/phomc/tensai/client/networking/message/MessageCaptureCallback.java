package dev.phomc.tensai.client.networking.message;

import net.fabricmc.fabric.api.networking.v1.PacketSender;

public interface MessageCaptureCallback {
	void respond(byte[] data, PacketSender sender);
}
