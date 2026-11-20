package com.steve.catbomb.events.server;

import com.steve.catbomb.MainClass;
import com.steve.catbomb.constants.ModConstants;
import com.steve.catbomb.constants.NbtConstants;
import com.steve.catbomb.network.TntNetworkPack;
import com.steve.catbomb.server.CatTntManager;
import com.steve.catbomb.util.Coroutine;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public class CatTntItemEvents {

    @SubscribeEvent
    public static void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getSide().isServer()) return;
        if (!(event.getTarget() instanceof Cat cat)) return;
        if (CatTntManager.hasLit(cat)) return;

        var player = event.getEntity();
        var heldItem = event.getItemStack();

        if (!cat.isTame() || player == cat.getOwner()) {
            if (heldItem.is(Items.TNT)) {
                if (!CatTntManager.addTnt(cat, 1)) {
                    return;
                }
                event.setCanceled(true);
                cat.playSound(SoundEvents.GRASS_PLACE, 1F, 1F);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.is(Items.FLINT_AND_STEEL)) {
                if (!CatTntManager.hasTnt(cat) || CatTntManager.hasLit(cat)) return;
                event.setCanceled(true);
                applyExplosion(cat);
                if (!player.getAbilities().instabuild) {
                    heldItem.hurtAndBreak(1, player, (entity) -> {});
                }
            } else if (heldItem.is(Items.FIRE_CHARGE)) {
                if (!CatTntManager.hasTnt(cat) || CatTntManager.hasLit(cat)) return;
                event.setCanceled(true);
                applyExplosion(cat);
                cat.playSound(SoundEvents.FIRECHARGE_USE, 1F, 1F);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            } else if (heldItem.isEmpty() && player.isCrouching()) {
                if (!CatTntManager.hasTnt(cat)) return;

                event.setCanceled(true);
                int count = CatTntManager.getTntCount(cat);
                var pickupTnt = new net.minecraft.world.item.ItemStack(Items.TNT, count);
                if (!player.getInventory().add(pickupTnt)) {
                    player.drop(pickupTnt, false);
                }
                CatTntManager.removeAllTnt(cat);
                cat.playSound(SoundEvents.ITEM_PICKUP, 1F, 1F);
                cat.setTarget(null);
                cat.setOrderedToSit(true);
            }
        }
    }

    private static void applyExplosion(Cat cat) {
        int count = CatTntManager.getTntCount(cat);
        if (count <= 0) return;

        float power = CatTntManager.getExplosionPower(cat);

        cat.getPersistentData().putBoolean(NbtConstants.CAT_LIT_TAG, true);

        MainClass.CHANNEL.send(PacketDistributor.ALL.noArg(), new TntNetworkPack(cat.getUUID(), count, true));
        cat.level().playSound(null, cat.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

        Coroutine.delay(5f, () -> {
            if (!cat.isRemoved()) {
                ServerLevel level = (ServerLevel) cat.level();
                level.explode(null, cat.getX(), cat.getY(), cat.getZ(), power, Level.ExplosionInteraction.TNT);
                cat.getPersistentData().remove(NbtConstants.CAT_TNT_COUNT_TAG);
                cat.getPersistentData().remove(NbtConstants.CAT_LIT_TAG);
                MainClass.CHANNEL.send(PacketDistributor.ALL.noArg(), new TntNetworkPack(cat.getUUID(), 0, false));
            }
        });
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Cat cat)) return;
        if (CatTntManager.hasTnt(cat)) {
            if (CatTntManager.hasLit(cat)) {
                if (cat.level() instanceof ServerLevel level) {
                    float power = CatTntManager.getExplosionPower(cat);
                    level.explode(null, cat.getX(), cat.getY(), cat.getZ(), power, Level.ExplosionInteraction.TNT);
                }
            } else {
                int count = CatTntManager.getTntCount(cat);
                cat.spawnAtLocation(new net.minecraft.world.item.ItemStack(Items.TNT, count));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof Cat cat)) return;

        if (CatTntManager.hasTnt(cat) &&
                !CatTntManager.hasLit(cat) &&
                cat.isOnFire()) {
            applyExplosion(cat);
        }
    }
}