/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.launch.mixins;

import baritone.api.BaritoneAPI;
import baritone.api.event.events.RotationMoveEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.spongepowered.asm.lib.Opcodes.GETFIELD;

/**
 * @author Brady
 * @since 9/10/2018
 */
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {

    /**
     * Event called to override the movement direction when jumping
     */
    private RotationMoveEvent jumpRotationEvent;

    public MixinEntityLivingBase(World worldIn, RotationMoveEvent jumpRotationEvent) {
        super(worldIn);
        this.jumpRotationEvent = jumpRotationEvent;
    }

    @Inject(
            method = "jump",
            at = @At("HEAD")
    )
    private void preMoveRelative(CallbackInfo ci) {
        // noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this)) {
            this.jumpRotationEvent = new RotationMoveEvent((EntityPlayerSP) (Object) this, RotationMoveEvent.Type.JUMP, this.rotationYaw);
            BaritoneAPI.getProvider().getBaritoneForPlayer((EntityPlayerSP) (Object) this).getGameEventHandler().onPlayerRotationMove(this.jumpRotationEvent);
        }
    }

    @Redirect(
            method = "jump",
            at = @At(
                    value = "FIELD",
                    opcode = GETFIELD,
                    target = "net/minecraft/entity/EntityLivingBase.rotationYaw:F"
            )
    )
    private float overrideYaw(EntityLivingBase self) {
        if (self instanceof EntityPlayerSP) {
            return this.jumpRotationEvent.getYaw();
        }
        return self.rotationYaw;
    }
}
