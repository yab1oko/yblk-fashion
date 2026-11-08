package yblk.fashion.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import yblk.fashion.Fashion;
import yblk.fashion.client.ClientSetup;
import yblk.fashion.compat.CuriosClothing;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Wearable clothing. Outerwear (hoodie / jacket) equips via Curios {@code outerwear}
 * over a CHEST t-shirt; durability stays on the ItemStack.
 */
public class ClothingItem extends ArmorItem implements ICurioItem {
    public static final String TAG_HOOD_OPEN = "HoodOpen";

    /**
     * {@link Item#Item} calls {@link #initializeClient} inside {@code super(...)},
     * before instance fields are assigned — stash type for that window.
     */
    private static final ThreadLocal<ClothingType> CONSTRUCTING_TYPE = new ThreadLocal<>();

    private final ClothingType type;
    @Nullable
    private final TShirtColor color;

    public ClothingItem(Properties properties, ClothingType type) {
        this(properties, type, null);
    }

    public ClothingItem(Properties properties, ClothingType type, @Nullable TShirtColor color) {
        super(materialFor(beginConstruct(type)), armorTypeFor(type), properties.stacksTo(1));
        this.type = type;
        this.color = color;
        CONSTRUCTING_TYPE.remove();
    }

    private static ClothingType beginConstruct(ClothingType type) {
        CONSTRUCTING_TYPE.set(type);
        return type;
    }

    private static ArmorMaterial materialFor(ClothingType type) {
        return switch (type) {
            case TSHIRT -> ModArmorMaterials.CLOTH;
            case TANK_TOP -> ModArmorMaterials.TANK;
            case VEST -> ModArmorMaterials.VEST;
            case HELMET -> ModArmorMaterials.HELMET;
            case SCRAP_HELMET, SCRAP_VEST, SCRAP_PANTS -> ModArmorMaterials.SCRAP;
            case HOODIE -> ModArmorMaterials.HOODIE;
            case JACKET -> ModArmorMaterials.JACKET;
            case COAT -> ModArmorMaterials.COAT;
            case PANTS -> ModArmorMaterials.PANTS;
            case SHORTS -> ModArmorMaterials.SHORTS;
            case SNEAKERS -> ModArmorMaterials.SNEAKERS;
            case SHOES -> ModArmorMaterials.SHOES;
            case HAT, CAP, COWBOY_HAT, STRAW_HAT -> ModArmorMaterials.HAT;
            case GHILLIE_HOOD, GHILLIE_TOP, GHILLIE_PANTS, GHILLIE_BOOTS -> ModArmorMaterials.GHILLIE;
        };
    }

    private static Type armorTypeFor(ClothingType type) {
        if (type.isHelmet()) {
            return Type.HELMET;
        }
        if (type.isBoots()) {
            return Type.BOOTS;
        }
        if (type.isLeggings() || type.isCurioPants()) {
            return Type.LEGGINGS;
        }
        return Type.CHESTPLATE;
    }

    public ClothingType getClothingType() {
        return type != null ? type : CONSTRUCTING_TYPE.get();
    }

    @Nullable
    public TShirtColor getTShirtColor() {
        return color;
    }

    /** Dye/color variant shared by all clothing. */
    @Nullable
    public TShirtColor getColor() {
        return color;
    }

    public static boolean isHoodOpen(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_HOOD_OPEN);
    }

    public static void setHoodOpen(ItemStack stack, boolean open) {
        stack.getOrCreateTag().putBoolean(TAG_HOOD_OPEN, open);
    }

    public static void toggleHood(ItemStack stack) {
        setHoodOpen(stack, !isHoodOpen(stack));
    }

    /** Outerwear / vest never occupy vanilla CHEST — that slot stays for shirts. */
    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        if (type.isCurioWear()) {
            return false;
        }
        return super.canEquip(stack, armorType, entity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (type.isCurioWear()) {
            // Curios handles right-click equip via {@link #canEquipFromUse}.
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public boolean hasCurioCapability(ItemStack stack) {
        return type.isCurioWear();
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (type.isOuterwear()) {
            return CuriosClothing.OUTERWEAR_SLOT.equals(slotContext.identifier());
        }
        if (type.isVest()) {
            return CuriosClothing.VEST_SLOT.equals(slotContext.identifier());
        }
        if (type.isCurioPants()) {
            return CuriosClothing.PANTS_SLOT.equals(slotContext.identifier());
        }
        return false;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return type.isCurioWear();
    }

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(this.getMaterial().getEquipSound(), 1.0F, 1.0F);
    }

    /**
     * Outerwear / vest / pants sit in Curios — vanilla armor attributes only apply to
     * {@link EquipmentSlot}. Mirror defense onto the matching Curios slot.
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext, UUID uuid, ItemStack stack) {
        boolean slotOk = (type.isOuterwear() && CuriosClothing.OUTERWEAR_SLOT.equals(slotContext.identifier()))
                || (type.isVest() && CuriosClothing.VEST_SLOT.equals(slotContext.identifier()))
                || (type.isCurioPants() && CuriosClothing.PANTS_SLOT.equals(slotContext.identifier()));
        if (!slotOk) {
            return ImmutableMultimap.of();
        }
        int defense = this.getDefense();
        float toughness = this.getMaterial().getToughness();
        float knockback = this.getMaterial().getKnockbackResistance();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        if (defense != 0) {
            builder.put(
                    Attributes.ARMOR,
                    new AttributeModifier(
                            uuid,
                            Fashion.MODID + ":curios_armor",
                            defense,
                            AttributeModifier.Operation.ADDITION));
        }
        if (toughness != 0) {
            builder.put(
                    Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(
                            uuid,
                            Fashion.MODID + ":curios_toughness",
                            toughness,
                            AttributeModifier.Operation.ADDITION));
        }
        if (knockback != 0) {
            builder.put(
                    Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(
                            uuid,
                            Fashion.MODID + ":curios_knockback",
                            knockback,
                            AttributeModifier.Operation.ADDITION));
        }
        return builder.build();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSetup.initClothingClient(this, consumer));
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if ("overlay".equals(type)) {
            return null;
        }
        if (this.type == ClothingType.TANK_TOP && color != null) {
            return Fashion.MODID + ":" + color.tankTopModelTexturePath();
        }
        if (this.type == ClothingType.VEST && color != null) {
            return Fashion.MODID + ":" + color.vestModelTexturePath();
        }
        if (this.type == ClothingType.HELMET) {
            return Fashion.MODID + ":textures/models/helmet.png";
        }
        if (this.type == ClothingType.SCRAP_HELMET
                || this.type == ClothingType.SCRAP_VEST
                || this.type == ClothingType.SCRAP_PANTS) {
            return Fashion.MODID + ":textures/models/scrap_armor.png";
        }
        if (this.type == ClothingType.HOODIE && color != null) {
            return Fashion.MODID + ":" + color.hoodieModelTexturePath();
        }
        if (this.type == ClothingType.JACKET && color != null) {
            return Fashion.MODID + ":" + color.jacketModelTexturePath();
        }
        if (this.type == ClothingType.COAT && color != null) {
            return Fashion.MODID + ":" + color.coatModelTexturePath();
        }
        if (this.type == ClothingType.PANTS && color != null) {
            return Fashion.MODID + ":" + color.pantsModelTexturePath();
        }
        if (this.type == ClothingType.SHORTS && color != null) {
            return Fashion.MODID + ":" + color.shortsModelTexturePath();
        }
        if (this.type == ClothingType.SNEAKERS && color != null) {
            return Fashion.MODID + ":" + color.sneakersModelTexturePath();
        }
        if (this.type == ClothingType.SHOES && color != null) {
            return Fashion.MODID + ":" + color.shoesModelTexturePath();
        }
        if (this.type == ClothingType.HAT && color != null) {
            return Fashion.MODID + ":" + color.hatModelTexturePath();
        }
        if (this.type == ClothingType.COWBOY_HAT && color != null) {
            return Fashion.MODID + ":" + color.cowboyHatModelTexturePath();
        }
        if (this.type == ClothingType.STRAW_HAT) {
            return Fashion.MODID + ":textures/models/straw_hat.png";
        }
        if (this.type == ClothingType.CAP && color != null) {
            return Fashion.MODID + ":" + color.capModelTexturePath();
        }
        if (this.type.isGhillie()) {
            return Fashion.MODID + ":textures/models/ghillie.png";
        }
        if (color != null) {
            return Fashion.MODID + ":" + color.modelTexturePath();
        }
        return Fashion.MODID + ":textures/models/red_tshirt.png";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        // Features ArmorSlotTooltip owns the "+N slots" line — avoid duplicating it here.
        if (this.type.hasHood()) {
            tip.add(Component.translatable(
                    isHoodOpen(stack) ? "item.fashion.hoodie.hood_open" : "item.fashion.hoodie.hood_closed"));
            tip.add(Component.translatable("item.fashion.hoodie.toggle_hint"));
        }
        if (ClothingMods.showsModSlot(stack) && ClothingMods.slotCount(stack) >= 2) {
            tip.add(Component.translatable("item.fashion.mod.slots_two")
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
        ClothingMods.appendInstalledTooltip(stack, tip);
        super.appendHoverText(stack, level, tip, flag);
    }
}
