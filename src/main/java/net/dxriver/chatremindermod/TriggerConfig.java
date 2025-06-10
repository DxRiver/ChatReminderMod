package net.dxriver.chatremindermod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TriggerConfig {
    private String hexColor;
    private SoundEvent sound;
    private int noteCount;
    private int delayTicks;
    private float volume;
    private float pitchMin;
    private float pitchMax;

    public TriggerConfig() {
        this.hexColor = "#a4e6fd"; 
        this.sound =SoundPlayer.getSoundEventByName(ResourceLocation.tryParse(ModSounds.WX1)) ;
        this.noteCount = 1;
        this.delayTicks = 0;
        this.volume = 1.0f;
        this.pitchMin = 1f;
        this.pitchMax = 1f;
    }

    
    public TriggerConfig(TriggerConfig other) {
        this.hexColor = other.hexColor;
        this.sound = other.sound;
        this.noteCount = other.noteCount;
        this.delayTicks = other.delayTicks;
        this.volume = other.volume;
        this.pitchMin = other.pitchMin;
        this.pitchMax = other.pitchMax;
    }

    
    public static Builder builder() {
        return new Builder();
    }

    

    public void setColor(String hex) {
        this.hexColor = validateHex(hex);
    }

    public void setSound(SoundEvent sound) {
        this.sound = sound;
    }

    public void setNoteCount(int count) {
        this.noteCount = Mth.clamp(count, 1, 20);
    }

    public void setDelayTicks(int ticks) {
        this.delayTicks = Math.max(ticks, 0);
    }

    public void setVolume(float vol) {
        this.volume = Mth.clamp(vol, 0.0f, 1.0f);
    }

    public void setPitchRange(float min, float max) {
        this.pitchMin = Mth.clamp(Math.min(min, max), 0.1f, 3.0f);
        this.pitchMax = Mth.clamp(Math.max(min, max), 0.1f, 3.0f);
    }

    

    private String validateHex(String hex) {
        if (hex == null || !hex.matches("^#?[0-9A-Fa-f]{6}$")) {
            return "#FF0000"; 
        }
        return hex.startsWith("#") ? hex : "#" + hex;
    }

    

    public String getHexColor() {
        return hexColor;
    }

    public SoundEvent getSound() {
        return sound;
    }

    public int getNoteCount() {
        return noteCount;
    }

    public int getDelayTicks() {
        return delayTicks;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitchMin() {
        return pitchMin;
    }

    public float getPitchMax() {
        return pitchMax;
    }

    
    public static class Builder {
        private final TriggerConfig config;

        public Builder() {
            this.config = new TriggerConfig();
        }

        
        public Builder(TriggerConfig existing) {
            this.config = new TriggerConfig(existing);
        }

        
        public Builder color(String hex) {
            config.setColor(hex);
            return this;
        }

        
        public Builder sound(SoundEvent sound) {
            config.setSound(sound);
            return this;
        }

        
        public Builder notes(int count) {
            config.setNoteCount(count);
            return this;
        }

        
        public Builder delay(int ticks) {
            config.setDelayTicks(ticks);
            return this;
        }

        
        public Builder volume(float vol) {
            config.setVolume(vol);
            return this;
        }

        
        public Builder pitchRange(float min, float max) {
            config.setPitchRange(min, max);
            return this;
        }

        
        public TriggerConfig build() {
            
            if (config.getPitchMin() > config.getPitchMax()) {
                throw new IllegalArgumentException("最小音调不能大于最大音调");
            }
            return config;
        }
    }
}