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
name|util
operator|.
name|Iterator
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
name|search
operator|.
name|CachingWrapperFilter
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
name|AttributeSource
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  A<code>FilterAtomicReader</code> contains another AtomicReader, which it  * uses as its basic source of data, possibly transforming the data along the  * way or providing additional functionality. The class  *<code>FilterAtomicReader</code> itself simply implements all abstract methods  * of<code>IndexReader</code> with versions that pass all requests to the  * contained index reader. Subclasses of<code>FilterAtomicReader</code> may  * further override some of these methods and may also provide additional  * methods and fields.  *<p><b>NOTE</b>: If you override {@link #getLiveDocs()}, you will likely need  * to override {@link #numDocs()} as well and vice-versa.  *<p><b>NOTE</b>: If this {@link FilterAtomicReader} does not change the  * content the contained reader, you could consider overriding  * {@link #getCoreCacheKey()} so that  * {@link CachingWrapperFilter} shares the same entries for this atomic reader  * and the wrapped one. {@link #getCombinedCoreAndDeletesKey()} could be  * overridden as well if the {@link #getLiveDocs() live docs} are not changed  * either.  */
end_comment

begin_class
DECL|class|FilterAtomicReader
specifier|public
class|class
name|FilterAtomicReader
extends|extends
name|AtomicReader
block|{
comment|/** Get the wrapped instance by<code>reader</code> as long as this reader is    *  an intance of {@link FilterAtomicReader}.  */
DECL|method|unwrap
specifier|public
specifier|static
name|AtomicReader
name|unwrap
parameter_list|(
name|AtomicReader
name|reader
parameter_list|)
block|{
while|while
condition|(
name|reader
operator|instanceof
name|FilterAtomicReader
condition|)
block|{
name|reader
operator|=
operator|(
operator|(
name|FilterAtomicReader
operator|)
name|reader
operator|)
operator|.
name|in
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
comment|/** Base class for filtering {@link Fields}    *  implementations. */
DECL|class|FilterFields
specifier|public
specifier|static
class|class
name|FilterFields
extends|extends
name|Fields
block|{
comment|/** The underlying Fields instance. */
DECL|field|in
specifier|protected
specifier|final
name|Fields
name|in
decl_stmt|;
comment|/**      * Creates a new FilterFields.      * @param in the underlying Fields instance.      */
DECL|method|FilterFields
specifier|public
name|FilterFields
parameter_list|(
name|Fields
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
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|in
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
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
name|in
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|in
operator|.
name|size
argument_list|()
return|;
block|}
block|}
comment|/** Base class for filtering {@link Terms} implementations.    *<p><b>NOTE</b>: If the order of terms and documents is not changed, and if    * these terms are going to be intersected with automata, you could consider    * overriding {@link #intersect} for better performance.    */
DECL|class|FilterTerms
specifier|public
specifier|static
class|class
name|FilterTerms
extends|extends
name|Terms
block|{
comment|/** The underlying Terms instance. */
DECL|field|in
specifier|protected
specifier|final
name|Terms
name|in
decl_stmt|;
comment|/**      * Creates a new FilterTerms      * @param in the underlying Terms instance.      */
DECL|method|FilterTerms
specifier|public
name|FilterTerms
parameter_list|(
name|Terms
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
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|(
name|TermsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|iterator
argument_list|(
name|reuse
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getSumTotalTermFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getSumDocFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getDocCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasFreqs
specifier|public
name|boolean
name|hasFreqs
parameter_list|()
block|{
return|return
name|in
operator|.
name|hasFreqs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasOffsets
specifier|public
name|boolean
name|hasOffsets
parameter_list|()
block|{
return|return
name|in
operator|.
name|hasOffsets
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasPositions
specifier|public
name|boolean
name|hasPositions
parameter_list|()
block|{
return|return
name|in
operator|.
name|hasPositions
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|in
operator|.
name|hasPayloads
argument_list|()
return|;
block|}
block|}
comment|/** Base class for filtering {@link TermsEnum} implementations. */
DECL|class|FilterTermsEnum
specifier|public
specifier|static
class|class
name|FilterTermsEnum
extends|extends
name|TermsEnum
block|{
comment|/** The underlying TermsEnum instance. */
DECL|field|in
specifier|protected
specifier|final
name|TermsEnum
name|in
decl_stmt|;
comment|/**      * Creates a new FilterTermsEnum      * @param in the underlying TermsEnum instance.      */
DECL|method|FilterTermsEnum
specifier|public
name|FilterTermsEnum
parameter_list|(
name|TermsEnum
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
annotation|@
name|Override
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
return|return
name|in
operator|.
name|attributes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|seekCeil
argument_list|(
name|text
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seekExact
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
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
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|term
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|ord
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|docFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|totalTermFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|docs
argument_list|(
name|liveDocs
argument_list|,
name|reuse
argument_list|,
name|flags
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|docsAndPositions
argument_list|(
name|liveDocs
argument_list|,
name|reuse
argument_list|,
name|flags
argument_list|)
return|;
block|}
block|}
comment|/** Base class for filtering {@link DocsEnum} implementations. */
DECL|class|FilterDocsEnum
specifier|public
specifier|static
class|class
name|FilterDocsEnum
extends|extends
name|DocsEnum
block|{
comment|/** The underlying DocsEnum instance. */
DECL|field|in
specifier|protected
specifier|final
name|DocsEnum
name|in
decl_stmt|;
comment|/**      * Create a new FilterDocsEnum      * @param in the underlying DocsEnum instance.      */
DECL|method|FilterDocsEnum
specifier|public
name|FilterDocsEnum
parameter_list|(
name|DocsEnum
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
annotation|@
name|Override
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
return|return
name|in
operator|.
name|attributes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|in
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
comment|/** Base class for filtering {@link DocsAndPositionsEnum} implementations. */
DECL|class|FilterDocsAndPositionsEnum
specifier|public
specifier|static
class|class
name|FilterDocsAndPositionsEnum
extends|extends
name|DocsAndPositionsEnum
block|{
comment|/** The underlying DocsAndPositionsEnum instance. */
DECL|field|in
specifier|protected
specifier|final
name|DocsAndPositionsEnum
name|in
decl_stmt|;
comment|/**      * Create a new FilterDocsAndPositionsEnum      * @param in the underlying DocsAndPositionsEnum instance.      */
DECL|method|FilterDocsAndPositionsEnum
specifier|public
name|FilterDocsAndPositionsEnum
parameter_list|(
name|DocsAndPositionsEnum
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
annotation|@
name|Override
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
return|return
name|in
operator|.
name|attributes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|nextPosition
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|startOffset
specifier|public
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|startOffset
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|endOffset
specifier|public
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|endOffset
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getPayload
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|in
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
comment|/** The underlying AtomicReader. */
DECL|field|in
specifier|protected
specifier|final
name|AtomicReader
name|in
decl_stmt|;
comment|/**    *<p>Construct a FilterAtomicReader based on the specified base reader.    *<p>Note that base reader is closed if this FilterAtomicReader is closed.</p>    * @param in specified base reader.    */
DECL|method|FilterAtomicReader
specifier|public
name|FilterAtomicReader
parameter_list|(
name|AtomicReader
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
name|in
operator|.
name|registerParentReader
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addCoreClosedListener
specifier|public
name|void
name|addCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|in
operator|.
name|addCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeCoreClosedListener
specifier|public
name|void
name|removeCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|in
operator|.
name|removeCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|getLiveDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|in
operator|.
name|getFieldInfos
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTermVectors
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|document
specifier|public
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|in
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
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
name|fields
argument_list|()
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
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"FilterAtomicReader("
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumericDocValues
specifier|public
name|NumericDocValues
name|getNumericDocValues
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
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBinaryDocValues
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
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
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
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
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedNumericDocValues
specifier|public
name|SortedNumericDocValues
name|getSortedNumericDocValues
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
name|getSortedNumericDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedSetDocValues
specifier|public
name|SortedSetDocValues
name|getSortedSetDocValues
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
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNormValues
specifier|public
name|NumericDocValues
name|getNormValues
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
name|getNormValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
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
name|getDocsWithField
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|in
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

