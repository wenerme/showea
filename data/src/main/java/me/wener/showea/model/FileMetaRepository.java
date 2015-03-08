package me.wener.showea.model;

import java.util.List;

public interface FileMetaRepository extends AbstractRepository<FileMeta, Long>
{
    List<FileMeta> findAllBySha1(String sha1);

    List<FileMeta> findAllByMd5(String md5);
}
