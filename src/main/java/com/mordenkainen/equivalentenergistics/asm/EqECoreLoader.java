package com.mordenkainen.equivalentenergistics.asm;

import java.util.Map;

import com.mordenkainen.equivalentenergistics.core.Reference;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name(Reference.MOD_NAME + " Core")
public class EqECoreLoader implements IFMLLoadingPlugin {

    public static boolean deobf = false;
    
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { EqECoreTransformer.class.getName() };
    }

    @Override
    public String getModContainerClass() {
        return EqECore.class.getName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(final Map<String, Object> data) {
        deobf = !(Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
