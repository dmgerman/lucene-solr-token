begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene3x
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene3x
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
name|Arrays
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
name|DocValuesConsumer
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
name|PerDocConsumer
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
name|DocValues
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
name|DocValues
operator|.
name|Source
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
name|DocValues
operator|.
name|Type
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
name|IndexableField
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
name|MergeState
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
name|ArrayUtil
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
name|Bits
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

begin_comment
comment|/**  * Writes and Merges Lucene 3.x norms format  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PreFlexRWNormsConsumer
class|class
name|PreFlexRWNormsConsumer
extends|extends
name|PerDocConsumer
block|{
comment|/** norms header placeholder */
DECL|field|NORMS_HEADER
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|NORMS_HEADER
init|=
operator|new
name|byte
index|[]
block|{
literal|'N'
block|,
literal|'R'
block|,
literal|'M'
block|,
operator|-
literal|1
block|}
decl_stmt|;
comment|/** Extension of norms file */
DECL|field|NORMS_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|NORMS_EXTENSION
init|=
literal|"nrm"
decl_stmt|;
comment|/** Extension of separate norms file    * @deprecated */
annotation|@
name|Deprecated
DECL|field|SEPARATE_NORMS_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|SEPARATE_NORMS_EXTENSION
init|=
literal|"s"
decl_stmt|;
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|segment
specifier|private
specifier|final
name|String
name|segment
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|field|writer
specifier|private
name|NormsWriter
name|writer
decl_stmt|;
DECL|method|PreFlexRWNormsConsumer
specifier|public
name|PreFlexRWNormsConsumer
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|IOContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segment
operator|=
name|segment
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
name|getNormsWriter
argument_list|()
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|canMerge
specifier|protected
name|boolean
name|canMerge
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|hasNorms
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesType
specifier|protected
name|Type
name|getDocValuesType
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|getNormType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addValuesField
specifier|public
name|DocValuesConsumer
name|addValuesField
parameter_list|(
name|Type
name|type
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|type
operator|!=
name|Type
operator|.
name|FIXED_INTS_8
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Codec only supports single byte norm values. Type give: "
operator|+
name|type
argument_list|)
throw|;
block|}
return|return
operator|new
name|Lucene3xNormsDocValuesConsumer
argument_list|(
name|fieldInfo
argument_list|)
return|;
block|}
DECL|class|Lucene3xNormsDocValuesConsumer
class|class
name|Lucene3xNormsDocValuesConsumer
extends|extends
name|DocValuesConsumer
block|{
comment|// Holds all docID/norm pairs we've seen
DECL|field|docIDs
specifier|private
name|int
index|[]
name|docIDs
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
DECL|field|norms
specifier|private
name|byte
index|[]
name|norms
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
DECL|field|upto
specifier|private
name|int
name|upto
decl_stmt|;
DECL|field|fi
specifier|private
specifier|final
name|FieldInfo
name|fi
decl_stmt|;
DECL|method|Lucene3xNormsDocValuesConsumer
name|Lucene3xNormsDocValuesConsumer
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|fi
operator|=
name|fieldInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NormsWriter
name|normsWriter
init|=
name|getNormsWriter
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|int
name|uptoDoc
init|=
literal|0
decl_stmt|;
name|normsWriter
operator|.
name|setNumTotalDocs
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|upto
operator|>
literal|0
condition|)
block|{
name|normsWriter
operator|.
name|startField
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|int
name|docID
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|docID
operator|<
name|docCount
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|uptoDoc
operator|<
name|upto
operator|&&
name|docIDs
index|[
name|uptoDoc
index|]
operator|==
name|docID
condition|)
block|{
name|normsWriter
operator|.
name|writeNorm
argument_list|(
name|norms
index|[
name|uptoDoc
index|]
argument_list|)
expr_stmt|;
name|uptoDoc
operator|++
expr_stmt|;
block|}
else|else
block|{
name|normsWriter
operator|.
name|writeNorm
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|// we should have consumed every norm
assert|assert
name|uptoDoc
operator|==
name|upto
assert|;
block|}
else|else
block|{
comment|// Fill entire field with default norm:
name|normsWriter
operator|.
name|startField
argument_list|(
name|fi
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|upto
operator|<
name|docCount
condition|;
name|upto
operator|++
control|)
name|normsWriter
operator|.
name|writeNorm
argument_list|(
operator|(
name|byte
operator|)
literal|0
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
name|normsWriter
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|IndexableField
name|docValue
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
name|docID
argument_list|,
name|docValue
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|docIDs
operator|.
name|length
operator|<=
name|upto
condition|)
block|{
assert|assert
name|docIDs
operator|.
name|length
operator|==
name|upto
assert|;
name|docIDs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|docIDs
argument_list|,
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|norms
operator|.
name|length
operator|<=
name|upto
condition|)
block|{
assert|assert
name|norms
operator|.
name|length
operator|==
name|upto
assert|;
name|norms
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|norms
argument_list|,
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
block|}
name|norms
index|[
name|upto
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
name|docIDs
index|[
name|upto
index|]
operator|=
name|docID
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|protected
name|Type
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|FIXED_INTS_8
return|;
block|}
block|}
DECL|method|getNormsWriter
specifier|public
name|NormsWriter
name|getNormsWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
operator|new
name|NormsWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|writer
return|;
block|}
DECL|class|NormsWriter
specifier|private
specifier|static
class|class
name|NormsWriter
block|{
DECL|field|output
specifier|private
specifier|final
name|IndexOutput
name|output
decl_stmt|;
DECL|field|normCount
specifier|private
name|int
name|normCount
init|=
literal|0
decl_stmt|;
DECL|field|numTotalDocs
specifier|private
name|int
name|numTotalDocs
init|=
literal|0
decl_stmt|;
DECL|method|NormsWriter
specifier|public
name|NormsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|normsFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|NORMS_EXTENSION
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexOutput
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|normsFileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|output
operator|=
name|out
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|NORMS_HEADER
argument_list|,
literal|0
argument_list|,
name|NORMS_HEADER
operator|.
name|length
argument_list|)
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setNumTotalDocs
specifier|public
name|void
name|setNumTotalDocs
parameter_list|(
name|int
name|numTotalDocs
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|numTotalDocs
operator|==
literal|0
operator|||
name|numTotalDocs
operator|==
name|this
operator|.
name|numTotalDocs
assert|;
name|this
operator|.
name|numTotalDocs
operator|=
name|numTotalDocs
expr_stmt|;
block|}
DECL|method|startField
specifier|public
name|void
name|startField
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|info
operator|.
name|omitNorms
operator|==
literal|false
assert|;
name|normCount
operator|++
expr_stmt|;
block|}
DECL|method|writeNorm
specifier|public
name|void
name|writeNorm
parameter_list|(
name|byte
name|norm
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeByte
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
literal|4
operator|+
name|normCount
operator|*
operator|(
name|long
operator|)
name|numTotalDocs
operator|!=
name|output
operator|.
name|getFilePointer
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|".nrm file size mismatch: expected="
operator|+
operator|(
literal|4
operator|+
name|normCount
operator|*
operator|(
name|long
operator|)
name|numTotalDocs
operator|)
operator|+
literal|" actual="
operator|+
name|output
operator|.
name|getFilePointer
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// TODO: we can actually use the defaul DV merge here and drop this specific stuff entirely
comment|/** we override merge and bulk-merge norms when there are no deletions */
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numMergedDocs
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|mergeState
operator|.
name|fieldInfos
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
name|startField
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|int
name|numMergedDocsForField
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|byte
index|[]
name|normBuffer
decl_stmt|;
name|DocValues
name|normValues
init|=
name|reader
operator|.
name|reader
operator|.
name|normValues
argument_list|(
name|fi
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|normValues
operator|==
literal|null
condition|)
block|{
comment|// Can be null if this segment doesn't have
comment|// any docs with this field
name|normBuffer
operator|=
operator|new
name|byte
index|[
name|maxDoc
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|normBuffer
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Source
name|directSource
init|=
name|normValues
operator|.
name|getDirectSource
argument_list|()
decl_stmt|;
assert|assert
name|directSource
operator|.
name|hasArray
argument_list|()
assert|;
name|normBuffer
operator|=
operator|(
name|byte
index|[]
operator|)
name|directSource
operator|.
name|getArray
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reader
operator|.
name|liveDocs
operator|==
literal|null
condition|)
block|{
comment|//optimized case for segments without deleted docs
name|output
operator|.
name|writeBytes
argument_list|(
name|normBuffer
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|numMergedDocsForField
operator|+=
name|maxDoc
expr_stmt|;
block|}
else|else
block|{
comment|// this segment has deleted docs, so we have to
comment|// check for every doc if it is deleted or not
specifier|final
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|liveDocs
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|maxDoc
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|.
name|get
argument_list|(
name|k
argument_list|)
condition|)
block|{
name|numMergedDocsForField
operator|++
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|normBuffer
index|[
name|k
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
assert|assert
name|numMergedDocs
operator|==
literal|0
operator|||
name|numMergedDocs
operator|==
name|numMergedDocsForField
assert|;
name|numMergedDocs
operator|=
name|numMergedDocsForField
expr_stmt|;
block|}
block|}
name|this
operator|.
name|numTotalDocs
operator|=
name|numMergedDocs
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
try|try
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|NORMS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
end_class

end_unit

