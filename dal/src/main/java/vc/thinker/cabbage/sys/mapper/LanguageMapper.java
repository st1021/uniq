package vc.thinker.cabbage.sys.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import vc.thinker.cabbage.sys.bo.LanguageBO;
import vc.thinker.cabbage.sys.model.Language;
import vc.thinker.cabbage.sys.model.LanguageExample;

public interface LanguageMapper {
    int countByExample(LanguageExample example);

    int deleteByExample(LanguageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Language record);

    int insertSelective(Language record);

    List<LanguageBO> selectByExample(LanguageExample example);

    LanguageBO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Language record, @Param("example") LanguageExample example);

    int updateByExample(@Param("record") Language record, @Param("example") LanguageExample example);

    int updateByPrimaryKeySelective(Language record);

    int updateByPrimaryKey(Language record);
}