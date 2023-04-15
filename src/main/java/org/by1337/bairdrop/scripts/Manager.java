package org.by1337.bairdrop.scripts;


import org.bukkit.Bukkit;
import org.by1337.bairdrop.BAirDrop;
import org.by1337.bairdrop.ConfigManager.Config;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class Manager {
    public Object runJsScript(String scriptName, HashMap<String, Object> property) {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);

        Scriptable scope = context.initStandardObjects();
        ScriptableObject.putProperty(scope, "Bukkit", Context.javaToJS(Bukkit.getServer(), scope));
        ScriptableObject.putProperty(scope, "BAirDrop", Context.javaToJS(BAirDrop.instance, scope));
        for (Map.Entry<String, Object> entry : property.entrySet()){
            ScriptableObject.putProperty(scope, entry.getKey(), Context.javaToJS(entry.getValue(), scope));
        }
        try {
            //return context.evaluateReader(scope, new FileReader(Config.scripts.get(scriptName)), "JavaScript", 1, null);
            return context.evaluateReader(scope, new InputStreamReader(new FileInputStream(Config.scripts.get(scriptName)), StandardCharsets.UTF_8), "JavaScript", 1, null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Context.exit();
        }
        return null;
    }
}