package thaumcraft.api.potions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class PotionVisExhaust extends Potion {
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");
    public static PotionVisExhaust instance = null; // will be instantiated at runtime
    private final int statusIconIndex = -1;

    public PotionVisExhaust(int par1, boolean par2, int par3) {
        super(par1, par2, par3);
        setIconIndex(0, 0);
    }

    public static void init() {
        instance.setPotionName("potion.visexhaust");
        instance.setIconIndex(5, 1);
        instance.setEffectiveness(0.25D);
    }

    @Override
    public boolean isBadEffect() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(rl);
        return super.getStatusIconIndex();
    }

    @Override
    public void performEffect(EntityLivingBase target, int par2) {

    }


}
