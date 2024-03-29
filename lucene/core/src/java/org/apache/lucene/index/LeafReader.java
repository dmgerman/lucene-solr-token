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

begin_comment
comment|/** {@code LeafReader} is an abstract class, providing an interface for accessing an  index.  Search of an index is done entirely through this abstract interface,  so that any subclass which implements it is searchable. IndexReaders implemented  by this subclass do not consist of several sub-readers,  they are atomic. They support retrieval of stored fields, doc values, terms,  and postings.<p>For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique  document in the index.  These document numbers are ephemeral -- they may change  as documents are added to and deleted from an index.  Clients should thus not  rely on a given document having the same number between sessions.<p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  IndexReader} instances are completely thread  safe, meaning multiple threads can call any of its methods,  concurrently.  If your application requires external  synchronization, you should<b>not</b> synchronize on the<code>IndexReader</code> instance; use your own  (non-Lucene) objects instead. */
end_comment

begin_class
DECL|class|LeafReader
specifier|public
specifier|abstract
class|class
name|LeafReader
extends|extends
name|IndexReader
block|{
DECL|field|readerContext
specifier|private
specifier|final
name|LeafReaderContext
name|readerContext
init|=
operator|new
name|LeafReaderContext
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|/** Sole constructor. (For invocation by subclass    *  constructors, typically implicit.) */
DECL|method|LeafReader
specifier|protected
name|LeafReader
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContext
specifier|public
specifier|final
name|LeafReaderContext
name|getContext
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|readerContext
return|;
block|}
comment|/**    * Optional method: Return a {@link IndexReader.CacheHelper} that can be used to cache    * based on the content of this leaf regardless of deletions. Two readers    * that have the same data but different sets of deleted documents or doc    * values updates may be considered equal. Consider using    * {@link #getReaderCacheHelper} if you need deletions or dv updates to be    * taken into account.    *<p>A return value of {@code null} indicates that this reader is not suited    * for caching, which is typically the case for short-lived wrappers that    * alter the content of the wrapped leaf reader.    * @lucene.experimental    */
DECL|method|getCoreCacheHelper
specifier|public
specifier|abstract
name|CacheHelper
name|getCoreCacheHelper
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|docFreq
specifier|public
specifier|final
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Terms
name|terms
init|=
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|docFreq
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/** Returns the number of documents containing the term    *<code>t</code>.  This method returns 0 if the term or    * field does not exists.  This method does not take into    * account deleted documents that have not yet been merged    * away. */
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
specifier|final
name|long
name|totalTermFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Terms
name|terms
init|=
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
specifier|final
name|long
name|getSumDocFreq
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Terms
name|terms
init|=
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|terms
operator|.
name|getSumDocFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
specifier|final
name|int
name|getDocCount
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Terms
name|terms
init|=
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|terms
operator|.
name|getDocCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
specifier|final
name|long
name|getSumTotalTermFreq
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Terms
name|terms
init|=
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|terms
operator|.
name|getSumTotalTermFreq
argument_list|()
return|;
block|}
comment|/** Returns the {@link Terms} index for this field, or null if it has none. */
DECL|method|terms
specifier|public
specifier|abstract
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link PostingsEnum} for the specified term.    *  This will return null if either the field or    *  term does not exist.    *<p><b>NOTE:</b> The returned {@link PostingsEnum} may contain deleted docs.    *  @see TermsEnum#postings(PostingsEnum) */
DECL|method|postings
specifier|public
specifier|final
name|PostingsEnum
name|postings
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|term
operator|.
name|field
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|term
operator|.
name|bytes
argument_list|()
operator|!=
literal|null
assert|;
specifier|final
name|Terms
name|terms
init|=
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|flags
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns {@link PostingsEnum} for the specified term    *  with {@link PostingsEnum#FREQS}.    *<p>    *  Use this method if you only require documents and frequencies,    *  and do not need any proximity data.    *  This method is equivalent to     *  {@link #postings(Term, int) postings(term, PostingsEnum.FREQS)}    *<p><b>NOTE:</b> The returned {@link PostingsEnum} may contain deleted docs.    *  @see #postings(Term, int)    */
DECL|method|postings
specifier|public
specifier|final
name|PostingsEnum
name|postings
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|postings
argument_list|(
name|term
argument_list|,
name|PostingsEnum
operator|.
name|FREQS
argument_list|)
return|;
block|}
comment|/** Returns {@link NumericDocValues} for this field, or    *  null if no numeric doc values were indexed for    *  this field.  The returned instance should only be    *  used by a single thread. */
DECL|method|getNumericDocValues
specifier|public
specifier|abstract
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link BinaryDocValues} for this field, or    *  null if no binary doc values were indexed for    *  this field.  The returned instance should only be    *  used by a single thread. */
DECL|method|getBinaryDocValues
specifier|public
specifier|abstract
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link SortedDocValues} for this field, or    *  null if no {@link SortedDocValues} were indexed for    *  this field.  The returned instance should only be    *  used by a single thread. */
DECL|method|getSortedDocValues
specifier|public
specifier|abstract
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link SortedNumericDocValues} for this field, or    *  null if no {@link SortedNumericDocValues} were indexed for    *  this field.  The returned instance should only be    *  used by a single thread. */
DECL|method|getSortedNumericDocValues
specifier|public
specifier|abstract
name|SortedNumericDocValues
name|getSortedNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link SortedSetDocValues} for this field, or    *  null if no {@link SortedSetDocValues} were indexed for    *  this field.  The returned instance should only be    *  used by a single thread. */
DECL|method|getSortedSetDocValues
specifier|public
specifier|abstract
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link NumericDocValues} representing norms    *  for this field, or null if no {@link NumericDocValues}    *  were indexed. The returned instance should only be    *  used by a single thread. */
DECL|method|getNormValues
specifier|public
specifier|abstract
name|NumericDocValues
name|getNormValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the {@link FieldInfos} describing all fields in    * this reader.    * @lucene.experimental    */
DECL|method|getFieldInfos
specifier|public
specifier|abstract
name|FieldInfos
name|getFieldInfos
parameter_list|()
function_decl|;
comment|/** Returns the {@link Bits} representing live (not    *  deleted) docs.  A set bit indicates the doc ID has not    *  been deleted.  If this method returns null it means    *  there are no deleted documents (all documents are    *  live).    *    *  The returned instance has been safely published for    *  use by multiple threads without additional    *  synchronization.    */
DECL|method|getLiveDocs
specifier|public
specifier|abstract
name|Bits
name|getLiveDocs
parameter_list|()
function_decl|;
comment|/** Returns the {@link PointValues} used for numeric or    *  spatial searches for the given field, or null if there    *  are no point fields. */
DECL|method|getPointValues
specifier|public
specifier|abstract
name|PointValues
name|getPointValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks consistency of this reader.    *<p>    * Note that this may be costly in terms of I/O, e.g.    * may involve computing a checksum value against large data files.    * @lucene.internal    */
DECL|method|checkIntegrity
specifier|public
specifier|abstract
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return metadata about this leaf.    * @lucene.experimental */
DECL|method|getMetaData
specifier|public
specifier|abstract
name|LeafMetaData
name|getMetaData
parameter_list|()
function_decl|;
block|}
end_class

end_unit

