package com.sonicether.soundphysics.integration.voicechat;

import com.sonicether.soundphysics.SoundPhysics;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class AudioChannel {

    private static final String CATEGORY_TEMPLATE = "voicechat:%s";
    public static final String CATEGORY_VOICECHAT = "voicechat";

    private final UUID channelId;
    private long lastUpdate;
    private Vec3 lastPos;

    public AudioChannel(UUID channelId) {
        this.channelId = channelId;
    }

    public void onSound(int source, @Nullable Vec3 soundPos, boolean auxOnly, @Nullable String category) {
        if (soundPos == null) {
            SoundPhysics.setDefaultEnvironment(source, auxOnly);
            return;
        }

        long time = System.currentTimeMillis();

        if (time - lastUpdate < 500 && (lastPos != null && lastPos.distanceTo(soundPos) < 1D)) {
            return;
        }

        SoundPhysics.setLastSoundCategoryAndName(SoundSource.MASTER, CATEGORY_TEMPLATE.formatted(category == null ? CATEGORY_VOICECHAT : category));

        if (auxOnly) {
            SoundPhysics.onPlayReverb(soundPos.x(), soundPos.y(), soundPos.z(), source);
        } else {
            SoundPhysics.onPlaySound(soundPos.x(), soundPos.y(), soundPos.z(), source);
        }

        lastUpdate = time;
        lastPos = soundPos;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public boolean canBeRemoved() {
        return System.currentTimeMillis() - lastUpdate > 5_000L;
    }
}
