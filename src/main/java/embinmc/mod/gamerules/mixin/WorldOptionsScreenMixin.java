package embinmc.mod.gamerules.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.WorldOptionsScreen;
import net.minecraft.client.server.IntegratedServer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldOptionsScreen.class)
public class WorldOptionsScreenMixin {
	@Shadow private @Nullable Button gameRulesButton;

	@Inject(method = "updateButton", at = @At("TAIL"))
	private void embinmc_letmechangemygamerules$allowGameRule(AbstractWidget widget, IntegratedServer singleplayerServer, Tooltip tooltip, Tooltip disabledTooltip, Tooltip hardcoreTooltip, CallbackInfo ci) {
		if (widget == this.gameRulesButton && singleplayerServer != null) {
			widget.active = true;
			widget.setTooltip(null);
		}
	}

	// 26.3
	// make sure the button is active on init
	@Inject(method = "generalOptions", at = @At("TAIL"))
	private void embinmc_letmechangemygamerules$reupdateGameRuleButton(LinearLayout content, IntegratedServer singleplayerServer, CallbackInfo ci) {
		if (singleplayerServer != null && this.gameRulesButton != null) {
			this.gameRulesButton.active = true;
			this.gameRulesButton.setTooltip(null);
		}
	}
}