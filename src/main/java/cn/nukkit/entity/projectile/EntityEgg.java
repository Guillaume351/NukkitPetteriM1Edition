package cn.nukkit.entity.projectile;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.utils.EntityUtils;
import cn.nukkit.entity.passive.EntityChicken;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class EntityEgg extends EntityProjectile {

    public static final int NETWORK_ID = 82;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    public EntityEgg(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityEgg(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.age > 1200) {
            this.kill();
        } else if (this.isCollided) {
            this.kill();

            if (Server.getInstance().getPropertyBoolean("block-listener", true)) {
                if (EntityUtils.rand(1, 20) == 5) {
                    EntityChicken entity = (EntityChicken) EntityUtils.create("Chicken", this.add(0.5, 1, 0.5));
                    if (entity != null) {
                        entity.spawnToAll();
                        entity.setBaby(true);
                    }
                }
            }
        }

        return super.onUpdate(currentTick);
    }
}
