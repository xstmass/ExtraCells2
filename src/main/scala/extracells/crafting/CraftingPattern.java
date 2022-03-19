package extracells.crafting;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import extracells.api.crafting.IFluidCraftingPatternDetails;
import extracells.registries.ItemEnum;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import java.util.HashMap;
import java.util.Map;

public class CraftingPattern implements IFluidCraftingPatternDetails, Comparable<CraftingPattern> {
    protected final ICraftingPatternDetails pattern;

    private IAEFluidStack[] fluidsCondensed = null;
    private IAEFluidStack[] fluids = null;

    public CraftingPattern(ICraftingPatternDetails _pattern) {
        this.pattern = _pattern;
    }

    // TODO gamerforEA code start
    private static <T extends IAEStack<T>> T[] condenceStacks(T[] stacks, T[] arrayToReturn) {
        Map<T, T> tmpMap = new HashMap<>();
        for (T stack : stacks) {
            if (stack != null) {
                T tmpStack = tmpMap.get(stack);
                if (tmpStack == null)
                    tmpMap.put(stack, stack.copy());
                else
                    tmpStack.add(stack);
            }
        }
        return tmpMap.values().toArray(arrayToReturn);
    }

    @Override
    public boolean canSubstitute() {
        return this.pattern.canSubstitute();
    }

    public int compareInt(int int1, int int2) {
        if (int1 == int2)
            return 0;
        if (int1 < int2)
            return -1;
        return 1;
    }

    @Override
    public int compareTo(CraftingPattern o) {
        return this.compareInt(o.getPriority(), this.getPriority());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        CraftingPattern other = (CraftingPattern) obj;
        return this.pattern != null && other.pattern != null && this.pattern.equals(other.pattern);
    }

    @Override
    public IAEFluidStack[] getCondensedFluidInputs() {
        if (this.fluidsCondensed == null)
            this.getCondensedInputs();
        return this.fluidsCondensed;
    }

    @Override
    public IAEItemStack[] getCondensedInputs() {
        return this.removeFluidContainers(this.pattern.getCondensedInputs(), true);
    }

    @Override
    public IAEItemStack[] getCondensedOutputs() {
        return this.pattern.getCondensedOutputs();
    }

    @Override
    public IAEFluidStack[] getFluidInputs() {
        if (this.fluids == null)
            this.getInputs();
        return this.fluids;
    }

    @Override
    public IAEItemStack[] getInputs() {
        return this.removeFluidContainers(this.pattern.getInputs(), false);
    }

    @Override
    public ItemStack getOutput(InventoryCrafting craftingInv, World world) {
        IAEItemStack[] input = this.pattern.getInputs();
        for (int i = 0; i < input.length; i++) {
            IAEItemStack stack = input[i];
            if (stack != null && FluidContainerRegistry.isFilledContainer(stack.getItemStack()))
                try {
                    craftingInv.setInventorySlotContents(i, input[i].getItemStack());
                } catch (Throwable ignored) {
                }
            else if (stack != null && stack.getItem() instanceof IFluidContainerItem)
                try {
                    craftingInv.setInventorySlotContents(i, input[i].getItemStack());
                } catch (Throwable ignored) {
                }
        }
        ItemStack returnStack = this.pattern.getOutput(craftingInv, world);
        for (int i = 0; i < input.length; i++) {
            IAEItemStack stack = input[i];
            if (stack != null && FluidContainerRegistry.isFilledContainer(stack.getItemStack()))
                craftingInv.setInventorySlotContents(i, null);
            else if (stack != null && stack.getItem() instanceof IFluidContainerItem)
                craftingInv.setInventorySlotContents(i, null);
        }

        return returnStack;
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return this.pattern.getOutputs();
    }

    @Override
    public ItemStack getPattern() {
        ItemStack p = this.pattern.getPattern();
        if (p == null)
            return null;
        ItemStack s = new ItemStack(ItemEnum.CRAFTINGPATTERN.getItem());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("item", p.writeToNBT(new NBTTagCompound()));
        s.setTagCompound(tag);
        return s;
    }

    @Override
    public int getPriority() {
        return this.pattern.getPriority();
    }

    @Override
    public void setPriority(int priority) {
        this.pattern.setPriority(priority);
    }

    @Override
    public boolean isCraftable() {
        return this.pattern.isCraftable();
    }

    @Override
    public boolean isValidItemForSlot(int slotIndex, ItemStack itemStack, World world) {
        return this.pattern.isValidItemForSlot(slotIndex, itemStack, world);
    }
    // TODO gamerforEA code end

    public IAEItemStack[] removeFluidContainers(IAEItemStack[] requirements, boolean isCondenced) {
        IAEItemStack[] resultItemStacks = new IAEItemStack[requirements.length];
        IAEFluidStack[] resultFluidStacks = new IAEFluidStack[requirements.length];

		/* TODO gamerforEA code replace, old code:
		int removed = 0;
		int i = 0;
		for (IAEItemStack requirement : requirements)
		{
			if (requirement != null)
			{
				ItemStack requirementSingleStack = requirement.getItemStack();
				requirementSingleStack.stackSize = 1;
				FluidStack requirementSingleFluidStack = null;
				if (FluidContainerRegistry.isFilledContainer(requirementSingleStack))
					requirementSingleFluidStack = FluidContainerRegistry.getFluidForFilledItem(requirementSingleStack);
				else if (requirement.getItem() instanceof IFluidContainerItem)
					requirementSingleFluidStack = ((IFluidContainerItem) requirement.getItem()).getFluid(requirementSingleStack);
				if (requirementSingleFluidStack == null)
					resultItemStacks[i] = requirement;
				else
				{
					removed++;
					resultFluidStacks[i] = AEApi.instance().storage().createFluidStack(new FluidStack(requirementSingleFluidStack.getFluid(), (int) (requirementSingleFluidStack.amount * requirement.getStackSize())));
				}
			}
			i++;
		}

		if (isCondenced)
		{
			int i2 = 0;
			IAEFluidStack[] fluids = new IAEFluidStack[removed];
			for (IAEFluidStack fluid : resultFluidStacks)
			{
				if (fluid != null)
				{
					fluids[i2] = fluid;
					i2++;
				}
			}
			int i3 = 0;
			IAEItemStack[] items = new IAEItemStack[requirements.length - removed];
			for (IAEItemStack item : resultItemStacks)
			{
				if (item != null)
				{
					items[i3] = item;
					i3++;
				}
			}
			resultItemStacks = items;
			this.fluidsCondensed = fluids;
		}
		else
			this.fluids = resultFluidStacks; */
        for (int i = 0; i < requirements.length; i++) {
            IAEItemStack requirement = requirements[i];
            if (requirement != null) {
                ItemStack requirementSingleStack = requirement.getItemStack();
                requirementSingleStack.stackSize = 1;

                ItemStack emptyRequirementStack = null;
                FluidStack resultFluidStack = null;
                if (FluidContainerRegistry.isFilledContainer(requirementSingleStack)) {
                    resultFluidStack = FluidContainerRegistry.getFluidForFilledItem(requirementSingleStack);
                    emptyRequirementStack = FluidContainerRegistry.drainFluidContainer(requirementSingleStack);
                } else if (requirement.getItem() instanceof IFluidContainerItem) {
                    IFluidContainerItem fluidContainerItem = (IFluidContainerItem) requirement.getItem();
                    emptyRequirementStack = requirementSingleStack.copy();
                    resultFluidStack = fluidContainerItem.drain(emptyRequirementStack, Integer.MAX_VALUE, true);
                }

                if (resultFluidStack == null || emptyRequirementStack == null)
                    resultItemStacks[i] = requirement;
                else {
                    long stackSize = requirement.getStackSize();
                    emptyRequirementStack.stackSize *= stackSize;
                    resultItemStacks[i] = AEApi.instance().storage().createItemStack(emptyRequirementStack);// TODO Fix it
                    resultFluidStack.amount *= stackSize;
                    resultFluidStacks[i] = AEApi.instance().storage().createFluidStack(resultFluidStack);
                }
            }
        }

        if (isCondenced) {
            resultItemStacks = condenceStacks(resultItemStacks, new IAEItemStack[0]);
            this.fluidsCondensed = condenceStacks(resultFluidStacks, new IAEFluidStack[0]);
        } else
            this.fluids = resultFluidStacks;
        // TODO gamerforEA code end

        return resultItemStacks;
    }

    @Override
    public int hashCode() {
        return this.pattern.hashCode();
    }

}
