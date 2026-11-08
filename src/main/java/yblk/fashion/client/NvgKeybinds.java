package yblk.fashion.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/** NVG power toggle — registered + ticked from {@link ClientSetup}. */
public final class NvgKeybinds {
    public static final KeyMapping TOGGLE_NVG = new KeyMapping(
            "key.fashion.toggle_nvg",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "key.categories.fashion"
    );

    private NvgKeybinds() {
    }
}
