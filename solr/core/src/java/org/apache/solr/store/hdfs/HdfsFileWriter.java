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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|conf
operator|.
name|Configuration
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
name|CreateFlag
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
name|hadoop
operator|.
name|fs
operator|.
name|FsServerDefaults
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
name|Path
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
name|permission
operator|.
name|FsPermission
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
name|OutputStreamIndexOutput
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|HdfsFileWriter
specifier|public
class|class
name|HdfsFileWriter
extends|extends
name|OutputStreamIndexOutput
block|{
DECL|field|HDFS_SYNC_BLOCK
specifier|public
specifier|static
specifier|final
name|String
name|HDFS_SYNC_BLOCK
init|=
literal|"solr.hdfs.sync.block"
decl_stmt|;
DECL|field|BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|16384
decl_stmt|;
DECL|method|HdfsFileWriter
specifier|public
name|HdfsFileWriter
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|"fileSystem="
operator|+
name|fileSystem
operator|+
literal|" path="
operator|+
name|path
argument_list|,
name|getOutputStream
argument_list|(
name|fileSystem
argument_list|,
name|path
argument_list|)
argument_list|,
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|getOutputStream
specifier|private
specifier|static
specifier|final
name|OutputStream
name|getOutputStream
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|fileSystem
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|FsServerDefaults
name|fsDefaults
init|=
name|fileSystem
operator|.
name|getServerDefaults
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flags
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|,
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
decl_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|HDFS_SYNC_BLOCK
argument_list|)
condition|)
block|{
name|flags
operator|.
name|add
argument_list|(
name|CreateFlag
operator|.
name|SYNC_BLOCK
argument_list|)
expr_stmt|;
block|}
return|return
name|fileSystem
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
operator|.
name|applyUMask
argument_list|(
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|,
name|flags
argument_list|,
name|fsDefaults
operator|.
name|getFileBufferSize
argument_list|()
argument_list|,
name|fsDefaults
operator|.
name|getReplication
argument_list|()
argument_list|,
name|fsDefaults
operator|.
name|getBlockSize
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

