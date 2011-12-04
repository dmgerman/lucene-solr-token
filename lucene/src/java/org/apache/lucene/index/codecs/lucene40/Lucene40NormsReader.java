begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|lucene40
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IdentityHashMap
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
name|FieldInfo
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
name|FieldInfos
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
name|IndexFileNames
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
name|SegmentInfo
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
name|codecs
operator|.
name|NormsReader
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
name|StringHelper
import|;
end_import

begin_class
DECL|class|Lucene40NormsReader
specifier|public
class|class
name|Lucene40NormsReader
extends|extends
name|NormsReader
block|{
comment|// this would be replaced by Source/SourceCache in a dv impl.
comment|// for now we have our own mini-version
DECL|field|norms
name|Map
argument_list|<
name|String
argument_list|,
name|Norm
argument_list|>
name|norms
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Norm
argument_list|>
argument_list|()
decl_stmt|;
comment|// any .nrm or .sNN files we have open at any time.
comment|// TODO: just a list, and double-close() separate norms files?
DECL|field|openFiles
name|Map
argument_list|<
name|IndexInput
argument_list|,
name|Boolean
argument_list|>
name|openFiles
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|IndexInput
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
comment|// points to a singleNormFile
DECL|field|singleNormStream
name|IndexInput
name|singleNormStream
decl_stmt|;
DECL|field|maxdoc
specifier|final
name|int
name|maxdoc
decl_stmt|;
comment|// note: just like segmentreader in 3.x, we open up all the files here (including separate norms) up front.
comment|// but we just don't do any seeks or reading yet.
DECL|method|Lucene40NormsReader
specifier|public
name|Lucene40NormsReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|FieldInfos
name|fields
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|Directory
name|separateNormsDir
parameter_list|)
throws|throws
name|IOException
block|{
name|maxdoc
operator|=
name|info
operator|.
name|docCount
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|long
name|nextNormSeek
init|=
name|Lucene40NormsWriter
operator|.
name|NORMS_HEADER
operator|.
name|length
decl_stmt|;
comment|//skip header (header unused for now)
for|for
control|(
name|FieldInfo
name|fi
range|:
name|fields
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
operator|!
name|fi
operator|.
name|omitNorms
condition|)
block|{
name|String
name|fileName
init|=
name|info
operator|.
name|getNormFileName
argument_list|(
name|fi
operator|.
name|number
argument_list|)
decl_stmt|;
name|Directory
name|d
init|=
name|info
operator|.
name|hasSeparateNorms
argument_list|(
name|fi
operator|.
name|number
argument_list|)
condition|?
name|separateNormsDir
else|:
name|dir
decl_stmt|;
comment|// singleNormFile means multiple norms share this file
name|boolean
name|singleNormFile
init|=
name|IndexFileNames
operator|.
name|matchesExtension
argument_list|(
name|fileName
argument_list|,
name|IndexFileNames
operator|.
name|NORMS_EXTENSION
argument_list|)
decl_stmt|;
name|IndexInput
name|normInput
init|=
literal|null
decl_stmt|;
name|long
name|normSeek
decl_stmt|;
if|if
condition|(
name|singleNormFile
condition|)
block|{
name|normSeek
operator|=
name|nextNormSeek
expr_stmt|;
if|if
condition|(
name|singleNormStream
operator|==
literal|null
condition|)
block|{
name|singleNormStream
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|put
argument_list|(
name|singleNormStream
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
comment|// All norms in the .nrm file can share a single IndexInput since
comment|// they are only used in a synchronized context.
comment|// If this were to change in the future, a clone could be done here.
name|normInput
operator|=
name|singleNormStream
expr_stmt|;
block|}
else|else
block|{
name|normInput
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|put
argument_list|(
name|normInput
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
comment|// if the segment was created in 3.2 or after, we wrote the header for sure,
comment|// and don't need to do the sketchy file size check. otherwise, we check
comment|// if the size is exactly equal to maxDoc to detect a headerless file.
comment|// NOTE: remove this check in Lucene 5.0!
name|String
name|version
init|=
name|info
operator|.
name|getVersion
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|isUnversioned
init|=
operator|(
name|version
operator|==
literal|null
operator|||
name|StringHelper
operator|.
name|getVersionComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|version
argument_list|,
literal|"3.2"
argument_list|)
operator|<
literal|0
operator|)
operator|&&
name|normInput
operator|.
name|length
argument_list|()
operator|==
name|maxdoc
decl_stmt|;
if|if
condition|(
name|isUnversioned
condition|)
block|{
name|normSeek
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|normSeek
operator|=
name|Lucene40NormsWriter
operator|.
name|NORMS_HEADER
operator|.
name|length
expr_stmt|;
block|}
block|}
name|Norm
name|norm
init|=
operator|new
name|Norm
argument_list|()
decl_stmt|;
name|norm
operator|.
name|file
operator|=
name|normInput
expr_stmt|;
name|norm
operator|.
name|offset
operator|=
name|normSeek
expr_stmt|;
name|norms
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|norm
argument_list|)
expr_stmt|;
name|nextNormSeek
operator|+=
name|maxdoc
expr_stmt|;
comment|// increment also if some norms are separate
block|}
block|}
comment|// nocommit: change to a real check? see LUCENE-3619
assert|assert
name|singleNormStream
operator|==
literal|null
operator|||
name|nextNormSeek
operator|==
name|singleNormStream
operator|.
name|length
argument_list|()
assert|;
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
if|if
condition|(
name|openFiles
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|openFiles
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|norms
specifier|public
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|Norm
name|norm
init|=
name|norms
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|norm
operator|==
literal|null
condition|?
literal|null
else|:
name|norm
operator|.
name|bytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|openFiles
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|openFiles
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|norms
operator|=
literal|null
expr_stmt|;
name|openFiles
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|class|Norm
class|class
name|Norm
block|{
DECL|field|file
name|IndexInput
name|file
decl_stmt|;
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|bytes
name|byte
name|bytes
index|[]
decl_stmt|;
DECL|method|bytes
specifier|synchronized
name|byte
index|[]
name|bytes
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
name|maxdoc
index|]
expr_stmt|;
comment|// some norms share fds
synchronized|synchronized
init|(
name|file
init|)
block|{
name|file
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|file
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// we are done with this file
if|if
condition|(
name|file
operator|!=
name|singleNormStream
condition|)
block|{
name|openFiles
operator|.
name|remove
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|file
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|bytes
return|;
block|}
block|}
block|}
end_class

end_unit

