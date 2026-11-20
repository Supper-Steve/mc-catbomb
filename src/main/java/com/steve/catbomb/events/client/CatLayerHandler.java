package com.steve.catbomb.events.client;

import com.steve.catbomb.client.layers.TntCatLayer;
import com.steve.catbomb.constants.ModConstants;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CatLayerHandler {

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        var renderDispatcher = event.getContext().getEntityRenderDispatcher();
        var renderer = renderDispatcher.renderers.get(EntityType.CAT);
        if (renderer instanceof CatRenderer catRenderer) {
            catRenderer.addLayer(new TntCatLayer(catRenderer));
        }
    }
}