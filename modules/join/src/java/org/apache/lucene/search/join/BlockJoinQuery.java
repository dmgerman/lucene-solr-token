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
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|IndexReader
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
name|IndexWriter
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
name|BooleanClause
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
name|DocIdSetIterator
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
name|search
operator|.
name|grouping
operator|.
name|TopGroups
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
name|OpenBitSet
import|;
end_import

begin_comment
comment|/**  * This query requires that you index  * children and parent docs as a single block, using the  * {@link IndexWriter#addDocuments} or {@link  * IndexWriter#updateDocuments} API.  In each block, the  * child documents must appear first, ending with the parent  * document.  At search time you provide a Filter  * identifying the parents, however this Filter must provide  * an {@link OpenBitSet} per sub-reader.  *  *<p>Once the block index is built, use this query to wrap  * any sub-query matching only child docs and join matches in that  * child document space up to the parent document space.  * You can then use this Query as a clause with  * other queries in the parent document space.</p>  *  *<p>The child documents must be orthogonal to the parent  * documents: the wrapped child query must never  * return a parent document.</p>  *  * If you'd like to retrieve {@link TopGroups} for the  * resulting query, use the {@link BlockJoinCollector}.  * Note that this is not necessary, ie, if you simply want  * to collect the parent documents and don't need to see  * which child documents matched under that parent, then  * you can use any collector.  *  *<p><b>NOTE</b>: If the overall query contains parent-only  * matches, for example you OR a parent-only query with a  * joined child-only query, then the resulting collected documents  * will be correct, however the {@link TopGroups} you get  * from {@link BlockJoinCollector} will not contain every  * child for parents that had matched.  *  *<p>See {@link org.apache.lucene.search.join} for an  * overview.</p>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BlockJoinQuery
specifier|public
class|class
name|BlockJoinQuery
extends|extends
name|Query
block|{
DECL|enum|ScoreMode
DECL|enum constant|None
DECL|enum constant|Avg
DECL|enum constant|Max
DECL|enum constant|Total
specifier|public
specifier|static
enum|enum
name|ScoreMode
block|{
name|None
block|,
name|Avg
block|,
name|Max
block|,
name|Total
block|}
empty_stmt|;
DECL|field|parentsFilter
specifier|private
specifier|final
name|Filter
name|parentsFilter
decl_stmt|;
DECL|field|childQuery
specifier|private
specifier|final
name|Query
name|childQuery
decl_stmt|;
comment|// If we are rewritten, this is the original childQuery we
comment|// were passed; we use this for .equals() and
comment|// .hashCode().  This makes rewritten query equal the
comment|// original, so that user does not have to .rewrite() their
comment|// query before searching:
DECL|field|origChildQuery
specifier|private
specifier|final
name|Query
name|origChildQuery
decl_stmt|;
DECL|field|scoreMode
specifier|private
specifier|final
name|ScoreMode
name|scoreMode
decl_stmt|;
DECL|method|BlockJoinQuery
specifier|public
name|BlockJoinQuery
parameter_list|(
name|Query
name|childQuery
parameter_list|,
name|Filter
name|parentsFilter
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|origChildQuery
operator|=
name|childQuery
expr_stmt|;
name|this
operator|.
name|childQuery
operator|=
name|childQuery
expr_stmt|;
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
block|}
DECL|method|BlockJoinQuery
specifier|private
name|BlockJoinQuery
parameter_list|(
name|Query
name|origChildQuery
parameter_list|,
name|Query
name|childQuery
parameter_list|,
name|Filter
name|parentsFilter
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|origChildQuery
operator|=
name|origChildQuery
expr_stmt|;
name|this
operator|.
name|childQuery
operator|=
name|childQuery
expr_stmt|;
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
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
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BlockJoinWeight
argument_list|(
name|this
argument_list|,
name|childQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
argument_list|,
name|parentsFilter
argument_list|,
name|scoreMode
argument_list|)
return|;
block|}
DECL|class|BlockJoinWeight
specifier|private
specifier|static
class|class
name|BlockJoinWeight
extends|extends
name|Weight
block|{
DECL|field|joinQuery
specifier|private
specifier|final
name|Query
name|joinQuery
decl_stmt|;
DECL|field|childWeight
specifier|private
specifier|final
name|Weight
name|childWeight
decl_stmt|;
DECL|field|parentsFilter
specifier|private
specifier|final
name|Filter
name|parentsFilter
decl_stmt|;
DECL|field|scoreMode
specifier|private
specifier|final
name|ScoreMode
name|scoreMode
decl_stmt|;
DECL|method|BlockJoinWeight
specifier|public
name|BlockJoinWeight
parameter_list|(
name|Query
name|joinQuery
parameter_list|,
name|Weight
name|childWeight
parameter_list|,
name|Filter
name|parentsFilter
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|joinQuery
operator|=
name|joinQuery
expr_stmt|;
name|this
operator|.
name|childWeight
operator|=
name|childWeight
expr_stmt|;
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|joinQuery
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|childWeight
operator|.
name|getValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|childWeight
operator|.
name|sumOfSquaredWeights
argument_list|()
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
parameter_list|)
block|{
name|childWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|,
name|ScorerContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Pass scoreDocsInOrder true, topScorer false to our sub:
specifier|final
name|Scorer
name|childScorer
init|=
name|childWeight
operator|.
name|scorer
argument_list|(
name|readerContext
argument_list|,
name|ScorerContext
operator|.
name|def
argument_list|()
operator|.
name|scoreDocsInOrder
argument_list|(
literal|true
argument_list|)
operator|.
name|topScorer
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|childScorer
operator|==
literal|null
condition|)
block|{
comment|// No matches
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|firstChildDoc
init|=
name|childScorer
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstChildDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
comment|// No matches
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSet
name|parents
init|=
name|parentsFilter
operator|.
name|getDocIdSet
argument_list|(
name|readerContext
argument_list|)
decl_stmt|;
comment|// TODO: once we do random-access filters we can
comment|// generalize this:
if|if
condition|(
name|parents
operator|==
literal|null
condition|)
block|{
comment|// No matches
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|parents
operator|instanceof
name|OpenBitSet
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"parentFilter must return OpenBitSet; got "
operator|+
name|parents
argument_list|)
throw|;
block|}
return|return
operator|new
name|BlockJoinScorer
argument_list|(
name|this
argument_list|,
name|childScorer
argument_list|,
operator|(
name|OpenBitSet
operator|)
name|parents
argument_list|,
name|firstChildDoc
argument_list|,
name|scoreMode
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" cannot explain match on parent document"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|scoresDocsOutOfOrder
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|BlockJoinScorer
specifier|static
class|class
name|BlockJoinScorer
extends|extends
name|Scorer
block|{
DECL|field|childScorer
specifier|private
specifier|final
name|Scorer
name|childScorer
decl_stmt|;
DECL|field|parentBits
specifier|private
specifier|final
name|OpenBitSet
name|parentBits
decl_stmt|;
DECL|field|scoreMode
specifier|private
specifier|final
name|ScoreMode
name|scoreMode
decl_stmt|;
DECL|field|parentDoc
specifier|private
name|int
name|parentDoc
decl_stmt|;
DECL|field|parentScore
specifier|private
name|float
name|parentScore
decl_stmt|;
DECL|field|nextChildDoc
specifier|private
name|int
name|nextChildDoc
decl_stmt|;
DECL|field|pendingChildDocs
specifier|private
name|int
index|[]
name|pendingChildDocs
init|=
operator|new
name|int
index|[
literal|5
index|]
decl_stmt|;
DECL|field|pendingChildScores
specifier|private
name|float
index|[]
name|pendingChildScores
decl_stmt|;
DECL|field|childDocUpto
specifier|private
name|int
name|childDocUpto
decl_stmt|;
DECL|method|BlockJoinScorer
specifier|public
name|BlockJoinScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Scorer
name|childScorer
parameter_list|,
name|OpenBitSet
name|parentBits
parameter_list|,
name|int
name|firstChildDoc
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
comment|//System.out.println("Q.init firstChildDoc=" + firstChildDoc);
name|this
operator|.
name|parentBits
operator|=
name|parentBits
expr_stmt|;
name|this
operator|.
name|childScorer
operator|=
name|childScorer
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
if|if
condition|(
name|scoreMode
operator|!=
name|ScoreMode
operator|.
name|None
condition|)
block|{
name|pendingChildScores
operator|=
operator|new
name|float
index|[
literal|5
index|]
expr_stmt|;
block|}
name|nextChildDoc
operator|=
name|firstChildDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitSubScorers
specifier|public
name|void
name|visitSubScorers
parameter_list|(
name|Query
name|parent
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|relationship
parameter_list|,
name|ScorerVisitor
argument_list|<
name|Query
argument_list|,
name|Query
argument_list|,
name|Scorer
argument_list|>
name|visitor
parameter_list|)
block|{
name|super
operator|.
name|visitSubScorers
argument_list|(
name|parent
argument_list|,
name|relationship
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
comment|//childScorer.visitSubScorers(weight.getQuery(), BooleanClause.Occur.MUST, visitor);
name|childScorer
operator|.
name|visitScorers
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
DECL|method|getChildCount
name|int
name|getChildCount
parameter_list|()
block|{
return|return
name|childDocUpto
return|;
block|}
DECL|method|swapChildDocs
name|int
index|[]
name|swapChildDocs
parameter_list|(
name|int
index|[]
name|other
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|ret
init|=
name|pendingChildDocs
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
name|pendingChildDocs
operator|=
operator|new
name|int
index|[
literal|5
index|]
expr_stmt|;
block|}
else|else
block|{
name|pendingChildDocs
operator|=
name|other
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|swapChildScores
name|float
index|[]
name|swapChildScores
parameter_list|(
name|float
index|[]
name|other
parameter_list|)
block|{
if|if
condition|(
name|scoreMode
operator|==
name|ScoreMode
operator|.
name|None
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ScoreMode is None"
argument_list|)
throw|;
block|}
specifier|final
name|float
index|[]
name|ret
init|=
name|pendingChildScores
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
name|pendingChildScores
operator|=
operator|new
name|float
index|[
literal|5
index|]
expr_stmt|;
block|}
else|else
block|{
name|pendingChildScores
operator|=
name|other
expr_stmt|;
block|}
return|return
name|ret
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
comment|//System.out.println("Q.nextDoc() nextChildDoc=" + nextChildDoc);
if|if
condition|(
name|nextChildDoc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
comment|//System.out.println("  end");
return|return
name|parentDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
comment|// Gather all children sharing the same parent as nextChildDoc
name|parentDoc
operator|=
name|parentBits
operator|.
name|nextSetBit
argument_list|(
name|nextChildDoc
argument_list|)
expr_stmt|;
comment|//System.out.println("  parentDoc=" + parentDoc);
assert|assert
name|parentDoc
operator|!=
operator|-
literal|1
assert|;
name|float
name|totalScore
init|=
literal|0
decl_stmt|;
name|float
name|maxScore
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|childDocUpto
operator|=
literal|0
expr_stmt|;
do|do
block|{
comment|//System.out.println("  c=" + nextChildDoc);
if|if
condition|(
name|pendingChildDocs
operator|.
name|length
operator|==
name|childDocUpto
condition|)
block|{
name|pendingChildDocs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|pendingChildDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|scoreMode
operator|!=
name|ScoreMode
operator|.
name|None
condition|)
block|{
name|pendingChildScores
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|pendingChildScores
argument_list|)
expr_stmt|;
block|}
block|}
name|pendingChildDocs
index|[
name|childDocUpto
index|]
operator|=
name|nextChildDoc
expr_stmt|;
if|if
condition|(
name|scoreMode
operator|!=
name|ScoreMode
operator|.
name|None
condition|)
block|{
comment|// TODO: specialize this into dedicated classes per-scoreMode
specifier|final
name|float
name|childScore
init|=
name|childScorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|pendingChildScores
index|[
name|childDocUpto
index|]
operator|=
name|childScore
expr_stmt|;
name|maxScore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|childScore
argument_list|,
name|maxScore
argument_list|)
expr_stmt|;
name|totalScore
operator|+=
name|childScore
expr_stmt|;
block|}
name|childDocUpto
operator|++
expr_stmt|;
name|nextChildDoc
operator|=
name|childScorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|nextChildDoc
operator|<
name|parentDoc
condition|)
do|;
comment|//System.out.println("  nextChildDoc=" + nextChildDoc);
comment|// Parent& child docs are supposed to be orthogonal:
assert|assert
name|nextChildDoc
operator|!=
name|parentDoc
assert|;
switch|switch
condition|(
name|scoreMode
condition|)
block|{
case|case
name|Avg
case|:
name|parentScore
operator|=
name|totalScore
operator|/
name|childDocUpto
expr_stmt|;
break|break;
case|case
name|Max
case|:
name|parentScore
operator|=
name|maxScore
expr_stmt|;
break|break;
case|case
name|Total
case|:
name|parentScore
operator|=
name|totalScore
expr_stmt|;
break|break;
case|case
name|None
case|:
break|break;
block|}
comment|//System.out.println("  return parentDoc=" + parentDoc);
return|return
name|parentDoc
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
name|parentDoc
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parentScore
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
name|parentTarget
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("Q.advance parentTarget=" + parentTarget);
if|if
condition|(
name|parentTarget
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|parentDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
specifier|final
name|int
name|prevParentDoc
init|=
name|parentBits
operator|.
name|prevSetBit
argument_list|(
name|parentTarget
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|//System.out.println("  rolled back to prevParentDoc=" + prevParentDoc + " vs parentDoc=" + parentDoc);
assert|assert
name|prevParentDoc
operator|>=
name|parentDoc
assert|;
if|if
condition|(
name|prevParentDoc
operator|>
name|nextChildDoc
condition|)
block|{
name|nextChildDoc
operator|=
name|childScorer
operator|.
name|advance
argument_list|(
name|prevParentDoc
argument_list|)
expr_stmt|;
comment|// System.out.println("  childScorer advanced to child docID=" + nextChildDoc);
comment|//} else {
comment|//System.out.println("  skip childScorer advance");
block|}
comment|// Parent& child docs are supposed to be orthogonal:
assert|assert
name|nextChildDoc
operator|!=
name|prevParentDoc
assert|;
specifier|final
name|int
name|nd
init|=
name|nextDoc
argument_list|()
decl_stmt|;
comment|//System.out.println("  return nextParentDoc=" + nd);
return|return
name|nd
return|;
block|}
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
name|childQuery
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|childRewrite
init|=
name|childQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|childRewrite
operator|!=
name|childQuery
condition|)
block|{
return|return
operator|new
name|BlockJoinQuery
argument_list|(
name|childQuery
argument_list|,
name|childRewrite
argument_list|,
name|parentsFilter
argument_list|,
name|scoreMode
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
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
literal|"BlockJoinQuery ("
operator|+
name|childQuery
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this query cannot support boosting; please use childQuery.setBoost instead"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this query cannot support boosting; please use childQuery.getBoost instead"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|_other
parameter_list|)
block|{
if|if
condition|(
name|_other
operator|instanceof
name|BlockJoinQuery
condition|)
block|{
specifier|final
name|BlockJoinQuery
name|other
init|=
operator|(
name|BlockJoinQuery
operator|)
name|_other
decl_stmt|;
return|return
name|origChildQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|origChildQuery
argument_list|)
operator|&&
name|parentsFilter
operator|.
name|equals
argument_list|(
name|other
operator|.
name|parentsFilter
argument_list|)
operator|&&
name|scoreMode
operator|==
name|other
operator|.
name|scoreMode
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|hash
init|=
literal|1
decl_stmt|;
name|hash
operator|=
name|prime
operator|*
name|hash
operator|+
name|origChildQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|prime
operator|*
name|hash
operator|+
name|scoreMode
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|prime
operator|*
name|hash
operator|+
name|parentsFilter
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hash
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|BlockJoinQuery
argument_list|(
operator|(
name|Query
operator|)
name|origChildQuery
operator|.
name|clone
argument_list|()
argument_list|,
name|parentsFilter
argument_list|,
name|scoreMode
argument_list|)
return|;
block|}
block|}
end_class

end_unit

