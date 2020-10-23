//package extracells.integration.mekanism
//
//import appeng.api.config.Actionable
//import appeng.api.networking.security.BaseActionSource
//import appeng.api.storage.IMEInventory
//import appeng.api.storage.StorageChannel
//import appeng.api.storage.data.IAEFluidStack
//import appeng.api.storage.data.IItemList
//import extracells.api.IExternalGasStorageHandler
//import extracells.integration.mekanism.HandlerMekanismGasTank.Inventory
//import extracells.util.GasUtil
//import mekanism.api.gas.Gas
//import mekanism.api.gas.GasStack
//import mekanism.api.gas.GasTank
//import net.minecraft.tileentity.TileEntity
//import net.minecraftforge.common.util.ForgeDirection
//import java.lang.reflect.Field
//
//object HandlerMekanismGasTank : IExternalGasStorageHandler() {
//    val clazz: Class<*> = Class.forName("mekanism.common.tile.TileEntityGasTank")
//    override fun canHandle(tile: TileEntity, d: ForgeDirection, mySrc: BaseActionSource): Boolean {
//        return tile != null && tile.getClass == clazz
//    }
//
//    override fun getInventory(tile: TileEntity, d: ForgeDirection, src: BaseActionSource): IMEInventory<IAEFluidStack> {
//        val tank: GasTank = getGasTank(tile)
//        return if (tank == null) null else Inventory(tank)
//    }
//
//    fun getGasTank(tile: TileEntity): GasTank {
//        return try {
//            val tank: Field = clazz.getField("gasTank")
//            if (tank != null) tank.get(tile) as GasTank else null
//        } catch (_: Throwable) {
//            null
//        }
//    }
//
//    open class Inventory(private val tank: GasTank) : IMEInventory<IAEFluidStack> {
//        override fun injectItems(stackType: IAEFluidStack, actionable: Actionable, baseActionSource: BaseActionSource): IAEFluidStack {
//            val gasStack: GasStack = GasUtil.getGasStack(stackType)
//            if (gasStack == null) return stackType
//            if (tank.canReceive(gasStack.gas)) {
//                val accepted: Int = tank.receive(gasStack, actionable == Actionable.MODULATE)
//                if (accepted == stackType.getStackSize) return null
//                val returnStack: IAEFluidStack = stackType.copy()
//              returnStack.stackSize = stackType.getStackSize - accepted
//            }
//            return stackType
//        }
//
//        override fun getChannel(): StorageChannel = StorageChannel.FLUIDS
//        override fun extractItems(stackType: IAEFluidStack, actionable: Actionable, baseActionSource: BaseActionSource): IAEFluidStack {
//            val gasStack: GasStack = GasUtil.getGasStack(stackType)
//            if (gasStack == null) return null
//            if (tank.canDraw(gasStack.gas)) {
//                val drawed: GasStack = tank.draw(gasStack.amount, actionable == Actionable.MODULATE)
//                return GasUtil.createAEFluidStack(drawed)
//            }
//            return null
//        }
//
//        override fun getAvailableItems(itemList: IItemList<IAEFluidStack>): IItemList<IAEFluidStack> {
//            val gas: GasStack = tank.gas
//            if (gas != null) itemList.add(GasUtil.createAEFluidStack(tank.gas))
//            return itemList
//        }
//    }
//}