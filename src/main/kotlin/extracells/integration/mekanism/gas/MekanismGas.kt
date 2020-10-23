//package extracells.integration.mekanism.gas
//
//import appeng.api.AEApi
//import appeng.api.IAppEngApi
//import appeng.api.features.IRegistryContainer
//import appeng.api.parts.IPartHelper
//import appeng.api.storage.ICellHandler
//import appeng.api.storage.ICellRegistry
//import extracells.api.ECApi
//import extracells.api.ExtraCellsApi
//import extracells.integration.mekanism.gas.MekanismGas.GasFluid
//import mekanism.api.gas.Gas
//import mekanism.api.gas.GasRegistry
//import mekanism.api.gas.IGasHandler
//import mekanism.api.gas.ITubeConnection
//import net.minecraft.util.IIcon
//import net.minecraftforge.fluids.Fluid
//import net.minecraftforge.fluids.FluidStack
//import java.util.List
//
//object MekanismGas {
//    internal var fluidGas: scala.collection.immutable.Map<Gas, Fluid> = Map.apply()
//    fun init(): Unit {
//        val api: IPartHelper = AEApi.instance().partHelper()
//        api.registerNewLayer(classOf<LayerGasHandler>().getName(), classOf<IGasHandler>().getName())
//        api.registerNewLayer(classOf<LayerTubeConnection>().getName(), classOf<ITubeConnection>().getName())
//        AEApi.instance().registries().cell().addCellHandler(GasCellHandler())
//    }
//
//    fun getFluidGasMap(): Map<Gas, Fluid> = mapAsJavaMap(fluidGas)
//    fun postInit(): Unit {
//        val it: Iterator<Gas> = GasRegistry.getRegisteredGasses().iterator()
//        /* ERROR converting `while(it.hasNext){
//      val g = it.next
//      val fluid = new GasFluid(g)
//      if((!FluidRegistry.isFluidRegistered(fluid)) && FluidRegistry.registerFluid(fluid))
//        fluidGas += (g -> fluid)
//    }`*/
//        ECApi.instance().addFluidToShowBlacklist(classOf<GasFluid>())
//        ECApi.instance().addFluidToStorageBlacklist(classOf<GasFluid>())
//    }
//
//    open class GasFluid(private val gas: Gas) : Fluid("ec.internal." + gas.name) {
//        override fun getLocalizedName(stack: FluidStack): String = gas.localizedName
//        override fun getIcon(): IIcon = gas.icon
//        override fun getStillIcon(): IIcon = gas.icon
//        override fun getFlowingIcon(): IIcon = gas.icon
//        fun getGas(): Gas = gas
//    }
//}