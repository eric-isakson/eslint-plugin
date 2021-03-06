package com.eslint.config.schema;

import com.eslint.utils.FileUtils;
import com.google.common.io.Files;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

// TODO refresh when config change
public final class RuleCache {
    // TODO figure out a way to automatically get this path or add it to config
    public static String defaultPath = "/usr/local/lib/node_modules/eslint/lib/rules";

    public List<String> rules = new ArrayList<String>();

    public Set<String> rulesMap = ContainerUtil.newLinkedHashSet();

    public static RuleCache instance;

    public void read(String path) {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith(".js");
            }
        };
        String[] rules1 = new File(path).list(filter);
        rules.addAll(Arrays.asList(rules1));

        List<String> names = ContainerUtil.map(rules1, new Function<String, String>() {
            public String fun(String file) {
                return Files.getNameWithoutExtension(file);
            }
        });
        rulesMap.addAll(names);
    }

    public static void initialize(Project project, String rulesPath) {
        instance = new RuleCache();
        ESLintSchema.load();
        SchemaJsonObject rules = ESLintSchema.ROOT.findOfType(ESLintSchema.RULES);
        if (rules != null) {
            for (BaseType b : rules.properties) {
                RuleCache.instance.rulesMap.add(b.title);
            }
        }
        String absRulesPath = FileUtils.resolvePath(project, rulesPath);
        if (StringUtil.isNotEmpty(absRulesPath)) {
            instance.read(absRulesPath);
        }
//        instance.read(RuleCache.defaultPath);
    }

    // c:/users/user/appdata/roaming/npm/node_modules

    public static void initializeFromPath(Project project, String rulesPath) {
        instance = new RuleCache();
        String absRulesPath = FileUtils.resolvePath(project, rulesPath);
        if (StringUtil.isNotEmpty(absRulesPath)) {
            instance.read(absRulesPath);
        }
        instance.read(RuleCache.defaultPath);
    }
}
