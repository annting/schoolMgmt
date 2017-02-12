package com.anting.dao;

import com.anting.entity.FilingType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FilingTypeMapper {
    int deleteByById(Integer id);

    int insert(FilingType record);

    int insertSelective(FilingType record);

    FilingType selectByById(Integer id);

    int updateByByIdSelective(FilingType record);

    int updateByById(FilingType record);
}