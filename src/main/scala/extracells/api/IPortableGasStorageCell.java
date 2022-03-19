package extracells.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IPortableGasStorageCell extends IGasStorageCell {

    boolean hasPower(EntityPlayer player, double amount, ItemStack is);

    boolean usePower(EntityPlayer player, double amount, ItemStack is);

}
