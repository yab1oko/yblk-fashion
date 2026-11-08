package yblk.fashion.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import yblk.fashion.Fashion;
import yblk.fashion.item.ArmorPlateItem;
import yblk.fashion.item.CigaretteItem;
import yblk.fashion.item.ClothingMods;
import yblk.fashion.item.PouchItem;
import yblk.fashion.network.ApplyClothingModPacket;
import yblk.fashion.network.FashionNetwork;
import yblk.fashion.network.RemoveClothingModPacket;

/**
 * Shift+RMB on fashion clothing opens 1–2 mod slots to the left of that inventory slot.
 */
@Mod.EventBusSubscriber(modid = Fashion.MODID, value = Dist.CLIENT)
public final class ClothingModSlotOverlay {
    private static final ResourceLocation SELECT_FRAME =
            new ResourceLocation(Fashion.MODID, "textures/gui/mod_slot_select.png");
    private static final int SLOT_SIZE = 18;
    private static final int FRAME = 3;
    private static final int GAP = 5;

    private static boolean panelOpen;
    private static AbstractContainerScreen<?> openOnScreen;
    private static int openSlotIndex = -1;

    private ClothingModSlotOverlay() {
    }

    /** Fashion creative tab must not rebuild/wipe stacks while the mod panel is open. */
    public static boolean isPanelOpenOn(Screen screen) {
        return panelOpen && openOnScreen == screen;
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        if (event.getScreen() == openOnScreen) {
            closePanel();
        }
    }

    /**
     * Draw in container foreground (before the floating cursor stack), so a carried
     * item stays on top of the mod slots.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onForeground(ContainerScreenEvent.Render.Foreground event) {
        AbstractContainerScreen<?> screen = event.getContainerScreen();
        if (!isInventoryScreen(screen) || !panelOpen || openOnScreen != screen) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }
        Slot clothingSlot = slotByIndex(screen, openSlotIndex);
        if (clothingSlot == null || clothingSlot.getItem().isEmpty()
                || !ClothingMods.showsModSlot(clothingSlot.getItem())) {
            closePanel();
            return;
        }
        ItemStack clothing = clothingSlot.getItem();

        int count = ClothingMods.slotCount(clothing);
        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();
        // Foreground pose is already translated to guiLeft/guiTop.
        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();
        GuiGraphics g = event.getGuiGraphics();
        g.pose().pushPose();
        g.pose().translate(0, 0, 200);

        for (int i = 0; i < count; i++) {
            int slotX = modSlotX(clothingSlot);
            int slotY = modSlotY(clothingSlot, i);
            boolean hovered = mouseX >= left + slotX && mouseX < left + slotX + SLOT_SIZE
                    && mouseY >= top + slotY && mouseY < top + slotY + SLOT_SIZE;

            drawDarkSlot(g, slotX, slotY);
            g.blit(SELECT_FRAME, slotX - FRAME, slotY - FRAME, 0, 0, 24, 24, 24, 24);

            ItemStack shown = ClothingMods.displayMod(clothing, i);
            if (!shown.isEmpty()) {
                g.renderItem(shown, slotX + 1, slotY + 1);
                g.renderItemDecorations(mc.font, shown, slotX + 1, slotY + 1);
            }

            if (hovered) {
                g.fillGradient(
                        slotX + 1,
                        slotY + 1,
                        slotX + SLOT_SIZE - 1,
                        slotY + SLOT_SIZE - 1,
                        0x80FFFFFF,
                        0x80FFFFFF);
                // Skip tooltip while carrying — cursor item + tip fight for space.
                if (screen.getMenu().getCarried().isEmpty()) {
                    g.renderTooltip(
                            mc.font,
                            shown.isEmpty()
                                    ? Component.translatable("item.fashion.mod.slot_hint")
                                    : shown.getHoverName(),
                            (int) mouseX - left,
                            (int) mouseY - top);
                }
            }
        }
        g.pose().popPose();
    }

    private static void drawDarkSlot(GuiGraphics g, int x, int y) {
        g.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF000000);
        g.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xFF2A2A2A);
        g.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + 2, 0xFF1A1A1A);
        g.fill(x + 1, y + 1, x + 2, y + SLOT_SIZE - 1, 0xFF1A1A1A);
        g.fill(x + SLOT_SIZE - 2, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xFF3F3F3F);
        g.fill(x + 1, y + SLOT_SIZE - 2, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xFF3F3F3F);
        g.fill(x + 2, y + 2, x + SLOT_SIZE - 2, y + SLOT_SIZE - 2, 0xFF222222);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClick(ScreenEvent.MouseButtonPressed.Pre event) {
        handleSlotClick(event.getScreen(), event.getButton(), event.getMouseX(), event.getMouseY(), event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRelease(ScreenEvent.MouseButtonReleased.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) {
            return;
        }
        if (!isInventoryScreen(screen) || !panelOpen || openOnScreen != screen) {
            return;
        }
        Slot clothingSlot = slotByIndex(screen, openSlotIndex);
        if (clothingSlot == null) {
            return;
        }
        ItemStack clothing = clothingSlot.getItem();
        int count = ClothingMods.showsModSlot(clothing) ? ClothingMods.slotCount(clothing) : 0;
        for (int i = 0; i < count; i++) {
            if (isOverModSlot(screen, modSlotX(clothingSlot), modSlotY(clothingSlot, i),
                    event.getMouseX(), event.getMouseY())) {
                event.setCanceled(true);
                return;
            }
        }
    }

    private static void handleSlotClick(
            Screen rawScreen,
            int button,
            double mouseX,
            double mouseY,
            ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(rawScreen instanceof AbstractContainerScreen<?> screen)) {
            return;
        }
        if (!isInventoryScreen(screen)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }

        boolean shift = isShiftDown(mc);
        Slot under = findHoveredClothingSlot(screen, player, mouseX, mouseY);

        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT
                && shift
                && under != null) {
            int menuIndex = under.index;
            if (panelOpen && openOnScreen == screen && openSlotIndex == menuIndex) {
                closePanel();
            } else {
                panelOpen = true;
                openOnScreen = screen;
                openSlotIndex = menuIndex;
            }
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, 1.0F);
            event.setCanceled(true);
            return;
        }

        if (!panelOpen || openOnScreen != screen) {
            return;
        }
        Slot clothingSlot = slotByIndex(screen, openSlotIndex);
        if (clothingSlot == null || clothingSlot.getItem().isEmpty()
                || !ClothingMods.showsModSlot(clothingSlot.getItem())) {
            closePanel();
            return;
        }
        ItemStack clothing = clothingSlot.getItem();

        int hit = -1;
        int count = ClothingMods.slotCount(clothing);
        for (int i = 0; i < count; i++) {
            if (isOverModSlot(screen, modSlotX(clothingSlot), modSlotY(clothingSlot, i), mouseX, mouseY)) {
                hit = i;
                break;
            }
        }
        if (hit < 0) {
            return;
        }
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT && button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return;
        }

        ItemStack carried = screen.getMenu().getCarried();
        boolean inventoryBacked = isInventoryBacked(player, clothingSlot);

        if (!carried.isEmpty() && ClothingMods.canAccept(clothing, carried)) {
            ItemStack one = carried.copy();
            one.setCount(1);
            ClothingMods.apply(clothing, one);
            clothingSlot.set(clothing);
            carried.shrink(1);
            if (carried.isEmpty()) {
                screen.getMenu().setCarried(ItemStack.EMPTY);
            }
            if (inventoryBacked) {
                FashionNetwork.CHANNEL.sendToServer(
                        new ApplyClothingModPacket(clothingSlot.getSlotIndex()));
            }
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.5F, 1.1F);
            event.setCanceled(true);
            return;
        }

        if (carried.isEmpty()) {
            ClothingMods.removeAt(clothing, hit).ifPresent(out -> {
                clothingSlot.set(clothing);
                screen.getMenu().setCarried(out);
                String modId = modIdOf(out);
                if (modId != null && inventoryBacked) {
                    FashionNetwork.CHANNEL.sendToServer(
                            new RemoveClothingModPacket(clothingSlot.getSlotIndex(), modId));
                }
                player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.5F, 0.9F);
            });
            event.setCanceled(true);
        }
    }

    private static String modIdOf(ItemStack out) {
        if (out.getItem() instanceof CigaretteItem) {
            return ClothingMods.CIGARETTE;
        }
        if (out.getItem() instanceof PouchItem) {
            return ClothingMods.POUCH;
        }
        if (out.getItem() instanceof ArmorPlateItem) {
            return ClothingMods.PLATE;
        }
        return null;
    }

    private static void closePanel() {
        panelOpen = false;
        openOnScreen = null;
        openSlotIndex = -1;
    }

    private static Slot slotByIndex(AbstractContainerScreen<?> screen, int index) {
        if (index < 0 || index >= screen.getMenu().slots.size()) {
            return null;
        }
        return screen.getMenu().getSlot(index);
    }

    private static Slot findHoveredClothingSlot(
            AbstractContainerScreen<?> screen, Player player, double mouseX, double mouseY) {
        Slot underMouse = screen.getSlotUnderMouse();
        if (isClothingTarget(screen, player, underMouse)
                && isOverSlot(screen, underMouse, mouseX, mouseY)) {
            return underMouse;
        }
        for (Slot slot : screen.getMenu().slots) {
            if (!isClothingTarget(screen, player, slot)) {
                continue;
            }
            if (isOverSlot(screen, slot, mouseX, mouseY)) {
                return slot;
            }
        }
        return null;
    }

    private static boolean isClothingTarget(
            AbstractContainerScreen<?> screen, Player player, Slot slot) {
        return slot != null
                && slot.isActive()
                && slot.hasItem()
                && ClothingMods.showsModSlot(slot.getItem())
                && isInventoryBacked(player, slot);
    }

    private static boolean isInventoryBacked(Player player, Slot slot) {
        return slot != null && slot.container == player.getInventory();
    }

    private static boolean isInventoryScreen(AbstractContainerScreen<?> screen) {
        return screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen;
    }

    private static int modSlotX(Slot clothingSlot) {
        return clothingSlot.x - SLOT_SIZE - GAP;
    }

    private static int modSlotY(Slot clothingSlot, int index) {
        return clothingSlot.y + index * (SLOT_SIZE + 2);
    }

    private static boolean isOverModSlot(
            AbstractContainerScreen<?> screen, int slotX, int slotY, double mouseX, double mouseY) {
        int x = screen.getGuiLeft() + slotX;
        int y = screen.getGuiTop() + slotY;
        return mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE;
    }

    private static boolean isOverSlot(
            AbstractContainerScreen<?> screen, Slot slot, double mouseX, double mouseY) {
        int x = screen.getGuiLeft() + slot.x;
        int y = screen.getGuiTop() + slot.y;
        return mouseX >= x - 1 && mouseX < x + 16 + 1 && mouseY >= y - 1 && mouseY < y + 16 + 1;
    }

    private static boolean isShiftDown(Minecraft mc) {
        if (Screen.hasShiftDown()) {
            return true;
        }
        if (mc.options.keyShift.isDown()) {
            return true;
        }
        long window = mc.getWindow().getWindow();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }
}
