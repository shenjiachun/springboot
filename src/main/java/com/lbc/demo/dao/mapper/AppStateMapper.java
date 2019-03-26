package com.lbc.demo.dao.mapper;

import com.lbc.demo.entity.AppState;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppStateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppState record);

    int insertSelective(AppState record);

    AppState selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppState record);

    int updateByPrimaryKey(AppState record);

    List<AppState> selectByAppId(AppState appState);

    List<AppState> allByAppId(AppState appState);

    int updateEndTime(AppState record);

}
