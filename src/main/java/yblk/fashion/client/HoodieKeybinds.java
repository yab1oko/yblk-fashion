package yblk.fashion.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/** Hood toggle — registered + ticked from {@link ClientSetup}. */
public final class HoodieKeybinds {
    public static final KeyMapping TOGGLE_HOOD = new KeyMapping(
            "key.fashion.toggle_hood",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.fashion"
    );

    private HoodieKeybinds() {
    }
}
