package cn.nukkit.utils.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.AbstractEntitySpawner;
import cn.nukkit.utils.EntityUtils;
import cn.nukkit.utils.SpawnResult;
import cn.nukkit.utils.Spawner;

public class HorseSpawner extends AbstractEntitySpawner {

    public HorseSpawner(Spawner spawnTask) {
        super(spawnTask);
    }

    @Override
    public SpawnResult spawn(Player player, Position pos, Level level) {
        SpawnResult result = SpawnResult.OK;

        final int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        final int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);

        if (blockId != Block.GRASS) {
            result = SpawnResult.WRONG_BLOCK;
        } else if (biomeId != 1 && biomeId != 35 && biomeId != 128 && biomeId != 129) {
            result = SpawnResult.WRONG_BIOME;
        } else if (level.getName().equals("nether") || level.getName().equals("end")) {
            result = SpawnResult.WRONG_BIOME;
        } else if (pos.y > 127 || pos.y < 1 || blockId == Block.AIR) {
            result = SpawnResult.POSITION_MISMATCH;
        } else {
            BaseEntity entity = this.spawnTask.createEntity("Horse", pos.add(0, 1, 0));
            if (EntityUtils.rand(0, 500) > 480) {
                entity.setBaby(true);
            }
        }

        return result;
    }

    @Override
    public final int getEntityNetworkId() {
        return EntityHorse.NETWORK_ID;
    }
}
