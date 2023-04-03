package com.nobleradical.dirtpower;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class RedstoneBrick extends Block {
    public static IntProperty POWER = Properties.POWER;
    public static EnumProperty<Direction> SOURCE_DIRECTION = EnumProperty.of("source_direction", Direction.class);
    private record DirectedVector(int power, Direction direction) {}
    private record PowerContext(World world, BlockPos pos, BlockState state, int power, Direction dir) {}

    public RedstoneBrick(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(POWER, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER).add(SOURCE_DIRECTION);
    }

    private void tellAll(String str) {
        DirtPower.LOGGER.info(str);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;
        tellAll("Update "+pos.toString());
        this.update(world, pos, state, sourceBlock, sourcePos);
        //world.updateNeighborsExcept(pos, this, Direction.fromVector(sourcePos.subtract(pos)));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state;
    }

    private void newPower(PowerContext ctx) {
        ctx.world.setBlockState(ctx.pos, ctx.state.with(POWER, ctx.power).with(SOURCE_DIRECTION, ctx.dir), Block.NOTIFY_ALL);
    }

    private void unPower(PowerContext ctx) {
        ctx.world.setBlockState(ctx.pos, ctx.state.with(POWER, 0), Block.NOTIFY_ALL);
    }

    private void update(World world, BlockPos pos, BlockState state, Block sourceBlock, BlockPos sourcePos) {
        DirectedVector recievedRedstone = this.getReceivedRedstone(world, pos);
        int statePwr = state.get(POWER);
        Direction stateDir = state.get(SOURCE_DIRECTION);
        int powerFromStateDir = this.getEmittedRedstonePower(world, pos.offset(stateDir), stateDir);
        int extPwr = recievedRedstone.power;
        Direction extDir = recievedRedstone.direction;
        PowerContext ctx = new PowerContext(world, pos, state, extPwr, extDir);
        if (sourceBlock == this && !world.getBlockState(sourcePos).isOf(Blocks.AIR)) {
            if (world.getBlockState(sourcePos).get(POWER) == 0) {
                DirtPower.LOGGER.debug("Propogated unpower");
                unPower(ctx);
                world.createAndScheduleBlockTick(pos, this, 1);
                return;
            }
        }
        if (statePwr == 0 && extPwr > 0) {
            DirtPower.LOGGER.debug("repower");
            newPower(ctx);
        } else if (powerFromStateDir != statePwr) {
            DirtPower.LOGGER.debug("ghost power");
            unPower(ctx);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        update(world, pos, state, Blocks.DIRT, pos);
    }
    private DirectedVector getReceivedRedstone(World world, BlockPos pos) {
        int power = 0;
        Direction powerDirection = Direction.UP;
        for (Direction direction : DIRECTIONS) {
            if (!world.getBlockState(pos.offset(direction)).isIn(ModBlockTags.REDSTONE_WIRES)  || world.getBlockState(pos.offset(direction)).isOf(this)) {
                tellAll(direction.toString());
                int i = this.getEmittedRedstonePower(world, pos.offset(direction), direction);
                if (i > power) {
                    power = i;
                    powerDirection = direction;
                }
            }
        }
        return new DirectedVector(power, powerDirection);
    }

    public int getEmittedRedstonePower(World world, BlockPos pos, Direction direction) {
        BlockState blockState = world.getBlockState(pos);
        int i = blockState.getWeakRedstonePower(world, pos, direction);
        tellAll("weak "+Integer.toString(i));
        if (blockState.isSolidBlock(world, pos)) {
            tellAll("strongup? \\\\");
            int j = Math.max(i, this.getReceivedStrongRedstonePower(world, pos));
            tellAll("Strong max: "+Integer.toString(j));
            return j;
            
        }
        return i;
    }

    private int getReceivedStrongRedstonePower(World world, BlockPos pos) {
        int i = 0;
        for (Direction direction : DIRECTIONS) {
            if (!world.getBlockState(pos.offset(direction)).isIn(ModBlockTags.REDSTONE_WIRES) || world.getBlockState(pos.offset(direction)).isOf(this)) {
                i = Math.max(i, world.getStrongRedstonePower(pos.offset(direction), direction));
                tellAll(Integer.toString(i)+" "+direction.toString());
                if (i >= 15) {
                    return i;
                }
            } else {
                tellAll("excluded "+direction.toString());
            }
        }
        return i;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        var receivedRedstone = getReceivedRedstone(world, pos);
        int i = receivedRedstone.power;
        Direction j = receivedRedstone.direction;
        return this.getDefaultState().with(POWER, i).with(SOURCE_DIRECTION, j);
    }
    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }
    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }
    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction.getOpposite() != state.get(SOURCE_DIRECTION)) {
            return state.get(POWER);
        } else {
            return 0;
        }
    }
}
