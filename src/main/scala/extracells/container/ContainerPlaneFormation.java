package extracells.container;

import appeng.api.AEApi;
import appeng.api.implementations.guiobjects.IGuiItem;
import appeng.api.implementations.guiobjects.INetworkTool;
import appeng.api.util.DimensionalCoord;
import com.gamerforea.extracells.ModUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import extracells.container.slot.SlotNetworkTool;
import extracells.container.slot.SlotRespective;
import extracells.gui.GuiFluidPlaneFormation;
import extracells.part.PartFluidPlaneFormation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPlaneFormation extends Container {
    // TODO gamerforEA code start
    private final PartFluidPlaneFormation part;

    @SideOnly(Side.CLIENT)
    // TODO gamerforEA code end
    private GuiFluidPlaneFormation gui;

    public ContainerPlaneFormation(PartFluidPlaneFormation part, EntityPlayer player) {
        // TODO gamerforEA code start
        this.part = part;
        // TODO gamerforEA code end

        this.addSlotToContainer(new SlotRespective(part.getUpgradeInventory(), 0, 187, 8));
        this.bindPlayerInventory(player.inventory);

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && AEApi.instance().definitions().items().networkTool().isSameAs(stack)) {
                DimensionalCoord coord = part.getHost().getLocation();
                IGuiItem guiItem = (IGuiItem) stack.getItem();
                INetworkTool networkTool = (INetworkTool) guiItem.getGuiObject(stack, coord.getWorld(), coord.x, coord.y, coord.z);
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 3; k++) {
                        this.addSlotToContainer(new SlotNetworkTool(networkTool, j + k * 3, 187 + k * 18, j * 18 + 102));
                    }
                }
                return;
            }
        }
    }

    protected void bindPlayerInventory(IInventory inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, i * 18 + 102));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 160));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        // TODO gamerforEA code replace, old code:
        // return true;
        return ModUtils.isUseableByPlayer(this.part, player);
        // TODO gamerforEA code end
    }

    // TODO gamerforEA code start
    @SideOnly(Side.CLIENT)
    private void transferStackInSlotClient(int slotnumber) {
        if (this.gui != null)
            this.gui.shiftClick(this.getSlot(slotnumber).getStack());
    }

    @SideOnly(Side.CLIENT)
    // TODO gamerforEA code end
    public void setGui(GuiFluidPlaneFormation _gui) {
        this.gui = _gui;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotnumber) {
		/* TODO gamerforEA code replace, old code:
		if (FMLCommonHandler.instance().getSide().isClient() && this.gui != null)
			this.gui.shiftClick(this.getSlot(slotnumber).getStack()); */
        if (FMLCommonHandler.instance().getSide().isClient())
            this.transferStackInSlotClient(slotnumber);
        // TODO gamerforEA code end

        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotnumber);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotnumber < 36) {
                if (!this.mergeItemStack(itemstack1, 36, this.inventorySlots.size(), true))
                    return null;
            } else if (!this.mergeItemStack(itemstack1, 0, 36, false))
                return null;

            if (itemstack1.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();
        }

        return itemstack;
    }
}
