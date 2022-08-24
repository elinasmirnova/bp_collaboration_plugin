package org.freeplane.plugin.collaboration;

import org.freeplane.features.mode.mindmapmode.MModeController;
import org.freeplane.main.mindmapmode.stylemode.SModeController;
import org.freeplane.main.osgi.IModeControllerExtensionProvider;
import org.freeplane.plugin.collaboration.actions.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

/**
 * The main class in plugin, activates all the extra actions in this package.
 */
public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) {
        registerMindMapModeExtension(context);
    }

    private void registerMindMapModeExtension(final BundleContext context) {
        final Hashtable<String, String[]> props = new Hashtable<>();
        props.put("mode", new String[]{MModeController.MODENAME, SModeController.MODENAME});
        context.registerService(
                IModeControllerExtensionProvider.class.getName(),
                (IModeControllerExtensionProvider) modeController -> {
                    if (modeController.getModeName().equals("MindMap")) {
                        modeController.addAction(new OpenWebPluginAction());
                        modeController.addAction(new ServerConnectionAction());
                        modeController.addAction(new OpenServerMindMapAction());
                        modeController.addAction(new SaveServerMindMapAction());
                    }
                }, props);
    }

    @Override
    public void stop(BundleContext context) {
    }
}
