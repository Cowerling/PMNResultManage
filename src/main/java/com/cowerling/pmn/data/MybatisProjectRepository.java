package com.cowerling.pmn.data;

import com.cowerling.pmn.annotation.GenericData;
import com.cowerling.pmn.data.mapper.ProjectMapper;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import javafx.util.Pair;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.ProjectSqlProvider.*;

@Repository
public class MybatisProjectRepository implements ProjectRepository {
    @Autowired
    @GenericData
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession currentSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public Project findProjectById(Long id) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);

            return projectMapper.selectProjectById(id);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<Project> findProjectsByUser(User user, FindMode findMode, Map<Field, Object> filters, List<Pair<Field, Order>> orders, int offset, int limit) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);
            RowBounds rowBounds = new RowBounds(offset, limit);

            return projectMapper.selectProjectsByUserId(user.getId(), findMode, filters, orders, rowBounds);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Long findProjectCountByUser(User user, FindMode findMode) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);

            return projectMapper.selectProjectCountByUserId(user.getId(), findMode);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void updateProject(Project project) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);
            projectMapper.updateProject(project);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveProject(Project project) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);
            projectMapper.insertProject(project);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void removeProjectById(Long id) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);
            projectMapper.deleteProjectById(id);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
