package net.nameplate.config;

import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// Cloth Config's AutoConfig.getConfigScreen(...) convenience helper was
// removed from the library's public API in this version — building a
// screen now requires wiring ConfigScreenProvider/ConfigManager directly,
// which isn't worth chasing for a config-screen button. Config values still
// load fine from the JSON file; players without ModMenu's GUI just edit
// that file directly. getModConfigScreenFactory() is a default interface
// method, so simply not overriding it here is enough.
@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
}
