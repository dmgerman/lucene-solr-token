begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|FSDirectory
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
name|lucene
operator|.
name|util
operator|.
name|SuppressForbidden
import|;
end_import

begin_comment
comment|/**  * Command-line tool that reads from a source index and  * writes to a dest index, correcting any broken offsets  * in the process.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FixBrokenOffsets
specifier|public
class|class
name|FixBrokenOffsets
block|{
DECL|field|infos
specifier|public
name|SegmentInfos
name|infos
decl_stmt|;
DECL|field|fsDir
name|FSDirectory
name|fsDir
decl_stmt|;
DECL|field|dir
name|Path
name|dir
decl_stmt|;
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"System.out required: command line tool"
argument_list|)
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: FixBrokenOffsetse<srcDir><destDir>"
argument_list|)
expr_stmt|;
return|return;
block|}
name|Path
name|srcPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|srcPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"srcPath "
operator|+
name|srcPath
operator|.
name|toAbsolutePath
argument_list|()
operator|+
literal|" doesn't exist"
argument_list|)
throw|;
block|}
name|Path
name|destPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|destPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"destPath "
operator|+
name|destPath
operator|.
name|toAbsolutePath
argument_list|()
operator|+
literal|" already exists; please remove it and re-run"
argument_list|)
throw|;
block|}
name|Directory
name|srcDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|srcDir
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|CodecReader
index|[]
name|filtered
init|=
operator|new
name|CodecReader
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
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
name|leaves
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|filtered
index|[
name|i
index|]
operator|=
name|SlowCodecReaderWrapper
operator|.
name|wrap
argument_list|(
operator|new
name|FilterLeafReader
argument_list|(
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|termVectors
init|=
name|in
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|termVectors
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|FilterFields
argument_list|(
name|termVectors
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FilterTerms
argument_list|(
name|super
operator|.
name|terms
argument_list|(
name|field
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FilterTermsEnum
argument_list|(
name|super
operator|.
name|iterator
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|PostingsEnum
name|postings
parameter_list|(
name|PostingsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FilterPostingsEnum
argument_list|(
name|super
operator|.
name|postings
argument_list|(
name|reuse
argument_list|,
name|flags
argument_list|)
argument_list|)
block|{
name|int
name|nextLastStartOffset
init|=
literal|0
decl_stmt|;
name|int
name|lastStartOffset
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|pos
init|=
name|super
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
name|lastStartOffset
operator|=
name|nextLastStartOffset
expr_stmt|;
name|nextLastStartOffset
operator|=
name|startOffset
argument_list|()
expr_stmt|;
return|return
name|pos
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|offset
init|=
name|super
operator|.
name|startOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|offset
operator|<
name|lastStartOffset
condition|)
block|{
name|offset
operator|=
name|lastStartOffset
expr_stmt|;
block|}
return|return
name|offset
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|offset
init|=
name|super
operator|.
name|endOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|offset
operator|<
name|lastStartOffset
condition|)
block|{
name|offset
operator|=
name|lastStartOffset
expr_stmt|;
block|}
return|return
name|offset
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|CacheHelper
name|getCoreCacheHelper
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|Directory
name|destDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
comment|// We need to maintain the same major version
name|int
name|createdMajor
init|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|srcDir
argument_list|)
operator|.
name|getIndexCreatedVersionMajor
argument_list|()
decl_stmt|;
operator|new
name|SegmentInfos
argument_list|(
name|createdMajor
argument_list|)
operator|.
name|commit
argument_list|(
name|destDir
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|destDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addIndexes
argument_list|(
name|filtered
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|reader
argument_list|,
name|srcDir
argument_list|,
name|destDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

