package com.hoopawolf.mwaw.entities.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.stream.Collectors;

public class EntityHelper
{
    public static double getAngleBetweenEntities(Entity first, Entity second)
    {
        return Math.atan2(second.getZ() - first.getZ(), second.getX() - first.getX()) * (180 / Math.PI) + 90;
    }

    public static List<Player> getPlayersNearby(Entity ent, double distanceX, double distanceY, double distanceZ, double radius)
    {
        List<Entity> nearbyEntities = ent.level.getEntities(ent, ent.getBoundingBox().inflate(distanceX, distanceY, distanceZ));
        return nearbyEntities.stream().filter(entityNeighbor -> entityNeighbor instanceof Player && ent.distanceTo(entityNeighbor) <= radius).map(entityNeighbor -> (Player) entityNeighbor).collect(Collectors.toList());
    }

    public static List<LivingEntity> getEntityLivingBaseNearby(Entity ent, double distanceX, double distanceY, double distanceZ, double radius)
    {
        return getEntitiesNearby(ent, LivingEntity.class, distanceX, distanceY, distanceZ, radius);
    }

    public static <T extends Entity> List getEntitiesNearby(Entity ent, Class<T> entityClass, double r)
    {
        return ent.level.getEntities(EntityTypeTest.forClass(entityClass), ent.getBoundingBox().inflate(r, r, r), e -> e != ent && ent.distanceTo(e) <= r);
    }

    public static <T extends Entity> List<T> getEntitiesNearby(Entity ent, Class<T> entityClass, double dX, double dY, double dZ, double r)
    {
        return ent.level.getEntities(EntityTypeTest.forClass(entityClass), ent.getBoundingBox().inflate(dX, dY, dZ), e -> e != ent && ent.distanceTo(e) <= r && e.getY() <= ent.getY() + dY);
    }

    public static <T extends Entity> List<T> getEntitiesNearbyWithPos(Level world, AABB box, BlockPos pos, Class<T> entityClass, double dX, double dY, double dZ, double r)
    {
        return world.getEntities(EntityTypeTest.forClass(entityClass), box.inflate(dX, dY, dZ), e -> pos.closerThan(e.position(), r) && e.getY() <= pos.getY() + dY);
    }
}
