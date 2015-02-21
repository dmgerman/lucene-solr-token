begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|Objects
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
name|Bits
operator|.
name|MatchNoBits
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
name|ToStringUtils
import|;
end_import

begin_comment
comment|/**  * A {@link Query} that matches documents that have a value for a given field  * as reported by {@link LeafReader#getDocsWithField(String)}.  */
end_comment

begin_class
DECL|class|FieldValueQuery
specifier|public
specifier|final
class|class
name|FieldValueQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
comment|/** Create a query that will match that have a value for the given    *  {@code field}. */
DECL|method|FieldValueQuery
specifier|public
name|FieldValueQuery
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|FieldValueQuery
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|FieldValueQuery
name|that
init|=
operator|(
name|FieldValueQuery
operator|)
name|obj
decl_stmt|;
return|return
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
argument_list|)
operator|&&
name|getBoost
argument_list|()
operator|==
name|that
operator|.
name|getBoost
argument_list|()
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
name|getClass
argument_list|()
argument_list|,
name|field
argument_list|,
name|getBoost
argument_list|()
argument_list|)
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
literal|"FieldValueQuery [field="
operator|+
name|this
operator|.
name|field
operator|+
literal|"]"
operator|+
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
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
name|Weight
argument_list|(
name|this
argument_list|)
block|{
specifier|private
name|float
name|queryNorm
decl_stmt|;
specifier|private
name|float
name|queryWeight
decl_stmt|;
annotation|@
name|Override
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
name|queryWeight
operator|=
name|getBoost
argument_list|()
expr_stmt|;
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
block|}
annotation|@
name|Override
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
name|queryNorm
operator|=
name|norm
operator|*
name|topLevelBoost
expr_stmt|;
name|queryWeight
operator|*=
name|queryNorm
expr_stmt|;
block|}
annotation|@
name|Override
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
specifier|final
name|Scorer
name|s
init|=
name|scorer
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|exists
init|=
operator|(
name|s
operator|!=
literal|null
operator|&&
name|s
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
operator|)
decl_stmt|;
specifier|final
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
if|if
condition|(
name|exists
condition|)
block|{
name|result
operator|.
name|setDescription
argument_list|(
name|FieldValueQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|", product of:"
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|queryWeight
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMatch
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|setDescription
argument_list|(
name|FieldValueQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|" doesn't match id "
operator|+
name|doc
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMatch
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
return|return
name|result
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
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Bits
name|docsWithField
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getDocsWithField
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|docsWithField
operator|==
literal|null
operator|||
name|docsWithField
operator|instanceof
name|MatchNoBits
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSetIterator
name|approximation
init|=
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
decl_stmt|;
specifier|final
name|TwoPhaseDocIdSetIterator
name|twoPhaseIterator
init|=
operator|new
name|TwoPhaseDocIdSetIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|doc
init|=
name|approximation
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|acceptDocs
operator|!=
literal|null
operator|&&
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|approximation
parameter_list|()
block|{
return|return
name|approximation
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|TwoPhaseDocIdSetIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseIterator
argument_list|)
decl_stmt|;
return|return
operator|new
name|Scorer
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|TwoPhaseDocIdSetIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
name|twoPhaseIterator
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|disi
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|disi
operator|.
name|docID
argument_list|()
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
name|disi
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|disi
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|queryWeight
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

