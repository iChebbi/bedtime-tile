package dev.local.bedtimetile;

import android.net.Uri;
import android.os.IBinder;
import android.service.notification.Condition;
import java.util.Map;

/**
 * Runs as the adb shell user (via Shizuku) with app_process, because only the system or a holder of
 * MANAGE_NOTIFICATIONS may switch a mode owned by another app (Digital Wellbeing's Bedtime mode).
 * Usage: app_process / dev.local.bedtimetile.Toggle [status|toggle|on|off]  -> prints on|off|none
 */
public class Toggle {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        String cmd = args.length > 0 ? args[0] : "status";
        IBinder binder = (IBinder) Class.forName("android.os.ServiceManager")
                .getMethod("getService", String.class).invoke(null, "notification");
        Object nm = Class.forName("android.app.INotificationManager$Stub")
                .getMethod("asInterface", IBinder.class).invoke(null, binder);
        Object config = nm.getClass().getMethod("getZenModeConfig").invoke(nm);
        Map<String, Object> rules = (Map<String, Object>) config.getClass().getField("automaticRules").get(config);
        for (Map.Entry<String, Object> e : rules.entrySet()) {
            Object rule = e.getValue();
            if (rule.getClass().getField("type").getInt(rule) != 3 /* AutomaticZenRule.TYPE_BEDTIME */) continue;
            boolean on = (boolean) rule.getClass().getMethod("isActive").invoke(rule);
            boolean want = cmd.equals("toggle") ? !on : cmd.equals("on") || (!cmd.equals("off") && on);
            if (want != on) {
                Uri conditionId = (Uri) rule.getClass().getField("conditionId").get(rule);
                Condition c = new Condition(conditionId, "", want ? Condition.STATE_TRUE : Condition.STATE_FALSE,
                        Condition.SOURCE_USER_ACTION);
                nm.getClass().getMethod("setAutomaticZenRuleState", String.class, Condition.class)
                        .invoke(nm, e.getKey(), c);
            }
            System.out.println(want ? "on" : "off");
            return;
        }
        System.out.println("none");
    }
}
