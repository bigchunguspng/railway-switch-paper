package only.in.ohio.railwaeswitch;

import only.in.ohio.railwaeswitch.listeners.MinecartListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        Bukkit.getLogger().info("Using RAIL WAE SWITCH!!!!! [WHOLESOME 100 EPIC 25 TRANSPORT 89]");
        getServer().getPluginManager().registerEvents(new MinecartListener(), this);
    }

    @Override
    public void onDisable()
    {
        Bukkit.getLogger().info("No more RAIL WAE SWITCH!!! *BOOM!*");
    }
}
