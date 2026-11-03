package com.example.diseasesurvival;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class DiseaseSurvival implements ModInitializer {
    private static final int INTERVAL_TICKS = 20 * 60 * 10;
    private static final int REFRESH_TICKS = 20 * 5;
    private static final int EFFECT_TICKS = 20 * 15;
    private static final Random RANDOM = new Random();

    private static int tickCounter = 0;
    private static int refreshCounter = 0;
    private static final Map<UUID, Disease> ACTIVE_DISEASES = new HashMap<>();

    private enum Disease {
        FLU("Грипп", "слабость и замедление",
                StatusEffects.WEAKNESS, 0, StatusEffects.SLOWNESS, 0),
        FEVER("Лихорадка", "слабость и тошнота",
                StatusEffects.WEAKNESS, 1, StatusEffects.NAUSEA, 0),
        FOOD_POISONING("Пищевое отравление", "голод и тошнота",
                StatusEffects.HUNGER, 1, StatusEffects.NAUSEA, 0),
        MIGRAINE("Мигрень", "замедление и слабость",
                StatusEffects.SLOWNESS, 1, StatusEffects.WEAKNESS, 0),
        COLD("Простуда", "слабость и тошнота",
                StatusEffects.WEAKNESS, 0, StatusEffects.NAUSEA, 0),
        EXHAUSTION("Истощение", "сильный голод и замедление",
                StatusEffects.HUNGER, 1, StatusEffects.SLOWNESS, 0);

        final String displayName;
        final String description;
        final net.minecraft.entity.effect.StatusEffect first;
        final int firstAmplifier;
        final net.minecraft.entity.effect.StatusEffect second;
        final int secondAmplifier;

        Disease(String displayName, String description,
                net.minecraft.entity.effect.StatusEffect first, int firstAmplifier,
                net.minecraft.entity.effect.StatusEffect second, int secondAmplifier) {
            this.displayName = displayName;
            this.description = description;
            this.first = first;
            this.firstAmplifier = firstAmplifier;
            this.second = second;
            this.secondAmplifier = secondAmplifier;
        }

        void apply(ServerPlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(first, EFFECT_TICKS, firstAmplifier));
            player.addStatusEffect(new StatusEffectInstance(second, EFFECT_TICKS, secondAmplifier));
        }
    }

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer
                    && player.getStackInHand(hand).isOf(Items.MILK_BUCKET)
                    && ACTIVE_DISEASES.remove(player.getUuid()) != null) {
                serverPlayer.sendMessage(Text.literal("§aВы вылечились!"), true);
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }

    private void onServerTick(MinecraftServer server) {
        tickCounter++;
        refreshCounter++;

        if (refreshCounter >= REFRESH_TICKS) {
            refreshCounter = 0;
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Disease disease = ACTIVE_DISEASES.get(player.getUuid());
                if (disease != null) {
                    disease.apply(player);
                }
            }
        }

        if (tickCounter >= INTERVAL_TICKS) {
            tickCounter = 0;
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Disease disease = Disease.values()[RANDOM.nextInt(Disease.values().length)];
                ACTIVE_DISEASES.put(player.getUuid(), disease);
                disease.apply(player);
                player.sendMessage(Text.literal("§c§lЗаболевание: §f" + disease.displayName
                        + " §7— " + disease.description), false);
                player.sendMessage(Text.literal("§eЛечение: выпейте ведро молока."), false);
            }
        }
    }
}
