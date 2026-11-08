package yblk.fashion.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import yblk.fashion.init.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fashion clothing modifications on garment NBT.
 * Slot count is 1 by default; sewing / loot may roll 2 ({@link #EXTRA_SLOT_CHANCE}).
 */
public final class ClothingMods {
    public static final String TAG_ROOT = "FashionMods";
    public static final String TAG_SLOTS = "Slots";
    public static final String CIGARETTE = "cigarette";
    public static final String POUCH = "pouch";
    public static final String PLATE = "plate";

    /** Chance a new garment rolls a second mod slot. */
    public static final float EXTRA_SLOT_CHANCE = 0.15F;

    private static final String[] MOD_ORDER = {CIGARETTE, POUCH, PLATE};

    private ClothingMods() {
    }

    public static boolean showsModSlot(ItemStack clothing) {
        return clothing.getItem() instanceof ClothingItem item
                && item.getClothingType().showsModSlot();
    }

    public static int slotCount(ItemStack clothing) {
        CompoundTag mods = modsTag(clothing);
        if (mods != null && mods.contains(TAG_SLOTS)) {
            return Math.max(1, Math.min(2, mods.getInt(TAG_SLOTS)));
        }
        return 1;
    }

    public static int installedCount(ItemStack clothing) {
        return installedModIds(clothing).size();
    }

    /** Tooltip lines when the garment has at least one mod installed. */
    public static void appendInstalledTooltip(ItemStack clothing, List<Component> tip) {
        List<String> ids = installedModIds(clothing);
        if (ids.isEmpty()) {
            return;
        }
        for (String id : ids) {
            tip.add(Component.translatable("item.fashion.mod.installed." + id)
                    .withStyle(ChatFormatting.GRAY));
        }
        tip.add(Component.translatable("item.fashion.mod.open_hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    public static List<String> installedModIds(ItemStack clothing) {
        List<String> out = new ArrayList<>(2);
        for (String id : MOD_ORDER) {
            if (has(clothing, id)) {
                out.add(id);
            }
        }
        return out;
    }

    public static boolean has(ItemStack clothing, String modId) {
        CompoundTag mods = modsTag(clothing);
        return mods != null && mods.getBoolean(modId);
    }

    public static boolean canAccept(ItemStack clothing, ItemStack mod) {
        if (clothing.isEmpty() || mod.isEmpty()) {
            return false;
        }
        if (!(clothing.getItem() instanceof ClothingItem item)) {
            return false;
        }
        if (installedCount(clothing) >= slotCount(clothing)) {
            return false;
        }
        ClothingType type = item.getClothingType();
        if (mod.getItem() instanceof CigaretteItem) {
            return type.isHelmet() && !has(clothing, CIGARETTE);
        }
        if (mod.getItem() instanceof PouchItem) {
            return type.acceptsPouch() && !has(clothing, POUCH);
        }
        if (mod.getItem() instanceof ArmorPlateItem) {
            return type.acceptsPlate() && !has(clothing, PLATE);
        }
        return false;
    }

    /** Mod stack for UI slot index {@code 0..slotCount-1}. */
    public static ItemStack displayMod(ItemStack clothing, int index) {
        List<String> ids = installedModIds(clothing);
        if (index < 0 || index >= ids.size()) {
            return ItemStack.EMPTY;
        }
        return stackForModId(ids.get(index));
    }

    /** @deprecated use {@link #displayMod(ItemStack, int)} */
    public static ItemStack displayMod(ItemStack clothing) {
        return displayMod(clothing, 0);
    }

    public static Optional<String> installedModId(ItemStack clothing) {
        List<String> ids = installedModIds(clothing);
        return ids.isEmpty() ? Optional.empty() : Optional.of(ids.get(0));
    }

    public static boolean apply(ItemStack clothing, ItemStack mod) {
        if (!canAccept(clothing, mod)) {
            return false;
        }
        CompoundTag root = clothing.getOrCreateTag();
        CompoundTag mods = root.contains(TAG_ROOT) ? root.getCompound(TAG_ROOT) : new CompoundTag();
        if (!mods.contains(TAG_SLOTS)) {
            mods.putInt(TAG_SLOTS, 1);
        }
        if (mod.getItem() instanceof CigaretteItem) {
            mods.putBoolean(CIGARETTE, true);
        } else if (mod.getItem() instanceof PouchItem) {
            mods.putBoolean(POUCH, true);
        } else if (mod.getItem() instanceof ArmorPlateItem) {
            mods.putBoolean(PLATE, true);
        } else {
            return false;
        }
        root.put(TAG_ROOT, mods);
        return true;
    }

    public static Optional<ItemStack> remove(ItemStack clothing, String modId) {
        if (!has(clothing, modId)) {
            return Optional.empty();
        }
        CompoundTag root = clothing.getOrCreateTag();
        CompoundTag mods = root.getCompound(TAG_ROOT);
        int slots = mods.contains(TAG_SLOTS) ? mods.getInt(TAG_SLOTS) : 1;
        mods.remove(modId);
        // Drop empty FashionMods except preserved slot count.
        boolean anyMod = false;
        for (String id : MOD_ORDER) {
            if (mods.getBoolean(id)) {
                anyMod = true;
                break;
            }
        }
        CompoundTag next = new CompoundTag();
        next.putInt(TAG_SLOTS, Math.max(1, Math.min(2, slots)));
        if (anyMod) {
            for (String id : MOD_ORDER) {
                if (mods.getBoolean(id)) {
                    next.putBoolean(id, true);
                }
            }
        }
        root.put(TAG_ROOT, next);
        return Optional.of(stackForModId(modId));
    }

    /** Remove whatever is shown in UI slot {@code index}. */
    public static Optional<ItemStack> removeAt(ItemStack clothing, int index) {
        List<String> ids = installedModIds(clothing);
        if (index < 0 || index >= ids.size()) {
            return Optional.empty();
        }
        return remove(clothing, ids.get(index));
    }

    public static void copyTo(ItemStack source, ItemStack result) {
        CompoundTag mods = modsTag(source);
        if (mods == null || mods.isEmpty()) {
            return;
        }
        result.getOrCreateTag().put(TAG_ROOT, mods.copy());
    }

    /**
     * Rolls 1 or 2 mod slots onto a freshly created garment (sewing / loot / craft).
     * No-op if already rolled or garment has no mod UI.
     */
    public static void maybeRollSlots(ItemStack clothing, RandomSource random) {
        if (!showsModSlot(clothing)) {
            return;
        }
        CompoundTag root = clothing.getOrCreateTag();
        CompoundTag mods = root.contains(TAG_ROOT) ? root.getCompound(TAG_ROOT) : new CompoundTag();
        if (mods.contains(TAG_SLOTS)) {
            return;
        }
        int slots = random.nextFloat() < EXTRA_SLOT_CHANCE ? 2 : 1;
        mods.putInt(TAG_SLOTS, slots);
        root.put(TAG_ROOT, mods);
    }

    private static ItemStack stackForModId(String modId) {
        Item item = switch (modId) {
            case CIGARETTE -> ModItems.CIGARETTE.get();
            case POUCH -> ModItems.POUCH.get();
            case PLATE -> ModItems.ARMOR_PLATE.get();
            default -> null;
        };
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static CompoundTag modsTag(ItemStack clothing) {
        if (clothing.isEmpty() || !clothing.hasTag()) {
            return null;
        }
        CompoundTag tag = clothing.getTag();
        if (tag == null || !tag.contains(TAG_ROOT)) {
            return null;
        }
        return tag.getCompound(TAG_ROOT);
    }
}
