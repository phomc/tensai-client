package dev.phomc.tensai.client.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import dev.phomc.tensai.client.keybinding.KeyBindingManager;
import dev.phomc.tensai.client.keybinding.KeyBindingMessenger;


public class TickEventListener implements Listener {
	private long counter;

	@Override
	public void onInitialize() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (counter == Long.MAX_VALUE) {
				counter = 0;
			} else {
				counter++;
			}

			if (counter % KeyBindingManager.getInstance().getInputDelay() == 0) {
				KeyBindingMessenger.getInstance().onStateCheck();
			}
		});
	}
}
