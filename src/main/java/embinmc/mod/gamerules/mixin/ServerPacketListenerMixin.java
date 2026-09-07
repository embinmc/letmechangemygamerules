package embinmc.mod.gamerules.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.gamerules.GameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPacketListenerMixin {
    @Shadow
    public abstract ServerPlayer getPlayer();

    @ModifyExpressionValue(method = "handleSetGameRule", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/permissions/PermissionSet;hasPermission(Lnet/minecraft/server/permissions/Permission;)Z"))
    public boolean embin$allowSetWithoutCommands(boolean original) {
        MinecraftServer server = this.getPlayer().level().getServer();
        if (server.isSingleplayer()) {
            return original || server.isSingleplayerOwner(this.getPlayer().nameAndId());
        }
        return original;
    }

    @ModifyExpressionValue(method = "sendGameRuleValues", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/permissions/PermissionSet;hasPermission(Lnet/minecraft/server/permissions/Permission;)Z"))
    public boolean embin$allowGetWithoutCommands(boolean original) {
        MinecraftServer server = this.getPlayer().level().getServer();
        if (server.isSingleplayer()) {
            return original || server.isSingleplayerOwner(this.getPlayer().nameAndId());
        }
        return original;
    }

    // send game rule changes to player even if not op
    @Inject(method = "broadcastGameRuleChangeToOperators", at = @At("TAIL"))
    private <T> void embin$allowBroadcastWithoutCommands(GameRule<T> rule, T value, CallbackInfo ci, @Local(name = "message") Component message) {
        MinecraftServer server = this.getPlayer().level().getServer();
        if (server.isSingleplayer()) {
            PlayerList playerList = server.getPlayerList();
            playerList.getPlayers().stream()
                    .filter(op -> !playerList.isOp(op.nameAndId()))
                    .filter(op -> server.isSingleplayerOwner(op.nameAndId()))
                    .forEach(p -> p.sendSystemMessage(message));
        }
    }
}
