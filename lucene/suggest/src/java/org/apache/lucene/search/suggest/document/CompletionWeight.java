begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.document
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
operator|.
name|document
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
name|index
operator|.
name|LeafReader
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
name|LeafReaderContext
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
name|Term
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
name|Terms
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
name|BulkScorer
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
name|DocIdSet
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
name|Explanation
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
name|Filter
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
name|Scorer
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
name|Weight
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
name|IntsRef
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
name|automaton
operator|.
name|Automaton
import|;
end_import

begin_comment
comment|/**  * Expert: the Weight for CompletionQuery, used to  * score and explain these queries.  *  * Subclasses can override {@link #setNextMatch(IntsRef)},  * {@link #boost()} and {@link #context()}  * to calculate the boost and extract the context of  * a matched path prefix.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompletionWeight
specifier|public
class|class
name|CompletionWeight
extends|extends
name|Weight
block|{
DECL|field|completionQuery
specifier|private
specifier|final
name|CompletionQuery
name|completionQuery
decl_stmt|;
DECL|field|automaton
specifier|private
specifier|final
name|Automaton
name|automaton
decl_stmt|;
comment|/**    * Creates a weight for<code>query</code> with an<code>automaton</code>,    * using the<code>reader</code> for index stats    */
DECL|method|CompletionWeight
specifier|public
name|CompletionWeight
parameter_list|(
specifier|final
name|CompletionQuery
name|query
parameter_list|,
specifier|final
name|Automaton
name|automaton
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|completionQuery
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|automaton
operator|=
name|automaton
expr_stmt|;
block|}
comment|/**    * Returns the automaton specified    * by the {@link CompletionQuery}    *    * @return query automaton    */
DECL|method|getAutomaton
specifier|public
name|Automaton
name|getAutomaton
parameter_list|()
block|{
return|return
name|automaton
return|;
block|}
annotation|@
name|Override
DECL|method|bulkScorer
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|Terms
name|terms
decl_stmt|;
specifier|final
name|NRTSuggester
name|suggester
decl_stmt|;
if|if
condition|(
operator|(
name|terms
operator|=
name|reader
operator|.
name|terms
argument_list|(
name|completionQuery
operator|.
name|getField
argument_list|()
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|terms
operator|instanceof
name|CompletionTerms
condition|)
block|{
name|CompletionTerms
name|completionTerms
init|=
operator|(
name|CompletionTerms
operator|)
name|terms
decl_stmt|;
if|if
condition|(
operator|(
name|suggester
operator|=
name|completionTerms
operator|.
name|suggester
argument_list|()
operator|)
operator|==
literal|null
condition|)
block|{
comment|// a segment can have a null suggester
comment|// i.e. no FST was built
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|completionQuery
operator|.
name|getField
argument_list|()
operator|+
literal|" is not a SuggestField"
argument_list|)
throw|;
block|}
name|DocIdSet
name|docIdSet
init|=
literal|null
decl_stmt|;
name|Filter
name|filter
init|=
name|completionQuery
operator|.
name|getFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|docIdSet
operator|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|docIdSet
operator|==
literal|null
operator|||
name|docIdSet
operator|.
name|iterator
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// filter matches no docs in current leave
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|docIdSet
operator|.
name|bits
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocIDSet does not provide random access interface"
argument_list|)
throw|;
block|}
block|}
name|Bits
name|acceptDocBits
init|=
operator|(
name|docIdSet
operator|!=
literal|null
operator|)
condition|?
name|docIdSet
operator|.
name|bits
argument_list|()
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|CompletionScorer
argument_list|(
name|this
argument_list|,
name|suggester
argument_list|,
name|reader
argument_list|,
name|acceptDocBits
argument_list|,
name|filter
operator|!=
literal|null
argument_list|,
name|automaton
argument_list|)
return|;
block|}
comment|/**    * Set for every partial path in the index that matched the query    * automaton.    *    * Subclasses should override {@link #boost()} and {@link #context()}    * to return an appropriate value with respect to the current pathPrefix.    *    * @param pathPrefix the prefix of a matched path    */
DECL|method|setNextMatch
specifier|protected
name|void
name|setNextMatch
parameter_list|(
name|IntsRef
name|pathPrefix
parameter_list|)
block|{   }
comment|/**    * Returns the boost of the partial path set by {@link #setNextMatch(IntsRef)}    *    * @return suggestion query-time boost    */
DECL|method|boost
specifier|protected
name|float
name|boost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Returns the context of the partial path set by {@link #setNextMatch(IntsRef)}    *    * @return suggestion context    */
DECL|method|context
specifier|protected
name|CharSequence
name|context
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|//TODO
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{   }
block|}
end_class

end_unit

