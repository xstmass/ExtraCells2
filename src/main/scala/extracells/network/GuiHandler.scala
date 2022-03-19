package extracells.network

import appeng.api.parts.IPartHost
import appeng.api.storage.IMEMonitor
import appeng.api.storage.data.IAEFluidStack
import cpw.mods.fml.common.network.IGuiHandler
import cpw.mods.fml.relauncher.{Side, SideOnly}
import extracells.Extracells
import extracells.api._
import extracells.block.TGuiBlock
import extracells.container._
import extracells.gui._
import extracells.part.PartECBase
import extracells.registries.BlockEnum
import extracells.tileentity.{TileEntityFluidCrafter, TileEntityFluidFiller, TileEntityFluidInterface}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection;

object GuiHandler extends IGuiHandler {

  var temp: Array[Any] = Array[Any]()

  def getGuiId(guiId: Int) = guiId + 6

  def getGuiId(part: PartECBase) = part.getSide().ordinal()

  def launchGui(ID: Int, player: EntityPlayer, args: Array[Any]) {
    temp = args
    player.openGui(Extracells, ID, null, 0, 0, 0);
  }

  def launchGui(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any =
    player.openGui(Extracells, ID, world, x, y, z);

  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    val gui: Any = getGuiBlockElement(player, world, x, y, z)
    if (gui != null)
      return gui.asInstanceOf[AnyRef]
    val side = ForgeDirection.getOrientation(ID);
    if (world.getBlock(x, y, z) == BlockEnum.FLUIDCRAFTER.getBlock()) {
      val tileEntity = world.getTileEntity(x, y, z);
      if (tileEntity == null || !(tileEntity.isInstanceOf[TileEntityFluidCrafter]))
        return null
      return new GuiFluidCrafter(player.inventory, tileEntity.asInstanceOf[TileEntityFluidCrafter].getInventory())
    }
    if (world != null
      && world.getBlock(x, y, z) == BlockEnum.ECBASEBLOCK.getBlock()) {
      val tileEntity = world.getTileEntity(x, y, z)
      if (tileEntity == null)
        return null
      if (tileEntity.isInstanceOf[TileEntityFluidInterface])
        return new GuiFluidInterface(player, tileEntity.asInstanceOf[IFluidInterface])
      else if (tileEntity.isInstanceOf[TileEntityFluidFiller])
        return new GuiFluidFiller(player, tileEntity.asInstanceOf[TileEntityFluidFiller])
      return null;
    }
    if (world != null && side != ForgeDirection.UNKNOWN)
      return getPartGui(side, player, world, x, y, z).asInstanceOf[AnyRef]
    getGui(ID - 6, player).asInstanceOf[AnyRef]
  }

  @SideOnly(Side.CLIENT)
  def getGui(ID: Int, player: EntityPlayer): Any = {
    ID match {
      case 0 =>
        new GuiFluidStorage(player, "extracells.part.fluid.terminal.name");
      case 1 =>
        new GuiFluidStorage(player, "extracells.part.fluid.terminal.name");
      case 3 =>
        new GuiFluidStorage(player, "extracells.item.storage.fluid.portable.name");
      case 4 =>
        new GuiGasStorage(player, "extracells.part.gas.terminal.name");
      case 5 =>
        new GuiGasStorage(player, "extracells.part.gas.terminal.name");
      case 6 =>
        new GuiGasStorage(player, "extracells.item.storage.gas.portable.name");
      case _ =>
        null;
    }
  }

  def getPartGui(side: ForgeDirection, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any =
    world.getTileEntity(x, y, z).asInstanceOf[IPartHost].getPart(side).asInstanceOf[PartECBase]
      .getClientGuiElement(player)

  def getGuiBlockElement(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any = {
    if (world == null || player == null)
      return null
    val block = world.getBlock(x, y, z)
    if (block == null)
      return null
    block match {
      case guiBlock: TGuiBlock => return guiBlock.getClientGuiElement(player, world, x, y, z)
      case _ => return null
    }
  }

  override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    val con: Any = getContainerBlockElement(player, world, x, y, z)
    if (con != null)
      return con.asInstanceOf[AnyRef]
    val side = ForgeDirection.getOrientation(ID)
    if (world != null && world.getBlock(x, y, z) == BlockEnum.FLUIDCRAFTER.getBlock()) {
      val tileEntity = world.getTileEntity(x, y, z)
      if (tileEntity == null || !(tileEntity.isInstanceOf[TileEntityFluidCrafter]))
        return null
      return new ContainerFluidCrafter(player.inventory,
        tileEntity.asInstanceOf[TileEntityFluidCrafter].getInventory())
    }
    if (world != null && world.getBlock(x, y, z) == BlockEnum.ECBASEBLOCK.getBlock()) {
      val tileEntity = world.getTileEntity(x, y, z)
      if (tileEntity == null)
        return null
      if (tileEntity.isInstanceOf[TileEntityFluidInterface])
        return new ContainerFluidInterface(player, tileEntity.asInstanceOf[IFluidInterface]);
      else if (tileEntity.isInstanceOf[TileEntityFluidFiller])
        return new ContainerFluidFiller(player.inventory, tileEntity.asInstanceOf[TileEntityFluidFiller])
      return null
    }
    if (world != null && side != ForgeDirection.UNKNOWN)
      return getPartContainer(side, player, world, x, y, z).asInstanceOf[AnyRef]
    getContainer(ID - 6, player, temp).asInstanceOf[AnyRef]
  }

  def getContainer(ID: Int, player: EntityPlayer, args: Array[Any]): Any = {
    ID match {
      case 0 =>
        val fluidInventory = args.apply(0).asInstanceOf[IMEMonitor[IAEFluidStack]]
        new ContainerFluidStorage(fluidInventory, player)
      case 1 =>
        val fluidInventory2 = args.apply(0).asInstanceOf[IMEMonitor[IAEFluidStack]]
        val handler = args.apply(1).asInstanceOf[IWirelessFluidTermHandler]
        new ContainerFluidStorage(fluidInventory2, player, handler)
      case 3 =>
        val fluidInventory3 = args.apply(0).asInstanceOf[IMEMonitor[IAEFluidStack]]
        val storageCell = args.apply(1).asInstanceOf[IPortableFluidStorageCell]
        new ContainerFluidStorage(fluidInventory3, player, storageCell)
      case 4 =>
        val fluidInventory = args.apply(0).asInstanceOf[IMEMonitor[IAEFluidStack]]
        new ContainerGasStorage(fluidInventory, player)
      case 5 =>
        val fluidInventory2 = args.apply(0).asInstanceOf[IMEMonitor[IAEFluidStack]]
        val handler = args.apply(1).asInstanceOf[IWirelessGasTermHandler]
        new ContainerGasStorage(fluidInventory2, player, handler)
      case 6 =>
        val fluidInventory3 = args.apply(0).asInstanceOf[IMEMonitor[IAEFluidStack]]
        val storageCell = args.apply(1).asInstanceOf[IPortableGasStorageCell]
        new ContainerGasStorage(fluidInventory3, player, storageCell)
      case _ =>
        null
    }
  }

  def getPartContainer(side: ForgeDirection, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any =
    world.getTileEntity(x, y, z).asInstanceOf[IPartHost].getPart(side).asInstanceOf[PartECBase]
      .getServerGuiElement(player)

  def getContainerBlockElement(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any = {
    if (world == null || player == null)
      return null
    val block = world.getBlock(x, y, z)
    if (block == null)
      return null
    block match {
      case guiBlock: TGuiBlock => return guiBlock.getServerGuiElement(player, world, x, y, z)
      case _ => return null
    }
  }
}
