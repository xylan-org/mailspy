package org.abelk.devmailserver.core.web.resources;

import java.util.List;

import lombok.Data;

/**
 * A group of web resources that can be bundled together in a production build.
 *
 * In a production build, these objects are replaced by copies in which the
 * files list is swapped to a singleton list containing only the bundled (i.e.
 * concatenated, minified, etc.) resource.
 *
 */
@Data
public class WebResourceBundle {

    private String name;
    private String classpathPrefix;
    private WebResourceType type;
    private List<String> files;
    private Integer order;

}
