package com.cowerling.pmn.data;

import com.cowerling.pmn.annotation.GenericData;
import com.cowerling.pmn.data.mapper.ProjectMapper;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MybatisProjectRepository implements ProjectRepository {
    @Autowired
    @GenericData
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession currentSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public List<Project> findProjectsByUser(User user, FindMode findMode, int offset, int limit) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);
            RowBounds rowBounds = new RowBounds(offset, limit);

            switch (findMode) {
                case CREATOR:
                    return projectMapper.selectProjectsByCreatorId(user.getId(), rowBounds);
                case MANAGER:
                    return projectMapper.selectProjectsByManagerId(user.getId(), rowBounds);
                case PRINCIPAL:
                    return projectMapper.selectProjectsByPrincipalId(user.getId(), rowBounds);
                case PARTICIPATOR:
                    return projectMapper.selectProjectsByParticipatorId(user.getId(), rowBounds);
                default:
                    return null;
            }
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Long findProjectCountByUser(User user, FindMode findMode) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);

            switch (findMode) {
                case CREATOR:
                    return projectMapper.selectProjectCountByCreatorId(user.getId());
                case MANAGER:
                    return projectMapper.selectProjectCountByManagerId(user.getId());
                case PRINCIPAL:
                    return projectMapper.selectProjectCountByPrincipalId(user.getId());
                case PARTICIPATOR:
                    return projectMapper.selectProjectCountByParticipatorId(user.getId());
                default:
                    return null;
            }
        } finally {
            sqlSession.close();
        }
    }
}
