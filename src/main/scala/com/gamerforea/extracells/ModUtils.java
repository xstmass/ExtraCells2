package com.gamerforea.extracells;

import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IStackWatcherHost;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import extracells.part.PartECBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import java.lang.reflect.Method;

public final class ModUtils
{
	public static boolean isUseableByPlayer(Object object, EntityPlayer player)
	{
		if (object == null || player == null)
			return false;
		if (object instanceof TileEntity)
			return isUseableByPlayer((TileEntity) object, player);
		if (object instanceof PartECBase)
			return isUseableByPlayer((PartECBase) object, player);
		if (object instanceof IInventory)
			return ((IInventory) object).isUseableByPlayer(player);
		return true;
	}

	public static boolean isUseableByPlayer(TileEntity tile, EntityPlayer player)
	{
		if (tile == null || player == null || !tile.hasWorldObj())
			return false;
		if (tile instanceof IInventory)
			return ((IInventory) tile).isUseableByPlayer(player);
		return tile.getWorldObj().getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord) == tile && player.getDistanceSq(tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5) <= 64;
	}

	public static boolean isUseableByPlayer(PartECBase part, EntityPlayer player)
	{
		if (part == null || player == null)
			return false;
		if (part instanceof IInventory)
			return ((IInventory) part).isUseableByPlayer(player);
		return part.isValid();
	}

	public static <T extends IStackWatcherHost> boolean canSendDirtyFlagForStackWatcherHost(T instance, Class<? extends T> expectedClass)
	{
		return isMethodDeclaredInClass(instance, expectedClass, "onStackChange", IItemList.class, IAEStack.class, IAEStack.class, BaseActionSource.class, StorageChannel.class);
	}

	public static <T> boolean isMethodDeclaredInClass(T instance, Class<? extends T> expectedClass, String methodName, Class<?>... parameterTypes)
	{
		if (instance == null)
			return false;
		Class<?> instanceClass = instance.getClass();
		Method method;
		try
		{
			method = instanceClass.getMethod(methodName, parameterTypes);
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
			return false;
		}
		return method.getDeclaringClass() == expectedClass;
	}
}
