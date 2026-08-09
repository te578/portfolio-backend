package com.example.demo.repository;

import com.example.demo.entity.Site;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SiteRepository {

    // company_cdはFilterがサブドメインから取り出したテナントコードと一致するか確認するために使う
    @Select("SELECT site_id AS siteId, company_cd AS companyCd, company_nm AS companyNm, status " +
            "FROM sites WHERE company_cd = #{companyCd} AND deleted_at IS NULL")
    Site findByCompanyCd(String companyCd);
}
