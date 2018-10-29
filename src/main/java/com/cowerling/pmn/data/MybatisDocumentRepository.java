package com.cowerling.pmn.data;

import com.cowerling.pmn.annotation.GenericData;
import com.cowerling.pmn.data.mapper.DocumentMapper;
import com.cowerling.pmn.domain.document.Document;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MybatisDocumentRepository implements DocumentRepository {
    @Autowired
    @GenericData
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession currentSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public List<Document> findDocuments() {
        SqlSession sqlSession = currentSession();

        try {
            DocumentMapper documentMapper = sqlSession.getMapper(DocumentMapper.class);
            return documentMapper.selectDocuments();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Document findDocumentById(Long id) {
        SqlSession sqlSession = currentSession();

        try {
            DocumentMapper documentMapper = sqlSession.getMapper(DocumentMapper.class);
            return documentMapper.selectDocumentById(id);
        } finally {
            sqlSession.close();
        }
    }
}
