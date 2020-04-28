package org.abelk.devmailserver.core.mailserver.convert;

import java.io.IOException;

import org.abelk.devmailserver.core.domain.DmsAttachment;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.springframework.stereotype.Component;

import tech.blueglacier.email.Attachment;

@Component
public class AttachmentConverter {

    private static final String TEXT_MEDIA_TYPE = "text";

    public DmsAttachment convert(final Attachment attachment) {
        DmsAttachment result = new DmsAttachment();
        if (attachment == null) {
            result = null;
        } else {
            try {
                final BodyDescriptor bodyDescriptor = attachment.getBd();
                final String charset = bodyDescriptor.getCharset();
                result.setMimeType(bodyDescriptor.getMimeType());
                result.setCharset(charset);
                result.setName(attachment.getAttachmentName());

                final byte[] bodyRaw = IOUtils.toByteArray(attachment.getIs());
                result.setBodyRaw(bodyRaw);
                if (mightBeText(bodyDescriptor)) {
                    result.setBodyText(new String(bodyRaw, Charsets.toCharset(charset)));
                }
            } catch (final IOException exception) {
                result = DmsAttachment.ofException(exception);
            }
        }
        return result;
    }

    private boolean mightBeText(final BodyDescriptor bodyDescriptor) {
        return bodyDescriptor.getCharset() != null || bodyDescriptor.getMediaType().equalsIgnoreCase(TEXT_MEDIA_TYPE);
    }

}
