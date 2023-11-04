package only.in.ohio.railwaeswitch.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
        if (vehicle.getType() == EntityType.MINECART)
        {
            var passengers = vehicle.getPassengers();
            if (!passengers.isEmpty())
            {
                var passenger = passengers.get(0);

                var rails = vehicle.getLocation().getBlock();
                var floor = rails.getRelative(BlockFace.DOWN);

                if (rails.getType() == Material.ACTIVATOR_RAIL && floor.getType() == Material.IRON_BLOCK)
                {
                    var velocity = event.getTo().subtract(event.getFrom());
                    var direction = GetMinecartDirection(velocity);

                    Bukkit.getLogger().info(velocity.toString());
                    Bukkit.getLogger().info(direction.toString());

                    var next = rails.getRelative(direction);
                    if (next.getType() != Material.RAIL) return;

                    // ascending tracks can't be switched
                    if (next.getBlockData().getAsString().contains(SHAPE_ASC)) return;

                    var yaw = passenger.getLocation().getYaw();
                    var shape = GetSwitchDirection(direction, yaw);

                    var state = next.getState();
                    var data = (Rail) state.getBlockData();
                    data.setShape(shape);
                    state.setBlockData(data);
                    state.update();
                }
            }
        }
    }

    private BlockFace GetMinecartDirection(Location speed)
    {
        if /**/ (speed.getX() != 0) return speed.getX() > 0 ? BlockFace.EAST : BlockFace.WEST;
        else if (speed.getZ() != 0) return speed.getZ() > 0 ? BlockFace.SOUTH : BlockFace.NORTH;

        else return BlockFace.DOWN;
    }

    private Rail.Shape GetSwitchDirection(BlockFace minecartDirection, float playerYaw)
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
}
