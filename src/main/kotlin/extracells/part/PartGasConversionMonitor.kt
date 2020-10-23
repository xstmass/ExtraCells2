//package extracells.part
//open class PartGasConversionMonitor : PartFluidConversionMonitor() {
//   val isMekEnabled: Boolean = Integration.Mods.MEKANISMGAS.isEnabled()
//  override fun onActivate(player: EntityPlayer, pos: Vec3): Boolean {
//    return if (isMekEnabled) onActivateGas(player, pos) else false
//  }
//  fun onActivateGas(player: EntityPlayer, pos: Vec3): Boolean {
//     val b: Boolean = /* ERROR converting `super`*/.onActivate(player, pos)
//    if (b) return b
//    if (player == null || player.worldObj == null) return true
//    if (player.worldObj.isRemote) return true
//     val s: ItemStack = player.getCurrentEquippedItem()
//     val mon: IMEMonitor<IAEFluidStack> = getFluidStorage()
//    if (this.locked && s != null && mon != null) {
//       val s2: ItemStack = s.copy()
//      s2.stackSize = 1
//      if (GasUtil.isFilled(s2)) {
//         val g: GasStack = GasUtil.getGasFromContainer(s2)
//         val f: FluidStack = GasUtil.getFluidStack(g)
//        if (f == null) return true
//         val fl: IAEFluidStack = FluidUtil.createAEFluidStack(f)
//         val not: IAEFluidStack = mon.injectItems(fl.copy(), Actionable.SIMULATE, MachineSource(this))
//        if (mon.canAccept(fl) && (not == null || not.getStackSize == 0L)) {
//          mon.injectItems(fl, Actionable.MODULATE, MachineSource(this))
//           val empty1: MutablePair<Integer, ItemStack> = GasUtil.drainStack(s2, g)
//           val empty: ItemStack = empty1.right
//          if (empty != null) {
//            dropItems(getHost().getTile().getWorldObj(), getHost().getTile().xCoord + getSide().offsetX, getHost().getTile().yCoord + getSide().offsetY, getHost().getTile().zCoord + getSide().offsetZ, empty)
//          }
//           val s3: ItemStack = s.copy()
//          s3.stackSize = s3.stackSize - 1
//          if (s3.stackSize == 0) {
//            player.inventory.setInventorySlotContents(player.inventory.currentItem, null)
//          } else {
//            player.inventory.setInventorySlotContents(player.inventory.currentItem, s3)
//          }
//        }
//        return true
//      } else if (GasUtil.isEmpty(s2)) {
//        if (this.fluid == null) return true
//         var extract: IAEFluidStack = null
//        if ((s2.getItem() is IGasItem)) {
//          extract = mon.extractItems(GasUtil.createAEFluidStack(GasUtil.getGas(this.fluid), ((s2.getItem() as IGasItem)).getMaxGas(s2)), Actionable.SIMULATE, MachineSource(this))
//        } else return true
//        if (extract != null) {
//          extract = mon.extractItems(extract, Actionable.MODULATE, MachineSource(this))
//          if (extract == null || extract.getStackSize <= 0) {
//            return true
//          }
//           val empty1: MutablePair<Integer, ItemStack> = GasUtil.fillStack(s2, GasUtil.getGasStack(extract.getFluidStack()))
//          if (empty1.left == 0) {
//            mon.injectItems(FluidUtil.createAEFluidStack(FluidStack(this.fluid, extract.getStackSize().toInt)), Actionable.MODULATE, MachineSource(this))
//            return true
//          }
//           val empty: ItemStack = empty1.right
//          if (empty != null) {
//            dropItems(getHost().getTile().getWorldObj(), getHost().getTile().xCoord + getSide().offsetX, getHost().getTile().yCoord + getSide().offsetY, getHost().getTile().zCoord + getSide().offsetZ, empty)
//          }
//           val s3: ItemStack = s.copy()
//          s3.stackSize = s3.stackSize - 1
//          if (s3.stackSize == 0) {
//            player.inventory.setInventorySlotContents(player.inventory.currentItem, null)
//          } else {
//            player.inventory.setInventorySlotContents(player.inventory.currentItem, s3)
//          }
//        }
//        return true
//      }
//    }
//    return false
//  }
//  fun storageMonitor(player: EntityPlayer, pos: Vec3): Boolean {
//    if (player == null || player.worldObj == null) return true
//    if (player.worldObj.isRemote) return true
//     val s: ItemStack = player.getCurrentEquippedItem()
//    if (s == null) {
//      if (this.locked) return false
//      if (this.fluid == null) return true
//      if (this.watcher != null) this.watcher.remove(FluidUtil.createAEFluidStack(this.fluid))
//      this.fluid = null
//      this.amount = 0L
//       val host: IPartHost = getHost()
//      if (host != null) host.markForUpdate()
//      return true
//    }
//    if (WrenchUtil.canWrench(s, player, this.tile.xCoord, this.tile.yCoord, this.tile.zCoord)) {
//      this.locked = !this.locked
//      WrenchUtil.wrenchUsed(s, player, this.tile.xCoord, this.tile.zCoord, this.tile.yCoord)
//       val host: IPartHost = getHost()
//      if (host != null) host.markForUpdate()
//      if (this.locked) player.addChatMessage(ChatComponentTranslation("chat.appliedenergistics2.isNowLocked")) else player.addChatMessage(ChatComponentTranslation("chat.appliedenergistics2.isNowUnlocked"))
//      return true
//    }
//    if (this.locked) return false
//    if (GasUtil.isFilled(s)) {
//      if (this.fluid != null && this.watcher != null) this.watcher.remove(FluidUtil.createAEFluidStack(this.fluid))
//       val gas: GasStack = GasUtil.getGasFromContainer(s)
//       val fluidStack: FluidStack = GasUtil.getFluidStack(gas)
//      this.fluid = run {
//        if (fluidStack == null) null else fluidStack.getFluid()
//      }
//      if (this.watcher != null) this.watcher.add(FluidUtil.createAEFluidStack(this.fluid))
//       val host: IPartHost = getHost()
//      if (host != null) host.markForUpdate()
//      return true
//    }
//    return false
//  }
//}