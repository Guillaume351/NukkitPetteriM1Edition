package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class InventorySlotPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.INVENTORY_SLOT_PACKET;
    }

    public int inventoryId;
    public int slot;
    public Item item;

    @Override
    public void decode() {
        this.inventoryId = (int) this.getUnsignedVarInt();
        this.slot = (int) this.getUnsignedVarInt();
        this.item = this.getSlot();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt((byte) this.inventoryId);
        this.putUnsignedVarInt(this.slot);
        this.putSlot(this.item);
    }
}
