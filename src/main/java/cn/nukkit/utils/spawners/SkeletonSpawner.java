package cn.nukkit.utils.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.mob.EntitySkeleton;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.AbstractEntitySpawner;
import cn.nukkit.utils.SpawnResult;
import cn.nukkit.utils.Spawner;

public class SkeletonSpawner extends AbstractEntitySpawner {

    public SkeletonSpawner(Spawner spawnTask) {
        super(spawnTask);
    }

    @Override
    public SpawnResult spawn(Player player, Position pos, Level level) {
        SpawnResult result = SpawnResult.OK;

        final int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        final int time = level.getTime() % Level.TIME_FULL;

        if (level.getName().equals("nether") || level.getName().equals("end")) {
            result = SpawnResult.WRONG_BIOME;
        } else if (pos.y > 127 || pos.y < 1 || blockId == Block.AIR) {
            result = SpawnResult.POSITION_MISMATCH;
        } else if (Block.transparent[blockId]) {
            result = SpawnResult.WRONG_BLOCK;
        } else if (level.getBlockLightAt((int) pos.x, (int) pos.y, (int) pos.z) > 7) {
            result = SpawnResult.WRONG_LIGHTLEVEL;
        } else if (time > 13184 && time < 22800) {
            this.spawnTask.createEntity("Skeleton", pos.add(0, 1, 0));
        }

        return result;
    }

    @Override
    public final int getEntityNetworkId() {
        return EntitySkeleton.NETWORK_ID;
    }
}
