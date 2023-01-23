package dev.phomc.tensai.client.keybinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import dev.phomc.tensai.client.i18n.CustomTranslationStorage;
import dev.phomc.tensai.client.mixin.KeyBindingMixin;
import dev.phomc.tensai.client.security.Permission;
import dev.phomc.tensai.keybinding.Key;
import dev.phomc.tensai.keybinding.KeyBinding;
import dev.phomc.tensai.keybinding.KeyState;
import dev.phomc.tensai.networking.Channel;

public class KeyBindingManager {
	private static final KeyBindingManager INSTANCE = new KeyBindingManager();

	public static KeyBindingManager getInstance() {
		return INSTANCE;
	}

	public static final Identifier KEYBINDING_NAMESPACE = new Identifier(Channel.KEYBINDING.getNamespace());
	public static final Permission KEY_RECORD_PERMISSION = new Permission(KEYBINDING_NAMESPACE, "record", Permission.Context.SESSION);

	public static InputUtil.Key getInputKey(@NotNull Key key) {
		return key.isMouse() ? InputUtil.Type.MOUSE.createFromCode(key.getGLFWCode()) : InputUtil.Type.KEYSYM.createFromCode(key.getGLFWCode());
	}

	public static Key lookupKey(@NotNull InputUtil.Key key) {
		return Key.lookupGLFW(key.getCode(), key.getCategory() == InputUtil.Type.MOUSE);
	}

	private List<net.minecraft.client.option.KeyBinding> registeredKeys = new ArrayList<>();
	private Map<Key, Integer> stateTable = new HashMap<>();
	private int inputDelay;

	public int getInputDelay() {
		return inputDelay;
	}

	public void setInputDelay(int inputDelay) {
		this.inputDelay = inputDelay;
	}

	public boolean testBulkAvailability(@NotNull List<KeyBinding> keymap) {
		for(KeyBinding keyBinding : keymap){
			InputUtil.Key key = getInputKey(keyBinding.getKey());
			if(KeyBindingMixin.getKeyCodeMapping().containsKey(key)) {
				return false;
			}
		}
		return true;
	}

	public void registerBulk(@NotNull List<KeyBinding> keymap) {
		stateTable = new HashMap<>();
		registeredKeys = new ArrayList<>();

		for(KeyBinding keyBinding : keymap){
			net.minecraft.client.option.KeyBinding v = new net.minecraft.client.option.KeyBinding(
					"key.tensai." + keyBinding.getKey(),
					keyBinding.getKey().isMouse() ? InputUtil.Type.MOUSE : InputUtil.Type.KEYSYM,
					keyBinding.getKey().getGLFWCode(),
					"key.categories.tensai"
			);
			CustomTranslationStorage.getInstance().put(v.getTranslationKey(), keyBinding.getName());
			KeyBindingHelper.registerKeyBinding(v);
			registeredKeys.add(v);
		}
	}

	public List<KeyState> fetchStates() {
		List<KeyState> states = new ArrayList<>();
		for(net.minecraft.client.option.KeyBinding key : registeredKeys){
			int n = ((KeyBindingMixin) key).getTimesPressed();
			Key k = lookupKey(key.getDefaultKey());
			Integer old = stateTable.put(k, n);
			if (old == null || old != n){
				states.add(new KeyState(k, n));
			}
		}
		return states;
	}
}
