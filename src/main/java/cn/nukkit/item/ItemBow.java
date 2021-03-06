package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.Random;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemBow extends ItemTool {

    public ItemBow() {
        this(0, 1);
    }

    public ItemBow(Integer meta) {
        this(meta, 1);
    }

    public ItemBow(Integer meta, int count) {
        this(BOW, meta, count, "Bow");
    }

    public ItemBow(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_BOW;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

    public boolean onReleaseUsing(Player player) {
        Item itemArrow = Item.get(Item.ARROW, 0, 1);

        if (player.isSurvival() && !player.getInventory().contains(itemArrow)) {
            player.getInventory().sendContents(player);
            return false;
        }

        double damage = 2;
        boolean flame = false;

        if (this.hasEnchantments()) {
            Enchantment bowDamage = this.getEnchantment(Enchantment.ID_BOW_POWER);

            if (bowDamage != null && bowDamage.getLevel() > 0) {
                damage += damage * 0.25 * (bowDamage.getLevel() + 1);
            }

            Enchantment flameEnchant = this.getEnchantment(Enchantment.ID_BOW_FLAME);
            flame = flameEnchant != null && flameEnchant.getLevel() > 0;
        }

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + player.getEyeHeight()))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", -Math.sin(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI)))
                        .add(new DoubleTag("", -Math.sin(player.pitch / 180 * Math.PI)))
                        .add(new DoubleTag("", Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI))))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (player.yaw > 180 ? 360 : 0) - (float) player.yaw))
                        .add(new FloatTag("", (float) -player.pitch)))
                .putShort("Fire", player.isOnFire() || flame ? 45 * 60 : 0)
                .putDouble("damage", damage);

        int diff = (Server.getInstance().getTick() - player.getStartActionTick());
        double p = (double) diff / 20;

        double f = Math.min((p * p + p * 2) / 3, 1) * 2.8;
        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, new EntityArrow(player.chunk, nbt, player, f == 2), f);

        if (f < 0.1 || diff < 5) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().kill();
            player.getInventory().sendContents(player);
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            if (player.isSurvival()) {
                Enchantment infinity;

                if (!this.hasEnchantments() || (infinity = this.getEnchantment(Enchantment.ID_BOW_INFINITY)) == null || infinity.getLevel() <= 0)
                    player.getInventory().removeItem(itemArrow);
                if (!this.isUnbreakable()) {
                    Enchantment durability = this.getEnchantment(Enchantment.ID_DURABILITY);
                    if (!(durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100))) {
                        this.setDamage(this.getDamage() + 2);
                        if (this.getDamage() >= getMaxDurability()) {
                            this.count--;
                        }
                    }
                }
            }
            if (entityShootBowEvent.getProjectile() instanceof EntityProjectile) {
                EntityProjectile proj = entityShootBowEvent.getProjectile();
                ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(proj);
                Server.getInstance().getPluginManager().callEvent(projectev);
                if (projectev.isCancelled()) {
                    proj.kill();
                } else {
                    proj.spawnToAll();
                    player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);

                    if (this.hasEnchantment(Enchantment.ID_BOW_INFINITY)) {
                        proj.namedTag.putBoolean("canNotPickup", true);
                    }
                }
            } else {
                entityShootBowEvent.getProjectile().spawnToAll();
            }
        }

        return true;
    }
}
