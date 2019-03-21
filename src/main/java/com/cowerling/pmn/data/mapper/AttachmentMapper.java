package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.data.provider.AttachmentProvider;
import com.cowerling.pmn.domain.attachment.Attachment;
import com.cowerling.pmn.domain.attachment.AttachmentAuthority;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.AttachmentProvider.*;

public interface AttachmentMapper {
    @Select("SELECT t_attachment.id, t_attachment.name AS name, file, project, uploader, upload_time, t_attachment.remark AS remark " +
            "FROM t_attachment " +
            "WHERE t_attachment.id = #{id}")
    @ResultMap("com.cowerling.pmn.data.mapper.AttachmentMapper.attachmentResult")
    Attachment selectAttachmentById(Long id);

    @SelectProvider(type = AttachmentProvider.class, method = "selectAttachmentsByUserId")
    @ResultMap("com.cowerling.pmn.data.mapper.AttachmentMapper.attachmentResult")
    List<Attachment> selectAttachmentsByUserId(Long userId, Map<Field, Object> filters, List<Pair<Field, Order>> orders, RowBounds rowBounds);

    @SelectProvider(type = AttachmentProvider.class, method = "selectAttachmentCountByUserId")
    Long selectAttachmentCountByUserId(Long userId, Map<Field, Object> filters);

    @Select("SELECT t_attachment_auth_category.category AS category " +
            "FROM t_attachment_auth " +
            "LEFT OUTER JOIN t_attachment_auth_category ON t_attachment_auth.category = t_attachment_auth_category.id " +
            "WHERE attachment = #{attachmentId} AND associator = #{associatorId}")
    List<AttachmentAuthority> selectAttachmentAuthoritiesByAttachmentId(@Param("attachmentId") Long attachmentId, @Param("associatorId") Long associatorId);

    @Insert("INSERT INTO t_attachment_auth(attachment, associator, category) " +
            "VALUES(#{attachmentId}, #{associatorId}, (SELECT id FROM t_attachment_auth_category WHERE category = #{attachmentAuthority}))")
    void insertAttachmentAuthorityByAttachmentId(@Param("attachmentId") Long attachmentId, @Param("associatorId") Long associatorId, @Param("attachmentAuthority") AttachmentAuthority attachmentAuthority);

    @Delete("DELETE FROM t_attachment_auth " +
            "WHERE attachment = #{attachmentId}")
    void deleteAttachmentAuthoritiesByAttachmentId(Long attachmentId);

    @Delete("DELETE FROM t_attachment_auth " +
            "WHERE attachment = #{attachmentId} AND associator = #{associatorId}")
    void deleteAttachmentAuthoritiesByAssociatorId(@Param("attachmentId") Long attachmentId, @Param("associatorId") Long associatorId);

    @Insert("INSERT INTO t_attachment(name, file, project, uploader, upload_time, remark) " +
            "VALUES(#{name}, #{file}, #{project.id}, #{uploader.id}, #{uploadTime}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertAttachment(Attachment attachment);

    @Delete("DELETE FROM t_attachment " +
            "WHERE id = #{id}")
    void deleteAttachment(Attachment attachment);
}
