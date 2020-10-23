//package extracells.item
//
//import appeng.api.AEApi
//import appeng.api.IAppEngApi
//import appeng.api.config.AccessRestriction
//import appeng.api.config.FuzzyMode
//import appeng.api.features.IRegistryContainer
//import appeng.api.storage.ICellRegistry
//import appeng.api.storage.IMEInventoryHandler
//import appeng.api.storage.ISaveProvider
//import appeng.api.storage.StorageChannel
//import appeng.api.storage.data.IAEFluidStack
//import extracells.api.ECApi
//import extracells.api.ExtraCellsApi
//import extracells.api.IHandlerFluidStorage
//import extracells.api.IPortableGasStorageCell
//import extracells.util.inventory.ECFluidFilterInventory
//import extracells.util.inventory.ECPrivateInventory
//import net.minecraft.client.renderer.texture.IIconRegister
//import net.minecraft.creativetab.CreativeTabs
//import net.minecraft.entity.player.EntityPlayer
//import net.minecraft.inventory.IInventory
//import net.minecraft.item.Item
//import net.minecraft.item.ItemStack
//import net.minecraft.nbt.NBTTagCompound
//import net.minecraft.util.IIcon
//import net.minecraft.util.StatCollector
//import net.minecraft.world.World
//import net.minecraftforge.fluids.Fluid
//import net.minecraftforge.fluids.FluidRegistry
//
//object ItemStoragePortableGasCell : ItemECBase(), IPortableGasStorageCell, PowerItem {
//    val MAX_POWER: Double = 20000.0
//    override fun MAX_POWER(): Double = MAX_POWER
//
//    internal var icon: IIcon? = null
//    override fun addInformation(itemStack: ItemStack, player: EntityPlayer, list: List<*>, par4: Boolean): Unit {
//        val list2: List<String> = (list as List<String>)
//        val handler: IMEInventoryHandler<IAEFluidStack> = (AEApi.instance().registries().cell().getCellInventory(itemStack, null, StorageChannel.FLUIDS) as IMEInventoryHandler<IAEFluidStack>)
//        if (!((handler is IHandlerFluidStorage))) {
//            return
//        }
//        val cellHandler: IHandlerFluidStorage = (handler as IHandlerFluidStorage)
//        val partitioned: Boolean = cellHandler.isFormatted
//        val usedBytes: Long = cellHandler.usedBytes().toLong()
//        val aeCurrentPower: Double = getAECurrentPower(itemStack)
//        list2.add(String.format(StatCollector.translateToLocal("extracells.tooltip.storage.gas.bytes"), ((usedBytes / 250) as Any), ((cellHandler.totalBytes / 250) as Any)))
//        list2.add(String.format(StatCollector.translateToLocal("extracells.tooltip.storage.gas.types"), (cellHandler.usedTypes() as Any), (cellHandler.totalTypes() as Any)))
//        if (usedBytes != 0) {
//            list2.add(String.format(StatCollector.translateToLocal("extracells.tooltip.storage.gas.content"), (usedBytes as Any)))
//        }
//        if (partitioned) {
//            list2.add(StatCollector.translateToLocal("gui.appliedenergistics2.Partitioned") + " - " + StatCollector.translateToLocal("gui.appliedenergistics2.Precise"))
//        }
//        list2.add(StatCollector.translateToLocal("gui.appliedenergistics2.StoredEnergy") + ": " + aeCurrentPower + " AE - " + Math.floor(aeCurrentPower / ItemStoragePortableFluidCell.MAX_POWER * 1e4) / 1e2 + "%")
//    }
//
//    override fun getConfigInventory(is: ItemStack): IInventory = ECFluidFilterInventory("configFluidCell", 63, is)
//    override fun getDurabilityForDisplay(itemStack: ItemStack): Double = 1 - getAECurrentPower(itemStack) / ItemStoragePortableFluidCell.MAX_POWER
//    override fun getFilter(stack: ItemStack): ArrayList<Fluid> {
//        val inventory: ECFluidFilterInventory = ECFluidFilterInventory("", 63, stack)
//        val stacks: scala.Array<ItemStack> = inventory.slots
//        val filter: ArrayList<Fluid> = ArrayList<Fluid>()
//        if (stacks.length == 0) return null
//        for (ItemStack in stacks) {
//            if (s != null) {
//                val f: Fluid = FluidRegistry.getFluid(s.getItemDamage())
//                if (f != null) filter.add(f)
//            }
//        }
//        return filter
//    }
//
//    override fun getFuzzyMode(is: ItemStack): FuzzyMode {
//        if ( is == null) return null
//        if ( !is.hasTagCompound) is .setTagCompound(NBTTagCompound())
//        if ( is.getTagCompound().hasKey("fuzzyMode")) return FuzzyMode.valueOf( is .getTagCompound().getString("fuzzyMode"))
//        is.getTagCompound().setString("fuzzyMode", FuzzyMode.IGNORE_ALL.name())
//        return FuzzyMode.IGNORE_ALL
//    }
//
//    override fun getIconFromDamage(dmg: Int): IIcon = this.icon
//    override fun getMaxBytes(is: ItemStack): Int = 512
//    override fun getMaxTypes(unused: ItemStack): Int = 3
//    override fun getPowerFlow(itemStack: ItemStack): AccessRestriction = AccessRestriction.READ_WRITE
//    override fun getSubItems(item: Item, creativeTab: CreativeTabs, itemList: List<*>): Unit {
//        val itemList2: List<ItemStack> = (itemList as List<ItemStack>)
//        itemList2.add(ItemStack(item))
//        val itemStack: ItemStack = ItemStack(item)
//        injectAEPower(itemStack, ItemStoragePortableFluidCell.MAX_POWER)
//        itemList2.add(itemStack)
//    }
//
//    override fun getUnlocalizedName(itemStack: ItemStack): String = "extracells.item.storage.gas.portable"
//    override fun getUpgradesInventory(is: ItemStack): IInventory = ECPrivateInventory("configInventory", 0, 64)
//    override fun hasPower(player: EntityPlayer, amount: Double, is: ItemStack): Boolean = getAECurrentPower(is) >= amount
//    override fun isEditable(is: ItemStack): Boolean {
//        if ( is == null) return false
//        return is.getItem == this
//    }
//
//    override fun onItemRightClick(itemStack: ItemStack, world: World, player: EntityPlayer): ItemStack = ECApi.instance().openPortableGasCellGui(player, itemStack, world)
//    override fun registerIcons(iconRegister: IIconRegister): Unit {
//        this.icon = iconRegister.registerIcon("extracells:storage.gas.portable")
//    }
//
//    override fun setFuzzyMode(is: ItemStack, fzMode: FuzzyMode): Unit {
//        if ( is == null) return
//        if ( !is.hasTagCompound) is .setTagCompound(NBTTagCompound())
//        val tag: NBTTagCompound = is .getTagCompound()
//        tag.setString("fuzzyMode", fzMode.name())
//    }
//
//    override fun showDurabilityBar(itemStack: ItemStack): Boolean = true
//    override fun usePower(player: EntityPlayer, amount: Double, is: ItemStack): Boolean {
//        extractAEPower(is, amount)
//        return true
//    }
//}