package com.tty.listener.player;

import com.tty.Ari;
import com.tty.lib.Log;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.*;
import com.tty.tool.PlayerDeathInfoCollector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CustomPlayerDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if (Ari.instance.getConfig().getBoolean("server.custom.death", false)) return;
        PlayerDeathInfoCollector.DeathInfo collect = PlayerDeathInfoCollector.collect(event);
        Log.debug(collect.toString());
        Component textComponent = Component.empty();
        String BASE_PREFIX = "server.custom-death.";
        switch (collect.deathCause) {
            case ENTITY_ATTACK, ENTITY_EXPLOSION, ENTITY_SWEEP_ATTACK, PROJECTILE, POISON -> {
                StringBuilder sb = new StringBuilder();
                if (collect.killer instanceof Player) {
                    sb.append(PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player." + (collect.weapon == null || collect.weapon.isEmpty() ? "air":collect.isProjectile ? "projectile" : "item")));
                } else {
                    sb.append(PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "mob." + (collect.weapon == null || collect.weapon.isEmpty() ? "air":collect.isProjectile ? "projectile" : "item")));
                }
                //增加 running away
                if(collect.killer instanceof LivingEntity livingEntity && PublicFunctionUtils.randomGenerator(0, 1) == 0) {
                    AttributeInstance max_health = livingEntity.getAttribute(Attribute.MAX_HEALTH);
                    if(max_health != null && livingEntity.getHealth() < max_health.getValue()) {
                        sb.append(PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "running-away"));
                    }
                }

                textComponent = this.build(collect, sb.toString());
            }
            case CONTACT, FALLING_BLOCK, LAVA, HOT_FLOOR -> {
                Material material = null;
                String key = "";
                if (collect.event instanceof EntityDamageByBlockEvent damageByBlockEvent) {
                    Block block = damageByBlockEvent.getDamager();
                    if (block == null) {
                        Log.error("can not find contact block");
                    } else {
                        key = "block";
                        material = block.getType();
                    }
                } else if(collect.event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
                    if(damageByEntityEvent.getDamager() instanceof FallingBlock fallingBlock) {
                        material = fallingBlock.getBlockData().getMaterial();
                        key = "falling-blocks";
                    } else {
                        Log.error("can not find falling block");
                    }
                }
                if(material == null) {
                    Log.error("custom-death: can not find material data");
                    return;
                }
                textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + key + "." + material.name().toLowerCase()));
            }
            case FALL -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.fall"));
            case FIRE, FIRE_TICK -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.fire"));
            case LIGHTNING -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.lightning"));
            case SUFFOCATION -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.suffocation"));
            case DROWNING -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.drowning"));
            case FREEZE -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.freeze"));
            case SUICIDE -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.suicide"));
            case VOID -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.void"));
            case WITHER -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.wither"));
            case FLY_INTO_WALL -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.fly_into_wall"));
            case KILL -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.kill"));
            case MAGIC -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.magic"));
            case STARVATION -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.starvation"));
            case SONIC_BOOM -> textComponent = this.build(collect, PlayerDeathInfoCollector.getRandomOfList(BASE_PREFIX + "player.sonic_boom"));
        }
        event.deathMessage(textComponent);
    }

    private Component build(PlayerDeathInfoCollector.DeathInfo deathInfo, String string) {
        Component compTemplate = LegacyComponentSerializer.legacyAmpersand().deserialize(string);
        List<String> strings = FormatUtils.extractLangPlaceholders(string);
        Map<LangType, Component> replacementMap = new EnumMap<>(LangType.class);
        strings.forEach(i -> {
            LangType langType = LangType.fromType(i);
            switch (langType) {
                case KILLER -> {
                    Component name = deathInfo.killer.name();
                    replacementMap.put(langType, name);
                }
                case VICTIM -> replacementMap.put(langType, Component.text(deathInfo.victim.getName()));
                case KILLER_ITEM -> replacementMap.put(langType, ComponentUtils.setHoverItem(deathInfo.weapon));
            }
        });
        for (Map.Entry<LangType, Component> entry : replacementMap.entrySet()) {
            compTemplate = compTemplate.replaceText(builder -> builder.matchLiteral(entry.getKey().getType()).replacement(entry.getValue()));
        }
        return compTemplate;
    }

}
