package dev.phomc.tensai.client.security;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

public class PermissionManager {
	private static final PermissionManager INSTANCE = new PermissionManager();

	public static PermissionManager getInstance() {
		return INSTANCE;
	}

	public void tryGrant(@NotNull Permission permission, @NotNull Consumer<Boolean> callback) {

	}
}
