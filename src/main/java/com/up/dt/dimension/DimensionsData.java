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
public class DimensionsData extends SavedData {
    
    private String[] ids;

    public DimensionsData() {
        this(new String[0]);
    }

    public DimensionsData(String[] ids) {
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

    public static DimensionsData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        DimensionsData data = new DimensionsData();
        String merged = tag.getString("ids");
//        dimensionKeys.clear();
//        if (ids != null) Stream.of(ids.split(";")).forEach(s -> dimensionKeys.add(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(s))));
        data.ids = merged != null ? merged.split(";") : new String[0];
        return data;
    }
    
    public static DimensionsData getSave(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(new SavedData.Factory<>(DimensionsData::new, DimensionsData::load), "alternateDimensions");
    }
    
}
