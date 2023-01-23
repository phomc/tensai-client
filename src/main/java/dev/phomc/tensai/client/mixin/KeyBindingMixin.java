package dev.phomc.tensai.client.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.util.InputUtil;

@Mixin(targets = "net.minecraft.client.option.KeyBinding")
public interface KeyBindingMixin {
	@Accessor("KEY_TO_BINDINGS")
	static Map<InputUtil.Key, net.minecraft.client.option.KeyBinding> getKeyCodeMapping() {
		throw new AssertionError();
	}

	@Accessor("timesPressed")
	int getTimesPressed();
}
