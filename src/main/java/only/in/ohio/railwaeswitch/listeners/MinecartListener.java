package only.in.ohio.railwaeswitch.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.ArrayList;
import java.util.stream.Collectors;

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
            var direction = getCartDirection(getVelocity(event));

            var next = rails.getRelative(direction);
            if (next.getType() != Material.RAIL) return;

            // ascending tracks can't be switched
            if (next.getBlockData().getAsString().contains(SHAPE_ASC)) return;

            var yaw = passenger.getLocation().getYaw();
            setRailShape(next, getRailShape(direction, yaw));
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

    private static BlockFace getCartDirection(Location speed)
    {
        if /**/ (speed.getX() != 0) return speed.getX() > 0 ? BlockFace.EAST : BlockFace.WEST;
        else if (speed.getZ() != 0) return speed.getZ() > 0 ? BlockFace.SOUTH : BlockFace.NORTH;

        else return BlockFace.SELF;
    }

    private static Rail.Shape getRailShape(BlockFace minecartDirection, float passengerYaw)
    {
        // todo select only valid directions (1-3)

        var routes = new ArrayList<BlockFace>();
        routes.add(BlockFace.NORTH);
        routes.add(BlockFace.EAST);
        routes.add(BlockFace.SOUTH);
        routes.add(BlockFace.WEST);

        // can't turn backwards
        routes.remove(minecartDirection.getOppositeFace());

        var angles = routes.stream().map(x -> angleDifference(passengerYaw, getDirectionYaw(x))).collect(Collectors.toList());

        var closest = angles.stream().min(Float::compareTo);
        var index = closest.map(angles::indexOf).orElse(0);
        var route = routes.get(index);

        return getRailShape(minecartDirection, route);
    }

    public static float getDirectionYaw(BlockFace direction)
    {
        return switch (direction)
        {
            case EAST -> -90;
            case SOUTH -> 0;
            case WEST -> 90;
            default -> 180;
        };
    }

    private static float angleDifference(float yaw1, float yaw2)
    {
        var abs = Math.abs(yaw1 - yaw2);
        return abs > 180 ? 360 - abs : abs;
    }

    private static Rail.Shape getRailShape(BlockFace straight, BlockFace turn)
    {
        if (straight == turn)
        {
            return straight.ordinal() % 2 == 0 ? Rail.Shape.NORTH_SOUTH : Rail.Shape.EAST_WEST;
        }
        if (straight == BlockFace.NORTH)
        {
            return turn == BlockFace.EAST ? Rail.Shape.SOUTH_EAST : Rail.Shape.SOUTH_WEST;
        }
        if (straight == BlockFace.SOUTH)
        {
            return turn == BlockFace.EAST ? Rail.Shape.NORTH_EAST : Rail.Shape.NORTH_WEST;
        }
        if (straight == BlockFace.EAST)
        {
            return turn == BlockFace.NORTH ? Rail.Shape.NORTH_WEST : Rail.Shape.SOUTH_WEST;
        }
        if (straight == BlockFace.WEST)
        {
            return turn == BlockFace.NORTH ? Rail.Shape.NORTH_EAST : Rail.Shape.SOUTH_EAST;
        }
        return Rail.Shape.EAST_WEST; // <-- literally anything
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
