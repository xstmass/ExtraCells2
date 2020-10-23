//package extracells.part
//
//import appeng.api.AEApi
//import appeng.api.IAppEngApi
//import appeng.api.config.Actionable
//import appeng.api.storage.IStorageHelper
//import appeng.api.storage.data.IAEFluidStack
//import extracells.integration.Integration
//import extracells.integration.Integration.Mods
//import extracells.util.GasUtil
//import mekanism.api.gas.Gas
//import mekanism.api.gas.GasStack
//import mekanism.api.gas.IGasHandler
//import net.minecraftforge.common.util.ForgeDirection
//import net.minecraftforge.fluids.Fluid
//import net.minecraftforge.fluids.FluidStack
//import java.util.List
//
//open class PartGasExport : PartFluidExport() {
//    private val isMekanismEnabled: Boolean = Integration.Mods.MEKANISMGAS.isEnabled()
//    override fun doWork(rate: Int, tickSinceLastCall: Int): Boolean {
//        return if (isMekanismEnabled) work(rate, tickSinceLastCall) else false
//    }
//
//    protected fun work(rate: Int, ticksSinceLastCall: Int): Boolean {
//        val facingTank: IGasHandler = getFacingGasTank()
//        if (facingTank == null || !isActive) return false
//        val filter: ArrayList<Fluid> = ArrayList<Fluid>()
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
//                val stack: IAEFluidStack = extractGasFluid(AEApi.instance().storage().createFluidStack(FluidStack(fluid, rate * ticksSinceLastCall)), Actionable.SIMULATE)
//                if (stack != null) {
//                    val gasStack: GasStack = GasUtil.getGasStack(stack.getFluidStack())
//                    if (gasStack != null && facingTank.canReceiveGas(getSide().getOpposite(), gasStack.getGas())) {
//                        val filled: Int = facingTank.receiveGas(getSide().getOpposite(), gasStack, true)
//                        if (filled > 0) {
//                            extractGasFluid(AEApi.instance().storage().createFluidStack(FluidStack(fluid, filled)), Actionable.MODULATE)
//                            return true
//                        }
//                    }
//                }
//            }
//        }
//        return false
//    }
//}