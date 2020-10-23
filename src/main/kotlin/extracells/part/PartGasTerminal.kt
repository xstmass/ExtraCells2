//package extracells.part
//
//import appeng.api.config.Actionable
//import appeng.api.networking.security.BaseActionSource
//import appeng.api.networking.security.MachineSource
//import appeng.api.storage.IMEMonitor
//import appeng.api.storage.data.IAEFluidStack
//import extracells.container.ContainerGasTerminal
//import extracells.gridblock.ECBaseGridBlock
//import extracells.gui.GuiGasTerminal
//import extracells.integration.Integration.Mods
//import extracells.util.FluidUtil
//import extracells.util.GasUtil
//import extracells.util.inventory.ECPrivateInventory
//import mekanism.api.gas.GasStack
//import net.minecraft.entity.player.EntityPlayer
//import net.minecraft.item.ItemStack
//import net.minecraftforge.fluids.Fluid
//import net.minecraftforge.fluids.FluidStack
//import org.apache.commons.lang3.tuple.MutablePair
//
//open class PartGasTerminal : PartFluidTerminal() {
//    val mekLoaded: Boolean = Mods.MEKANISMGAS.isEnabled
//    var doNextFill: Boolean = false
//    protected override fun isItemValidForInputSlot(i: Int, itemStack: ItemStack): Boolean {
//        return GasUtil.isGasContainer(itemStack)
//    }
//
//    override fun doWork(): Unit {
//        if (mekLoaded) doWorkGas()
//    }
//
//    fun doWorkGas(): Unit {
//        val secondSlot: ItemStack = this.inventory.getStackInSlot(1)
//        if (secondSlot != null && secondSlot.stackSize >= secondSlot.getMaxStackSize) return
//        var container: ItemStack = this.inventory.getStackInSlot(0)
//        if (container == null) doNextFill = false
//        if (!GasUtil.isGasContainer(container)) return
//        container = container.copy()
//        container.stackSize = 1
//        val gridBlock: ECBaseGridBlock = gridBlock
//        if (gridBlock == null) return
//        val monitor: IMEMonitor<IAEFluidStack> = gridBlock.fluidMonitor
//        if (monitor == null) return
//        val gasStack: GasStack = GasUtil.getGasFromContainer(container)
//        if (GasUtil.isEmpty(container) || gasStack.amount < GasUtil.getCapacity(container) && GasUtil.getFluidStack(gasStack).getFluid == this.currentFluid && doNextFill) {
//            if (this.currentFluid == null) return
//            val capacity: Int = GasUtil.getCapacity(container)
//            val result: IAEFluidStack = monitor.extractItems(FluidUtil.createAEFluidStack(this.currentFluid, capacity), Actionable.SIMULATE, this.machineSource)
//            var proposedAmount: Int = 0
//            if (result == null) proposedAmount = 0 else if (gasStack == null) proposedAmount = Math.min(capacity, result.stackSize).toInt else proposedAmount = Math.min(capacity - gasStack.amount, result.stackSize).toInt
//            val filledContainer: MutablePair<Integer, ItemStack> = GasUtil.fillStack(container, GasUtil.getGasStack(FluidStack(this.currentFluid, proposedAmount)))
//            val gasStack2: GasStack = GasUtil.getGasFromContainer(filledContainer.getRight())
//            if (gasStack2 == null) {
//                doNextFill = false
//            } else if (container.stackSize == 1 && gasStack2.amount < GasUtil.getCapacity(filledContainer.getRight())) {
//                this.inventory.setInventorySlotContents(0, filledContainer.getRight())
//                monitor.extractItems(FluidUtil.createAEFluidStack(this.currentFluid, filledContainer.getLeft().toLong), Actionable.MODULATE, this.machineSource)
//                doNextFill = true
//            } else if (fillSecondSlot(filledContainer.getRight())) {
//                monitor.extractItems(FluidUtil.createAEFluidStack(this.currentFluid, filledContainer.getLeft().toLong), Actionable.MODULATE, this.machineSource)
//                decreaseFirstSlot()
//                doNextFill = false
//            }
//        } else {
//            val containerGas: GasStack = GasUtil.getGasFromContainer(container)
//            val drainedContainer: MutablePair<Integer, ItemStack> = GasUtil.drainStack(container.copy(), containerGas)
//            val gasStack: GasStack = containerGas.copy()
//            gasStack.amount = drainedContainer.getLeft()
//            val notInjected: IAEFluidStack = monitor.injectItems(GasUtil.createAEFluidStack(gasStack), Actionable.SIMULATE, this.machineSource)
//            if (notInjected != null) return
//            val emptyContainer: ItemStack = drainedContainer.getRight()
//            if (emptyContainer != null && GasUtil.getGasFromContainer(emptyContainer) != null && emptyContainer.stackSize == 1) {
//                monitor.injectItems(GasUtil.createAEFluidStack(gasStack), Actionable.MODULATE, this.machineSource)
//                this.inventory.setInventorySlotContents(0, emptyContainer)
//            } else if (emptyContainer == null || fillSecondSlot(emptyContainer)) {
//                monitor.injectItems(GasUtil.createAEFluidStack(containerGas), Actionable.MODULATE, this.machineSource)
//                decreaseFirstSlot()
//            }
//        }
//    }
//
//    override fun getServerGuiElement(player: EntityPlayer): Any {
//        return if (mekLoaded) ContainerGasTerminal(this, player) else null
//    }
//
//    override fun getClientGuiElement(player: EntityPlayer): Any {
//        return if (mekLoaded) GuiGasTerminal(this, player) else null
//    }
//}