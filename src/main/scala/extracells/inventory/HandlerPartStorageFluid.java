package extracells.inventory;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.implementations.tiles.ITileStorageMonitorable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkStorageEvent;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import extracells.part.PartFluidStorage;
import extracells.util.FluidUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class HandlerPartStorageFluid
        implements IMEInventoryHandler<IAEFluidStack>, IMEMonitorHandlerReceiver<IAEFluidStack> {

    public ITileStorageMonitorable externalSystem;
    protected PartFluidStorage node;
    protected IFluidHandler tank;
    protected AccessRestriction access = AccessRestriction.READ_WRITE;
    protected List<Fluid> prioritizedFluids = new ArrayList<>();
    protected boolean inverted;
    protected TileEntity tile = null;
    private IExternalStorageHandler externalHandler = null;

    public HandlerPartStorageFluid(PartFluidStorage _node) {
        this.node = _node;
    }

    @Override
    public boolean canAccept(IAEFluidStack input) {
        if (!this.node.isActive())
            return false;
        if (this.tank == null && this.externalSystem == null && this.externalHandler == null || !(this.access == AccessRestriction.WRITE || this.access == AccessRestriction.READ_WRITE) || input == null)
            return false;
        if (this.externalSystem != null) {
            IStorageMonitorable monitor = this.externalSystem.getMonitorable(this.node.getSide().getOpposite(), new MachineSource(this.node));
            if (monitor == null)
                return false;
            IMEMonitor<IAEFluidStack> fluidInventory = monitor.getFluidInventory();
            return fluidInventory != null && fluidInventory.canAccept(input);
        } else if (this.externalHandler != null) {
            IMEInventory<IAEFluidStack> inventory = this.externalHandler.getInventory(this.tile, this.node.getSide().getOpposite(), StorageChannel.FLUIDS, new MachineSource(this.node));
            return inventory != null;
        }
        FluidTankInfo[] infoArray = this.tank.getTankInfo(this.node.getSide().getOpposite());
        if (infoArray != null && infoArray.length > 0) {
            FluidTankInfo info = infoArray[0];
            if (info.fluid == null || info.fluid.amount == 0 || info.fluid.getFluidID() == input.getFluidStack().getFluidID())
                if (this.inverted)
                    return !this.prioritizedFluids.isEmpty() || !this.isPrioritized(input);
                else
                    return this.prioritizedFluids.isEmpty() || this.isPrioritized(input);
        }
        return false;
    }

    @Override
    public IAEFluidStack extractItems(IAEFluidStack request, Actionable mode, BaseActionSource src) {
        if (!this.node.isActive() || !(this.access == AccessRestriction.READ || this.access == AccessRestriction.READ_WRITE))
            return null;
        if (this.externalSystem != null && request != null) {
            IStorageMonitorable monitor = this.externalSystem.getMonitorable(this.node.getSide().getOpposite(), src);
            if (monitor == null)
                return null;
            IMEMonitor<IAEFluidStack> fluidInventory = monitor.getFluidInventory();
            if (fluidInventory == null)
                return null;
            return fluidInventory.extractItems(request, mode, src);

        } else if (this.externalHandler != null && request != null) {
            IMEInventory<IAEFluidStack> inventory = this.externalHandler.getInventory(this.tile, this.node.getSide().getOpposite(), StorageChannel.FLUIDS, new MachineSource(this.node));
            if (inventory == null)
                return null;
            return inventory.extractItems(request, mode, new MachineSource(this.node));
        }
        if (this.tank == null || request == null || this.access == AccessRestriction.WRITE || this.access == AccessRestriction.NO_ACCESS)
            return null;
        FluidStack toDrain = request.getFluidStack();
        int drained = 0;
        int drained2;
        do {
            FluidStack drain = this.tank.drain(this.node.getSide().getOpposite(), new FluidStack(toDrain.getFluid(), toDrain.amount - drained), mode == Actionable.MODULATE);
            if (drain == null)
                drained2 = 0;
            else
                drained2 = drain.amount;
            drained = drained + drained2;
        }
        while (toDrain.amount != drained && drained2 != 0);
        if (drained == 0)
            return null;
        IItemList<IAEFluidStack> fluids = this.getAvailableItems(AEApi.instance().storage().createFluidList());
        for (IAEFluidStack fluid : fluids) {
            if (fluid.getFluid() == request.getFluid())
                drained = (int) Math.min(drained, fluid.getStackSize());
        }

        if (drained == toDrain.amount)
            return request;
        return FluidUtil.createAEFluidStack(toDrain.getFluidID(), drained);
    }

    @Override
    public AccessRestriction getAccess() {
        return this.access;
    }

    @Override
    public IItemList<IAEFluidStack> getAvailableItems(IItemList<IAEFluidStack> out) {
        if (!this.node.isActive() || !(this.access == AccessRestriction.READ || this.access == AccessRestriction.READ_WRITE))
            return out;
        if (this.externalSystem != null) {
            IStorageMonitorable monitor = this.externalSystem.getMonitorable(this.node.getSide().getOpposite(), new MachineSource(this.node));
            if (monitor == null)
                return out;
            IMEMonitor<IAEFluidStack> fluidInventory = monitor.getFluidInventory();
            if (fluidInventory == null)
                return out;
            IItemList<IAEFluidStack> list = this.externalSystem.getMonitorable(this.node.getSide().getOpposite(), new MachineSource(this.node)).getFluidInventory().getStorageList();
            for (IAEFluidStack stack : list) {
                out.add(stack);
            }
        } else if (this.externalHandler != null) {
            IMEInventory<IAEFluidStack> inventory = this.externalHandler.getInventory(this.tile, this.node.getSide().getOpposite(), StorageChannel.FLUIDS, new MachineSource(this.node));
            if (inventory == null)
                return out;
            IItemList<IAEFluidStack> list = inventory.getAvailableItems(AEApi.instance().storage().createFluidList());
            for (IAEFluidStack stack : list) {
                out.add(stack);
            }
        } else if (this.tank != null) {
            FluidTankInfo[] infoArray = this.tank.getTankInfo(this.node.getSide().getOpposite());
            if (infoArray != null && infoArray.length > 0)
                for (FluidTankInfo info : infoArray) {
                    if (info.fluid != null)
                        out.add(AEApi.instance().storage().createFluidStack(info.fluid));
                }
        }
        return out;
    }

    @Override
    public StorageChannel getChannel() {
        return StorageChannel.FLUIDS;
    }

    @Override
    public int getPriority() {
        return this.node.getPriority();
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public IAEFluidStack injectItems(IAEFluidStack input, Actionable mode, BaseActionSource src) {
        if (!(this.access == AccessRestriction.WRITE || this.access == AccessRestriction.READ_WRITE))
            return null;
        if (this.externalSystem != null && input != null) {
            IStorageMonitorable monitor = this.externalSystem.getMonitorable(this.node.getSide().getOpposite(), src);
            if (monitor == null)
                return input;
            IMEMonitor<IAEFluidStack> fluidInventory = monitor.getFluidInventory();
            if (fluidInventory == null)
                return input;
            return fluidInventory.injectItems(input, mode, src);
        } else if (this.externalHandler != null && input != null) {
            IMEInventory<IAEFluidStack> inventory = this.externalHandler.getInventory(this.tile, this.node.getSide().getOpposite(), StorageChannel.FLUIDS, new MachineSource(this.node));
            if (inventory == null)
                return input;
            return inventory.injectItems(input, mode, new MachineSource(this.node));
        }
        if (this.tank == null || input == null || !this.canAccept(input))
            return input;

        FluidStack toFill = input.getFluidStack();

        // TODO gamerforEA code start
        int maxFill = 0;
        for (FluidTankInfo info : this.tank.getTankInfo(this.node.getSide().getOpposite())) {
            if (info.fluid == null)
                maxFill += info.capacity;
            else if (info.fluid.getFluid() == toFill.getFluid())
                maxFill += info.capacity - info.fluid.amount;
        }
        if (maxFill <= 0)
            return input;
        int prevAmount = toFill.amount;
        toFill.amount = Math.min(prevAmount, maxFill);
        // TODO gamerforEA code end

        int filled = 0;
        int filled2;
        do {
            filled2 = this.tank.fill(this.node.getSide().getOpposite(), new FluidStack(toFill.getFluid(), toFill.amount - filled), mode == Actionable.MODULATE);
            filled = filled + filled2;
        }
        while (filled2 != 0 && filled != toFill.amount);

        // TODO gamerforEA code start
        toFill.amount = prevAmount;
        // TODO gamerforEA code end

		/* TODO gamerforEA code clear:
		FluidTankInfo[] infos = this.tank.getTankInfo(this.node.getSide().getOpposite());
		int maxFill = 0;
		for (FluidTankInfo info : infos)
		{
			if (info.fluid == null)
				maxFill += info.capacity;
			else if (info.fluid.getFluid() == toFill.getFluid())
				maxFill += info.capacity - info.fluid.amount;
		} */

        filled = Math.min(filled, maxFill);
        if (filled == toFill.amount)
            return null;
        return FluidUtil.createAEFluidStack(toFill.getFluidID(), toFill.amount - filled);
    }

    @Override
    public boolean isPrioritized(IAEFluidStack input) {
        if (input == null)
            return false;
        for (Fluid fluid : this.prioritizedFluids) {
            if (fluid == input.getFluid())
                return true;
        }
        return false;
    }

    @Override
    public boolean isValid(Object verificationToken) {
        return true;
    }

    @Override
    public void onListUpdate() {

    }

    public void onNeighborChange() {
        if (this.externalSystem != null) {
            IStorageMonitorable monitor = this.externalSystem.getMonitorable(this.node.getSide().getOpposite(), new MachineSource(this.node));
            if (monitor != null) {
                IMEMonitor<IAEFluidStack> fluidInventory = monitor.getFluidInventory();
                if (fluidInventory != null)
                    fluidInventory.removeListener(this);
            }
        }
        this.tank = null;
        ForgeDirection orientation = this.node.getSide();
        TileEntity hostTile = this.node.getHostTile();
        if (hostTile == null)
            return;
        if (hostTile.getWorldObj() == null)
            return;
        TileEntity tileEntity = hostTile.getWorldObj().getTileEntity(hostTile.xCoord + orientation.offsetX, hostTile.yCoord + orientation.offsetY, hostTile.zCoord + orientation.offsetZ);
        this.tile = tileEntity;
        this.tank = null;
        this.externalSystem = null;
        if (tileEntity == null) {
            this.externalHandler = null;
            return;
        }
        this.externalHandler = AEApi.instance().registries().externalStorage().getHandler(tileEntity, this.node.getSide().getOpposite(), StorageChannel.FLUIDS, new MachineSource(this.node));
        if (tileEntity instanceof ITileStorageMonitorable) {
            this.externalSystem = (ITileStorageMonitorable) tileEntity;
            IStorageMonitorable monitor = this.externalSystem.getMonitorable(this.node.getSide().getOpposite(), new MachineSource(this.node));
            if (monitor == null)
                return;
            IMEMonitor<IAEFluidStack> fluidInventory = monitor.getFluidInventory();
            if (fluidInventory == null)
                return;
            fluidInventory.addListener(this, null);

        } else if (this.externalHandler == null && tileEntity instanceof IFluidHandler)
            this.tank = (IFluidHandler) tileEntity;
    }

    @Override
    public void postChange(IBaseMonitor<IAEFluidStack> monitor, Iterable<IAEFluidStack> change, BaseActionSource actionSource) {
        IGridNode gridNode = this.node.getGridNode();
        if (gridNode != null) {
            IGrid grid = gridNode.getGrid();
            if (grid != null) {
                grid.postEvent(new MENetworkCellArrayUpdate());
                gridNode.getGrid().postEvent(new MENetworkStorageEvent(this.node.getGridBlock().getFluidMonitor(), StorageChannel.FLUIDS));
            }
            this.node.getHost().markForUpdate();
        }
    }

    public void setAccessRestriction(AccessRestriction access) {
        this.access = access;
    }

    public void setInverted(boolean _inverted) {
        this.inverted = _inverted;
    }

    public void setPrioritizedFluids(Fluid[] _fluids) {
        this.prioritizedFluids.clear();
        for (Fluid fluid : _fluids) {
            if (fluid != null)
                this.prioritizedFluids.add(fluid);
        }
    }

    @Override
    public boolean validForPass(int i) {
        return true; // TODO
    }
}
