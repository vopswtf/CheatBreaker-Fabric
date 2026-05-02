package cc.vops.cheatbreaker.client.audio;

import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import java.util.UUID;

@Getter
public class PlayerProxy
{
    private Player player;
    private double x;
    private double y;
    private double z;
    private String entityName;
    public boolean usesEntity;

    public PlayerProxy(final Player player, final UUID uniqueId, final String name, final double x, final double y, final double z) {
        this.player = player;
        this.entityName = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.usesEntity = (player != null);
    }

    public String entityName() {
        return (this.entityName != null) ? this.entityName : this.player.getPlainTextName();
    }

    public Vector3f position() {
        return (this.player != null) ?
                (this.usesEntity ? new Vector3f(
                    (float) this.player.getX(),
                    (float) this.player.getY(),
                    (float)this.player.getZ()
                ) : new Vector3f((float)this.x, (float)this.y, (float)this.z)) : new Vector3f((float)this.x, (float)this.y, (float)this.z);
    }

    public void setName(final String name) {
        this.entityName = name;
    }

    public void setPlayer(final Player entity) {
        this.player = entity;
        this.usesEntity = true;
    }

    public void setPosition(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "PlayerProxy[" + this.entityName + ": " + this.x + ", " + this.y + "," + this.z + "]";
    }

    public void update(final ClientLevel world) {
        if (world != null) {
            this.player = world.getPlayerByUUID(player.getUUID());
            this.usesEntity = (this.player != null);
        }
    }
}
