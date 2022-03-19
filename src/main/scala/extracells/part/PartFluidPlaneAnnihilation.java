package extracells.part;

import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.events.MENetworkChannelChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.MachineSource;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.util.AEColor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import extracells.gridblock.ECBaseGridBlock;
import extracells.render.TextureManager;
import extracells.util.FluidUtil;
import extracells.util.PermissionUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class PartFluidPlaneAnnihilation extends PartECBase {

    @Override
    public int cableConnectionRenderTo() {
        return 2;
    }

    @SuppressWarnings("unused")
    @MENetworkEventSubscribe
    public void channelChanged(MENetworkChannelChanged e) {
        if (e.node == this.getGridNode())
            this.onNeighborChanged();
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(5, 5, 13, 11, 11, 14);
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public double getPowerUsage() {
        return 1.0D;
    }

    @Override
    public boolean onActivate(EntityPlayer player, Vec3 pos) {
        if (PermissionUtil.hasPermission(player, SecurityPermissions.BUILD, (IPart) this)) {
            return super.onActivate(player, pos);
        }
        return false;
    }

    @Override
    public void onNeighborChanged() {
        TileEntity hostTile = this.getHostTile();
        ECBaseGridBlock gridBlock = this.getGridBlock();
        if (hostTile == null || gridBlock == null)
            return;
        IMEMonitor<IAEFluidStack> monitor = gridBlock.getFluidMonitor();
        if (monitor == null)
            return;
        World world = hostTile.getWorldObj();
        int x = hostTile.xCoord;
        int y = hostTile.yCoord;
        int z = hostTile.zCoord;
        ForgeDirection side = this.getSide();
        int xx = x + side.offsetX;
        int yy = y + side.offsetY;
        int zz = z + side.offsetZ;
        Block fluidBlock = world.getBlock(xx, yy, zz);
        int meta = world.getBlockMetadata(xx, yy, zz);

        if (fluidBlock instanceof IFluidBlock) {
            IFluidBlock block = (IFluidBlock) fluidBlock;

            // TODO gamerforEA code replace, old code:
            // FluidStack drained = block.drain(world, xx, yy, zz, false);
            FluidStack drained = null;
            try {
                drained = block.drain(world, xx, yy, zz, false);
            } catch (Throwable ignored) {
                // NPE in net.minecraftforge.fluids.BlockFluidFinite.drain -> net.minecraftforge.fluids.FluidStack.<init>
            }
            // TODO gamerforEA code end

            if (drained == null)
                return;
            IAEFluidStack toInject = FluidUtil.createAEFluidStack(drained);
            IAEFluidStack notInjected = monitor.injectItems(toInject, Actionable.SIMULATE, new MachineSource(this));
            if (notInjected != null)
                return;
            monitor.injectItems(toInject, Actionable.MODULATE, new MachineSource(this));
            block.drain(world, xx, yy, zz, true);
        } else if (meta == 0) {
            if (fluidBlock == Blocks.flowing_water) {
                IAEFluidStack toInject = FluidUtil.createAEFluidStack(FluidRegistry.WATER);
                IAEFluidStack notInjected = monitor.injectItems(toInject, Actionable.SIMULATE, new MachineSource(this));
                if (notInjected != null)
                    return;
                monitor.injectItems(toInject, Actionable.MODULATE, new MachineSource(this));
                world.setBlockToAir(xx, yy, zz);
            } else if (fluidBlock == Blocks.flowing_lava) {
                IAEFluidStack toInject = FluidUtil.createAEFluidStack(FluidRegistry.LAVA);
                IAEFluidStack notInjected = monitor.injectItems(toInject, Actionable.SIMULATE, new MachineSource(this));
                if (notInjected != null)
                    return;
                monitor.injectItems(toInject, Actionable.MODULATE, new MachineSource(this));
                world.setBlockToAir(xx, yy, zz);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventory(IPartRenderHelper rh, RenderBlocks renderer) {
        IIcon side = TextureManager.PANE_SIDE.getTexture();
        rh.setTexture(side, side, side, TextureManager.BUS_BORDER.getTexture(), side, side);
        rh.setBounds(2, 2, 14, 14, 14, 16);
        rh.renderInventoryBox(renderer);
        rh.setBounds(3, 3, 14, 13, 13, 16);
        rh.setInvColor(AEColor.Cyan.blackVariant);
        rh.renderInventoryFace(TextureManager.PANE_FRONT.getTextures()[0], ForgeDirection.SOUTH, renderer);
        Tessellator.instance.setBrightness(13 << 20 | 13 << 4);
        rh.setInvColor(AEColor.Cyan.mediumVariant);
        rh.renderInventoryFace(TextureManager.PANE_FRONT.getTextures()[1], ForgeDirection.SOUTH, renderer);
        rh.setInvColor(AEColor.Cyan.whiteVariant);
        rh.renderInventoryFace(TextureManager.PANE_FRONT.getTextures()[2], ForgeDirection.SOUTH, renderer);

        rh.setBounds(5, 5, 13, 11, 11, 14);
        this.renderInventoryBusLights(rh, renderer);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderStatic(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer) {
        Tessellator ts = Tessellator.instance;
        IIcon side = TextureManager.PANE_SIDE.getTexture();
        rh.setTexture(side, side, side, TextureManager.BUS_BORDER.getTexture(), side, side);
        rh.setBounds(2, 2, 14, 14, 14, 16);
        rh.renderBlock(x, y, z, renderer);
        rh.setBounds(3, 3, 14, 13, 13, 16);
        IPartHost host = this.getHost();
        if (host != null) {
            ts.setColorOpaque_I(host.getColor().blackVariant);
            rh.renderFace(x, y, z, TextureManager.PANE_FRONT.getTextures()[0], ForgeDirection.SOUTH, renderer);
            if (this.isActive())
                ts.setBrightness(13 << 20 | 13 << 4);
            ts.setColorOpaque_I(host.getColor().mediumVariant);
            rh.renderFace(x, y, z, TextureManager.PANE_FRONT.getTextures()[1], ForgeDirection.SOUTH, renderer);
            ts.setColorOpaque_I(host.getColor().whiteVariant);
            rh.renderFace(x, y, z, TextureManager.PANE_FRONT.getTextures()[2], ForgeDirection.SOUTH, renderer);
        }

        rh.setBounds(5, 5, 13, 11, 11, 14);
        this.renderStaticBusLights(x, y, z, rh, renderer);
    }

    @Override
    @SuppressWarnings("unused")
    @MENetworkEventSubscribe
    public void setPower(MENetworkPowerStatusChange notUsed) {
        super.setPower(notUsed);
        this.onNeighborChanged();
    }
}
