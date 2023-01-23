package dev.phomc.tensai.client.security;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Identifier;

public record Permission(@NotNull Identifier namespace, @NotNull String key, @NotNull Context context) {
	public Permission {
		if(!key.matches("[0-9A-Za-z-_]+")){
			throw new IllegalArgumentException("invalid permission key");
		}
	}

	public Permission(String namespace, String key, Context context) {
		this(new Identifier(namespace), key, context);
	}

	@Override
	public String toString() {
		return String.format("%s:%s", namespace, key);
	}

	public enum Context {
		/**
		 * The permission has a global effect.<br>
		 * Once the permission is granted, it remains permanently.
		 */
		GLOBAL,

		/**
		 * The permission takes effect only on a specific server.<br>
		 * Once the permission is granted, it remains permanently (for that server only).
		 */
		SERVER,

		/**
		 * The permission takes effect only on a server.<br>
		 * Once the permission is granted, it remains until the player quits the server.
		 */
		SESSION
	}
}
