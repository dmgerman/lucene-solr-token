begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
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
name|BinaryDocValues
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
name|SortedSetDocValues
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
name|SimpleCollector
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
name|BytesRefHash
import|;
end_import

begin_class
DECL|class|TermsWithScoreCollector
specifier|abstract
class|class
name|TermsWithScoreCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|INITIAL_ARRAY_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|INITIAL_ARRAY_SIZE
init|=
literal|256
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|collectedTerms
specifier|final
name|BytesRefHash
name|collectedTerms
init|=
operator|new
name|BytesRefHash
argument_list|()
decl_stmt|;
DECL|field|scoreMode
specifier|final
name|ScoreMode
name|scoreMode
decl_stmt|;
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|field|scoreSums
name|float
index|[]
name|scoreSums
init|=
operator|new
name|float
index|[
name|INITIAL_ARRAY_SIZE
index|]
decl_stmt|;
DECL|method|TermsWithScoreCollector
name|TermsWithScoreCollector
parameter_list|(
name|String
name|field
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
block|}
DECL|method|getCollectedTerms
specifier|public
name|BytesRefHash
name|getCollectedTerms
parameter_list|()
block|{
return|return
name|collectedTerms
return|;
block|}
DECL|method|getScoresPerTerm
specifier|public
name|float
index|[]
name|getScoresPerTerm
parameter_list|()
block|{
return|return
name|scoreSums
return|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Chooses the right {@link TermsWithScoreCollector} implementation.    *    * @param field                     The field to collect terms for    * @param multipleValuesPerDocument Whether the field to collect terms for has multiple values per document.    * @return a {@link TermsWithScoreCollector} instance    */
DECL|method|create
specifier|static
name|TermsWithScoreCollector
name|create
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|multipleValuesPerDocument
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
if|if
condition|(
name|multipleValuesPerDocument
condition|)
block|{
switch|switch
condition|(
name|scoreMode
condition|)
block|{
case|case
name|Avg
case|:
return|return
operator|new
name|MV
operator|.
name|Avg
argument_list|(
name|field
argument_list|)
return|;
default|default:
return|return
operator|new
name|MV
argument_list|(
name|field
argument_list|,
name|scoreMode
argument_list|)
return|;
block|}
block|}
else|else
block|{
switch|switch
condition|(
name|scoreMode
condition|)
block|{
case|case
name|Avg
case|:
return|return
operator|new
name|SV
operator|.
name|Avg
argument_list|(
name|field
argument_list|)
return|;
default|default:
return|return
operator|new
name|SV
argument_list|(
name|field
argument_list|,
name|scoreMode
argument_list|)
return|;
block|}
block|}
block|}
comment|// impl that works with single value per document
DECL|class|SV
specifier|static
class|class
name|SV
extends|extends
name|TermsWithScoreCollector
block|{
DECL|field|fromDocTerms
name|BinaryDocValues
name|fromDocTerms
decl_stmt|;
DECL|method|SV
name|SV
parameter_list|(
name|String
name|field
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|scoreMode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ord
init|=
name|collectedTerms
operator|.
name|add
argument_list|(
name|fromDocTerms
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|ord
operator|=
operator|-
name|ord
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|ord
operator|>=
name|scoreSums
operator|.
name|length
condition|)
block|{
name|scoreSums
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|scoreSums
argument_list|)
expr_stmt|;
block|}
block|}
name|float
name|current
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|float
name|existing
init|=
name|scoreSums
index|[
name|ord
index|]
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|compare
argument_list|(
name|existing
argument_list|,
literal|0.0f
argument_list|)
operator|==
literal|0
condition|)
block|{
name|scoreSums
index|[
name|ord
index|]
operator|=
name|current
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|scoreMode
condition|)
block|{
case|case
name|Total
case|:
name|scoreSums
index|[
name|ord
index|]
operator|=
name|scoreSums
index|[
name|ord
index|]
operator|+
name|current
expr_stmt|;
break|break;
case|case
name|Max
case|:
if|if
condition|(
name|current
operator|>
name|existing
condition|)
block|{
name|scoreSums
index|[
name|ord
index|]
operator|=
name|current
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|fromDocTerms
operator|=
name|DocValues
operator|.
name|getBinary
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
DECL|class|Avg
specifier|static
class|class
name|Avg
extends|extends
name|SV
block|{
DECL|field|scoreCounts
name|int
index|[]
name|scoreCounts
init|=
operator|new
name|int
index|[
name|INITIAL_ARRAY_SIZE
index|]
decl_stmt|;
DECL|method|Avg
name|Avg
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|ScoreMode
operator|.
name|Avg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ord
init|=
name|collectedTerms
operator|.
name|add
argument_list|(
name|fromDocTerms
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|ord
operator|=
operator|-
name|ord
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|ord
operator|>=
name|scoreSums
operator|.
name|length
condition|)
block|{
name|scoreSums
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|scoreSums
argument_list|)
expr_stmt|;
name|scoreCounts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|scoreCounts
argument_list|)
expr_stmt|;
block|}
block|}
name|float
name|current
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|float
name|existing
init|=
name|scoreSums
index|[
name|ord
index|]
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|compare
argument_list|(
name|existing
argument_list|,
literal|0.0f
argument_list|)
operator|==
literal|0
condition|)
block|{
name|scoreSums
index|[
name|ord
index|]
operator|=
name|current
expr_stmt|;
name|scoreCounts
index|[
name|ord
index|]
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|scoreSums
index|[
name|ord
index|]
operator|=
name|scoreSums
index|[
name|ord
index|]
operator|+
name|current
expr_stmt|;
name|scoreCounts
index|[
name|ord
index|]
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getScoresPerTerm
specifier|public
name|float
index|[]
name|getScoresPerTerm
parameter_list|()
block|{
if|if
condition|(
name|scoreCounts
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|scoreCounts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|scoreSums
index|[
name|i
index|]
operator|=
name|scoreSums
index|[
name|i
index|]
operator|/
name|scoreCounts
index|[
name|i
index|]
expr_stmt|;
block|}
name|scoreCounts
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|scoreSums
return|;
block|}
block|}
block|}
comment|// impl that works with multiple values per document
DECL|class|MV
specifier|static
class|class
name|MV
extends|extends
name|TermsWithScoreCollector
block|{
DECL|field|fromDocTermOrds
name|SortedSetDocValues
name|fromDocTermOrds
decl_stmt|;
DECL|method|MV
name|MV
parameter_list|(
name|String
name|field
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|scoreMode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|fromDocTermOrds
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|long
name|ord
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|fromDocTermOrds
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|int
name|termID
init|=
name|collectedTerms
operator|.
name|add
argument_list|(
name|fromDocTermOrds
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|termID
operator|<
literal|0
condition|)
block|{
name|termID
operator|=
operator|-
name|termID
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|termID
operator|>=
name|scoreSums
operator|.
name|length
condition|)
block|{
name|scoreSums
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|scoreSums
argument_list|)
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|scoreMode
condition|)
block|{
case|case
name|Total
case|:
name|scoreSums
index|[
name|termID
index|]
operator|+=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
break|break;
case|case
name|Max
case|:
name|scoreSums
index|[
name|termID
index|]
operator|=
name|Math
operator|.
name|max
argument_list|(
name|scoreSums
index|[
name|termID
index|]
argument_list|,
name|scorer
operator|.
name|score
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|fromDocTermOrds
operator|=
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
DECL|class|Avg
specifier|static
class|class
name|Avg
extends|extends
name|MV
block|{
DECL|field|scoreCounts
name|int
index|[]
name|scoreCounts
init|=
operator|new
name|int
index|[
name|INITIAL_ARRAY_SIZE
index|]
decl_stmt|;
DECL|method|Avg
name|Avg
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|ScoreMode
operator|.
name|Avg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|fromDocTermOrds
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|long
name|ord
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|fromDocTermOrds
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|int
name|termID
init|=
name|collectedTerms
operator|.
name|add
argument_list|(
name|fromDocTermOrds
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|termID
operator|<
literal|0
condition|)
block|{
name|termID
operator|=
operator|-
name|termID
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|termID
operator|>=
name|scoreSums
operator|.
name|length
condition|)
block|{
name|scoreSums
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|scoreSums
argument_list|)
expr_stmt|;
name|scoreCounts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|scoreCounts
argument_list|)
expr_stmt|;
block|}
block|}
name|scoreSums
index|[
name|termID
index|]
operator|+=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|scoreCounts
index|[
name|termID
index|]
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getScoresPerTerm
specifier|public
name|float
index|[]
name|getScoresPerTerm
parameter_list|()
block|{
if|if
condition|(
name|scoreCounts
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|scoreCounts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|scoreSums
index|[
name|i
index|]
operator|=
name|scoreSums
index|[
name|i
index|]
operator|/
name|scoreCounts
index|[
name|i
index|]
expr_stmt|;
block|}
name|scoreCounts
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|scoreSums
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

