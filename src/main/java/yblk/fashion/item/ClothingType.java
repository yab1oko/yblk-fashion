package yblk.fashion.item;

/**
 * Clothing cover flags — which 2nd skin layers to hide while worn.
 * Outerwear (hoodie / jacket / coat) equips in Curios {@code outerwear}, not CHEST.
 */
public enum ClothingType {
    /** Short sleeves — hide jacket & sleeves; redraw forearm below hem. */
    TSHIRT(true, true, false, false, 4.0F),
    /**
     * Tank top — CHEST; hide jacket outer (keep sleeves); body inflate like
     * t-shirt; PNG alpha = straps / neckline / armholes over base skin.
     */
    TANK_TOP(true, false, false, false, 0.0F),
    /**
     * Plate carrier — Curios {@code vest}; leave jacket/sleeves on; PNG alpha cut;
     * +5 armor, 400 dur, Features +3 slots.
     */
    VEST(false, false, false, false, 0.0F),
    /** Long sleeves + hood — hide jacket & sleeves; G toggles hood open/closed. */
    HOODIE(true, true, false, false, 11.0F),
    /** Long sleeves + hood; slightly thicker mesh than hoodie. */
    JACKET(true, true, false, false, 11.0F),
    /**
     * Long coat — Curios outerwear; solid body cube, open front = texture alpha slit.
     * Hides jacket + sleeves + pants; placket redraws outer skin in the slit,
     * wrists + legs below flaps redrawn ({@code CoatPlacketLayer} skips if a
     * chest shirt already covers the torso).
     */
    COAT(true, true, true, false, 10.0F),
    /** Legs only — hide pant outer layers; redraw calves below hem. */
    PANTS(false, false, true, false, 0.0F),
    /** Short legs — hide pant outer; redraw calves from shorts hem. */
    SHORTS(false, false, true, false, 0.0F),
    /** Feet — hide pant outer layers; redraw shin outer above the shoe. */
    SNEAKERS(false, false, true, false, 0.0F),
    /** Leather shoes — same cut as sneakers; shin redraw above cuff. */
    SHOES(false, false, true, false, 0.0F),
    /**
     * Hat — hide skin hat layer; {@code HatOuterLayer} redraws face/bangs below hem.
     * Inflate can stay ≤ 0.5 for a tight shell (no occlusion cut).
     */
    HAT(false, false, false, true, 0.0F),
    /** Leather cowboy hat — same hideHat + HatOuter cut as beanie. */
    COWBOY_HAT(false, false, false, true, 0.0F),
    /** Straw hat — wide brim; hideHat + HatOuter below brim. */
    STRAW_HAT(false, false, false, true, 0.0F),
    /**
     * Cap — leave skin hat layer on; crown inflate &gt; 0.5 cuts by occlusion.
     */
    CAP(false, false, false, false, 0.0F),
    /**
     * Tactical helmet — HELMET slot; leave hat outer on; shell inflate &gt; 0.5
     * cuts by occlusion + PNG alpha; no Features slots. Hood uses over-hat inflate.
     */
    HELMET(false, false, false, false, 0.0F),
    /**
     * Scrap helmet — HELMET slot; leave hat on; shell inflate like tactical;
     * face plate = detail deform; PNG alpha cut (no HatOuter).
     */
    SCRAP_HELMET(false, false, false, false, 0.0F),
    /**
     * Scrap chestplate — Curios {@code vest}; leave jacket/sleeves on;
     * body+arms inflate over outerwear; PNG alpha cut (no Outer redraw).
     */
    SCRAP_VEST(false, false, false, false, 0.0F),
    /**
     * Scrap pants — Curios {@code pants}; leave pant outer on;
     * leg inflate over cloth pants; PNG alpha cut (no Calf redraw).
     */
    SCRAP_PANTS(false, false, false, false, 0.0F),
    /** Ghillie hood — HELMET; full head shell + flaps; biome foliage tint. */
    GHILLIE_HOOD(false, false, false, true, 0.0F),
    /** Ghillie top — CHEST body+arms; biome foliage tint. */
    GHILLIE_TOP(true, true, false, false, 12.0F),
    /** Ghillie pants — LEGS; biome foliage tint. */
    GHILLIE_PANTS(false, false, true, false, 0.0F),
    /** Ghillie boots — FEET; biome foliage tint. */
    GHILLIE_BOOTS(false, false, true, false, 0.0F);

    public final boolean hideJacket;
    public final boolean hideSleeves;
    public final boolean hidePants;
    public final boolean hideHat;
    /** Arm-local sleeve length from y=-2 (vanilla arm top). Unused for legs/feet/hat. */
    public final float sleeveHeight;

    ClothingType(boolean hideJacket, boolean hideSleeves, boolean hidePants, boolean hideHat, float sleeveHeight) {
        this.hideJacket = hideJacket;
        this.hideSleeves = hideSleeves;
        this.hidePants = hidePants;
        this.hideHat = hideHat;
        this.sleeveHeight = sleeveHeight;
    }

    /** Arm-local Y where fabric ends and skin 2nd-layer may show again. */
    public float sleeveHemY() {
        return -2.0F + sleeveHeight;
    }

    public boolean hasHood() {
        return this == HOODIE || this == JACKET;
    }

    /** Curios outerwear slot — worn over a CHEST t-shirt. */
    public boolean isOuterwear() {
        return this == HOODIE || this == JACKET || this == COAT;
    }

    /** Curios vest (plate carrier) slot — armor over chest clothing. */
    public boolean isVest() {
        return this == VEST || this == SCRAP_VEST;
    }

    /** Curios pants slot — scrap armor legs over cloth pants. */
    public boolean isCurioPants() {
        return this == SCRAP_PANTS;
    }

    /** Equips via Curios (not vanilla armor slots). */
    public boolean isCurioWear() {
        return isOuterwear() || isVest() || isCurioPants();
    }

    public boolean isLeggings() {
        return this == PANTS || this == SHORTS || this == GHILLIE_PANTS;
    }

    public boolean isGhillie() {
        return this == GHILLIE_HOOD || this == GHILLIE_TOP || this == GHILLIE_PANTS || this == GHILLIE_BOOTS;
    }

    public boolean isBoots() {
        return this == SNEAKERS || this == SHOES || this == GHILLIE_BOOTS;
    }

    public boolean isHelmet() {
        return this == HAT || this == CAP || this == COWBOY_HAT || this == STRAW_HAT
                || this == HELMET || this == SCRAP_HELMET || this == GHILLIE_HOOD;
    }

    /**
     * Full face-plate helmets — hide Curios masks / NVG / cigarette while worn.
     * (Beanies / caps / cowboy / straw still show face cosmetics.)
     */
    public boolean hidesFaceCosmetics() {
        return this == HELMET || this == SCRAP_HELMET || this == GHILLIE_HOOD;
    }

    /** CHEST undershirt (t-shirt / tank / ghillie top) — coat placket skips. */
    public boolean isChestShirt() {
        return this == TSHIRT || this == TANK_TOP || this == GHILLIE_TOP;
    }

    /**
     * Thigh pouch mod — vanilla CHEST garments or legwear (not Curios).
     * One chest + one legs worn → double pouch mesh.
     */
    public boolean acceptsPouch() {
        return isChestShirt() || isLeggings();
    }

    /** Body-side pouch — vanilla CHEST only (t-shirt / tank / ghillie top). */
    public boolean isPouchBodySlot() {
        return isChestShirt();
    }

    /** Armor plate — vanilla CHEST shirt or legwear. */
    public boolean acceptsPlate() {
        return isChestShirt() || isLeggings();
    }

    /** Curios pieces have no clothing-mod slot. */
    public boolean showsModSlot() {
        return !isCurioWear();
    }

    /** Fabric / wool / RPM cloth pieces consumed at the sewing station. */
    public int sewFabricCost() {
        return switch (this) {
            case TSHIRT -> 3;
            case TANK_TOP -> 2;
            case VEST, HELMET -> 1;
            case PANTS -> 4;
            case SHORTS -> 2;
            case HOODIE -> 5;
            case JACKET -> 8;
            case COAT -> 6;
            case SNEAKERS, CAP -> 1;
            case SHOES, COWBOY_HAT, STRAW_HAT, SCRAP_HELMET, SCRAP_VEST, SCRAP_PANTS -> 0;
            case HAT -> 2;
            case GHILLIE_HOOD -> 2;
            case GHILLIE_TOP -> 6;
            case GHILLIE_PANTS -> 4;
            case GHILLIE_BOOTS -> 2;
        };
    }

    /** Vanilla leather pieces (footwear / leather hats / coat trim). */
    public int sewLeatherCost() {
        return switch (this) {
            case SNEAKERS -> 2;
            case SHOES, COAT -> 3;
            case COWBOY_HAT -> 2;
            default -> 0;
        };
    }

    /**
     * Iron ingots or RPM {@code forged_iron_plate} for plate carriers.
     */
    public int sewPlateCost() {
        return switch (this) {
            case VEST, HELMET -> 3;
            default -> 0;
        };
    }

    /** Wheat for straw hat. */
    public int sewWheatCost() {
        return this == STRAW_HAT ? 3 : 0;
    }

    /** Leaves ({@code #minecraft:leaves}) for ghillie — matches crafting-table recipes. */
    public int sewLeavesCost() {
        return switch (this) {
            case GHILLIE_HOOD -> 7;
            case GHILLIE_TOP -> 5;
            case GHILLIE_PANTS -> 6;
            case GHILLIE_BOOTS -> 4;
            default -> 0;
        };
    }

    /** Fabric returned when uncrafting with a knife. */
    public int uncraftFabricCount() {
        return switch (this) {
            case SNEAKERS, HAT, CAP, TANK_TOP, VEST, HELMET, GHILLIE_HOOD, GHILLIE_BOOTS -> 1;
            case SHOES, COWBOY_HAT, STRAW_HAT, SCRAP_HELMET, SCRAP_VEST, SCRAP_PANTS -> 0;
            default -> sewFabricCost() - 1;
        };
    }

    /**
     * Inventory slots when Features inventory-revamp is on.
     * Bags use their own constants; hats / shoes / t-shirt grant none.
     * Ghillie set totals 9 (2+3+2+2).
     */
    public int featuresSlotBonus() {
        return switch (this) {
            case HOODIE, JACKET, COAT -> 4;
            case VEST, PANTS -> 3;
            case SHORTS -> 2;
            case GHILLIE_HOOD, GHILLIE_BOOTS -> 2;
            case GHILLIE_TOP -> 3;
            case GHILLIE_PANTS -> 2;
            default -> 0;
        };
    }
}
