package yblk.fashion.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import yblk.fashion.Fashion;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.BalaclavaItem;
import yblk.fashion.item.BandanaItem;
import yblk.fashion.item.GasMaskItem;
import yblk.fashion.item.GlassesItem;
import yblk.fashion.item.NvgItem;
import yblk.fashion.item.ClothingFabric;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.PortableBagItem;
import yblk.fashion.item.TShirtColor;
import yblk.fashion.menu.SewingStationMenu;
import yblk.fashion.sewing.SewingCategory;
import yblk.fashion.sewing.SewingProduct;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Custom top panel from sewing_station.png (256 content + 3px frame = 262);
 * texture file is 512 with the GUI at 0,0. Vanilla inventory strip below.
 */
public class SewingStationScreen extends AbstractContainerScreen<SewingStationMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Fashion.MODID, "textures/gui/sewing_station.png");
    private static final ResourceLocation INVENTORY_TEXTURE =
            new ResourceLocation("textures/gui/container/inventory.png");
    private static final ResourceLocation DYE_SLOT_GHOST =
            new ResourceLocation(Fashion.MODID, "textures/gui/dye_slot.png");
    /** Vanilla empty-armor ghosts + fashion curios ghost (same gray look). */
    private static final ResourceLocation CAT_ICON_HEAD =
            new ResourceLocation("textures/item/empty_armor_slot_helmet.png");
    private static final ResourceLocation CAT_ICON_CHEST =
            new ResourceLocation("textures/item/empty_armor_slot_chestplate.png");
    private static final ResourceLocation CAT_ICON_LEGS =
            new ResourceLocation("textures/item/empty_armor_slot_leggings.png");
    private static final ResourceLocation CAT_ICON_FEET =
            new ResourceLocation("textures/item/empty_armor_slot_boots.png");
    private static final ResourceLocation CAT_ICON_CURIOS =
            new ResourceLocation(Fashion.MODID, "textures/gui/empty_curios_slot.png");

    /** GUI atlas size (full frame sits at 0,0 inside this). */
    private static final int TEX_SIZE = 512;

    private static final int PREV_X = 15, PREV_Y = 16, ARROW_W = 12, ARROW_H = 14;
    private static final int NEXT_X = 121, NEXT_Y = 16;

    private static final int WELL_X = 26, WELL_Y = 32, WELL_W = 96, WELL_H = 58;
    private static final int PREVIEW_CX = 74, PREVIEW_CY = 86, PREVIEW_SCALE = 28;

    private static final int SEW_X = SewingStationMenu.SEW_X;
    private static final int SEW_Y = 21;
    private static final int SEW_W = SewingStationMenu.SEW_W;
    private static final int SEW_H = 18;

    private static final int MAT_SLOT_Y = SewingStationMenu.MAT_SLOT_Y;
    private static final int MAT_SLOT_SIZE = SewingStationMenu.MAT_SLOT_SIZE;
    private static final int MAT_SLOT_STRIDE = SewingStationMenu.MAT_SLOT_STRIDE;
    /** Clear only under the Sew panel — never carve into the preview well. */
    private static final int MAT_CLEAR_X0 = SEW_X - 2;
    private static final int MAT_CLEAR_Y0 = 52;
    private static final int MAT_CLEAR_X1 = SEW_X + SEW_W + 2;
    private static final int MAT_CLEAR_Y1 = 76;

    /**
     * Left category rail — flush to the panel, 18×18 like inventory slots,
     * vertically centered on the custom top panel.
     */
    private static final int CAT_SIZE = 18;
    private static final int CAT_GAP = 3;
    private static final int CAT_X = -CAT_SIZE;
    private static final int CAT_Y0 =
            (SewingStationMenu.TOP_HEIGHT - (SewingCategory.values().length * CAT_SIZE
                    + (SewingCategory.values().length - 1) * CAT_GAP)) / 2;

    /** Slots stripped for a clean mannequin (armor + hands). */
    private static final EquipmentSlot[] PREVIEW_CLEAR = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND
    };

    private final Map<EquipmentSlot, ItemStack> previewSaved = new EnumMap<>(EquipmentSlot.class);
    private final List<CurioSnap> previewCuriosSaved = new ArrayList<>();
    private boolean hoverFabric;
    private boolean hoverKit;
    private boolean hoverLeather;
    private boolean hoverPlates;
    private boolean hoverWheat;
    private boolean hoverLeaves;

    /** One Curios stack restored after the mannequin frame. */
    private record CurioSnap(String slotId, int index, ItemStack stack) {
    }

    public SewingStationScreen(SewingStationMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = SewingStationMenu.GUI_WIDTH;
        this.imageHeight = SewingStationMenu.TOP_HEIGHT + 82;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 11;
        this.titleLabelY = 5;
        this.inventoryLabelX = SewingStationMenu.INV_ORIGIN_X + 8;
        this.inventoryLabelY = SewingStationMenu.INV_ORIGIN_Y - 11;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        hoverFabric = false;
        hoverKit = false;
        hoverLeather = false;
        hoverPlates = false;
        hoverWheat = false;
        hoverLeaves = false;
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY);

        if (hoverFabric) {
            SewingProduct product = menu.getSelectedProduct();
            g.renderTooltip(this.font, ClothingFabric.sewingFabricIcon(product.fabricCount()), mouseX, mouseY);
        }
        if (hoverKit) {
            ItemStack kit = ClothingFabric.sewingKitIcon(menu.getSelectedProduct().effectiveSewingKitCost());
            if (!kit.isEmpty()) {
                g.renderTooltip(this.font, kit, mouseX, mouseY);
            }
        }
        if (hoverLeather) {
            SewingProduct product = menu.getSelectedProduct();
            g.renderTooltip(this.font, new ItemStack(Items.LEATHER, product.leatherCount()), mouseX, mouseY);
        }
        if (hoverPlates) {
            SewingProduct product = menu.getSelectedProduct();
            g.renderTooltip(this.font, ClothingFabric.sewingPlateIcon(product.plateCount()), mouseX, mouseY);
        }
        if (hoverWheat) {
            SewingProduct product = menu.getSelectedProduct();
            g.renderTooltip(this.font, new ItemStack(Items.WHEAT, product.wheatCount()), mouseX, mouseY);
        }
        if (hoverLeaves) {
            SewingProduct product = menu.getSelectedProduct();
            g.renderTooltip(this.font, new ItemStack(Items.OAK_LEAVES, product.leavesCount()), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        g.blit(TEXTURE, leftPos, topPos, 0, 0,
                imageWidth, SewingStationMenu.TOP_HEIGHT, TEX_SIZE, TEX_SIZE);

        // Preview well shifted up from the baked texture rect (y=34).
        int wx0 = leftPos + WELL_X;
        int wy0 = topPos + WELL_Y;
        int wx1 = wx0 + WELL_W;
        int wy1 = wy0 + WELL_H;
        // Clear baked well + old borders so nothing peeks below.
        g.fill(wx0 - 2, topPos + 31, wx1 + 2, topPos + 94, 0xFFC6C6C6);
        g.fill(wx0 - 1, wy0 - 1, wx1 + 1, wy1 + 1, 0xFF373737); // border
        g.fill(wx0, wy0, wx1, wy1, 0xFF1E1E22);                 // well
        // 1px gap under the well frame, then a 55,55,55 strip (+1px L/R vs well border).
        int stripY = wy1 + 2;
        g.fill(wx0 - 2, stripY, wx1 + 2, stripY + 1, 0xFF373737);

        g.blit(INVENTORY_TEXTURE,
                leftPos + SewingStationMenu.INV_ORIGIN_X,
                topPos + SewingStationMenu.INV_ORIGIN_Y,
                0, 84, 176, 82, 256, 256);

        SewingProduct product = menu.getSelectedProduct();
        TShirtColor dye = menu.dyeInSlot();

        // Baked arrows sit at y=17; redraw 1px higher.
        g.fill(leftPos + PREV_X, topPos + 16, leftPos + PREV_X + ARROW_W, topPos + 31, 0xFFC6C6C6);
        g.fill(leftPos + NEXT_X, topPos + 16, leftPos + NEXT_X + ARROW_W, topPos + 31, 0xFFC6C6C6);
        g.blit(TEXTURE, leftPos + PREV_X, topPos + PREV_Y, 15, 17, ARROW_W, ARROW_H, TEX_SIZE, TEX_SIZE);
        g.blit(TEXTURE, leftPos + NEXT_X, topPos + NEXT_Y, 121, 17, ARROW_W, ARROW_H, TEX_SIZE, TEX_SIZE);

        if (hover(mouseX, mouseY, PREV_X, PREV_Y, ARROW_W, ARROW_H)) {
            g.fill(leftPos + PREV_X, topPos + PREV_Y,
                    leftPos + PREV_X + ARROW_W, topPos + PREV_Y + ARROW_H, 0x40FFFFFF);
        }
        if (hover(mouseX, mouseY, NEXT_X, NEXT_Y, ARROW_W, ARROW_H)) {
            g.fill(leftPos + NEXT_X, topPos + NEXT_Y,
                    leftPos + NEXT_X + ARROW_W, topPos + NEXT_Y + ARROW_H, 0x40FFFFFF);
        }

        List<Integer> inCat = menu.getSelectedCategory().productIndices();
        String productName = inCat.isEmpty()
                ? menu.getSelectedCategory().title().getString()
                : product.title().getString();
        g.drawString(this.font, productName, leftPos + 74 - this.font.width(productName) / 2, topPos + 20, 0x404040, false);

        renderCategoryRail(g, mouseX, mouseY);
        renderSewButton(g, mouseX, mouseY);
        renderMaterialRequirements(g, mouseX, mouseY, product);
        renderDyeSlotGhost(g);
        renderPlayerPreview(g, mouseX, mouseY, dye);
    }

    private void renderCategoryRail(GuiGraphics g, int mouseX, int mouseY) {
        SewingCategory selected = menu.getSelectedCategory();
        SewingCategory[] cats = SewingCategory.values();
        for (int i = 0; i < cats.length; i++) {
            SewingCategory cat = cats[i];
            int x = leftPos + CAT_X;
            int y = topPos + CAT_Y0 + i * (CAT_SIZE + CAT_GAP);
            boolean over = mouseX >= x && mouseX < x + CAT_SIZE && mouseY >= y && mouseY < y + CAT_SIZE;
            boolean on = cat == selected;
            boolean empty = cat.productIndices().isEmpty();

            // Vanilla inventory slot chrome (18×18).
            g.blit(INVENTORY_TEXTURE, x, y, 7, 83, CAT_SIZE, CAT_SIZE, 256, 256);
            if (on) {
                g.fill(x + 1, y + 1, x + CAT_SIZE - 1, y + CAT_SIZE - 1, 0x665A8A3A);
            } else if (over) {
                g.fill(x + 1, y + 1, x + CAT_SIZE - 1, y + CAT_SIZE - 1, 0x40FFFFFF);
            }

            ResourceLocation icon = categoryGhostIcon(cat);
            g.blit(icon, x + 1, y + 1, 0, 0, 16, 16, 16, 16);
            // Dim empty categories, but never curios — its gold ring must stay readable.
            if (empty && cat != SewingCategory.CURIOS) {
                g.fill(x + 1, y + 1, x + CAT_SIZE - 1, y + CAT_SIZE - 1, 0x66000000);
            }
        }
    }

    private static ResourceLocation categoryGhostIcon(SewingCategory cat) {
        return switch (cat) {
            case HEAD -> CAT_ICON_HEAD;
            case CHEST -> CAT_ICON_CHEST;
            case LEGS -> CAT_ICON_LEGS;
            case FEET -> CAT_ICON_FEET;
            case CURIOS -> CAT_ICON_CURIOS;
        };
    }

    private void renderDyeSlotGhost(GuiGraphics g) {
        int chromeX = leftPos + SewingStationMenu.DYE_SLOT_X;
        int chromeY = topPos + SewingStationMenu.DYE_SLOT_Y;
        // Same 18×18 inventory slot chrome as fabric / leather / kit.
        g.blit(INVENTORY_TEXTURE, chromeX, chromeY, 7, 83, MAT_SLOT_SIZE, MAT_SLOT_SIZE, 256, 256);
        if (menu.getSlot(0).getItem().isEmpty()) {
            g.blit(DYE_SLOT_GHOST, chromeX + 1, chromeY + 1, 0, 0, 16, 16, 16, 16);
        }
    }

    private void renderSewButton(GuiGraphics g, int mouseX, int mouseY) {
        boolean can = menu.canSew();
        boolean over = hover(mouseX, mouseY, SEW_X, SEW_Y, SEW_W, SEW_H);
        int x0 = leftPos + SEW_X;
        int y0 = topPos + SEW_Y;
        int x1 = x0 + SEW_W;
        int y1 = y0 + SEW_H;

        int face = can ? (over ? 0xFF6EAC48 : 0xFF5A8A3A) : 0xFF6B6B6B;
        int hi = can ? 0xFFA0D070 : 0xFF9A9A9A;
        int lo = can ? 0xFF3A5C28 : 0xFF3A3A3A;

        g.fill(x0, y0, x1, y1, face);
        g.fill(x0, y0, x1, y0 + 1, hi);
        g.fill(x0, y0, x0 + 1, y1, hi);
        g.fill(x0, y1 - 1, x1, y1, lo);
        g.fill(x1 - 1, y0, x1, y1, lo);

        g.drawCenteredString(this.font, Component.translatable("screen.fashion.sewing_station.sew"),
                x0 + SEW_W / 2, y0 + 5, 0xFFFFFF);
    }

    /**
     * Material ghosts to the right of dye under Sew.
     * Order L→R: dye, fabric, kit?, leather?, plates?, wheat?, leaves?.
     */
    private void renderMaterialRequirements(GuiGraphics g, int mouseX, int mouseY, SewingProduct product) {
        g.fill(leftPos + MAT_CLEAR_X0, topPos + MAT_CLEAR_Y0,
                leftPos + MAT_CLEAR_X1, topPos + MAT_CLEAR_Y1, 0xFFC6C6C6);

        int[] xs = menu.materialSlotXs();
        int fabricX = xs[0];
        int kitX = xs[1];
        int leatherX = xs[2];
        int plateX = xs[3];
        int wheatX = xs[4];
        int leavesX = xs[5];

        if (fabricX >= 0) {
            renderRequirementSlot(g, mouseX, mouseY, fabricX, MAT_SLOT_Y,
                    ClothingFabric.sewingFabricIcon(1), product.fabricCount(), menu.countFabric(),
                    () -> hoverFabric = true);
        }
        if (kitX >= 0) {
            int kitCost = product.effectiveSewingKitCost();
            renderRequirementSlot(g, mouseX, mouseY, kitX, MAT_SLOT_Y,
                    ClothingFabric.sewingKitIcon(kitCost), kitCost, menu.countSewingKits(),
                    () -> hoverKit = true);
        }
        if (leatherX >= 0) {
            renderRequirementSlot(g, mouseX, mouseY, leatherX, MAT_SLOT_Y,
                    new ItemStack(Items.LEATHER), product.leatherCount(), menu.countLeather(),
                    () -> hoverLeather = true);
        }
        if (plateX >= 0) {
            renderRequirementSlot(g, mouseX, mouseY, plateX, MAT_SLOT_Y,
                    ClothingFabric.sewingPlateIcon(1), product.plateCount(), menu.countPlates(),
                    () -> hoverPlates = true);
        }
        if (wheatX >= 0) {
            renderRequirementSlot(g, mouseX, mouseY, wheatX, MAT_SLOT_Y,
                    new ItemStack(Items.WHEAT), product.wheatCount(), menu.countWheat(),
                    () -> hoverWheat = true);
        }
        if (leavesX >= 0) {
            renderRequirementSlot(g, mouseX, mouseY, leavesX, MAT_SLOT_Y,
                    new ItemStack(Items.OAK_LEAVES), product.leavesCount(), menu.countLeaves(),
                    () -> hoverLeaves = true);
        }
    }

    private void renderRequirementSlot(
            GuiGraphics g,
            int mouseX,
            int mouseY,
            int slotX,
            int slotY,
            ItemStack icon,
            int need,
            int have,
            Runnable onHover) {
        int x = leftPos + slotX;
        int y = topPos + slotY;

        // Vanilla inventory slot chrome (18×18).
        g.blit(INVENTORY_TEXTURE, x, y, 7, 83, MAT_SLOT_SIZE, MAT_SLOT_SIZE, 256, 256);
        g.renderItem(icon, x + 1, y + 1);

        String count = String.valueOf(need);
        int color = have < need ? 0xFF5555 : 0xFFFFFF;
        g.pose().pushPose();
        g.pose().translate(0.0F, 0.0F, 200.0F);
        g.drawString(this.font, count, x + 1 + 19 - this.font.width(count), y + 1 + 9, color, true);
        g.pose().popPose();

        if (mouseX >= x && mouseX < x + MAT_SLOT_SIZE && mouseY >= y && mouseY < y + MAT_SLOT_SIZE) {
            onHover.run();
        }
    }

    /**
     * Naked mannequin: strip armor/hands + Curios, then equip only the craft result
     * (vanilla slot or Curios outerwear/mask). Always shows a valid preview color.
     */
    private void renderPlayerPreview(GuiGraphics g, int mouseX, int mouseY, TShirtColor dye) {
        if (minecraft == null || minecraft.player == null) {
            return;
        }
        LocalPlayer player = minecraft.player;
        SewingProduct product = menu.getSelectedProduct();
        ItemStack preview = product.previewStack(dye);
        boolean backView = "backpack".equals(product.id()) || "sports_bag".equals(product.id());

        previewSaved.clear();
        for (EquipmentSlot slot : PREVIEW_CLEAR) {
            previewSaved.put(slot, player.getItemBySlot(slot));
            player.setItemSlot(slot, ItemStack.EMPTY);
        }
        saveAndClearCurios(player);
        float savedYBody = player.yBodyRot;
        float savedYRot = player.getYRot();
        float savedYHead = player.yHeadRot;
        float savedYHeadO = player.yHeadRotO;
        float savedXRot = player.getXRot();
        try {
            equipPreview(player, preview);

            int x0 = leftPos + WELL_X;
            int y0 = topPos + WELL_Y;
            int y1 = y0 + WELL_H;
            g.enableScissor(x0, y0, x0 + WELL_W, y1);
            if (backView) {
                // FollowsMouse always forces yBodyRot=180 (face camera). Bags sit on the
                // back — render with body yaw 0 so the camera sees the bag.
                player.yBodyRot = 0.0F;
                player.setYRot(0.0F);
                player.setXRot(0.0F);
                player.yHeadRot = 0.0F;
                player.yHeadRotO = 0.0F;
                Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
                InventoryScreen.renderEntityInInventory(
                        g,
                        leftPos + PREVIEW_CX,
                        topPos + PREVIEW_CY,
                        PREVIEW_SCALE,
                        pose,
                        null,
                        player);
            } else {
                InventoryScreen.renderEntityInInventoryFollowsMouse(
                        g,
                        leftPos + PREVIEW_CX,
                        topPos + PREVIEW_CY,
                        PREVIEW_SCALE,
                        (float) (leftPos + PREVIEW_CX - mouseX),
                        (float) (topPos + PREVIEW_CY - 50 - mouseY),
                        player);
            }
            g.disableScissor();
        } finally {
            player.yBodyRot = savedYBody;
            player.setYRot(savedYRot);
            player.yHeadRot = savedYHead;
            player.yHeadRotO = savedYHeadO;
            player.setXRot(savedXRot);
            for (EquipmentSlot slot : PREVIEW_CLEAR) {
                ItemStack saved = previewSaved.get(slot);
                player.setItemSlot(slot, saved != null ? saved : ItemStack.EMPTY);
            }
            previewSaved.clear();
            restoreCurios(player);
        }
    }

    private void equipPreview(LocalPlayer player, ItemStack preview) {
        if (preview.isEmpty()) {
            return;
        }
        if (preview.getItem() instanceof BandanaItem
                || preview.getItem() instanceof BalaclavaItem
                || preview.getItem() instanceof NvgItem
                || preview.getItem() instanceof GasMaskItem
                || preview.getItem() instanceof GlassesItem) {
            setCurio(player, BandanaItem.CURIOS_SLOT, 0, preview);
            return;
        }
        if (preview.getItem() instanceof PortableBagItem) {
            setCurio(player, PortableBagItem.CURIOS_SLOT, 0, preview);
            return;
        }
        if (preview.getItem() instanceof ClothingItem clothing && clothing.getClothingType().isOuterwear()) {
            setCurio(player, CuriosClothing.OUTERWEAR_SLOT, 0, preview);
            return;
        }
        if (preview.getItem() instanceof ClothingItem clothing && clothing.getClothingType().isVest()) {
            setCurio(player, CuriosClothing.VEST_SLOT, 0, preview);
            return;
        }
        EquipmentSlot wearSlot = EquipmentSlot.CHEST;
        if (preview.getItem() instanceof ArmorItem armor) {
            wearSlot = armor.getType().getSlot();
        }
        player.setItemSlot(wearSlot, preview);
    }

    private void saveAndClearCurios(LocalPlayer player) {
        previewCuriosSaved.clear();
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
            for (Map.Entry<String, ICurioStacksHandler> entry : inv.getCurios().entrySet()) {
                String id = entry.getKey();
                IDynamicStackHandler stacks = entry.getValue().getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        previewCuriosSaved.add(new CurioSnap(id, i, stack.copy()));
                    }
                    inv.setEquippedCurio(id, i, ItemStack.EMPTY);
                }
            }
        });
    }

    private void restoreCurios(LocalPlayer player) {
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
            // Clear whatever the preview put in, then restore snaps.
            for (Map.Entry<String, ICurioStacksHandler> entry : inv.getCurios().entrySet()) {
                IDynamicStackHandler stacks = entry.getValue().getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    inv.setEquippedCurio(entry.getKey(), i, ItemStack.EMPTY);
                }
            }
            for (CurioSnap snap : previewCuriosSaved) {
                inv.setEquippedCurio(snap.slotId(), snap.index(), snap.stack());
            }
        });
        previewCuriosSaved.clear();
    }

    private static void setCurio(LocalPlayer player, String slotId, int index, ItemStack stack) {
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> inv.setEquippedCurio(slotId, index, stack));
    }

    private boolean hover(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + w
                && mouseY >= topPos + y && mouseY < topPos + y + h;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && minecraft != null && minecraft.gameMode != null) {
            SewingCategory[] cats = SewingCategory.values();
            for (int i = 0; i < cats.length; i++) {
                int x = CAT_X;
                int y = CAT_Y0 + i * (CAT_SIZE + CAT_GAP);
                if (hover((int) mouseX, (int) mouseY, x, y, CAT_SIZE, CAT_SIZE)) {
                    minecraft.gameMode.handleInventoryButtonClick(
                            menu.containerId, SewingStationMenu.BTN_CATEGORY_BASE + i);
                    return true;
                }
            }
            if (hover((int) mouseX, (int) mouseY, PREV_X, PREV_Y, ARROW_W, ARROW_H)) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, SewingStationMenu.BTN_PREV);
                return true;
            }
            if (hover((int) mouseX, (int) mouseY, NEXT_X, NEXT_Y, ARROW_W, ARROW_H)) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, SewingStationMenu.BTN_NEXT);
                return true;
            }
            if (hover((int) mouseX, (int) mouseY, SEW_X, SEW_Y, SEW_W, SEW_H)) {
                if (menu.canSew()) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, SewingStationMenu.BTN_SEW);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderTooltip(GuiGraphics g, int mouseX, int mouseY) {
        super.renderTooltip(g, mouseX, mouseY);
        SewingCategory[] cats = SewingCategory.values();
        for (int i = 0; i < cats.length; i++) {
            int x = leftPos + CAT_X;
            int y = topPos + CAT_Y0 + i * (CAT_SIZE + CAT_GAP);
            if (mouseX >= x && mouseX < x + CAT_SIZE && mouseY >= y && mouseY < y + CAT_SIZE) {
                g.renderTooltip(this.font, cats[i].title(), mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        g.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}
