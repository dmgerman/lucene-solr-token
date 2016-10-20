begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|util
operator|.
name|Bits
import|;
end_import

begin_comment
comment|/**  * A query that matches all documents.  *  */
end_comment

begin_class
DECL|class|MatchAllDocsQuery
specifier|public
specifier|final
class|class
name|MatchAllDocsQuery
extends|extends
name|Query
block|{
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
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|,
name|boost
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"weight("
operator|+
name|MatchAllDocsQuery
operator|.
name|this
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
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
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|float
name|score
init|=
name|score
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
return|return
operator|new
name|BulkScorer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|max
operator|=
name|Math
operator|.
name|min
argument_list|(
name|max
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|FakeScorer
name|scorer
init|=
operator|new
name|FakeScorer
argument_list|()
decl_stmt|;
name|scorer
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|min
init|;
name|doc
operator|<
name|max
condition|;
operator|++
name|doc
control|)
block|{
name|scorer
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|acceptDocs
operator|==
literal|null
operator|||
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|max
operator|==
name|maxDoc
condition|?
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
else|:
name|max
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
block|}
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
literal|"*:*"
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
return|return
name|sameClassAs
argument_list|(
name|o
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
name|classHash
argument_list|()
return|;
block|}
block|}
end_class

end_unit

