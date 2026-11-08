package yblk.fashion.block;

import net.minecraft.util.StringRepresentable;

/** Two-block sewing table: MAIN holds the BE; SIDE is the machine half. */
public enum SewingStationPart implements StringRepresentable {
    MAIN("main"),
    SIDE("side");

    private final String name;

    SewingStationPart(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
