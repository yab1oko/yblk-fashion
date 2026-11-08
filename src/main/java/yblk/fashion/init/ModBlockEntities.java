package yblk.fashion.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yblk.fashion.Fashion;
import yblk.fashion.block.entity.SewingStationBlockEntity;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Fashion.MODID);

    public static final RegistryObject<BlockEntityType<SewingStationBlockEntity>> SEWING_STATION =
            BLOCK_ENTITIES.register("sewing_station",
                    () -> BlockEntityType.Builder.of(SewingStationBlockEntity::new, ModBlocks.SEWING_STATION.get())
                            .build(null));

    private ModBlockEntities() {
    }
}
