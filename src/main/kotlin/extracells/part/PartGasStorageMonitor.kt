//package extracells.part
//
//import appeng.api.networking.storage.IStackWatcher
//import appeng.api.parts.IPartHost
//import appeng.api.storage.data.IAEFluidStack
//import appeng.api.storage.data.IAEStack
//import extracells.integration.Integration
//import extracells.integration.Integration.Mods
//import extracells.util.FluidUtil
//import extracells.util.GasUtil
//import extracells.util.WrenchUtil
//import mekanism.api.gas.GasStack
//import net.minecraft.entity.player.EntityPlayer
//import net.minecraft.item.ItemStack
//import net.minecraft.tileentity.TileEntity
//import net.minecraft.util.ChatComponentTranslation
//import net.minecraft.util.IChatComponent
//import net.minecraft.util.Vec3
//import net.minecraft.world.World
//import net.minecraftforge.fluids.Fluid
//import net.minecraftforge.fluids.FluidStack
//
//open class PartGasStorageMonitor : PartFluidStorageMonitor() {
//    val isMekEnabled: Boolean = Integration.Mods.MEKANISMGAS.isEnabled
//    override fun onActivate(player: EntityPlayer, pos: Vec3): Boolean {
//        return if (isMekEnabled) onActivateGas(player, pos) else false
//    }
//
//    fun onActivateGas(player: EntityPlayer, pos: Vec3): Boolean {
//        if (player == null || player.worldObj == null) return true
//        if (player.worldObj.isRemote) return true
//        val s: ItemStack = player.currentEquippedItem
//        if (s == null) {
//            if (this.locked) return false
//            if (this.fluid == null) return true
//            if (this.watcher != null) this.watcher.remove(FluidUtil.createAEFluidStack(this.fluid))
//            this.fluid = null
//            this.amount = 0L
//            val host: IPartHost = host
//            if (host != null) host.markForUpdate()
//            return true
//        }
//        if (WrenchUtil.canWrench(s, player, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord)) {
//            this.locked = !this.locked
//            WrenchUtil.wrenchUsed(s, player, this.tile.xCoord, this.tile.zCoord, this.tile.yCoord)
//            val host: IPartHost = host
//            if (host != null) host.markForUpdate()
//            if (this.locked) player.addChatMessage(ChatComponentTranslation("chat.appliedenergistics2.isNowLocked")) else player.addChatMessage(ChatComponentTranslation("chat.appliedenergistics2.isNowUnlocked"))
//            return true
//        }
//        if (this.locked) return false
//        if (GasUtil.isFilled(s)) {
//            if (this.fluid != null && this.watcher != null) this.watcher.remove(FluidUtil.createAEFluidStack(this.fluid))
//            val gas: GasStack = GasUtil.getGasFromContainer(s)
//            val fluidStack: FluidStack = GasUtil.getFluidStack(gas)
//            this.fluid = run {
//                if (fluidStack == null) null else fluidStack.getFluid()
//            }
//            if (this.watcher != null) this.watcher.add(FluidUtil.createAEFluidStack(this.fluid))
//            val host: IPartHost = host
//            if (host != null) host.markForUpdate()
//            return true
//        }
//        return false
//    }
//}