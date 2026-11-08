package yblk.fashion.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import yblk.fashion.Fashion;
import yblk.fashion.init.ModCreativeTabs;
import yblk.fashion.init.ModItems;
import yblk.fashion.item.ClothingMods;
import yblk.fashion.item.TShirtColor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * CTS-style collapsible folders inside the Fashion creative tab.
 * Sewing station on top; dyed clothing under «Разноцветные»; misc under «Разные».
 */
@Mod.EventBusSubscriber(modid = Fashion.MODID, value = Dist.CLIENT)
public final class CreativeFolderTab {
    private static final int COLUMNS = 9;
    private static final int VISIBLE_ROWS = 5;
    private static final int BORDER = 0xFF373737;
    private static final ResourceLocation CREATIVE_BACKGROUND =
            new ResourceLocation("textures/gui/container/creative_inventory/tab_items.png");

    private static final Folder MULTICOLOR = new Folder(
            Component.translatable("itemGroup.fashion.multicolor"),
            multicolorClothing());
    private static final Folder MISC = new Folder(
            Component.translatable("itemGroup.fashion.misc"),
            miscClothing());
    private static final Folder MODS = new Folder(
            Component.translatable("itemGroup.fashion.mods"),
            modifications());

    private static Field selectedTabField;
    private static Field scrollField;
    private static final List<ItemStack> layout = new ArrayList<>();
    private static final List<Folder> headerAtRow = new ArrayList<>();

    private CreativeFolderTab() {
    }

    @SubscribeEvent
    public static void beforeRender(ScreenEvent.Render.Pre event) {
        if (!(event.getScreen() instanceof CreativeModeInventoryScreen screen) || !isFashionTab()) {
            return;
        }
        // Don't wipe clothing-mod NBT while the Shift+RMB panel is open on this screen.
        if (ClothingModSlotOverlay.isPanelOpenOn(screen)) {
            return;
        }
        apply(screen.getMenu(), screen);
    }

    @SubscribeEvent
    public static void onForeground(ContainerScreenEvent.Render.Foreground event) {
        if (!(event.getContainerScreen() instanceof CreativeModeInventoryScreen screen) || !isFashionTab()) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        Font font = Minecraft.getInstance().font;
        int origin = scrollRow(screen);
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        RenderSystem.disableDepthTest();
        for (int row = 0; row < headerAtRow.size(); row++) {
            int visible = row - origin;
            if (visible < 0 || visible >= VISIBLE_ROWS || headerAtRow.get(row) == null) {
                continue;
            }
            drawFolder(graphics, font, 9, 18 + visible * 18, headerAtRow.get(row));
        }
        for (int row = 0; row < headerAtRow.size(); row++) {
            int visible = row - origin;
            if (visible < 0 || visible >= VISIBLE_ROWS || headerAtRow.get(row) == null) {
                continue;
            }
            drawFolderBorder(graphics, 9, 18 + visible * 18);
        }
        RenderSystem.enableDepthTest();
        graphics.pose().popPose();
    }

    @SubscribeEvent
    public static void onClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof CreativeModeInventoryScreen screen) || !isFashionTab()) {
            return;
        }
        // Only the Fashion item-grid (9×5). Armor / hotbar / offhand sit outside — never cancel those.
        int x = (int) event.getMouseX() - screen.getGuiLeft() - 9;
        int y = (int) event.getMouseY() - screen.getGuiTop() - 18;
        if (x < 0 || x >= COLUMNS * 18 || y < 0 || y >= VISIBLE_ROWS * 18) {
            return;
        }
        int row = y / 18 + scrollRow(screen);
        if (row < 0 || row >= headerAtRow.size()) {
            return;
        }
        Folder folder = headerAtRow.get(row);
        if (folder == null) {
            return;
        }
        // Folder header row only (open defaults true). LMB toggles; any button cancels grid steal.
        if (event.getButton() == 0) {
            folder.open = !folder.open;
            apply(screen.getMenu(), screen);
        }
        event.setCanceled(true);
    }

    private static void apply(
            CreativeModeInventoryScreen.ItemPickerMenu menu, CreativeModeInventoryScreen screen) {
        rebuild();
        preserveClothingMods(menu.items, layout);
        if (same(menu.items, layout)) {
            return;
        }
        float scroll = readScroll(screen);
        menu.items.clear();
        menu.items.addAll(layout);
        menu.scrollTo(scroll);
    }

    /** Keep FashionMods NBT on catalog stacks across folder rebuilds. */
    private static void preserveClothingMods(List<ItemStack> current, List<ItemStack> next) {
        int n = Math.min(current.size(), next.size());
        for (int i = 0; i < n; i++) {
            ItemStack from = current.get(i);
            ItemStack to = next.get(i);
            if (from.isEmpty() || to.isEmpty()) {
                continue;
            }
            if (!ItemStack.isSameItem(from, to)) {
                continue;
            }
            if (!ClothingMods.showsModSlot(from)) {
                continue;
            }
            if (from.hasTag()) {
                next.set(i, from.copy());
            }
        }
    }

    private static void rebuild() {
        layout.clear();
        headerAtRow.clear();
        // Top: sewing station, then mods (visible without scrolling past dyeing folders).
        headerAtRow.add(null);
        layout.add(new ItemStack(ModItems.SEWING_STATION.get()));
        padRow(1);
        appendFolder(MODS);
        appendFolder(MULTICOLOR);
        appendFolder(MISC);
    }

    private static void appendFolder(Folder folder) {
        headerAtRow.add(folder);
        for (int column = 0; column < COLUMNS; column++) {
            layout.add(ItemStack.EMPTY);
        }
        if (!folder.open) {
            return;
        }
        int column = 0;
        for (RegistryObject<Item> item : folder.items) {
            if (column == 0) {
                headerAtRow.add(null);
            }
            layout.add(new ItemStack(item.get()));
            column++;
            if (column == COLUMNS) {
                column = 0;
            }
        }
        if (column != 0) {
            padRow(column);
        }
    }

    private static void padRow(int column) {
        while (column < COLUMNS) {
            layout.add(ItemStack.EMPTY);
            column++;
        }
    }

    private static boolean same(List<ItemStack> current, List<ItemStack> next) {
        if (current.size() != next.size()) {
            return false;
        }
        for (int i = 0; i < next.size(); i++) {
            ItemStack left = current.get(i);
            ItemStack right = next.get(i);
            if (left.isEmpty() && right.isEmpty()) {
                continue;
            }
            if (!ItemStack.isSameItemSameTags(left, right)) {
                return false;
            }
        }
        return true;
    }

    private static void drawFolder(GuiGraphics graphics, Font font, int x, int y, Folder folder) {
        graphics.blit(CREATIVE_BACKGROUND, x - 1, y - 1, COLUMNS * 18 + 1, 19, 28, 6, 16, 8, 256, 256);
        int textY = y + 4;
        graphics.drawString(font, folder.label, x - 1, textY, 0x404040, false);
        String mark = folder.open ? "-" : "+";
        graphics.drawString(font, mark, x + COLUMNS * 18 - 4 - font.width(mark), textY, 0x404040, false);
    }

    private static void drawFolderBorder(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y + 17, x + COLUMNS * 18 - 1, y + 18, BORDER);
    }

    /** Visible top row of the Fashion creative grid (for catalog mod NBT sync). */
    static int scrollRow(CreativeModeInventoryScreen screen) {
        int extra = Mth.positiveCeilDiv(screen.getMenu().items.size(), COLUMNS) - VISIBLE_ROWS;
        if (extra <= 0) {
            return 0;
        }
        return Math.max((int) (readScroll(screen) * extra + 0.5D), 0);
    }

    private static float readScroll(CreativeModeInventoryScreen screen) {
        try {
            if (scrollField == null) {
                scrollField = findScrollField();
                scrollField.setAccessible(true);
            }
            return scrollField.getFloat(screen);
        } catch (ReflectiveOperationException ignored) {
            return 0.0F;
        }
    }

    private static Field findScrollField() throws NoSuchFieldException {
        try {
            return CreativeModeInventoryScreen.class.getDeclaredField("scrollOffs");
        } catch (NoSuchFieldException ignored) {
        }
        for (Field field : CreativeModeInventoryScreen.class.getDeclaredFields()) {
            if (field.getType() == float.class && !Modifier.isStatic(field.getModifiers())) {
                return field;
            }
        }
        throw new NoSuchFieldException("scrollOffs");
    }

    private static boolean isFashionTab() {
        CreativeModeTab tab = selectedTab();
        return tab != null && tab == ModCreativeTabs.MAIN.get();
    }

    private static CreativeModeTab selectedTab() {
        try {
            if (selectedTabField == null) {
                try {
                    selectedTabField = CreativeModeInventoryScreen.class.getDeclaredField("selectedTab");
                } catch (NoSuchFieldException missing) {
                    selectedTabField = CreativeModeInventoryScreen.class.getDeclaredField("f_98507_");
                }
                selectedTabField.setAccessible(true);
            }
            return (CreativeModeTab) selectedTabField.get(null);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static List<RegistryObject<Item>> multicolorClothing() {
        List<RegistryObject<Item>> items = new ArrayList<>();
        for (TShirtColor color : TShirtColor.values()) {
            items.add(ModItems.TSHIRTS.get(color));
            if (color.hasTankTop()) {
                items.add(ModItems.TANK_TOPS.get(color));
            }
            items.add(ModItems.PANTS.get(color));
            items.add(ModItems.SHORTS.get(color));
            items.add(ModItems.HOODIES.get(color));
            items.add(ModItems.JACKETS.get(color));
            items.add(ModItems.SNEAKERS.get(color));
            items.add(ModItems.HATS.get(color));
            items.add(ModItems.CAPS.get(color));
        }
        return items;
    }

    private static List<RegistryObject<Item>> miscClothing() {
        List<RegistryObject<Item>> items = new ArrayList<>();
        for (TShirtColor color : TShirtColor.values()) {
            if (color.hasShoes()) {
                items.add(ModItems.SHOES.get(color));
            }
        }
        for (TShirtColor color : TShirtColor.values()) {
            if (color.hasCowboyHat()) {
                items.add(ModItems.COWBOY_HATS.get(color));
            }
        }
        items.add(ModItems.STRAW_HAT);
        items.add(ModItems.HELMET);
        items.add(ModItems.SCRAP_HELMET);
        items.add(ModItems.SCRAP_CHESTPLATE);
        items.add(ModItems.SCRAP_PANTS);
        items.add(ModItems.GHILLIE_HOOD);
        items.add(ModItems.GHILLIE_TOP);
        items.add(ModItems.GHILLIE_PANTS);
        items.add(ModItems.GHILLIE_BOOTS);
        for (TShirtColor color : TShirtColor.values()) {
            if (color.hasCoat()) {
                items.add(ModItems.COATS.get(color));
            }
        }
        for (TShirtColor color : TShirtColor.values()) {
            if (color.hasVest()) {
                items.add(ModItems.VESTS.get(color));
            }
        }
        for (TShirtColor color : TShirtColor.values()) {
            if (color.hasBandana()) {
                items.add(ModItems.BANDANAS.get(color));
            }
        }
        for (TShirtColor color : TShirtColor.values()) {
            if (color.hasBalaclava()) {
                items.add(ModItems.BALACLAVAS.get(color));
            }
        }
        items.add(ModItems.NVG);
        items.add(ModItems.GAS_MASK);
        items.add(ModItems.GLASSES);
        items.add(ModItems.BLACK_GLASSES);
        for (TShirtColor color : TShirtColor.values()) {
            if (color.hasClutch()) {
                items.add(ModItems.CLUTCHES.get(color));
            }
            if (color.hasSportsBag()) {
                items.add(ModItems.SPORTS_BAGS.get(color));
            }
            if (color.hasBackpack()) {
                items.add(ModItems.BACKPACKS.get(color));
            }
        }
        return items;
    }

    private static List<RegistryObject<Item>> modifications() {
        List<RegistryObject<Item>> items = new ArrayList<>();
        items.add(ModItems.CIGARETTE);
        items.add(ModItems.POUCH);
        items.add(ModItems.ARMOR_PLATE);
        return items;
    }

    private static final class Folder {
        private final Component label;
        private final RegistryObject<Item>[] items;
        private boolean open = true;

        private Folder(Component label, List<RegistryObject<Item>> items) {
            this.label = label;
            @SuppressWarnings("unchecked")
            RegistryObject<Item>[] array = items.toArray(RegistryObject[]::new);
            this.items = array;
        }
    }
}
