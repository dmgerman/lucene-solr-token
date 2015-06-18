begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.store.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|hdfs
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|BlockLocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|SolrInfoMBean
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

begin_class
DECL|class|HdfsLocalityReporter
specifier|public
class|class
name|HdfsLocalityReporter
implements|implements
name|SolrInfoMBean
block|{
DECL|field|LOCALITY_BYTES_TOTAL
specifier|public
specifier|static
specifier|final
name|String
name|LOCALITY_BYTES_TOTAL
init|=
literal|"locality.bytes.total"
decl_stmt|;
DECL|field|LOCALITY_BYTES_LOCAL
specifier|public
specifier|static
specifier|final
name|String
name|LOCALITY_BYTES_LOCAL
init|=
literal|"locality.bytes.local"
decl_stmt|;
DECL|field|LOCALITY_BYTES_RATIO
specifier|public
specifier|static
specifier|final
name|String
name|LOCALITY_BYTES_RATIO
init|=
literal|"locality.bytes.ratio"
decl_stmt|;
DECL|field|LOCALITY_BLOCKS_TOTAL
specifier|public
specifier|static
specifier|final
name|String
name|LOCALITY_BLOCKS_TOTAL
init|=
literal|"locality.blocks.total"
decl_stmt|;
DECL|field|LOCALITY_BLOCKS_LOCAL
specifier|public
specifier|static
specifier|final
name|String
name|LOCALITY_BLOCKS_LOCAL
init|=
literal|"locality.blocks.local"
decl_stmt|;
DECL|field|LOCALITY_BLOCKS_RATIO
specifier|public
specifier|static
specifier|final
name|String
name|LOCALITY_BLOCKS_RATIO
init|=
literal|"locality.blocks.ratio"
decl_stmt|;
DECL|field|logger
specifier|public
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HdfsLocalityReporter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|hostname
specifier|private
name|String
name|hostname
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|HdfsDirectory
argument_list|,
name|ConcurrentMap
argument_list|<
name|FileStatus
argument_list|,
name|BlockLocation
index|[]
argument_list|>
argument_list|>
name|cache
decl_stmt|;
DECL|method|HdfsLocalityReporter
specifier|public
name|HdfsLocalityReporter
parameter_list|()
block|{
name|cache
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the host name to use when determining locality    * @param hostname The name of this host; should correspond to what HDFS Data Nodes think this is.    */
DECL|method|setHost
specifier|public
name|void
name|setHost
parameter_list|(
name|String
name|hostname
parameter_list|)
block|{
name|this
operator|.
name|hostname
operator|=
name|hostname
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"hdfs-locality"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getSpecificationVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Provides metrics for HDFS data locality."
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|OTHER
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Provide statistics on HDFS block locality, both in terms of bytes and block counts.    */
annotation|@
name|Override
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
name|long
name|totalBytes
init|=
literal|0
decl_stmt|;
name|long
name|localBytes
init|=
literal|0
decl_stmt|;
name|int
name|totalCount
init|=
literal|0
decl_stmt|;
name|int
name|localCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|HdfsDirectory
argument_list|>
name|iterator
init|=
name|cache
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|HdfsDirectory
name|hdfsDirectory
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|hdfsDirectory
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|refreshDirectory
argument_list|(
name|hdfsDirectory
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|FileStatus
argument_list|,
name|BlockLocation
index|[]
argument_list|>
name|blockMap
init|=
name|cache
operator|.
name|get
argument_list|(
name|hdfsDirectory
argument_list|)
decl_stmt|;
comment|// For every block in every file in this directory, count it
for|for
control|(
name|BlockLocation
index|[]
name|locations
range|:
name|blockMap
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|BlockLocation
name|bl
range|:
name|locations
control|)
block|{
name|totalBytes
operator|+=
name|bl
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|totalCount
operator|++
expr_stmt|;
if|if
condition|(
name|Arrays
operator|.
name|asList
argument_list|(
name|bl
operator|.
name|getHosts
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|hostname
argument_list|)
condition|)
block|{
name|localBytes
operator|+=
name|bl
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|localCount
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Could not retrieve locality information for {} due to exception: {}"
argument_list|,
name|hdfsDirectory
operator|.
name|getHdfsDirPath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|createStatistics
argument_list|(
name|totalBytes
argument_list|,
name|localBytes
argument_list|,
name|totalCount
argument_list|,
name|localCount
argument_list|)
return|;
block|}
comment|/**    * Generate a statistics object based on the given measurements for all files monitored by this reporter.    *     * @param totalBytes    *          The total bytes used    * @param localBytes    *          The amount of bytes found on local nodes    * @param totalCount    *          The total block count    * @param localCount    *          The amount of blocks found on local nodes    * @return HDFS block locality statistics    */
DECL|method|createStatistics
specifier|private
name|NamedList
argument_list|<
name|Number
argument_list|>
name|createStatistics
parameter_list|(
name|long
name|totalBytes
parameter_list|,
name|long
name|localBytes
parameter_list|,
name|int
name|totalCount
parameter_list|,
name|int
name|localCount
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Number
argument_list|>
name|statistics
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Number
argument_list|>
argument_list|()
decl_stmt|;
name|statistics
operator|.
name|add
argument_list|(
name|LOCALITY_BYTES_TOTAL
argument_list|,
name|totalBytes
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|add
argument_list|(
name|LOCALITY_BYTES_LOCAL
argument_list|,
name|localBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|localBytes
operator|==
literal|0
condition|)
block|{
name|statistics
operator|.
name|add
argument_list|(
name|LOCALITY_BYTES_RATIO
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statistics
operator|.
name|add
argument_list|(
name|LOCALITY_BYTES_RATIO
argument_list|,
name|localBytes
operator|/
operator|(
name|double
operator|)
name|totalBytes
argument_list|)
expr_stmt|;
block|}
name|statistics
operator|.
name|add
argument_list|(
name|LOCALITY_BLOCKS_TOTAL
argument_list|,
name|totalCount
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|add
argument_list|(
name|LOCALITY_BLOCKS_LOCAL
argument_list|,
name|localCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|localCount
operator|==
literal|0
condition|)
block|{
name|statistics
operator|.
name|add
argument_list|(
name|LOCALITY_BLOCKS_RATIO
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statistics
operator|.
name|add
argument_list|(
name|LOCALITY_BLOCKS_RATIO
argument_list|,
name|localCount
operator|/
operator|(
name|double
operator|)
name|totalCount
argument_list|)
expr_stmt|;
block|}
return|return
name|statistics
return|;
block|}
comment|/**    * Add a directory for block locality reporting. This directory will continue to be checked until its close method has    * been called.    *     * @param dir    *          The directory to keep metrics on.    */
DECL|method|registerDirectory
specifier|public
name|void
name|registerDirectory
parameter_list|(
name|HdfsDirectory
name|dir
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Registering direcotry {} for locality metrics."
argument_list|,
name|dir
operator|.
name|getHdfsDirPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|dir
argument_list|,
operator|new
name|ConcurrentHashMap
argument_list|<
name|FileStatus
argument_list|,
name|BlockLocation
index|[]
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update the cached block locations for the given directory. This includes deleting any files that no longer exist in    * the file system and adding any new files that have shown up.    *     * @param dir    *          The directory to refresh    * @throws IOException    *           If there is a problem getting info from HDFS    */
DECL|method|refreshDirectory
specifier|private
name|void
name|refreshDirectory
parameter_list|(
name|HdfsDirectory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|FileStatus
argument_list|,
name|BlockLocation
index|[]
argument_list|>
name|directoryCache
init|=
name|cache
operator|.
name|get
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|FileStatus
argument_list|>
name|cachedStatuses
init|=
name|directoryCache
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|dir
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FileStatus
index|[]
name|statuses
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
operator|.
name|getHdfsDirPath
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FileStatus
argument_list|>
name|statusList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|statuses
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Updating locality information for: {}"
argument_list|,
name|statusList
argument_list|)
expr_stmt|;
comment|// Keep only the files that still exist
name|cachedStatuses
operator|.
name|retainAll
argument_list|(
name|statusList
argument_list|)
expr_stmt|;
comment|// Fill in missing entries in the cache
for|for
control|(
name|FileStatus
name|status
range|:
name|statusList
control|)
block|{
if|if
condition|(
operator|!
name|status
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|directoryCache
operator|.
name|containsKey
argument_list|(
name|status
argument_list|)
condition|)
block|{
name|BlockLocation
index|[]
name|locations
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|status
argument_list|,
literal|0
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
decl_stmt|;
name|directoryCache
operator|.
name|put
argument_list|(
name|status
argument_list|,
name|locations
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
