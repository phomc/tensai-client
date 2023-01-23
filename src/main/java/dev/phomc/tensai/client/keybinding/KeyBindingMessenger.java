package dev.phomc.tensai.client.keybinding;

import java.util.List;

import net.minecraft.util.Identifier;

import dev.phomc.tensai.client.networking.message.Messenger;
import dev.phomc.tensai.client.security.PermissionManager;
import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.keybinding.KeyState;
import dev.phomc.tensai.networking.message.MessageType;
import dev.phomc.tensai.networking.message.c2s.KeyBindingRegisterResponse;
import dev.phomc.tensai.networking.message.c2s.KeyBindingStateUpdate;
import dev.phomc.tensai.networking.message.s2c.KeyBindingRegisterMessage;

public class KeyBindingMessenger extends Messenger {
	private static final KeyBindingMessenger INSTANCE = new KeyBindingMessenger(KeyBindingManager.KEYBINDING_NAMESPACE);

	public static KeyBindingMessenger getInstance() {
		return INSTANCE;
	}

	private KeyBindingMessenger(Identifier namespace) {
		super(namespace);
	}

	@Override
	public void onInitialize() {
		capture(MessageType.KEYBINDING_REGISTER, (data, sender) -> {
			KeyBindingRegisterMessage msg = new KeyBindingRegisterMessage();
			msg.unpack(data);

			PermissionManager.getInstance().tryGrant(KeyBindingManager.KEY_RECORD_PERMISSION, ok -> {
				byte result = ok ? KeyBinding.RegisterStatus.UNKNOWN : KeyBinding.RegisterStatus.CLIENT_REJECTED;

				if(ok) {
					if(KeyBindingManager.getInstance().testBulkAvailability(msg.getKeymap())) {
						KeyBindingManager.getInstance().registerBulk(msg.getKeymap());
						KeyBindingManager.getInstance().setInputDelay(msg.getInputDelay());
						result = KeyBinding.RegisterStatus.SUCCESS;
					} else {
						result = KeyBinding.RegisterStatus.KEY_DUPLICATED;
					}
				}

				deliver(new KeyBindingRegisterResponse(result), sender);
			});
		});
	}

	public void onStateCheck() {
		List<KeyState> states = KeyBindingManager.getInstance().fetchStates();
		if(!states.isEmpty()) {
			deliver(new KeyBindingStateUpdate(states));
		}
	}
}
