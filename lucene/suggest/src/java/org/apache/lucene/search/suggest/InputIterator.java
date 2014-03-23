begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|Set
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
name|suggest
operator|.
name|Lookup
operator|.
name|LookupResult
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|suggest
operator|.
name|analyzing
operator|.
name|AnalyzingInfixSuggester
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|suggest
operator|.
name|analyzing
operator|.
name|AnalyzingSuggester
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|suggest
operator|.
name|analyzing
operator|.
name|FuzzySuggester
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|BytesRefIterator
import|;
end_import

begin_comment
comment|/**  * Interface for enumerating term,weight,payload triples for suggester consumption;  * currently only {@link AnalyzingSuggester}, {@link  * FuzzySuggester} and {@link AnalyzingInfixSuggester} support payloads.  */
end_comment

begin_interface
DECL|interface|InputIterator
specifier|public
interface|interface
name|InputIterator
extends|extends
name|BytesRefIterator
block|{
comment|/** A term's weight, higher numbers mean better suggestions. */
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
function_decl|;
comment|/** An arbitrary byte[] to record per suggestion.  See    *  {@link LookupResult#payload} to retrieve the payload    *  for each suggestion. */
DECL|method|payload
specifier|public
name|BytesRef
name|payload
parameter_list|()
function_decl|;
comment|/** Returns true if the iterator has payloads */
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
function_decl|;
comment|/**     * A term's contexts context can be used to filter suggestions.    * May return null, if suggest entries do not have any context    * */
DECL|method|contexts
specifier|public
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|()
function_decl|;
comment|/** Returns true if the iterator has contexts */
DECL|method|hasContexts
specifier|public
name|boolean
name|hasContexts
parameter_list|()
function_decl|;
comment|/** Singleton InputIterator that iterates over 0 BytesRefs. */
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|InputIterator
name|EMPTY
init|=
operator|new
name|InputIteratorWrapper
argument_list|(
name|BytesRefIterator
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
comment|/**    * Wraps a BytesRefIterator as a suggester InputIterator, with all weights    * set to<code>1</code> and carries no payload    */
DECL|class|InputIteratorWrapper
specifier|public
specifier|static
class|class
name|InputIteratorWrapper
implements|implements
name|InputIterator
block|{
DECL|field|wrapped
specifier|private
specifier|final
name|BytesRefIterator
name|wrapped
decl_stmt|;
comment|/**       * Creates a new wrapper, wrapping the specified iterator and       * specifying a weight value of<code>1</code> for all terms       * and nullifies associated payloads.      */
DECL|method|InputIteratorWrapper
specifier|public
name|InputIteratorWrapper
parameter_list|(
name|BytesRefIterator
name|wrapped
parameter_list|)
block|{
name|this
operator|.
name|wrapped
operator|=
name|wrapped
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
literal|1
return|;
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
name|wrapped
operator|.
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|payload
specifier|public
name|BytesRef
name|payload
parameter_list|()
block|{
return|return
literal|null
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
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|contexts
specifier|public
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasContexts
specifier|public
name|boolean
name|hasContexts
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_interface

end_unit

