package com.anting.dao;

import com.anting.entity.Filing;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FilingMapper {
    int deleteByById(Integer id);

    int insert(Filing record);

    int insertSelective(Filing record);

    Filing selectByById(Integer id);

    int updateByByIdSelective(Filing record);

    int updateByById(Filing record);
}