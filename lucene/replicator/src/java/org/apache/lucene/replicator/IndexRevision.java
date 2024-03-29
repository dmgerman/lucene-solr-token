begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
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
name|io
operator|.
name|InputStream
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
name|IndexWriter
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
name|SnapshotDeletionPolicy
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

begin_comment
comment|/**  * A {@link Revision} of a single index files which comprises the list of files  * that are part of the current {@link IndexCommit}. To ensure the files are not  * deleted by {@link IndexWriter} for as long as this revision stays alive (i.e.  * until {@link #release()}), the current commit point is snapshotted, using  * {@link SnapshotDeletionPolicy} (this means that the given writer's  * {@link IndexWriterConfig#getIndexDeletionPolicy() config} should return  * {@link SnapshotDeletionPolicy}).  *<p>  * When this revision is {@link #release() released}, it releases the obtained  * snapshot as well as calls {@link IndexWriter#deleteUnusedFiles()} so that the  * snapshotted files are deleted (if they are no longer needed).  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IndexRevision
specifier|public
class|class
name|IndexRevision
implements|implements
name|Revision
block|{
DECL|field|RADIX
specifier|private
specifier|static
specifier|final
name|int
name|RADIX
init|=
literal|16
decl_stmt|;
DECL|field|SOURCE
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE
init|=
literal|"index"
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|commit
specifier|private
specifier|final
name|IndexCommit
name|commit
decl_stmt|;
DECL|field|sdp
specifier|private
specifier|final
name|SnapshotDeletionPolicy
name|sdp
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|String
name|version
decl_stmt|;
DECL|field|sourceFiles
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|sourceFiles
decl_stmt|;
comment|// returns a RevisionFile with some metadata
DECL|method|newRevisionFile
specifier|private
specifier|static
name|RevisionFile
name|newRevisionFile
parameter_list|(
name|String
name|file
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|RevisionFile
name|revFile
init|=
operator|new
name|RevisionFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|revFile
operator|.
name|size
operator|=
name|dir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
expr_stmt|;
return|return
name|revFile
return|;
block|}
comment|/** Returns a singleton map of the revision files from the given {@link IndexCommit}. */
DECL|method|revisionFiles
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|revisionFiles
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|commitFiles
init|=
name|commit
operator|.
name|getFileNames
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RevisionFile
argument_list|>
name|revisionFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|commitFiles
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|segmentsFile
init|=
name|commit
operator|.
name|getSegmentsFileName
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|commit
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|commitFiles
control|)
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|equals
argument_list|(
name|segmentsFile
argument_list|)
condition|)
block|{
name|revisionFiles
operator|.
name|add
argument_list|(
name|newRevisionFile
argument_list|(
name|file
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|revisionFiles
operator|.
name|add
argument_list|(
name|newRevisionFile
argument_list|(
name|segmentsFile
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
comment|// segments_N must be last
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|SOURCE
argument_list|,
name|revisionFiles
argument_list|)
return|;
block|}
comment|/**    * Returns a String representation of a revision's version from the given    * {@link IndexCommit}.    */
DECL|method|revisionVersion
specifier|public
specifier|static
name|String
name|revisionVersion
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|commit
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|RADIX
argument_list|)
return|;
block|}
comment|/**    * Constructor over the given {@link IndexWriter}. Uses the last    * {@link IndexCommit} found in the {@link Directory} managed by the given    * writer.    */
DECL|method|IndexRevision
specifier|public
name|IndexRevision
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexDeletionPolicy
name|delPolicy
init|=
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getIndexDeletionPolicy
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|delPolicy
operator|instanceof
name|SnapshotDeletionPolicy
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"IndexWriter must be created with SnapshotDeletionPolicy"
argument_list|)
throw|;
block|}
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|sdp
operator|=
operator|(
name|SnapshotDeletionPolicy
operator|)
name|delPolicy
expr_stmt|;
name|this
operator|.
name|commit
operator|=
name|sdp
operator|.
name|snapshot
argument_list|()
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|revisionVersion
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceFiles
operator|=
name|revisionFiles
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|long
name|gen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|version
argument_list|,
name|RADIX
argument_list|)
decl_stmt|;
name|long
name|commitGen
init|=
name|commit
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
return|return
name|commitGen
operator|<
name|gen
condition|?
operator|-
literal|1
else|:
operator|(
name|commitGen
operator|>
name|gen
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Revision
name|o
parameter_list|)
block|{
name|IndexRevision
name|other
init|=
operator|(
name|IndexRevision
operator|)
name|o
decl_stmt|;
return|return
name|commit
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|commit
argument_list|)
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
name|version
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceFiles
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|getSourceFiles
parameter_list|()
block|{
return|return
name|sourceFiles
return|;
block|}
annotation|@
name|Override
DECL|method|open
specifier|public
name|InputStream
name|open
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|source
operator|.
name|equals
argument_list|(
name|SOURCE
argument_list|)
operator|:
literal|"invalid source; expected="
operator|+
name|SOURCE
operator|+
literal|" got="
operator|+
name|source
assert|;
return|return
operator|new
name|IndexInputInputStream
argument_list|(
name|commit
operator|.
name|getDirectory
argument_list|()
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
throws|throws
name|IOException
block|{
name|sdp
operator|.
name|release
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteUnusedFiles
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"IndexRevision version="
operator|+
name|version
operator|+
literal|" files="
operator|+
name|sourceFiles
return|;
block|}
block|}
end_class

end_unit

