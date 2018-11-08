package com.cowerling.pmn.data;

import com.cowerling.pmn.annotation.GenericData;
import com.cowerling.pmn.data.mapper.ProjectMapper;
import com.cowerling.pmn.domain.data.DataRecordCategory;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.project.ProjectVerification;
import com.cowerling.pmn.domain.user.User;
import org.apache.commons.lang3.tuple.Pair;
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
    public List<Project> findProjectsByUser(User user, FindMode findMode, Map<Field, Object> filters, List<Pair<Field, Order>> orders) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);

            return projectMapper.selectProjectsByUserId(user.getId(), findMode, filters, orders, new RowBounds(0, Integer.MAX_VALUE));
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Long findProjectCountByUser(User user, FindMode findMode, Map<Field, Object> filters) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);

            return projectMapper.selectProjectCountByUserId(user.getId(), findMode, filters);
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
    public void removeProjectSeparately(Project project) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);
            projectMapper.deleteProjectById(project.getId());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void removeProject(Project project) {
        removeProjectVerification(project);
        removeProjectSeparately(project);
    }

    @Override
    public List<DataRecordCategory> findDataRecordCategoriesByProject(Project project) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);

            return projectMapper.selectDataRecordCategoriesByProjectCategory(project.getCategory());
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveProjectVerification(Project project, ProjectVerification projectVerification) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);
            projectMapper.insertProjectVerification(project.getId(), projectVerification);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void updateProjectVerification(Project project, ProjectVerification projectVerification) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);
            projectMapper.updateProjectVerification(project.getId(), projectVerification);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void removeProjectVerification(Project project) {
        SqlSession sqlSession = currentSession();

        try {
            ProjectMapper projectMapper = sqlSession.getMapper(ProjectMapper.class);
            projectMapper.deleteProjectVerificationByProjectId(project.getId());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
