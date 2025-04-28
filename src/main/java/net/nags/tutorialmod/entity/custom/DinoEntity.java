package net.nags.tutorialmod.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.nags.tutorialmod.entity.ModEntities;
import org.jetbrains.annotations.Nullable;


public class DinoEntity extends Animal{
    private int roarCooldown = 200; //10 seconds if 20 ticks = 1 sec
    private boolean isEnraged = false; // Track if the Dino is enraged (hit)
    private LivingEntity revengeTarget; // Store the attacker (player who hit the Dino)
    private boolean isTamed = false;
    private Player tamer;

    public void tame(Player player) {
        if (!this.isTamed) {
            this.isTamed = true;
            this.tamer = player;

            // Optionally play a sound or particle effect
            this.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), 1.0F, 1.0F);
            // Make Dino friendly to the player
            this.setTarget(null); // Stop targeting players when tamed
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
                this.setRot(player.getYRot(), player.getXRot());
                this.yRotO = this.getYRot();
                this.xRotO = this.getXRot();
                this.setYHeadRot(this.getYRot());
                this.yBodyRot = this.getYRot();

                float forward = player.zza; // W/S input
                float strafe = player.xxa;  // A/D input

                if (this.onGround()) {
                    this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.5F);

                    Vec3 movement = new Vec3(strafe, travelVector.y, forward);
                    this.moveRelative(0.1F, movement); // Magic method that applies strafing and forward motion
                    this.move(MoverType.SELF, this.getDeltaMovement()); // Actually move the Dino
                } else {
                    super.travel(new Vec3(0, travelVector.y, 0)); // In air: fall naturally
                }
            } else {
                super.travel(travelVector);
            }
        }
    }


    @Override
    public boolean isControlledByLocalInstance() {
        // Only allow control if tamed and has a player rider
        return this.isTamed && this.getControllingPassenger() instanceof Player;
    }

    @Override
    public boolean isVehicle() {
        // More reliable vehicle check that works with your taming system
        return super.isVehicle() && this.isTamed;
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : (this.getPassengers().get(0) instanceof LivingEntity living ? living : null);
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            if (!this.isTamed) {
                tame(player);
                return InteractionResult.SUCCESS;
            } else if (this.tamer != null && this.tamer == player) {
                if (this.isVehicle()) {
                    if (player.isShiftKeyDown()) {
                        this.ejectPassengers(); // Dismount
                        return InteractionResult.SUCCESS;
                    }
                } else if (this.canAddPassenger(player)) {
                    player.startRiding(this); // Mount
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }



    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && roarCooldown <= 0) {
            roarCooldown = 200;
            this.level().playSound(null, this.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, this.getSoundSource(), 1.0F, 1.0F);
            this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10)).forEach(player -> {
                if (this.tamer == null || player != this.tamer) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                }
            });
        }

        if (source.getEntity() instanceof LivingEntity attacker && !this.isTamed) {
            this.revengeTarget = attacker;
            this.isEnraged = true;
        }

        return super.hurt(source, amount);
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
    public void tick() {
        super.tick();
        setupAnimationStates(); // Enable idle animation

        // Manage roar cooldown
        if (!this.level().isClientSide) {
            if (roarCooldown > 0) {
                roarCooldown--;
            }
        }

        // Knockback (exclude tamer and rider)
        if (!this.level().isClientSide && this.getTarget() != null && this.distanceTo(this.getTarget()) < 4.0F) {
            this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(4)).forEach(player -> {
                if (this.tamer == null || (player != this.tamer && !this.hasPassenger(player))) {
                    double knockbackStrength = 1.5;
                    Vec3 delta = player.position().subtract(this.position()).normalize();
                    player.push(delta.x * knockbackStrength, 0.5, delta.z * knockbackStrength);
                }
            });
        }

        // Enraged state and speed
        if (this.isEnraged) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.600);
        } else {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.350);
        }

        // Particle effects for enraged state
        if (this.isEnraged && revengeTarget != null && revengeTarget.isAlive()) {
            if (this.level().isClientSide && this.random.nextInt(10) == 0) {
                double particleX = this.getX() + (this.random.nextDouble() - 0.5) * this.getBbWidth();
                double particleY = this.getY() + this.getBbHeight() * 0.5 + (this.random.nextDouble() - 0.5) * 0.5;
                double particleZ = this.getZ() + (this.random.nextDouble() - 0.5) * this.getBbWidth();
                this.level().addParticle(ParticleTypes.FLAME, particleX, particleY, particleZ, 0.0, 0.05, 0.0);
            }
        } else {
            this.isEnraged = false;
            this.revengeTarget = null;
        }

        // Targeting logic
        if (!this.isTamed && revengeTarget != null && revengeTarget.isAlive()) {
            this.setTarget(revengeTarget);
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