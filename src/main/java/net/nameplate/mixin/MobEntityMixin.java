package net.nameplate.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.nameplate.NameplateMain;
import net.nameplate.access.MobEntityAccess;

@Mixin(Mob.class)
public class MobEntityMixin implements MobEntityAccess {

    @Unique
    private int mobRpgLevel = 1;
    @Unique
    private boolean showMobRpgLabel = true;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initMixin(EntityType<? extends Mob> entityType, Level world, CallbackInfo info) {
        if (NameplateMain.CONFIG.excludedEntities.contains(((Mob) (Object) this).getType().toString().replace("entity.", "").replace(".", ":"))) {
            this.showMobRpgLabel = false;
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(ValueOutput nbt, CallbackInfo info) {
        nbt.putInt("MobRpgLevel", this.mobRpgLevel);
        nbt.putBoolean("HasMobRpgLabel", this.showMobRpgLabel);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(ValueInput nbt, CallbackInfo info) {
        this.mobRpgLevel = nbt.getIntOr("MobRpgLevel", 1);
        this.showMobRpgLabel = nbt.getBooleanOr("HasMobRpgLabel", true);
    }

    @Override
    public void setMobRpgLevel(int level) {
        this.mobRpgLevel = level;
    }

    @Override
    public int getMobRpgLevel() {
        return this.mobRpgLevel;
    }

    @Override
    public boolean showMobRpgLabel() {
        return this.showMobRpgLabel;
    }

    @Override
    public void setShowMobRpgLabel(boolean setLabel) {
        this.showMobRpgLabel = setLabel;
    }
}
