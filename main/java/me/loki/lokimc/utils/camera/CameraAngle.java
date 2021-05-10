package me.loki.lokimc.utils.camera;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class CameraAngle {
    public float yaw;
    public float pitch;

    public CameraAngle(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public CameraAngle normalizeRotation() {
        float newPitch = this.pitch % 360;
        float newYaw = this.yaw % 360;
        if (newYaw < 0) newYaw += 360;
        if (newPitch < -270) {
            newPitch += 360;
        }
        if (newPitch < -90) {
            newPitch = -90 + -90 - newPitch;
            newYaw = (newYaw + 180) % 360;
        }
        if (newPitch > 90) {
            newPitch = 90 + 90 - newPitch;
            newYaw = (newYaw + 180) % 360;
        }
        if (newPitch > 270) {
            newPitch -= 360;
        }
        this.yaw = newYaw;
        this.pitch = newPitch;
        return this;
    }

    public float distance(CameraAngle x) {
        float dist;
        dist = (float)sqrt(pow(this.distanceYaw(x), 2) + pow(this.distancePitch(x), 2));
        return dist;
    }

    public float distanceYaw(CameraAngle x) {
        float dist;
        if (this.yaw - x.yaw > 180) {
            dist = this.yaw - x.yaw - 360;
        } else if (this.yaw - x.yaw < -180) {
            dist = this.yaw - x.yaw + 360;
        } else {
            dist = this.yaw - x.yaw;
        }
        return dist;
    }

    public float distancePitch(CameraAngle x) {
        float dist;
        dist = this.pitch - x.pitch;
        return dist;
    }
}
