package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockIce extends BlockTransparent {

    public BlockIce() {
    }

    @Override
    public int getId() {
        return ICE;
    }

    @Override
    public String getName() {
        return "Ice";
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getFrictionFactor() {
        return 0.98;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean onBreak(Item item) {
        if (!this.getLevel().getName().equals("nether")) {
            this.getLevel().setBlock(this, new BlockWater(), true);
        } else {
            super.onBreak(item);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (!this.getLevel().getName().equals("nether")) {
                if (this.getLevel().getBlockLightAt((int) this.x, (int) this.y, (int) this.z) >= 12) {
                    this.getLevel().setBlock(this, new BlockWater(), true);
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            }
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ICE_BLOCK_COLOR;
    }
    
    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
