package thaumcraft.api.crafting;

import net.minecraft.world.World;

/**
 * @author Azanor
 * <p>
 * Blocks that implement this interface act as infusion crafting stabilisers like candles and skulls
 */
public interface IInfusionStabiliser {

    /**
     * returns true if the block can stabilise things
     */
    boolean canStabaliseInfusion(World world, int x, int y, int z);

}
