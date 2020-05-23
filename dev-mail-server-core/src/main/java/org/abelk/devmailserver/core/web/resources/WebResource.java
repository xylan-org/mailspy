package org.abelk.devmailserver.core.web.resources;

import lombok.Builder;
import lombok.Data;

/**
 * A representation of a web resource after processing (i.e. setting prefixes, ordering, etc.).
 *
 */
@Data
@Builder
public class WebResource {

    private WebResourceType type;
    private String file;

}
