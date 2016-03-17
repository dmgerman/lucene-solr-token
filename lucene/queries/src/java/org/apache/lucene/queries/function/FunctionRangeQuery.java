begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|IndexSearcher
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
name|Query
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

begin_comment
comment|/**  * A Query wrapping a {@link ValueSource} that matches docs in which the values in the value source match a configured  * range.  The score is the float value.  This can be a slow query if run by itself since it must visit all docs;  * ideally it's combined with other queries.  * It's mostly a wrapper around  * {@link FunctionValues#getRangeScorer(LeafReaderContext, String, String, boolean, boolean)}.  *  * A similar class is {@code org.apache.lucene.search.DocValuesRangeQuery} in the sandbox module.  That one is  * constant scoring.  *  * @see FunctionQuery (constant scoring)  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FunctionRangeQuery
specifier|public
class|class
name|FunctionRangeQuery
extends|extends
name|Query
block|{
DECL|field|valueSource
specifier|private
specifier|final
name|ValueSource
name|valueSource
decl_stmt|;
comment|// These two are declared as strings because FunctionValues.getRangeScorer takes String args and parses them.
DECL|field|lowerVal
specifier|private
specifier|final
name|String
name|lowerVal
decl_stmt|;
DECL|field|upperVal
specifier|private
specifier|final
name|String
name|upperVal
decl_stmt|;
DECL|field|includeLower
specifier|private
specifier|final
name|boolean
name|includeLower
decl_stmt|;
DECL|field|includeUpper
specifier|private
specifier|final
name|boolean
name|includeUpper
decl_stmt|;
DECL|method|FunctionRangeQuery
specifier|public
name|FunctionRangeQuery
parameter_list|(
name|ValueSource
name|valueSource
parameter_list|,
name|Number
name|lowerVal
parameter_list|,
name|Number
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
argument_list|(
name|valueSource
argument_list|,
name|lowerVal
operator|==
literal|null
condition|?
literal|null
else|:
name|lowerVal
operator|.
name|toString
argument_list|()
argument_list|,
name|upperVal
operator|==
literal|null
condition|?
literal|null
else|:
name|upperVal
operator|.
name|toString
argument_list|()
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
expr_stmt|;
block|}
DECL|method|FunctionRangeQuery
specifier|public
name|FunctionRangeQuery
parameter_list|(
name|ValueSource
name|valueSource
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|valueSource
operator|=
name|valueSource
expr_stmt|;
name|this
operator|.
name|lowerVal
operator|=
name|lowerVal
expr_stmt|;
name|this
operator|.
name|upperVal
operator|=
name|upperVal
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
block|}
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|()
block|{
return|return
name|valueSource
return|;
block|}
DECL|method|getLowerVal
specifier|public
name|String
name|getLowerVal
parameter_list|()
block|{
return|return
name|lowerVal
return|;
block|}
DECL|method|getUpperVal
specifier|public
name|String
name|getUpperVal
parameter_list|()
block|{
return|return
name|upperVal
return|;
block|}
DECL|method|isIncludeLower
specifier|public
name|boolean
name|isIncludeLower
parameter_list|()
block|{
return|return
name|includeLower
return|;
block|}
DECL|method|isIncludeUpper
specifier|public
name|boolean
name|isIncludeUpper
parameter_list|()
block|{
return|return
name|includeUpper
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"frange("
operator|+
name|valueSource
operator|+
literal|"):"
operator|+
operator|(
name|includeLower
condition|?
literal|'['
else|:
literal|'{'
operator|)
operator|+
operator|(
name|lowerVal
operator|==
literal|null
condition|?
literal|"*"
else|:
name|lowerVal
operator|)
operator|+
literal|" TO "
operator|+
operator|(
name|upperVal
operator|==
literal|null
condition|?
literal|"*"
else|:
name|upperVal
operator|)
operator|+
operator|(
name|includeUpper
condition|?
literal|']'
else|:
literal|'}'
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|FunctionRangeQuery
operator|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|FunctionRangeQuery
name|that
init|=
operator|(
name|FunctionRangeQuery
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|includeLower
argument_list|,
name|that
operator|.
name|includeLower
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|includeUpper
argument_list|,
name|that
operator|.
name|includeUpper
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|valueSource
argument_list|,
name|that
operator|.
name|valueSource
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|lowerVal
argument_list|,
name|that
operator|.
name|lowerVal
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|upperVal
argument_list|,
name|that
operator|.
name|upperVal
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|super
operator|.
name|hashCode
argument_list|()
argument_list|,
name|valueSource
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FunctionRangeWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
DECL|class|FunctionRangeWeight
specifier|private
class|class
name|FunctionRangeWeight
extends|extends
name|Weight
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|field|vsContext
specifier|private
specifier|final
name|Map
name|vsContext
decl_stmt|;
DECL|method|FunctionRangeWeight
specifier|public
name|FunctionRangeWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|FunctionRangeQuery
operator|.
name|this
argument_list|)
expr_stmt|;
name|vsContext
operator|=
name|ValueSource
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|valueSource
operator|.
name|createWeight
argument_list|(
name|vsContext
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
comment|//callback on valueSource tree
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
comment|//none
block|}
comment|//Note: this uses the functionValue's floatVal() as the score; queryNorm/boost is ignored.
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
literal|1f
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
block|{
comment|//no-op
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
name|FunctionValues
name|functionValues
init|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|vsContext
argument_list|,
name|context
argument_list|)
decl_stmt|;
comment|//note: by using ValueSourceScorer directly, we avoid calling scorer.advance(doc) and checking if true,
comment|//  which can be slow since if that doc doesn't match, it has to linearly find the next matching
name|ValueSourceScorer
name|scorer
init|=
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|.
name|matches
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|scorer
operator|.
name|iterator
argument_list|()
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|scorer
operator|.
name|score
argument_list|()
argument_list|,
name|FunctionRangeQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
argument_list|,
name|functionValues
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
name|FunctionRangeQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
argument_list|,
name|functionValues
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|ValueSourceScorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|FunctionValues
name|functionValues
init|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|vsContext
argument_list|,
name|context
argument_list|)
decl_stmt|;
comment|// getRangeScorer takes String args and parses them. Weird.
return|return
name|functionValues
operator|.
name|getRangeScorer
argument_list|(
name|context
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

