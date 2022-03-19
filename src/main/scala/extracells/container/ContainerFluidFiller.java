package extracells.container;

import com.gamerforea.extracells.ModUtils;
import extracells.tileentity.TileEntityFluidFiller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerFluidFiller extends Container {
    public TileEntityFluidFiller tileentity;

    public ContainerFluidFiller(InventoryPlayer player,
                                TileEntityFluidFiller tileentity) {
        this.tileentity = tileentity;

        bindPlayerInventory(player);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
                        8 + j * 18, i * 18 + 84));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        // TODO gamerforEA code replace, old code:
        // return true;
        return ModUtils.isUseableByPlayer(this.tileentity, player);
        // TODO gamerforEA code end
    }

    @Override
    protected void retrySlotClick(int par1, int par2, boolean par3,
                                  EntityPlayer par4EntityPlayer) {
        // DON'T DO ANYTHING, YOU SHITTY METHOD!
    }
}
