//package extracells.part
//
//import appeng.api.config.Actionable
//import appeng.api.networking.security.BaseActionSource
//import appeng.api.networking.security.MachineSource
//import appeng.api.storage.IMEMonitor
//import appeng.api.storage.data.IAEFluidStack
//import extracells.gridblock.ECBaseGridBlock
//import extracells.integration.Integration
//import extracells.integration.Integration.Mods
//import extracells.util.FluidUtil
//import extracells.util.GasUtil
//import mekanism.api.gas.Gas
//import mekanism.api.gas.GasStack
//import mekanism.api.gas.IGasHandler
//import net.minecraftforge.common.util.ForgeDirection
//import net.minecraftforge.fluids.Fluid
//import net.minecraftforge.fluids.FluidStack
//
//open class PartGasImport : PartFluidImport(), IGasHandler {
//    private val isMekanismEnabled: Boolean = Integration.Mods.MEKANISMGAS.isEnabled()
//    override fun doWork(rate: Int, TicksSinceLastCall: Int): Boolean {
//        if ((!isMekanismEnabled) || getFacingGasTank == null || !isActive) return false
//        var empty: Boolean = true
//        val filter: List<Fluid> = ArrayList<Fluid>()
//        filter.add(this.filterFluids.apply(4))
//        if (this.filterSize >= 1) {
//            var i: Byte = 1
//            /* ERROR converting `while (i < 9) {
//          {
//            if (i != 4) {
//              filter.add(this.filterFluids(i))
//            }
//          }
//          i = (i + 2).toByte
//        }`*/
//        }
//        if (this.filterSize >= 2) {
//            var i: Byte = 0
//            /* ERROR converting `while (i < 9) {
//          {
//            if (i != 4) {
//              filter.add(this.filterFluids(i))
//            }
//          }
//          i = (i + 2).toByte
//        }`*/
//        }
//        /* ERROR converting `import scala.collection.JavaConversions._`*/
//        for (Fluid in asScalaBuffer(asScalaBuffer(asScalaBuffer(asScalaBuffer(asScalaBuffer(asScalaBuffer(asScalaBuffer(asScalaBuffer(asScalaBuffer(asScalaBuffer(filter))))))))))) {
//            if (fluid != null) {
//                empty = false
//                if (fillToNetwork(fluid, rate * TicksSinceLastCall)) {
//                    return true
//                }
//            }
//        }
//        return empty && fillToNetwork(null, rate * TicksSinceLastCall)
//    }
//
//    protected override fun fillToNetwork(fluid: Fluid, toDrain: Int): Boolean {
//        var drained: GasStack = null
//        val facingTank: IGasHandler = getFacingGasTank()
//        val side: ForgeDirection = getSide()
//        val gasType: Gas = run {
//            if (fluid == null) null else {
//                val gasStack: GasStack = GasUtil.getGasStack(FluidStack(fluid, toDrain))
//                if (gasStack == null) null else gasStack.getGas()
//            }
//        }
//        if (gasType == null) {
//            drained = facingTank.drawGas(side.getOpposite(), toDrain, false)
//        } else if (facingTank.canDrawGas(side.getOpposite(), gasType)) {
//            drained = facingTank.drawGas(side.getOpposite(), toDrain, false)
//        }
//        if (drained == null || drained.amount <= 0 || drained.getGas == null) return false
//        val toFill: IAEFluidStack = FluidUtil.createAEFluidStack(GasUtil.getFluidStack(drained))
//        val notInjected: IAEFluidStack = injectGas(toFill, Actionable.MODULATE)
//        return if (notInjected != null) {
//            val amount: Int = (toFill.getStackSize - notInjected.getStackSize).toInt
//            if (amount > 0) {
//                facingTank.drawGas(side.getOpposite(), amount, true)
//                true
//            } else {
//                false
//            }
//        } else {
//            facingTank.drawGas(side.getOpposite(), toFill.getFluidStack().amount, true)
//            true
//        }
//    }
//
//    override fun receiveGas(side: ForgeDirection, stack: GasStack, doTransfer: Boolean): Int {
//        if (stack == null || stack.amount <= 0 || !canReceiveGas(side, stack.getGas())) return 0
//        val amount: Int = Math.min(stack.amount, 125 + this.speedState * 125)
//        val gasStack: IAEFluidStack = GasUtil.createAEFluidStack(stack.getGas(), amount)
//        val notInjected: IAEFluidStack = run {
//            if (getGridBlock == null) {
//                gasStack
//            } else {
//                val monitor: IMEMonitor<IAEFluidStack> = getGridBlock().getFluidMonitor()
//                if (monitor == null) gasStack else monitor.injectItems(gasStack, if (true) Actionable.MODULATE else Actionable.SIMULATE, MachineSource(this))
//            }
//        }
//        return if (notInjected == null) amount else amount - notInjected.getStackSize().toInt
//    }
//
//    override fun receiveGas(side: ForgeDirection, stack: GasStack): Int = receiveGas(side, stack, true)
//    override fun drawGas(side: ForgeDirection, amount: Int, doTransfer: Boolean): GasStack = null
//    override fun drawGas(side: ForgeDirection, amount: Int): GasStack = drawGas(side, amount, true)
//    override fun canDrawGas(side: ForgeDirection, gasType: Gas): Boolean = false
//    override fun canReceiveGas(side: ForgeDirection, gasType: Gas): Boolean {
//        val fluid: Any = /* ERROR converting `MekanismGas.getFluidGasMap.get`*/(gasType)
//        var isEmpty: Boolean = true
//        for (Fluid in filterFluids) {
//            if (filter != null) {
//                isEmpty = false
//                if (filter == fluid) return true
//            }
//        }
//        return isEmpty
//    }
//}