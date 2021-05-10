package me.loki.lokimc.utils.other;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;

public class ProximitySensor {
    double range;
    int ticksToGoOff;
    int ticksSinceTriggered;
    Minecraft mc = Minecraft.getMinecraft();

    public ProximitySensor(double range) {
        this(range, 0);
    }

    public ProximitySensor(double range, int ticksToGoOff) {
        this.range = range;
        this.ticksToGoOff = ticksToGoOff;
        ticksSinceTriggered = 0;
    }

    private boolean checkProximity() {
         for (Entity entity: mc.theWorld.getLoadedEntityList()) {
             if (entity instanceof EntityOtherPlayerMP) {
                 EntityOtherPlayerMP testedEntity = (EntityOtherPlayerMP)entity;
                 if (mc.thePlayer.getDistanceToEntity(testedEntity) < range) {
                     System.out.println("Nearby player: " + testedEntity);
                     return true;
                 }
             }
         }
         return false;
    }

    public boolean onTick() {
        if (checkProximity()) {
            ticksSinceTriggered++;
        } else {
            ticksSinceTriggered = 0;
        }
        return ticksSinceTriggered >= ticksToGoOff;
    }
}
