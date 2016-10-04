begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.uhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|uhighlight
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|ArrayList
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
name|Map
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
name|TokenStream
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
name|spans
operator|.
name|Spans
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
name|automaton
operator|.
name|CharacterRunAutomaton
import|;
end_import

begin_comment
comment|/**  * Ultimately returns a list of {@link OffsetsEnum} yielding potentially highlightable words in the text.  Needs  * information about the query up front.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|FieldOffsetStrategy
specifier|public
specifier|abstract
class|class
name|FieldOffsetStrategy
block|{
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|terms
specifier|protected
name|BytesRef
index|[]
name|terms
decl_stmt|;
comment|// Query: free-standing terms
DECL|field|strictPhrases
specifier|protected
name|PhraseHelper
name|strictPhrases
decl_stmt|;
comment|// Query: position-sensitive information TODO: rename
DECL|field|automata
specifier|protected
name|CharacterRunAutomaton
index|[]
name|automata
decl_stmt|;
comment|// Query: free-standing wildcards (multi-term query)
DECL|method|FieldOffsetStrategy
specifier|public
name|FieldOffsetStrategy
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
index|[]
name|queryTerms
parameter_list|,
name|PhraseHelper
name|phraseHelper
parameter_list|,
name|CharacterRunAutomaton
index|[]
name|automata
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
name|terms
operator|=
name|queryTerms
expr_stmt|;
name|this
operator|.
name|strictPhrases
operator|=
name|phraseHelper
expr_stmt|;
name|this
operator|.
name|automata
operator|=
name|automata
expr_stmt|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|getOffsetSource
specifier|public
specifier|abstract
name|UnifiedHighlighter
operator|.
name|OffsetSource
name|getOffsetSource
parameter_list|()
function_decl|;
comment|/**    * The primary method -- return offsets for highlightable words in the specified document.    * IMPORTANT: remember to close them all.    */
DECL|method|getOffsetsEnums
specifier|public
specifier|abstract
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|getOffsetsEnums
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|createOffsetsEnums
specifier|protected
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|createOffsetsEnums
parameter_list|(
name|LeafReader
name|leafReader
parameter_list|,
name|int
name|doc
parameter_list|,
name|TokenStream
name|tokenStream
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|offsetsEnums
init|=
name|createOffsetsEnumsFromReader
argument_list|(
name|leafReader
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|automata
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|offsetsEnums
operator|.
name|add
argument_list|(
name|createOffsetsEnumFromTokenStream
argument_list|(
name|doc
argument_list|,
name|tokenStream
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|offsetsEnums
return|;
block|}
DECL|method|createOffsetsEnumsFromReader
specifier|protected
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|createOffsetsEnumsFromReader
parameter_list|(
name|LeafReader
name|atomicReader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// For strict positions, get a Map of term to Spans:
comment|//    note: ScriptPhraseHelper.NONE does the right thing for these method calls
specifier|final
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Spans
argument_list|>
name|strictPhrasesTermToSpans
init|=
name|strictPhrases
operator|.
name|getTermToSpans
argument_list|(
name|atomicReader
argument_list|,
name|doc
argument_list|)
decl_stmt|;
comment|// Usually simply wraps terms in a List; but if willRewrite() then can be expanded
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|sourceTerms
init|=
name|strictPhrases
operator|.
name|expandTermsIfRewrite
argument_list|(
name|terms
argument_list|,
name|strictPhrasesTermToSpans
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|OffsetsEnum
argument_list|>
name|offsetsEnums
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|sourceTerms
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Terms
name|termsIndex
init|=
name|atomicReader
operator|==
literal|null
operator|||
name|sourceTerms
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|atomicReader
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsIndex
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|termsEnum
init|=
name|termsIndex
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|//does not return null
for|for
control|(
name|BytesRef
name|term
range|:
name|sourceTerms
control|)
block|{
if|if
condition|(
operator|!
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
condition|)
block|{
continue|continue;
comment|// term not found
block|}
name|PostingsEnum
name|postingsEnum
init|=
name|termsEnum
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|OFFSETS
argument_list|)
decl_stmt|;
if|if
condition|(
name|postingsEnum
operator|==
literal|null
condition|)
block|{
comment|// no offsets or positions available
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field '"
operator|+
name|field
operator|+
literal|"' was indexed without offsets, cannot highlight"
argument_list|)
throw|;
block|}
if|if
condition|(
name|doc
operator|!=
name|postingsEnum
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
condition|)
block|{
comment|// now it's positioned, although may be exhausted
continue|continue;
block|}
name|postingsEnum
operator|=
name|strictPhrases
operator|.
name|filterPostings
argument_list|(
name|term
argument_list|,
name|postingsEnum
argument_list|,
name|strictPhrasesTermToSpans
operator|.
name|get
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|postingsEnum
operator|==
literal|null
condition|)
block|{
continue|continue;
comment|// completely filtered out
block|}
name|offsetsEnums
operator|.
name|add
argument_list|(
operator|new
name|OffsetsEnum
argument_list|(
name|term
argument_list|,
name|postingsEnum
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|offsetsEnums
return|;
block|}
DECL|method|createOffsetsEnumFromTokenStream
specifier|protected
name|OffsetsEnum
name|createOffsetsEnumFromTokenStream
parameter_list|(
name|int
name|doc
parameter_list|,
name|TokenStream
name|tokenStream
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if there are automata (MTQ), we have to initialize the "fake" enum wrapping them.
assert|assert
name|tokenStream
operator|!=
literal|null
assert|;
comment|// TODO Opt: we sometimes evaluate the automata twice when this TS isn't the original; can we avoid?
name|PostingsEnum
name|mtqPostingsEnum
init|=
name|MultiTermHighlighting
operator|.
name|getDocsEnum
argument_list|(
name|tokenStream
argument_list|,
name|automata
argument_list|)
decl_stmt|;
assert|assert
name|mtqPostingsEnum
operator|instanceof
name|Closeable
assert|;
comment|// FYI we propagate close() later.
name|mtqPostingsEnum
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
operator|new
name|OffsetsEnum
argument_list|(
literal|null
argument_list|,
name|mtqPostingsEnum
argument_list|)
return|;
block|}
block|}
end_class

end_unit

