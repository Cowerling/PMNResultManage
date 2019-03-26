package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.attachment.Attachment;
import com.cowerling.pmn.domain.attachment.AttachmentAuthority;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.AttachmentProvider.*;

public interface AttachmentRepository {
    Attachment findAttachmentById(Long id);
    List<Attachment> findAttachmentsByUser(User user, Map<Field, Object> filters, List<Pair<Field, Order>> orders, int offset, int limit);
    List<Attachment> findAttachmentsByProject(Project project);
    Long findAttachmentCountByUser(User user, Map<Field, Object> filters);
    List<AttachmentAuthority> findAttachmentAuthorities(Attachment attachment, User associator);
    void saveAttachmentAuthority(Attachment attachment, User associator, AttachmentAuthority attachmentAuthority);
    void saveAttachmentAuthorities(Attachment attachment, User associator, AttachmentAuthority[] attachmentAuthorities);
    void removeAttachmentAuthorities(Attachment attachment);
    void removeAttachmentAuthorities(Attachment attachment, User associator);
    void updateAttachmentAuthorities(Attachment attachment, User associator, AttachmentAuthority[] attachmentAuthorities);
    void saveAttachment(Attachment attachment);
    void removeAttachmentSeparately(Attachment attachment);
    void removeAttachment(Attachment attachment);
}
