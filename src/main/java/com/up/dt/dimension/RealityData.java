package com.up.dt.dimension;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

/**
 *
 * @author Ricky
 */
public class RealityData extends SavedData {
    
    private String[] ids;

    public RealityData() {
        this(new String[0]);
    }

    public RealityData(String[] ids) {
        this.ids = ids;
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
//        tag.putString("ids", dimensionKeys.stream().map(k -> k.location().toString()).collect(Collectors.joining(";")));
        tag.putString("ids", String.join(";", ids));
        return tag;
    }

    public static RealityData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        RealityData data = new RealityData();
        String merged = tag.getString("ids");
//        dimensionKeys.clear();
//        if (ids != null) Stream.of(ids.split(";")).forEach(s -> dimensionKeys.add(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(s))));
        data.ids = merged != null ? merged.split(";") : new String[0];
        return data;
    }
    
    public static RealityData getSave(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(new SavedData.Factory<>(RealityData::new, RealityData::load), "alternateDimensions");
    }
    
}
