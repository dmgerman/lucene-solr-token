begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.analyzing
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
name|analyzing
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|analysis
operator|.
name|Analyzer
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
name|analyzing
operator|.
name|AnalyzingSuggester
operator|.
name|PathIntersector
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
name|analyzing
operator|.
name|FSTUtil
operator|.
name|Path
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
name|BasicAutomata
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
name|BasicOperations
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
name|LevenshteinAutomata
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
name|SpecialOperations
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|PairOutputs
operator|.
name|Pair
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|FuzzySuggester
specifier|public
class|class
name|FuzzySuggester
extends|extends
name|AnalyzingSuggester
block|{
DECL|field|maxEdits
specifier|private
specifier|final
name|int
name|maxEdits
decl_stmt|;
DECL|field|transpositions
specifier|private
specifier|final
name|boolean
name|transpositions
decl_stmt|;
DECL|field|minPrefix
specifier|private
specifier|final
name|int
name|minPrefix
decl_stmt|;
DECL|method|FuzzySuggester
specifier|public
name|FuzzySuggester
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
argument_list|(
name|analyzer
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
DECL|method|FuzzySuggester
specifier|public
name|FuzzySuggester
parameter_list|(
name|Analyzer
name|indexAnalyzer
parameter_list|,
name|Analyzer
name|queryAnalyzer
parameter_list|)
block|{
name|this
argument_list|(
name|indexAnalyzer
argument_list|,
name|queryAnalyzer
argument_list|,
name|EXACT_FIRST
operator||
name|PRESERVE_SEP
argument_list|,
literal|256
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// nocommit: probably want an option to like, require the first character or something :)
DECL|method|FuzzySuggester
specifier|public
name|FuzzySuggester
parameter_list|(
name|Analyzer
name|indexAnalyzer
parameter_list|,
name|Analyzer
name|queryAnalyzer
parameter_list|,
name|int
name|options
parameter_list|,
name|int
name|maxSurfaceFormsPerAnalyzedForm
parameter_list|,
name|int
name|maxGraphExpansions
parameter_list|,
name|int
name|maxEdits
parameter_list|,
name|boolean
name|transpositions
parameter_list|,
name|int
name|minPrefix
parameter_list|)
block|{
name|super
argument_list|(
name|indexAnalyzer
argument_list|,
name|queryAnalyzer
argument_list|,
name|options
argument_list|,
name|maxSurfaceFormsPerAnalyzedForm
argument_list|,
name|maxGraphExpansions
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxEdits
operator|=
name|maxEdits
expr_stmt|;
name|this
operator|.
name|transpositions
operator|=
name|transpositions
expr_stmt|;
name|this
operator|.
name|minPrefix
operator|=
name|minPrefix
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPathIntersector
specifier|protected
name|PathIntersector
name|getPathIntersector
parameter_list|(
name|Automaton
name|automaton
parameter_list|,
name|FST
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|fst
parameter_list|)
block|{
return|return
operator|new
name|FuzzyPathIntersector
argument_list|(
name|automaton
argument_list|,
name|fst
argument_list|)
return|;
block|}
DECL|method|toLevenshteinAutomata
specifier|final
name|Automaton
name|toLevenshteinAutomata
parameter_list|(
name|Automaton
name|automaton
parameter_list|)
block|{
comment|// nocommit: how slow can this be :)
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|ref
init|=
name|SpecialOperations
operator|.
name|getFiniteStrings
argument_list|(
name|automaton
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Automaton
name|subs
index|[]
init|=
operator|new
name|Automaton
index|[
name|ref
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IntsRef
name|path
range|:
name|ref
control|)
block|{
if|if
condition|(
name|path
operator|.
name|length
operator|<=
name|minPrefix
condition|)
block|{
name|subs
index|[
name|upto
index|]
operator|=
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|path
operator|.
name|ints
argument_list|,
name|path
operator|.
name|offset
argument_list|,
name|path
operator|.
name|length
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
else|else
block|{
name|Automaton
name|prefix
init|=
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|path
operator|.
name|ints
argument_list|,
name|path
operator|.
name|offset
argument_list|,
name|minPrefix
argument_list|)
decl_stmt|;
name|int
name|ints
index|[]
init|=
operator|new
name|int
index|[
name|path
operator|.
name|length
operator|-
name|minPrefix
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|path
operator|.
name|ints
argument_list|,
name|path
operator|.
name|offset
operator|+
name|minPrefix
argument_list|,
name|ints
argument_list|,
literal|0
argument_list|,
name|ints
operator|.
name|length
argument_list|)
expr_stmt|;
name|LevenshteinAutomata
name|lev
init|=
operator|new
name|LevenshteinAutomata
argument_list|(
name|ints
argument_list|,
literal|256
argument_list|,
name|transpositions
argument_list|)
decl_stmt|;
name|Automaton
name|levAutomaton
init|=
name|lev
operator|.
name|toAutomaton
argument_list|(
name|maxEdits
argument_list|)
decl_stmt|;
name|Automaton
name|combined
init|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|prefix
argument_list|,
name|levAutomaton
argument_list|)
argument_list|)
decl_stmt|;
name|combined
operator|.
name|setDeterministic
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// its like the special case in concatenate itself, except we cloneExpanded already
name|subs
index|[
name|upto
index|]
operator|=
name|combined
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|subs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|BasicAutomata
operator|.
name|makeEmpty
argument_list|()
return|;
comment|// matches nothing
block|}
elseif|else
if|if
condition|(
name|subs
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|subs
index|[
literal|0
index|]
return|;
block|}
else|else
block|{
name|Automaton
name|a
init|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|subs
argument_list|)
argument_list|)
decl_stmt|;
comment|// nocommit: we could call toLevenshteinAutomata() before det?
comment|// this only happens if you have multiple paths anyway (e.g. synonyms)
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
block|}
DECL|class|FuzzyPathIntersector
specifier|private
specifier|final
class|class
name|FuzzyPathIntersector
extends|extends
name|PathIntersector
block|{
DECL|method|FuzzyPathIntersector
specifier|public
name|FuzzyPathIntersector
parameter_list|(
name|Automaton
name|automaton
parameter_list|,
name|FST
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|fst
parameter_list|)
block|{
name|super
argument_list|(
name|automaton
argument_list|,
name|fst
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|intersectAll
specifier|public
name|List
argument_list|<
name|Path
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
name|intersectAll
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|FSTUtil
operator|.
name|intersectPrefixPaths
argument_list|(
name|toLevenshteinAutomata
argument_list|(
name|automaton
argument_list|)
argument_list|,
name|fst
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

