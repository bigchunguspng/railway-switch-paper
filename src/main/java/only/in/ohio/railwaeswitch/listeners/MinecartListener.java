package only.in.ohio.railwaeswitch.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class MinecartListener implements Listener
{
    final String SHAPE_EW = "shape=east_west";
    //final String SHAPE_NS = "shape=north_south";

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
                    var ew = rails.getBlockData().getAsString().contains(SHAPE_EW); // rails go along X axis

                    Bukkit.getLogger().info(ew ? "--X--" : "--Z--");

                    // get cart movement direction: EWSN

                    // get rail block after switch block (return if it ain't no rail)

                    // get passenger POV

                    // switch rails
                }
            }
        }
    }
}
