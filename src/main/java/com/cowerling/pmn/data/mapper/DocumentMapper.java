package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.domain.document.Document;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DocumentMapper {
    @Select("SELECT id, name, brief, file " +
            "FROM t_document")
    @ResultMap("com.cowerling.pmn.data.mapper.DocumentMapper.documentResult")
    List<Document> selectDocuments();

    @Select("SELECT id, name, brief, file " +
            "FROM t_document " +
            "WHERE id = #{id}")
    @ResultMap("com.cowerling.pmn.data.mapper.DocumentMapper.documentResult")
    Document selectDocumentById(Long id);
}
