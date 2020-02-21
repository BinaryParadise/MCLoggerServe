package com.frontend.mappers;

import com.frontend.domain.MCEnvConfig;
import com.frontend.domain.MCEnvConfigDetail;
import com.frontend.models.MCPagination;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EnvConfigMapper {
  boolean insert(MCEnvConfig config);

  boolean deleteById(int id);

  boolean update(MCEnvConfig config);

  MCEnvConfig findById(int id);

  List<MCEnvConfigDetail> findByAppId(int appId, int type);

  List<MCEnvConfigDetail> findByAppIdPage(int appId, int type, MCPagination page);

  List<MCEnvConfig> findByAppKey(String appkey);
}
