package xreliquary.items.alkahestry;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

public class AlkahestryDrainRecipe implements IRecipe {

    public static Item returnedItem;

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean valid = false;
        boolean foundNonTome = false;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack == null)
                continue;
            if ((stack.getItem() instanceof ItemAlkahestryTome)) {
                if (!valid) valid = NBTHelper.getInteger("redstone", stack) > 0;
            } else {
                foundNonTome = true;
            }
        }
        return !foundNonTome && valid;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack tome = null;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if (stack.getItem() instanceof ItemAlkahestryTome) {
                    tome = stack;
                }
            }
        }

        if (tome != null) {
            int quantity = NBTHelper.getInteger("redstone", tome);
            quantity = Math.min(quantity, new ItemStack(Items.redstone, 1, 0).getMaxStackSize());

            if (quantity == 0)
                return null;
            return new ItemStack(Items.redstone, quantity);
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(returnedItem, 1);
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            ItemStack remainingStack = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);

            if (remainingStack != null && remainingStack.getItem() instanceof ItemAlkahestryTome) {
                NBTHelper.setInteger("redstone", remainingStack, NBTHelper.getInteger("redstone", remainingStack) - getCraftingResult(inv).stackSize);
                remainingStack.setItemDamage(remainingStack.getMaxDamage() - NBTHelper.getInteger("redstone", remainingStack));
            }

            aitemstack[i] = remainingStack;
        }

        return aitemstack;
    }

}
