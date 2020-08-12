package net.genin.christophe.secure.fileupload.models.adapters;

import java.util.List;

public interface ConfigurationAdapter {

    List<String> extensionsIdApplication(String idApplication);

    Long validateTimeByIdApplication(String idApplication);
}
