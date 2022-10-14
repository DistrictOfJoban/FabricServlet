package com.lx.fabricservlet.mixin;

import com.lx.fabricservlet.FabricServlet;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)

public class MinecraftServerMixin {

    @Inject(method = "shutdown()V", at = @At(value="HEAD"))
    public void shutdown(CallbackInfo ci) {
        FabricServlet.stopAllServers();
    }
}
