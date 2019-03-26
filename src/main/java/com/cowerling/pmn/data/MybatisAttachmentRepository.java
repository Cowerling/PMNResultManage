package com.cowerling.pmn.data;

import com.cowerling.pmn.annotation.GenericData;
import com.cowerling.pmn.data.mapper.AttachmentMapper;
import com.cowerling.pmn.domain.attachment.Attachment;
import com.cowerling.pmn.domain.attachment.AttachmentAuthority;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.AttachmentProvider.*;

@Repository
public class MybatisAttachmentRepository implements AttachmentRepository {
    @Autowired
    @GenericData
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession currentSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public Attachment findAttachmentById(Long id) {
        SqlSession sqlSession = currentSession();

        try {
            AttachmentMapper attachmentMapper = sqlSession.getMapper(AttachmentMapper.class);
            return attachmentMapper.selectAttachmentById(id);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<Attachment> findAttachmentsByUser(User user, Map<Field, Object> filters, List<Pair<Field, Order>> orders, int offset, int limit) {
        SqlSession sqlSession = currentSession();

        try {
            AttachmentMapper attachmentMapper = sqlSession.getMapper(AttachmentMapper.class);
            RowBounds rowBounds = new RowBounds(offset, limit);

            return attachmentMapper.selectAttachmentsByUserId(user.getId(), filters, orders, rowBounds);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<Attachment> findAttachmentsByProject(Project project) {
        SqlSession sqlSession = currentSession();

        try {
            AttachmentMapper attachmentMapper = sqlSession.getMapper(AttachmentMapper.class);

            return attachmentMapper.selectAttachmentsByProjectId(project.getId());
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Long findAttachmentCountByUser(User user, Map<Field, Object> filters) {
        SqlSession sqlSession = currentSession();

        try {
            AttachmentMapper attachmentMapper = sqlSession.getMapper(AttachmentMapper.class);

            return attachmentMapper.selectAttachmentCountByUserId(user.getId(), filters);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<AttachmentAuthority> findAttachmentAuthorities(Attachment attachment, User associator) {
        SqlSession sqlSession = currentSession();

        try {
            AttachmentMapper attachmentMapper = sqlSession.getMapper(AttachmentMapper.class);

            return attachmentMapper.selectAttachmentAuthoritiesByAttachmentId(attachment.getId(), associator.getId());
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveAttachmentAuthority(Attachment attachment, User associator, AttachmentAuthority attachmentAuthority) {
        SqlSession sqlSession = currentSession();

        try {
            AttachmentMapper attachmentMapper = sqlSession.getMapper(AttachmentMapper.class);
            attachmentMapper.insertAttachmentAuthorityByAttachmentId(attachment.getId(), associator.getId(), attachmentAuthority);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveAttachmentAuthorities(Attachment attachment, User associator, AttachmentAuthority[] attachmentAuthorities) {
        for (AttachmentAuthority attachmentAuthority: attachmentAuthorities) {
            saveAttachmentAuthority(attachment, associator, attachmentAuthority);
        }
    }

    @Override
    public void removeAttachmentAuthorities(Attachment attachment) {
        SqlSession sqlSession = currentSession();

        try {
            AttachmentMapper attachmentMapper = sqlSession.getMapper(AttachmentMapper.class);
            attachmentMapper.deleteAttachmentAuthoritiesByAttachmentId(attachment.getId());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void removeAttachmentAuthorities(Attachment attachment, User associator) {
        SqlSession sqlSession = currentSession();

        try {
            AttachmentMapper attachmentMapper = sqlSession.getMapper(AttachmentMapper.class);
            attachmentMapper.deleteAttachmentAuthoritiesByAssociatorId(attachment.getId(), associator.getId());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void updateAttachmentAuthorities(Attachment attachment, User associator, AttachmentAuthority[] attachmentAuthorities) {
        removeAttachmentAuthorities(attachment, associator);
        saveAttachmentAuthorities(attachment, associator, attachmentAuthorities);
    }

    @Override
    public void saveAttachment(Attachment attachment) {
        SqlSession sqlSession = currentSession();

        try {
            AttachmentMapper attachmentMapper = sqlSession.getMapper(AttachmentMapper.class);
            attachmentMapper.insertAttachment(attachment);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void removeAttachmentSeparately(Attachment attachment) {
        SqlSession sqlSession = currentSession();

        try {
            AttachmentMapper attachmentMapper = sqlSession.getMapper(AttachmentMapper.class);
            attachmentMapper.deleteAttachment(attachment);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        removeAttachmentAuthorities(attachment);
        removeAttachmentSeparately(attachment);
    }
}
