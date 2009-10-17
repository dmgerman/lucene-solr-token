begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|document
operator|.
name|FieldSelector
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
name|Collection
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

begin_comment
comment|/**  A<code>FilterIndexReader</code> contains another IndexReader, which it  * uses as its basic source of data, possibly transforming the data along the  * way or providing additional functionality. The class  *<code>FilterIndexReader</code> itself simply implements all abstract methods  * of<code>IndexReader</code> with versions that pass all requests to the  * contained index reader. Subclasses of<code>FilterIndexReader</code> may  * further override some of these methods and may also provide additional  * methods and fields.  */
end_comment

begin_class
DECL|class|FilterIndexReader
specifier|public
class|class
name|FilterIndexReader
extends|extends
name|IndexReader
block|{
comment|/** Base class for filtering {@link TermDocs} implementations. */
DECL|class|FilterTermDocs
specifier|public
specifier|static
class|class
name|FilterTermDocs
implements|implements
name|TermDocs
block|{
DECL|field|in
specifier|protected
name|TermDocs
name|in
decl_stmt|;
DECL|method|FilterTermDocs
specifier|public
name|FilterTermDocs
parameter_list|(
name|TermDocs
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|TermEnum
name|termEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|in
operator|.
name|doc
argument_list|()
return|;
block|}
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|in
operator|.
name|freq
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|int
index|[]
name|docs
parameter_list|,
name|int
index|[]
name|freqs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|read
argument_list|(
name|docs
argument_list|,
name|freqs
argument_list|)
return|;
block|}
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|skipTo
argument_list|(
name|i
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Base class for filtering {@link TermPositions} implementations. */
DECL|class|FilterTermPositions
specifier|public
specifier|static
class|class
name|FilterTermPositions
extends|extends
name|FilterTermDocs
implements|implements
name|TermPositions
block|{
DECL|method|FilterTermPositions
specifier|public
name|FilterTermPositions
parameter_list|(
name|TermPositions
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|this
operator|.
name|in
operator|)
operator|.
name|nextPosition
argument_list|()
return|;
block|}
DECL|method|getPayloadLength
specifier|public
name|int
name|getPayloadLength
parameter_list|()
block|{
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|this
operator|.
name|in
operator|)
operator|.
name|getPayloadLength
argument_list|()
return|;
block|}
DECL|method|getPayload
specifier|public
name|byte
index|[]
name|getPayload
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|this
operator|.
name|in
operator|)
operator|.
name|getPayload
argument_list|(
name|data
argument_list|,
name|offset
argument_list|)
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|this
operator|.
name|in
operator|)
operator|.
name|isPayloadAvailable
argument_list|()
return|;
block|}
block|}
comment|/** Base class for filtering {@link TermEnum} implementations. */
DECL|class|FilterTermEnum
specifier|public
specifier|static
class|class
name|FilterTermEnum
extends|extends
name|TermEnum
block|{
DECL|field|in
specifier|protected
name|TermEnum
name|in
decl_stmt|;
DECL|method|FilterTermEnum
specifier|public
name|FilterTermEnum
parameter_list|(
name|TermEnum
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|term
specifier|public
name|Term
name|term
parameter_list|()
block|{
return|return
name|in
operator|.
name|term
argument_list|()
return|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|in
operator|.
name|docFreq
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|in
specifier|protected
name|IndexReader
name|in
decl_stmt|;
comment|/**    *<p>Construct a FilterIndexReader based on the specified base reader.    * Directory locking for delete, undeleteAll, and setNorm operations is    * left to the base reader.</p>    *<p>Note that base reader is closed if this FilterIndexReader is closed.</p>    * @param in specified base reader.    */
DECL|method|FilterIndexReader
specifier|public
name|FilterIndexReader
parameter_list|(
name|IndexReader
name|in
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|directory
specifier|public
name|Directory
name|directory
parameter_list|()
block|{
return|return
name|in
operator|.
name|directory
argument_list|()
return|;
block|}
DECL|method|getTermFreqVectors
specifier|public
name|TermFreqVector
index|[]
name|getTermFreqVectors
parameter_list|(
name|int
name|docNumber
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|getTermFreqVectors
argument_list|(
name|docNumber
argument_list|)
return|;
block|}
DECL|method|getTermFreqVector
specifier|public
name|TermFreqVector
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|getTermFreqVector
argument_list|(
name|docNumber
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|method|getTermFreqVector
specifier|public
name|void
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|String
name|field
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|in
operator|.
name|getTermFreqVector
argument_list|(
name|docNumber
argument_list|,
name|field
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
DECL|method|getTermFreqVector
specifier|public
name|void
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|in
operator|.
name|getTermFreqVector
argument_list|(
name|docNumber
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|in
operator|.
name|numDocs
argument_list|()
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|in
operator|.
name|maxDoc
argument_list|()
return|;
block|}
DECL|method|document
specifier|public
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|document
argument_list|(
name|n
argument_list|,
name|fieldSelector
argument_list|)
return|;
block|}
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|in
operator|.
name|isDeleted
argument_list|(
name|n
argument_list|)
return|;
block|}
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|in
operator|.
name|hasDeletions
argument_list|()
return|;
block|}
DECL|method|doUndeleteAll
specifier|protected
name|void
name|doUndeleteAll
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|in
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
block|}
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|hasNorms
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|norms
specifier|public
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|norms
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|method|norms
specifier|public
name|void
name|norms
parameter_list|(
name|String
name|f
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|in
operator|.
name|norms
argument_list|(
name|f
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
DECL|method|doSetNorm
specifier|protected
name|void
name|doSetNorm
parameter_list|(
name|int
name|d
parameter_list|,
name|String
name|f
parameter_list|,
name|byte
name|b
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|in
operator|.
name|setNorm
argument_list|(
name|d
argument_list|,
name|f
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|terms
argument_list|()
return|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|terms
argument_list|(
name|t
argument_list|)
return|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|docFreq
argument_list|(
name|t
argument_list|)
return|;
block|}
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|termDocs
argument_list|()
return|;
block|}
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|termDocs
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|termPositions
argument_list|()
return|;
block|}
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|in
operator|.
name|deleteDocument
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
DECL|method|doCommit
specifier|protected
name|void
name|doCommit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitUserData
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|commit
argument_list|(
name|commitUserData
argument_list|)
expr_stmt|;
block|}
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getFieldNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|(
name|IndexReader
operator|.
name|FieldOption
name|fieldNames
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|getFieldNames
argument_list|(
name|fieldNames
argument_list|)
return|;
block|}
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|getVersion
argument_list|()
return|;
block|}
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|isCurrent
argument_list|()
return|;
block|}
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|isOptimized
argument_list|()
return|;
block|}
DECL|method|getSequentialSubReaders
specifier|public
name|IndexReader
index|[]
name|getSequentialSubReaders
parameter_list|()
block|{
return|return
name|in
operator|.
name|getSequentialSubReaders
argument_list|()
return|;
block|}
block|}
end_class

end_unit

