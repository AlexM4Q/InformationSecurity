package com.futur.infoseq.service;

import com.futur.common.helpers.StringHelper;
import com.futur.common.helpers.resources.ResourcesHelper;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public final class ResourceService {

    @NotNull
    public static final URL START_LAYOUT_FXML = ResourcesHelper.getInternalUrl("view/start_layout.fxml");

    private ResourceService() {
        StringHelper.throwNonInitializeable();
    }

}
