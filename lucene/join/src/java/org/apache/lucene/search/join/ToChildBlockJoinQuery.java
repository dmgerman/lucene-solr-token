begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|search
operator|.
name|FilterWeight
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
name|util
operator|.
name|BitSet
import|;
end_import

begin_comment
comment|/**  * Just like {@link ToParentBlockJoinQuery}, except this  * query joins in reverse: you provide a Query matching  * parent documents and it joins down to child  * documents.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ToChildBlockJoinQuery
specifier|public
class|class
name|ToChildBlockJoinQuery
extends|extends
name|Query
block|{
comment|/** Message thrown from {@link    *  ToChildBlockJoinScorer#validateParentDoc} on misuse,    *  when the parent query incorrectly returns child docs. */
DECL|field|INVALID_QUERY_MESSAGE
specifier|static
specifier|final
name|String
name|INVALID_QUERY_MESSAGE
init|=
literal|"Parent query must not match any docs besides parent filter. "
operator|+
literal|"Combine them as must (+) and must-not (-) clauses to find a problem doc. docID="
decl_stmt|;
DECL|field|ILLEGAL_ADVANCE_ON_PARENT
specifier|static
specifier|final
name|String
name|ILLEGAL_ADVANCE_ON_PARENT
init|=
literal|"Expect to be advanced on child docs only. got docID="
decl_stmt|;
DECL|field|parentsFilter
specifier|private
specifier|final
name|BitSetProducer
name|parentsFilter
decl_stmt|;
DECL|field|parentQuery
specifier|private
specifier|final
name|Query
name|parentQuery
decl_stmt|;
comment|/**    * Create a ToChildBlockJoinQuery.    *     * @param parentQuery Query that matches parent documents    * @param parentsFilter Filter identifying the parent documents.    */
DECL|method|ToChildBlockJoinQuery
specifier|public
name|ToChildBlockJoinQuery
parameter_list|(
name|Query
name|parentQuery
parameter_list|,
name|BitSetProducer
name|parentsFilter
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|parentQuery
operator|=
name|parentQuery
expr_stmt|;
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
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
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ToChildBlockJoinWeight
argument_list|(
name|this
argument_list|,
name|parentQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
argument_list|,
name|parentsFilter
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
comment|/** Return our parent query. */
DECL|method|getParentQuery
specifier|public
name|Query
name|getParentQuery
parameter_list|()
block|{
return|return
name|parentQuery
return|;
block|}
DECL|class|ToChildBlockJoinWeight
specifier|private
specifier|static
class|class
name|ToChildBlockJoinWeight
extends|extends
name|FilterWeight
block|{
DECL|field|parentsFilter
specifier|private
specifier|final
name|BitSetProducer
name|parentsFilter
decl_stmt|;
DECL|field|doScores
specifier|private
specifier|final
name|boolean
name|doScores
decl_stmt|;
DECL|method|ToChildBlockJoinWeight
specifier|public
name|ToChildBlockJoinWeight
parameter_list|(
name|Query
name|joinQuery
parameter_list|,
name|Weight
name|parentWeight
parameter_list|,
name|BitSetProducer
name|parentsFilter
parameter_list|,
name|boolean
name|doScores
parameter_list|)
block|{
name|super
argument_list|(
name|joinQuery
argument_list|,
name|parentWeight
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
expr_stmt|;
name|this
operator|.
name|doScores
operator|=
name|doScores
expr_stmt|;
block|}
comment|// NOTE: acceptDocs applies (and is checked) only in the
comment|// child document space
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|parentScorer
init|=
name|in
operator|.
name|scorer
argument_list|(
name|readerContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentScorer
operator|==
literal|null
condition|)
block|{
comment|// No matches
return|return
literal|null
return|;
block|}
comment|// NOTE: this doesn't take acceptDocs into account, the responsibility
comment|// to not match deleted docs is on the scorer
specifier|final
name|BitSet
name|parents
init|=
name|parentsFilter
operator|.
name|getBitSet
argument_list|(
name|readerContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|parents
operator|==
literal|null
condition|)
block|{
comment|// No parents
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ToChildBlockJoinScorer
argument_list|(
name|this
argument_list|,
name|parentScorer
argument_list|,
name|parents
argument_list|,
name|doScores
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
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|ToChildBlockJoinScorer
name|scorer
init|=
operator|(
name|ToChildBlockJoinScorer
operator|)
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
operator|&&
name|scorer
operator|.
name|iterator
argument_list|()
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
condition|)
block|{
name|int
name|parentDoc
init|=
name|scorer
operator|.
name|getParentDoc
argument_list|()
decl_stmt|;
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
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Score based on parent document %d"
argument_list|,
name|parentDoc
operator|+
name|context
operator|.
name|docBase
argument_list|)
argument_list|,
name|in
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|parentDoc
argument_list|)
argument_list|)
return|;
block|}
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"Not a match"
argument_list|)
return|;
block|}
block|}
DECL|class|ToChildBlockJoinScorer
specifier|static
class|class
name|ToChildBlockJoinScorer
extends|extends
name|Scorer
block|{
DECL|field|parentScorer
specifier|private
specifier|final
name|Scorer
name|parentScorer
decl_stmt|;
DECL|field|parentIt
specifier|private
specifier|final
name|DocIdSetIterator
name|parentIt
decl_stmt|;
DECL|field|parentBits
specifier|private
specifier|final
name|BitSet
name|parentBits
decl_stmt|;
DECL|field|doScores
specifier|private
specifier|final
name|boolean
name|doScores
decl_stmt|;
DECL|field|parentScore
specifier|private
name|float
name|parentScore
decl_stmt|;
DECL|field|parentFreq
specifier|private
name|int
name|parentFreq
init|=
literal|1
decl_stmt|;
DECL|field|childDoc
specifier|private
name|int
name|childDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|parentDoc
specifier|private
name|int
name|parentDoc
init|=
literal|0
decl_stmt|;
DECL|method|ToChildBlockJoinScorer
specifier|public
name|ToChildBlockJoinScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Scorer
name|parentScorer
parameter_list|,
name|BitSet
name|parentBits
parameter_list|,
name|boolean
name|doScores
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|doScores
operator|=
name|doScores
expr_stmt|;
name|this
operator|.
name|parentBits
operator|=
name|parentBits
expr_stmt|;
name|this
operator|.
name|parentScorer
operator|=
name|parentScorer
expr_stmt|;
name|this
operator|.
name|parentIt
operator|=
name|parentScorer
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|parentScorer
argument_list|,
literal|"BLOCK_JOIN"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|childDoc
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
comment|//System.out.println("Q.nextDoc() parentDoc=" + parentDoc + " childDoc=" + childDoc);
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|childDoc
operator|+
literal|1
operator|==
name|parentDoc
condition|)
block|{
comment|// OK, we are done iterating through all children
comment|// matching this one parent doc, so we now nextDoc()
comment|// the parent.  Use a while loop because we may have
comment|// to skip over some number of parents w/ no
comment|// children:
while|while
condition|(
literal|true
condition|)
block|{
name|parentDoc
operator|=
name|parentIt
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|validateParentDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|parentDoc
operator|==
literal|0
condition|)
block|{
comment|// Degenerate but allowed: first parent doc has no children
comment|// TODO: would be nice to pull initial parent
comment|// into ctor so we can skip this if... but it's
comment|// tricky because scorer must return -1 for
comment|// .doc() on init...
name|parentDoc
operator|=
name|parentIt
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|validateParentDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|parentDoc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|childDoc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
comment|//System.out.println("  END");
return|return
name|childDoc
return|;
block|}
comment|// Go to first child for this next parentDoc:
name|childDoc
operator|=
literal|1
operator|+
name|parentBits
operator|.
name|prevSetBit
argument_list|(
name|parentDoc
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|childDoc
operator|==
name|parentDoc
condition|)
block|{
comment|// This parent has no children; continue
comment|// parent loop so we move to next parent
continue|continue;
block|}
if|if
condition|(
name|childDoc
operator|<
name|parentDoc
condition|)
block|{
if|if
condition|(
name|doScores
condition|)
block|{
name|parentScore
operator|=
name|parentScorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|parentFreq
operator|=
name|parentScorer
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
comment|//System.out.println("  " + childDoc);
return|return
name|childDoc
return|;
block|}
else|else
block|{
comment|// Degenerate but allowed: parent has no children
block|}
block|}
block|}
else|else
block|{
assert|assert
name|childDoc
operator|<
name|parentDoc
operator|:
literal|"childDoc="
operator|+
name|childDoc
operator|+
literal|" parentDoc="
operator|+
name|parentDoc
assert|;
name|childDoc
operator|++
expr_stmt|;
comment|//System.out.println("  " + childDoc);
return|return
name|childDoc
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|childTarget
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|childTarget
operator|>=
name|parentDoc
condition|)
block|{
if|if
condition|(
name|childTarget
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|childDoc
operator|=
name|parentDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
name|parentDoc
operator|=
name|parentIt
operator|.
name|advance
argument_list|(
name|childTarget
operator|+
literal|1
argument_list|)
expr_stmt|;
name|validateParentDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|parentDoc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|childDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
comment|// scan to the first parent that has children
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|firstChild
init|=
name|parentBits
operator|.
name|prevSetBit
argument_list|(
name|parentDoc
operator|-
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|firstChild
operator|!=
name|parentDoc
condition|)
block|{
comment|// this parent has children
name|childTarget
operator|=
name|Math
operator|.
name|max
argument_list|(
name|childTarget
argument_list|,
name|firstChild
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// parent with no children, move to the next one
name|parentDoc
operator|=
name|parentIt
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|validateParentDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|parentDoc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|childDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
if|if
condition|(
name|doScores
condition|)
block|{
name|parentScore
operator|=
name|parentScorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|parentFreq
operator|=
name|parentScorer
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
block|}
assert|assert
name|childTarget
operator|<
name|parentDoc
assert|;
assert|assert
operator|!
name|parentBits
operator|.
name|get
argument_list|(
name|childTarget
argument_list|)
assert|;
name|childDoc
operator|=
name|childTarget
expr_stmt|;
comment|//System.out.println("  " + childDoc);
return|return
name|childDoc
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
name|parentIt
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** Detect mis-use, where provided parent query in fact      *  sometimes returns child documents.  */
DECL|method|validateParentDoc
specifier|private
name|void
name|validateParentDoc
parameter_list|()
block|{
if|if
condition|(
name|parentDoc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
operator|&&
operator|!
name|parentBits
operator|.
name|get
argument_list|(
name|parentDoc
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|INVALID_QUERY_MESSAGE
operator|+
name|parentDoc
argument_list|)
throw|;
block|}
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
name|childDoc
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
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parentFreq
return|;
block|}
DECL|method|getParentDoc
name|int
name|getParentDoc
parameter_list|()
block|{
return|return
name|parentDoc
return|;
block|}
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
name|parentRewrite
init|=
name|parentQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentRewrite
operator|!=
name|parentQuery
condition|)
block|{
return|return
operator|new
name|ToChildBlockJoinQuery
argument_list|(
name|parentRewrite
argument_list|,
name|parentsFilter
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
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
literal|"ToChildBlockJoinQuery ("
operator|+
name|parentQuery
operator|.
name|toString
argument_list|()
operator|+
literal|")"
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
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|ToChildBlockJoinQuery
name|other
parameter_list|)
block|{
return|return
name|parentQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|parentQuery
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|hash
init|=
name|classHash
argument_list|()
decl_stmt|;
name|hash
operator|=
name|prime
operator|*
name|hash
operator|+
name|parentQuery
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
block|}
end_class

end_unit

