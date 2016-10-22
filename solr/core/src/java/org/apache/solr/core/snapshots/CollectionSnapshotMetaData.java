begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core.snapshots
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|snapshots
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|Replica
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CoreAdminParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONWriter
import|;
end_import

begin_comment
comment|/**  * This class defines the meta-data about a collection level snapshot  */
end_comment

begin_class
DECL|class|CollectionSnapshotMetaData
specifier|public
class|class
name|CollectionSnapshotMetaData
implements|implements
name|JSONWriter
operator|.
name|Writable
block|{
DECL|class|CoreSnapshotMetaData
specifier|public
specifier|static
class|class
name|CoreSnapshotMetaData
implements|implements
name|JSONWriter
operator|.
name|Writable
block|{
DECL|field|coreName
specifier|private
specifier|final
name|String
name|coreName
decl_stmt|;
DECL|field|indexDirPath
specifier|private
specifier|final
name|String
name|indexDirPath
decl_stmt|;
DECL|field|generationNumber
specifier|private
specifier|final
name|long
name|generationNumber
decl_stmt|;
DECL|field|leader
specifier|private
specifier|final
name|boolean
name|leader
decl_stmt|;
DECL|field|shardId
specifier|private
specifier|final
name|String
name|shardId
decl_stmt|;
DECL|field|files
specifier|private
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|files
decl_stmt|;
DECL|method|CoreSnapshotMetaData
specifier|public
name|CoreSnapshotMetaData
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|indexDirPath
parameter_list|,
name|long
name|generationNumber
parameter_list|,
name|String
name|shardId
parameter_list|,
name|boolean
name|leader
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|this
operator|.
name|coreName
operator|=
name|coreName
expr_stmt|;
name|this
operator|.
name|indexDirPath
operator|=
name|indexDirPath
expr_stmt|;
name|this
operator|.
name|generationNumber
operator|=
name|generationNumber
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|leader
operator|=
name|leader
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|CoreSnapshotMetaData
specifier|public
name|CoreSnapshotMetaData
parameter_list|(
name|NamedList
name|resp
parameter_list|)
block|{
name|this
operator|.
name|coreName
operator|=
operator|(
name|String
operator|)
name|resp
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexDirPath
operator|=
operator|(
name|String
operator|)
name|resp
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|INDEX_DIR_PATH
argument_list|)
expr_stmt|;
name|this
operator|.
name|generationNumber
operator|=
operator|(
name|Long
operator|)
name|resp
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|GENERATION_NUM
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
operator|(
name|String
operator|)
name|resp
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|SHARD_ID
argument_list|)
expr_stmt|;
name|this
operator|.
name|leader
operator|=
operator|(
name|Boolean
operator|)
name|resp
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|LEADER
argument_list|)
expr_stmt|;
name|this
operator|.
name|files
operator|=
operator|(
name|Collection
argument_list|<
name|String
argument_list|>
operator|)
name|resp
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|FILE_LIST
argument_list|)
expr_stmt|;
block|}
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
name|coreName
return|;
block|}
DECL|method|getIndexDirPath
specifier|public
name|String
name|getIndexDirPath
parameter_list|()
block|{
return|return
name|indexDirPath
return|;
block|}
DECL|method|getGenerationNumber
specifier|public
name|long
name|getGenerationNumber
parameter_list|()
block|{
return|return
name|generationNumber
return|;
block|}
DECL|method|getFiles
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFiles
parameter_list|()
block|{
return|return
name|files
return|;
block|}
DECL|method|getShardId
specifier|public
name|String
name|getShardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
DECL|method|isLeader
specifier|public
name|boolean
name|isLeader
parameter_list|()
block|{
return|return
name|leader
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|JSONWriter
name|arg0
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|info
operator|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
name|SolrSnapshotManager
operator|.
name|INDEX_DIR_PATH
argument_list|,
name|getIndexDirPath
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
name|SolrSnapshotManager
operator|.
name|GENERATION_NUM
argument_list|,
name|getGenerationNumber
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
name|SolrSnapshotManager
operator|.
name|SHARD_ID
argument_list|,
name|getShardId
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
name|SolrSnapshotManager
operator|.
name|LEADER
argument_list|,
name|isLeader
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
name|SolrSnapshotManager
operator|.
name|FILE_LIST
argument_list|,
name|getFiles
argument_list|()
argument_list|)
expr_stmt|;
name|arg0
operator|.
name|write
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|toNamedList
specifier|public
name|NamedList
name|toNamedList
parameter_list|()
block|{
name|NamedList
name|result
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|SolrSnapshotManager
operator|.
name|INDEX_DIR_PATH
argument_list|,
name|getIndexDirPath
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|SolrSnapshotManager
operator|.
name|GENERATION_NUM
argument_list|,
name|getGenerationNumber
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|SolrSnapshotManager
operator|.
name|SHARD_ID
argument_list|,
name|getShardId
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|SolrSnapshotManager
operator|.
name|LEADER
argument_list|,
name|isLeader
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|SolrSnapshotManager
operator|.
name|FILE_LIST
argument_list|,
name|getFiles
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|enum|SnapshotStatus
specifier|public
specifier|static
enum|enum
name|SnapshotStatus
block|{
DECL|enum constant|Successful
DECL|enum constant|InProgress
DECL|enum constant|Failed
name|Successful
block|,
name|InProgress
block|,
name|Failed
block|;   }
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|status
specifier|private
specifier|final
name|SnapshotStatus
name|status
decl_stmt|;
DECL|field|creationDate
specifier|private
specifier|final
name|Date
name|creationDate
decl_stmt|;
DECL|field|replicaSnapshots
specifier|private
specifier|final
name|List
argument_list|<
name|CoreSnapshotMetaData
argument_list|>
name|replicaSnapshots
decl_stmt|;
DECL|method|CollectionSnapshotMetaData
specifier|public
name|CollectionSnapshotMetaData
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|SnapshotStatus
operator|.
name|InProgress
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|CoreSnapshotMetaData
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|CollectionSnapshotMetaData
specifier|public
name|CollectionSnapshotMetaData
parameter_list|(
name|String
name|name
parameter_list|,
name|SnapshotStatus
name|status
parameter_list|,
name|Date
name|creationTime
parameter_list|,
name|List
argument_list|<
name|CoreSnapshotMetaData
argument_list|>
name|replicaSnapshots
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|creationDate
operator|=
name|creationTime
expr_stmt|;
name|this
operator|.
name|replicaSnapshots
operator|=
name|replicaSnapshots
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|CollectionSnapshotMetaData
specifier|public
name|CollectionSnapshotMetaData
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
operator|(
name|String
operator|)
name|data
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|SnapshotStatus
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|data
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|SNAPSHOT_STATUS
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|creationDate
operator|=
operator|new
name|Date
argument_list|(
operator|(
name|Long
operator|)
name|data
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|replicaSnapshots
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|r
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|SNAPSHOT_REPLICAS
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|x
range|:
name|r
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|x
decl_stmt|;
name|String
name|coreName
init|=
operator|(
name|String
operator|)
name|info
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
name|String
name|indexDirPath
init|=
operator|(
name|String
operator|)
name|info
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|INDEX_DIR_PATH
argument_list|)
decl_stmt|;
name|long
name|generationNumber
init|=
operator|(
name|Long
operator|)
name|info
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|GENERATION_NUM
argument_list|)
decl_stmt|;
name|String
name|shardId
init|=
operator|(
name|String
operator|)
name|info
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|SHARD_ID
argument_list|)
decl_stmt|;
name|boolean
name|leader
init|=
operator|(
name|Boolean
operator|)
name|info
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|LEADER
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|(
name|Collection
argument_list|<
name|String
argument_list|>
operator|)
name|info
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|FILE_LIST
argument_list|)
decl_stmt|;
name|replicaSnapshots
operator|.
name|add
argument_list|(
operator|new
name|CoreSnapshotMetaData
argument_list|(
name|coreName
argument_list|,
name|indexDirPath
argument_list|,
name|generationNumber
argument_list|,
name|shardId
argument_list|,
name|leader
argument_list|,
name|files
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|CollectionSnapshotMetaData
specifier|public
name|CollectionSnapshotMetaData
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|data
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
operator|(
name|String
operator|)
name|data
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|String
name|statusStr
init|=
operator|(
name|String
operator|)
name|data
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|SNAPSHOT_STATUS
argument_list|)
decl_stmt|;
name|this
operator|.
name|creationDate
operator|=
operator|new
name|Date
argument_list|(
operator|(
name|Long
operator|)
name|data
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|SnapshotStatus
operator|.
name|valueOf
argument_list|(
name|statusStr
argument_list|)
expr_stmt|;
name|this
operator|.
name|replicaSnapshots
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|r
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|SNAPSHOT_REPLICAS
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|x
range|:
name|r
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|x
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|coreName
init|=
operator|(
name|String
operator|)
name|info
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
name|String
name|indexDirPath
init|=
operator|(
name|String
operator|)
name|info
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|INDEX_DIR_PATH
argument_list|)
decl_stmt|;
name|long
name|generationNumber
init|=
operator|(
name|Long
operator|)
name|info
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|GENERATION_NUM
argument_list|)
decl_stmt|;
name|String
name|shardId
init|=
operator|(
name|String
operator|)
name|info
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|SHARD_ID
argument_list|)
decl_stmt|;
name|boolean
name|leader
init|=
operator|(
name|Boolean
operator|)
name|info
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|LEADER
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|(
name|Collection
argument_list|<
name|String
argument_list|>
operator|)
name|info
operator|.
name|get
argument_list|(
name|SolrSnapshotManager
operator|.
name|FILE_LIST
argument_list|)
decl_stmt|;
name|replicaSnapshots
operator|.
name|add
argument_list|(
operator|new
name|CoreSnapshotMetaData
argument_list|(
name|coreName
argument_list|,
name|indexDirPath
argument_list|,
name|generationNumber
argument_list|,
name|shardId
argument_list|,
name|leader
argument_list|,
name|files
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getStatus
specifier|public
name|SnapshotStatus
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
DECL|method|getCreationDate
specifier|public
name|Date
name|getCreationDate
parameter_list|()
block|{
return|return
name|creationDate
return|;
block|}
DECL|method|getReplicaSnapshots
specifier|public
name|List
argument_list|<
name|CoreSnapshotMetaData
argument_list|>
name|getReplicaSnapshots
parameter_list|()
block|{
return|return
name|replicaSnapshots
return|;
block|}
DECL|method|getReplicaSnapshotsForShard
specifier|public
name|List
argument_list|<
name|CoreSnapshotMetaData
argument_list|>
name|getReplicaSnapshotsForShard
parameter_list|(
name|String
name|shardId
parameter_list|)
block|{
name|List
argument_list|<
name|CoreSnapshotMetaData
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreSnapshotMetaData
name|d
range|:
name|replicaSnapshots
control|)
block|{
if|if
condition|(
name|d
operator|.
name|getShardId
argument_list|()
operator|.
name|equals
argument_list|(
name|shardId
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|isSnapshotExists
specifier|public
name|boolean
name|isSnapshotExists
parameter_list|(
name|String
name|shardId
parameter_list|,
name|Replica
name|r
parameter_list|)
block|{
for|for
control|(
name|CoreSnapshotMetaData
name|d
range|:
name|replicaSnapshots
control|)
block|{
if|if
condition|(
name|d
operator|.
name|getShardId
argument_list|()
operator|.
name|equals
argument_list|(
name|shardId
argument_list|)
operator|&&
name|d
operator|.
name|getCoreName
argument_list|()
operator|.
name|equals
argument_list|(
name|r
operator|.
name|getCoreName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|getShards
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getShards
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreSnapshotMetaData
name|d
range|:
name|replicaSnapshots
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|d
operator|.
name|getShardId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|JSONWriter
name|arg0
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|this
operator|.
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
name|SolrSnapshotManager
operator|.
name|SNAPSHOT_STATUS
argument_list|,
name|this
operator|.
name|status
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
name|SolrSnapshotManager
operator|.
name|CREATION_DATE
argument_list|,
name|this
operator|.
name|getCreationDate
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
name|SolrSnapshotManager
operator|.
name|SNAPSHOT_REPLICAS
argument_list|,
name|this
operator|.
name|replicaSnapshots
argument_list|)
expr_stmt|;
name|arg0
operator|.
name|write
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|toNamedList
specifier|public
name|NamedList
name|toNamedList
parameter_list|()
block|{
name|NamedList
name|result
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|this
operator|.
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|SolrSnapshotManager
operator|.
name|SNAPSHOT_STATUS
argument_list|,
name|this
operator|.
name|status
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|SolrSnapshotManager
operator|.
name|CREATION_DATE
argument_list|,
name|this
operator|.
name|getCreationDate
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
name|replicas
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreSnapshotMetaData
name|x
range|:
name|replicaSnapshots
control|)
block|{
name|replicas
operator|.
name|add
argument_list|(
name|x
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|x
operator|.
name|toNamedList
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|SolrSnapshotManager
operator|.
name|SNAPSHOT_REPLICAS
argument_list|,
name|replicas
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit
