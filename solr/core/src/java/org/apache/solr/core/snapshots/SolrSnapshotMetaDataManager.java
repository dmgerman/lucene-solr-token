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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

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
name|HashMap
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|CodecUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DirectoryReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexCommit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexDeletionPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IOContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|SolrException
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
name|SolrException
operator|.
name|ErrorCode
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
name|core
operator|.
name|DirectoryFactory
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
name|core
operator|.
name|DirectoryFactory
operator|.
name|DirContext
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
name|core
operator|.
name|IndexDeletionPolicyWrapper
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
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This class is responsible to manage the persistent snapshots meta-data for the Solr indexes. The  * persistent snapshots are implemented by relying on Lucene {@linkplain IndexDeletionPolicy}  * abstraction to configure a specific {@linkplain IndexCommit} to be retained. The  * {@linkplain IndexDeletionPolicyWrapper} in Solr uses this class to create/delete the Solr index  * snapshots.  */
end_comment

begin_class
DECL|class|SolrSnapshotMetaDataManager
specifier|public
class|class
name|SolrSnapshotMetaDataManager
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|SNAPSHOT_METADATA_DIR
specifier|public
specifier|static
specifier|final
name|String
name|SNAPSHOT_METADATA_DIR
init|=
literal|"snapshot_metadata"
decl_stmt|;
comment|/**    * A class defining the meta-data for a specific snapshot.    */
DECL|class|SnapshotMetaData
specifier|public
specifier|static
class|class
name|SnapshotMetaData
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|indexDirPath
specifier|private
name|String
name|indexDirPath
decl_stmt|;
DECL|field|generationNumber
specifier|private
name|long
name|generationNumber
decl_stmt|;
DECL|method|SnapshotMetaData
specifier|public
name|SnapshotMetaData
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexDirPath
parameter_list|,
name|long
name|generationNumber
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
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
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"SnapshotMetaData[name="
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|", indexDirPath="
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|indexDirPath
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|", generation="
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|generationNumber
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** Prefix used for the save file. */
DECL|field|SNAPSHOTS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|SNAPSHOTS_PREFIX
init|=
literal|"snapshots_"
decl_stmt|;
DECL|field|VERSION_START
specifier|private
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|private
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|CODEC_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"solr-snapshots"
decl_stmt|;
comment|// The index writer which maintains the snapshots metadata
DECL|field|nextWriteGen
specifier|private
name|long
name|nextWriteGen
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
comment|/** Used to map snapshot name to snapshot meta-data. */
DECL|field|nameToDetailsMapping
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SnapshotMetaData
argument_list|>
name|nameToDetailsMapping
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Used to figure out the *current* index data directory path */
DECL|field|solrCore
specifier|private
specifier|final
name|SolrCore
name|solrCore
decl_stmt|;
comment|/**    * A constructor.    *    * @param dir The directory where the snapshot meta-data should be stored. Enables updating    *            the existing meta-data.    * @throws IOException in case of errors.    */
DECL|method|SolrSnapshotMetaDataManager
specifier|public
name|SolrSnapshotMetaDataManager
parameter_list|(
name|SolrCore
name|solrCore
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|solrCore
argument_list|,
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|)
expr_stmt|;
block|}
comment|/**    * A constructor.    *    * @param dir The directory where the snapshot meta-data is stored.    * @param mode CREATE If previous meta-data should be erased.    *             APPEND If previous meta-data should be read and updated.    *             CREATE_OR_APPEND Creates a new meta-data structure if one does not exist    *                              Updates the existing structure if one exists.    * @throws IOException in case of errors.    */
DECL|method|SolrSnapshotMetaDataManager
specifier|public
name|SolrSnapshotMetaDataManager
parameter_list|(
name|SolrCore
name|solrCore
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|OpenMode
name|mode
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|solrCore
operator|=
name|solrCore
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|OpenMode
operator|.
name|CREATE
condition|)
block|{
name|deleteSnapshotMetadataFiles
argument_list|()
expr_stmt|;
block|}
name|loadFromSnapshotMetadataFile
argument_list|()
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|OpenMode
operator|.
name|APPEND
operator|&&
name|nextWriteGen
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"no snapshots stored in this directory"
argument_list|)
throw|;
block|}
block|}
comment|/**    * @return The snapshot meta-data directory    */
DECL|method|getSnapshotsDir
specifier|public
name|Directory
name|getSnapshotsDir
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
comment|/**    * This method creates a new snapshot meta-data entry.    *    * @param name The name of the snapshot.    * @param indexDirPath The directory path where the index files are stored.    * @param gen The generation number for the {@linkplain IndexCommit} being snapshotted.    * @throws IOException in case of I/O errors.    */
DECL|method|snapshot
specifier|public
specifier|synchronized
name|void
name|snapshot
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexDirPath
parameter_list|,
name|long
name|gen
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Creating the snapshot named {} for core {} associated with index commit with generation {} in directory {}"
argument_list|,
name|name
argument_list|,
name|solrCore
operator|.
name|getName
argument_list|()
argument_list|,
name|gen
argument_list|,
name|indexDirPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|nameToDetailsMapping
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"A snapshot with name "
operator|+
name|name
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
name|SnapshotMetaData
name|d
init|=
operator|new
name|SnapshotMetaData
argument_list|(
name|name
argument_list|,
name|indexDirPath
argument_list|,
name|gen
argument_list|)
decl_stmt|;
name|nameToDetailsMapping
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|persist
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|release
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Suppress so we keep throwing original exception
block|}
block|}
block|}
block|}
comment|/**    * This method deletes a previously created snapshot (if any).    *    * @param name The name of the snapshot to be deleted.    * @return The snapshot meta-data if the snapshot with the snapshot name exists.    * @throws IOException in case of I/O error    */
DECL|method|release
specifier|public
specifier|synchronized
name|Optional
argument_list|<
name|SnapshotMetaData
argument_list|>
name|release
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleting the snapshot named {} for core {}"
argument_list|,
name|name
argument_list|,
name|solrCore
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|SnapshotMetaData
name|result
init|=
name|nameToDetailsMapping
operator|.
name|remove
argument_list|(
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|persist
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|nameToDetailsMapping
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/**    * This method returns if snapshot is created for the specified generation number in    * the *current* index directory.    *    * @param genNumber The generation number for the {@linkplain IndexCommit} to be checked.    * @return true if the snapshot is created.    *         false otherwise.    */
DECL|method|isSnapshotted
specifier|public
specifier|synchronized
name|boolean
name|isSnapshotted
parameter_list|(
name|long
name|genNumber
parameter_list|)
block|{
return|return
operator|!
name|nameToDetailsMapping
operator|.
name|isEmpty
argument_list|()
operator|&&
name|isSnapshotted
argument_list|(
name|solrCore
operator|.
name|getIndexDir
argument_list|()
argument_list|,
name|genNumber
argument_list|)
return|;
block|}
comment|/**    * This method returns if snapshot is created for the specified generation number in    * the specified index directory.    *    * @param genNumber The generation number for the {@linkplain IndexCommit} to be checked.    * @return true if the snapshot is created.    *         false otherwise.    */
DECL|method|isSnapshotted
specifier|public
specifier|synchronized
name|boolean
name|isSnapshotted
parameter_list|(
name|String
name|indexDirPath
parameter_list|,
name|long
name|genNumber
parameter_list|)
block|{
return|return
operator|!
name|nameToDetailsMapping
operator|.
name|isEmpty
argument_list|()
operator|&&
name|nameToDetailsMapping
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|entry
lambda|->
name|entry
operator|.
name|getIndexDirPath
argument_list|()
operator|.
name|equals
argument_list|(
name|indexDirPath
argument_list|)
operator|&&
name|entry
operator|.
name|getGenerationNumber
argument_list|()
operator|==
name|genNumber
argument_list|)
return|;
block|}
comment|/**    * This method returns the snapshot meta-data for the specified name (if it exists).    *    * @param name The name of the snapshot    * @return The snapshot meta-data if exists.    */
DECL|method|getSnapshotMetaData
specifier|public
specifier|synchronized
name|Optional
argument_list|<
name|SnapshotMetaData
argument_list|>
name|getSnapshotMetaData
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|nameToDetailsMapping
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @return A list of snapshots created so far.    */
DECL|method|listSnapshots
specifier|public
specifier|synchronized
name|List
argument_list|<
name|String
argument_list|>
name|listSnapshots
parameter_list|()
block|{
comment|// We create a copy for thread safety.
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nameToDetailsMapping
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * This method returns a list of snapshots created in a specified index directory.    *    * @param indexDirPath The index directory path.    * @return a list snapshots stored in the specified directory.    */
DECL|method|listSnapshotsInIndexDir
specifier|public
specifier|synchronized
name|Collection
argument_list|<
name|SnapshotMetaData
argument_list|>
name|listSnapshotsInIndexDir
parameter_list|(
name|String
name|indexDirPath
parameter_list|)
block|{
return|return
name|nameToDetailsMapping
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|entry
lambda|->
name|indexDirPath
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getIndexDirPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * This method returns the {@linkplain IndexCommit} associated with the specified    *<code>commitName</code>. A snapshot with specified<code>commitName</code> must    * be created before invoking this method.    *    * @param commitName The name of persisted commit    * @return the {@linkplain IndexCommit}    * @throws IOException in case of I/O error.    */
DECL|method|getIndexCommitByName
specifier|public
name|Optional
argument_list|<
name|IndexCommit
argument_list|>
name|getIndexCommitByName
parameter_list|(
name|String
name|commitName
parameter_list|)
throws|throws
name|IOException
block|{
name|Optional
argument_list|<
name|IndexCommit
argument_list|>
name|result
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
name|Optional
argument_list|<
name|SnapshotMetaData
argument_list|>
name|metaData
init|=
name|getSnapshotMetaData
argument_list|(
name|commitName
argument_list|)
decl_stmt|;
if|if
condition|(
name|metaData
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|String
name|indexDirPath
init|=
name|metaData
operator|.
name|get
argument_list|()
operator|.
name|getIndexDirPath
argument_list|()
decl_stmt|;
name|long
name|gen
init|=
name|metaData
operator|.
name|get
argument_list|()
operator|.
name|getGenerationNumber
argument_list|()
decl_stmt|;
name|Directory
name|d
init|=
name|solrCore
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|indexDirPath
argument_list|,
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|DirectoryFactory
operator|.
name|LOCK_TYPE_NONE
argument_list|)
decl_stmt|;
try|try
block|{
name|result
operator|=
name|DirectoryReader
operator|.
name|listCommits
argument_list|(
name|d
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|ic
lambda|->
name|ic
operator|.
name|getGeneration
argument_list|()
operator|==
name|gen
argument_list|)
operator|.
name|findAny
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to find commit with generation {} in the directory {}"
argument_list|,
name|gen
argument_list|,
name|indexDirPath
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|solrCore
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Commit with name {} is not persisted for core {}"
argument_list|,
name|commitName
argument_list|,
name|solrCore
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|persist
specifier|private
specifier|synchronized
name|void
name|persist
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|fileName
init|=
name|SNAPSHOTS_PREFIX
operator|+
name|nextWriteGen
decl_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|nameToDetailsMapping
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|SnapshotMetaData
argument_list|>
name|ent
range|:
name|nameToDetailsMapping
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getIndexDirPath
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getGenerationNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
name|dir
operator|.
name|sync
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextWriteGen
operator|>
literal|0
condition|)
block|{
name|String
name|lastSaveFile
init|=
name|SNAPSHOTS_PREFIX
operator|+
operator|(
name|nextWriteGen
operator|-
literal|1
operator|)
decl_stmt|;
comment|// exception OK: likely it didn't exist
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|dir
argument_list|,
name|lastSaveFile
argument_list|)
expr_stmt|;
block|}
name|nextWriteGen
operator|++
expr_stmt|;
block|}
DECL|method|deleteSnapshotMetadataFiles
specifier|private
specifier|synchronized
name|void
name|deleteSnapshotMetadataFiles
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|file
operator|.
name|startsWith
argument_list|(
name|SNAPSHOTS_PREFIX
argument_list|)
condition|)
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Reads the snapshot meta-data information from the given {@link Directory}.    */
DECL|method|loadFromSnapshotMetadataFile
specifier|private
specifier|synchronized
name|void
name|loadFromSnapshotMetadataFile
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Loading from snapshot metadata file..."
argument_list|)
expr_stmt|;
name|long
name|genLoaded
init|=
operator|-
literal|1
decl_stmt|;
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|snapshotFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|file
operator|.
name|startsWith
argument_list|(
name|SNAPSHOTS_PREFIX
argument_list|)
condition|)
block|{
name|long
name|gen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|file
operator|.
name|substring
argument_list|(
name|SNAPSHOTS_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|genLoaded
operator|==
operator|-
literal|1
operator|||
name|gen
operator|>
name|genLoaded
condition|)
block|{
name|snapshotFiles
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SnapshotMetaData
argument_list|>
name|snapshotMetaDataMapping
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|file
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|indexDirPath
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|long
name|commitGen
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|snapshotMetaDataMapping
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|SnapshotMetaData
argument_list|(
name|name
argument_list|,
name|indexDirPath
argument_list|,
name|commitGen
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe2
parameter_list|)
block|{
comment|// Save first exception& throw in the end
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
block|{
name|ioe
operator|=
name|ioe2
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|genLoaded
operator|=
name|gen
expr_stmt|;
name|nameToDetailsMapping
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nameToDetailsMapping
operator|.
name|putAll
argument_list|(
name|snapshotMetaDataMapping
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|genLoaded
operator|==
operator|-
literal|1
condition|)
block|{
comment|// Nothing was loaded...
if|if
condition|(
name|ioe
operator|!=
literal|null
condition|)
block|{
comment|// ... not for lack of trying:
throw|throw
name|ioe
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|snapshotFiles
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// Remove any broken / old snapshot files:
name|String
name|curFileName
init|=
name|SNAPSHOTS_PREFIX
operator|+
name|genLoaded
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|snapshotFiles
control|)
block|{
if|if
condition|(
operator|!
name|curFileName
operator|.
name|equals
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|dir
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|nextWriteGen
operator|=
literal|1
operator|+
name|genLoaded
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
