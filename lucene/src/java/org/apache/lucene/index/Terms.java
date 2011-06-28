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
name|Comparator
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
name|CloseableThreadLocal
import|;
end_import

begin_comment
comment|/**  * Access to the terms in a specific field.  See {@link Fields}.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Terms
specifier|public
specifier|abstract
class|class
name|Terms
block|{
comment|// Privately cache a TermsEnum per-thread for looking up
comment|// docFreq and getting a private DocsEnum
DECL|field|threadEnums
specifier|private
specifier|final
name|CloseableThreadLocal
argument_list|<
name|TermsEnum
argument_list|>
name|threadEnums
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|TermsEnum
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Returns an iterator that will step through all    *  terms. This method will not return null.*/
DECL|method|iterator
specifier|public
specifier|abstract
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Return the BytesRef Comparator used to sort terms    *  provided by the iterator.  This method may return null    *  if there are no terms.  This method may be invoked    *  many times; it's best to cache a single instance&    *  reuse it. */
DECL|method|getComparator
specifier|public
specifier|abstract
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of documents containing the    *  specified term text.  Returns 0 if the term does not    *  exist. */
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|getThreadTermsEnum
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|text
argument_list|,
literal|true
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
comment|/** Returns the number of documents containing the    *  specified term text.  Returns 0 if the term does not    *  exist. */
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|getThreadTermsEnum
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|text
argument_list|,
literal|true
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
comment|/** Get {@link DocsEnum} for the specified term.  This    *  method may return null if the term does not exist. */
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|BytesRef
name|text
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|getThreadTermsEnum
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|text
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|docs
argument_list|(
name|skipDocs
argument_list|,
name|reuse
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** Get {@link DocsEnum} for the specified term.  This    *  method will may return null if the term does not    *  exists, or positions were not indexed. */
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|BytesRef
name|text
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|getThreadTermsEnum
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|text
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
name|skipDocs
argument_list|,
name|reuse
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Expert: Get {@link DocsEnum} for the specified {@link TermState}.    * This method may return<code>null</code> if the term does not exist.    *     * @see TermsEnum#termState()    * @see TermsEnum#seekExact(BytesRef, TermState) */
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|BytesRef
name|term
parameter_list|,
name|TermState
name|termState
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|getThreadTermsEnum
argument_list|()
decl_stmt|;
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
name|termState
argument_list|)
expr_stmt|;
return|return
name|termsEnum
operator|.
name|docs
argument_list|(
name|skipDocs
argument_list|,
name|reuse
argument_list|)
return|;
block|}
comment|/**    * Get {@link DocsEnum} for the specified {@link TermState}. This    * method will may return<code>null</code> if the term does not exists, or positions were    * not indexed.    *     * @see TermsEnum#termState()    * @see TermsEnum#seekExact(BytesRef, TermState) */
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|BytesRef
name|term
parameter_list|,
name|TermState
name|termState
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|getThreadTermsEnum
argument_list|()
decl_stmt|;
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
name|termState
argument_list|)
expr_stmt|;
return|return
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
name|skipDocs
argument_list|,
name|reuse
argument_list|)
return|;
block|}
DECL|method|getUniqueTermCount
specifier|public
name|long
name|getUniqueTermCount
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this reader does not implement getUniqueTermCount()"
argument_list|)
throw|;
block|}
comment|/** Returns the sum of {@link TermsEnum#totalTermFreq} for    *  all terms in this field, or -1 if this measure isn't    *  stored by the codec (or if this fields omits term freq    *  and positions).  Note that, just like other term    *  measures, this measure does not take deleted documents    *  into account. */
DECL|method|getSumTotalTermFreq
specifier|public
specifier|abstract
name|long
name|getSumTotalTermFreq
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a thread-private {@link TermsEnum} instance. Obtaining    * {@link TermsEnum} from this method might be more efficient than using    * {@link #iterator()} directly since this method doesn't necessarily create a    * new {@link TermsEnum} instance.    *<p>    * NOTE: {@link TermsEnum} instances obtained from this method must not be    * shared across threads. The enum should only be used within a local context    * where other threads can't access it.    *     * @return a thread-private {@link TermsEnum} instance    * @throws IOException    *           if an IOException occurs    * @lucene.internal    */
DECL|method|getThreadTermsEnum
specifier|public
name|TermsEnum
name|getThreadTermsEnum
parameter_list|()
throws|throws
name|IOException
block|{
name|TermsEnum
name|termsEnum
init|=
name|threadEnums
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|==
literal|null
condition|)
block|{
name|termsEnum
operator|=
name|iterator
argument_list|()
expr_stmt|;
name|threadEnums
operator|.
name|set
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
block|}
return|return
name|termsEnum
return|;
block|}
comment|// subclass must close when done:
DECL|method|close
specifier|protected
name|void
name|close
parameter_list|()
block|{
name|threadEnums
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|field|EMPTY_ARRAY
specifier|public
specifier|final
specifier|static
name|Terms
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|Terms
index|[
literal|0
index|]
decl_stmt|;
block|}
end_class

end_unit

