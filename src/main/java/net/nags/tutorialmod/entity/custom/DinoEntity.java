package net.nags.tutorialmod.entity.custom;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.nags.tutorialmod.entity.ModEntities;
import net.nags.tutorialmod.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class DinoEntity extends Animal{
    private int roarCooldown = 200; //10 seconds if 20 ticks = 1 sec
    private boolean isEnraged = false; // Track if the Dino is enraged (hit)
    private LivingEntity revengeTarget; // Store the attacker (player who hit the Dino)

    @Override
    public boolean hurt(DamageSource source, float amount){
        // Handle Roar Stun every 10 seconds
        if (!this.level().isClientSide) {
            roarCooldown--;
            if (roarCooldown <= 0) {
                roarCooldown = 200; // reset cooldown

                this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, this.getSoundSource(), 5.0F, 1.0F);

                this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10)).forEach(player -> {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 100, 2)); // 5 seconds slow
                });
            }
        }

        // Store the attacker as revengeTarget
        if (source.getEntity() instanceof LivingEntity attacker) {
            this.revengeTarget = attacker;
            this.isEnraged = true; // Dino becomes enraged after being hit
        }

        boolean result = super.hurt(source, amount);
        if(source.getEntity() instanceof LivingEntity attacker){
            this.revengeTarget = attacker;
        }
        return result;
    }

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public DinoEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        //add code for attacking
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5, true));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 300)
                .add(Attributes.MOVEMENT_SPEED, 0.350)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.ATTACK_DAMAGE, 8.0);
    }


    @Override
    public boolean isFood(ItemStack pStack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return ModEntities.DINO.get().create(pLevel);
    }

    private void setupAnimationStates() {
        if(this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    //make sure that dino kick's the hell out of the user
    public void tick(){
        //knockback
        if (!this.level().isClientSide && this.getTarget() != null && this.distanceTo(this.getTarget()) < 4.0F) {
            this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(4)).forEach(player -> {
                double knockbackStrength = 1.5D;
                double dx = player.getX() - this.getX();
                double dz = player.getZ() - this.getZ();
                player.push(dx * knockbackStrength, 0.5D, dz * knockbackStrength);
            });
        }

        // If the Dino is enraged, increase speed
        if (this.isEnraged) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.600);  // Increase speed when enraged
        } else {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.350);  // Default speed
        }

        // If the revenge target (attacker) is alive, keep showing particles
        if (revengeTarget != null && revengeTarget.isAlive()) {
            if (this.level().isClientSide) {
                double particleX = this.getX();
                double particleY = this.getY() + this.getBbHeight() / 2.0;
                double particleZ = this.getZ();

                // Spawn flame and smoke particles around Dino's body (red, to simulate rage)
                this.level().addParticle(ParticleTypes.SWEEP_ATTACK, particleX, particleY, particleZ, 0.0, 0.0, 0.0);
                this.level().addParticle(ParticleTypes.DRAGON_BREATH, particleX, particleY, particleZ, 0.0, 0.0, 0.0);
                this.level().addParticle(ParticleTypes.DRAGON_BREATH, particleX, particleY, particleZ, 0.0, 0.0, 0.0);
            }
        } else {
            // If revenge target is dead, stop particles and reset enraged state
            this.isEnraged = false;
        }

        super.tick();

        // Set the Dino's target to the revenge target (if alive)
        if (revengeTarget != null && revengeTarget.isAlive()) {
            this.setTarget(revengeTarget);
        } else {
            revengeTarget = null;
        }
    }


    //sounds not moans :)
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ELDER_GUARDIAN_CURSE;
    }


    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }
}