//package extracells.part
//
//import appeng.api.parts.IPartCollisionHelper
//import appeng.api.parts.IPartRenderHelper
//import extracells.integration.mekanism.gas.GasInterfaceBase
//import mekanism.api.gas.GasTank
//import net.minecraft.client.renderer.RenderBlocks
//import net.minecraftforge.common.util.ForgeDirection
//
//open class PartGasInterface : PartECBase(), GasInterfaceBase {
//    var fluidFilter: Int = 1.-()
//    override fun cableConnectionRenderTo(): Int =$qmark$qmark$qmark()
//    override fun renderStatic(x: Int, y: Int, z: Int, rh: IPartRenderHelper, renderer: RenderBlocks): Unit =$qmark$qmark$qmark()
//    override fun renderInventory(rh: IPartRenderHelper, renderer: RenderBlocks): Unit =$qmark$qmark$qmark()
//    override fun getBoxes(bch: IPartCollisionHelper): Unit =$qmark$qmark$qmark()
//    override fun getGasTank(side: ForgeDirection): GasTank =$qmark$qmark$qmark()
//    override fun getFilter(side: ForgeDirection): Int = fluidFilter
//    override fun isMekanismLoaded(): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun setFilter(side: ForgeDirection, fluid: Int): Unit = fluidFilter = fluid
//}