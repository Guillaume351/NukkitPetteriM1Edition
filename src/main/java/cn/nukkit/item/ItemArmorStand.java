package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelEventPacket;

public class ItemArmorStand extends Item {

    public ItemArmorStand() {
        this(0);
    }

    public ItemArmorStand(Integer meta) {
        this(meta, 1);
    }

    public ItemArmorStand(Integer meta, int count) {
        super(ARMOR_STAND, meta, count, "Armor Stand");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (!Server.getInstance().getPropertyBoolean("spawn-eggs", true)) {
            player.sendMessage("\u00A7cArmor stands are disabled on this server");
            return false;
        }
        FullChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);

        if (chunk == null) {
            return false;
        }

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", block.getX() + 0.5))
                        .add(new DoubleTag("", block.getY()))
                        .add(new DoubleTag("", block.getZ() + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", this.getDirection((float) player.getYaw())))
                        .add(new FloatTag("", 0)));

        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        Entity entity = Entity.createEntity("ArmorStand", chunk, nbt);

        if (entity != null) {
            if (player.isSurvival()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }
            entity.spawnToAll();
            player.getLevel().addLevelSoundEvent(entity, LevelEventPacket.EVENT_SOUND_ARMOR_STAND_PLACE);
            return true;
        }
        return false;
    }

    public float getDirection(float yaw) {
        return (Math.round(yaw / 22.5f / 2) * 45) - 180;
    }
}
