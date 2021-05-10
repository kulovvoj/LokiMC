package me.loki.lokimc.utils.camera;

import net.minecraft.client.Minecraft;

import static java.lang.Float.max;
import static java.lang.Float.min;
import static java.lang.Math.pow;

public class CameraRotation {
    CameraAngle start, end;
    public Minecraft mc = Minecraft.getMinecraft();

    public CameraRotation(CameraAngle start, CameraAngle end) {
        this.start = start;
        this.end = end;
        if (start.yaw - end.yaw < -180) {
            end.yaw -= 360;
        } else if (start.yaw - end.yaw > 180) {
            end.yaw += 360;
        }
    }

    public void onTick() {
        float cameraSpeed;
        CameraAngle currentCameraAngle = new CameraAngle(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        float startMinSpeedModifier = 0.7F;
        float endMinSpeedModifier = 0.7F;
        float startDistance = min(2, start.distance(end) / 2);
        float endDistance = min(5, start.distance(end) / 2);
        cameraSpeed = min(2F / end.distance(currentCameraAngle), 1);
        cameraSpeed *= min(max(start.distance(end) / 10, 1), 3);
        if (start.distance(currentCameraAngle) <= startDistance) {
            cameraSpeed *= ((1 - ((float)pow((startDistance - start.distance(currentCameraAngle)) / startDistance, 2))) + startMinSpeedModifier) / (1 + startMinSpeedModifier);
        } else if (currentCameraAngle.distance(end) <= endDistance) {
            cameraSpeed *= ((1 - ((float)pow((endDistance - currentCameraAngle.distance(end)) / endDistance, 2))) + endMinSpeedModifier) / (1 + endMinSpeedModifier);
        }
        cameraSpeed = min(cameraSpeed, 1);
        mc.thePlayer.rotationYaw += end.distanceYaw(currentCameraAngle) * cameraSpeed;
        mc.thePlayer.rotationPitch += end.distancePitch(currentCameraAngle) * cameraSpeed;
    }
}
