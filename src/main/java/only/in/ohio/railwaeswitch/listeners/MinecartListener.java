package only.in.ohio.railwaeswitch.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class MinecartListener implements Listener
{
    final String SHAPE_ASC = "shape=ascending";

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event)
    {
        var vehicle = event.getVehicle();
        if (vehicle.getType() != EntityType.MINECART) return;

        var passengers = vehicle.getPassengers();
        if (passengers.isEmpty()) return;

        var passenger = passengers.get(0);

        var rails = vehicle.getLocation().getBlock();

        if (blockIsTwoWaySwitch(rails))
        {
            var direction = getMinecartDirection(getVelocity(event));

            Bukkit.getLogger().info(direction.toString());

            var next = rails.getRelative(direction);
            if (next.getType() != Material.RAIL) return;

            // ascending tracks can't be switched
            if (next.getBlockData().getAsString().contains(SHAPE_ASC)) return;

            var yaw = passenger.getLocation().getYaw();
            var shape = getRailShape(direction, yaw);

            setRailShape(next, shape);
        }
    }

    private static boolean blockIsTwoWaySwitch(Block rails)
    {
        var bottom = rails.getRelative(BlockFace.DOWN);
        return rails.getType() == Material.ACTIVATOR_RAIL && bottom.getType() == Material.IRON_BLOCK;
    }

    private static Location getVelocity(VehicleMoveEvent event)
    {
        return event.getTo().subtract(event.getFrom());
    }

    private static BlockFace getMinecartDirection(Location speed)
    {
        if /**/ (speed.getX() != 0) return speed.getX() > 0 ? BlockFace.EAST : BlockFace.WEST;
        else if (speed.getZ() != 0) return speed.getZ() > 0 ? BlockFace.SOUTH : BlockFace.NORTH;

        else return BlockFace.DOWN;
    }

    private static Rail.Shape getRailShape(BlockFace minecartDirection, float playerYaw)
    {
        switch (minecartDirection)
        {
            case NORTH:
                return Rail.Shape.SOUTH_EAST;
            case EAST:
                return Rail.Shape.SOUTH_WEST;
            case SOUTH:
                return Rail.Shape.NORTH_WEST;
            default:
                return Rail.Shape.NORTH_EAST;
        }
    }

    private static void setRailShape(Block rail, Rail.Shape shape)
    {
        var state = rail.getState();
        var data = (Rail) state.getBlockData();
        data.setShape(shape);
        state.setBlockData(data);
        state.update();
    }
}
