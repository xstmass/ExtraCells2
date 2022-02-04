package extracells.container;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import extracells.api.IPortableFluidStorageCell;
import extracells.api.IWirelessFluidTermHandler;
import extracells.container.slot.SlotPlayerInventory;
import extracells.container.slot.SlotRespective;
import extracells.gui.GuiFluidStorage;
import extracells.gui.widget.fluid.IFluidSelectorContainer;
import extracells.inventory.HandlerItemStorageFluid;
import extracells.network.packet.part.PacketFluidStorage;
import extracells.util.FluidUtil;
import extracells.util.inventory.ECPrivateInventory;
import extracells.util.inventory.IInventoryUpdateReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.MutablePair;

public class ContainerFluidStorage extends Container
        implements IMEMonitorHandlerReceiver<IAEFluidStack>, IFluidSelectorContainer, IInventoryUpdateReceiver,
        IStorageContainer {

    // TODO gamerforEA code replace, old code:
    // private IMEMonitor<IAEFluidStack> monitor;
    public static final ThreadLocal<IStorageMonitorable> TEMP_STORAGE_MONITORABLE = new ThreadLocal<>();
    private final IMEMonitor<IAEFluidStack> monitor;
    private final IStorageMonitorable storageMonitorable;
    public boolean hasWirelessTermHandler = false;
    private GuiFluidStorage guiFluidStorage;
    private IItemList<IAEFluidStack> fluidStackList;
    private Fluid selectedFluid;
    private IAEFluidStack selectedFluidStack;
    private EntityPlayer player;
    private HandlerItemStorageFluid storageFluid;
    private IWirelessFluidTermHandler handler = null;
    private IPortableFluidStorageCell storageCell = null;
    private ECPrivateInventory inventory = new ECPrivateInventory("extracells.item.fluid.storage", 2, 64, this) {
        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemStack) {
            return FluidUtil.isFluidContainer(itemStack);
        }
    };
    // TODO gamerforEA code end

    public ContainerFluidStorage(EntityPlayer _player) {
        this(null, _player);
    }

    public ContainerFluidStorage(IMEMonitor<IAEFluidStack> _monitor, EntityPlayer _player) {
        this.monitor = _monitor;
        this.player = _player;

        // TODO gamerforEA code start
        this.storageMonitorable = TEMP_STORAGE_MONITORABLE.get();
        // TODO gamerforEA code end

        if (!this.player.worldObj.isRemote && this.monitor != null) {
            this.monitor.addListener(this, null);
            this.fluidStackList = this.monitor.getStorageList();
        } else
            this.fluidStackList = AEApi.instance().storage().createFluidList();

        // Input Slot accepts all FluidContainers
        this.addSlotToContainer(new SlotRespective(this.inventory, 0, 8, 92));
        // Input Slot accepts nothing
        this.addSlotToContainer(new SlotFurnace(this.player, this.inventory, 1, 26, 92));

        this.bindPlayerInventory(this.player.inventory);
    }

    public ContainerFluidStorage(IMEMonitor<IAEFluidStack> _monitor, EntityPlayer _player, IPortableFluidStorageCell _storageCell) {
        this.hasWirelessTermHandler = _storageCell != null;
        this.storageCell = _storageCell;
        this.monitor = _monitor;
        this.player = _player;

        // TODO gamerforEA code start
        this.storageMonitorable = TEMP_STORAGE_MONITORABLE.get();
        // TODO gamerforEA code end

        if (!this.player.worldObj.isRemote && this.monitor != null) {
            this.monitor.addListener(this, null);
            this.fluidStackList = this.monitor.getStorageList();
        } else
            this.fluidStackList = AEApi.instance().storage().createFluidList();

        // Input Slot accepts all FluidContainers
        this.addSlotToContainer(new SlotRespective(this.inventory, 0, 8, 92));
        // Input Slot accepts nothing
        this.addSlotToContainer(new SlotFurnace(this.player, this.inventory, 1, 26, 92));

        this.bindPlayerInventory(this.player.inventory);
    }

    public ContainerFluidStorage(IMEMonitor<IAEFluidStack> _monitor, EntityPlayer _player, IWirelessFluidTermHandler _handler) {
        this.hasWirelessTermHandler = _handler != null;
        this.handler = _handler;
        this.monitor = _monitor;
        this.player = _player;

        // TODO gamerforEA code start
        this.storageMonitorable = TEMP_STORAGE_MONITORABLE.get();
        // TODO gamerforEA code end

        if (!this.player.worldObj.isRemote && this.monitor != null) {
            this.monitor.addListener(this, null);
            this.fluidStackList = this.monitor.getStorageList();
        } else
            this.fluidStackList = AEApi.instance().storage().createFluidList();

        // Input Slot accepts all FluidContainers
        this.addSlotToContainer(new SlotRespective(this.inventory, 0, 8, 92));
        // Input Slot accepts nothing
        this.addSlotToContainer(new SlotFurnace(this.player, this.inventory, 1, 26, 92));

        this.bindPlayerInventory(this.player.inventory);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new SlotPlayerInventory(inventoryPlayer, this, j + i * 9 + 9, 8 + j * 18, i * 18 + 122));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new SlotPlayerInventory(inventoryPlayer, this, i, 8 + i * 18, 180));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        // TODO gamerforEA code replace, old code:
        // return true;
        return this.monitor == null || this.storageMonitorable == null || this.monitor == this.storageMonitorable.getFluidInventory();
        // TODO gamerforEA code end
    }

    public void decreaseFirstSlot() {
        ItemStack slot = this.inventory.getStackInSlot(0);
        if (slot == null)
            return;
        slot.stackSize--;
        if (slot.stackSize <= 0)
            this.inventory.setInventorySlotContents(0, null);
    }

    public void doWork() {
        ItemStack secondSlot = this.inventory.getStackInSlot(1);

        // TODO gamerforEA code replace, old code:
        // if (secondSlot != null && secondSlot.stackSize > secondSlot.getMaxStackSize())
        if (secondSlot != null && secondSlot.stackSize >= secondSlot.getMaxStackSize())
            // TODO gamerforEA code end
            return;

        ItemStack container = this.inventory.getStackInSlot(0);
        if (!FluidUtil.isFluidContainer(container))
            return;
        if (this.monitor == null)
            return;

        container = container.copy();
        container.stackSize = 1;

        if (FluidUtil.isEmpty(container)) {
            if (this.selectedFluid == null)
                return;
            int capacity = FluidUtil.getCapacity(container);
            //Tries to simulate the extraction of fluid from storage.
            IAEFluidStack result = this.monitor.extractItems(FluidUtil.createAEFluidStack(this.selectedFluid, capacity), Actionable.SIMULATE, new PlayerSource(this.player, null));

            //Calculates the amount of fluid to fill container with.
            int proposedAmount = result == null ? 0 : (int) Math.min(capacity, result.getStackSize());
            if (proposedAmount == 0)
                return;

            // TODO gamerforEA code start
            ItemStack containerCopy = container.copy();
            // TODO gamerforEA code end

            //Tries to fill the container with fluid.
            MutablePair<Integer, ItemStack> filledContainer = FluidUtil.fillStack(container, new FluidStack(this.selectedFluid, proposedAmount));

            //Moves it to second slot and commits extraction to grid.
			/* TODO gamerforEA code replace, old code:
			if (this.fillSecondSlot(filledContainer.getRight()))
			{
				this.monitor.extractItems(FluidUtil.createAEFluidStack(this.selectedFluid, filledContainer.getLeft()), Actionable.MODULATE, new PlayerSource(this.player, null));
				this.decreaseFirstSlot();
			} */
            if (this.simulateFillSecondSlot(filledContainer.getRight())) {
                container = containerCopy;
                result = this.monitor.extractItems(FluidUtil.createAEFluidStack(this.selectedFluid, filledContainer.getLeft()), Actionable.MODULATE, new PlayerSource(this.player, null));
                proposedAmount = result == null ? 0 : (int) Math.min(capacity, result.getStackSize());
                if (proposedAmount == 0)
                    return;
                filledContainer = FluidUtil.fillStack(container, new FluidStack(this.selectedFluid, proposedAmount));
                if (this.fillSecondSlot(filledContainer.getRight()))
                    this.decreaseFirstSlot();
            }
            // TODO gamerforEA code end
        } else if (FluidUtil.isFilled(container)) {
            FluidStack containerFluid = FluidUtil.getFluidFromContainer(container);

            //Tries to inject fluid to network.
            IAEFluidStack notInjected = this.monitor.injectItems(FluidUtil.createAEFluidStack(containerFluid), Actionable.SIMULATE, new PlayerSource(this.player, null));
            if (notInjected != null)
                return;
            if (this.handler != null) {
                if (!this.handler.hasPower(this.player, 20.0D, this.player.getCurrentEquippedItem()))
                    return;
                this.handler.usePower(this.player, 20.0D, this.player.getCurrentEquippedItem());
            } else if (this.storageCell != null) {
                if (!this.storageCell.hasPower(this.player, 20.0D, this.player.getCurrentEquippedItem()))
                    return;
                this.storageCell.usePower(this.player, 20.0D, this.player.getCurrentEquippedItem());
            }

			/* TODO gamerforEA code replace, old code:
			MutablePair<Integer, ItemStack> drainedContainer = FluidUtil.drainStack(container, containerFluid);
			if (this.fillSecondSlot(drainedContainer.getRight()))
			{
				this.monitor.injectItems(FluidUtil.createAEFluidStack(containerFluid), Actionable.MODULATE, new PlayerSource(this.player, null));
				this.decreaseFirstSlot();
			} */
            MutablePair<Integer, ItemStack> drainedContainer = FluidUtil.drainStack(container, containerFluid);
            ItemStack emptyContainer = drainedContainer.getRight();
            if (this.simulateFillSecondSlot(emptyContainer)) {
                int amount = drainedContainer.getLeft();
                if (amount > 0) {
                    containerFluid.amount = amount;
                    if (this.monitor.injectItems(FluidUtil.createAEFluidStack(containerFluid), Actionable.SIMULATE, new PlayerSource(this.player, null)) == null && this.fillSecondSlot(emptyContainer)) {
                        this.monitor.injectItems(FluidUtil.createAEFluidStack(containerFluid), Actionable.MODULATE, new PlayerSource(this.player, null));
                        this.decreaseFirstSlot();
                    }
                }
            }
            // TODO gamerforEA code end
        }
    }

    // TODO gamerforEA code start
    private boolean simulateFillSecondSlot(ItemStack stack) {
        if (stack == null)
            return false;
        ItemStack secondSlot = this.inventory.getStackInSlot(1);
        if (secondSlot == null) {
            if (this.handler != null)
                return this.handler.hasPower(this.player, 20.0D, this.player.getCurrentEquippedItem());
            if (this.storageCell != null)
                return this.storageCell.hasPower(this.player, 20.0D, this.player.getCurrentEquippedItem());
        } else {
            if (!secondSlot.isItemEqual(stack) || !ItemStack.areItemStackTagsEqual(stack, secondSlot))
                return false;
            if (this.handler != null)
                return this.handler.hasPower(this.player, 20.0D, this.player.getCurrentEquippedItem());
            if (this.storageCell != null)
                return this.storageCell.hasPower(this.player, 20.0D, this.player.getCurrentEquippedItem());
        }
        return true;
    }
    // TODO gamerforEA code end

    public boolean fillSecondSlot(ItemStack stack) {
        if (stack == null)
            return false;
        ItemStack secondSlot = this.inventory.getStackInSlot(1);
        if (secondSlot == null) {
            if (this.handler != null) {
                if (!this.handler.hasPower(this.player, 20.0D, this.player.getCurrentEquippedItem()))
                    return false;
                this.handler.usePower(this.player, 20.0D, this.player.getCurrentEquippedItem());
            } else if (this.storageCell != null) {
                if (!this.storageCell.hasPower(this.player, 20.0D, this.player.getCurrentEquippedItem()))
                    return false;
                this.storageCell.usePower(this.player, 20.0D, this.player.getCurrentEquippedItem());
            }
            this.inventory.setInventorySlotContents(1, stack);
            return true;
        }
        if (!secondSlot.isItemEqual(stack) || !ItemStack.areItemStackTagsEqual(stack, secondSlot))
            return false;
        if (this.handler != null) {
            if (!this.handler.hasPower(this.player, 20.0D, this.player.getCurrentEquippedItem()))
                return false;
            this.handler.usePower(this.player, 20.0D, this.player.getCurrentEquippedItem());
        } else if (this.storageCell != null) {
            if (!this.storageCell.hasPower(this.player, 20.0D, this.player.getCurrentEquippedItem()))
                return false;
            this.storageCell.usePower(this.player, 20.0D, this.player.getCurrentEquippedItem());
        }
        this.inventory.incrStackSize(1, stack.stackSize);
        return true;
    }

    public void forceFluidUpdate() {
        if (this.monitor != null)
            new PacketFluidStorage(this.player, this.monitor.getStorageList()).sendPacketToPlayer(this.player);
        new PacketFluidStorage(this.player, this.hasWirelessTermHandler).sendPacketToPlayer(this.player);
    }

    public IItemList<IAEFluidStack> getFluidStackList() {
        return this.fluidStackList;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public Fluid getSelectedFluid() {
        return this.selectedFluid;
    }

    @Override
    public void setSelectedFluid(Fluid _selectedFluid) {
        new PacketFluidStorage(this.player, _selectedFluid).sendPacketToServer();
        this.receiveSelectedFluid(_selectedFluid);
    }

    public IAEFluidStack getSelectedFluidStack() {
        return this.selectedFluidStack;
    }

    @Override
    public boolean hasWirelessTermHandler() {
        return this.hasWirelessTermHandler;
    }

    @Override
    public boolean isValid(Object verificationToken) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        if (!entityPlayer.worldObj.isRemote) {
            this.monitor.removeListener(this);
            for (int i = 0; i < 2; i++) {
                this.player.dropPlayerItemWithRandomChoice(((Slot) this.inventorySlots.get(i)).getStack(), false);
            }
        }
    }

    @Override
    public void onInventoryChanged() {

    }

    @Override
    public void onListUpdate() {
    }

    @Override
    public void postChange(IBaseMonitor<IAEFluidStack> monitor, Iterable<IAEFluidStack> change, BaseActionSource actionSource) {
        this.fluidStackList = ((IMEMonitor<IAEFluidStack>) monitor).getStorageList();
        new PacketFluidStorage(this.player, this.fluidStackList).sendPacketToPlayer(this.player);
        new PacketFluidStorage(this.player, this.hasWirelessTermHandler).sendPacketToPlayer(this.player);
    }

    public void receiveSelectedFluid(Fluid _selectedFluid) {
        this.selectedFluid = _selectedFluid;
        if (this.selectedFluid != null)
            for (IAEFluidStack stack : this.fluidStackList) {
                if (stack != null && stack.getFluid() == this.selectedFluid) {
                    this.selectedFluidStack = stack;
                    break;
                }
            }
        else
            this.selectedFluidStack = null;
        if (this.guiFluidStorage != null)
            this.guiFluidStorage.updateSelectedFluid();
    }

    public void removeEnergyTick() {
        if (this.handler != null) {
            if (this.handler.hasPower(this.player, 1.0D, this.player.getCurrentEquippedItem()))
                this.handler.usePower(this.player, 1.0D, this.player.getCurrentEquippedItem());
        } else if (this.storageCell != null)
            if (this.storageCell.hasPower(this.player, 0.5D, this.player.getCurrentEquippedItem()))
                this.storageCell.usePower(this.player, 0.5D, this.player.getCurrentEquippedItem());
    }

    public void setGui(GuiFluidStorage _guiFluidStorage) {
        this.guiFluidStorage = _guiFluidStorage;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotnumber) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotnumber);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (this.inventory.isItemValidForSlot(0, itemstack1)) {
                if (slotnumber == 0 || slotnumber == 1) {
                    if (!this.mergeItemStack(itemstack1, 2, 36, false))
                        return null;
                } else if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    return null;
                if (itemstack1.stackSize == 0)
                    slot.putStack(null);
                else
                    slot.onSlotChanged();
            } else
                return null;
        }
        return itemstack;
    }

    public void updateFluidList(IItemList<IAEFluidStack> _fluidStackList) {
        this.fluidStackList = _fluidStackList;
        if (this.guiFluidStorage != null)
            this.guiFluidStorage.updateFluids();
    }
}
