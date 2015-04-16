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
name|IndexReaderContext
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
name|PostingsEnum
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
name|ReaderUtil
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
name|TermContext
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
name|TermState
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
name|TermsEnum
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
name|similarities
operator|.
name|Similarity
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
name|similarities
operator|.
name|Similarity
operator|.
name|SimScorer
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
name|ToStringUtils
import|;
end_import

begin_comment
comment|/**  * A Query that matches documents containing a term. This may be combined with  * other terms with a {@link BooleanQuery}.  */
end_comment

begin_class
DECL|class|TermQuery
specifier|public
class|class
name|TermQuery
extends|extends
name|Query
block|{
DECL|field|term
specifier|private
specifier|final
name|Term
name|term
decl_stmt|;
DECL|field|perReaderTermState
specifier|private
specifier|final
name|TermContext
name|perReaderTermState
decl_stmt|;
DECL|class|TermWeight
specifier|final
class|class
name|TermWeight
extends|extends
name|Weight
block|{
DECL|field|similarity
specifier|private
specifier|final
name|Similarity
name|similarity
decl_stmt|;
DECL|field|stats
specifier|private
specifier|final
name|Similarity
operator|.
name|SimWeight
name|stats
decl_stmt|;
DECL|field|termStates
specifier|private
specifier|final
name|TermContext
name|termStates
decl_stmt|;
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|method|TermWeight
specifier|public
name|TermWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|TermContext
name|termStates
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|TermQuery
operator|.
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
assert|assert
name|termStates
operator|!=
literal|null
operator|:
literal|"TermContext must not be null"
assert|;
name|this
operator|.
name|termStates
operator|=
name|termStates
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|similarity
operator|.
name|computeWeight
argument_list|(
name|getBoost
argument_list|()
argument_list|,
name|searcher
operator|.
name|collectionStatistics
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
argument_list|,
name|searcher
operator|.
name|termStatistics
argument_list|(
name|term
argument_list|,
name|termStates
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"weight("
operator|+
name|TermQuery
operator|.
name|this
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
block|{
return|return
name|stats
operator|.
name|getValueForNormalization
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
name|queryNorm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|stats
operator|.
name|normalize
argument_list|(
name|queryNorm
argument_list|,
name|topLevelBoost
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
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|termStates
operator|.
name|topReaderContext
operator|==
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|context
argument_list|)
operator|:
literal|"The top-reader used to create Weight ("
operator|+
name|termStates
operator|.
name|topReaderContext
operator|+
literal|") is not the same as the current reader's top-reader ("
operator|+
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|context
argument_list|)
assert|;
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|getTermsEnum
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PostingsEnum
name|docs
init|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|acceptDocs
argument_list|,
literal|null
argument_list|,
name|needsScores
condition|?
name|PostingsEnum
operator|.
name|FREQS
else|:
name|PostingsEnum
operator|.
name|NONE
argument_list|)
decl_stmt|;
assert|assert
name|docs
operator|!=
literal|null
assert|;
return|return
operator|new
name|TermScorer
argument_list|(
name|this
argument_list|,
name|docs
argument_list|,
name|similarity
operator|.
name|simScorer
argument_list|(
name|stats
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns a {@link TermsEnum} positioned at this weights Term or null if      * the term does not exist in the given context      */
DECL|method|getTermsEnum
specifier|private
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermState
name|state
init|=
name|termStates
operator|.
name|get
argument_list|(
name|context
operator|.
name|ord
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
comment|// term is not present in that reader
assert|assert
name|termNotInReader
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|term
argument_list|)
operator|:
literal|"no termstate found but term exists in reader term="
operator|+
name|term
assert|;
return|return
literal|null
return|;
block|}
comment|// System.out.println("LD=" + reader.getLiveDocs() + " set?=" +
comment|// (reader.getLiveDocs() != null ? reader.getLiveDocs().get(0) : "null"));
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
return|return
name|termsEnum
return|;
block|}
DECL|method|termNotInReader
specifier|private
name|boolean
name|termNotInReader
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
comment|// only called from assert
comment|// System.out.println("TQ.termNotInReader reader=" + reader + " term=" +
comment|// field + ":" + bytes.utf8ToString());
return|return
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
operator|==
literal|0
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
name|Scorer
name|scorer
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
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|int
name|newDoc
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDoc
operator|==
name|doc
condition|)
block|{
name|float
name|freq
init|=
name|scorer
operator|.
name|freq
argument_list|()
decl_stmt|;
name|SimScorer
name|docScorer
init|=
name|similarity
operator|.
name|simScorer
argument_list|(
name|stats
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"weight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|") ["
operator|+
name|similarity
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"], result of:"
argument_list|)
expr_stmt|;
name|Explanation
name|scoreExplanation
init|=
name|docScorer
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
operator|new
name|Explanation
argument_list|(
name|freq
argument_list|,
literal|"termFreq="
operator|+
name|freq
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|scoreExplanation
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|scoreExplanation
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
return|return
operator|new
name|ComplexExplanation
argument_list|(
literal|false
argument_list|,
literal|0.0f
argument_list|,
literal|"no matching term"
argument_list|)
return|;
block|}
block|}
comment|/** Constructs a query for the term<code>t</code>. */
DECL|method|TermQuery
specifier|public
name|TermQuery
parameter_list|(
name|Term
name|t
parameter_list|)
block|{
name|term
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|perReaderTermState
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Expert: constructs a TermQuery that will use the provided docFreq instead    * of looking up the docFreq against the searcher.    */
DECL|method|TermQuery
specifier|public
name|TermQuery
parameter_list|(
name|Term
name|t
parameter_list|,
name|TermContext
name|states
parameter_list|)
block|{
assert|assert
name|states
operator|!=
literal|null
assert|;
name|term
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|perReaderTermState
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|states
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the term of this query. */
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
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
specifier|final
name|IndexReaderContext
name|context
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
specifier|final
name|TermContext
name|termState
decl_stmt|;
if|if
condition|(
name|perReaderTermState
operator|==
literal|null
operator|||
name|perReaderTermState
operator|.
name|topReaderContext
operator|!=
name|context
condition|)
block|{
comment|// make TermQuery single-pass if we don't have a PRTS or if the context
comment|// differs!
name|termState
operator|=
name|TermContext
operator|.
name|build
argument_list|(
name|context
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// PRTS was pre-build for this IS
name|termState
operator|=
name|this
operator|.
name|perReaderTermState
expr_stmt|;
block|}
return|return
operator|new
name|TermWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|termState
argument_list|)
return|;
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
name|terms
operator|.
name|add
argument_list|(
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Prints a user-readable version of this query. */
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
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
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
operator|!
operator|(
name|o
operator|instanceof
name|TermQuery
operator|)
condition|)
return|return
literal|false
return|;
name|TermQuery
name|other
init|=
operator|(
name|TermQuery
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|&&
name|this
operator|.
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
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
name|super
operator|.
name|hashCode
argument_list|()
operator|^
name|term
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

