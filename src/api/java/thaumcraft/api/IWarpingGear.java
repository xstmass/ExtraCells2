package thaumcraft.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author Azanor
 * <p>
 * Armor, held items or bauble slot items that implement this interface add warp when equipped or held.
 */

public interface IWarpingGear {

    /**
     * returns how much warp this item adds while worn or held.
     */
    int getWarp(ItemStack itemstack, EntityPlayer player);


}
