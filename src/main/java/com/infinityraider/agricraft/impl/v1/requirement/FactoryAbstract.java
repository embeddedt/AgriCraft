package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.Tag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.extensions.IForgeStructure;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Intermediate implementation forwarding all default methods to the predicate ones
 */
public abstract class FactoryAbstract implements IDefaultGrowConditionFactory {
    public static final BlockPos OFFSET_NONE = new BlockPos(0, 0, 0);
    public static final BlockPos OFFSET_SOIL = new BlockPos(0, -1, 0);
    public static final BlockPos OFFSET_BELOW = new BlockPos(0, -2, 0);

    protected FactoryAbstract() {}

    protected abstract IAgriGrowCondition statesInRange(int strength, RequirementType type, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate);

    @Override
    public IAgriGrowCondition soil(int strength, IAgriSoil soil) {
        return this.statesInRange(strength, RequirementType.SOIL, 1, 1, OFFSET_SOIL, OFFSET_SOIL, soil::isVariant);
    }

    @Override
    public IAgriGrowCondition soil(int strength, IAgriSoil... soils) {
        return this.soil(strength, Arrays.asList(soils));
    }

    @Override
    public IAgriGrowCondition soil(int strength, Collection<IAgriSoil> soils) {
        return this.statesInRange(strength, RequirementType.SOIL, 1, 1, OFFSET_SOIL, OFFSET_SOIL,
                (state) -> soils.stream().anyMatch(soil -> soil.isVariant(state))
        );
    }

    @Override
    public IAgriGrowCondition light(int strength, int min, int max) {
        return this.light(strength, (light) -> light >= min && light <= max);
    }

    @Override
    public IAgriGrowCondition light(int strength, int value) {
        return this.light(strength, (light) -> light == value);
    }

    @Override
    public IAgriGrowCondition redstone(int strength, int min, int max) {
        return this.redstone(strength, (redstone) -> redstone >= min && redstone <= max);
    }

    @Override
    public IAgriGrowCondition redstone(int strength, int value) {
        return this.redstone(strength, (redstone) -> redstone == value);
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(int strength, Fluid fluid) {
        return this.liquidFromFluid(strength, fluid::equals);
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(int strength, Fluid... fluids) {
        return this.liquidFromFluid(strength, Arrays.asList(fluids));
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(int strength, Collection<Fluid> fluids) {
        return this.liquidFromFluid(strength, fluids::contains);
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(int strength, Predicate<Fluid> predicate) {
        return this.liquidFromState(strength, (state) -> predicate.test(state.getFluid()));
    }

    @Override
    public IAgriGrowCondition liquidFromState(int strength, FluidState state) {
        return this.liquidFromState(strength, state::equals);
    }

    @Override
    public IAgriGrowCondition liquidFromState(int strength, FluidState... states) {
        return this.liquidFromState(strength, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition liquidFromState(int strength, Collection<FluidState> states) {
        return this.liquidFromState(strength, states::contains);
    }

    @Override
    public IAgriGrowCondition liquidFromState(int strength, Predicate<FluidState> predicate) {
        return this.statesInRange(strength, RequirementType.LIQUID, 1, 1, OFFSET_NONE, OFFSET_NONE, (state) -> predicate.test(state.getFluidState()));
    }

    @Override
    public IAgriGrowCondition liquidFromTag(int strength, Tag<Fluid> fluid) {
        return this.liquidFromFluid(strength, fluid::contains);
    }

    @Override
    public IAgriGrowCondition liquidFromTag(int strength, Tag<Fluid>... fluids) {
        return this.liquidFromTag(strength, Arrays.asList(fluids));
    }

    @Override
    public IAgriGrowCondition liquidFromTag(int strength, Collection<Tag<Fluid>> fluids) {
        return this.liquidFromFluid(strength, (liquid) -> fluids.stream().anyMatch(fluid -> fluid.contains(liquid)));
    }

    @Override
    public IAgriGrowCondition liquidFromClass(int strength, Class<Fluid> fluid) {
        return this.liquidFromFluid(strength, fluid::isInstance);
    }

    @Override
    public IAgriGrowCondition liquidFromClass(int strength, Class<Fluid>... fluids) {
        return this.liquidFromClass(strength, Arrays.asList(fluids));
    }

    @Override
    public IAgriGrowCondition liquidFromClass(int strength, Collection<Class<Fluid>> fluids) {
        return this.liquidFromFluid(strength,liquid ->fluids.stream().anyMatch(fluid -> fluid.isInstance(liquid)));
    }

    @Override
    public IAgriGrowCondition liquidFromClass(int strength, Predicate<Class<? extends Fluid>> predicate) {
        return this.liquidFromFluid(strength, liquid -> predicate.test(liquid.getClass()));
    }

    @Override
    public IAgriGrowCondition biome(int strength, Biome biome) {
        return this.biome(strength, biome::equals);
    }

    @Override
    public IAgriGrowCondition biome(int strength, Biome... biomes) {
        return this.biome(strength, Arrays.asList(biomes));
    }

    @Override
    public IAgriGrowCondition biome(int strength, Collection<Biome> biomes) {
        return this.biome(strength, biomes::contains);
    }

    @Override
    public IAgriGrowCondition biomeFromCategory(int strength, Biome.Category category) {
        return this.biomeFromCategory(strength, category::equals);
    }

    @Override
    public IAgriGrowCondition biomeFromCategories(int strength, Biome.Category... categories) {
        return this.biomeFromCategories(strength, Arrays.asList(categories));
    }

    @Override
    public IAgriGrowCondition biomeFromCategories(int strength, Collection<Biome.Category> categories) {
        return this.biomeFromCategory(strength, categories::contains);
    }

    @Override
    public IAgriGrowCondition biomeFromCategory(int strength, Predicate<Biome.Category> predicate) {
        return this.biome(strength, (biome) -> predicate.test(biome.getCategory()));
    }

    @Override
    public IAgriGrowCondition climate(int strength, Biome.Climate climate) {
        return this.climate(strength, climate::equals);
    }

    @Override
    public IAgriGrowCondition climate(int strength, Biome.Climate... climates) {
        return this.climate(strength, Arrays.asList(climates));
    }

    @Override
    public IAgriGrowCondition climate(int strength, Collection<Biome.Climate> climates) {
        return this.climate(strength, climates::contains);
    }

    @Override
    public IAgriGrowCondition climate(int strength, Predicate<Biome.Climate> predicate) {
        return this.biome(strength, (biome) -> predicate.test(this.getClimate(biome)));
    }

    private final Biome.Climate getClimate(Biome biome) {
        return ObfuscationReflectionHelper.getPrivateValue(Biome.class, biome, "field_242423_j");
    }

    @Override
    public IAgriGrowCondition dimensionFromKey(int strength, RegistryKey<World> dimension) {
        return this.dimensionFromKey(strength, dimension::equals);
    }

    @Override
    public IAgriGrowCondition dimensionFromKeys(int strength, RegistryKey<World>... dimensions) {
        return this.dimensionFromKeys(strength, Arrays.asList(dimensions));
    }

    @Override
    public IAgriGrowCondition dimensionFromKeys(int strength, Collection<RegistryKey<World>> dimensions) {
        return this.dimensionFromKey(strength, dimensions::contains);
    }

    @Override
    public IAgriGrowCondition dimensionFromType(int strength, DimensionType dimension) {
        return this.dimensionFromType(strength, dimension::equals);
    }

    @Override
    public IAgriGrowCondition dimensionFromTypes(int strength, DimensionType... dimensions) {
        return this.dimensionFromTypes(strength, Arrays.asList(dimensions));
    }

    @Override
    public IAgriGrowCondition dimensionFromTypes(int strength, Collection<DimensionType> dimensions) {
        return this.dimensionFromType(strength, dimensions::contains);
    }

    @Override
    public IAgriGrowCondition withWeed(int strength) {
        return this.weed(strength, IAgriWeed::isWeed);
    }

    @Override
    public IAgriGrowCondition withoutWeed(int strength) {
        return this.weed(strength, weed -> !weed.isWeed());
    }

    @Override
    public IAgriGrowCondition withWeed(int strength, IAgriWeed weed) {
        return this.weed(strength, weed::equals);
    }

    @Override
    public IAgriGrowCondition withoutWeed(int strength, IAgriWeed weed) {
        return this.weed(strength, aWeed -> !weed.equals(aWeed));
    }

    @Override
    public IAgriGrowCondition weed(int strength, Predicate<IAgriWeed> predicate) {
        return this.weed(strength, (weed, growth) -> predicate.test(weed));
    }

    @Override
    public IAgriGrowCondition withWeed(int strength, IAgriWeed weed, IAgriGrowthStage stage) {
        return this.weed(strength, (aWeed, growth) -> weed.equals(aWeed) && stage.equals(growth));
    }

    @Override
    public IAgriGrowCondition withoutWeed(int strength, IAgriWeed weed, IAgriGrowthStage stage) {
        return this.weed(strength, (aWeed, growth) -> !(weed.equals(aWeed) && stage.equals(growth)));
    }

    @Override
    public IAgriGrowCondition timeAllowed(int strength, long min, long max) {
        return this.time(strength, (time) -> time >= min && time <= max);
    }

    @Override
    public IAgriGrowCondition timeForbidden(int strength, long min, long max) {
        return this.time(strength, (time) -> time < min || time > max);
    }

    @Override
    public IAgriGrowCondition blockBelow(int strength, Block block) {
        return this.blockBelow(strength, block::equals);
    }

    @Override
    public IAgriGrowCondition blockBelow(int strength, Block... blocks) {
        return this.blockBelow(strength, Arrays.asList(blocks));
    }

    @Override
    public IAgriGrowCondition blockBelow(int strength, Collection<Block> blocks) {
        return this.blockBelow(strength, blocks::contains);
    }

    @Override
    public IAgriGrowCondition blockBelow(int strength, Predicate<Block> predicate) {
        return this.stateBelow(strength, state -> predicate.test(state.getBlock()));
    }

    @Override
    public IAgriGrowCondition stateBelow(int strength, BlockState state) {
        return this.stateBelow(strength, state::equals);
    }

    @Override
    public IAgriGrowCondition stateBelow(int strength, BlockState... states) {
        return this.stateBelow(strength, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition stateBelow(int strength, Collection<BlockState> states) {
        return this.stateBelow(strength, states::contains);
    }

    @Override
    public IAgriGrowCondition stateBelow(int strength, Predicate<BlockState> predicate) {
        return this.statesInRange(strength, RequirementType.BLOCK_BELOW, 1, 1, OFFSET_BELOW, OFFSET_BELOW, predicate);
    }

    @Override
    public IAgriGrowCondition tagBelow(int strength, Tag<Block> tag) {
        return this.blockBelow(strength, tag::contains);
    }

    @Override
    public IAgriGrowCondition tagBelow(int strength, Tag<Block>... tags) {
        return this.tagBelow(strength, Arrays.asList(tags));
    }

    @Override
    public IAgriGrowCondition tagBelow(int strength, Collection<Tag<Block>> tags) {
        return this.blockBelow(strength, (block) -> tags.stream().anyMatch(tag -> tag.contains(block)));
    }

    @Override
    public IAgriGrowCondition classBelow(int strength, Class<Block> clazz) {
        return this.blockBelow(strength, clazz::isInstance);
    }

    @Override
    public IAgriGrowCondition classBelow(int strength, Class<Block>... classes) {
        return this.classBelow(strength, Arrays.asList(classes));
    }

    @Override
    public IAgriGrowCondition classBelow(int strength, Collection<Class<Block>> classes) {
        return this.blockBelow(strength, (block) -> classes.stream().anyMatch(clazz -> clazz.isInstance(block)));
    }

    @Override
    public IAgriGrowCondition classBelow(int strength, Predicate<Class<? extends Block>> predicate) {
        return this.blockBelow(strength, (block) -> predicate.test(block.getClass()));
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block block) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, block::equals);
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block... blocks) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, Arrays.asList(blocks));
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, blocks::contains);
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<Block> predicate) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, (state) -> predicate.test(state.getBlock()));
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState state) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, state::equals);
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState... states) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, states::contains);
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate) {
        return this.statesNearby(strength, amount, amount, minOffset, maxOffset, predicate);
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, tag::contains);
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags) {
        return this.tagsNearby(strength, amount, minOffset, maxOffset, Arrays.asList(tags));
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, (block) -> tags.stream().anyMatch(tag -> tag.contains(block)));
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Class<Block> clazz) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, clazz::isInstance);
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Class<Block>... classes) {
        return this.classNearby(strength, amount, minOffset, maxOffset, Arrays.asList(classes));
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Class<Block>> classes) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, (block) -> classes.stream().anyMatch(clazz -> clazz.isInstance(block)));
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<Class<? extends Block>> predicate) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, (block) -> predicate.test(block.getClass()));
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block block) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, block::equals);
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block... blocks) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(blocks));
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, blocks::contains);
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<Block> predicate) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, (state) -> predicate.test(state.getBlock()));
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState state) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, state::equals);
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState... states) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, states::contains);
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate) {
        return this.statesInRange(strength, RequirementType.BLOCKS_NEARBY, min, max, minOffset, maxOffset, predicate);
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, tag::contains);
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags) {
        return this.tagsNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(tags));
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, (block) -> tags.stream().anyMatch(tag -> tag.contains(block)));
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Class<Block> clazz) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, clazz::isInstance);
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Class<Block>... classes) {
        return this.classNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(classes));
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Class<Block>> classes) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, (block) -> classes.stream().anyMatch(clazz -> clazz.isInstance(block)));
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<Class<? extends Block>> predicate) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, (block) -> predicate.test(block.getClass()));
    }

    @Override
    public IAgriGrowCondition entityNearby(int strength, EntityType<?> type, double range) {
        return this.entityNearby(strength, (entity) -> entity.getType().equals(type), range);
    }

    @Override
    public IAgriGrowCondition entityNearby(int strength, Class<Entity> clazz, double range) {
        return this.entityNearby(strength, clazz::isInstance, range);
    }

    @Override
    public IAgriGrowCondition entityNearby(int strength, Predicate<Entity> predicate, double range) {
        return this.entitiesNearby(strength, predicate, range, 1);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, EntityType<?> type, double range, int amount) {
        return this.entitiesNearby(strength, (entity) -> entity.getType().equals(type), range, amount);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, Class<Entity> clazz, double range, int amount) {
        return this.entitiesNearby(strength, clazz::isInstance, range, amount);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, Predicate<Entity> predicate, double range, int amount) {
        return this.entitiesNearby(strength, predicate, range, amount, amount);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, EntityType<?> type, double range, int min, int max) {
        return this.entitiesNearby(strength, (entity) -> entity.getType().equals(type), range, min, max);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, Class<Entity> clazz, double range, int min, int max) {
        return this.entitiesNearby(strength, clazz::isInstance, range, min, max);
    }



    @Override
    public IAgriGrowCondition inSeason(int strength, AgriSeason season) {
        return this.season(strength, season::matches);
    }

    @Override
    public IAgriGrowCondition inSeasons(int strength, AgriSeason... seasons) {
        return this.inSeasons(strength, Arrays.asList(seasons));
    }

    @Override
    public IAgriGrowCondition inSeasons(int strength, Collection<AgriSeason> seasons) {
        return this.season(strength, season -> seasons.stream().anyMatch(season::matches));
    }

    @Override
    public IAgriGrowCondition notInSeason(int strength, AgriSeason season) {
        return this.season(strength, s -> !s.matches(season));
    }

    @Override
    public IAgriGrowCondition notInSeasons(int strength, AgriSeason... seasons) {
        return this.notInSeasons(strength, Arrays.asList(seasons));
    }

    @Override
    public IAgriGrowCondition notInSeasons(int strength, Collection<AgriSeason> seasons) {
        return this.season(strength, season -> seasons.stream().noneMatch(season::matches));
    }

    @Override
    public IAgriGrowCondition inVillage(int strength) {
        return this.inStructure(strength, Structure.VILLAGE);
    }

    @Override
    public IAgriGrowCondition inPillagerOutpost(int strength) {
        return this.inStructure(strength, Structure.PILLAGER_OUTPOST);
    }

    @Override
    public IAgriGrowCondition inMineshaft(int strength) {
        return this.inStructure(strength, Structure.MINESHAFT);
    }

    @Override
    public IAgriGrowCondition inMansion(int strength) {
        return this.inStructure(strength, Structure.WOODLAND_MANSION);
    }

    @Override
    public IAgriGrowCondition inPyramid(int strength) {
        return this.structure(strength, structure -> structure == Structure.DESERT_PYRAMID || structure == Structure.JUNGLE_PYRAMID);
    }

    @Override
    public IAgriGrowCondition inJunglePyramid(int strength) {
        return this.inStructure(strength, Structure.JUNGLE_PYRAMID);
    }

    @Override
    public IAgriGrowCondition inDesertPyramid(int strength) {
        return this.inStructure(strength, Structure.DESERT_PYRAMID);
    }

    @Override
    public IAgriGrowCondition inIgloo(int strength) {
        return this.inStructure(strength, Structure.IGLOO);
    }

    @Override
    public IAgriGrowCondition inRuinedPortal(int strength) {
        return this.inStructure(strength, Structure.RUINED_PORTAL);
    }

    @Override
    public IAgriGrowCondition inShipwreck(int strength) {
        return this.inStructure(strength, Structure.SHIPWRECK);
    }

    @Override
    public IAgriGrowCondition inSwampHut(int strength) {
        return this.inStructure(strength, Structure.SWAMP_HUT);
    }

    @Override
    public IAgriGrowCondition inStronghold(int strength) {
        return this.inStructure(strength, Structure.STRONGHOLD);
    }

    @Override
    public IAgriGrowCondition inMonument(int strength) {
        return this.inStructure(strength, Structure.MONUMENT);
    }

    @Override
    public IAgriGrowCondition inOceanRuin(int strength) {
        return this.inStructure(strength, Structure.OCEAN_RUIN);
    }

    @Override
    public IAgriGrowCondition inFortress(int strength) {
        return this.inStructure(strength, Structure.FORTRESS);
    }

    @Override
    public IAgriGrowCondition inEndCity(int strength) {
        return this.inStructure(strength, Structure.END_CITY);
    }

    @Override
    public IAgriGrowCondition inBuriedTreasure(int strength) {
        return this.inStructure(strength, Structure.BURIED_TREASURE);
    }

    @Override
    public IAgriGrowCondition inNetherFossil(int strength) {
        return this.inStructure(strength, Structure.NETHER_FOSSIL);
    }

    @Override
    public IAgriGrowCondition inBastionRemnant(int strength) {
        return this.inStructure(strength, Structure.BASTION_REMNANT);
    }

    @Override
    public IAgriGrowCondition notInVillage(int strength) {
        return this.notInStructure(strength, Structure.VILLAGE);
    }

    @Override
    public IAgriGrowCondition notInPillagerOutpost(int strength) {
        return this.notInStructure(strength, Structure.PILLAGER_OUTPOST);
    }

    @Override
    public IAgriGrowCondition notInMineshaft(int strength) {
        return this.notInStructure(strength, Structure.MINESHAFT);
    }

    @Override
    public IAgriGrowCondition notInMansion(int strength) {
        return this.notInStructure(strength, Structure.WOODLAND_MANSION);
    }

    @Override
    public IAgriGrowCondition notInPyramid(int strength) {
        return this.structure(strength, structure -> structure != Structure.DESERT_PYRAMID && structure != Structure.JUNGLE_PYRAMID);
    }

    @Override
    public IAgriGrowCondition notInJunglePyramid(int strength) {
        return this.notInStructure(strength, Structure.JUNGLE_PYRAMID);
    }

    @Override
    public IAgriGrowCondition notInDesertPyramid(int strength) {
        return this.notInStructure(strength, Structure.DESERT_PYRAMID);
    }

    @Override
    public IAgriGrowCondition notInIgloo(int strength) {
        return this.notInStructure(strength, Structure.IGLOO);
    }

    @Override
    public IAgriGrowCondition notInRuinedPortal(int strength) {
        return this.notInStructure(strength, Structure.RUINED_PORTAL);
    }

    @Override
    public IAgriGrowCondition notInShipwreck(int strength) {
        return this.notInStructure(strength, Structure.SHIPWRECK);
    }

    @Override
    public IAgriGrowCondition notInSwampHut(int strength) {
        return this.notInStructure(strength, Structure.SWAMP_HUT);
    }

    @Override
    public IAgriGrowCondition notInStronghold(int strength) {
        return this.notInStructure(strength, Structure.STRONGHOLD);
    }

    @Override
    public IAgriGrowCondition notInMonument(int strength) {
        return this.notInStructure(strength, Structure.MONUMENT);
    }

    @Override
    public IAgriGrowCondition notInOceanRuin(int strength) {
        return this.notInStructure(strength, Structure.OCEAN_RUIN);
    }

    @Override
    public IAgriGrowCondition notInFortress(int strength) {
        return this.notInStructure(strength, Structure.FORTRESS);
    }

    @Override
    public IAgriGrowCondition notInEndCity(int strength) {
        return this.notInStructure(strength, Structure.END_CITY);
    }

    @Override
    public IAgriGrowCondition notInBuriedTreasure(int strength) {
        return this.notInStructure(strength, Structure.BURIED_TREASURE);
    }

    @Override
    public IAgriGrowCondition notInNetherFossil(int strength) {
        return this.notInStructure(strength, Structure.NETHER_FOSSIL);
    }

    @Override
    public IAgriGrowCondition notInBastionRemnant(int strength) {
        return this.notInStructure(strength, Structure.BASTION_REMNANT);
    }

    @Override
    public IAgriGrowCondition inStructure(int strength, IForgeStructure structure) {
        return this.structure(strength, structure::equals);
    }

    @Override
    public IAgriGrowCondition notInStructure(int strength, IForgeStructure structure) {
        return this.structure(strength, aStructure -> !structure.equals(aStructure));
    }
}
