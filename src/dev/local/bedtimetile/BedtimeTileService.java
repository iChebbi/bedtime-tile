package dev.local.bedtimetile;

import android.content.pm.PackageManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import rikka.shizuku.Shizuku;

/** Quick settings tile that switches Digital Wellbeing's Bedtime mode by running {@link Toggle} through Shizuku. */
public class BedtimeTileService extends TileService {
    // The sticky listener fires at once when Shizuku is already connected, so it doubles as the initial refresh.
    private final Shizuku.OnBinderReceivedListener onBinder = () -> run("status");
    private final Shizuku.OnRequestPermissionResultListener onPermission = (code, result) -> run("status");

    @Override
    public void onStartListening() {
        Shizuku.addBinderReceivedListenerSticky(onBinder);
        Shizuku.addRequestPermissionResultListener(onPermission);
        if (!Shizuku.pingBinder()) show(false, "Start Shizuku");
    }

    @Override
    public void onStopListening() {
        Shizuku.removeBinderReceivedListener(onBinder);
        Shizuku.removeRequestPermissionResultListener(onPermission);
    }

    @Override
    public void onClick() {
        // Flip the tile at once; the shell command takes about half a second and corrects the tile if it fails.
        boolean want = getQsTile().getState() != Tile.STATE_ACTIVE;
        show(want, null);
        run(want ? "on" : "off");
    }

    private void run(String cmd) {
        if (!Shizuku.pingBinder()) {
            show(false, "Start Shizuku");
            return;
        }
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            show(false, "Tap to allow");
            if (!cmd.equals("status")) Shizuku.requestPermission(1);
            return;
        }
        new Thread(() -> {
            String out = null;
            try {
                // Shizuku.newProcess is private in the client library; it is the library's way to run a command as shell.
                Method newProcess = Shizuku.class.getDeclaredMethod("newProcess", String[].class, String[].class, String.class);
                newProcess.setAccessible(true);
                String script = "CLASSPATH='" + getApplicationInfo().sourceDir + "' app_process / " + Toggle.class.getName() + " " + cmd;
                Process p = (Process) newProcess.invoke(null, new String[] {"sh", "-c", script}, null, null);
                try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    out = r.readLine();
                }
                p.waitFor();
            } catch (Exception ignored) {
                // falls through to "Failed" below
            }
            if ("on".equals(out)) show(true, null);
            else if ("off".equals(out)) show(false, null);
            else show(false, "none".equals(out) ? "No Bedtime mode" : "Failed");
        }).start();
    }

    private void show(boolean on, String subtitle) {
        Tile tile = getQsTile();
        if (tile == null) return;
        tile.setState(on ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setSubtitle(subtitle);
        tile.updateTile();
    }
}
